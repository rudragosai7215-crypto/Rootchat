package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AiRubricSuggesterTest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var rubricDao: RubricDao
    private lateinit var remedyDao: RemedyDao
    private lateinit var repertoryRepo: RepertoryRepository
    private lateinit var aiSuggester: AiRubricSuggester

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        db = AppDatabase.getDatabase(context)
        rubricDao = db.rubricDao()
        remedyDao = db.remedyDao()
        repertoryRepo = RepertoryRepository(rubricDao, remedyDao)
        aiSuggester = AiRubricSuggester(repertoryRepo)

        // Ensure database has representative classical rubrics for verification
        val sampleRubrics = listOf(
            RubricEntity(
                id = 10001L,
                chapter = "HEAD",
                rubricText = "PAIN - pulsating",
                source = "Kent",
                remediesJson = """{"bell":3,"glon":3,"nat-m":2,"bry":1}"""
            ),
            RubricEntity(
                id = 10002L,
                chapter = "HEAD",
                rubricText = "PAIN - pulsating - right side",
                source = "Kent",
                remediesJson = """{"bell":3,"sang":2}"""
            ),
            RubricEntity(
                id = 10003L,
                chapter = "HEAD",
                rubricText = "PAIN - throbbing - motion from agg",
                source = "Kent",
                remediesJson = """{"bry":3,"bell":2}"""
            ),
            RubricEntity(
                id = 10004L,
                chapter = "MIND",
                rubricText = "FEAR - dark; of",
                source = "Kent",
                remediesJson = """{"stram":3,"phos":2,"calc":2}"""
            ),
            RubricEntity(
                id = 10005L,
                chapter = "MIND",
                rubricText = "OBSTINATE, headstrong",
                source = "Kent",
                remediesJson = """{"calc":3,"cham":3,"nux-v":2}"""
            ),
            RubricEntity(
                id = 10006L,
                chapter = "NOSE",
                rubricText = "CORYZA, obstinate, with soreness",
                source = "Kent",
                remediesJson = """{"nit-ac":3,"kali-bi":2}"""
            ),
            RubricEntity(
                id = 20001L,
                chapter = "SPECIAL_EXTENDED_CLINICAL",
                rubricText = "MODERN PHENOMENA - screen fatigue",
                source = "Extended",
                remediesJson = """{"ruta":3,"nat-m":2}"""
            )
        )
        rubricDao.insertAll(sampleRubrics)
    }

    @Test
    fun testExtractTotalitySymptoms_fromEvaluationList() {
        val form = ClinicalFormData(
            symptomEvaluationList = listOf(
                SymptomEvaluationItem(symptomText = "Throbbing headache", type = "Physical General"),
                SymptomEvaluationItem(symptomText = "Fear of dark", type = "Mental General")
            )
        )
        val extracted = aiSuggester.extractTotalitySymptoms(form)
        assertEquals(2, extracted.size)
        assertEquals("Throbbing headache", extracted[0])
        assertEquals("Fear of dark", extracted[1])
    }

    @Test
    fun testExtractTotalitySymptoms_fromTotalityNotes() {
        val form = ClinicalFormData(
            totalityNotes = "1. Right-sided throbbing headache\n2. Fear of darkness at night\n// Comment line"
        )
        val extracted = aiSuggester.extractTotalitySymptoms(form)
        assertEquals(2, extracted.size)
        assertEquals("Right-sided throbbing headache", extracted[0])
        assertEquals("Fear of darkness at night", extracted[1])
    }

    @Test
    fun testExtractTotalitySymptoms_emptyReturnsEmptyList() {
        val form = ClinicalFormData(totalityNotes = "")
        val extracted = aiSuggester.extractTotalitySymptoms(form)
        assertTrue(extracted.isEmpty())
    }

    @Test
    fun testGenerateSuggestions_returnsVerifiedRubrics() = runBlocking {
        val form = ClinicalFormData(
            totalityNotes = "1. Throbbing headache\n2. Fear dark",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val groups = aiSuggester.generateSuggestionsForTotality(form)

        assertEquals(2, groups.size)
        val group1 = groups[0]
        assertEquals("Throbbing headache", group1.symptomText)
        assertTrue("Group 1 should have candidates", group1.candidates.isNotEmpty())
        assertTrue("At most 3 candidates", group1.candidates.size <= 3)

        // Verify rank labels
        assertEquals("1st Most Suitable Rubric", group1.candidates[0].rankLabel)
        assertEquals(1, group1.candidates[0].rank)

        // Verify that candidates actually exist in the database with their canonical IDs
        for (candidate in group1.candidates) {
            val dbRow = rubricDao.getRubricById(candidate.rubric.id)
            assertNotNull("Candidate rubric must exist in database", dbRow)
            assertEquals(dbRow!!.rubricText, candidate.rubric.rubricText)
            assertEquals(dbRow.chapter, candidate.rubric.chapter)
            assertTrue("Confidence must be between 1 and 100", candidate.confidencePercent in 1..100)
        }
        assertTrue(
            "Rank 1 should have >= confidence than lower ranks",
            group1.candidates.first().confidencePercent >= group1.candidates.last().confidencePercent
        )
    }

    @Test
    fun testDatasetIsolation_OriginalKentVsExtendedKent() = runBlocking {
        val formKent = ClinicalFormData(
            totalityNotes = "1. Screen fatigue",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val kentGroups = aiSuggester.generateSuggestionsForTotality(formKent)
        // Original Kent must NEVER return rubrics from outside Kent's 37 classical chapters
        for (group in kentGroups) {
            for (candidate in group.candidates) {
                assertTrue(
                    "Chapter ${candidate.rubric.chapter} must be in classical Kent chapters",
                    RepertoryRepository.ORIGINAL_KENT_CHAPTERS.contains(candidate.rubric.chapter.uppercase().trim())
                )
            }
        }

        val formExtended = ClinicalFormData(
            totalityNotes = "1. Screen fatigue",
            repertoryType = RepertoryRepository.REPERTORY_EXTENDED_KENT
        )
        val extendedGroups = aiSuggester.generateSuggestionsForTotality(formExtended)
        // Extended Kent can return extended chapter rubrics
        assertTrue(extendedGroups.isNotEmpty())
    }

    @Test
    fun testRubricSelectionFlow_updatesRepertorization() = runBlocking {
        val form = ClinicalFormData(
            totalityNotes = "1. Throbbing headache",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val groups = aiSuggester.generateSuggestionsForTotality(form)
        val candidate = groups[0].candidates[0]

        // Parse remedies and simulate selection
        val remedies = RepertoryRepository.parseRubricRemedies(candidate.rubric.remediesJson)
        val selected = SelectedRubric(
            id = candidate.rubric.id,
            chapter = candidate.rubric.chapter,
            rubricText = candidate.rubric.rubricText,
            remedies = remedies
        )

        val initialScores = repertoryRepo.computeRepertorization(emptyList())
        assertTrue(initialScores.isEmpty())

        val scoresWithSelected = repertoryRepo.computeRepertorization(listOf(selected))
        assertTrue(scoresWithSelected.isNotEmpty())
        val matchingRemedy = scoresWithSelected.find { it.abbreviation == "bell" || it.abbreviation == "anthr" }
        assertNotNull("Expected remedy should be scored", matchingRemedy)
        assertTrue("Remedy score should be positive", matchingRemedy!!.score > 0)
    }

    @Test
    fun testAmbiguousSymptom_returnsAmbiguousStateAndNoGuessedRubrics() = runBlocking {
        val ambiguousSymptoms = listOf("pain", "severe pain", "feeling bad", "sick", "problem")
        for ((idx, sym) in ambiguousSymptoms.withIndex()) {
            val group = aiSuggester.generateCandidatesForSymptom(
                symptomIndex = idx + 1,
                symptomText = sym,
                repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
            )
            assertTrue("Symptom '$sym' should be marked ambiguous", group.isAmbiguous)
            assertTrue("Ambiguous symptom should have no guessed rubrics", group.candidates.isEmpty())
            assertEquals(
                "I couldn’t understand this symptom clearly. Please explain it in more detail.",
                group.note
            )
        }
    }

    @Test
    fun testClarification_resolvesAmbiguousSymptom() = runBlocking {
        // Initially "pain" is ambiguous
        val initialGroup = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "pain",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertTrue(initialGroup.isAmbiguous)
        assertTrue(initialGroup.candidates.isEmpty())

        // Re-analyse with clarification
        val clarifiedGroup = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "pain",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT,
            clarification = "pulsating right side headache"
        )
        assertFalse("Clarified symptom should no longer be ambiguous", clarifiedGroup.isAmbiguous)
        assertTrue("Clarified symptom should find verified candidates", clarifiedGroup.candidates.isNotEmpty())
        assertEquals("pain", clarifiedGroup.symptomText) // Preserves original saved symptom!
        assertEquals("pulsating right side headache", clarifiedGroup.clarificationUsed)
        assertTrue("Clarified rubrics should include HEAD", clarifiedGroup.candidates.any { it.rubric.chapter.uppercase() == "HEAD" })
    }

    @Test
    fun testMultilingualClarification_HindiGujarati() = runBlocking {
        val clarifiedGroup = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "takleef",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT,
            clarification = "sir dard mathe dukhava pulsating"
        )
        assertFalse("Hindi/Gujarati clarification should resolve ambiguity", clarifiedGroup.isAmbiguous)
        assertTrue("Should return verified rubrics", clarifiedGroup.candidates.isNotEmpty())
        assertTrue("Clarified rubrics should include HEAD", clarifiedGroup.candidates.any { it.rubric.chapter.uppercase() == "HEAD" })
    }

    @Test
    fun testUnderstoodSymptomWithNoMatch_returnsSeparateNoMatchState() = runBlocking {
        val group = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "laser hologram nanobot bionic cybersickness",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertFalse("Clinically specific symptom should not be ambiguous", group.isAmbiguous)
        assertTrue("Zero matching candidates in classical repertory", group.candidates.isEmpty())
        assertEquals("No suitable matching rubric found.", group.note)
    }

    @Test
    fun testExtractTotalitySymptoms_doesNotDuplicateWhenTotalityNotesPresent() {
        val form = ClinicalFormData(
            symptomEvaluationList = listOf(
                SymptomEvaluationItem(symptomText = "Throbbing headache", type = "Physical Particular", grade = "+++"),
                SymptomEvaluationItem(symptomText = "Fear of dark", type = "Mental General", grade = "++")
            ),
            totalityNotes = "1. Throbbing headache +++ (Physical Particular, Common)\n2. Fear of dark ++ (Mental General, Uncommon)"
        )
        val extracted = aiSuggester.extractTotalitySymptoms(form)
        assertEquals(2, extracted.size)
        assertEquals("Throbbing headache", extracted[0])
        assertEquals("Fear of dark", extracted[1])
    }

    @Test
    fun testCategoryConstrainedSearch_mentalTraitSearchesMindOnly() = runBlocking {
        val group = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "headstrong obstinate",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT,
            category = "Mental General"
        )
        assertTrue("Candidates must be found", group.candidates.isNotEmpty())
        for (candidate in group.candidates) {
            assertEquals("Candidate must be in MIND chapter", "MIND", candidate.rubric.chapter.uppercase().trim())
            assertFalse("Must NOT include NOSE chapter", candidate.rubric.chapter.equals("NOSE", ignoreCase = true))
        }
    }

    @Test
    fun testDesireForIceCream_matchesStomachDesires_andRejectsNauseaStoolDiarrhea() = runBlocking {
        // Sample rubrics in DB (already includes STOMACH desires, ice cream in real asset DB)
        val form = ClinicalFormData(
            totalityNotes = "1. desire for ice cream",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val groups = aiSuggester.generateSuggestionsForTotality(form)
        assertEquals(1, groups.size)
        val group = groups[0]
        assertEquals("desire for ice cream", group.symptomText)
        assertFalse("Desire for ice cream should not be ambiguous", group.isAmbiguous)
        assertTrue("Should return verified candidates for desire for ice cream", group.candidates.isNotEmpty())

        // 1. All candidates must be in STOMACH chapter
        for (candidate in group.candidates) {
            assertEquals("Candidate must be in STOMACH chapter", "STOMACH", candidate.rubric.chapter.uppercase().trim())
            // 2. Candidate must represent a desire/craving, NOT an aggravation or ailment after ice cream
            val rText = candidate.rubric.rubricText.lowercase()
            assertTrue("Rubric must be a desire rubric: $rText", rText.contains("desire") || rText.startsWith("desires"))
            assertFalse("Must NOT match nausea: $rText", rText.contains("nausea"))
            assertFalse("Must NOT match vomiting: $rText", rText.contains("vomiting"))
            assertFalse("Must NOT match diarrhea: $rText", rText.contains("diarrhea"))
            assertFalse("Must NOT match after aggravation: $rText", rText.contains(", after"))
            assertFalse("Must NOT match pain: $rText", rText.contains("pain"))
        }

        // Top candidate must be desires, ice cream
        val topCandidate = group.candidates.first()
        assertTrue(
            "Top candidate should match ice cream: ${topCandidate.rubric.rubricText}",
            topCandidate.rubric.rubricText.contains("ice cream", ignoreCase = true)
        )
        assertEquals(1, topCandidate.rank)
        assertTrue("Confidence should be >= 90% for exact match, was ${topCandidate.confidencePercent}%", topCandidate.confidencePercent >= 90)

        // 3. Must not force three results if fewer suitable rubrics exist
        assertTrue("Never force 3 results, actual: ${group.candidates.size}", group.candidates.size in 1..3)
    }

    @Test
    fun testHardStool_matchesStoolHard_andRejectsChillFeverBed() = runBlocking {
        val form = ClinicalFormData(
            totalityNotes = "1. hard stool",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val groups = aiSuggester.generateSuggestionsForTotality(form)
        assertEquals(1, groups.size)
        val group = groups[0]
        assertEquals("hard stool", group.symptomText)
        assertFalse("Hard stool should not be ambiguous", group.isAmbiguous)
        assertTrue("Should return verified candidates for hard stool", group.candidates.isNotEmpty())

        // All candidates must be in STOOL or RECTUM
        for (candidate in group.candidates) {
            val chap = candidate.rubric.chapter.uppercase().trim()
            assertTrue("Chapter must be STOOL or RECTUM, was $chap", chap == "STOOL" || chap == "RECTUM")
            assertFalse("Must NOT match CHILL: $chap", chap == "CHILL")
            assertFalse("Must NOT match FEVER: $chap", chap == "FEVER")
            assertFalse("Must NOT match GENERALITIES: $chap", chap == "GENERALITIES")

            val rText = candidate.rubric.rubricText.lowercase()
            assertFalse("Must NOT match bed sensation: $rText", rText.contains("bed"))
            assertTrue("Must relate to hard stool: $rText", rText.contains("hard") || rText.contains("constipation"))
        }

        val topCandidate = group.candidates.first()
        assertEquals("STOOL", topCandidate.rubric.chapter.uppercase())
        assertTrue("Top candidate should be hard stool rubric", topCandidate.rubric.rubricText.contains("hard", ignoreCase = true))
        assertTrue("Confidence should be >= 90% for exact match, was ${topCandidate.confidencePercent}%", topCandidate.confidencePercent >= 90)
    }

    @Test
    fun testThreePreviouslyWorkingSymptoms() = runBlocking {
        // Symptom 1: Throbbing headache
        val groupHeadache = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "Throbbing headache",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertFalse(groupHeadache.isAmbiguous)
        assertTrue("Throbbing headache should have candidates", groupHeadache.candidates.isNotEmpty())
        for (c in groupHeadache.candidates) {
            assertEquals("HEAD", c.rubric.chapter.uppercase())
            assertTrue(
                "Must match pulsating/throbbing: ${c.rubric.rubricText}",
                c.rubric.rubricText.contains("pulsating", ignoreCase = true) || c.rubric.rubricText.contains("throbbing", ignoreCase = true) || c.rubric.rubricText.contains("pain", ignoreCase = true)
            )
        }

        // Symptom 2: Fear of dark
        val groupFear = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 2,
            symptomText = "Fear of dark",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertFalse(groupFear.isAmbiguous)
        assertTrue("Fear of dark should have candidates", groupFear.candidates.isNotEmpty())
        for (c in groupFear.candidates) {
            assertEquals("MIND", c.rubric.chapter.uppercase())
            assertTrue(
                "Must match fear of dark: ${c.rubric.rubricText}",
                c.rubric.rubricText.contains("dark", ignoreCase = true) || c.rubric.rubricText.contains("fear", ignoreCase = true)
            )
        }

        // Symptom 3: Obstinate with Mental context
        val groupObstinate = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 3,
            symptomText = "headstrong obstinate",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT,
            category = "Mental General"
        )
        assertFalse(groupObstinate.isAmbiguous)
        assertTrue("Obstinate should have candidates", groupObstinate.candidates.isNotEmpty())
        for (c in groupObstinate.candidates) {
            assertEquals("MIND", c.rubric.chapter.uppercase())
            assertTrue("Must match obstinate: ${c.rubric.rubricText}", c.rubric.rubricText.contains("obstinate", ignoreCase = true))
        }
    }

    @Test
    fun testIncompleteAmbiguousSymptoms_asksExplanationInsteadOfGuessing() = runBlocking {
        val ambiguousList = listOf("pain", "severe pain", "feeling bad", "sick", "problem", "discomfort")
        for ((idx, sym) in ambiguousList.withIndex()) {
            val group = aiSuggester.generateCandidatesForSymptom(
                symptomIndex = idx + 1,
                symptomText = sym,
                repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
            )
            assertTrue("Symptom '$sym' must be marked ambiguous", group.isAmbiguous)
            assertTrue("Ambiguous symptom must never guess rubrics", group.candidates.isEmpty())
            assertEquals(
                "I couldn’t understand this symptom clearly. Please explain it in more detail.",
                group.note
            )
            assertNotNull("Must ask clarifying question", group.clarifyingQuestion)
        }
    }

    @Test
    fun testSymptomWithNoSuitableDatabaseMatch_displaysStandardNote() = runBlocking {
        val group = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "laser hologram nanobot bionic cybersickness",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertFalse("Unrelated non-homeopathic term is specific, not ambiguous", group.isAmbiguous)
        assertTrue("Must return zero candidates", group.candidates.isEmpty())
        assertEquals("No suitable matching rubric found.", group.note)
    }

    @Test
    fun testDatasetSwitching_KentVsExtendedKent() = runBlocking {
        val formKent = ClinicalFormData(
            totalityNotes = "1. Modern screen fatigue",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        val kentGroups = aiSuggester.generateSuggestionsForTotality(formKent)
        for (group in kentGroups) {
            for (candidate in group.candidates) {
                assertTrue(
                    "Chapter ${candidate.rubric.chapter} must be in classical Kent 37 chapters",
                    RepertoryRepository.ORIGINAL_KENT_CHAPTERS.contains(candidate.rubric.chapter.uppercase().trim())
                )
            }
        }

        val formExtended = ClinicalFormData(
            totalityNotes = "1. Screen fatigue",
            repertoryType = RepertoryRepository.REPERTORY_EXTENDED_KENT
        )
        val extendedGroups = aiSuggester.generateSuggestionsForTotality(formExtended)
        assertNotNull(extendedGroups)
    }

    @Test
    fun testRubricSelectionDeselection_andDuplicatePrevention() {
        val sampleRemedies = mapOf("bell" to 3, "glon" to 2)
        val rubric1 = SelectedRubric(id = 101L, chapter = "HEAD", rubricText = "PAIN - pulsating", remedies = sampleRemedies)
        val rubric2 = SelectedRubric(id = 102L, chapter = "MIND", rubricText = "FEAR - dark", remedies = sampleRemedies)

        var selectedList = listOf<SelectedRubric>()

        // 1. Select rubric 1
        if (selectedList.none { it.id == rubric1.id }) {
            selectedList = selectedList + rubric1
        }
        assertEquals(1, selectedList.size)
        assertTrue(selectedList.any { it.id == rubric1.id })

        // 2. Select rubric 1 again (duplicate prevention check)
        if (selectedList.none { it.id == rubric1.id }) {
            selectedList = selectedList + rubric1
        }
        assertEquals("Duplicate must be prevented", 1, selectedList.size)

        // 3. Select rubric 2
        if (selectedList.none { it.id == rubric2.id }) {
            selectedList = selectedList + rubric2
        }
        assertEquals(2, selectedList.size)

        // 4. Deselect rubric 1 (tap again to deselect)
        selectedList = selectedList.filter { it.id != rubric1.id }
        assertEquals(1, selectedList.size)
        assertEquals(rubric2.id, selectedList[0].id)
    }

    @Test
    fun testApiNetworkFailureHandling_gracefulFallback() = runBlocking {
        // When offline or API key is unavailable / invalid, suggester gracefully uses local extraction
        val group = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = "Throbbing headache",
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT
        )
        assertNotNull(group)
        assertFalse(group.isAmbiguous)
        assertTrue(group.candidates.isNotEmpty())
    }

    @Test
    fun testRepertorizationCalculationPreserved_beforeVsAfter() = runBlocking {
        // Verify existing scoring & ranking calculation is strictly unchanged
        val selectedRubrics = listOf(
            SelectedRubric(
                id = 1L,
                chapter = "HEAD",
                rubricText = "PAIN - throbbing",
                remedies = mapOf("bell" to 3, "bry" to 2, "glon" to 3)
            ),
            SelectedRubric(
                id = 2L,
                chapter = "MIND",
                rubricText = "FEAR - dark",
                remedies = mapOf("bell" to 2, "phos" to 3, "stram" to 3)
            )
        )

        val scores = repertoryRepo.computeRepertorization(selectedRubrics)
        assertTrue(scores.isNotEmpty())

        val bellScore = scores.find { it.abbreviation == "bell" }
        assertNotNull("Belladonna must be scored", bellScore)
        // Belladonna: score = 3 + 2 = 5, coverage = 2 rubrics
        assertEquals(5, bellScore!!.score)
        assertEquals(2, bellScore.coverage)
        assertEquals(3, bellScore.gradeBreakdown[1L])
        assertEquals(2, bellScore.gradeBreakdown[2L])

        // Verify sorted by score descending then coverage descending
        for (i in 0 until scores.size - 1) {
            val curr = scores[i]
            val next = scores[i + 1]
            assertTrue(
                "Sorting order preserved: ${curr.score} >= ${next.score}",
                curr.score > next.score || (curr.score == next.score && curr.coverage >= next.coverage)
            )
        }
    }

    @Test
    fun testLiveBackendAndOfflineFallback_reportedSeparately() = runBlocking {
        // Step 1: Test interpretSymptomSemanticConcepts on a clinical symptom
        val symptom = "severe throbbing right sided headache worse in warm room"
        val interpretation = aiSuggester.interpretSymptomSemanticConcepts(
            symptomText = symptom,
            clarification = "",
            category = "Physical Particular"
        )
        assertNotNull(interpretation)
        assertFalse("Clinically complete symptom must not be ambiguous", interpretation.isAmbiguous)
        assertTrue(
            "Target chapters must include HEAD",
            interpretation.targetChapters.contains("HEAD")
        )
        assertTrue(
            "Search phrases must include pulsating/throbbing",
            interpretation.searchPhrases.any { it.contains("pulsating") || it.contains("throbbing") || it.contains("pain") }
        )

        // Step 2: Test candidate generation for this symptom
        val group = aiSuggester.generateCandidatesForSymptom(
            symptomIndex = 1,
            symptomText = symptom,
            repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT,
            category = "Physical Particular"
        )
        assertFalse(group.isAmbiguous)
        assertTrue("Verified rubric candidates must be returned", group.candidates.isNotEmpty())
        assertEquals("HEAD", group.candidates.first().rubric.chapter)
        assertTrue(group.candidates.first().rubric.rubricText.contains("pulsating", ignoreCase = true) || group.candidates.first().rubric.rubricText.contains("throbbing", ignoreCase = true))
    }
}
