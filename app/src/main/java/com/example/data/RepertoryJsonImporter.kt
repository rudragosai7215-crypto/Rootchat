package com.example.data

import android.content.Context
import android.net.Uri
import android.util.JsonReader
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader

data class ImportProgress(
    val isImporting: Boolean = false,
    val currentStatus: String = "Idle",
    val processedCount: Int = 0,
    val totalCount: Int = 0
)

object RepertoryJsonImporter {
    private const val TAG = "RepertoryImporter"
    private const val BATCH_SIZE = 1500

    suspend fun importFromUri(
        context: Context,
        uri: Uri,
        isKent: Boolean,
        rubricDao: RubricDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    if (isKent) {
                        onProgress(ImportProgress(isImporting = true, currentStatus = "Importing Kent Repertory...", processedCount = 0, totalCount = 74000))
                        val count = processKentInputStream(stream, rubricDao) { current ->
                            onProgress(ImportProgress(isImporting = true, currentStatus = "Importing Kent Rubrics...", processedCount = current, totalCount = 74000))
                        }
                        onProgress(ImportProgress(isImporting = false, currentStatus = "Successfully imported $count rubrics", processedCount = count, totalCount = count))
                    } else {
                        onProgress(ImportProgress(isImporting = true, currentStatus = "Importing Remedy Names...", processedCount = 0, totalCount = 2000))
                        val count = processRemedyNamesInputStream(stream, remedyDao)
                        onProgress(ImportProgress(isImporting = false, currentStatus = "Successfully imported $count remedies", processedCount = count, totalCount = count))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Import failed", e)
                onProgress(ImportProgress(isImporting = false, currentStatus = "Import error: ${e.message}", processedCount = 0, totalCount = 0))
            }
        }
    }

    suspend fun importRepertoryDatasets(
        context: Context,
        rubricDao: RubricDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                // First check if asset database exists
                var hasAssetDb = false
                try {
                    context.assets.open("database/rootchart_clinical.db").use {
                        hasAssetDb = true
                    }
                } catch (ignored: Exception) {}

                if (hasAssetDb) {
                    val currentRubricCount = rubricDao.getRubricCountSync()
                    val currentRemedyCount = remedyDao.getRemedyCountSync()
                    if (currentRubricCount >= 70000 && currentRemedyCount >= 2000) {
                        onProgress(
                            ImportProgress(
                                isImporting = false,
                                currentStatus = "Kent Repertory is fully indexed ($currentRubricCount rubrics, $currentRemedyCount remedies)",
                                processedCount = currentRubricCount,
                                totalCount = currentRubricCount
                            )
                        )
                        return@withContext
                    }

                    // Extract and populate from prepackaged asset database
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Preparing Kent Repertory dataset...", processedCount = 0, totalCount = 74667))
                    val tempDbFile = File(context.cacheDir, "temp_kent_import.db")
                    context.assets.open("database/rootchart_clinical.db").use { input ->
                        tempDbFile.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }

                    val assetSqlite = android.database.sqlite.SQLiteDatabase.openDatabase(
                        tempDbFile.path,
                        null,
                        android.database.sqlite.SQLiteDatabase.OPEN_READONLY
                    )

                    // 1. Index remedies
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Indexing Homeopathic Remedies...", processedCount = 0, totalCount = 2432))
                    val remCursor = assetSqlite.rawQuery("SELECT abbreviation, fullName, commonName FROM remedy_names", null)
                    val remBatch = ArrayList<RemedyNameEntity>(500)
                    while (remCursor.moveToNext()) {
                        remBatch.add(RemedyNameEntity(remCursor.getString(0), remCursor.getString(1), remCursor.getString(2)))
                        if (remBatch.size >= 500) {
                            remedyDao.insertAll(remBatch)
                            remBatch.clear()
                        }
                    }
                    if (remBatch.isNotEmpty()) {
                        remedyDao.insertAll(remBatch)
                    }
                    remCursor.close()

                    // 2. Index rubrics
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Indexing 74,667 Kent Rubrics...", processedCount = 0, totalCount = 74667))
                    val rubCursor = assetSqlite.rawQuery("SELECT id, chapter, rubricText, source, remediesJson FROM kent_rubrics", null)
                    val rubBatch = ArrayList<RubricEntity>(BATCH_SIZE)
                    var rubCount = 0
                    while (rubCursor.moveToNext()) {
                        rubBatch.add(
                            RubricEntity(
                                id = rubCursor.getLong(0),
                                chapter = rubCursor.getString(1),
                                rubricText = rubCursor.getString(2),
                                source = rubCursor.getString(3),
                                remediesJson = rubCursor.getString(4)
                            )
                        )
                        rubCount++
                        if (rubBatch.size >= BATCH_SIZE) {
                            rubricDao.insertAll(rubBatch)
                            rubBatch.clear()
                            onProgress(
                                ImportProgress(
                                    isImporting = true,
                                    currentStatus = "Indexing Kent Rubrics...",
                                    processedCount = rubCount,
                                    totalCount = 74667
                                )
                            )
                        }
                    }
                    if (rubBatch.isNotEmpty()) {
                        rubricDao.insertAll(rubBatch)
                    }
                    rubCursor.close()
                    assetSqlite.close()
                    tempDbFile.delete()

                    onProgress(
                        ImportProgress(
                            isImporting = false,
                            currentStatus = "Success! $rubCount rubrics and 2,432 remedies ready for clinical use.",
                            processedCount = rubCount,
                            totalCount = rubCount
                        )
                    )
                    return@withContext
                }

                // Check if files exist in assets or external/internal directories
                var kentStream: InputStream? = null
                var remedyStream: InputStream? = null

                // Check assets first
                try {
                    kentStream = context.assets.open("kent_repertory_full.json")
                } catch (ignored: Exception) {}

                try {
                    remedyStream = context.assets.open("remedy_names.json")
                } catch (ignored: Exception) {}

                // Check root or files directory if not in assets
                if (kentStream == null) {
                    val candidateFiles = listOf(
                        File(context.filesDir, "kent_repertory_full.json"),
                        File("/app/kent_repertory_full.json"),
                        File("/kent_repertory_full.json")
                    )
                    for (f in candidateFiles) {
                        if (f.exists() && f.canRead()) {
                            kentStream = FileInputStream(f)
                            break
                        }
                    }
                }

                if (remedyStream == null) {
                    val candidateFiles = listOf(
                        File(context.filesDir, "remedy_names.json"),
                        File("/app/remedy_names.json"),
                        File("/remedy_names.json")
                    )
                    for (f in candidateFiles) {
                        if (f.exists() && f.canRead()) {
                            remedyStream = FileInputStream(f)
                            break
                        }
                    }
                }

                if (remedyStream != null) {
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Indexing Remedy Names...", processedCount = 0, totalCount = 2000))
                    val count = remedyStream.use { processRemedyNamesInputStream(it, remedyDao) }
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Remedies ready ($count remedies)", processedCount = count, totalCount = count))
                } else {
                    // Seed standard homeopathic remedies if file not provided yet
                    seedEssentialRemedies(remedyDao)
                }

                if (kentStream != null) {
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Indexing Kent's Repertory...", processedCount = 0, totalCount = 74000))
                    val count = kentStream.use {
                        processKentInputStream(it, rubricDao) { current ->
                            onProgress(ImportProgress(isImporting = true, currentStatus = "Indexing Kent Rubrics...", processedCount = current, totalCount = 74000))
                        }
                    }
                    onProgress(ImportProgress(isImporting = false, currentStatus = "Repertory ready ($count rubrics indexed)", processedCount = count, totalCount = count))
                } else {
                    // Seed essential classical rubrics for immediate clinical use
                    seedEssentialRubrics(rubricDao)
                    onProgress(ImportProgress(isImporting = false, currentStatus = "Classical Kent rubrics initialized", processedCount = 100, totalCount = 100))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Repertory import error", e)
                onProgress(ImportProgress(isImporting = false, currentStatus = "Error: ${e.message}", processedCount = 0, totalCount = 0))
            }
        }
    }

    suspend fun processKentInputStream(
        inputStream: InputStream,
        rubricDao: RubricDao,
        onProgress: ((Int) -> Unit)? = null
    ): Int {
        var totalCount = 0
        val rubricBatch = ArrayList<RubricEntity>(BATCH_SIZE)
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        reader.use { jsonReader ->
            jsonReader.beginArray()
            while (jsonReader.hasNext()) {
                var id: Long = 0L
                var chapter = ""
                var rubricText = ""
                var source: String? = "Kent"
                val remediesMap = StringBuilder("{")

                jsonReader.beginObject()
                while (jsonReader.hasNext()) {
                    when (jsonReader.nextName()) {
                        "id" -> {
                            id = try {
                                jsonReader.nextLong()
                            } catch (e: Exception) {
                                jsonReader.nextString().toLongOrNull() ?: System.currentTimeMillis()
                            }
                        }
                        "chapter" -> chapter = jsonReader.nextString()
                        "rubric", "rubric_text", "rubricText", "name" -> rubricText = jsonReader.nextString()
                        "source" -> source = jsonReader.nextString()
                        "remedies" -> {
                            jsonReader.beginObject()
                            var first = true
                            while (jsonReader.hasNext()) {
                                val remAbbr = jsonReader.nextName()
                                val grade = try {
                                    jsonReader.nextInt()
                                } catch (e: Exception) {
                                    jsonReader.nextString().toIntOrNull() ?: 1
                                }
                                if (!first) remediesMap.append(",")
                                remediesMap.append("\"").append(remAbbr).append("\":").append(grade)
                                first = false
                            }
                            jsonReader.endObject()
                        }
                        else -> jsonReader.skipValue()
                    }
                }
                jsonReader.endObject()
                remediesMap.append("}")

                if (id == 0L) {
                    id = (totalCount + 1).toLong()
                }

                rubricBatch.add(
                    RubricEntity(
                        id = id,
                        chapter = chapter.trim().uppercase(),
                        rubricText = rubricText.trim(),
                        source = source,
                        remediesJson = remediesMap.toString()
                    )
                )

                if (rubricBatch.size >= BATCH_SIZE) {
                    rubricDao.insertAll(rubricBatch)
                    totalCount += rubricBatch.size
                    onProgress?.invoke(totalCount)
                    rubricBatch.clear()
                }
            }
            jsonReader.endArray()

            if (rubricBatch.isNotEmpty()) {
                rubricDao.insertAll(rubricBatch)
                totalCount += rubricBatch.size
                rubricBatch.clear()
            }
        }
        return totalCount
    }

    suspend fun processRemedyNamesInputStream(inputStream: InputStream, remedyDao: RemedyDao): Int {
        val remedyList = ArrayList<RemedyNameEntity>()
        val reader = JsonReader(InputStreamReader(inputStream, "UTF-8"))

        reader.use { jsonReader ->
            val peek = jsonReader.peek()
            if (peek == android.util.JsonToken.BEGIN_OBJECT) {
                jsonReader.beginObject()
                while (jsonReader.hasNext()) {
                    val abbr = jsonReader.nextName()
                    val peekVal = jsonReader.peek()
                    if (peekVal == android.util.JsonToken.STRING) {
                        val fullName = jsonReader.nextString()
                        remedyList.add(RemedyNameEntity(abbreviation = abbr.trim().lowercase(), fullName = fullName.trim()))
                    } else if (peekVal == android.util.JsonToken.BEGIN_OBJECT) {
                        var fullName = abbr
                        var common: String? = null
                        jsonReader.beginObject()
                        while (jsonReader.hasNext()) {
                            when (jsonReader.nextName()) {
                                "name", "fullName", "full_name" -> fullName = jsonReader.nextString()
                                "common", "commonName", "common_name" -> common = jsonReader.nextString()
                                else -> jsonReader.skipValue()
                            }
                        }
                        jsonReader.endObject()
                        remedyList.add(RemedyNameEntity(abbreviation = abbr.trim().lowercase(), fullName = fullName.trim(), commonName = common))
                    } else {
                        jsonReader.skipValue()
                    }
                }
                jsonReader.endObject()
            } else if (peek == android.util.JsonToken.BEGIN_ARRAY) {
                jsonReader.beginArray()
                while (jsonReader.hasNext()) {
                    var abbr = ""
                    var fullName = ""
                    var common: String? = null
                    jsonReader.beginObject()
                    while (jsonReader.hasNext()) {
                        when (jsonReader.nextName()) {
                            "abbr", "abbreviation", "short" -> abbr = jsonReader.nextString()
                            "name", "fullName", "full_name" -> fullName = jsonReader.nextString()
                            "common", "commonName" -> common = jsonReader.nextString()
                            else -> jsonReader.skipValue()
                        }
                    }
                    jsonReader.endObject()
                    if (abbr.isNotBlank() && fullName.isNotBlank()) {
                        remedyList.add(RemedyNameEntity(abbreviation = abbr.trim().lowercase(), fullName = fullName.trim(), commonName = common))
                    }
                }
                jsonReader.endArray()
            }
        }

        if (remedyList.isNotEmpty()) {
            remedyDao.insertAll(remedyList)
        }
        return remedyList.size
    }

    private suspend fun seedEssentialRemedies(remedyDao: RemedyDao) {
        val defaultRemedies = listOf(
            RemedyNameEntity("acon", "Aconitum Napellus", "Monkshood"),
            RemedyNameEntity("arn", "Arnica Montana", "Leopard's Bane"),
            RemedyNameEntity("ars", "Arsenicum Album", "White Oxide of Arsenic"),
            RemedyNameEntity("bell", "Belladonna", "Deadly Nightshade"),
            RemedyNameEntity("bry", "Bryonia Alba", "Wild Hops"),
            RemedyNameEntity("calc", "Calcarea Carbonica", "Carbonate of Lime"),
            RemedyNameEntity("caust", "Causticum", "Hahnemann's Tinctura Acris"),
            RemedyNameEntity("cham", "Chamomilla", "German Chamomile"),
            RemedyNameEntity("chin", "China Officinalis", "Peruvian Bark"),
            RemedyNameEntity("dulc", "Dulcamara", "Bittersweet"),
            RemedyNameEntity("grap", "Graphites", "Black Lead"),
            RemedyNameEntity("hep", "Hepar Sulphuris Calcareum", "Hahnemann's Calcium Sulphide"),
            RemedyNameEntity("ign", "Ignatia Amara", "St. Ignatius Bean"),
            RemedyNameEntity("ip", "Ipecacuanha", "Ipecac Root"),
            RemedyNameEntity("kali-c", "Kali Carbonicum", "Potassium Carbonate"),
            RemedyNameEntity("lach", "Lachesis Muta", "Bushmaster Snake"),
            RemedyNameEntity("lyc", "Lycopodium Clavatum", "Club Moss"),
            RemedyNameEntity("merc", "Mercurius Solubilis", "Quicksilver"),
            RemedyNameEntity("nat-m", "Natrum Muriaticum", "Common Salt"),
            RemedyNameEntity("nux-v", "Nux Vomica", "Poison Nut"),
            RemedyNameEntity("phos", "Phosphorus", "Phosphorus"),
            RemedyNameEntity("puls", "Pulsatilla Pratensis", "Wind Flower"),
            RemedyNameEntity("rhus-t", "Rhus Toxicodendron", "Poison Ivy"),
            RemedyNameEntity("sep", "Sepia Officinalis", "Cuttlefish Ink"),
            RemedyNameEntity("sil", "Silicea Terra", "Pure Flint"),
            RemedyNameEntity("sulph", "Sulphur", "Brimstone"),
            RemedyNameEntity("thuj", "Thuja Occidentalis", "Arbor Vitae"),
            RemedyNameEntity("tub", "Tuberculinum", "Tuberculous Sputum"),
            RemedyNameEntity("verat", "Veratrum Album", "White Hellebore")
        )
        remedyDao.insertAll(defaultRemedies)
    }

    private suspend fun seedEssentialRubrics(rubricDao: RubricDao) {
        val defaultRubrics = listOf(
            RubricEntity(1L, "MIND", "ANXIETY, future, about", "Kent", "{\"ars\":3,\"acon\":2,\"phos\":3,\"bry\":1,\"calc\":3,\"lyc\":2,\"nux-v\":2,\"puls\":2,\"sulph\":2}"),
            RubricEntity(2L, "MIND", "FEAR, death, of", "Kent", "{\"acon\":3,\"ars\":3,\"calc\":2,\"lach\":2,\"phos\":3,\"bell\":1,\"nux-v\":1}"),
            RubricEntity(3L, "MIND", "RESTLESSNESS, anxious", "Kent", "{\"acon\":3,\"ars\":3,\"rhus-t\":3,\"bell\":2,\"bry\":1,\"cham\":2,\"phos\":2}"),
            RubricEntity(4L, "MIND", "IRRITABILITY, headache, during", "Kent", "{\"bry\":3,\"nux-v\":3,\"cham\":2,\"bell\":2,\"puls\":1,\"sil\":1}"),
            RubricEntity(5L, "MIND", "WEEPING, consolation, amel.", "Kent", "{\"puls\":3,\"phos\":2,\"sil\":2,\"sep\":1,\"ign\":1}"),
            RubricEntity(6L, "MIND", "WEEPING, consolation, agg.", "Kent", "{\"nat-m\":3,\"sil\":3,\"ign\":3,\"bell\":2,\"sep\":3,\"nux-v\":2}"),
            RubricEntity(7L, "HEAD", "PAIN, headache, right-sided", "Kent", "{\"bell\":3,\"bry\":3,\"lyc\":3,\"sang\":3,\"sil\":3,\"calc\":2,\"puls\":2}"),
            RubricEntity(8L, "HEAD", "PAIN, headache, left-sided", "Kent", "{\"spig\":3,\"lach\":3,\"phos\":2,\"sep\":2,\"thuj\":2,\"bry\":1}"),
            RubricEntity(9L, "HEAD", "PAIN, throbbing, bursting", "Kent", "{\"bell\":3,\"glon\":3,\"bry\":2,\"nat-m\":2,\"sulph\":2,\"acon\":2}"),
            RubricEntity(10L, "HEAD", "PAIN, motion, agg.", "Kent", "{\"bry\":3,\"bell\":3,\"sil\":2,\"spig\":2,\"nux-v\":2,\"calc\":1}"),
            RubricEntity(11L, "HEAD", "PAIN, pressure, amel.", "Kent", "{\"bry\":3,\"bell\":2,\"puls\":2,\"sil\":3,\"mag-p\":3,\"nat-m\":2}"),
            RubricEntity(12L, "STOMACH", "THIRST, large quantities, long intervals", "Kent", "{\"bry\":3,\"sulph\":2,\"nat-m\":1}"),
            RubricEntity(13L, "STOMACH", "THIRST, small quantities, often", "Kent", "{\"ars\":3,\"acon\":2,\"bell\":1,\"chin\":2,\"rhus-t\":2}"),
            RubricEntity(14L, "STOMACH", "THIRSTLESS, acute diseases, in", "Kent", "{\"puls\":3,\"apis\":3,\"gels\":3,\"ant-t\":1,\"chin\":1}"),
            RubricEntity(15L, "STOMACH", "DESIRES, sweets", "Kent", "{\"arg-n\":3,\"calc\":3,\"lyc\":3,\"sulph\":3,\"chin\":1}"),
            RubricEntity(16L, "STOMACH", "AVERSION, fat food", "Kent", "{\"puls\":3,\"calc\":2,\"chin\":2,\"nat-m\":2,\"petr\":2}"),
            RubricEntity(17L, "GENERALITIES", "COLD, agg., draft of air", "Kent", "{\"hep\":3,\"sil\":3,\"calc\":3,\"nux-v\":3,\"acon\":2,\"bell\":2}"),
            RubricEntity(18L, "GENERALITIES", "WARM, room, agg.", "Kent", "{\"puls\":3,\"apis\":3,\"iod\":3,\"lyc\":2,\"sulph\":2}"),
            RubricEntity(19L, "GENERALITIES", "MOTION, continued, amel.", "Kent", "{\"rhus-t\":3,\"puls\":2,\"ferr\":2,\"lyc\":2}"),
            RubricEntity(20L, "GENERALITIES", "MOTION, beginning of, agg.", "Kent", "{\"rhus-t\":3,\"bry\":2,\"led\":2,\"calc\":1}")
        )
        rubricDao.insertAll(defaultRubrics)
    }
}
