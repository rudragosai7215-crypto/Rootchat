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

        return withContext(Dispatchers.IO) {
            val cleanedQuery = q.lowercase()
                .replace("->", " ")
                .replace("[-:,;./]".toRegex(), " ")
                .replace("\\s+".toRegex(), " ")
                .trim()
            val tokens = cleanedQuery.split(" ").filter { it.length >= 2 }
            if (tokens.isEmpty()) return@withContext emptyList()

            val candidates = LinkedHashMap<Long, RubricEntity>()

            // 1. Direct query & multi-token query
            rubricDao.searchRubrics(q, limit = 120).forEach { candidates[it.id] = it }
            rubricDao.searchRubricsPrefix(q, limit = 120).forEach { candidates[it.id] = it }
            if (cleanedQuery != q.lowercase()) {
                rubricDao.searchRubrics(cleanedQuery, limit = 120).forEach { candidates[it.id] = it }
                rubricDao.searchRubricsPrefix(cleanedQuery, limit = 120).forEach { candidates[it.id] = it }
            }

            if (tokens.size >= 3) {
                rubricDao.searchRubricsTriple(tokens[0], tokens[1], tokens[2], limit = 80).forEach { candidates[it.id] = it }
            }
            if (tokens.size >= 2) {
                rubricDao.searchRubricsMulti(tokens[0], tokens[1], limit = 80).forEach { candidates[it.id] = it }
            }

            // 2. Individual token searches
            for (tok in tokens.take(4)) {
                if (tok.length >= 3) {
                    rubricDao.searchRubrics(tok, limit = 60).forEach { candidates[it.id] = it }
                    rubricDao.searchRubricsPrefix(tok, limit = 60).forEach { candidates[it.id] = it }
                }
            }

            // 3. Symptom-based plain-language synonym mapping
            val expandedSynonyms = mutableListOf<String>()
            for ((symptomKey, synonyms) in symptomSynonyms) {
                if (cleanedQuery.contains(symptomKey) || tokens.any { it == symptomKey }) {
                    expandedSynonyms.addAll(synonyms)
                }
            }
            for (syn in expandedSynonyms.distinct().take(4)) {
                rubricDao.searchRubrics(syn, limit = 30).forEach { candidates[it.id] = it }
            }
            if (expandedSynonyms.size >= 2) {
                rubricDao.searchRubricsMulti(expandedSynonyms[0], expandedSynonyms[1], limit = 40).forEach { candidates[it.id] = it }
            }

            // 4. Typo tolerance: prefix & substring pattern search for unrecognized/low-match tokens
            for (tok in tokens) {
                if (tok.length >= 4) {
                    // Try prefix search (e.g. 'throbing' -> 'throb%', 'ringwrom' -> 'ringw%' or 'ring%')
                    val prefix = tok.take(4)
                    rubricDao.searchRubricsByPattern("%$prefix%", limit = 30).forEach { candidates[it.id] = it }
                    if (tok.length >= 6) {
                        val prefix5 = tok.take(5)
                        rubricDao.searchRubricsByPattern("%$prefix5%", limit = 30).forEach { candidates[it.id] = it }
                    }
                }
            }

            if (candidates.isEmpty()) return@withContext emptyList()

            // 5. Score and rank all candidates across the repertory:
            // Priority 1: Exact phrase matches rank first (score >= 1,000,000)
            // Priority 2: Lower priority related word matches rank below (score < 100,000)
            val scored = candidates.values.map { candidate ->
                val fullPath = "${candidate.chapter} - ${candidate.rubricText}".lowercase()
                val fullClean = fullPath.replace("[-:,;./>]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
                val rubricClean = candidate.rubricText.lowercase().replace("[-:,;./>]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
                val chapterClean = candidate.chapter.lowercase().trim()
                val targetWords = fullClean.split(" ")

                // Check for Exact Phrase Match (Highest priority):
                // Clinician's typed text matches as a complete, exact phrase
                val isExactPhrase = fullClean == cleanedQuery ||
                        rubricClean == cleanedQuery ||
                        fullClean.startsWith(cleanedQuery) ||
                        rubricClean.startsWith(cleanedQuery) ||
                        fullClean.contains(cleanedQuery) ||
                        rubricClean.contains(cleanedQuery) ||
                        candidate.rubricText.contains(q, ignoreCase = true) ||
                        fullPath.contains(q, ignoreCase = true)

                var score = 0

                if (isExactPhrase) {
                    // Highest priority tier base: 1,000,000 (strictly ranks above all word-level matches)
                    score = 1_000_000
                    if (fullClean == cleanedQuery) {
                        score += 500_000
                    } else if (rubricClean == cleanedQuery) {
                        score += 450_000
                    } else if (rubricClean.startsWith(cleanedQuery)) {
                        score += 350_000 - candidate.rubricText.length
                    } else if (fullClean.startsWith(cleanedQuery)) {
                        score += 300_000 - candidate.rubricText.length
                    } else if (rubricClean.contains(cleanedQuery)) {
                        score += 200_000 - candidate.rubricText.length
                    } else {
                        score += 100_000 - candidate.rubricText.length
                    }
                }

                // Lower priority: Related word matches evaluation
                var wordScore = 0
                var exactMatches = 0
                var substrMatches = 0
                var typoMatches = 0

                for (qTok in tokens) {
                    when {
                        targetWords.contains(qTok) -> exactMatches++
                        targetWords.any { it.contains(qTok) || qTok.contains(it) } -> substrMatches++
                        targetWords.any { tw ->
                            val maxLen = maxOf(qTok.length, tw.length)
                            if (maxLen >= 7) damerauLevenshtein(qTok, tw) <= 2
                            else if (maxLen >= 4) damerauLevenshtein(qTok, tw) <= 1
                            else false
                        } -> typoMatches++
                    }
                }

                val totalQueryTokens = tokens.size
                if (exactMatches == totalQueryTokens) {
                    wordScore += 30000 + (totalQueryTokens * 1500) - (candidate.rubricText.length / 4)
                    // Check word order preservation bonus
                    var lastIndex = -1
                    var inOrder = true
                    for (tok in tokens) {
                        val idx = fullClean.indexOf(tok)
                        if (idx == -1 || idx < lastIndex) { inOrder = false; break }
                        lastIndex = idx
                    }
                    if (inOrder) wordScore += 8000
                } else if (exactMatches + substrMatches == totalQueryTokens) {
                    wordScore += 20000 + (exactMatches * 1000) - (candidate.rubricText.length / 4)
                } else if (exactMatches + substrMatches + typoMatches == totalQueryTokens) {
                    wordScore += 15000 + (exactMatches * 1000 + typoMatches * 500) - (candidate.rubricText.length / 4)
                } else {
                    wordScore += (exactMatches * 2000 + substrMatches * 1000 + typoMatches * 600) - (candidate.rubricText.length / 6)
                }

                // Chapter match bonus
                if (tokens.isNotEmpty() && chapterClean.contains(tokens[0])) {
                    wordScore += 4000
                }

                // Symptom synonym bonus
                if (expandedSynonyms.isNotEmpty()) {
                    var synMatchCount = 0
                    for (syn in expandedSynonyms) {
                        if (targetWords.contains(syn) || targetWords.any { it.contains(syn) }) {
                            synMatchCount++
                        }
                    }
                    if (synMatchCount > 0) {
                        wordScore += 12000 + (synMatchCount * 2000)
                    }
                }

                // Cap wordScore to 99,999 so it cannot reach Tier 1 base of 1,000,000
                val cappedWordScore = wordScore.coerceIn(0, 99_999)

                val finalScore = if (isExactPhrase) {
                    // For exact phrase matches, word bonuses act as fine-grained tie breakers within Tier 1
                    score + (cappedWordScore / 10)
                } else {
                    // Lower priority: word-level matches only
                    cappedWordScore
                }

                Pair(candidate, finalScore)
            }

            scored.filter { it.second > 0 }
                .sortedByDescending { it.second }
                .map { it.first }
                .take(60)
        }
    }

    private fun damerauLevenshtein(s1: String, s2: String): Int {
        val len1 = s1.length
        val len2 = s2.length
        if (len1 == 0) return len2
        if (len2 == 0) return len1
        if (s1 == s2) return 0
        val dp = Array(len1 + 1) { IntArray(len2 + 1) }
        for (i in 0..len1) dp[i][0] = i
        for (j in 0..len2) dp[0][j] = j
        for (i in 1..len1) {
            for (j in 1..len2) {
                val cost = if (s1[i - 1] == s2[j - 1]) 0 else 1
                dp[i][j] = minOf(
                    dp[i - 1][j] + 1,       // deletion
                    dp[i][j - 1] + 1,       // insertion
                    dp[i - 1][j - 1] + cost // substitution
                )
                if (i > 1 && j > 1 && s1[i - 1] == s2[j - 2] && s1[i - 2] == s2[j - 1]) {
                    dp[i][j] = minOf(dp[i][j], dp[i - 2][j - 2] + cost) // transposition
                }
            }
        }
        return dp[len1][len2]
    }

    private val symptomSynonyms: Map<String, List<String>> = mapOf(
        "headache" to listOf("head", "pain"),
        "headaches" to listOf("head", "pain"),
        "sore throat" to listOf("throat", "pain"),
        "throat ache" to listOf("throat", "pain"),
        "earache" to listOf("ear", "pain"),
        "toothache" to listOf("teeth", "pain"),
        "stomach ache" to listOf("stomach", "pain"),
        "stomachache" to listOf("stomach", "pain"),
        "belly ache" to listOf("abdomen", "pain"),
        "abdominal pain" to listOf("abdomen", "pain"),
        "loose motion" to listOf("diarrhea", "stool"),
        "loose motions" to listOf("diarrhea", "stool"),
        "loose stools" to listOf("diarrhea", "stool"),
        "diarrhea" to listOf("diarrhea", "stool"),
        "diarrhoea" to listOf("diarrhea", "stool"),
        "constipation" to listOf("rectum", "constipation"),
        "sleeplessness" to listOf("sleep", "sleeplessness"),
        "insomnia" to listOf("sleep", "sleeplessness"),
        "cold sores" to listOf("herpes", "eruptions"),
        "fever blister" to listOf("herpes", "eruptions"),
        "hives" to listOf("urticaria", "eruptions"),
        "nettle rash" to listOf("urticaria", "eruptions"),
        "ringworm" to listOf("ringworm", "eruptions"),
        "tinea" to listOf("ringworm", "eruptions"),
        "itching" to listOf("itching", "skin"),
        "itchy" to listOf("itching", "skin"),
        "dizziness" to listOf("vertigo"),
        "giddiness" to listOf("vertigo"),
        "vomiting" to listOf("stomach", "vomiting"),
        "throwing up" to listOf("stomach", "vomiting"),
        "nausea" to listOf("stomach", "nausea"),
        "piles" to listOf("rectum", "hemorrhoids"),
        "hemorrhoids" to listOf("rectum", "hemorrhoids"),
        "haemorrhoids" to listOf("rectum", "hemorrhoids"),
        "hair fall" to listOf("head", "hair", "falling"),
        "hair loss" to listOf("head", "hair", "falling"),
        "baldness" to listOf("head", "hair", "falling"),
        "alopecia" to listOf("head", "hair", "falling"),
        "sweating" to listOf("perspiration"),
        "sweat" to listOf("perspiration"),
        "night sweats" to listOf("perspiration", "night"),
        "panic" to listOf("anxiety", "fear"),
        "anxiety" to listOf("anxiety", "fear"),
        "sadness" to listOf("sadness", "melancholy"),
        "depression" to listOf("sadness", "melancholy"),
        "weakness" to listOf("weakness", "prostration"),
        "fatigue" to listOf("weakness", "prostration"),
        "exhaustion" to listOf("weakness", "prostration"),
        "tiredness" to listOf("weakness", "prostration"),
        "joint pain" to listOf("extremities", "pain"),
        "arthritis" to listOf("extremities", "pain"),
        "pimples" to listOf("face", "eruptions", "pimples"),
        "acne" to listOf("face", "eruptions", "pimples"),
        "boils" to listOf("skin", "eruptions", "boils"),
        "warts" to listOf("skin", "warts"),
        "corns" to listOf("skin", "corns"),
        "runny nose" to listOf("nose", "coryza"),
        "coryza" to listOf("nose", "coryza"),
        "chills" to listOf("chill"),
        "acidity" to listOf("stomach", "heartburn", "eructations"),
        "heartburn" to listOf("stomach", "heartburn"),
        "gas" to listOf("abdomen", "flatulence"),
        "bloating" to listOf("abdomen", "flatulence"),
        "fever" to listOf("fever", "chill", "heat"),
        "high temperature" to listOf("fever", "heat"),
        "pyrexia" to listOf("fever", "heat"),
        "cough" to listOf("cough", "chest"),
        "coughing" to listOf("cough", "chest"),
        "dry cough" to listOf("cough", "dry"),
        "breathlessness" to listOf("respiration", "difficult", "asthmatic"),
        "shortness of breath" to listOf("respiration", "difficult"),
        "asthma" to listOf("respiration", "asthmatic"),
        "back pain" to listOf("back", "pain"),
        "backache" to listOf("back", "pain"),
        "lumbago" to listOf("back", "pain"),
        "sciatica" to listOf("extremities", "pain"),
        "neck pain" to listOf("neck", "pain"),
        "restlessness" to listOf("mind", "restlessness"),
        "anger" to listOf("mind", "anger", "irritability"),
        "irritability" to listOf("mind", "irritability"),
        "temper" to listOf("mind", "anger"),
        "fear" to listOf("mind", "fear"),
        "scared" to listOf("mind", "fear"),
        "fright" to listOf("mind", "fear"),
        "grief" to listOf("mind", "grief", "sadness"),
        "sorrow" to listOf("mind", "grief"),
        "eczema" to listOf("skin", "eruptions"),
        "dermatitis" to listOf("skin", "eruptions"),
        "psoriasis" to listOf("skin", "eruptions"),
        "bleeding" to listOf("generalities", "hemorrhage"),
        "hemorrhage" to listOf("generalities", "hemorrhage"),
        "numbness" to listOf("extremities", "numbness"),
        "trembling" to listOf("generalities", "trembling"),
        "shivering" to listOf("chill", "shivering"),
        "vertigo" to listOf("vertigo")
    )

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

    suspend fun getRubricsInChaptersPaged(
        chapters: List<String>,
        query: String,
        limit: Int = 100,
        offset: Int = 0
    ): Pair<List<RubricEntity>, Int> = withContext(Dispatchers.IO) {
        if (chapters.isEmpty()) return@withContext Pair(emptyList(), 0)
        val q = query.trim()
        if (q.isBlank()) {
            val total = rubricDao.getRubricCountInChapters(chapters)
            val list = rubricDao.getRubricsByChapters(chapters, limit, offset)
            Pair(list, total)
        } else {
            val total = rubricDao.getSearchRubricCountInChapters(chapters, q)
            val list = rubricDao.searchRubricsInChapters(chapters, q, limit, offset)
            Pair(list, total)
        }
    }

    suspend fun searchRubricsInChapter(chapter: String, query: String, limit: Int = 100): List<RubricEntity> = withContext(Dispatchers.IO) {
        if (query.isBlank()) {
            rubricDao.getRubricsByChapter(chapter, limit, 0)
        } else {
            searchRubricsInChapters(listOf(chapter), query, limit)
        }
    }

    suspend fun searchRubricsInChapters(chapters: List<String>, query: String, limit: Int = 180): List<RubricEntity> = withContext(Dispatchers.IO) {
        if (chapters.isEmpty()) return@withContext emptyList()
        if (query.isBlank()) {
            return@withContext rubricDao.getRubricsByChapters(chapters, limit, 0)
        }

        val q = query.trim()
        val cleanedQuery = q.lowercase()
            .replace("->", " ")
            .replace("[-:,;./]".toRegex(), " ")
            .replace("\\s+".toRegex(), " ")
            .trim()
        val tokens = cleanedQuery.split(" ").filter { it.length >= 2 }
        if (tokens.isEmpty()) {
            return@withContext rubricDao.getRubricsByChapters(chapters, limit, 0)
        }

        val candidates = LinkedHashMap<Long, RubricEntity>()

        // 1. Fetch exact phrase candidates from chapters
        rubricDao.searchRubricsInChapters(chapters, q, limit).forEach { candidates[it.id] = it }
        if (cleanedQuery != q.lowercase()) {
            rubricDao.searchRubricsInChapters(chapters, cleanedQuery, limit).forEach { candidates[it.id] = it }
        }

        // 2. Fetch individual word candidates for multi-word queries
        for (tok in tokens.take(4)) {
            if (tok.length >= 3) {
                rubricDao.searchRubricsInChapters(chapters, tok, limit = 60).forEach { candidates[it.id] = it }
            }
        }

        if (candidates.isEmpty()) return@withContext emptyList()

        // 3. Prioritize: Exact phrase matches (Highest Priority) first, then related word matches
        val exactMatches = mutableListOf<Pair<RubricEntity, Int>>()
        val wordMatches = mutableListOf<Pair<RubricEntity, Int>>()

        for (cand in candidates.values) {
            val rubricClean = cand.rubricText.lowercase()
                .replace("[-:,;./>]".toRegex(), " ")
                .replace("\\s+".toRegex(), " ")
                .trim()
            val rawContains = cand.rubricText.contains(q, ignoreCase = true)
            val cleanContains = rubricClean.contains(cleanedQuery)

            if (rawContains || cleanContains) {
                // Tier 1: Exact phrase match
                var score = 1_000_000
                if (cand.rubricText.equals(q, ignoreCase = true) || rubricClean == cleanedQuery) {
                    score += 500_000
                } else if (cand.rubricText.startsWith(q, ignoreCase = true) || rubricClean.startsWith(cleanedQuery)) {
                    score += 250_000 - cand.rubricText.length
                } else {
                    score += 100_000 - cand.rubricText.length
                }
                exactMatches.add(Pair(cand, score))
            } else {
                // Tier 2: Word-level match
                var matchedTokens = 0
                val targetWords = rubricClean.split(" ")
                for (tok in tokens) {
                    if (targetWords.contains(tok)) {
                        matchedTokens += 2
                    } else if (targetWords.any { it.contains(tok) || tok.contains(it) }) {
                        matchedTokens += 1
                    }
                }
                if (matchedTokens > 0) {
                    val score = (matchedTokens * 1000) - (cand.rubricText.length / 4)
                    wordMatches.add(Pair(cand, score))
                }
            }
        }

        val sortedExact = exactMatches.sortedByDescending { it.second }.map { it.first }
        val sortedWord = wordMatches.sortedByDescending { it.second }.map { it.first }

        (sortedExact + sortedWord).take(limit)
    }

    suspend fun getRubricById(id: Long): RubricEntity? = withContext(Dispatchers.IO) {
        rubricDao.getRubricById(id)
    }

    @Volatile
    private var remedyNameCache: Map<String, String>? = null

    private suspend fun getRemedyMap(): Map<String, String> {
        remedyNameCache?.let { return it }
        return withContext(Dispatchers.IO) {
            val list = remedyDao.getAllRemediesSync()
            val map = list.associate { it.abbreviation.lowercase().trim() to it.fullName }
            remedyNameCache = map
            map
        }
    }

    suspend fun computeRepertorization(selectedRubrics: List<SelectedRubric>): List<RepertorizationScore> {
        if (selectedRubrics.isEmpty()) return emptyList()

        val remedyMap = getRemedyMap()

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

            // Map and resolve full names from cache instantaneously
            scoreMap.map { (abbr, score) ->
                val coverage = coverageMap[abbr] ?: 0
                val breakdown = breakdownMap[abbr] ?: emptyMap()
                val resolvedName = remedyMap[abbr] ?: abbr.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(java.util.Locale.ROOT) else it.toString()
                }
                RepertorizationScore(
                    abbreviation = abbr,
                    fullName = resolvedName,
                    score = score,
                    coverage = coverage,
                    gradeBreakdown = breakdown
                )
            }.sortedWith(
                compareByDescending<RepertorizationScore> { it.score }
                    .thenByDescending { it.coverage }
            )
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
