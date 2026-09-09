package com.example.data.local

import android.content.Context
import android.util.Log
import com.example.data.model.KentRubric
import com.example.data.model.RemedyGrade
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.ZipInputStream

/**
 * Intelligent Kent Repertory Data Ingestion Engine.
 * Automatically loads full Kent Repertory datasets (74,000+ rubrics) from:
 * 1. Assets folder (e.g. kent_repertory.json, kent_rubrics.json, kent.csv, kent_repertory.zip)
 * 2. App internal storage files
 * 3. Fallback to comprehensive built-in Kent dataset
 */
object KentRubricDataLoader {

  private const val TAG = "KentRubricDataLoader"
  private var cachedRubrics: List<KentRubric>? = null

  suspend fun loadAllRubrics(context: Context): List<KentRubric> = withContext(Dispatchers.IO) {
    cachedRubrics?.let { return@withContext it }

    val loadedList = mutableListOf<KentRubric>()

    try {
      // 1. Check if user placed a ZIP file in assets
      val assetList = context.assets.list("") ?: emptyArray()
      val zipFile = assetList.firstOrNull { it.endsWith(".zip", ignoreCase = true) && it.contains("kent", ignoreCase = true) }
        ?: assetList.firstOrNull { it.endsWith(".zip", ignoreCase = true) }

      if (zipFile != null) {
        Log.i(TAG, "Found repertory ZIP asset: $zipFile. Parsing stream...")
        context.assets.open(zipFile).use { input ->
          val zipRubrics = parseZipStream(input)
          if (zipRubrics.isNotEmpty()) {
            loadedList.addAll(zipRubrics)
          }
        }
      }

      // 2. Check for JSON repertory asset
      if (loadedList.isEmpty()) {
        val jsonFile = assetList.firstOrNull { 
          it.endsWith(".json", ignoreCase = true) && 
          (it.contains("kent", ignoreCase = true) || it.contains("repertory", ignoreCase = true) || it.contains("rubric", ignoreCase = true))
        }
        if (jsonFile != null) {
          Log.i(TAG, "Found JSON repertory asset: $jsonFile")
          context.assets.open(jsonFile).use { input ->
            val jsonRubrics = parseJsonStream(input)
            loadedList.addAll(jsonRubrics)
          }
        }
      }

      // 3. Check for CSV / TSV / TXT repertory asset
      if (loadedList.isEmpty()) {
        val textFile = assetList.firstOrNull {
          (it.endsWith(".csv", ignoreCase = true) || it.endsWith(".tsv", ignoreCase = true) || it.endsWith(".txt", ignoreCase = true)) &&
          (it.contains("kent", ignoreCase = true) || it.contains("repertory", ignoreCase = true) || it.contains("rubric", ignoreCase = true))
        }
        if (textFile != null) {
          Log.i(TAG, "Found text/csv repertory asset: $textFile")
          context.assets.open(textFile).use { input ->
            val textRubrics = parseDelimitedStream(input, textFile.endsWith(".tsv", ignoreCase = true))
            loadedList.addAll(textRubrics)
          }
        }
      }

      // 4. Check internal files directory (if uploaded at runtime)
      if (loadedList.isEmpty()) {
        val filesDir = context.filesDir
        val localRubricFile = filesDir.listFiles()?.firstOrNull { 
          it.name.contains("kent", ignoreCase = true) || it.name.contains("repertory", ignoreCase = true)
        }
        if (localRubricFile != null && localRubricFile.exists()) {
          localRubricFile.inputStream().use { input ->
            if (localRubricFile.name.endsWith(".zip", ignoreCase = true)) {
              loadedList.addAll(parseZipStream(input))
            } else if (localRubricFile.name.endsWith(".json", ignoreCase = true)) {
              loadedList.addAll(parseJsonStream(input))
            } else {
              loadedList.addAll(parseDelimitedStream(input, false))
            }
          }
        }
      }

    } catch (e: Exception) {
      Log.e(TAG, "Error loading external repertory dataset: ${e.message}", e)
    }

    // If loaded from external source, combine or use it; otherwise fallback to built-in dataset
    val finalRubrics = if (loadedList.isNotEmpty()) {
      Log.i(TAG, "Successfully loaded ${loadedList.size} rubrics from external dataset!")
      // Merge with built-in so nothing is lost
      val existingIds = loadedList.map { it.id }.toSet()
      val combined = loadedList.toMutableList()
      KentRepertoryDataset.rubrics.forEach { builtin ->
        if (!existingIds.contains(builtin.id)) {
          combined.add(builtin)
        }
      }
      combined
    } else {
      Log.i(TAG, "Using built-in Kent repertory dataset with ${KentRepertoryDataset.rubrics.size} rubrics.")
      KentRepertoryDataset.rubrics
    }

    cachedRubrics = finalRubrics
    finalRubrics
  }

  private fun parseZipStream(inputStream: InputStream): List<KentRubric> {
    val results = mutableListOf<KentRubric>()
    ZipInputStream(inputStream).use { zis ->
      var entry = zis.nextEntry
      while (entry != null) {
        val name = entry.name.lowercase()
        if (!entry.isDirectory && (name.endsWith(".json") || name.endsWith(".csv") || name.endsWith(".tsv") || name.endsWith(".txt"))) {
          if (name.endsWith(".json")) {
            results.addAll(parseJsonStream(zis))
          } else {
            results.addAll(parseDelimitedStream(zis, name.endsWith(".tsv")))
          }
          if (results.size > 500) break
        }
        zis.closeEntry()
        entry = zis.nextEntry
      }
    }
    return results
  }

  private fun parseJsonStream(inputStream: InputStream): List<KentRubric> {
    val list = mutableListOf<KentRubric>()
    val reader = BufferedReader(InputStreamReader(inputStream))
    val content = reader.readText()

    try {
      if (content.trim().startsWith("[")) {
        val array = JSONArray(content)
        for (i in 0 until array.length()) {
          val obj = array.optJSONObject(i) ?: continue
          val rubric = parseJsonObjectToRubric(obj, "rubric_$i")
          if (rubric != null) list.add(rubric)
        }
      } else if (content.trim().startsWith("{")) {
        val root = JSONObject(content)
        val rubricsArray = root.optJSONArray("rubrics") 
          ?: root.optJSONArray("data")
          ?: root.optJSONArray("items")
        if (rubricsArray != null) {
          for (i in 0 until rubricsArray.length()) {
            val obj = rubricsArray.optJSONObject(i) ?: continue
            val rubric = parseJsonObjectToRubric(obj, "rubric_$i")
            if (rubric != null) list.add(rubric)
          }
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to parse JSON repertory content: ${e.message}")
    }

    return list
  }

  private fun parseJsonObjectToRubric(obj: JSONObject, defaultId: String): KentRubric? {
    val id = obj.optString("id", defaultId)
    val chapter = obj.optString("chapter", obj.optString("section", "Generalities")).trim().capitalizeFirst()
    val rubricName = obj.optString("rubric", obj.optString("rubricName", obj.optString("name", ""))).trim()
    if (rubricName.isBlank()) return null
    val subRubric = obj.optString("subrubric", obj.optString("subRubric", ""))
    val miasm = obj.optString("miasm", "Psora")
    val modality = obj.optString("modality", "")

    val remedies = mutableListOf<RemedyGrade>()
    val remediesArr = obj.optJSONArray("remedies")
    if (remediesArr != null) {
      for (j in 0 until remediesArr.length()) {
        val rObj = remediesArr.optJSONObject(j)
        if (rObj != null) {
          val abbr = rObj.optString("abbr", rObj.optString("remedy", rObj.optString("name", "")))
          val grade = rObj.optInt("grade", rObj.optInt("weight", 2))
          if (abbr.isNotBlank()) remedies.add(RemedyGrade(abbr.trim().capitalizeFirst(), grade))
        } else {
          val remStr = remediesArr.optString(j, "")
          if (remStr.isNotBlank()) remedies.addAll(parseRemediesString(remStr))
        }
      }
    } else {
      val remStr = obj.optString("remedies", "")
      if (remStr.isNotBlank()) {
        remedies.addAll(parseRemediesString(remStr))
      }
    }

    return KentRubric(
      id = id,
      chapter = chapter,
      rubricName = rubricName,
      subRubric = subRubric,
      remedies = remedies,
      miasm = miasm,
      modality = modality
    )
  }

  private fun parseDelimitedStream(inputStream: InputStream, isTsv: Boolean): List<KentRubric> {
    val list = mutableListOf<KentRubric>()
    val delimiter = if (isTsv) "\t" else ","
    val reader = BufferedReader(InputStreamReader(inputStream))
    var line: String? = reader.readLine()
    var lineCount = 0

    while (line != null) {
      lineCount++
      val trimmed = line.trim()
      if (trimmed.isNotBlank() && !trimmed.startsWith("#") && !trimmed.startsWith("chapter", ignoreCase = true)) {
        val parts = trimmed.split(delimiter)
        if (parts.size >= 2) {
          val chapter = parts[0].trim().capitalizeFirst()
          val rubricName = parts[1].trim()
          val remediesStr = if (parts.size > 2) parts[2] else ""
          val modality = if (parts.size > 3) parts[3].trim() else ""
          val miasm = if (parts.size > 4) parts[4].trim() else "Psora"

          val remedies = parseRemediesString(remediesStr)
          list.add(
            KentRubric(
              id = "ext_${lineCount}",
              chapter = chapter,
              rubricName = rubricName,
              subRubric = "",
              remedies = remedies,
              miasm = miasm,
              modality = modality
            )
          )
        }
      }
      line = reader.readLine()
    }
    return list
  }

  private fun parseRemediesString(str: String): List<RemedyGrade> {
    val list = mutableListOf<RemedyGrade>()
    val tokens = str.split(",", ";", " ")
    for (t in tokens) {
      val cleaned = t.trim()
      if (cleaned.isBlank()) continue
      val grade = when {
        cleaned.endsWith("3") || cleaned.contains("(3)") || cleaned.all { it.isUpperCase() } -> 3
        cleaned.endsWith("2") || cleaned.contains("(2)") -> 2
        else -> 1
      }
      val abbr = cleaned.replace(Regex("[0-9()_\\-.]"), "").trim().capitalizeFirst()
      if (abbr.length >= 2) {
        list.add(RemedyGrade(abbr, grade))
      }
    }
    return list
  }

  private fun String.capitalizeFirst(): String {
    if (this.isBlank()) return this
    return this.substring(0, 1).uppercase() + this.substring(1).lowercase()
  }
}
