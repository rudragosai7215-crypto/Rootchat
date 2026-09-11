package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class RootChartComprehensiveQATest {

    private lateinit var context: Context
    private lateinit var db: AppDatabase
    private lateinit var caseRepo: CaseRepository
    private lateinit var repertoryRepo: RepertoryRepository
    private lateinit var userProfileDao: UserProfileDao
    private lateinit var caseDao: CaseDao
    private lateinit var rubricDao: RubricDao
    private lateinit var remedyDao: RemedyDao

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        db = AppDatabase.getDatabase(context)
        userProfileDao = db.userProfileDao()
        caseDao = db.caseDao()
        rubricDao = db.rubricDao()
        remedyDao = db.remedyDao()
        caseRepo = CaseRepository(caseDao)
        repertoryRepo = RepertoryRepository(rubricDao, remedyDao)

        // Clean cases for test isolation
        caseDao.deleteAllCases()
    }

    // ==========================================
    // PHASE 1 — SMOKE TEST
    // ==========================================
    @Test
    fun testST001_ApplicationDatabaseLaunchesWithoutCrash() = runBlocking {
        assertNotNull("Database must be non-null", db)
        val rubricCount = rubricDao.getRubricCountSync()
        val remedyCount = remedyDao.getRemedyCountSync()
        assertTrue("Rubric count must be > 70000 (actual: $rubricCount)", rubricCount >= 70000)
        assertTrue("Remedy count must be > 2000 (actual: $remedyCount)", remedyCount >= 2000)
        println("QA Smoke Test: Verified $rubricCount Kent rubrics and $remedyCount remedies in SQLite.")
    }

    // ==========================================
    // PHASE 2 — LOGIN & PROFILE
    // ==========================================
    @Test
    fun testPhase2_LoginAndProfileManagement() = runBlocking {
        // 1. Doctor Profile Creation
        val docProfile = UserProfile(clinicianName = "Dr. Rudra Test", role = "Doctor")
        userProfileDao.insertProfile(docProfile)
        var current = userProfileDao.getUserProfileSync()
        assertNotNull(current)
        assertEquals("Dr. Rudra Test", current?.clinicianName)
        assertEquals("Doctor", current?.role)

        // 2. Student Profile Creation
        val studentProfile = UserProfile(clinicianName = "Student Aryan", role = "Student")
        userProfileDao.insertProfile(studentProfile)
        current = userProfileDao.getUserProfileSync()
        assertEquals("Student Aryan", current?.clinicianName)
        assertEquals("Student", current?.role)

        // 3. Special Characters in Name
        val specialProfile = UserProfile(clinicianName = "Dr. M. S. O'Connor-Smith, M.D. (Hom.)", role = "Doctor")
        userProfileDao.insertProfile(specialProfile)
        current = userProfileDao.getUserProfileSync()
        assertEquals("Dr. M. S. O'Connor-Smith, M.D. (Hom.)", current?.clinicianName)

        // 4. Long Name Handling
        val longName = "Dr. " + "A".repeat(150)
        val longProfile = UserProfile(clinicianName = longName, role = "Doctor")
        userProfileDao.insertProfile(longProfile)
        current = userProfileDao.getUserProfileSync()
        assertEquals(longName, current?.clinicianName)

        // 5. Dark Mode Toggle Persistence
        userProfileDao.updateDarkMode(true)
        current = userProfileDao.getUserProfileSync()
        assertTrue(current?.isDarkMode == true)
        userProfileDao.updateDarkMode(false)
        current = userProfileDao.getUserProfileSync()
        assertFalse(current?.isDarkMode == true)
    }

    // ==========================================
    // PHASE 3 — DASHBOARD & CASE STATISTICS
    // ==========================================
    @Test
    fun testPhase3_DashboardCaseStatistics() = runBlocking {
        // Create 3 Acute cases
        for (i in 1..3) {
            caseRepo.saveCase(
                CaseEntity(
                    id = "acute_$i",
                    caseType = "ACUTE",
                    patientName = "Synthetic Acute Patient $i",
                    chiefComplaint = "Acute complaint $i",
                    isCompleted = true
                )
            )
        }

        // Create 2 Chronic cases
        for (i in 1..2) {
            caseRepo.saveCase(
                CaseEntity(
                    id = "chronic_$i",
                    caseType = "CHRONIC",
                    patientName = "Synthetic Chronic Patient $i",
                    chiefComplaint = "Chronic complaint $i",
                    isCompleted = true
                )
            )
        }

        val total = caseRepo.totalCount.first()
        val acute = caseRepo.acuteCount.first()
        val chronic = caseRepo.chronicCount.first()

        assertEquals("Total cases must be 5", 5, total)
        assertEquals("Acute cases must be 3", 3, acute)
        assertEquals("Chronic cases must be 2", 2, chronic)
    }

    // ==========================================
    // PHASE 4 — ACUTE CASE WORKFLOW & DATA ROUNDTRIP
    // ==========================================
    @Test
    fun testPhase4_AcuteCaseAllSectionsDataLossPrevention() = runBlocking {
        val acuteForm = ClinicalFormData(
            srNo = "AC-001",
            opdCaseNo = "OPD-2026-99",
            name = "Synthetic Patient Acute",
            date = "10/09/2026",
            age = "28",
            sex = "Male",
            occupation = "Architect",
            address = "74 Elm St",
            maritalStatus = "Single",
            lsmcRows = listOf(
                LsmcRow(location = "Head, forehead", sensation = "Throbbing bursting", modality = "< Motion, < Light", concomitant = "Intense thirst for cold water")
            ),
            acuteOdp = "Started 6 hours ago after exposure to cold dry wind",
            associatedComplaints = "Chills running down the spine",
            appetite = "Reduced",
            thirst = "Thirsty for large quantities of cold water",
            bp = "120/80 mmHg",
            pulse = "110 bpm",
            temperature = "102.4 F",
            rr = "22/min",
            tongue = "Dry, red tip",
            respiratorySystem = "Bilateral vesicular breath sounds clear",
            cardiovascularSystem = "S1 S2 normal, tachycardia present",
            investigationBlood = "WBC 11,200",
            provisionalDiagnosis = "Acute pyrexia / viral fever",
            clinicianObservation = "Patient tossed around bed, anxious look",
            symptomAnalysisList = listOf(
                SymptomAnalysisItem(symptomText = "Sudden fever after dry cold wind", type = "Cause / Etiology", frequency = "Rare")
            ),
            symptomEvaluationList = listOf(
                SymptomEvaluationItem(symptomText = "Sudden fever after dry cold wind", grade = "+++", type = "Cause", frequency = "Keynote")
            ),
            prescribedRemedy = "ACON",
            prescribedRemedyFullName = "Aconitum Napellus",
            potency = "200C",
            dose = "4 pills",
            repetition = "Every 3 hours",
            instructions = "Review in 24 hours if fever persists",
            managementAdvices = "Light warm fluids, boiled water"
        )

        val caseEntity = acuteForm.toCaseEntity(CaseEntity(id = "acute_cuj_1", caseType = "ACUTE"))
        caseRepo.saveCase(caseEntity)

        val retrieved = caseRepo.getCaseByIdSync("acute_cuj_1")
        assertNotNull("Saved acute case must be retrievable", retrieved)
        val restoredForm = ClinicalFormData.fromCaseEntity(retrieved!!)

        assertEquals(acuteForm.srNo, restoredForm.srNo)
        assertEquals(acuteForm.opdCaseNo, restoredForm.opdCaseNo)
        assertEquals(acuteForm.name, restoredForm.name)
        assertEquals(acuteForm.acuteOdp, restoredForm.acuteOdp)
        assertEquals(1, restoredForm.lsmcRows.size)
        assertEquals("Head, forehead", restoredForm.lsmcRows[0].location)
        assertEquals("Throbbing bursting", restoredForm.lsmcRows[0].sensation)
        assertEquals(acuteForm.temperature, restoredForm.temperature)
        assertEquals(acuteForm.provisionalDiagnosis, restoredForm.provisionalDiagnosis)
        assertEquals(acuteForm.prescribedRemedy, restoredForm.prescribedRemedy)
        assertEquals(acuteForm.prescribedRemedyFullName, restoredForm.prescribedRemedyFullName)
        assertEquals(acuteForm.potency, restoredForm.potency)
    }

    // ==========================================
    // PHASE 5 — CHRONIC CASE & MIASMATIC EVALUATION
    // ==========================================
    @Test
    fun testPhase5_ChronicCaseMiasmAndHistoryTables() = runBlocking {
        val miasms = listOf("Psoric", "Sycotic", "Syphilitic", "Tubercular", "Mixed")
        
        for (miasm in miasms) {
            val chronicForm = ClinicalFormData(
                name = "Chronic Patient $miasm",
                age = "42",
                sex = "Female",
                chronicChiefComplaint = "Chronic migratory joint pains",
                onset = "5 years ago",
                pastHistoryRows = listOf(
                    PastHistoryRow(no = "1", disease = "Recurrent tonsillitis in childhood", duration = "3 yrs", treatment = "Homeopathy"),
                    PastHistoryRow(no = "2", disease = "Jaundice", duration = "2 weeks", treatment = "Conservative")
                ),
                familyHistoryRows = listOf(
                    FamilyHistoryRow(no = "1", relationship = "Mother", healthIllness = "Rheumatoid Arthritis", aliveDead = "Alive"),
                    FamilyHistoryRow(no = "2", relationship = "Paternal Grandfather", healthIllness = "Asthma", aliveDead = "Deceased")
                ),
                provisionalDiagnosis = "Seronegative Spondyloarthropathy",
                finalDiagnosis = "Rheumatoid Arthritis with Synovitis",
                predominantMiasm = miasm,
                miasmaticNotes = "Strong syco-psoric diathesis with stiffness < morning",
                prescribedRemedy = "RHUS-T",
                prescribedRemedyFullName = "Rhus Toxicodendron"
            )

            val caseEntity = chronicForm.toCaseEntity(CaseEntity(id = "chronic_$miasm", caseType = "CHRONIC"))
            caseRepo.saveCase(caseEntity)

            val fetched = caseRepo.getCaseByIdSync("chronic_$miasm")
            assertNotNull(fetched)
            val restored = ClinicalFormData.fromCaseEntity(fetched!!)

            assertEquals("Provisional and Final diagnosis must remain separate", "Seronegative Spondyloarthropathy", restored.provisionalDiagnosis)
            assertEquals("Final Diagnosis must remain separate", "Rheumatoid Arthritis with Synovitis", restored.finalDiagnosis)
            assertEquals("Miasm must be saved accurately", miasm, restored.predominantMiasm)
            assertEquals(2, restored.pastHistoryRows.size)
            assertEquals(2, restored.familyHistoryRows.size)
            assertEquals("Mother", restored.familyHistoryRows[0].relationship)
            assertEquals("Rheumatoid Arthritis", restored.familyHistoryRows[0].healthIllness)
        }
    }

    // ==========================================
    // PHASE 6 — REPERTORY SEARCH (REP-001 to REP-007)
    // ==========================================
    @Test
    fun testPhase6_RepertorySearchSuite() = runBlocking {
        // REP-001: Search known rubric
        val feverResults = repertoryRepo.searchRubrics("fever")
        assertTrue("Search for 'fever' should return results", feverResults.isNotEmpty())
        assertTrue("Results should contain 'fever' in text or chapter",
            feverResults.any { it.rubricText.contains("fever", ignoreCase = true) || it.chapter.contains("fever", ignoreCase = true) }
        )

        // REP-002: Search partial rubric
        val headThrob = repertoryRepo.searchRubrics("head throb")
        assertTrue("Search for multi-term 'head throb' should return matching rubrics", headThrob.isNotEmpty())

        // REP-003: Search with different capitalization
        val lowerResults = repertoryRepo.searchRubrics("anxiety")
        val upperResults = repertoryRepo.searchRubrics("ANXIETY")
        val mixedResults = repertoryRepo.searchRubrics("AnXiEtY")
        assertEquals("Search must be case-insensitive", lowerResults.size, upperResults.size)
        assertEquals("Search must be case-insensitive", lowerResults.size, mixedResults.size)

        // REP-004: Search invalid term
        val invalidResults = repertoryRepo.searchRubrics("xyznonexistentrubric123987")
        assertTrue("Invalid term must return empty list without error", invalidResults.isEmpty())

        // REP-005: Clear / Empty search
        val emptyResults = repertoryRepo.searchRubrics("   ")
        assertTrue("Empty or whitespace search must return empty list", emptyResults.isEmpty())

        // REP-006: Search repeatedly without memory leak or crash
        for (term in listOf("pain", "cough", "chill", "fear", "stomach", "sleep")) {
            val res = repertoryRepo.searchRubrics(term)
            assertTrue("Repeated search for '$term' should succeed", res.isNotEmpty())
        }

        // REP-007: Safe handling of long search query
        val longQuery = "a".repeat(300)
        val longRes = repertoryRepo.searchRubrics(longQuery)
        assertTrue("Long search query must not crash", longRes.isEmpty())
    }

    // ==========================================
    // PHASE 7 — BROWSE CHAPTERS
    // ==========================================
    @Test
    fun testPhase7_BrowseChaptersKentRepertory() = runBlocking {
        val chapters = repertoryRepo.getAllChapters()
        assertTrue("Kent repertory should contain all major chapters (found ${chapters.size})", chapters.size >= 35)
        assertTrue("Should contain MIND chapter", chapters.any { it.equals("MIND", ignoreCase = true) })
        assertTrue("Should contain HEAD chapter", chapters.any { it.equals("HEAD", ignoreCase = true) })
        assertTrue("Should contain GENERALITIES chapter", chapters.any { it.contains("GENERAL", ignoreCase = true) })

        // Check rubrics within MIND chapter
        val mindRubrics = repertoryRepo.getRubricsByChapter("MIND", limit = 20, offset = 0)
        assertTrue("MIND chapter must have rubrics", mindRubrics.isNotEmpty())
        assertEquals(20, mindRubrics.size)
        assertTrue(mindRubrics.all { it.chapter.equals("MIND", ignoreCase = true) })
    }

    // ==========================================
    // PHASE 8 & 9 — TOTALITY & REPERTORIZATION ENGINE (CALCULATION AUDIT)
    // ==========================================
    @Test
    fun testPhase8And9_TotalityAndRepertorizationMathematicalAudit() = runBlocking {
        // Find 3 realistic rubrics from SQLite database
        val anxietyRubric = repertoryRepo.searchRubrics("anxiety").first()
        val restlessnessRubric = repertoryRepo.searchRubrics("restlessness").first()
        val thirstRubric = repertoryRepo.searchRubrics("thirst").first()

        val selectedRubrics = listOf(
            SelectedRubric(
                id = anxietyRubric.id,
                chapter = anxietyRubric.chapter,
                rubricText = anxietyRubric.rubricText,
                remedies = RepertoryRepository.parseRubricRemedies(anxietyRubric.remediesJson)
            ),
            SelectedRubric(
                id = restlessnessRubric.id,
                chapter = restlessnessRubric.chapter,
                rubricText = restlessnessRubric.rubricText,
                remedies = RepertoryRepository.parseRubricRemedies(restlessnessRubric.remediesJson)
            ),
            SelectedRubric(
                id = thirstRubric.id,
                chapter = thirstRubric.chapter,
                rubricText = thirstRubric.rubricText,
                remedies = RepertoryRepository.parseRubricRemedies(thirstRubric.remediesJson)
            )
        )

        // 1. Serialization & Deserialization test
        val serialized = RepertoryRepository.serializeSelectedRubrics(selectedRubrics)
        val deserialized = RepertoryRepository.parseSelectedRubrics(serialized)
        assertEquals(3, deserialized.size)
        assertEquals(anxietyRubric.id, deserialized[0].id)
        assertEquals(restlessnessRubric.id, deserialized[1].id)
        assertEquals(thirstRubric.id, deserialized[2].id)

        // 2. Perform Repertorization
        val results = repertoryRepo.computeRepertorization(selectedRubrics)
        assertTrue("Repertorization must produce candidates", results.isNotEmpty())

        // 3. INDEPENDENT MATHEMATICAL AUDIT
        // Calculate expected score, coverage, and ranking for each candidate manually
        val manualScores = mutableMapOf<String, Int>()
        val manualCoverage = mutableMapOf<String, Int>()

        for (rubric in selectedRubrics) {
            for ((abbr, grade) in rubric.remedies) {
                val key = abbr.lowercase().trim()
                if (grade > 0) {
                    manualScores[key] = (manualScores[key] ?: 0) + grade
                    manualCoverage[key] = (manualCoverage[key] ?: 0) + 1
                }
            }
        }

        // Verify top 10 remedies against manual audit
        for (item in results.take(10)) {
            val expectedScore = manualScores[item.abbreviation.lowercase()]
            val expectedCov = manualCoverage[item.abbreviation.lowercase()]

            assertNotNull("Remedy ${item.abbreviation} must exist in manual calculation", expectedScore)
            assertEquals("Score for ${item.abbreviation} must match manual audit", expectedScore, item.score)
            assertEquals("Coverage for ${item.abbreviation} must match manual audit", expectedCov, item.coverage)
        }

        // Verify sorting invariants:
        // Item[i].score >= Item[i+1].score (or if score equal, coverage[i] >= coverage[i+1])
        for (i in 0 until results.size - 1) {
            val curr = results[i]
            val next = results[i + 1]
            assertTrue(
                "Ranking invariant failed: ${curr.score} < ${next.score}",
                curr.score > next.score || (curr.score == next.score && curr.coverage >= next.coverage)
            )
        }
    }

    // ==========================================
    // PHASE 10 & 11 — WHY THIS REMEDY & DIFFERENTIATE VERIFICATION
    // ==========================================
    @Test
    fun testPhase10And11_WhyThisRemedyAndDifferentiationLogic() = runBlocking {
        val rubric1 = SelectedRubric(
            id = 101L,
            chapter = "MIND",
            rubricText = "Anxiety anticipating",
            remedies = mapOf("acon" to 3, "ars" to 3, "puls" to 2, "nux-v" to 1)
        )
        val rubric2 = SelectedRubric(
            id = 102L,
            chapter = "GENERALITIES",
            rubricText = "Cold, exposure to",
            remedies = mapOf("acon" to 3, "ars" to 2, "puls" to 1, "nux-v" to 3)
        )
        val rubric3 = SelectedRubric(
            id = 103L,
            chapter = "STOMACH",
            rubricText = "Thirst extreme",
            remedies = mapOf("acon" to 2, "ars" to 3, "puls" to 1, "nux-v" to 1)
        )

        val selected = listOf(rubric1, rubric2, rubric3)
        val results = repertoryRepo.computeRepertorization(selected)

        // Check Aconitum and Arsenicum
        val acon = results.find { it.abbreviation.equals("acon", ignoreCase = true) }
        val ars = results.find { it.abbreviation.equals("ars", ignoreCase = true) }

        assertNotNull(acon)
        assertNotNull(ars)

        // Acon: 3 + 3 + 2 = 8, coverage = 3
        assertEquals(8, acon!!.score)
        assertEquals(3, acon.coverage)
        assertEquals(3, acon.gradeBreakdown[101L])
        assertEquals(3, acon.gradeBreakdown[102L])
        assertEquals(2, acon.gradeBreakdown[103L])

        // Ars: 3 + 2 + 3 = 8, coverage = 3 (TIE SCORING!)
        assertEquals(8, ars!!.score)
        assertEquals(3, ars.coverage)
        assertEquals(3, ars.gradeBreakdown[101L])
        assertEquals(2, ars.gradeBreakdown[102L])
        assertEquals(3, ars.gradeBreakdown[103L])

        // Tie verification
        assertEquals("Both remedies must be tied at 8 points", acon.score, ars.score)

        // Differentiation comparison
        // Rubric 101: Acon 3 vs Ars 3 (Equal)
        // Rubric 102: Acon 3 vs Ars 2 (Acon leads by +1)
        // Rubric 103: Acon 2 vs Ars 3 (Ars leads by +1)
        val gradeDiff101 = (acon.gradeBreakdown[101L] ?: 0) - (ars.gradeBreakdown[101L] ?: 0)
        val gradeDiff102 = (acon.gradeBreakdown[102L] ?: 0) - (ars.gradeBreakdown[102L] ?: 0)
        val gradeDiff103 = (acon.gradeBreakdown[103L] ?: 0) - (ars.gradeBreakdown[103L] ?: 0)

        assertEquals(0, gradeDiff101)
        assertEquals(1, gradeDiff102)
        assertEquals(-1, gradeDiff103)
    }

    // ==========================================
    // PHASE 13 — CASE LIBRARY CRUD
    // ==========================================
    @Test
    fun testPhase13_CaseLibraryCRUDOperations() = runBlocking {
        val caseId = UUID.randomUUID().toString()
        val testCase = CaseEntity(
            id = caseId,
            caseType = "ACUTE",
            patientName = "Synthetic Patient Library",
            patientAge = "45",
            patientGender = "Female",
            clinicalDiagnosis = "Acute Bronchitis",
            prescribedRemedy = "BRY",
            prescribedRemedyFullName = "Bryonia Alba",
            isCompleted = true
        )

        // Insert
        caseRepo.saveCase(testCase)
        var fetched = caseRepo.getCaseByIdSync(caseId)
        assertNotNull(fetched)
        assertEquals("Synthetic Patient Library", fetched?.patientName)
        assertEquals("Bryonia Alba", fetched?.prescribedRemedyFullName)

        // Update
        val updated = fetched!!.copy(
            clinicalDiagnosis = "Acute Bronchitis with Pleurisy",
            potency = "1M"
        )
        caseRepo.saveCase(updated)
        fetched = caseRepo.getCaseByIdSync(caseId)
        assertEquals("Acute Bronchitis with Pleurisy", fetched?.clinicalDiagnosis)
        assertEquals("1M", fetched?.potency)

        // Delete
        caseRepo.deleteCase(caseId)
        fetched = caseRepo.getCaseByIdSync(caseId)
        assertNull("Deleted case must no longer exist", fetched)
    }

    // ==========================================
    // PHASE 15 — PDF EXPORT GENERATION
    // ==========================================
    @Test
    fun testPhase15_PdfExportGenerationIntegrity() = runBlocking {
        val testCase = CaseEntity(
            id = "pdf_test_case_1",
            caseType = "ACUTE",
            patientName = "Synthetic PDF Test Patient",
            patientAge = "33",
            patientGender = "Male",
            patientContact = "555-0199",
            chiefComplaint = "Severe throbbing headache after sunstroke",
            hpi = "Onset after 2 hours direct sun exposure",
            etiology = "Sun exposure, heat",
            lsmcLocation = "Forehead and temples",
            lsmcSensation = "Throbbing, hammering",
            lsmcModality = "< Motion, < Stooping, > Pressure",
            lsmcConcomitant = "Face flushed red, pupils dilated",
            mentalSymptoms = "Extreme irritability, delirious when drowsy",
            physicalGenerals = "Hot head with cold extremities",
            thermalState = "Hot",
            thirstState = "Great thirst for cold water",
            clinicalDiagnosis = "Congestive Cephalea / Heat Exhaustion",
            prescribedRemedy = "GLON",
            prescribedRemedyFullName = "Glonoinum",
            potency = "30C",
            dose = "4 globules",
            repetition = "Every 1 hour",
            instructions = "Rest in dark cool room, cold compress to forehead",
            isCompleted = true
        )

        val rubrics = listOf(
            SelectedRubric(
                id = 201L,
                chapter = "HEAD",
                rubricText = "PAIN, sun, from exposure to",
                remedies = mapOf("glon" to 3, "bell" to 3, "nat-c" to 3)
            ),
            SelectedRubric(
                id = 202L,
                chapter = "HEAD",
                rubricText = "PAIN, pulsating, throbbing",
                remedies = mapOf("glon" to 3, "bell" to 3)
            )
        )

        val scores = listOf(
            RepertorizationScore("glon", "Glonoinum", score = 6, coverage = 2, gradeBreakdown = mapOf(201L to 3, 202L to 3)),
            RepertorizationScore("bell", "Belladonna", score = 6, coverage = 2, gradeBreakdown = mapOf(201L to 3, 202L to 3)),
            RepertorizationScore("nat-c", "Natrum Carbonicum", score = 3, coverage = 1, gradeBreakdown = mapOf(201L to 3))
        )

        val file: File? = PdfExporter.exportCaseToPdf(context, testCase, rubrics, scores)
        assertNotNull("Generated PDF file must not be null", file)
        assertTrue("PDF file must exist on disk", file!!.exists())
        assertTrue("PDF file size must be > 0 bytes (actual: ${file.length()})", file.length() > 0)
        println("QA PDF Test: Successfully generated PDF report at ${file.absolutePath}, size=${file.length()} bytes")
    }

    // ==========================================
    // PHASE 17 — OFFLINE INTEGRITY
    // ==========================================
    @Test
    fun testPhase17_OfflineZeroNetworkIntegrity() = runBlocking {
        // Query local DB directly and verify zero remote dependencies
        val count = db.rubricDao().getRubricCountSync()
        val allChapters = db.rubricDao().getAllChapters()
        val remedies = db.remedyDao().searchRemedies("bell", limit = 10)

        assertTrue("Offline rubric count valid", count > 70000)
        assertTrue("Offline chapters present", allChapters.isNotEmpty())
        assertTrue("Offline remedy lookup present", remedies.isNotEmpty())
    }

    // ==========================================
    // PHASE 24 — STRESS TEST (50+ CASES CRUD)
    // ==========================================
    @Test
    fun testPhase24_StressTesting50Cases() = runBlocking {
        val startTime = System.currentTimeMillis()
        val count = 50

        for (i in 1..count) {
            val type = if (i % 2 == 0) "ACUTE" else "CHRONIC"
            caseRepo.saveCase(
                CaseEntity(
                    id = "stress_case_$i",
                    caseType = type,
                    patientName = "Synthetic Stress Subject #$i",
                    patientAge = "${20 + (i % 50)}",
                    patientGender = if (i % 2 == 0) "Male" else "Female",
                    chiefComplaint = "Complaint pattern #$i with systemic implications",
                    clinicalDiagnosis = "Stress Test Diagnosis #$i",
                    prescribedRemedy = if (i % 2 == 0) "SULPH" else "CALC",
                    prescribedRemedyFullName = if (i % 2 == 0) "Sulphur" else "Calcarea Carbonica",
                    isCompleted = true
                )
            )
        }

        val totalStored = caseRepo.totalCount.first()
        val acuteStored = caseRepo.acuteCount.first()
        val chronicStored = caseRepo.chronicCount.first()

        assertEquals(50, totalStored)
        assertEquals(25, acuteStored)
        assertEquals(25, chronicStored)

        val allCases = caseRepo.allCases.first()
        assertEquals(50, allCases.size)

        val duration = System.currentTimeMillis() - startTime
        println("QA Stress Test: Inserted, indexed, and retrieved 50 complete cases in ${duration}ms without errors.")
        assertTrue("50 cases should complete in under 5000ms", duration < 5000)
    }

    // ==========================================
    // PHASE 25 — DATA ISOLATION / PRIVACY
    // ==========================================
    @Test
    fun testPhase25_DataIsolationBetweenPatients() = runBlocking {
        val caseA = CaseEntity(
            id = "case_patient_A",
            caseType = "ACUTE",
            patientName = "Alice Wonderland",
            chiefComplaint = "Acute allergic urticaria",
            prescribedRemedy = "APIS",
            prescribedRemedyFullName = "Apis Mellifica",
            clinicalDiagnosis = "Urticaria"
        )
        val caseB = CaseEntity(
            id = "case_patient_B",
            caseType = "CHRONIC",
            patientName = "Bob Builder",
            chiefComplaint = "Chronic lumbar disc disease",
            prescribedRemedy = "KALI-C",
            prescribedRemedyFullName = "Kali Carbonicum",
            clinicalDiagnosis = "Lumbar Radiculopathy"
        )

        caseRepo.saveCase(caseA)
        caseRepo.saveCase(caseB)

        val fetchedA = caseRepo.getCaseByIdSync("case_patient_A")
        val fetchedB = caseRepo.getCaseByIdSync("case_patient_B")

        assertNotNull(fetchedA)
        assertNotNull(fetchedB)

        // Ensure zero cross-contamination
        assertEquals("Alice Wonderland", fetchedA?.patientName)
        assertEquals("Acute allergic urticaria", fetchedA?.chiefComplaint)
        assertEquals("APIS", fetchedA?.prescribedRemedy)

        assertEquals("Bob Builder", fetchedB?.patientName)
        assertEquals("Chronic lumbar disc disease", fetchedB?.chiefComplaint)
        assertEquals("KALI-C", fetchedB?.prescribedRemedy)
        assertNotEquals(fetchedA?.patientName, fetchedB?.patientName)
        assertNotEquals(fetchedA?.chiefComplaint, fetchedB?.chiefComplaint)
    }

    // ==========================================
    // PHASE 27 — AUTOMATED ACUTE CUJ TEST CASE
    // ==========================================
    @Test
    fun testPhase27_AutomatedAcuteCUJTestCase() = runBlocking {
        val acuteCase = CaseEntity(
            id = "CUJ_ACUTE_001",
            caseType = "ACUTE",
            patientName = "Test Acute 001",
            patientAge = "25",
            patientGender = "Male",
            chiefComplaint = "Acute fever. Headache, Body ache, Chills, Thirst, Restlessness",
            hpi = "Acute onset after exposure to wind",
            mentalSymptoms = "Anxiety, fear of death during fever",
            physicalGenerals = "Intense burning heat, unquenchable thirst for cold water",
            thermalState = "Hot",
            thirstState = "Excessive, large quantities",
            clinicalDiagnosis = "Acute pyrexia",
            prescribedRemedy = "ACON",
            prescribedRemedyFullName = "Aconitum Napellus",
            potency = "200C",
            dose = "4 pills",
            repetition = "Every 2 hours",
            instructions = "Rest, plenty of water",
            isCompleted = true
        )

        // Save
        caseRepo.saveCase(acuteCase)

        // Reopen
        val reloaded = caseRepo.getCaseByIdSync("CUJ_ACUTE_001")
        assertNotNull(reloaded)
        assertEquals("Test Acute 001", reloaded?.patientName)
        assertEquals("25", reloaded?.patientAge)
        assertEquals("Male", reloaded?.patientGender)
        assertTrue(reloaded?.chiefComplaint?.contains("Acute fever") == true)
        assertEquals("Aconitum Napellus", reloaded?.prescribedRemedyFullName)

        // Export PDF
        val pdf = PdfExporter.exportCaseToPdf(context, reloaded!!, emptyList(), emptyList())
        assertNotNull(pdf)
        assertTrue(pdf!!.exists())
        assertTrue(pdf.length() > 0)
    }

    // ==========================================
    // PHASE 28 — AUTOMATED CHRONIC CUJ TEST CASE
    // ==========================================
    @Test
    fun testPhase28_AutomatedChronicCUJTestCase() = runBlocking {
        val chronicCase = CaseEntity(
            id = "CUJ_CHRONIC_001",
            caseType = "CHRONIC",
            patientName = "Test Chronic 001",
            patientAge = "30",
            patientGender = "Female",
            chiefComplaint = "Chronic eczema with severe itching",
            hpi = "Eczema present for 4 years, worse in winter",
            personalHistory = "Appetite good, desires sweets, aversions to fat",
            pastHistory = "Childhood asthma suppressed with bronchodilators",
            familyHistory = "Father - Diabetes Mellitus; Mother - Hypertension",
            vitals = "BP 118/76, Pulse 74",
            clinicalDiagnosis = "Chronic Allergic Eczema (Nummular)",
            miasmaticEvaluation = "Psoric",
            miasmaticNotes = "Dominant psoric itch < night in warmth of bed",
            mentalSymptoms = "Selfish, philosophic, aversion to bathing",
            physicalGenerals = "Thermal: Hot, standing aggravates all symptoms",
            prescribedRemedy = "SULPH",
            prescribedRemedyFullName = "Sulphur",
            potency = "200C",
            dose = "1 dose morning empty stomach",
            repetition = "Single dose, wait and watch for 4 weeks",
            instructions = "No medicinal ointments or strong perfumes",
            followUpDate = "10 Oct 2026",
            followUpNotes = "Observe initial aggravation and skin discharge",
            isCompleted = true
        )

        // Save
        caseRepo.saveCase(chronicCase)

        // Reopen
        val reloaded = caseRepo.getCaseByIdSync("CUJ_CHRONIC_001")
        assertNotNull(reloaded)
        assertEquals("Test Chronic 001", reloaded?.patientName)
        assertEquals("30", reloaded?.patientAge)
        assertEquals("Female", reloaded?.patientGender)
        assertEquals("Chronic Allergic Eczema (Nummular)", reloaded?.clinicalDiagnosis)
        assertEquals("Psoric", reloaded?.miasmaticEvaluation)
        assertEquals("Sulphur", reloaded?.prescribedRemedyFullName)

        // Export PDF
        val pdf = PdfExporter.exportCaseToPdf(context, reloaded!!, emptyList(), emptyList())
        assertNotNull(pdf)
        assertTrue(pdf!!.exists())
        assertTrue(pdf.length() > 0)
    }
}
