package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Remedy
import com.example.ui.theme.MiasmPsora
import com.example.ui.theme.MiasmSycosis
import com.example.ui.theme.MiasmSyphilis
import com.example.ui.theme.MiasmTubercular

@Composable
fun MateriaMedicaScreen(
  remedies: List<Remedy>,
  searchQuery: String,
  selectedRemedy: Remedy?,
  onSearchQueryChanged: (String) -> Unit,
  onSelectRemedy: (Remedy?) -> Unit,
  onPrescribeRemedy: (Remedy) -> Unit,
  modifier: Modifier = Modifier
) {
  val filteredRemedies = remedies.filter {
    searchQuery.isBlank() ||
    it.fullName.contains(searchQuery, ignoreCase = true) ||
    it.abbreviation.contains(searchQuery, ignoreCase = true) ||
    it.commonName.contains(searchQuery, ignoreCase = true) ||
    it.clinicalUses.contains(searchQuery, ignoreCase = true)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("materia_medica_screen")
  ) {
    // Search Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(16.dp)
    ) {
      Text(
        text = "Materia Medica Reference",
        style = MaterialTheme.typography.titleLarge.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 19.sp
        )
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = "High-yield homeopathic polycrests, keynotes, modalities & clinical uses",
        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
      )

      Spacer(modifier = Modifier.height(12.dp))

      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("mm_search_input"),
        placeholder = { Text("Search remedy by name, kingdom, symptom...", fontSize = 14.sp) },
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

    LazyColumn(
      modifier = Modifier
        .fillMaxSize()
        .testTag("mm_remedy_list"),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(filteredRemedies, key = { it.abbreviation }) { remedy ->
        RemedyCard(
          remedy = remedy,
          onClick = { onSelectRemedy(remedy) }
        )
      }
    }
  }

  // Remedy Detail Dialog
  if (selectedRemedy != null) {
    RemedyDetailDialog(
      remedy = selectedRemedy,
      onDismiss = { onSelectRemedy(null) },
      onPrescribe = {
        onSelectRemedy(null)
        onPrescribeRemedy(selectedRemedy)
      }
    )
  }
}

@Composable
fun RemedyCard(
  remedy: Remedy,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .clickable(onClick = onClick)
      .testTag("mm_card_${remedy.abbreviation}"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = remedy.fullName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = remedy.abbreviation,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              )
            }
          }
          Text(
            text = "${remedy.commonName} • ${remedy.kingdom}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.outline,
              fontSize = 11.sp
            )
          )
        }

        Surface(
          shape = RoundedCornerShape(8.dp),
          color = when (remedy.thermal.lowercase()) {
            "hot" -> Color(0xFFFFEBEE)
            "chilly" -> Color(0xFFE1F5FE)
            else -> Color(0xFFF3E5F5)
          }
        ) {
          Text(
            text = remedy.thermal,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall.copy(
              fontWeight = FontWeight.Bold,
              color = when (remedy.thermal.lowercase()) {
                "hot" -> Color(0xFFC62828)
                "chilly" -> Color(0xFF0277BD)
                else -> Color(0xFF6A1B9A)
              }
            )
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Keynote highlight
      Text(
        text = remedy.keynotes.firstOrNull() ?: "",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontSize = 12.sp
        ),
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "Clinical: ${remedy.clinicalUses}",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.primary,
          fontWeight = FontWeight.Medium,
          fontSize = 11.sp
        ),
        maxLines = 1
      )
    }
  }
}

@Composable
fun RemedyDetailDialog(
  remedy: Remedy,
  onDismiss: () -> Unit,
  onPrescribe: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    modifier = Modifier.testTag("remedy_detail_dialog"),
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = remedy.fullName,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "${remedy.commonName} (${remedy.abbreviation})",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
          )
        }
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "Close")
        }
      }
    },
    text = {
      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        item {
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "Thermal: ${remedy.thermal}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
            Surface(
              shape = RoundedCornerShape(6.dp),
              color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = "Miasm: ${remedy.primaryMiasm}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
            }
          }
        }

        item {
          Text(
            text = "Guiding Keynotes:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          remedy.keynotes.forEach { kn ->
            Text(
              text = "• $kn",
              style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
              modifier = Modifier.padding(vertical = 2.dp)
            )
          }
        }

        item {
          Text(
            text = "Modalities:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = remedy.modalities,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )
        }

        item {
          Text(
            text = "Therapeutic & Clinical Applications:",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = remedy.clinicalUses,
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 12.sp
            )
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onPrescribe,
        modifier = Modifier.testTag("dialog_prescribe_btn")
      ) {
        Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("Select for Prescription")
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Dismiss")
      }
    }
  )
}
