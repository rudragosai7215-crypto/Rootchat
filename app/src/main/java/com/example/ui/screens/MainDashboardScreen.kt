package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import com.example.ui.components.RootChartLogo
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedCaseEntity
import com.example.ui.theme.ClinicalTerracotta
import com.example.ui.theme.ClinicalTerracottaDark
import com.example.ui.theme.KentGrade2Blue
import com.example.ui.theme.LinenBackground
import com.example.ui.theme.LinenSurface
import com.example.ui.theme.SageMiasm
import com.example.ui.theme.WarmCharcoal
import com.example.ui.viewmodel.PractitionerProfile

@Composable
fun MainDashboardScreen(
  practitioner: PractitionerProfile,
  savedCases: List<SavedCaseEntity>,
  onStartAcuteCase: () -> Unit,
  onStartChronicCase: () -> Unit,
  onDeleteCase: (SavedCaseEntity) -> Unit,
  onUpdateFollowUp: (caseId: Long, notes: String) -> Unit,
  onGenerateReport: (SavedCaseEntity) -> String,
  onLogout: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var activeDetailCase by remember { mutableStateOf<SavedCaseEntity?>(null) }
  var caseToDelete by remember { mutableStateOf<SavedCaseEntity?>(null) }

  val totalCases = savedCases.size
  val acuteCount = savedCases.count { it.caseType.equals("Acute", ignoreCase = true) }
  val chronicCount = savedCases.count { it.caseType.equals("Chronic", ignoreCase = true) }

  var patientSearchQuery by remember { mutableStateOf("") }
  val filteredCases = remember(savedCases, patientSearchQuery) {
    if (patientSearchQuery.isBlank()) savedCases
    else savedCases.filter {
      it.patientName.contains(patientSearchQuery.trim(), ignoreCase = true)
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(LinenBackground)
  ) {
    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      item {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar: Brand + Practitioner Info + Logout
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            RootChartLogo(size = 42.dp)

            Spacer(modifier = Modifier.width(12.dp))

            Column {
              Text(
                text = "RootChart",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = WarmCharcoal
              )
              Text(
                text = "CLINICAL STUDIO",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = ClinicalTerracotta,
                letterSpacing = 1.5.sp
              )
            }
          }

          // Practitioner Info & Logout
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
              .clip(RoundedCornerShape(20.dp))
              .background(LinenSurface)
              .border(1.dp, Color(0xFFDCD4C4), RoundedCornerShape(20.dp))
              .padding(horizontal = 10.dp, vertical = 6.dp)
          ) {
            Column(horizontalAlignment = Alignment.End) {
              Text(
                text = practitioner.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = WarmCharcoal
              )
              Text(
                text = practitioner.role,
                fontSize = 10.sp,
                color = ClinicalTerracotta
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
              onClick = onLogout,
              modifier = Modifier.size(28.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Logout,
                contentDescription = "Logout",
                tint = WarmCharcoal.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }

      // 1. TOP: Total Cases Indicator
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("total_cases_card"),
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = LinenSurface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "TOTAL CLINICAL CASES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = ClinicalTerracotta
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "$totalCases Cases",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = WarmCharcoal
              )
              Text(
                text = "Recorded in practice database",
                fontSize = 12.sp,
                color = WarmCharcoal.copy(alpha = 0.6f)
              )
            }

            // Sub-breakdown chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              Surface(
                color = ClinicalTerracotta.copy(alpha = 0.12f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "$acuteCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = ClinicalTerracotta
                  )
                  Text(
                    text = "Acute",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = ClinicalTerracotta
                  )
                }
              }

              Surface(
                color = SageMiasm.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp)
              ) {
                Column(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "$chronicCount",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = SageMiasm
                  )
                  Text(
                    text = "Chronic",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    color = SageMiasm
                  )
                }
              }
            }
          }
        }
      }

      // 2. MIDDLE: Start New Case (Acute vs Chronic)
      item {
        Column(modifier = Modifier.fillMaxWidth()) {
          Text(
            text = "Start New Case",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            color = WarmCharcoal
          )
        }
      }

      // Acute Case Card - Clean & Classic
      item {
        Card(
          onClick = onStartAcuteCase,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("start_acute_case_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, ClinicalTerracotta.copy(alpha = 0.35f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(ClinicalTerracotta.copy(alpha = 0.12f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = ClinicalTerracotta,
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Acute Case",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = WarmCharcoal
              )
            }

            Button(
              onClick = onStartAcuteCase,
              colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(38.dp)
            ) {
              Text("Start", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }

      // Chronic Case Card - Clean & Classic
      item {
        Card(
          onClick = onStartChronicCase,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("start_chronic_case_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = androidx.compose.foundation.BorderStroke(1.5.dp, SageMiasm.copy(alpha = 0.45f)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(46.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SageMiasm.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Spa,
                contentDescription = null,
                tint = SageMiasm,
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Chronic Case",
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = WarmCharcoal
              )
            }

            Button(
              onClick = onStartChronicCase,
              colors = ButtonDefaults.buttonColors(containerColor = SageMiasm),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.height(38.dp)
            ) {
              Text("Start", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }

      // 3. BOTTOM: Case Library (Saved Cases with Search by Patient Name)
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "Case Library",
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Serif,
              color = WarmCharcoal
            )
            Text(
              text = "Searchable archive of patient cases • Tap to review, copy report or update follow-up",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.65f)
            )
          }
        }
        Spacer(modifier = Modifier.height(10.dp))

        // Search Bar for Patient Name
        OutlinedTextField(
          value = patientSearchQuery,
          onValueChange = { patientSearchQuery = it },
          placeholder = { Text("Search saved cases by patient name...") },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = ClinicalTerracotta
            )
          },
          trailingIcon = {
            if (patientSearchQuery.isNotBlank()) {
              IconButton(onClick = { patientSearchQuery = "" }) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Clear search",
                  tint = WarmCharcoal.copy(alpha = 0.6f)
                )
              }
            }
          },
          singleLine = true,
          shape = RoundedCornerShape(12.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ClinicalTerracotta,
            unfocusedBorderColor = Color(0xFFD6CEBE),
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("patient_name_search")
        )
      }

      if (savedCases.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = LinenSurface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.LocalHospital,
                contentDescription = null,
                tint = WarmCharcoal.copy(alpha = 0.35f),
                modifier = Modifier.size(44.dp)
              )
              Spacer(modifier = Modifier.height(10.dp))
              Text(
                text = "No Cases in Library",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = WarmCharcoal
              )
              Text(
                text = "Choose Acute or Chronic above to record a patient case.",
                fontSize = 12.sp,
                color = WarmCharcoal.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp)
              )
            }
          }
        }
      } else if (filteredCases.isEmpty()) {
        item {
          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 12.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = LinenSurface)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "No patient cases matching \"$patientSearchQuery\"",
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = WarmCharcoal
              )
              TextButton(onClick = { patientSearchQuery = "" }) {
                Text("Clear search filter", color = ClinicalTerracotta)
              }
            }
          }
        }
      } else {
        items(filteredCases) { caseItem ->
          SavedCaseItemCard(
            caseEntity = caseItem,
            onOpenDetails = { activeDetailCase = caseItem },
            onDelete = { caseToDelete = caseItem },
            onCopy = {
              val report = onGenerateReport(caseItem)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              clipboard.setPrimaryClip(ClipData.newPlainText("RootChart Case Report", report))
            }
          )
        }
      }

      item {
        Spacer(modifier = Modifier.height(36.dp))
      }
    }
  }

  // Case Detail & Edit Dialog
  activeDetailCase?.let { caseItem ->
    CaseDetailEditDialog(
      caseEntity = caseItem,
      onDismiss = { activeDetailCase = null },
      onSaveFollowUp = { newNotes ->
        onUpdateFollowUp(caseItem.id, newNotes)
        activeDetailCase = null
      },
      onCopyReport = {
        val report = onGenerateReport(caseItem)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("RootChart Case Report", report))
      }
    )
  }

  // Delete Confirmation Dialog
  caseToDelete?.let { caseItem ->
    AlertDialog(
      onDismissRequest = { caseToDelete = null },
      title = { Text("Delete Case Record?", fontWeight = FontWeight.Bold) },
      text = { Text("Are you sure you want to permanently delete the case for ${caseItem.patientName}?") },
      confirmButton = {
        Button(
          onClick = {
            onDeleteCase(caseItem)
            caseToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta)
        ) {
          Text("Delete")
        }
      },
      dismissButton = {
        TextButton(onClick = { caseToDelete = null }) {
          Text("Cancel")
        }
      }
    )
  }
}

@Composable
fun SavedCaseItemCard(
  caseEntity: SavedCaseEntity,
  onOpenDetails: () -> Unit,
  onDelete: () -> Unit,
  onCopy: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isAcute = caseEntity.caseType.equals("Acute", ignoreCase = true)
  val badgeColor = if (isAcute) ClinicalTerracotta else SageMiasm

  Card(
    onClick = onOpenDetails,
    modifier = modifier
      .fillMaxWidth()
      .testTag("saved_case_item_${caseEntity.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = caseEntity.patientName,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = WarmCharcoal
          )
          Spacer(modifier = Modifier.width(8.dp))
          Surface(
            color = badgeColor.copy(alpha = 0.15f),
            shape = RoundedCornerShape(6.dp)
          ) {
            Text(
              text = if (isAcute) "⚡ ACUTE" else "🌿 CHRONIC",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = badgeColor,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }
        }

        Text(
          text = "${caseEntity.patientAge}y / ${caseEntity.patientGender}",
          fontSize = 12.sp,
          color = WarmCharcoal.copy(alpha = 0.6f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Complaint: ${caseEntity.chiefComplaint}",
        fontSize = 13.sp,
        color = WarmCharcoal.copy(alpha = 0.85f),
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Prescribed Remedy Tag & Actions
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = KentGrade2Blue.copy(alpha = 0.1f),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Rx: ",
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = KentGrade2Blue
            )
            Text(
              text = "${caseEntity.prescribedRemedy} ${caseEntity.potency}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = WarmCharcoal
            )
          }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
          IconButton(
            onClick = onOpenDetails,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Follow Up",
              tint = WarmCharcoal.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.ContentCopy,
              contentDescription = "Copy Report",
              tint = WarmCharcoal.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }

          IconButton(
            onClick = onDelete,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Delete",
              tint = ClinicalTerracotta.copy(alpha = 0.7f),
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }
    }
  }
}

@Composable
fun CaseDetailEditDialog(
  caseEntity: SavedCaseEntity,
  onDismiss: () -> Unit,
  onSaveFollowUp: (newNotes: String) -> Unit,
  onCopyReport: () -> Unit
) {
  var followUpNotes by remember { mutableStateOf(caseEntity.followUpNotes) }

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = caseEntity.patientName,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            fontFamily = FontFamily.Serif
          )
          Text(
            text = "${caseEntity.caseType} Case • ${caseEntity.patientAge} yrs • ${caseEntity.patientGender}",
            fontSize = 12.sp,
            color = WarmCharcoal.copy(alpha = 0.6f)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Chief Complaint
        Column {
          Text("Chief Complaint:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ClinicalTerracotta)
          Text(caseEntity.chiefComplaint, fontSize = 13.sp, color = WarmCharcoal)
        }

        // Totality Rubrics
        Column {
          Text("Totality Rubrics:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = ClinicalTerracotta)
          Text(caseEntity.selectedRubricsSummary, fontSize = 12.sp, color = WarmCharcoal.copy(alpha = 0.8f))
        }

        // Prescription
        Surface(
          color = LinenSurface,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Text(
              text = "Prescribed: ${caseEntity.prescribedRemedy} ${caseEntity.potency}",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = WarmCharcoal
            )
            Text(
              text = "Dosage: ${caseEntity.dosage}",
              fontSize = 12.sp,
              color = WarmCharcoal.copy(alpha = 0.7f)
            )
          }
        }

        // Edit Follow-up Notes
        Column {
          Text(
            text = "Follow-up & Clinical Notes (Editable):",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = WarmCharcoal
          )
          Spacer(modifier = Modifier.height(4.dp))
          OutlinedTextField(
            value = followUpNotes,
            onValueChange = { followUpNotes = it },
            placeholder = { Text("Update patient follow-up, symptom response, or second prescription...") },
            minLines = 3,
            maxLines = 5,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = ClinicalTerracotta,
              unfocusedBorderColor = Color(0xFFD6CEBE)
            )
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = { onSaveFollowUp(followUpNotes) },
        colors = ButtonDefaults.buttonColors(containerColor = ClinicalTerracotta)
      ) {
        Text("Save Notes")
      }
    },
    dismissButton = {
      Row {
        OutlinedButton(onClick = onCopyReport) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy Report")
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onDismiss) {
          Text("Close")
        }
      }
    }
  )
}
