package com.example.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RemedyScore

@Composable
fun PrescriptionDialog(
  topRemedies: List<RemedyScore>,
  onDismiss: () -> Unit,
  onSavePrescription: (remedy: String, potency: String, dosage: String, followUp: String) -> Unit
) {
  val defaultRemedy = topRemedies.firstOrNull()?.remedyFullName ?: "Nux Vomica"
  var selectedRemedy by remember { mutableStateOf(defaultRemedy) }
  var selectedPotency by remember { mutableStateOf("30C") }
  var dosageText by remember { mutableStateOf("4 pills TDS (three times daily) for 3 days") }
  var followUpText by remember { mutableStateOf("Follow-up review in 7 days. Avoid raw onion, garlic, coffee; clean tongue.") }

  val commonPotencies = listOf("6C", "30C", "200C", "1M", "10M", "0/1 LM", "6X", "Q")
  val commonDosages = listOf(
    "4 pills TDS for 3 days",
    "Single dose stat (at bedtime)",
    "4 pills BD for 7 days",
    "10 drops in 1/2 cup water OD",
    "Repeat hourly if acute pain"
  )

  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("prescription_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.LocalPharmacy,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Rx Prescription & Save Case",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Selected Remedy Input
        OutlinedTextField(
          value = selectedRemedy,
          onValueChange = { selectedRemedy = it },
          label = { Text("Prescribed Homeopathic Medicine", fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("prescribe_remedy_input"),
          singleLine = true,
          shape = RoundedCornerShape(10.dp)
        )

        // Quick candidate chips
        if (topRemedies.isNotEmpty()) {
          Text(
            text = "Repertorized Candidates:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
          )
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            topRemedies.take(5).forEach { candidate ->
              val isSelected = selectedRemedy == candidate.remedyFullName
              FilterChip(
                selected = isSelected,
                onClick = { selectedRemedy = candidate.remedyFullName },
                label = { Text("${candidate.remedyAbbr} (${candidate.totalScore}p)", fontSize = 11.sp) }
              )
            }
          }
        }

        // Potency Picker
        Text(
          text = "Select Potency (Dynamization):",
          style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
        )
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          commonPotencies.forEach { pot ->
            val isSelected = selectedPotency == pot
            FilterChip(
              selected = isSelected,
              onClick = { selectedPotency = pot },
              label = { Text(pot, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
              modifier = Modifier.testTag("potency_chip_$pot")
            )
          }
        }

        // Dosage / Instructions
        OutlinedTextField(
          value = dosageText,
          onValueChange = { dosageText = it },
          label = { Text("Posology & Dosage Frequency", fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("dosage_input"),
          shape = RoundedCornerShape(10.dp),
          maxLines = 2
        )

        // Dosage preset quick chips
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          commonDosages.forEach { dose ->
            Surface(
              onClick = { dosageText = dose },
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.surfaceVariant
            ) {
              Text(
                text = dose,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp)
              )
            }
          }
        }

        // Follow up notes
        OutlinedTextField(
          value = followUpText,
          onValueChange = { followUpText = it },
          label = { Text("Regimen, Diet & Follow-up Instructions", fontSize = 12.sp) },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("follow_up_input"),
          shape = RoundedCornerShape(10.dp),
          maxLines = 2
        )
      }
    },
    confirmButton = {
      Button(
        onClick = {
          onSavePrescription(selectedRemedy, selectedPotency, dosageText, followUpText)
        },
        modifier = Modifier.testTag("save_prescription_button"),
        shape = RoundedCornerShape(10.dp)
      ) {
        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Save to Library", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel")
      }
    }
  )
}
