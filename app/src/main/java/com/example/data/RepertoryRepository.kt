package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RepertoryRepository(
    private val rubricDao: RubricDao,
    private val remedyDao: RemedyDao
) {
    val rubricCount: Flow<Int> = rubricDao.getRubricCount()
    val remedyCount: Flow<Int> = remedyDao.getRemedyCount()

    suspend fun searchRubrics(query: String): List<RubricEntity> {
        val q = query.trim()
        if (q.isBlank()) return emptyList()
        val tokens = q.split("\\s+".toRegex()).filter { it.isNotBlank() }
        return withContext(Dispatchers.IO) {
            when {
                tokens.size >= 3 -> rubricDao.searchRubricsTriple(tokens[0], tokens[1], tokens[2], limit = 40)
                tokens.size == 2 -> rubricDao.searchRubricsMulti(tokens[0], tokens[1], limit = 40)
                else -> rubricDao.searchRubrics(q, limit = 40)
            }
        }
    }

    suspend fun getRemedyFullName(abbreviation: String): String {
        val entity = withContext(Dispatchers.IO) {
            remedyDao.getRemedyByAbbreviation(abbreviation)
        }
        return entity?.fullName ?: abbreviation.uppercase()
    }

    suspend fun getAllChapters(): List<String> = withContext(Dispatchers.IO) {
        rubricDao.getAllChapters()
    }

    suspend fun getRubricsByChapter(chapter: String, limit: Int = 100, offset: Int = 0): List<RubricEntity> = withContext(Dispatchers.IO) {
        rubricDao.getRubricsByChapter(chapter, limit, offset)
    }

    suspend fun searchRubricsInChapter(chapter: String, query: String, limit: Int = 100): List<RubricEntity> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            rubricDao.getRubricsByChapter(chapter, limit, 0)
        } else {
            rubricDao.searchRubricsInChapter(chapter, query.trim(), limit)
        }
    }

    suspend fun getRubricById(id: Long): RubricEntity? = withContext(Dispatchers.IO) {
        rubricDao.getRubricById(id)
    }

    suspend fun computeRepertorization(selectedRubrics: List<SelectedRubric>): List<RepertorizationScore> {
        if (selectedRubrics.isEmpty()) return emptyList()

        return withContext(Dispatchers.Default) {
            // remedyAbbr -> mutable accumulator (score, coverage, breakdown)
            val scoreMap = mutableMapOf<String, Int>()
            val coverageMap = mutableMapOf<String, Int>()
            val breakdownMap = mutableMapOf<String, MutableMap<Long, Int>>()

            for (rubric in selectedRubrics) {
                for ((abbr, grade) in rubric.remedies) {
                    val normalizedAbbr = abbr.lowercase().trim()
                    if (grade > 0) {
                        scoreMap[normalizedAbbr] = (scoreMap[normalizedAbbr] ?: 0) + grade
                        coverageMap[normalizedAbbr] = (coverageMap[normalizedAbbr] ?: 0) + 1
                        val map = breakdownMap.getOrPut(normalizedAbbr) { mutableMapOf() }
                        map[rubric.id] = grade
                    }
                }
            }

            // Fetch remedy names for all found abbreviations
            val results = scoreMap.map { (abbr, score) ->
                val coverage = coverageMap[abbr] ?: 0
                val breakdown = breakdownMap[abbr] ?: emptyMap()
                RepertorizationScore(
                    abbreviation = abbr,
                    fullName = abbr, // will resolve below
                    score = score,
                    coverage = coverage,
                    gradeBreakdown = breakdown
                )
            }.sortedWith(
                compareByDescending<RepertorizationScore> { it.score }
                    .thenByDescending { it.coverage }
            )

            // Resolve full names for top remedies efficiently
            results.map { item ->
                val entity = remedyDao.getRemedyByAbbreviation(item.abbreviation)
                item.copy(fullName = entity?.fullName ?: item.abbreviation.replaceFirstChar { it.uppercase() })
            }
        }
    }

    companion object {
        fun parseSelectedRubrics(jsonString: String): List<SelectedRubric> {
            if (jsonString.isBlank() || jsonString == "[]") return emptyList()
            return try {
                val array = org.json.JSONArray(jsonString)
                val list = mutableListOf<SelectedRubric>()
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    val id = obj.optLong("id", 0L)
                    val chapter = obj.optString("chapter", "")
                    val text = obj.optString("rubricText", "")
                    val remediesObj = obj.optJSONObject("remedies")
                    val remediesMap = mutableMapOf<String, Int>()
                    if (remediesObj != null) {
                        val keys = remediesObj.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            remediesMap[key] = remediesObj.getInt(key)
                        }
                    }
                    list.add(SelectedRubric(id = id, chapter = chapter, rubricText = text, remedies = remediesMap))
                }
                list
            } catch (e: Exception) {
                emptyList()
            }
        }

        fun serializeSelectedRubrics(list: List<SelectedRubric>): String {
            val array = org.json.JSONArray()
            for (item in list) {
                val obj = JSONObject()
                obj.put("id", item.id)
                obj.put("chapter", item.chapter)
                obj.put("rubricText", item.rubricText)
                val remediesObj = JSONObject()
                for ((k, v) in item.remedies) {
                    remediesObj.put(k, v)
                }
                obj.put("remedies", remediesObj)
                array.put(obj)
            }
            return array.toString()
        }

        fun parseRubricRemedies(remediesJson: String): Map<String, Int> {
            if (remediesJson.isBlank() || remediesJson == "{}") return emptyMap()
            return try {
                val obj = JSONObject(remediesJson)
                val map = mutableMapOf<String, Int>()
                val keys = obj.keys()
                while (keys.hasNext()) {
                    val key = keys.next()
                    map[key] = obj.getInt(key)
                }
                map
            } catch (e: Exception) {
                emptyMap()
            }
        }
    }
}
