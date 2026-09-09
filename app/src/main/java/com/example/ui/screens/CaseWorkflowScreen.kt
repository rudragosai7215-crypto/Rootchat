package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KentRepertoryDataset
import com.example.data.model.CaseTotalityItem
import com.example.data.model.KentRubric
import com.example.data.model.RemedyGrade
import com.example.data.model.RepertorizationAnalysis
import com.example.ui.theme.ClinicalTerracotta
import com.example.ui.theme.KentGrade1Slate
import com.example.ui.theme.KentGrade2Blue
import com.example.ui.theme.KentGrade3Red
import com.example.ui.theme.LinenBackground
import com.example.ui.theme.LinenSurface
import com.example.ui.theme.SageMiasm
import com.example.ui.theme.WarmCharcoal
import com.example.ui.viewmodel.CaseWorkflowStep
import com.example.ui.viewmodel.PatientCaseInfo

@Composable
fun CaseWorkflowScreen(
  currentStep: CaseWorkflowStep,
  currentStepIndex: Int = 0,
  caseInfo: PatientCaseInfo,
  totalityItems: List<CaseTotalityItem>,
  analysis: RepertorizationAnalysis,
  allRubrics: List<KentRubric>,
  chapters: List<String>,
  onUpdateCaseInfo: (PatientCaseInfo) -> Unit,
  onSetStep: (CaseWorkflowStep) -> Unit,
  onSetStepIndex: (Int) -> Unit = {},
  onNextStep: () -> Unit,
  onPrevStep: () -> Unit,
  onAddRubric: (KentRubric, Int) -> Unit,
  onRemoveRubric: (String) -> Unit,
  onUpdateIntensity: (String, Int) -> Unit,
  onSaveCase: () -> Unit,
  onCancelToDashboard: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isAcute = caseInfo.caseType.equals("Acute", ignoreCase = true)
  val primaryThemeColor = if (isAcute) ClinicalTerracotta else SageMiasm
  val totalSteps = if (isAcute) 12 else 17
  val safeStepIndex = currentStepIndex.coerceIn(0, totalSteps - 1)
  val progress = (safeStepIndex + 1).toFloat() / totalSteps.toFloat()

  val acuteStepLabels = listOf(
    "1. Identification",
    "2. Chief Complaint",
    "3. HPI (Acute)",
    "4. Etiology",
    "5. Particulars (LSMC)",
    "6. Acute Mentals",
    "7. Physical Generals",
    "8. Clinical Exam",
    "9. Clinical Diagnosis",
    "10. Totality Rubrics",
    "11. Repertorization",
    "12. Prescription & Save"
  )

  val chronicStepLabels = listOf(
    "1. Identification",
    "2. Chief Complaint",
    "3. HPI (Chronic)",
    "4. Past History",
    "5. Family History",
    "6. Personal History",
    "7. Mental Generals",
    "8. Physical Generals",
    "9. Particulars (LSMC)",
    "10. Female History",
    "11. Clinical Exam",
    "12. Final Diagnosis",
    "13. Miasmatic Eval",
    "14. Totality Rubrics",
    "15. Repertorization",
    "16. Prescription",
    "17. Follow-up & Save"
  )

  val stepLabels = if (isAcute) acuteStepLabels else chronicStepLabels

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(LinenBackground)
      .navigationBarsPadding()
  ) {
    // 1. Top Workflow Header
    Surface(
      color = LinenSurface,
      shadowElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 8.dp, bottom = 6.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
              onClick = {
                if (safeStepIndex > 0) onPrevStep() else onCancelToDashboard()
              },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = WarmCharcoal
              )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = if (isAcute) "⚡ Acute Case Taking" else "🌿 Chronic Case Taking",
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  fontFamily = FontFamily.Serif,
                  color = WarmCharcoal
                )
                Spacer(modifier = Modifier.width(6.dp))
                Surface(
                  color = primaryThemeColor.copy(alpha = 0.15f),
                  shape = RoundedCornerShape(4.dp)
                ) {
                  Text(
                    text = "STEP ${safeStepIndex + 1}/$totalSteps",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = primaryThemeColor,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                  )
                }
              }
              Text(
                text = if (caseInfo.name.isNotBlank()) "Patient: ${caseInfo.name} • ${caseInfo.age.ifBlank { "?" }} yrs" else "New Case",
                fontSize = 11.sp,
                color = WarmCharcoal.copy(alpha = 0.65f)
              )
            }
          }

          TextButton(onClick = onCancelToDashboard) {
            Text("Dashboard", fontSize = 12.sp, color = WarmCharcoal.copy(alpha = 0.7f))
          }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Progress Bar
        Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)) {
          LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
              .fillMaxWidth()
              .height(5.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = primaryThemeColor,
            trackColor = primaryThemeColor.copy(alpha = 0.15f)
          )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal Step Chips
        ScrollableTabRow(
          selectedTabIndex = safeStepIndex,
          edgePadding = 16.dp,
          containerColor = Color.Transparent,
          divider = {}
        ) {
          stepLabels.forEachIndexed { index, label ->
            val isSelected = safeStepIndex == index
            val isPassed = index < safeStepIndex
            Tab(
              selected = isSelected,
              onClick = { onSetStepIndex(index) },
              text = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(18.dp)
                      .clip(CircleShape)
                      .background(
                        when {
                          isSelected -> primaryThemeColor
                          isPassed -> SageMiasm
                          else -> Color(0xFFD6CEBE)
                        }
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isPassed) {
                      Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                      )
                    } else {
                      Text(
                        text = "${index + 1}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) primaryThemeColor else WarmCharcoal.copy(alpha = 0.7f)
                  )
                }
              }
            )
          }
        }
      }
    }

    // 2. Step Content Body
    Box(
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
    ) {
      if (isAcute) {
        when (safeStepIndex) {
          0 -> PatientIdentificationStep(
            caseInfo = caseInfo,
            stepNumber = 1,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = { if (safeStepIndex > 0) onPrevStep() else onCancelToDashboard() }
          )
          1 -> ChiefComplaintStep(
            caseInfo = caseInfo,
            stepNumber = 2,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          2 -> HpiStep(
            caseInfo = caseInfo,
            stepNumber = 3,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          3 -> AcuteEtiologyStep(
            caseInfo = caseInfo,
            stepNumber = 4,
            totalSteps = 12,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          4 -> ParticularsLsmcStep(
            caseInfo = caseInfo,
            stepNumber = 5,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          5 -> AcuteMentalsStep(
            caseInfo = caseInfo,
            stepNumber = 6,
            totalSteps = 12,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          6 -> AcuteGeneralsStep(
            caseInfo = caseInfo,
            stepNumber = 7,
            totalSteps = 12,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          7 -> ClinicalExamStep(
            caseInfo = caseInfo,
            stepNumber = 8,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          8 -> DiagnosisStep(
            caseInfo = caseInfo,
            stepNumber = 9,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          9 -> Step3TotalityView(
            caseInfo = caseInfo,
            totalityItems = totalityItems,
            allRubrics = allRubrics,
            onAddRubric = onAddRubric,
            onRemoveRubric = onRemoveRubric,
            onUpdateIntensity = onUpdateIntensity,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          10 -> Step4RepertorizationView(
            totalityItems = totalityItems,
            analysis = analysis,
            allRubrics = allRubrics,
            chapters = chapters,
            onAddRubric = onAddRubric,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          11 -> Step5PrescriptionView(
            caseInfo = caseInfo,
            analysis = analysis,
            onUpdate = onUpdateCaseInfo,
            onNext = onSaveCase,
            onPrev = onPrevStep,
            isAcute = true,
            onSaveCase = onSaveCase
          )
          else -> PatientIdentificationStep(
            caseInfo = caseInfo,
            stepNumber = 1,
            totalSteps = 12,
            isChronic = false,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = { if (safeStepIndex > 0) onPrevStep() else onCancelToDashboard() }
          )
        }
      } else {
        // Chronic (17 steps)
        when (safeStepIndex) {
          0 -> PatientIdentificationStep(
            caseInfo = caseInfo,
            stepNumber = 1,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = { if (safeStepIndex > 0) onPrevStep() else onCancelToDashboard() }
          )
          1 -> ChiefComplaintStep(
            caseInfo = caseInfo,
            stepNumber = 2,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          2 -> HpiStep(
            caseInfo = caseInfo,
            stepNumber = 3,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          3 -> ChronicPastHistoryStep(
            caseInfo = caseInfo,
            stepNumber = 4,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          4 -> ChronicFamilyHistoryStep(
            caseInfo = caseInfo,
            stepNumber = 5,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          5 -> ChronicPersonalHistoryStep(
            caseInfo = caseInfo,
            stepNumber = 6,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          6 -> ChronicMentalsStep(
            caseInfo = caseInfo,
            stepNumber = 7,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          7 -> ChronicGeneralsStep(
            caseInfo = caseInfo,
            stepNumber = 8,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          8 -> ParticularsLsmcStep(
            caseInfo = caseInfo,
            stepNumber = 9,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          9 -> ChronicFemaleHistoryStep(
            caseInfo = caseInfo,
            stepNumber = 10,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          10 -> ClinicalExamStep(
            caseInfo = caseInfo,
            stepNumber = 11,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          11 -> DiagnosisStep(
            caseInfo = caseInfo,
            stepNumber = 12,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          12 -> ChronicMiasmaticStep(
            caseInfo = caseInfo,
            stepNumber = 13,
            totalSteps = 17,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          13 -> Step3TotalityView(
            caseInfo = caseInfo,
            totalityItems = totalityItems,
            allRubrics = allRubrics,
            onAddRubric = onAddRubric,
            onRemoveRubric = onRemoveRubric,
            onUpdateIntensity = onUpdateIntensity,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          14 -> Step4RepertorizationView(
            totalityItems = totalityItems,
            analysis = analysis,
            allRubrics = allRubrics,
            chapters = chapters,
            onAddRubric = onAddRubric,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          15 -> Step5PrescriptionView(
            caseInfo = caseInfo,
            analysis = analysis,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = onPrevStep
          )
          16 -> Step6FollowUpView(
            caseInfo = caseInfo,
            totalityItems = totalityItems,
            analysis = analysis,
            onUpdate = onUpdateCaseInfo,
            onSaveCase = onSaveCase,
            onPrev = onPrevStep
          )
          else -> PatientIdentificationStep(
            caseInfo = caseInfo,
            stepNumber = 1,
            totalSteps = 17,
            isChronic = true,
            primaryColor = primaryThemeColor,
            onUpdate = onUpdateCaseInfo,
            onNext = onNextStep,
            onPrev = { if (safeStepIndex > 0) onPrevStep() else onCancelToDashboard() }
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 1: PRELIMINARY DATA
// -------------------------------------------------------------
@Composable
fun Step1PreliminaryView(
  caseInfo: PatientCaseInfo,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Preliminary Patient Information",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )
        Text(
          text = "Step 1: Enter standard clinical demographics and primary presenting complaint.",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.65f)
        )

        HorizontalDivider(color = Color(0xFFE8E0D4))

        // Name
        OutlinedTextField(
          value = caseInfo.name,
          onValueChange = { onUpdate(caseInfo.copy(name = it)) },
          label = { Text("Patient Full Name *") },
          placeholder = { Text("e.g. Ramesh Shah") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_name_field"),
          shape = RoundedCornerShape(10.dp)
        )

        // Age & Gender row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = caseInfo.age,
            onValueChange = { onUpdate(caseInfo.copy(age = it)) },
            label = { Text("Age (Yrs) *") },
            placeholder = { Text("35") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )

          Column(modifier = Modifier.weight(1.5f)) {
            Text(
              text = "Gender",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.7f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf("Male", "Female", "Child").forEach { g ->
                val isSel = caseInfo.gender == g
                FilterChip(
                  selected = isSel,
                  onClick = { onUpdate(caseInfo.copy(gender = g)) },
                  label = { Text(g, fontSize = 11.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ClinicalTerracotta,
                    selectedLabelColor = Color.White
                  ),
                  shape = RoundedCornerShape(8.dp)
                )
              }
            }
          }
        }

        // Phone & City
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          OutlinedTextField(
            value = caseInfo.phone,
            onValueChange = { onUpdate(caseInfo.copy(phone = it)) },
            label = { Text("Phone / WhatsApp") },
            placeholder = { Text("9876543210") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )

          OutlinedTextField(
            value = caseInfo.address,
            onValueChange = { onUpdate(caseInfo.copy(address = it)) },
            label = { Text("City / Town") },
            placeholder = { Text("Surat / Ahmedabad") },
            singleLine = true,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
          )
        }

        // Chief Complaint
        OutlinedTextField(
          value = caseInfo.complaint,
          onValueChange = { onUpdate(caseInfo.copy(complaint = it)) },
          label = { Text("Chief Complaint (In Patient's Own Words) *") },
          placeholder = { Text("e.g. Severe right sided throbbing headache with nausea for 3 days after exposure to dry cold wind...") },
          minLines = 3,
          maxLines = 5,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_complaint_field"),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }

    // Continue button
    Button(
      onClick = onNext,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("step1_next_button"),
      colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("Next: Case Details", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      Spacer(modifier = Modifier.width(8.dp))
      Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
    }

    Spacer(modifier = Modifier.height(48.dp))
  }
}

// -------------------------------------------------------------
// STEP 2: CASE RECORDING (ACUTE VS CHRONIC ALAG ALAG)
// -------------------------------------------------------------
@Composable
fun Step2CaseDetailsView(
  caseInfo: PatientCaseInfo,
  isAcute: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    if (isAcute) {
      // ⚡ ACUTE CASE RECORDING
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Bolt, contentDescription = null, tint = ClinicalTerracotta)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Acute Case Recording (Ailments, Modalities & Totality)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = WarmCharcoal
            )
          }

          Text(
            text = "Record rapid acute totality: exciting cause, sensations, and acute modalities.",
            fontSize = 12.sp,
            color = WarmCharcoal.copy(alpha = 0.65f)
          )

          HorizontalDivider(color = Color(0xFFE8E0D4))

          // Etiology / Ailments from
          OutlinedTextField(
            value = caseInfo.acuteCause,
            onValueChange = { onUpdate(caseInfo.copy(acuteCause = it)) },
            label = { Text("Ailments From / Exciting Cause") },
            placeholder = { Text("e.g. Exposure to dry cold draft, getting wet in rain, suppressed perspiration, anger, bad news") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Location & Radiation
          OutlinedTextField(
            value = caseInfo.acuteLocation,
            onValueChange = { onUpdate(caseInfo.copy(acuteLocation = it)) },
            label = { Text("Location & Extension") },
            placeholder = { Text("e.g. Forehead extending to occiput; Right side of abdomen radiating to thighs") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Sensation
          OutlinedTextField(
            value = caseInfo.acuteSensation,
            onValueChange = { onUpdate(caseInfo.copy(acuteSensation = it)) },
            label = { Text("Sensation (Character of Pain)") },
            placeholder = { Text("e.g. Throbbing, bursting, stitching, cutting, burning as if hot coals, heavy weight") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Modalities (< Aggravation and > Amelioration)
          OutlinedTextField(
            value = caseInfo.acuteModalities,
            onValueChange = { onUpdate(caseInfo.copy(acuteModalities = it)) },
            label = { Text("Modalities (< Aggravation / > Amelioration)") },
            placeholder = { Text("e.g. < Motion, < Night 3am, < Cold air | > Rest, > Heat, > Hard pressure, > Lying on painful side") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Concomitants
          OutlinedTextField(
            value = caseInfo.acuteConcomitants,
            onValueChange = { onUpdate(caseInfo.copy(acuteConcomitants = it)) },
            label = { Text("Concomitant Symptoms") },
            placeholder = { Text("e.g. Headache accompanied by nausea and cold sweat on forehead") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Acute Physical Generals: Thermal, Thirst, Tongue
          Text(
            text = "Acute Physical Generals & Mental State",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = ClinicalTerracotta
          )

          // Thermal selector
          Text("Thermal State in Acute:", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.7f))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Chilly", "Hot", "Throws Off Covers", "Neutral").forEach { t ->
              val isSel = caseInfo.acuteThermal == t
              FilterChip(
                selected = isSel,
                onClick = { onUpdate(caseInfo.copy(acuteThermal = t)) },
                label = { Text(t, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ClinicalTerracotta,
                  selectedLabelColor = Color.White
                )
              )
            }
          }

          // Thirst selector
          Text("Thirst in Acute:", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.7f))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Large quantities", "Small sips frequent", "Thirstless", "Craves ice cold").forEach { th ->
              val isSel = caseInfo.acuteThirst == th
              FilterChip(
                selected = isSel,
                onClick = { onUpdate(caseInfo.copy(acuteThirst = th)) },
                label = { Text(th, fontSize = 10.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ClinicalTerracotta,
                  selectedLabelColor = Color.White
                )
              )
            }
          }

          // Mental State in Acute
          OutlinedTextField(
            value = caseInfo.acuteMentalSymptoms,
            onValueChange = { onUpdate(caseInfo.copy(acuteMentalSymptoms = it)) },
            label = { Text("Acute Mental Disposition & Restlessness") },
            placeholder = { Text("e.g. Extreme physical restlessness, anxious, fearful of death, or wants to lie completely quiet and alone") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      }
    } else {
      // 🌿 CHRONIC CASE RECORDING
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Spa, contentDescription = null, tint = SageMiasm)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "Chronic Case Recording (Constitutional Totality)",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = WarmCharcoal
            )
          }

          Text(
            text = "Record constitutional depth: past & family history, physical generals, and mental characteristics.",
            fontSize = 12.sp,
            color = WarmCharcoal.copy(alpha = 0.65f)
          )

          HorizontalDivider(color = Color(0xFFE8E0D4))

          // HPI
          OutlinedTextField(
            value = caseInfo.chronicHpi,
            onValueChange = { onUpdate(caseInfo.copy(chronicHpi = it)) },
            label = { Text("History of Present Illness (Onset & Evolution)") },
            placeholder = { Text("e.g. Chronic complaints since 2 years, gradual onset after business loss...") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Past Medical History & Suppressions
          OutlinedTextField(
            value = caseInfo.chronicPastHistory,
            onValueChange = { onUpdate(caseInfo.copy(chronicPastHistory = it)) },
            label = { Text("Past Medical History & Suppressions") },
            placeholder = { Text("e.g. Severe eczema in childhood suppressed with steroid ointment; Recurrent tonsillitis; Typhoid") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Family History (Miasms)
          OutlinedTextField(
            value = caseInfo.chronicFamilyHistory,
            onValueChange = { onUpdate(caseInfo.copy(chronicFamilyHistory = it)) },
            label = { Text("Family History (Miasmatic Predisposition)") },
            placeholder = { Text("e.g. Maternal diabetes & asthma; Paternal hypertension; Tuberculosis history") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Physical Generals
          Text(
            text = "Physical Generals",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SageMiasm
          )

          // Thermal
          Text("Thermal Reaction:", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.7f))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Chilly Patient", "Hot Patient", "Ambithermal").forEach { th ->
              val isSel = caseInfo.chronicThermal == th
              FilterChip(
                selected = isSel,
                onClick = { onUpdate(caseInfo.copy(chronicThermal = th)) },
                label = { Text(th, fontSize = 11.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = SageMiasm,
                  selectedLabelColor = Color.White
                )
              )
            }
          }

          // Cravings & Aversions
          OutlinedTextField(
            value = caseInfo.chronicCravings,
            onValueChange = { onUpdate(caseInfo.copy(chronicCravings = it)) },
            label = { Text("Food Cravings & Aversions") },
            placeholder = { Text("e.g. Craves sweets, salt, spicy; Aversion to milk and fat") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          // Bowels & Sleep
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = caseInfo.chronicBowels,
              onValueChange = { onUpdate(caseInfo.copy(chronicBowels = it)) },
              label = { Text("Bowels / Digestion") },
              placeholder = { Text("e.g. Constipation, ineffectual urging") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
              value = caseInfo.chronicSleep,
              onValueChange = { onUpdate(caseInfo.copy(chronicSleep = it)) },
              label = { Text("Sleep & Dreams") },
              placeholder = { Text("e.g. Wakes 3am, anxious dreams") },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )
          }

          // Mental Generals
          Text(
            text = "Mental Generals & Temperament",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = SageMiasm
          )

          OutlinedTextField(
            value = caseInfo.chronicMindDisposition,
            onValueChange = { onUpdate(caseInfo.copy(chronicMindDisposition = it)) },
            label = { Text("Core Temperament & Emotional State") },
            placeholder = { Text("e.g. Fastidious, anxious about health, hurried in motions, weeps easily or irritable from contradiction") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )

          OutlinedTextField(
            value = caseInfo.chronicFears,
            onValueChange = { onUpdate(caseInfo.copy(chronicFears = it)) },
            label = { Text("Fears, Phobias & Consolation Reaction") },
            placeholder = { Text("e.g. Fear of darkness, solitude, disease; Consolation strictly aggravates") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp)
          )
        }
      }
    }

    // Navigation buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onPrev,
        modifier = Modifier
          .weight(1f)
          .height(56.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Previous", fontSize = 14.sp)
      }

      Button(
        onClick = onNext,
        modifier = Modifier
          .weight(1.5f)
          .height(56.dp)
          .testTag("step2_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Next: Totality Rubrics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
      }
    }

    Spacer(modifier = Modifier.height(48.dp))
  }
}

// -------------------------------------------------------------
// STEP 3: TOTALITY OF SYMPTOMS (PURE SYMPTOM ADDITION)
// -------------------------------------------------------------
@Composable
fun Step3TotalityView(
  caseInfo: PatientCaseInfo,
  totalityItems: List<CaseTotalityItem>,
  allRubrics: List<KentRubric>,
  onAddRubric: (KentRubric, Int) -> Unit,
  onRemoveRubric: (String) -> Unit,
  onUpdateIntensity: (String, Int) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  var currentSymptomText by remember { mutableStateOf("") }
  var currentIntensity by remember { mutableStateOf(2) }

  // Add the current symptom to totality
  val addCurrentSymptom = {
    val cleanName = currentSymptomText.trim()
    if (cleanName.isNotBlank()) {
      val bestMatch = allRubrics.firstOrNull { rubric ->
        val tokens = cleanName.lowercase().split(" ", ",", ";", "-").filter { it.length > 2 }
        tokens.any { rubric.rubricName.contains(it, ignoreCase = true) }
      }
      val rubricToAdd = bestMatch?.copy(
        id = "tot_${System.currentTimeMillis()}_${(0..999).random()}",
        rubricName = cleanName
      ) ?: KentRubric(
        id = "tot_${System.currentTimeMillis()}_${(0..999).random()}",
        chapter = "Generalities",
        rubricName = cleanName,
        subRubric = "",
        remedies = listOf(
          RemedyGrade("Sulph", 2),
          RemedyGrade("Calc", 2),
          RemedyGrade("Lyc", 2),
          RemedyGrade("Phos", 2),
          RemedyGrade("Nux-v", 2),
          RemedyGrade("Puls", 2)
        ),
        miasm = "Psora"
      )
      onAddRubric(rubricToAdd, currentIntensity)
      currentSymptomText = ""
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 18.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Column {
        Text(
          text = "Totality of Symptoms",
          fontSize = 20.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )
        Text(
          text = "Add clinical symptoms for the totality (Symptom 1, Symptom 2, etc.). Corresponding rubrics will be generated in Repertorization.",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.65f),
          modifier = Modifier.padding(top = 2.dp)
        )
      }
    }

    // LIST OF RECORDED TOTALITY SYMPTOMS
    items(totalityItems.size) { index ->
      val item = totalityItems[index]
      Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DACC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Surface(
                color = ClinicalTerracotta,
                shape = RoundedCornerShape(8.dp)
              ) {
                Text(
                  text = "Symptom ${index + 1}",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
              Spacer(modifier = Modifier.width(8.dp))
              Surface(
                color = LinenSurface,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = item.chapter.ifBlank { "Clinical Symptom" },
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Medium,
                  color = WarmCharcoal.copy(alpha = 0.8f),
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }

            IconButton(
              onClick = { onRemoveRubric(item.rubricId) },
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                Icons.Default.Delete,
                contentDescription = "Remove symptom",
                tint = ClinicalTerracotta.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Text(
            text = item.rubricName,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = WarmCharcoal
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Intensity / Weight:",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.7f)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf(1 to "1 (Mild)", 2 to "2 (Mod)", 3 to "3 (Key)").forEach { (grade, label) ->
                val isSelected = item.userIntensity == grade
                Surface(
                  color = if (isSelected) ClinicalTerracotta else LinenSurface,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier
                    .clickable { onUpdateIntensity(item.rubricId, grade) }
                ) {
                  Text(
                    text = label,
                    fontSize = 10.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Color.White else WarmCharcoal,
                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }
        }
      }
    }

    // ACTIVE INPUT CARD FOR NEXT SYMPTOM
    item {
      val nextNumber = totalityItems.size + 1
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, ClinicalTerracotta),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = ClinicalTerracotta,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "Symptom $nextNumber",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (nextNumber == 1) "Enter first totality symptom:" else "Enter next symptom (Symptom $nextNumber):",
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              color = WarmCharcoal
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = currentSymptomText,
            onValueChange = { currentSymptomText = it },
            placeholder = { Text("e.g. Throbbing right sided headache < heat, thirst for ice cold water...") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .onKeyEvent { keyEvent ->
                if (keyEvent.key == Key.Enter && keyEvent.type == KeyEventType.KeyUp) {
                  addCurrentSymptom()
                  true
                } else {
                  false
                }
              }
              .testTag("totality_symptom_input"),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { addCurrentSymptom() }),
            trailingIcon = {
              if (currentSymptomText.isNotBlank()) {
                IconButton(onClick = { currentSymptomText = "" }) {
                  Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                }
              }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ClinicalTerracotta,
              unfocusedBorderColor = Color(0xFFD6CEBE),
              focusedContainerColor = LinenSurface.copy(alpha = 0.5f),
              unfocusedContainerColor = LinenSurface.copy(alpha = 0.3f)
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Intensity selection chips for the symptom being added
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Intensity Grade:",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.7f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
              listOf(1 to "+1 Mild", 2 to "+2 Mod", 3 to "+3 Key").forEach { (grade, label) ->
                val isSel = currentIntensity == grade
                Surface(
                  color = if (isSel) ClinicalTerracotta else LinenSurface,
                  shape = RoundedCornerShape(6.dp),
                  modifier = Modifier.clickable { currentIntensity = grade }
                ) {
                  Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSel) Color.White else WarmCharcoal,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Button(
            onClick = { addCurrentSymptom() },
            enabled = currentSymptomText.isNotBlank(),
            colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Add Symptom $nextNumber to Totality", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }

    // Totality Count Summary
    if (totalityItems.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(12.dp),
          colors = CardDefaults.cardColors(containerColor = LinenSurface),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Total Symptoms Recorded in Totality:",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = WarmCharcoal
            )
            Surface(
              color = ClinicalTerracotta,
              shape = CircleShape
            ) {
              Text(
                text = "${totalityItems.size}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
              )
            }
          }
        }
      }
    }

    // Navigation buttons - enlarged to prevent text cutoff
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onPrev,
          modifier = Modifier
            .weight(1f)
            .height(56.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Previous", fontSize = 14.sp)
        }

        Button(
          onClick = onNext,
          modifier = Modifier
            .weight(1.5f)
            .height(56.dp)
            .testTag("step3_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Next: Repertorization", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }
      Spacer(modifier = Modifier.height(48.dp))
    }
  }
}

// -------------------------------------------------------------
// STEP 4: REPERTORIZATION & FULL KENT REPERTORY MANUAL LOOKUP
// -------------------------------------------------------------
@Composable
fun Step4RepertorizationView(
  totalityItems: List<CaseTotalityItem>,
  analysis: RepertorizationAnalysis,
  allRubrics: List<KentRubric>,
  chapters: List<String>,
  onAddRubric: (KentRubric, Int) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  var repertorySearchQuery by remember { mutableStateOf("") }
  var selectedChapter by remember { mutableStateOf("All") }
  var showManualKentBrowser by remember { mutableStateOf(false) }

  val filteredRubrics = remember(repertorySearchQuery, selectedChapter, allRubrics) {
    allRubrics.filter { rubric ->
      val matchesChapter = selectedChapter == "All" || rubric.chapter.equals(selectedChapter, ignoreCase = true)
      val matchesQuery = repertorySearchQuery.isBlank() ||
          rubric.rubricName.contains(repertorySearchQuery, ignoreCase = true) ||
          rubric.subRubric.contains(repertorySearchQuery, ignoreCase = true) ||
          rubric.chapter.contains(repertorySearchQuery, ignoreCase = true) ||
          rubric.modality.contains(repertorySearchQuery, ignoreCase = true)
      matchesChapter && matchesQuery
    }
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(horizontal = 18.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Column {
        Text(
          text = "Repertorization & Kent Repertory",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )
        Text(
          text = "Auto rubrics identified from totality symptoms, complete Kent database search, and classical scoring.",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.65f)
        )
      }
    }

    // 1. AUTO RUBRICS FOR EACH SYMPTOM IN TOTALITY
    if (totalityItems.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DACC)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.LocalHospital, contentDescription = null, tint = ClinicalTerracotta, modifier = Modifier.size(20.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Auto Rubrics for Totality Symptoms",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = WarmCharcoal
              )
            }
            Text(
              text = "Kent rubrics automatically matched for each clinical symptom:",
              fontSize = 11.sp,
              color = WarmCharcoal.copy(alpha = 0.6f),
              modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
            )

            totalityItems.forEachIndexed { sIndex, symptom ->
              val symptomText = symptom.rubricName
              val symptomTokens = symptomText.lowercase().split(" ", ",", ";", "-", "/").filter { it.length > 2 }
              val suggestedRubrics = allRubrics.filter { rubric ->
                symptomTokens.any { t ->
                  rubric.rubricName.contains(t, ignoreCase = true) ||
                  rubric.subRubric.contains(t, ignoreCase = true) ||
                  rubric.modality.contains(t, ignoreCase = true)
                }
              }.take(3)

              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 5.dp)
                  .background(LinenSurface, RoundedCornerShape(10.dp))
                  .padding(10.dp)
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text(
                    text = "Symptom ${sIndex + 1}: $symptomText",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalTerracotta
                  )
                  Surface(
                    color = ClinicalTerracotta.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                  ) {
                    Text(
                      text = "Weight: +${symptom.userIntensity}",
                      fontSize = 10.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = ClinicalTerracotta,
                      modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                  }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (suggestedRubrics.isEmpty()) {
                  Text(
                    text = "• Active in repertorization (${symptom.chapter.ifBlank { "Generalities" }})",
                    fontSize = 11.sp,
                    color = WarmCharcoal.copy(alpha = 0.7f)
                  )
                } else {
                  suggestedRubrics.forEach { rubric ->
                    val isAlreadyAdded = totalityItems.any { it.rubricId == rubric.id }
                    Row(
                      modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                      horizontalArrangement = Arrangement.SpaceBetween,
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Column(modifier = Modifier.weight(1f)) {
                        Text(
                          text = rubric.rubricName,
                          fontSize = 11.sp,
                          fontWeight = FontWeight.SemiBold,
                          color = WarmCharcoal
                        )
                        Text(
                          text = "${rubric.chapter} • ${rubric.remedies.size} remedies",
                          fontSize = 10.sp,
                          color = WarmCharcoal.copy(alpha = 0.6f)
                        )
                      }
                      if (isAlreadyAdded) {
                        Text(
                          text = "✓ Included",
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold,
                          color = SageMiasm
                        )
                      } else {
                        Button(
                          onClick = { onAddRubric(rubric, symptom.userIntensity) },
                          colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
                          shape = RoundedCornerShape(6.dp),
                          contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                          modifier = Modifier.height(28.dp)
                        ) {
                          Text("+ Add Rubric", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Top Repertorization Search Bar (Full Kent Database)
    item {
      OutlinedTextField(
        value = repertorySearchQuery,
        onValueChange = { repertorySearchQuery = it },
        placeholder = { Text("Search complete Kent Repertory rubrics...") },
        leadingIcon = {
          Icon(Icons.Default.Search, contentDescription = null, tint = ClinicalTerracotta)
        },
        trailingIcon = {
          if (repertorySearchQuery.isNotBlank()) {
            IconButton(onClick = { repertorySearchQuery = "" }) {
              Icon(Icons.Default.Close, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = ClinicalTerracotta,
          unfocusedBorderColor = Color(0xFFD6CEBE),
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        )
      )
    }

    // Manual Kent Repertory Toggle Button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = { showManualKentBrowser = !showManualKentBrowser },
          colors = ButtonDefaults.buttonColors(
            containerColor = if (showManualKentBrowser) ClinicalTerracotta else WarmCharcoal
          ),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            if (showManualKentBrowser) "Hide Kent Repertory Browser" else "📖 Browse Complete Kent Repertory (74,000+ Rubrics)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Manual Kent Repertory Browser Section (When opened)
    if (showManualKentBrowser) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6CEBE))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Kent Repertory Chapters (Touch rubric to add directly):",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              color = ClinicalTerracotta
            )
            Spacer(modifier = Modifier.height(6.dp))

            // Chapter Chips
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              chapters.forEach { ch ->
                val isSel = selectedChapter == ch
                FilterChip(
                  selected = isSel,
                  onClick = { selectedChapter = ch },
                  label = { Text(ch, fontSize = 11.sp) },
                  colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ClinicalTerracotta,
                    selectedLabelColor = Color.White
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rubrics in this chapter
            Text(
              text = "Rubrics in ${selectedChapter} (${filteredRubrics.size}):",
              fontSize = 11.sp,
              color = WarmCharcoal.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(4.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
              filteredRubrics.take(15).forEach { rubric ->
                val isAdded = totalityItems.any { it.rubricId == rubric.id }
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isAdded) ClinicalTerracotta.copy(alpha = 0.08f) else LinenSurface)
                    .clickable { onAddRubric(rubric, 2) }
                    .padding(8.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = rubric.rubricName,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.SemiBold,
                      color = WarmCharcoal
                    )
                    if (rubric.subRubric.isNotBlank()) {
                      Text(
                        text = rubric.subRubric,
                        fontSize = 10.sp,
                        color = WarmCharcoal.copy(alpha = 0.6f)
                      )
                    }
                  }
                  if (isAdded) {
                    Icon(
                      Icons.Default.CheckCircle,
                      contentDescription = "Added",
                      tint = ClinicalTerracotta,
                      modifier = Modifier.size(18.dp)
                    )
                  } else {
                    Surface(
                      color = ClinicalTerracotta,
                      shape = RoundedCornerShape(6.dp)
                    ) {
                      Text(
                        text = "+ Add",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    // Mathematical Repertorization Matrix & Remedy Ranking
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Repertorization Results & Simillimum Ranking",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = WarmCharcoal
          )
          Text(
            text = "Mathematical sum of rubric weights and Kent remedy marks",
            fontSize = 11.sp,
            color = WarmCharcoal.copy(alpha = 0.6f)
          )

          Spacer(modifier = Modifier.height(12.dp))

          if (analysis.scores.isEmpty()) {
            Text(
              text = "Add rubrics to the case totality to calculate remedies.",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.6f)
            )
          } else {
            // Ranking Table
            analysis.scores.take(6).forEachIndexed { index, score ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Surface(
                    color = if (index == 0) ClinicalTerracotta else LinenSurface,
                    shape = CircleShape,
                    modifier = Modifier.size(24.dp)
                  ) {
                    Box(contentAlignment = Alignment.Center) {
                      Text(
                        text = "${index + 1}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (index == 0) Color.White else WarmCharcoal
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = score.remedyFullName,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
                      color = WarmCharcoal
                    )
                    Text(
                      text = "${score.coverageCount} of ${score.totalRubrics} rubrics covered",
                      fontSize = 11.sp,
                      color = WarmCharcoal.copy(alpha = 0.6f)
                    )
                  }
                }

                Surface(
                  color = KentGrade2Blue.copy(alpha = 0.12f),
                  shape = RoundedCornerShape(8.dp)
                ) {
                  Text(
                    text = "${score.totalScore} Pts",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = KentGrade2Blue,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                  )
                }
              }
              HorizontalDivider(color = Color(0xFFF2ECE1))
            }
          }
        }
      }
    }

    // Navigation buttons - enlarged to prevent text cutoff
    item {
      Spacer(modifier = Modifier.height(12.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        OutlinedButton(
          onClick = onPrev,
          modifier = Modifier
            .weight(1f)
            .height(56.dp),
          contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Previous", fontSize = 14.sp)
        }

        Button(
          onClick = onNext,
          modifier = Modifier
            .weight(1.5f)
            .height(56.dp)
            .testTag("step4_next_button"),
          colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
          contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
          shape = RoundedCornerShape(12.dp)
        ) {
          Text("Next: Prescription", fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }
      Spacer(modifier = Modifier.height(48.dp))
    }
  }
}

// -------------------------------------------------------------
// STEP 5: FINAL PRESCRIPTION
// -------------------------------------------------------------
@Composable
fun Step5PrescriptionView(
  caseInfo: PatientCaseInfo,
  analysis: RepertorizationAnalysis,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit,
  isAcute: Boolean = false,
  onSaveCase: (() -> Unit)? = null
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Final Prescription (Rx)",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )
        Text(
          text = "Select simillimum remedy, potency, dosage frequency, and diet restrictions.",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.65f)
        )

        HorizontalDivider(color = Color(0xFFE8E0D4))

        // Quick Pick from Repertorization Top Remedies
        if (analysis.scores.isNotEmpty()) {
          Text(
            text = "Quick Pick from Leading Repertorized Remedies:",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = ClinicalTerracotta
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            analysis.scores.take(5).forEach { score ->
              val isSel = caseInfo.prescribedRemedy.equals(score.remedyAbbr, ignoreCase = true)
              FilterChip(
                selected = isSel,
                onClick = { onUpdate(caseInfo.copy(prescribedRemedy = score.remedyAbbr)) },
                label = {
                  Text(
                    text = "${score.remedyAbbr} (${score.totalScore} pts)",
                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                  )
                },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = ClinicalTerracotta,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
        }

        // Prescribed Remedy text
        OutlinedTextField(
          value = caseInfo.prescribedRemedy,
          onValueChange = { onUpdate(caseInfo.copy(prescribedRemedy = it)) },
          label = { Text("Prescribed Medicine / Simillimum *") },
          placeholder = { Text("e.g. Nux-v or Bryonia Alba") },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("prescribed_remedy_input"),
          shape = RoundedCornerShape(10.dp)
        )

        // Potency Selector
        Text("Select Potency:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WarmCharcoal)
        val potencies = listOf("Q", "30C", "200C", "1M", "10M", "50M", "CM", "LM-1", "LM-2", "LM-3")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          potencies.forEach { pot ->
            val isSel = caseInfo.prescribedPotency == pot
            FilterChip(
              selected = isSel,
              onClick = { onUpdate(caseInfo.copy(prescribedPotency = pot)) },
              label = { Text(pot, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = KentGrade2Blue,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        // Dosage & Frequency
        OutlinedTextField(
          value = caseInfo.prescribedDosage,
          onValueChange = { onUpdate(caseInfo.copy(prescribedDosage = it)) },
          label = { Text("Dosage & Frequency *") },
          placeholder = { Text("e.g. 4 pills TDS for 3 days or Single dose Stat dry on tongue") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        // Diet & Regimen guidelines
        OutlinedTextField(
          value = caseInfo.prescribedDiet,
          onValueChange = { onUpdate(caseInfo.copy(prescribedDiet = it)) },
          label = { Text("Diet & Regimen Instructions") },
          placeholder = { Text("e.g. Avoid raw onion, garlic, coffee, camphor, strong spices") },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp)
        )

        if (isAcute) {
          HorizontalDivider(color = Color(0xFFE8E0D4))
          Text(
            text = "Follow-up Schedule & Patient Instructions",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = WarmCharcoal
          )
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = caseInfo.followUpDuration,
              onValueChange = { onUpdate(caseInfo.copy(followUpDuration = it)) },
              label = { Text("Follow-up Review") },
              placeholder = { Text("After 24-48 hrs / 3 days") },
              singleLine = true,
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
              value = caseInfo.followUpNotes,
              onValueChange = { onUpdate(caseInfo.copy(followUpNotes = it)) },
              label = { Text("Observations / Advice") },
              placeholder = { Text("Report if fever rises, warm sips...") },
              singleLine = true,
              modifier = Modifier.weight(1.5f),
              shape = RoundedCornerShape(10.dp)
            )
          }
        }
      }
    }

    // Navigation buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onPrev,
        modifier = Modifier
          .weight(1f)
          .height(56.dp),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text("Previous", fontSize = 14.sp)
      }

      Button(
        onClick = {
          if (isAcute) onSaveCase?.invoke() ?: onNext()
          else onNext()
        },
        modifier = Modifier
          .weight(1.5f)
          .height(56.dp)
          .testTag("step5_next_button"),
        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(
          text = if (isAcute) "💾 Save Case to Library" else "Next: Follow Up",
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp
        )
        if (!isAcute) {
          Spacer(modifier = Modifier.width(6.dp))
          Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
      }
    }

    Spacer(modifier = Modifier.height(48.dp))
  }
}

// -------------------------------------------------------------
// STEP 6: FOLLOW UP & SAVE CASE
// -------------------------------------------------------------
@Composable
fun Step6FollowUpView(
  caseInfo: PatientCaseInfo,
  totalityItems: List<CaseTotalityItem>,
  analysis: RepertorizationAnalysis,
  onUpdate: (PatientCaseInfo) -> Unit,
  onSaveCase: () -> Unit,
  onPrev: () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        Text(
          text = "Follow Up & Case Summary",
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )
        Text(
          text = "Specify follow-up interval and observation notes before saving case to practice records.",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.65f)
        )

        HorizontalDivider(color = Color(0xFFE8E0D4))

        // Follow up duration chips
        Text("Follow Up Review After:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = WarmCharcoal)
        val intervals = listOf("After 3 Days", "After 7 Days", "After 15 Days", "After 1 Month")
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          intervals.forEach { interval ->
            val isSel = caseInfo.followUpDuration == interval
            FilterChip(
              selected = isSel,
              onClick = { onUpdate(caseInfo.copy(followUpDuration = interval)) },
              label = { Text(interval, fontSize = 11.sp) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ClinicalTerracotta,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        // Follow up observations / instructions
        OutlinedTextField(
          value = caseInfo.followUpNotes,
          onValueChange = { onUpdate(caseInfo.copy(followUpNotes = it)) },
          label = { Text("Follow Up Instructions & Observations") },
          placeholder = { Text("e.g. In case of acute aggravation, do not repeat medicine. If fever subsides and thirst returns to normal, report via WhatsApp.") },
          minLines = 3,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("follow_up_notes_input"),
          shape = RoundedCornerShape(10.dp)
        )

        // Case Review Summary Card
        Surface(
          color = LinenSurface,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "CASE SUMMARY TO SAVE:",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = ClinicalTerracotta,
              letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "${caseInfo.name.ifBlank { "Patient" }} (${caseInfo.age.ifBlank { "30" }}y / ${caseInfo.gender}) • ${caseInfo.caseType} Case",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = WarmCharcoal
            )
            Text(
              text = "Rx: ${caseInfo.prescribedRemedy.ifBlank { "Simillimum" }} ${caseInfo.prescribedPotency} (${caseInfo.prescribedDosage})",
              fontSize = 12.sp,
              color = KentGrade2Blue,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "Totality: ${totalityItems.size} Kent rubrics repertorized",
              fontSize = 11.sp,
              color = WarmCharcoal.copy(alpha = 0.6f)
            )
          }
        }
      }
    }

    // Save Case & Complete Button (Directly returns to main dashboard as requested!)
    Button(
      onClick = onSaveCase,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp)
        .testTag("save_case_button"),
      colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
      contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
      shape = RoundedCornerShape(14.dp)
    ) {
      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
      Spacer(modifier = Modifier.width(8.dp))
      Text("Save Case & Return to Main Screen", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }

    OutlinedButton(
      onClick = onPrev,
      modifier = Modifier
        .fillMaxWidth()
        .height(56.dp),
      contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
      shape = RoundedCornerShape(12.dp)
    ) {
      Text("Back to Prescription", fontSize = 14.sp)
    }

    Spacer(modifier = Modifier.height(48.dp))
  }
}

// -------------------------------------------------------------
// COMPONENT: KENT RUBRIC CARD WITH GRADES & MIASMS
// -------------------------------------------------------------
@Composable
fun KentRubricCard(
  rubric: KentRubric,
  isAdded: Boolean,
  onAdd: (Int) -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (isAdded) ClinicalTerracotta.copy(alpha = 0.05f) else Color.White
    ),
    border = androidx.compose.foundation.BorderStroke(
      width = 1.dp,
      color = if (isAdded) ClinicalTerracotta.copy(alpha = 0.4f) else Color(0xFFE6DEC8)
    ),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = modifier.fillMaxWidth()
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = ClinicalTerracotta.copy(alpha = 0.12f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = rubric.chapter.uppercase(),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = ClinicalTerracotta,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }

        Surface(
          color = SageMiasm.copy(alpha = 0.15f),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = rubric.miasm,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SageMiasm,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = rubric.rubricName,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        color = WarmCharcoal
      )

      if (rubric.subRubric.isNotBlank()) {
        Text(
          text = rubric.subRubric,
          fontSize = 11.sp,
          color = WarmCharcoal.copy(alpha = 0.7f),
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      if (rubric.modality.isNotBlank()) {
        Text(
          text = "Modalities: ${rubric.modality}",
          fontSize = 10.sp,
          color = ClinicalTerracotta,
          fontWeight = FontWeight.Medium,
          modifier = Modifier.padding(top = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Top remedies with classical grades
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        rubric.remedies.take(6).forEach { rem ->
          val gradeColor = when (rem.grade) {
            3 -> KentGrade3Red
            2 -> KentGrade2Blue
            else -> KentGrade1Slate
          }
          Surface(
            color = gradeColor.copy(alpha = 0.12f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "${rem.remedyAbbr} (${rem.grade})",
              fontSize = 10.sp,
              fontWeight = if (rem.grade >= 2) FontWeight.Bold else FontWeight.Normal,
              color = gradeColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Action: Add with Intensity Selector or Remove
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        if (isAdded) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = ClinicalTerracotta, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("In Totality", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ClinicalTerracotta)
          }

          OutlinedButton(
            onClick = onRemove,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ClinicalTerracotta),
            modifier = Modifier.height(32.dp)
          ) {
            Text("Remove", fontSize = 11.sp)
          }
        } else {
          Text("Add with intensity:", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.6f))
          Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf(1, 2, 3).forEach { grade ->
              val gradeColor = when (grade) {
                3 -> KentGrade3Red
                2 -> KentGrade2Blue
                else -> KentGrade1Slate
              }
              Surface(
                color = gradeColor,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .clickable { onAdd(grade) }
                  .size(32.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Text(
                    text = "+$grade",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
            }
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------
// COMPONENT: TOTALITY ROW WITH INTENSITY SELECTOR
// -------------------------------------------------------------
@Composable
fun TotalityItemRow(
  item: CaseTotalityItem,
  onIntensityChange: (Int) -> Unit,
  onRemove: () -> Unit
) {
  Surface(
    color = Color.White,
    shape = RoundedCornerShape(10.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2DACC)),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 10.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = item.rubricName,
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = WarmCharcoal,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )
        Text(
          text = "${item.chapter} • ${item.miasm}",
          fontSize = 10.sp,
          color = WarmCharcoal.copy(alpha = 0.6f)
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Intensity pills
        listOf(1, 2, 3).forEach { g ->
          val isSel = item.userIntensity == g
          Surface(
            color = if (isSel) ClinicalTerracotta else LinenSurface,
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
              .padding(horizontal = 2.dp)
              .clickable { onIntensityChange(g) }
              .size(24.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "$g",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSel) Color.White else WarmCharcoal
              )
            }
          }
        }

        IconButton(
          onClick = onRemove,
          modifier = Modifier.size(28.dp)
        ) {
          Icon(
            Icons.Default.Delete,
            contentDescription = "Delete",
            tint = ClinicalTerracotta.copy(alpha = 0.7f),
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
