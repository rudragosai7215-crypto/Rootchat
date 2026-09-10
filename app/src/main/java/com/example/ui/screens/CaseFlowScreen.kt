package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseEntity
import com.example.data.ClinicalFormData
import com.example.data.RepertorizationScore
import com.example.data.RepertoryRepository
import com.example.data.SelectedRubric
import com.example.ui.screens.caseflow.AcuteLsmcStep
import com.example.ui.screens.caseflow.AnalysisOfSymptomsStep
import com.example.ui.screens.caseflow.AssociatedComplaintsStep
import com.example.ui.screens.caseflow.CaseIdentificationStep
import com.example.ui.screens.caseflow.ChronicChiefComplaintStep
import com.example.ui.screens.caseflow.ChronicHpcStep
import com.example.ui.screens.caseflow.ChronicOdpStep
import com.example.ui.screens.caseflow.EvaluationOfSymptomsStep
import com.example.ui.screens.caseflow.FamilyHistoryTableStep
import com.example.ui.screens.caseflow.FinalDiagnosisStep
import com.example.ui.screens.caseflow.FollowUpStep
import com.example.ui.screens.caseflow.GeneralPhysicalExamStep
import com.example.ui.screens.caseflow.InvestigationStep
import com.example.ui.screens.caseflow.ManagementStep
import com.example.ui.screens.caseflow.MenstrualHistoryStep
import com.example.ui.screens.caseflow.MentalLifeSpanStep
import com.example.ui.screens.caseflow.MiasmaticEvaluationStep
import com.example.ui.screens.caseflow.ObservationStep
import com.example.ui.screens.caseflow.ObstetricHistoryStep
import com.example.ui.screens.caseflow.PastHistoryTableStep
import com.example.ui.screens.caseflow.PersonalHistoryStep
import com.example.ui.screens.caseflow.PrescriptionStep
import com.example.ui.screens.caseflow.ProvisionalDiagnosisStep
import com.example.ui.screens.caseflow.RepertorizationStep
import com.example.ui.screens.caseflow.SystemicExamStep
import com.example.ui.screens.caseflow.TotalityOfSymptomsStep
import com.example.ui.screens.caseflow.VitalExaminationStep
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.GlassBackgroundScaffold
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaseFlowScreen(
    initialCase: CaseEntity,
    repertoryRepository: RepertoryRepository,
    rubricCount: Int,
    remedyCount: Int,
    onSaveCase: (CaseEntity) -> Unit,
    onFinishCase: (CaseEntity) -> Unit,
    onCancel: () -> Unit,
    onImportPrompt: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isAcute = initialCase.caseType.equals("ACUTE", ignoreCase = true)
    val totalSteps = if (isAcute) 20 else 26

    var currentStep by remember { mutableIntStateOf(initialCase.currentStep.coerceIn(0, totalSteps - 1)) }
    var caseData by remember { mutableStateOf(initialCase) }
    var formData by remember(initialCase.id) { mutableStateOf(ClinicalFormData.fromCaseEntity(initialCase)) }

    // Selected Rubrics State
    var selectedRubrics by remember {
        mutableStateOf(RepertoryRepository.parseSelectedRubrics(initialCase.selectedRubricsJson))
    }

    // Repertorization Results Cache
    var repertorizationResults by remember { mutableStateOf<List<RepertorizationScore>>(emptyList()) }

    // Compute repertorization whenever selected rubrics change
    LaunchedEffect(selectedRubrics) {
        repertorizationResults = repertoryRepository.computeRepertorization(selectedRubrics)
    }

    val stepTitles = remember(isAcute) {
        if (isAcute) {
            listOf(
                "Case Identification",
                "Chief Complaints (LSMC with O.D.P.)",
                "Associated Complaints",
                "Personal History",
                "Vital Examination",
                "Menstrual History (if applicable)",
                "Patient's Obstetric History (if applicable)",
                "General Physical Examination",
                "Systemic Examination",
                "Clinical Investigations",
                "Provisional Diagnosis",
                "Clinician's Observation",
                "Acute Mental State & Personality",
                "Analysis of Symptoms",
                "Evaluation of Symptoms",
                "Totality of Symptoms",
                "Homeopathic Repertorization",
                "Selection of Acute Similimum",
                "Management (Advices)",
                "Follow-up & Prognosis"
            )
        } else {
            listOf(
                "Case Identification",
                "Chief Complaints",
                "O.D.P. (Onset, Duration, Progress)",
                "History of Presenting Complaint (Chronic)",
                "Associated Complaints",
                "Past Medical & Surgical History",
                "Family History",
                "Personal History",
                "Vital Examination",
                "Menstrual History (if applicable)",
                "Patient's Obstetric History (if applicable)",
                "General Physical Examination",
                "Systemic Examination",
                "Clinical Investigations",
                "Provisional Diagnosis",
                "Final Diagnosis",
                "Clinician's Observation",
                "Mental & Life Span Narrative",
                "Miasmatic Evaluation",
                "Analysis of Symptoms",
                "Evaluation of Symptoms",
                "Totality of Symptoms",
                "Homeopathic Repertorization",
                "Selection of Constitutional Similimum",
                "Management (Advices)",
                "Follow-up & Prognosis"
            )
        }
    }

    val accentColor = if (isAcute) {
        if (isDark) TerracottaLight else TerracottaPrimary
    } else {
        if (isDark) SageGreenLight else SageGreenPrimary
    }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    // Helper to persist current form state and rubrics state into caseData
    fun updateCaseWithRubrics(): CaseEntity {
        val synced = formData.toCaseEntity(caseData)
        return synced.copy(
            currentStep = currentStep,
            selectedRubricsJson = RepertoryRepository.serializeSelectedRubrics(selectedRubrics)
        )
    }

    fun handleFormUpdate(newForm: ClinicalFormData) {
        formData = newForm
        caseData = newForm.toCaseEntity(caseData)
    }

    GlassBackgroundScaffold(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            // Header Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val updated = updateCaseWithRubrics()
                            onSaveCase(updated)
                            onCancel()
                        },
                        modifier = Modifier.testTag("btn_close_case_flow")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Save and Close",
                            tint = textColor
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isAcute) "ACUTE PROTOCOL" else "CHRONIC PROTOCOL",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                            color = accentColor,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Step ${currentStep + 1} of $totalSteps",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = subtextColor
                        )
                    }

                    IconButton(
                        onClick = {
                            val updated = updateCaseWithRubrics()
                            onSaveCase(updated)
                            Toast.makeText(context, "Case draft saved", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("btn_save_case_draft")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Quick Save Draft",
                            tint = accentColor
                        )
                    }
                }
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / totalSteps },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = accentColor,
                trackColor = if (isDark) Color(0x33FFFFFF) else Color(0x22000000)
            )

            // Step Content Area (Scrollable)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Step Title & Indicator
                Text(
                    text = "STEP ${currentStep + 1}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stepTitles[currentStep],
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = textColor
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Render dynamic form based on current step
                if (isAcute) {
                    RenderAcuteStep(
                        step = currentStep,
                        formData = formData,
                        onUpdateForm = ::handleFormUpdate,
                        selectedRubrics = selectedRubrics,
                        onAddRubric = { selectedRubrics = selectedRubrics + it },
                        onRemoveRubric = { rubricId ->
                            selectedRubrics = selectedRubrics.filter { it.id != rubricId }
                        },
                        repertoryRepository = repertoryRepository,
                        repertorizationResults = repertorizationResults,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        isDark = isDark
                    )
                } else {
                    RenderChronicStep(
                        step = currentStep,
                        formData = formData,
                        onUpdateForm = ::handleFormUpdate,
                        selectedRubrics = selectedRubrics,
                        onAddRubric = { selectedRubrics = selectedRubrics + it },
                        onRemoveRubric = { rubricId ->
                            selectedRubrics = selectedRubrics.filter { it.id != rubricId }
                        },
                        repertoryRepository = repertoryRepository,
                        repertorizationResults = repertorizationResults,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        isDark = isDark
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom Navigation Actions (Back / Continue)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                color = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    if (currentStep > 0) {
                        OutlinedButton(
                            onClick = {
                                val updated = updateCaseWithRubrics()
                                onSaveCase(updated)
                                currentStep--
                            },
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp)
                                .testTag("btn_step_back")
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Back")
                        }
                    }

                    // Continue / Finish Button
                    Button(
                        onClick = {
                            val updated = updateCaseWithRubrics()
                            if (currentStep < totalSteps - 1) {
                                onSaveCase(updated)
                                currentStep++
                            } else {
                                val finishedCase = updated.copy(
                                    isCompleted = true,
                                    dateModified = System.currentTimeMillis()
                                )
                                onSaveCase(finishedCase)
                                onFinishCase(finishedCase)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(if (currentStep == 0) 2f else 1.2f)
                            .height(50.dp)
                            .testTag(if (currentStep < totalSteps - 1) "btn_step_continue" else "btn_finish_case")
                    ) {
                        Text(
                            text = if (currentStep < totalSteps - 1) "Continue" else "Save & Complete Case",
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = if (currentStep < totalSteps - 1) Icons.AutoMirrored.Filled.ArrowForward else Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Acute Protocol Step Router (20 Steps)
// -------------------------------------------------------------------------------------------------
@Composable
private fun RenderAcuteStep(
    step: Int,
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    repertorizationResults: List<RepertorizationScore>,
    rubricCount: Int,
    remedyCount: Int,
    isDark: Boolean
) {
    when (step) {
        0 -> CaseIdentificationStep(formData, onUpdateForm, isDark)
        1 -> AcuteLsmcStep(formData, onUpdateForm, isDark)
        2 -> AssociatedComplaintsStep(formData, onUpdateForm, isDark)
        3 -> PersonalHistoryStep(formData, onUpdateForm, isDark)
        4 -> VitalExaminationStep(formData, onUpdateForm, isDark)
        5 -> MenstrualHistoryStep(formData, onUpdateForm, isDark)
        6 -> ObstetricHistoryStep(formData, onUpdateForm, isDark)
        7 -> GeneralPhysicalExamStep(formData, onUpdateForm, isDark)
        8 -> SystemicExamStep(formData, onUpdateForm, isDark)
        9 -> InvestigationStep(formData, onUpdateForm, isDark)
        10 -> ProvisionalDiagnosisStep(formData, onUpdateForm, isDark)
        11 -> ObservationStep(formData, onUpdateForm, isDark)
        12 -> MentalLifeSpanStep(formData, onUpdateForm, isDark, isAcute = true)
        13 -> AnalysisOfSymptomsStep(formData, onUpdateForm, isDark)
        14 -> EvaluationOfSymptomsStep(formData, onUpdateForm, isDark)
        15 -> TotalityOfSymptomsStep(formData, onUpdateForm, selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, isDark)
        16 -> RepertorizationStep(selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, repertorizationResults, rubricCount, remedyCount, isDark)
        17 -> PrescriptionStep(formData, onUpdateForm, repertorizationResults, isDark, isAcute = true)
        18 -> ManagementStep(formData, onUpdateForm, isDark)
        19 -> FollowUpStep(formData, onUpdateForm, isDark)
    }
}

// -------------------------------------------------------------------------------------------------
// Chronic Protocol Step Router (26 Steps)
// -------------------------------------------------------------------------------------------------
@Composable
private fun RenderChronicStep(
    step: Int,
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    repertorizationResults: List<RepertorizationScore>,
    rubricCount: Int,
    remedyCount: Int,
    isDark: Boolean
) {
    when (step) {
        0 -> CaseIdentificationStep(formData, onUpdateForm, isDark)
        1 -> ChronicChiefComplaintStep(formData, onUpdateForm, isDark)
        2 -> ChronicOdpStep(formData, onUpdateForm, isDark)
        3 -> ChronicHpcStep(formData, onUpdateForm, isDark)
        4 -> AssociatedComplaintsStep(formData, onUpdateForm, isDark)
        5 -> PastHistoryTableStep(formData, onUpdateForm, isDark)
        6 -> FamilyHistoryTableStep(formData, onUpdateForm, isDark)
        7 -> PersonalHistoryStep(formData, onUpdateForm, isDark)
        8 -> VitalExaminationStep(formData, onUpdateForm, isDark)
        9 -> MenstrualHistoryStep(formData, onUpdateForm, isDark)
        10 -> ObstetricHistoryStep(formData, onUpdateForm, isDark)
        11 -> GeneralPhysicalExamStep(formData, onUpdateForm, isDark)
        12 -> SystemicExamStep(formData, onUpdateForm, isDark)
        13 -> InvestigationStep(formData, onUpdateForm, isDark)
        14 -> ProvisionalDiagnosisStep(formData, onUpdateForm, isDark)
        15 -> FinalDiagnosisStep(formData, onUpdateForm, isDark)
        16 -> ObservationStep(formData, onUpdateForm, isDark)
        17 -> MentalLifeSpanStep(formData, onUpdateForm, isDark, isAcute = false)
        18 -> MiasmaticEvaluationStep(formData, onUpdateForm, isDark)
        19 -> AnalysisOfSymptomsStep(formData, onUpdateForm, isDark)
        20 -> EvaluationOfSymptomsStep(formData, onUpdateForm, isDark)
        21 -> TotalityOfSymptomsStep(formData, onUpdateForm, selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, isDark)
        22 -> RepertorizationStep(selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, repertorizationResults, rubricCount, remedyCount, isDark)
        23 -> PrescriptionStep(formData, onUpdateForm, repertorizationResults, isDark, isAcute = false)
        24 -> ManagementStep(formData, onUpdateForm, isDark)
        25 -> FollowUpStep(formData, onUpdateForm, isDark)
    }
}
