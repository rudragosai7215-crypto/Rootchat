package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
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
import com.example.ui.screens.caseflow.RepertorySelectionStep
import com.example.ui.screens.caseflow.SystemicExamStep
import com.example.ui.screens.caseflow.TotalityOfSymptomsStep
import com.example.ui.screens.caseflow.VitalExaminationStep
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassBackgroundScaffold
import com.example.ui.theme.LightGlassBorder
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
    onCancel: (CaseEntity?) -> Unit,
    onImportPrompt: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    saveError: String? = null,
    onClearSaveError: () -> Unit = {}
) {
    val context = LocalContext.current
    val isAcute = initialCase.caseType.equals("ACUTE", ignoreCase = true)
    val totalSteps = if (isAcute) 21 else 27

    var currentStep by remember(initialCase.id) { mutableIntStateOf(initialCase.currentStep.coerceIn(0, totalSteps - 1)) }
    var caseData by remember(initialCase.id) { mutableStateOf(initialCase) }
    var formData by remember(initialCase.id) { mutableStateOf(ClinicalFormData.fromCaseEntity(initialCase)) }
    var showStepListDialog by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    LaunchedEffect(currentStep) {
        scrollState.scrollTo(0)
    }

    // Selected Rubrics State (strictly unique by rubric id)
    var selectedRubrics by remember(initialCase.id) {
        mutableStateOf(RepertoryRepository.parseSelectedRubrics(initialCase.selectedRubricsJson).distinctBy { it.id })
    }

    val handleAddRubric: (SelectedRubric) -> Unit = { rubric ->
        if (selectedRubrics.none { it.id == rubric.id }) {
            selectedRubrics = selectedRubrics + rubric
        }
    }

    val handleRemoveRubric: (Long) -> Unit = { rubricId ->
        selectedRubrics = selectedRubrics.filter { it.id != rubricId }
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
                "Selection of Repertory",
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
                "Selection of Repertory",
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

    // Debounced Autosave engine
    var saveStatusText by remember { mutableStateOf("Saved") }
    var isAutosaving by remember { mutableStateOf(false) }
    var initialAutosaveSkipped by remember { mutableStateOf(false) }

    LaunchedEffect(formData, selectedRubrics, currentStep) {
        if (!initialAutosaveSkipped) {
            initialAutosaveSkipped = true
            return@LaunchedEffect
        }
        saveStatusText = "Editing..."
        delay(1200) // 1.2-second debounce
        isAutosaving = true
        saveStatusText = "Autosaving..."
        val updated = updateCaseWithRubrics()
        onSaveCase(updated)
        delay(300)
        isAutosaving = false
        saveStatusText = "Autosaved"
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
                    .padding(horizontal = 14.dp, vertical = 6.dp),
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
                            onCancel(updated)
                        },
                        modifier = Modifier.testTag("btn_close_case_flow")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Save and Close",
                            tint = textColor
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showStepListDialog = true }
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (isAcute) "Acute Protocol" else "Chronic Protocol",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            fontFamily = FontFamily.Serif
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Step ${currentStep + 1} of $totalSteps",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = subtextColor
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = subtextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Autosave Status Pill (Sage Green capsule matching reference)
                    Surface(
                        color = if (isDark) Color(0xFF1E3326) else Color(0xFFE8EFEA),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.clickable {
                            if (!isSaving) {
                                val updated = updateCaseWithRubrics()
                                onSaveCase(updated)
                                saveStatusText = "Saved"
                                Toast.makeText(context, "Case draft saved", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            if (isAutosaving || isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(10.dp),
                                    strokeWidth = 1.5.dp,
                                    color = if (isDark) SageGreenLight else SageGreenPrimary
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = if (isDark) SageGreenLight else SageGreenPrimary,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                            Text(
                                text = if (isSaving) "Saving..." else if (isAutosaving) "Saving" else "Autosaved",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) SageGreenLight else SageGreenPrimary
                            )
                        }
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
                    .verticalScroll(scrollState)
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
                        onAddRubric = handleAddRubric,
                        onRemoveRubric = handleRemoveRubric,
                        repertoryRepository = repertoryRepository,
                        repertorizationResults = repertorizationResults,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        onNavigateNext = { if (currentStep < totalSteps - 1) currentStep++ },
                        isDark = isDark
                    )
                } else {
                    RenderChronicStep(
                        step = currentStep,
                        formData = formData,
                        onUpdateForm = ::handleFormUpdate,
                        selectedRubrics = selectedRubrics,
                        onAddRubric = handleAddRubric,
                        onRemoveRubric = handleRemoveRubric,
                        repertoryRepository = repertoryRepository,
                        repertorizationResults = repertorizationResults,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        onNavigateNext = { if (currentStep < totalSteps - 1) currentStep++ },
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
                                if (isSaving) return@OutlinedButton
                                val updated = updateCaseWithRubrics()
                                onSaveCase(updated)
                                currentStep--
                            },
                            enabled = !isSaving,
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
                            if (isSaving) return@Button
                            val updated = updateCaseWithRubrics()
                            if (currentStep < totalSteps - 1) {
                                onSaveCase(updated)
                                currentStep++
                            } else {
                                val finishedCase = updated.copy(
                                    isCompleted = true,
                                    dateModified = System.currentTimeMillis()
                                )
                                onFinishCase(finishedCase)
                            }
                        },
                        enabled = !isSaving,
                        colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(if (currentStep == 0) 2f else 1.2f)
                            .height(50.dp)
                            .testTag(if (currentStep < totalSteps - 1) "btn_step_continue" else "btn_finish_case")
                    ) {
                        if (isSaving && currentStep == totalSteps - 1) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Saving Case...", fontWeight = FontWeight.SemiBold, color = Color.White)
                        } else {
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

            if (showStepListDialog) {
                AlertDialog(
                    onDismissRequest = { showStepListDialog = false },
                    title = {
                        Text(
                            text = "Jump to Protocol Step",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                    },
                    text = {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 420.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            itemsIndexed(stepTitles) { idx, title ->
                                val isCurrent = idx == currentStep
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            if (!isSaving) {
                                                val updated = updateCaseWithRubrics().copy(currentStep = idx)
                                                caseData = updated
                                                currentStep = idx
                                                onSaveCase(updated)
                                            }
                                            showStepListDialog = false
                                        }
                                        .background(if (isCurrent) accentColor.copy(alpha = 0.25f) else Color.Transparent)
                                        .border(
                                            width = if (isCurrent) 1.5.dp else 0.5.dp,
                                            color = if (isCurrent) accentColor else if (isDark) DarkGlassBorder else LightGlassBorder,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "${idx + 1}.",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isCurrent) accentColor else subtextColor
                                        )
                                        Text(
                                            text = title,
                                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = if (isCurrent) accentColor else textColor,
                                            modifier = Modifier.weight(1f)
                                        )
                                        if (isCurrent) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Current Step",
                                                tint = accentColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showStepListDialog = false }) {
                            Text("Close", color = accentColor)
                        }
                    }
                )
            }

            if (saveError != null) {
                AlertDialog(
                    onDismissRequest = onClearSaveError,
                    title = {
                        Text("Storage Error", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Text(saveError)
                    },
                    confirmButton = {
                        Button(onClick = onClearSaveError) {
                            Text("Dismiss")
                        }
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Acute Protocol Step Router (21 Steps)
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
    onNavigateNext: () -> Unit,
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
        16 -> RepertorySelectionStep(formData, onUpdateForm, repertoryRepository, rubricCount, remedyCount, onNavigateNext, isDark)
        17 -> RepertorizationStep(selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, repertorizationResults, rubricCount, remedyCount, isDark, repertoryType = formData.repertoryType)
        18 -> PrescriptionStep(formData, onUpdateForm, repertorizationResults, isDark, isAcute = true)
        19 -> ManagementStep(formData, onUpdateForm, isDark)
        20 -> FollowUpStep(formData, onUpdateForm, isDark)
    }
}

// -------------------------------------------------------------------------------------------------
// Chronic Protocol Step Router (27 Steps)
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
    onNavigateNext: () -> Unit,
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
        22 -> RepertorySelectionStep(formData, onUpdateForm, repertoryRepository, rubricCount, remedyCount, onNavigateNext, isDark)
        23 -> RepertorizationStep(selectedRubrics, onAddRubric, onRemoveRubric, repertoryRepository, repertorizationResults, rubricCount, remedyCount, isDark, repertoryType = formData.repertoryType)
        24 -> PrescriptionStep(formData, onUpdateForm, repertorizationResults, isDark, isAcute = false)
        25 -> ManagementStep(formData, onUpdateForm, isDark)
        26 -> FollowUpStep(formData, onUpdateForm, isDark)
    }
}
