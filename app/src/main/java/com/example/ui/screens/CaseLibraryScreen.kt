package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderShared
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.SavedCaseEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CaseLibraryScreen(
  cases: List<SavedCaseEntity>,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  onDeleteCase: (SavedCaseEntity) -> Unit,
  onUpdateFollowUp: (Long, String) -> Unit,
  onGenerateReport: (SavedCaseEntity) -> String,
  onShowNotification: (String) -> Unit,
  onStartNewCase: () -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var caseToDelete by remember { mutableStateOf<SavedCaseEntity?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("case_library_screen")
  ) {
    // Top Bar & Stats
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(16.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Patient Case Library",
            style = MaterialTheme.typography.titleLarge.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 20.sp
            )
          )
          Text(
            text = "${cases.size} recorded homeopathic cases",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
          )
        }

        Button(
          onClick = onStartNewCase,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.testTag("start_new_case_btn")
        ) {
          Icon(Icons.Default.Healing, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("New Case", fontSize = 12.sp)
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Search input
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("library_search_input"),
        placeholder = { Text("Search by patient name, complaint, remedy...", fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear")
            }
          }
        },
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = MaterialTheme.colorScheme.primary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
        )
      )
    }

    if (cases.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.FolderShared,
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No saved patient cases yet",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Select rubrics in Totality, repertorize, and prescribe to save patient records to your local database.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          )
        }
      }
    } else {
      LazyColumn(
        modifier = Modifier
          .fillMaxSize()
          .testTag("saved_cases_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(cases, key = { it.id }) { caseItem ->
          SavedCaseCard(
            caseEntity = caseItem,
            onDelete = { caseToDelete = caseItem },
            onUpdateFollowUp = { notes -> onUpdateFollowUp(caseItem.id, notes) },
            onCopyReport = {
              val report = onGenerateReport(caseItem)
              val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
              val clip = ClipData.newPlainText("Homeopathic Case Report", report)
              clipboard.setPrimaryClip(clip)
              onShowNotification("Case Report copied to clipboard!")
            }
          )
        }
      }
    }
  }

  // Delete Confirmation Dialog
  if (caseToDelete != null) {
    AlertDialog(
      onDismissRequest = { caseToDelete = null },
      title = { Text("Delete Patient Record?") },
      text = { Text("Are you sure you want to delete the clinical record for '${caseToDelete?.patientName}'?") },
      confirmButton = {
        Button(
          onClick = {
            caseToDelete?.let { onDeleteCase(it) }
            caseToDelete = null
          },
          colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.error
          )
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
fun SavedCaseCard(
  caseEntity: SavedCaseEntity,
  onDelete: () -> Unit,
  onUpdateFollowUp: (String) -> Unit,
  onCopyReport: () -> Unit,
  modifier: Modifier = Modifier
) {
  val dateFormatted = remember(caseEntity.createdAtTimestamp) {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    sdf.format(Date(caseEntity.createdAtTimestamp))
  }

  var isEditingNotes by remember { mutableStateOf(false) }
  var noteDraft by remember(caseEntity.followUpNotes) { mutableStateOf(caseEntity.followUpNotes) }

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("saved_case_card_${caseEntity.id}"),
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      // Top header: Name, Age, Gender & Case type badge
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = caseEntity.patientName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(${caseEntity.patientAge}y, ${caseEntity.patientGender})",
              style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
            )
          }
          Text(
            text = dateFormatted,
            style = MaterialTheme.typography.labelSmall.copy(
              color = MaterialTheme.colorScheme.outline,
              fontSize = 10.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = if (caseEntity.caseType.equals("Acute", ignoreCase = true))
                  MaterialTheme.colorScheme.tertiaryContainer
                  else MaterialTheme.colorScheme.secondaryContainer
        ) {
          Text(
            text = caseEntity.caseType.uppercase(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Chief complaint
      Text(
        text = "Chief Complaint: ${caseEntity.chiefComplaint}",
        style = MaterialTheme.typography.bodyMedium.copy(
          fontWeight = FontWeight.Medium,
          fontSize = 13.sp
        )
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Prescribed Rx Pill
      Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LocalPharmacy,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Column {
            Text(
              text = "Rx: ${caseEntity.prescribedRemedy} ${caseEntity.potency}",
              style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
              )
            )
            Text(
              text = caseEntity.dosage,
              style = MaterialTheme.typography.bodySmall.copy(
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
              )
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Totality & Repertorization Summary
      Text(
        text = "Totality Rubrics: ${caseEntity.selectedRubricsSummary}",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 11.sp
        ),
        maxLines = 2
      )

      if (caseEntity.topRankedRemedies.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Repertorized: ${caseEntity.topRankedRemedies}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.outline,
            fontSize = 10.sp
          )
        )
      }

      if (caseEntity.miasmSummary.isNotBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = caseEntity.miasmSummary,
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.primary,
            fontSize = 10.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))
      HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
      Spacer(modifier = Modifier.height(8.dp))

      // Follow-up notes section
      if (isEditingNotes) {
        OutlinedTextField(
          value = noteDraft,
          onValueChange = { noteDraft = it },
          label = { Text("Follow-up Observations", fontSize = 11.sp) },
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(10.dp),
          maxLines = 3
        )
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.End
        ) {
          TextButton(onClick = { isEditingNotes = false }) {
            Text("Cancel", fontSize = 11.sp)
          }
          Button(
            onClick = {
              onUpdateFollowUp(noteDraft)
              isEditingNotes = false
            },
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
          ) {
            Text("Save Notes", fontSize = 11.sp)
          }
        }
      } else {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (caseEntity.followUpNotes.isNotBlank()) "Follow-up: ${caseEntity.followUpNotes}"
                   else "No follow-up notes added",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            ),
            modifier = Modifier.weight(1f),
            maxLines = 2
          )
          TextButton(onClick = { isEditingNotes = true }) {
            Text(if (caseEntity.followUpNotes.isNotBlank()) "Edit" else "+ Note", fontSize = 11.sp)
          }
        }
      }

      // Actions bottom: Copy Report & Delete
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = onCopyReport,
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("copy_report_btn_${caseEntity.id}")
        ) {
          Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Copy Report", fontSize = 11.sp)
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
          onClick = onDelete,
          modifier = Modifier
            .size(32.dp)
            .testTag("delete_case_btn_${caseEntity.id}")
        ) {
          Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete case",
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }
  }
}
