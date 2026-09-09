package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Male
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ClinicalTerracotta
import com.example.ui.theme.SageMiasm
import com.example.ui.theme.WarmCharcoal
import com.example.ui.viewmodel.PatientCaseInfo

/**
 * Standard Step Container with Header, Card and Bottom Back / Continue navigation
 */
@Composable
fun StepContainer(
  stepNumber: Int,
  totalSteps: Int,
  stepTitle: String,
  stepDescription: String,
  nextButtonLabel: String,
  onNext: () -> Unit,
  onPrev: () -> Unit,
  primaryColor: Color = ClinicalTerracotta,
  content: @Composable () -> Unit
) {
  val scrollState = rememberScrollState()

  Column(
    modifier = Modifier
      .fillMaxSize()
      .verticalScroll(scrollState)
      .padding(horizontal = 16.dp, vertical = 14.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
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
        // Step Badge & Title
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Surface(
            color = primaryColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = "STEP $stepNumber OF $totalSteps",
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = primaryColor,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
          }
        }

        Text(
          text = stepTitle,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Serif,
          color = WarmCharcoal
        )

        Text(
          text = stepDescription,
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.7f),
          lineHeight = 16.sp
        )

        HorizontalDivider(color = Color(0xFFEFE8DE))

        content()
      }
    }

    // Bottom Navigation Buttons
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 24.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      OutlinedButton(
        onClick = onPrev,
        modifier = Modifier
          .weight(1f)
          .height(48.dp)
          .testTag("step_back_button"),
        shape = RoundedCornerShape(12.dp)
      ) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Back", fontWeight = FontWeight.SemiBold)
      }

      Button(
        onClick = onNext,
        modifier = Modifier
          .weight(2f)
          .height(48.dp)
          .testTag("step_continue_button"),
        colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
        shape = RoundedCornerShape(12.dp)
      ) {
        Text(nextButtonLabel, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 1: PATIENT IDENTIFICATION
// -------------------------------------------------------------
@Composable
fun PatientIdentificationStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Patient Identification",
    stepDescription = "Record core demographic identification data for clinic registration.",
    nextButtonLabel = "Continue: Chief Complaint",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Name
    OutlinedTextField(
      value = caseInfo.name,
      onValueChange = { onUpdate(caseInfo.copy(name = it)) },
      label = { Text("Patient Full Name *") },
      placeholder = { Text("e.g. Meera Sharma / Rajesh Patel") },
      singleLine = true,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("patient_name_field"),
      shape = RoundedCornerShape(10.dp)
    )

    // Age & Gender
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = caseInfo.age,
        onValueChange = { onUpdate(caseInfo.copy(age = it)) },
        label = { Text("Age (Yrs) *") },
        placeholder = { Text("28") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )

      Column(modifier = Modifier.weight(1.5f)) {
        Text(
          text = "Gender *",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = WarmCharcoal.copy(alpha = 0.8f)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          listOf("Female", "Male", "Child").forEach { g ->
            val isSel = caseInfo.gender == g
            FilterChip(
              selected = isSel,
              onClick = { onUpdate(caseInfo.copy(gender = g)) },
              label = { Text(g, fontSize = 11.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = primaryColor,
                selectedLabelColor = Color.White
              ),
              shape = RoundedCornerShape(8.dp)
            )
          }
        }
      }
    }

    // Phone & Address
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
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
        label = { Text("City / Address") },
        placeholder = { Text("Ahmedabad / Surat") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )
    }

    // Chronic specific fields: Occupation & Marital Status
    if (isChronic) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = caseInfo.occupation,
          onValueChange = { onUpdate(caseInfo.copy(occupation = it)) },
          label = { Text("Occupation") },
          placeholder = { Text("Teacher, Software Engineer, Homemaker") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = caseInfo.maritalStatus,
          onValueChange = { onUpdate(caseInfo.copy(maritalStatus = it)) },
          label = { Text("Marital Status") },
          placeholder = { Text("Married / Single") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 2: CHIEF COMPLAINT
// -------------------------------------------------------------
@Composable
fun ChiefComplaintStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Chief Complaint",
    stepDescription = "Record the patient's primary presenting suffering in their own spontaneous words.",
    nextButtonLabel = "Continue: History of Present Illness",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.complaint,
      onValueChange = { onUpdate(caseInfo.copy(complaint = it)) },
      label = { Text("Chief Complaint (In Patient's Own Words) *") },
      placeholder = {
        Text(
          if (isChronic) "e.g. Chronic migraine attacks for 3 years, recurring every weekend with intense vomiting and visual blurring..."
          else "e.g. Sudden severe fever with chills, dry cough and violent headache for 18 hours after cold wind exposure..."
        )
      },
      minLines = 4,
      maxLines = 7,
      modifier = Modifier
        .fillMaxWidth()
        .testTag("patient_complaint_field"),
      shape = RoundedCornerShape(10.dp)
    )

    // Quick homeopathic tips
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
      shape = RoundedCornerShape(10.dp)
    ) {
      Row(
        modifier = Modifier.padding(12.dp),
        verticalAlignment = Alignment.Top
      ) {
        Icon(Icons.Default.Info, contentDescription = null, tint = primaryColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Hahnemannian Guidance (§84 Organon): Allow the patient to explain their sufferings uninterruptedly without interrupting their train of thoughts.",
          fontSize = 11.sp,
          color = WarmCharcoal.copy(alpha = 0.75f),
          lineHeight = 15.sp
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 3: HISTORY OF PRESENT ILLNESS (HPI)
// -------------------------------------------------------------
@Composable
fun HpiStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  val hpiValue = if (isChronic) caseInfo.chronicHpi else caseInfo.acuteHpi
  val onHpiChanged: (String) -> Unit = { updated ->
    if (isChronic) onUpdate(caseInfo.copy(chronicHpi = updated))
    else onUpdate(caseInfo.copy(acuteHpi = updated))
  }

  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = if (isChronic) "History of Present Illness (Chronic)" else "History of Present Illness (Acute)",
    stepDescription = if (isChronic)
      "Chronological progression of chronic complaints, remissions, exacerbations and past treatments."
    else
      "Onset mode (sudden vs gradual), duration (hours/days), progression and immediate circumstances.",
    nextButtonLabel = if (isChronic) "Continue: Past History" else "Continue: Etiology",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = hpiValue,
      onValueChange = onHpiChanged,
      label = { Text("Detailed History of Present Illness *") },
      placeholder = {
        Text(
          if (isChronic) "Initial origin, chronological course, treatments taken, recurrence patterns..."
          else "Sudden/gradual onset, exact time of appearance, rapid intensity climb, current stage..."
        )
      },
      minLines = 4,
      maxLines = 8,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Quick symptom tags
    Text(
      text = "Quick Clinical Tags:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = WarmCharcoal.copy(alpha = 0.7f)
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      val tags = if (isChronic) {
        listOf("Gradual insidious onset", "Frequent relapses", "Suppressed by allopathic drugs", "Worse in winter", "Worse with stress")
      } else {
        listOf("Sudden & violent onset (Acon/Bell)", "Gradual insidious onset (Bry/Gels)", "Onset after midnight", "Rapidly worsening")
      }
      tags.forEach { tag ->
        Surface(
          color = Color(0xFFF0EBE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable {
            val current = hpiValue
            val appended = if (current.isBlank()) tag else "$current; $tag"
            onHpiChanged(appended)
          }
        ) {
          Text(
            text = "+ $tag",
            fontSize = 11.sp,
            color = WarmCharcoal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 4 (ACUTE): ETIOLOGY (EXCITING CAUSE)
// -------------------------------------------------------------
@Composable
fun AcuteEtiologyStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Etiology (Exciting Cause)",
    stepDescription = "Causa occasionalis: Identifying the specific exciting or precipitating cause points directly to the acute simillimum.",
    nextButtonLabel = "Continue: Particulars (LSMC)",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.acuteCause,
      onValueChange = { onUpdate(caseInfo.copy(acuteCause = it)) },
      label = { Text("Exciting Cause / Ailments From *") },
      placeholder = { Text("e.g. Exposure to dry cold wind, getting wet in rain, anger, bad news...") },
      minLines = 3,
      maxLines = 5,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Homeopathic Keynote Etiologies (Tap to add):",
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      color = primaryColor
    )

    val etiologies = listOf(
      "Exposure to dry cold wind (Aconite)",
      "Getting wet in rain / damp weather (Rhus-tox)",
      "Emotional shock / fright / bad news (Gelsemium)",
      "Anger / vexation / mortification (Chamomilla, Staph)",
      "Overeating rich food / alcohol (Nux-vomica)",
      "Physical overexertion / fall / injury (Arnica)",
      "Sun exposure / overheating (Glonoinum, Belladonna)",
      "Suppressed perspiration / drafts of air (Dulcamara)",
      "Loss of vital fluids / sleeplessness (China, Cocc)"
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      etiologies.forEach { cause ->
        val isAdded = caseInfo.acuteCause.contains(cause)
        Surface(
          color = if (isAdded) primaryColor.copy(alpha = 0.2f) else Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          border = if (isAdded) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null,
          modifier = Modifier.clickable {
            val current = caseInfo.acuteCause
            val updated = if (isAdded) current.replace(cause, "").trim().trim(';', ',')
            else if (current.isBlank()) cause else "$current; $cause"
            onUpdate(caseInfo.copy(acuteCause = updated))
          }
        ) {
          Text(
            text = if (isAdded) "✓ $cause" else "+ $cause",
            fontSize = 11.sp,
            fontWeight = if (isAdded) FontWeight.Bold else FontWeight.Normal,
            color = if (isAdded) primaryColor else WarmCharcoal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 4 (CHRONIC): PAST HISTORY (CHILDHOOD, SUPPRESSIONS, SURGERIES)
// -------------------------------------------------------------
@Composable
fun ChronicPastHistoryStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Past History & Suppressions",
    stepDescription = "Detailed history of childhood ailments, suppressed skin eruptions, suppressed discharges, and surgeries.",
    nextButtonLabel = "Continue: Family History",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.chronicPastHistory,
      onValueChange = { onUpdate(caseInfo.copy(chronicPastHistory = it)) },
      label = { Text("Past Medical History & Suppressions *") },
      placeholder = { Text("Childhood eczema, recurrent tonsillitis, suppressed discharges, typhoid, jaundice, surgeries, injuries...") },
      minLines = 4,
      maxLines = 8,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Common Suppressive & Landmark Events:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = WarmCharcoal.copy(alpha = 0.8f)
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(
        "Suppressed skin eruption by steroid ointment",
        "Suppressed foot sweat (Silica)",
        "Suppressed catarrh / nasal polyp surgery",
        "Recurrent childhood tonsillitis / adenoids",
        "History of measles / chickenpox / mumps",
        "Major physical trauma / head injury",
        "Treated with recurrent broad spectrum antibiotics"
      ).forEach { tag ->
        Surface(
          color = Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable {
            val current = caseInfo.chronicPastHistory
            val updated = if (current.isBlank()) tag else "$current; $tag"
            onUpdate(caseInfo.copy(chronicPastHistory = updated))
          }
        ) {
          Text("+ $tag", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 5 (CHRONIC): FAMILY HISTORY
// -------------------------------------------------------------
@Composable
fun ChronicFamilyHistoryStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Family History",
    stepDescription = "Hereditary diathesis across paternal & maternal lineage reveals chronic miasmatic susceptibility.",
    nextButtonLabel = "Continue: Personal History",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.chronicFamilyHistory,
      onValueChange = { onUpdate(caseInfo.copy(chronicFamilyHistory = it)) },
      label = { Text("Family History (Maternal & Paternal Lineage) *") },
      placeholder = { Text("Father: Hypertension, Type 2 Diabetes; Mother: Bronchial Asthma, Rheumatoid Arthritis; Grandparents...") },
      minLines = 4,
      maxLines = 7,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Hereditary Diathesis Tags:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = primaryColor
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(
        "Diabetes Mellitus (Sycotic)",
        "Hypertension / Cardiovascular (Syphilitic)",
        "Bronchial Asthma / Allergies (Tubercular)",
        "Tuberculosis in lineage (Tubercular)",
        "Carcinoma / Cancer diathesis (Mixed)",
        "Autoimmune disorders",
        "Psychiatric illness / Depression in family"
      ).forEach { tag ->
        Surface(
          color = Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable {
            val current = caseInfo.chronicFamilyHistory
            val updated = if (current.isBlank()) tag else "$current; $tag"
            onUpdate(caseInfo.copy(chronicFamilyHistory = updated))
          }
        ) {
          Text("+ $tag", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 6 (CHRONIC): PERSONAL HISTORY
// -------------------------------------------------------------
@Composable
fun ChronicPersonalHistoryStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Personal History",
    stepDescription = "Dietary habits, lifestyle rhythm, bowel/bladder routine, sleep quality and stress factors.",
    nextButtonLabel = "Continue: Mental Generals",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.chronicPersonalHistory,
      onValueChange = { onUpdate(caseInfo.copy(chronicPersonalHistory = it)) },
      label = { Text("Personal History & Habits *") },
      placeholder = { Text("Dietary patterns, tea/coffee intake, alcohol, tobacco/smoking, sleep schedule, stress, exercise routine...") },
      minLines = 4,
      maxLines = 7,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Lifestyle & Habit Chips:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = WarmCharcoal.copy(alpha = 0.8f)
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(
        "Strict Vegetarian",
        "Excess Tea / Coffee (>4 cups)",
        "Tobacco chewer / Smoker",
        "Irregular sleep (Late night screen)",
        "Sedentary desk lifestyle",
        "High mental stress / Work anxiety",
        "Disturbed bowel regularity"
      ).forEach { tag ->
        Surface(
          color = Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable {
            val current = caseInfo.chronicPersonalHistory
            val updated = if (current.isBlank()) tag else "$current; $tag"
            onUpdate(caseInfo.copy(chronicPersonalHistory = updated))
          }
        ) {
          Text("+ $tag", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------
// PARTICULAR SYMPTOMS (LSMC) - USED IN ACUTE (STEP 5) & CHRONIC (STEP 9)
// -------------------------------------------------------------
@Composable
fun ParticularsLsmcStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  val loc = if (isChronic) caseInfo.chronicLocation else caseInfo.acuteLocation
  val sens = if (isChronic) caseInfo.chronicSensation else caseInfo.acuteSensation
  val mods = if (isChronic) caseInfo.chronicModalities else caseInfo.acuteModalities
  val concom = if (isChronic) caseInfo.chronicConcomitants else caseInfo.acuteConcomitants

  val nextLabel = if (isChronic) "Continue: Female History" else "Continue: Acute Mentals"

  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Particular Symptoms (LSMC)",
    stepDescription = "Boenninghausen's Complete Symptom: Location, Sensation, Modality (Aggravation < & Amelioration >), and Concomitants.",
    nextButtonLabel = nextLabel,
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Location
    OutlinedTextField(
      value = loc,
      onValueChange = { updated ->
        if (isChronic) onUpdate(caseInfo.copy(chronicLocation = updated))
        else onUpdate(caseInfo.copy(acuteLocation = updated))
      },
      label = { Text("Location & Extension") },
      placeholder = { Text("e.g. Right throat extending to right ear; Occiput radiating to forehead") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Sensation
    OutlinedTextField(
      value = sens,
      onValueChange = { updated ->
        if (isChronic) onUpdate(caseInfo.copy(chronicSensation = updated))
        else onUpdate(caseInfo.copy(acuteSensation = updated))
      },
      label = { Text("Sensation & Character") },
      placeholder = { Text("e.g. Throbbing, stitching, burning, cutting, heavy bruised feeling, bursting") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Modalities with < and > chips
    OutlinedTextField(
      value = mods,
      onValueChange = { updated ->
        if (isChronic) onUpdate(caseInfo.copy(chronicModalities = updated))
        else onUpdate(caseInfo.copy(acuteModalities = updated))
      },
      label = { Text("Modalities (Aggravation < & Amelioration >) *") },
      placeholder = { Text("e.g. < Motion, < Cold air, < Night 3 AM; > Rest, > Warm sips, > Pressure") },
      minLines = 2,
      maxLines = 4,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Modality quick chips
    Text(
      text = "Quick Modality Chips:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = primaryColor
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(
        "< Motion (Bryonia)",
        "> Continuous motion (Rhus-t)",
        "< Cold air / draft (Hep, Sil)",
        "> Warm drinks (Ars)",
        "< Swallowing empty (Lach)",
        "> Hard pressure (Mag-p, Bry)",
        "< Night 2-3 AM (Kali-c)",
        "> Open air (Puls)"
      ).forEach { modChip ->
        Surface(
          color = Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable {
            val current = mods
            val updated = if (current.isBlank()) modChip else "$current; $modChip"
            if (isChronic) onUpdate(caseInfo.copy(chronicModalities = updated))
            else onUpdate(caseInfo.copy(acuteModalities = updated))
          }
        ) {
          Text("+ $modChip", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }
    }

    // Concomitants
    OutlinedTextField(
      value = concom,
      onValueChange = { updated ->
        if (isChronic) onUpdate(caseInfo.copy(chronicConcomitants = updated))
        else onUpdate(caseInfo.copy(acuteConcomitants = updated))
      },
      label = { Text("Concomitant Symptoms") },
      placeholder = { Text("e.g. Headache accompanied by nausea and yawning; Fever with violent shivering") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )
  }
}

// -------------------------------------------------------------
// STEP 6 (ACUTE): ACUTE MENTAL SYMPTOMS
// -------------------------------------------------------------
@Composable
fun AcuteMentalsStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Acute Mental Symptoms",
    stepDescription = "The mental and emotional state altered during the acute attack carries paramount prescribing value (§213 Organon).",
    nextButtonLabel = "Continue: Physical Generals",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = caseInfo.acuteMentalSymptoms,
      onValueChange = { onUpdate(caseInfo.copy(acuteMentalSymptoms = it)) },
      label = { Text("Acute Mental & Emotional State *") },
      placeholder = { Text("e.g. Agonizing restlessness with fear of death; or heavy drowsiness and irritability...") },
      minLines = 3,
      maxLines = 6,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Keynote Acute Mental States:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = primaryColor
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      listOf(
        "Agonizing restlessness & fear of death (Aconite)",
        "Anxious restlessness, tosses in bed (Arsenic)",
        "Dullness, drowsiness & wants to lie quiet (Gelsemium)",
        "Extreme irritability, snappish, cannot bear pain (Chamomilla)",
        "Mild, weeping & seeks solace / sympathy (Pulsatilla)",
        "Wants to be completely undisturbed (Bryonia)",
        "Delirium, wild look, flushed face (Belladonna)"
      ).forEach { state ->
        val isAdded = caseInfo.acuteMentalSymptoms.contains(state)
        Surface(
          color = if (isAdded) primaryColor.copy(alpha = 0.2f) else Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          border = if (isAdded) androidx.compose.foundation.BorderStroke(1.dp, primaryColor) else null,
          modifier = Modifier.clickable {
            val current = caseInfo.acuteMentalSymptoms
            val updated = if (isAdded) current.replace(state, "").trim().trim(';', ',')
            else if (current.isBlank()) state else "$current; $state"
            onUpdate(caseInfo.copy(acuteMentalSymptoms = updated))
          }
        ) {
          Text(
            text = if (isAdded) "✓ $state" else "+ $state",
            fontSize = 11.sp,
            color = if (isAdded) primaryColor else WarmCharcoal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 7 (CHRONIC): MENTAL GENERALS
// -------------------------------------------------------------
@Composable
fun ChronicMentalsStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Mental Generals",
    stepDescription = "In chronic constitutional prescribing, the mental generals rank highest in Kent's hierarchy of symptoms.",
    nextButtonLabel = "Continue: Physical Generals",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Disposition
    OutlinedTextField(
      value = caseInfo.chronicMindDisposition,
      onValueChange = { onUpdate(caseInfo.copy(chronicMindDisposition = it)) },
      label = { Text("Temperament & Disposition *") },
      placeholder = { Text("e.g. Fastidious, perfectionist, hurried, reserved, suspicious, mild and yielding, hot-tempered...") },
      minLines = 2,
      maxLines = 4,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Emotional Triggers
    OutlinedTextField(
      value = caseInfo.chronicEmotionalTriggers,
      onValueChange = { onUpdate(caseInfo.copy(chronicEmotionalTriggers = it)) },
      label = { Text("Emotional Triggers & Ailments From") },
      placeholder = { Text("e.g. Prolonged grief, mortification, disappointment in love, financial worry, suppressed anger...") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Fears
    OutlinedTextField(
      value = caseInfo.chronicFears,
      onValueChange = { onUpdate(caseInfo.copy(chronicFears = it)) },
      label = { Text("Fears & Phobias") },
      placeholder = { Text("e.g. Fear of darkness, being alone, cancer/disease, thunderstorms, high places, crowds, dogs...") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Consolation
    Text(
      text = "Reaction to Consolation & Sympathy:",
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      color = WarmCharcoal.copy(alpha = 0.8f)
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf(
        "Consolation Aggravates (Nat-m, Sep, Ign)",
        "Consolation Ameliorates (Puls, Phos)",
        "Indifferent"
      ).forEach { option ->
        val isSel = caseInfo.chronicConsolation == option
        FilterChip(
          selected = isSel,
          onClick = { onUpdate(caseInfo.copy(chronicConsolation = option)) },
          label = { Text(option, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primaryColor,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 7 (ACUTE): PHYSICAL GENERALS (THERMAL, THIRST, TONGUE, SWEAT)
// -------------------------------------------------------------
@Composable
fun AcuteGeneralsStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Physical Generals",
    stepDescription = "Acute thermal state, thirst pattern, tongue coating, perspiration and physical distress.",
    nextButtonLabel = "Continue: Clinical Examination",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Thermal
    Text("Acute Thermal Reaction *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf("Chilly (wants wrap up)", "Hot (throws off covers)", "Neutral / Ambithermal").forEach { therm ->
        val isSel = caseInfo.acuteThermal == therm
        FilterChip(
          selected = isSel,
          onClick = { onUpdate(caseInfo.copy(acuteThermal = therm)) },
          label = { Text(therm, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primaryColor,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    }

    // Thirst
    Text("Acute Thirst Pattern *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf(
        "Thirstless (Puls, Apis, Gels)",
        "Small sips frequently (Arsenic)",
        "Large quantities long intervals (Bryonia)",
        "Craves ice cold (Phos)"
      ).forEach { thirst ->
        val isSel = caseInfo.acuteThirst == thirst
        FilterChip(
          selected = isSel,
          onClick = { onUpdate(caseInfo.copy(acuteThirst = thirst)) },
          label = { Text(thirst, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primaryColor,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    }

    // Tongue
    OutlinedTextField(
      value = caseInfo.acuteTongue,
      onValueChange = { onUpdate(caseInfo.copy(acuteTongue = it)) },
      label = { Text("Tongue Appearance") },
      placeholder = { Text("e.g. Red triangular tip (Rhus-t), White coated with tooth imprints (Merc), Dry mapped...") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Sweat & Sleep
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = caseInfo.acutePerspiration,
        onValueChange = { onUpdate(caseInfo.copy(acutePerspiration = it)) },
        label = { Text("Perspiration") },
        placeholder = { Text("Hot profuse sweat, cold clammy...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = caseInfo.acuteSleep,
        onValueChange = { onUpdate(caseInfo.copy(acuteSleep = it)) },
        label = { Text("Sleep & Position") },
        placeholder = { Text("Restless, sleepless from cough...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )
    }
  }
}

// -------------------------------------------------------------
// STEP 8 (CHRONIC): PHYSICAL GENERALS (THERMAL, THIRST, CRAVINGS, AVERSIONS, DIGESTION, SLEEP)
// -------------------------------------------------------------
@Composable
fun ChronicGeneralsStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Physical Generals",
    stepDescription = "Constitutional thermal tolerance, thirst, cravings, aversions, bowel tendencies and sleep architecture.",
    nextButtonLabel = "Continue: Particulars (LSMC)",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Thermal
    Text("Constitutional Thermal State *", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      listOf("Chilly (Cannot tolerate cold)", "Hot (Cannot tolerate warmth/stuffiness)", "Ambithermal").forEach { therm ->
        val isSel = caseInfo.chronicThermal == therm
        FilterChip(
          selected = isSel,
          onClick = { onUpdate(caseInfo.copy(chronicThermal = therm)) },
          label = { Text(therm, fontSize = 11.sp) },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = primaryColor,
            selectedLabelColor = Color.White
          ),
          shape = RoundedCornerShape(8.dp)
        )
      }
    }

    // Thirst
    OutlinedTextField(
      value = caseInfo.chronicThirst,
      onValueChange = { onUpdate(caseInfo.copy(chronicThirst = it)) },
      label = { Text("Thirst Characteristics") },
      placeholder = { Text("e.g. Thirstless, drinks 3-4 liters daily, craves cold water, warm tea only...") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Cravings & Aversions
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = caseInfo.chronicCravings,
        onValueChange = { onUpdate(caseInfo.copy(chronicCravings = it)) },
        label = { Text("Food Cravings") },
        placeholder = { Text("Sweets, Salt, Sour, Spicy, Fat, Cold milk, Eggs...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = caseInfo.chronicAversions,
        onValueChange = { onUpdate(caseInfo.copy(chronicAversions = it)) },
        label = { Text("Food Aversions") },
        placeholder = { Text("Meat, Milk, Bread, Fat, Sweets...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )
    }

    // Bowels & Perspiration
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
      OutlinedTextField(
        value = caseInfo.chronicBowels,
        onValueChange = { onUpdate(caseInfo.copy(chronicBowels = it)) },
        label = { Text("Bowels / Digestion") },
        placeholder = { Text("Habitual constipation, morning diarrhea...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )

      OutlinedTextField(
        value = caseInfo.chronicPerspiration,
        onValueChange = { onUpdate(caseInfo.copy(chronicPerspiration = it)) },
        label = { Text("Perspiration & Odor") },
        placeholder = { Text("Profuse on palms/soles, sour odor...") },
        singleLine = true,
        modifier = Modifier.weight(1f),
        shape = RoundedCornerShape(10.dp)
      )
    }

    // Sleep
    OutlinedTextField(
      value = caseInfo.chronicSleep,
      onValueChange = { onUpdate(caseInfo.copy(chronicSleep = it)) },
      label = { Text("Sleep Pattern & Recurring Dreams") },
      placeholder = { Text("Wakes 3 AM regularly, sleeps on abdomen, dreams of falling / snakes...") },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )
  }
}

// -------------------------------------------------------------
// STEP 10 (CHRONIC): FEMALE HISTORY (IF APPLICABLE)
// -------------------------------------------------------------
@Composable
fun ChronicFemaleHistoryStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Female History",
    stepDescription = "Menarche, menstrual cycle periodicity, flow characteristics, dysmenorrhea and obstetric history.",
    nextButtonLabel = "Continue: Clinical Examination",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Applicable Switch
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFFF6F0E6), RoundedCornerShape(10.dp))
        .padding(horizontal = 14.dp, vertical = 8.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        Text("Applicable to Patient", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WarmCharcoal)
        Text(
          text = if (caseInfo.isFemaleHistoryApplicable) "Active recording for female patient" else "Not applicable (Male patient or child)",
          fontSize = 11.sp,
          color = WarmCharcoal.copy(alpha = 0.6f)
        )
      }

      Switch(
        checked = caseInfo.isFemaleHistoryApplicable,
        onCheckedChange = { onUpdate(caseInfo.copy(isFemaleHistoryApplicable = it)) },
        colors = SwitchDefaults.colors(checkedThumbColor = primaryColor, checkedTrackColor = primaryColor.copy(alpha = 0.3f))
      )
    }

    if (caseInfo.isFemaleHistoryApplicable) {
      OutlinedTextField(
        value = caseInfo.chronicFemaleHistory,
        onValueChange = { onUpdate(caseInfo.copy(chronicFemaleHistory = it)) },
        label = { Text("Menstrual & Obstetric History *") },
        placeholder = {
          Text("Cycle length (28 days / irregular), duration, flow (profuse/scanty, dark, clotted, acrid), dysmenorrhea (< before/during flow), leucorrhea, pregnancies/abortions, menopause symptoms...")
        },
        minLines = 4,
        maxLines = 7,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Text(
        text = "Key Menstrual Rubric Modalities:",
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = primaryColor
      )

      @OptIn(ExperimentalLayoutApi::class)
      FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        listOf(
          "Menses delayed, scanty, suppressed (Puls, Sep)",
          "Menses profuse, too early, dark clotted (Cham, Sabina)",
          "Violent dysmenorrhea > warmth / pressure (Mag-p)",
          "Acrid, excoriating leucorrhea (Kreosote)",
          "Complaints worse before menses",
          "Hot flushes of climacteric (Lachesis)"
        ).forEach { tag ->
          Surface(
            color = Color(0xFFF3ECE1),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.clickable {
              val current = caseInfo.chronicFemaleHistory
              val updated = if (current.isBlank()) tag else "$current; $tag"
              onUpdate(caseInfo.copy(chronicFemaleHistory = updated))
            }
          ) {
            Text("+ $tag", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
          }
        }
      }
    } else {
      Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text("Step Marked as Not Applicable", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = WarmCharcoal)
          Text(
            text = "This step is bypassed for male/child cases. Tap Continue below to proceed directly to Clinical Examination.",
            fontSize = 11.sp,
            color = WarmCharcoal.copy(alpha = 0.65f)
          )
        }
      }
    }
  }
}

// -------------------------------------------------------------
// CLINICAL EXAMINATION (ACUTE STEP 8 / CHRONIC STEP 11)
// -------------------------------------------------------------
@Composable
fun ClinicalExamStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  val nextLabel = if (isChronic) "Continue: Final Diagnosis" else "Continue: Clinical Diagnosis"

  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Clinical Examination",
    stepDescription = "General physical appearance, vital signs, systemic physical examination and investigations.",
    nextButtonLabel = nextLabel,
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    if (isChronic) {
      OutlinedTextField(
        value = caseInfo.chronicClinicalExam,
        onValueChange = { onUpdate(caseInfo.copy(chronicClinicalExam = it)) },
        label = { Text("Physical & Systemic Examination *") },
        placeholder = { Text("General build, pallor, icterus, cyanosis, clubbing, edema, lymph nodes, CVS, RS, Abdomen...") },
        minLines = 3,
        maxLines = 6,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        OutlinedTextField(
          value = caseInfo.chronicVitals,
          onValueChange = { onUpdate(caseInfo.copy(chronicVitals = it)) },
          label = { Text("Vitals (Pulse, BP, Wt)") },
          placeholder = { Text("BP: 120/80, P: 76, Wt: 64kg") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = caseInfo.chronicInvestigations,
          onValueChange = { onUpdate(caseInfo.copy(chronicInvestigations = it)) },
          label = { Text("Lab & Diagnostic Reports") },
          placeholder = { Text("CBC, ESR, Thyroid, USG, X-Ray...") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }
    } else {
      // Acute Vitals Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        OutlinedTextField(
          value = caseInfo.acutePulse,
          onValueChange = { onUpdate(caseInfo.copy(acutePulse = it)) },
          label = { Text("Pulse /min") },
          placeholder = { Text("98") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = caseInfo.acuteTemperature,
          onValueChange = { onUpdate(caseInfo.copy(acuteTemperature = it)) },
          label = { Text("Temp (°F)") },
          placeholder = { Text("102.4") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )

        OutlinedTextField(
          value = caseInfo.acuteBloodPressure,
          onValueChange = { onUpdate(caseInfo.copy(acuteBloodPressure = it)) },
          label = { Text("BP (mmHg)") },
          placeholder = { Text("120/80") },
          singleLine = true,
          modifier = Modifier.weight(1f),
          shape = RoundedCornerShape(10.dp)
        )
      }

      OutlinedTextField(
        value = caseInfo.acuteGeneralAppearance,
        onValueChange = { onUpdate(caseInfo.copy(acuteGeneralAppearance = it)) },
        label = { Text("General Appearance & Local Examination *") },
        placeholder = { Text("Flushed red face, dry hot skin, congested tonsils with white follicles, chest wheeze...") },
        minLines = 3,
        maxLines = 5,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp)
      )
    }
  }
}

// -------------------------------------------------------------
// CLINICAL / FINAL DIAGNOSIS (ACUTE STEP 9 / CHRONIC STEP 12)
// -------------------------------------------------------------
@Composable
fun DiagnosisStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  isChronic: Boolean,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  val diagValue = if (isChronic) caseInfo.chronicFinalDiagnosis else caseInfo.acuteClinicalDiagnosis
  val onDiagChanged: (String) -> Unit = { updated ->
    if (isChronic) onUpdate(caseInfo.copy(chronicFinalDiagnosis = updated))
    else onUpdate(caseInfo.copy(acuteClinicalDiagnosis = updated))
  }

  val nextLabel = if (isChronic) "Continue: Miasmatic Evaluation" else "Continue: Totality Rubrics"

  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = if (isChronic) "Final Diagnosis" else "Clinical Diagnosis",
    stepDescription = "Establishing the clinical/nosological diagnosis helps prognostic assessment, red-flag monitoring and regimen guidelines.",
    nextButtonLabel = nextLabel,
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    OutlinedTextField(
      value = diagValue,
      onValueChange = onDiagChanged,
      label = { Text("Clinical / Pathological Diagnosis *") },
      placeholder = {
        Text(
          if (isChronic) "e.g. Rheumatoid Arthritis (Seropositive); Bronchial Asthma; Hypothyroidism; PCOD..."
          else "e.g. Acute Follicular Tonsillitis; Acute Viral Bronchitis; Acute Gastroenteritis..."
        )
      },
      singleLine = true,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    Text(
      text = "Common Clinical Categories:",
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      color = primaryColor
    )

    @OptIn(ExperimentalLayoutApi::class)
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      val conditions = if (isChronic) {
        listOf("Rheumatoid Arthritis", "Psoriasis Vulgaris", "Bronchial Asthma", "Migraine (Chronic)", "PCOD / Fibroids", "GERD / Dyspepsia", "Atopic Dermatitis")
      } else {
        listOf("Acute Pharyngitis / Tonsillitis", "Acute Gastroenteritis", "Acute Viral URI", "Acute Otitis Media", "Renal Colic", "Acute Bronchitis")
      }
      conditions.forEach { cond ->
        Surface(
          color = Color(0xFFF3ECE1),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.clickable { onDiagChanged(cond) }
        ) {
          Text("+ $cond", fontSize = 11.sp, color = WarmCharcoal, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
      }
    }
  }
}

// -------------------------------------------------------------
// STEP 13 (CHRONIC): MIASMATIC EVALUATION (DROPDOWN + NOTES)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChronicMiasmaticStep(
  caseInfo: PatientCaseInfo,
  stepNumber: Int,
  totalSteps: Int,
  primaryColor: Color,
  onUpdate: (PatientCaseInfo) -> Unit,
  onNext: () -> Unit,
  onPrev: () -> Unit
) {
  var expandedDropdown by remember { mutableStateOf(false) }
  val miasms = listOf("Psoric", "Sycotic", "Syphilitic", "Tubercular", "Mixed")

  StepContainer(
    stepNumber = stepNumber,
    totalSteps = totalSteps,
    stepTitle = "Miasmatic Evaluation",
    stepDescription = "Evaluate the fundamental miasmatic background (§205 Organon) to select a deep-acting anti-miasmatic simillimum.",
    nextButtonLabel = "Continue: Totality Rubrics",
    onNext = onNext,
    onPrev = onPrev,
    primaryColor = primaryColor
  ) {
    // Miasm Dropdown Box
    Text(
      text = "Primary Dominant Miasm *",
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      color = WarmCharcoal
    )

    ExposedDropdownMenuBox(
      expanded = expandedDropdown,
      onExpandedChange = { expandedDropdown = !expandedDropdown },
      modifier = Modifier.fillMaxWidth()
    ) {
      OutlinedTextField(
        value = caseInfo.chronicMiasm,
        onValueChange = {},
        readOnly = true,
        label = { Text("Miasm (Dropdown Selection)") },
        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = Color.White,
          unfocusedContainerColor = Color.White
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
          .fillMaxWidth()
          .menuAnchor()
      )

      ExposedDropdownMenu(
        expanded = expandedDropdown,
        onDismissRequest = { expandedDropdown = false }
      ) {
        miasms.forEach { m ->
          DropdownMenuItem(
            text = {
              Column {
                Text(m, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                  text = when (m) {
                    "Psoric" -> "Hypersensitivity, itch, functional disturbance, irritation"
                    "Sycotic" -> "Infiltration, overgrowth, warts, slow progression, secretiveness"
                    "Syphilitic" -> "Destruction, ulceration, bone pains < night, degeneration"
                    "Tubercular" -> "Rapid emaciation, changeability, recurrent colds, wanderlust"
                    else -> "Complex multi-miasmatic combination of psora, sycosis & syphilis"
                  },
                  fontSize = 11.sp,
                  color = WarmCharcoal.copy(alpha = 0.65f)
                )
              }
            },
            onClick = {
              onUpdate(caseInfo.copy(chronicMiasm = m))
              expandedDropdown = false
            }
          )
        }
      }
    }

    // Miasm Clinical Notes Field
    OutlinedTextField(
      value = caseInfo.chronicMiasmNotes,
      onValueChange = { onUpdate(caseInfo.copy(chronicMiasmNotes = it)) },
      label = { Text("Miasmatic Evaluation & Clinical Justification Notes *") },
      placeholder = {
        Text("e.g. Dominant Sycotic miasm evidenced by chronic catarrh, warty growths, and family history of diabetes; with latent Psora causing intense itch...")
      },
      minLines = 4,
      maxLines = 7,
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(10.dp)
    )

    // Quick clinical miasm guide
    Card(
      colors = CardDefaults.cardColors(containerColor = Color(0xFFFAF7F2)),
      shape = RoundedCornerShape(10.dp)
    ) {
      Column(modifier = Modifier.padding(12.dp)) {
        Text("Clinical Miasm Pointers:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = primaryColor)
        Text("• Psora: Lack of reaction, functional complaints, dry skin, hunger at 11 AM (Sulphur)", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.75f))
        Text("• Sycosis: Pelvic catarrh, overgrowth, warty excrescences, < damp weather (Thuja)", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.75f))
        Text("• Syphilis: Deep tissue breakdown, nocturnal bone aches, destructive ulcerations (Merc)", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.75f))
        Text("• Tubercular: Family history of asthma/TB, desires to travel, takes cold easily (Tuberculinum)", fontSize = 11.sp, color = WarmCharcoal.copy(alpha = 0.75f))
      }
    }
  }
}
