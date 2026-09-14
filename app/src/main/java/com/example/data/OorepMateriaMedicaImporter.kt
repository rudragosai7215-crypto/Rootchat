package com.example.data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream

data class MateriaMedicaImportResult(
    val success: Boolean,
    val remediesCount: Int = 0,
    val sectionsCount: Int = 0,
    val message: String = ""
)

object OorepMateriaMedicaImporter {
    private const val TAG = "OorepImporter"
    private const val BATCH_SIZE = 500

    suspend fun importFromUri(
        context: Context,
        uri: Uri,
        materiaMedicaDao: MateriaMedicaDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ): MateriaMedicaImportResult = withContext(Dispatchers.IO) {
        try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                importFromStream(stream, materiaMedicaDao, remedyDao, onProgress)
            } ?: MateriaMedicaImportResult(false, message = "Cannot open input stream from selected URI")
        } catch (e: Exception) {
            Log.e(TAG, "Failed importing from URI", e)
            MateriaMedicaImportResult(false, message = "Import error: ${e.message}")
        }
    }

    suspend fun autoCheckAndImportWorkspaceFiles(
        context: Context,
        materiaMedicaDao: MateriaMedicaDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ): MateriaMedicaImportResult = withContext(Dispatchers.IO) {
        // 1. Check in assets via AssetManager
        val assetCandidates = listOf("oorep.sql", "oorep.sql.gz", "oorep.zip", "database/oorep.sql", "database/oorep.sql.gz")
        for (assetName in assetCandidates) {
            try {
                context.assets.open(assetName).use { stream ->
                    return@withContext importFromStream(stream, materiaMedicaDao, remedyDao, onProgress)
                }
            } catch (_: Exception) {
                // Not in assets
            }
        }

        // 2. Check in files directory
        val localCandidates = mutableListOf(
            File(context.filesDir, "oorep.sql"),
            File(context.filesDir, "oorep.sql.gz"),
            File(context.filesDir, "oorep.zip")
        )
        context.getExternalFilesDir(null)?.let { extDir ->
            localCandidates.add(File(extDir, "oorep.sql"))
            localCandidates.add(File(extDir, "oorep.sql.gz"))
            localCandidates.add(File(extDir, "oorep.zip"))
        }

        for (file in localCandidates) {
            if (file.exists() && file.length() > 0) {
                return@withContext importFromLocalFile(file, materiaMedicaDao, remedyDao, onProgress)
            }
        }
        MateriaMedicaImportResult(false, message = "No local OOREP database dump file found")
    }

    suspend fun importFromLocalFile(
        file: File,
        materiaMedicaDao: MateriaMedicaDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ): MateriaMedicaImportResult = withContext(Dispatchers.IO) {
        try {
            if (!file.exists()) {
                return@withContext MateriaMedicaImportResult(false, message = "File does not exist: ${file.name}")
            }
            FileInputStream(file).use { stream ->
                importFromStream(stream, materiaMedicaDao, remedyDao, onProgress)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed importing from local file ${file.name}", e)
            MateriaMedicaImportResult(false, message = "Import error: ${e.message}")
        }
    }

    suspend fun importFromStream(
        rawStream: InputStream,
        materiaMedicaDao: MateriaMedicaDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ): MateriaMedicaImportResult = withContext(Dispatchers.IO) {
        val buffered = BufferedInputStream(rawStream, 65536)
        buffered.mark(16)
        val header = ByteArray(4)
        val readBytes = buffered.read(header, 0, 4)
        buffered.reset()

        val isZip = readBytes >= 4 && header[0] == 0x50.toByte() && header[1] == 0x4B.toByte() &&
                header[2] == 0x03.toByte() && header[3] == 0x04.toByte()
        val isGzip = readBytes >= 2 && header[0] == 0x1F.toByte() && header[1] == 0x8B.toByte()

        if (isZip) {
            val zis = ZipInputStream(buffered)
            var entry = zis.nextEntry
            while (entry != null) {
                val name = entry.name.lowercase()
                if (name.endsWith(".sql") || name.endsWith(".sql.gz")) {
                    onProgress(ImportProgress(isImporting = true, currentStatus = "Found database dump in zip: ${entry.name}"))
                    val subStream = if (name.endsWith(".gz")) GZIPInputStream(zis) else zis
                    return@withContext parseSqlStream(subStream, materiaMedicaDao, remedyDao, onProgress)
                }
                entry = zis.nextEntry
            }
            return@withContext MateriaMedicaImportResult(false, message = "No .sql or .sql.gz found in zip archive")
        } else if (isGzip) {
            GZIPInputStream(buffered).use { gzipStream ->
                return@withContext parseSqlStream(gzipStream, materiaMedicaDao, remedyDao, onProgress)
            }
        } else {
            return@withContext parseSqlStream(buffered, materiaMedicaDao, remedyDao, onProgress)
        }
    }

    private suspend fun parseSqlStream(
        stream: InputStream,
        materiaMedicaDao: MateriaMedicaDao,
        remedyDao: RemedyDao,
        onProgress: (ImportProgress) -> Unit
    ): MateriaMedicaImportResult {
        val reader = BufferedReader(InputStreamReader(stream, Charsets.UTF_8), 65536)

        var boerickeInfoId: String? = null
        val boerickeChapterMap = mutableMapOf<String, RemedyChapterInfo>() // chapterId -> Info

        // Preload remedy abbreviation map for fast cross-referencing
        val knownRemedies: List<RemedyNameEntity> = try {
            remedyDao.getAllRemediesSync()
        } catch (e: Exception) {
            emptyList<RemedyNameEntity>()
        }
        val nameToAbbr = mutableMapOf<String, String>()
        val abbrToFullName = mutableMapOf<String, String>()
        for (r in knownRemedies) {
            val cleanName = normalizeName(r.fullName)
            nameToAbbr[cleanName] = r.abbreviation
            abbrToFullName[r.abbreviation.lowercase()] = r.fullName
        }

        var currentCopyTable: String? = null
        var copyColumns = listOf<String>()

        var totalSectionsInserted = 0
        val sectionBatch = ArrayList<MateriaMedicaEntity>(BATCH_SIZE)

        onProgress(ImportProgress(isImporting = true, currentStatus = "Scanning OOREP dump for Boericke Materia Medica (1906)..."))

        var line = reader.readLine()
        var lineCount = 0

        while (line != null) {
            lineCount++
            val trimmed = line.trim()

            // Detect COPY statement
            if (trimmed.startsWith("COPY ", ignoreCase = true)) {
                val copyMatch = Regex("COPY\\s+(?:public\\.)?([a-zA-Z0-9_]+)\\s*(?:\\(([^)]+)\\))?", RegexOption.IGNORE_CASE).find(trimmed)
                if (copyMatch != null) {
                    currentCopyTable = copyMatch.groupValues[1].lowercase()
                    val colsRaw = copyMatch.groupValues[2]
                    copyColumns = if (colsRaw.isNotBlank()) {
                        colsRaw.split(",").map { it.trim().lowercase() }
                    } else {
                        emptyList()
                    }
                }
                line = reader.readLine()
                continue
            }

            // End of COPY block
            if (trimmed == "\\.") {
                currentCopyTable = null
                copyColumns = emptyList()
                line = reader.readLine()
                continue
            }

            // Handle COPY data rows
            if (currentCopyTable != null) {
                when (currentCopyTable) {
                    "mminfo" -> {
                        // Parse mminfo row (tab delimited)
                        val cols = line.split("\t")
                        val idIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("id") else 0
                        val abbrevIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("abbrev") else 2

                        val id = cols.getOrNull(if (idIdx >= 0) idIdx else 0)?.trim()
                        val abbrev = cols.getOrNull(if (abbrevIdx >= 0) abbrevIdx else 2)?.trim()
                        if (id != null && abbrev != null && abbrev.equals("boericke", ignoreCase = true)) {
                            boerickeInfoId = id
                            Log.d(TAG, "Found Boericke MM Info ID: $boerickeInfoId")
                        }
                    }
                    "mmchapter" -> {
                        // Parse mmchapter row (tab delimited)
                        val cols = line.split("\t")
                        val idIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("id") else 0
                        val infoIdIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("mminfoid") else 1
                        val nameIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("name") else 2

                        val id = cols.getOrNull(if (idIdx >= 0) idIdx else 0)?.trim()
                        val infoId = cols.getOrNull(if (infoIdIdx >= 0) infoIdIdx else 1)?.trim()
                        val rawName = cols.getOrNull(if (nameIdx >= 0) nameIdx else 2)?.trim()

                        if (id != null && (infoId == boerickeInfoId || boerickeInfoId == null && infoId == "1")) {
                            val decodedName = unescapePg(rawName ?: "Unknown")
                            val (fullName, abbrev) = resolveRemedyNames(decodedName, nameToAbbr, abbrToFullName)
                            boerickeChapterMap[id] = RemedyChapterInfo(id, fullName, abbrev)
                        }
                    }
                    "mmsection" -> {
                        // Parse mmsection row (tab delimited)
                        val cols = line.split("\t")
                        val chapterIdIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("mmchapterid") else 1
                        val nameIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("name") else 2
                        val textIdx = if (copyColumns.isNotEmpty()) copyColumns.indexOf("text") else 3

                        val chapterId = cols.getOrNull(if (chapterIdIdx >= 0) chapterIdIdx else 1)?.trim()
                        val sectionNameRaw = cols.getOrNull(if (nameIdx >= 0) nameIdx else 2)?.trim()
                        val textRaw = cols.getOrNull(if (textIdx >= 0) textIdx else 3)?.trim()

                        if (chapterId != null && boerickeChapterMap.containsKey(chapterId)) {
                            val chapter = boerickeChapterMap[chapterId]!!
                            val sectionName = unescapePg(sectionNameRaw ?: "General")
                            val symptomText = unescapePg(textRaw ?: "")

                            if (symptomText.isNotBlank()) {
                                sectionBatch.add(
                                    MateriaMedicaEntity(
                                        remedyAbbreviation = chapter.abbreviation,
                                        remedyFullName = chapter.fullName,
                                        section = sectionName,
                                        symptomText = symptomText
                                    )
                                )

                                if (sectionBatch.size >= BATCH_SIZE) {
                                    materiaMedicaDao.insertAll(sectionBatch)
                                    totalSectionsInserted += sectionBatch.size
                                    sectionBatch.clear()
                                    onProgress(
                                        ImportProgress(
                                            isImporting = true,
                                            currentStatus = "Importing Boericke Materia Medica sections ($totalSectionsInserted entries)...",
                                            processedCount = totalSectionsInserted,
                                            totalCount = 15000
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Also check for INSERT INTO syntax in case dump was made with --inserts
            if (trimmed.startsWith("INSERT INTO ", ignoreCase = true)) {
                if (trimmed.contains("mminfo", ignoreCase = true) && trimmed.contains("boericke", ignoreCase = true)) {
                    val idMatch = Regex("VALUES\\s*\\(\\s*([0-9]+)", RegexOption.IGNORE_CASE).find(trimmed)
                    if (idMatch != null) {
                        boerickeInfoId = idMatch.groupValues[1]
                    }
                }
            }

            line = reader.readLine()
        }

        // Flush remaining sections
        if (sectionBatch.isNotEmpty()) {
            materiaMedicaDao.insertAll(sectionBatch)
            totalSectionsInserted += sectionBatch.size
            sectionBatch.clear()
        }

        val finalDistinctRemedies = materiaMedicaDao.getDistinctRemedyCountSync()
        val finalTotalSections = materiaMedicaDao.getTotalSectionCountSync()

        onProgress(
            ImportProgress(
                isImporting = false,
                currentStatus = "Successfully imported $finalDistinctRemedies remedies and $finalTotalSections sections from Boericke (1906)",
                processedCount = finalTotalSections,
                totalCount = finalTotalSections
            )
        )

        return MateriaMedicaImportResult(
            success = finalTotalSections > 0,
            remediesCount = finalDistinctRemedies,
            sectionsCount = finalTotalSections,
            message = "Successfully imported $finalDistinctRemedies remedies and $finalTotalSections sections from Boericke's Materia Medica (1906, Public Domain)"
        )
    }

    private data class RemedyChapterInfo(
        val chapterId: String,
        val fullName: String,
        val abbreviation: String
    )

    private fun resolveRemedyNames(
        rawName: String,
        nameToAbbr: Map<String, String>,
        abbrToFullName: Map<String, String>
    ): Pair<String, String> {
        val cleanName = rawName.trim()
        val normalized = normalizeName(cleanName)

        // 1. Direct match by normalized full name
        val abbrFromName = nameToAbbr[normalized]
        if (abbrFromName != null) {
            val officialFullName = abbrToFullName[abbrFromName] ?: cleanName
            return Pair(officialFullName, abbrFromName)
        }

        // 2. Maybe rawName is already an abbreviation
        val fullNameFromAbbr = abbrToFullName[cleanName.lowercase()]
        if (fullNameFromAbbr != null) {
            return Pair(fullNameFromAbbr, cleanName.lowercase())
        }

        // 3. Fallback: generate abbreviation from name
        val generatedAbbr = cleanName.lowercase().replace(" ", "-").replace(".", "")
        return Pair(cleanName, generatedAbbr)
    }

    private fun normalizeName(name: String): String {
        return name.lowercase()
            .replace(".", "")
            .replace("-", " ")
            .replace("  ", " ")
            .trim()
    }

    private fun unescapePg(str: String): String {
        return str
            .replace("\\n", "\n")
            .replace("\\r", "\r")
            .replace("\\t", "\t")
            .replace("\\\\", "\\")
            .replace("\\N", "")
            .replace("\\\"", "\"")
            .replace("\\'", "'")
    }
}
