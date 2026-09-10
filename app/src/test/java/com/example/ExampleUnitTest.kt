package com.example

import com.example.data.CaseEntity
import com.example.data.ClinicalFormData
import com.example.data.FamilyHistoryRow
import com.example.data.LsmcRow
import com.example.data.PastHistoryRow
import com.example.data.SymptomAnalysisItem
import com.example.data.SymptomEvaluationItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleUnitTest {

    @Test
    fun testClinicalFormDataRoundTrip() {
        val form = ClinicalFormData(
            srNo = "101",
            opdCaseNo = "OPD-2026/045",
            name = "John Doe",
            date = "2026-09-10",
            age = "34",
            sex = "Male",
            qualification = "B.Tech",
            religion = "Universal",
            occupation = "Engineer",
            address = "123 Clinic Road",
            maritalStatus = "Married",
            acuteOdp = "Sudden onset after cold exposure",
            lsmcRows = listOf(
                LsmcRow(location = "Throat right", sensation = "Stitching", modality = "< Cold drinks", concomitant = "Chilly sweat")
            ),
            pastHistoryRows = listOf(
                PastHistoryRow(no = "1", disease = "Typhoid", duration = "3 weeks", treatment = "Allopathic")
            ),
            familyHistoryRows = listOf(
                FamilyHistoryRow(no = "1", relationship = "Father", healthIllness = "Hypertension", aliveDead = "Alive")
            ),
            symptomAnalysisList = listOf(
                SymptomAnalysisItem(symptomText = "Thirst for cold water", type = "Physical General", frequency = "Common")
            ),
            symptomEvaluationList = listOf(
                SymptomEvaluationItem(symptomText = "Thirst for cold water", grade = "+++", type = "Physical General", frequency = "Common")
            ),
            menstrualApplicable = false,
            obstetricApplicable = false,
            predominantMiasm = "Psoric",
            prescribedRemedy = "ACON",
            prescribedRemedyFullName = "Aconitum Napellus"
        )

        val caseEntity = form.toCaseEntity(CaseEntity(caseType = "ACUTE"))
        val restored = ClinicalFormData.fromCaseEntity(caseEntity)

        assertEquals("101", restored.srNo)
        assertEquals("OPD-2026/045", restored.opdCaseNo)
        assertEquals("John Doe", restored.name)
        assertEquals(1, restored.lsmcRows.size)
        assertEquals("Throat right", restored.lsmcRows[0].location)
        assertEquals("Stitching", restored.lsmcRows[0].sensation)
        assertEquals(1, restored.pastHistoryRows.size)
        assertEquals("Typhoid", restored.pastHistoryRows[0].disease)
        assertEquals(1, restored.familyHistoryRows.size)
        assertEquals("Father", restored.familyHistoryRows[0].relationship)
        assertEquals(1, restored.symptomAnalysisList.size)
        assertEquals(1, restored.symptomEvaluationList.size)
        assertEquals("+++", restored.symptomEvaluationList[0].grade)
        assertFalse(restored.menstrualApplicable)
        assertFalse(restored.obstetricApplicable)
        assertEquals("ACON", restored.prescribedRemedy)
        assertEquals("Aconitum Napellus", restored.prescribedRemedyFullName)
    }
}
