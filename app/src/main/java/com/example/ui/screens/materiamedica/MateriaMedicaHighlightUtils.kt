package com.example.ui.screens.materiamedica

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import com.example.data.CaseEntity
import com.example.data.SelectedRubric

data class HighlightTerms(
    val exactPhrases: List<String> = emptyList(),
    val relatedWords: Set<String> = emptySet()
)

data class HighlightResult(
    val annotatedText: AnnotatedString,
    val exactMatchCount: Int,
    val wordMatchCount: Int
) {
    val totalCount: Int get() = exactMatchCount + wordMatchCount
}

object MateriaMedicaHighlightUtils {

    val STOP_WORDS = setOf(
        "the", "and", "with", "from", "after", "before", "during", "when", "then",
        "than", "that", "this", "these", "those", "have", "has", "had", "will", "would",
        "should", "could", "been", "being", "very", "much", "more", "most", "also",
        "into", "over", "under", "about", "such", "some", "only", "other", "their",
        "there", "where", "which", "while", "what", "pain", "pains", "feel", "feels",
        "like", "without", "every", "part", "parts", "both", "side", "sides", "well"
    )

    /**
     * Extracts salient clinical search terms from a case to auto-highlight in Materia Medica,
     * categorized into exact symptom phrases (Priority 1) and related individual words (Priority 2).
     */
    fun extractCaseTerms(
        selectedRubrics: List<SelectedRubric> = emptyList(),
        caseEntity: CaseEntity? = null
    ): HighlightTerms {
        val exactPhrases = mutableListOf<String>()
        val words = mutableSetOf<String>()

        // 1. From Selected Rubrics
        for (rub in selectedRubrics) {
            val cleanRubric = rub.rubricText
                .replace("->", " ")
                .replace("[-:,;./]".toRegex(), " ")
                .replace("\\s+".toRegex(), " ")
                .trim()
                .lowercase()
            if (cleanRubric.length >= 4) {
                exactPhrases.add(cleanRubric)
            }
            // Sub-clauses of rubric
            val subParts = rub.rubricText.split("->", "-", ",").map {
                it.replace("[-:,;./]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim().lowercase()
            }
            for (part in subParts) {
                if (part.length >= 4 && part.contains(" ")) {
                    exactPhrases.add(part)
                }
            }

            val tokens = rub.rubricText.split(Regex("[^a-zA-Z0-9]+"))
            for (t in tokens) {
                val clean = t.lowercase().trim()
                if (clean.length >= 4 && clean !in STOP_WORDS) {
                    words.add(clean)
                }
            }
        }

        // 2. From Totality Notes & Chief Complaint & LSMC blocks
        val textBlocks = listOfNotNull(
            caseEntity?.totalitySymptomNotes,
            caseEntity?.chiefComplaint,
            caseEntity?.mentalSymptoms,
            caseEntity?.physicalGenerals,
            caseEntity?.lsmcSensation,
            caseEntity?.lsmcModality,
            caseEntity?.cravingsAversions
        )

        for (block in textBlocks) {
            if (block.isNotBlank()) {
                val lines = block.split("\n", ";", ".")
                for (l in lines) {
                    val cleanLine = l.replace("^\\s*(\\d+[.)\\-\\s]+|[-*•]\\s*)".toRegex(), "")
                        .replace("[-:,;./]".toRegex(), " ")
                        .replace("\\s+".toRegex(), " ")
                        .trim()
                        .lowercase()
                    if (cleanLine.length >= 4 && cleanLine !in STOP_WORDS) {
                        exactPhrases.add(cleanLine)
                    }
                }

                val tokens = block.split(Regex("[^a-zA-Z0-9]+"))
                for (w in tokens) {
                    val clean = w.lowercase().trim()
                    if (clean.length >= 4 && clean !in STOP_WORDS) {
                        words.add(clean)
                    }
                }
            }
        }

        return HighlightTerms(
            exactPhrases = exactPhrases.distinct().sortedByDescending { it.length },
            relatedWords = words
        )
    }

    /**
     * Backward-compatible keyword extractor
     */
    fun extractCaseKeywords(
        selectedRubrics: List<SelectedRubric> = emptyList(),
        caseEntity: CaseEntity? = null
    ): Set<String> {
        return extractCaseTerms(selectedRubrics, caseEntity).relatedWords
    }

    /**
     * Highlights matching terms in symptomText prioritizing:
     * 1. Highest priority — exact phrase match: complete phrase highlighted first and most prominently
     * 2. Lower priority — related word matches: highlighted after and non-overlapping
     */
    fun highlightMatches(
        text: String,
        terms: HighlightTerms,
        isDark: Boolean,
        userQuery: String = ""
    ): HighlightResult {
        if (text.isBlank()) {
            return HighlightResult(AnnotatedString(""), 0, 0)
        }

        val exactHighlightBg = if (isDark) Color(0x66FF8F00) else Color(0x4DFF6F00)
        val exactHighlightColor = if (isDark) Color(0xFFFFE082) else Color(0xFFBF360C)

        val wordHighlightBg = if (isDark) Color(0x3DFFA000) else Color(0x2BFFB74D)
        val wordHighlightColor = if (isDark) Color(0xFFFFD54F) else Color(0xFF8A4700)

        val lowerText = text.lowercase()

        // 1. Collect exact phrases to check (clinician typed query ranks first)
        val candidateExactPhrases = mutableListOf<String>()
        val trimmedQuery = userQuery.trim().lowercase()
        if (trimmedQuery.length >= 2) {
            candidateExactPhrases.add(trimmedQuery)
            val cleanedUserQuery = trimmedQuery.replace("[-:,;./]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
            if (cleanedUserQuery != trimmedQuery && cleanedUserQuery.length >= 2) {
                candidateExactPhrases.add(cleanedUserQuery)
            }
        }
        candidateExactPhrases.addAll(terms.exactPhrases)

        // Find exact phrase match ranges
        val exactRanges = mutableListOf<IntRange>()
        for (phrase in candidateExactPhrases.distinct()) {
            if (phrase.isBlank()) continue
            var startIndex = 0
            while (startIndex < lowerText.length) {
                val found = lowerText.indexOf(phrase, startIndex)
                if (found == -1) break

                val prevChar = if (found > 0) lowerText[found - 1] else ' '
                val nextCharIdx = found + phrase.length
                val nextChar = if (nextCharIdx < lowerText.length) lowerText[nextCharIdx] else ' '

                val isWordStart = !prevChar.isLetterOrDigit()
                val isWordEnd = !nextChar.isLetterOrDigit()

                if (isWordStart && isWordEnd) {
                    val range = found until (found + phrase.length)
                    // Check if already covered by an existing exact range
                    if (exactRanges.none { it.first <= range.first && it.last >= range.last }) {
                        exactRanges.add(range)
                    }
                }
                startIndex = found + 1
            }
        }

        // Merge overlapping exact ranges
        val mergedExactRanges = mergeIntervals(exactRanges)

        // 2. Find related word matches (non-overlapping with exact phrase ranges)
        val candidateWords = mutableSetOf<String>()
        if (trimmedQuery.length >= 2) {
            val queryTokens = trimmedQuery.split(Regex("[^a-zA-Z0-9]+")).filter { it.length >= 3 && it !in STOP_WORDS }
            candidateWords.addAll(queryTokens)
        }
        candidateWords.addAll(terms.relatedWords)

        val wordRanges = mutableListOf<IntRange>()
        for (word in candidateWords) {
            if (word.length < 3) continue
            var startIndex = 0
            while (startIndex < lowerText.length) {
                val found = lowerText.indexOf(word, startIndex)
                if (found == -1) break

                val prevChar = if (found > 0) lowerText[found - 1] else ' '
                val nextCharIdx = found + word.length
                val nextChar = if (nextCharIdx < lowerText.length) lowerText[nextCharIdx] else ' '

                val isWordStart = !prevChar.isLetterOrDigit()
                val isWordEnd = !nextChar.isLetterOrDigit()

                if (isWordStart && isWordEnd) {
                    val range = found until (found + word.length)
                    // Skip if inside or overlapping with any exact phrase match
                    val overlapsExact = mergedExactRanges.any { exact ->
                        maxOf(exact.first, range.first) <= minOf(exact.last, range.last)
                    }
                    if (!overlapsExact) {
                        wordRanges.add(range)
                    }
                }
                startIndex = found + 1
            }
        }

        val mergedWordRanges = mergeIntervals(wordRanges)

        // Build AnnotatedString with exact matches styled first/prominently, then word matches
        val annotated = buildAnnotatedString {
            append(text)

            // Priority 1: Exact phrase matches
            for (range in mergedExactRanges) {
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.ExtraBold,
                        background = exactHighlightBg,
                        color = exactHighlightColor
                    ),
                    start = range.first,
                    end = minOf(range.last + 1, text.length)
                )
            }

            // Priority 2: Related word matches
            for (range in mergedWordRanges) {
                addStyle(
                    style = SpanStyle(
                        fontWeight = FontWeight.Bold,
                        background = wordHighlightBg,
                        color = wordHighlightColor
                    ),
                    start = range.first,
                    end = minOf(range.last + 1, text.length)
                )
            }
        }

        return HighlightResult(
            annotatedText = annotated,
            exactMatchCount = mergedExactRanges.size,
            wordMatchCount = mergedWordRanges.size
        )
    }

    /**
     * Backward-compatible highlight helper
     */
    fun highlightMatches(
        text: String,
        keywords: Set<String>,
        isDark: Boolean
    ): Pair<AnnotatedString, Int> {
        val result = highlightMatches(
            text = text,
            terms = HighlightTerms(relatedWords = keywords),
            isDark = isDark
        )
        return Pair(result.annotatedText, result.totalCount)
    }

    private fun mergeIntervals(intervals: List<IntRange>): List<IntRange> {
        if (intervals.isEmpty()) return emptyList()
        val sorted = intervals.sortedBy { it.first }
        val merged = mutableListOf<IntRange>()
        var current = sorted[0]
        for (i in 1 until sorted.size) {
            val next = sorted[i]
            if (next.first <= current.last + 1) {
                current = current.first..maxOf(current.last, next.last)
            } else {
                merged.add(current)
                current = next
            }
        }
        merged.add(current)
        return merged
    }
}
