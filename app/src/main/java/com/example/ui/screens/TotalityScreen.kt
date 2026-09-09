package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.KentRepertoryDataset
import com.example.data.model.CaseTotalityItem
import com.example.ui.components.RootChartLogo
import com.example.ui.theme.CardBorderLight
import com.example.ui.theme.CharcoalMutedLight
import com.example.ui.theme.CharcoalTextLight
import com.example.ui.theme.MiasmPsora
import com.example.ui.theme.MiasmSycosis
import com.example.ui.theme.MiasmSyphilis
import com.example.ui.theme.MiasmTubercular
import com.example.ui.theme.PaperSurfaceLight
import com.example.ui.theme.TerracottaContainerLight
import com.example.ui.theme.TerracottaPrimaryLight
import com.example.ui.viewmodel.PatientCaseInfo
import com.example.ui.viewmodel.PractitionerProfile

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TotalityScreen(
  caseInfo: PatientCaseInfo,
  totalityItems: List<CaseTotalityItem>,
  presets: List<KentRepertoryDataset.TotalityPreset>,
  practitionerProfile: PractitionerProfile = PractitionerProfile(),
  onOpenProfileDialog: () -> Unit = {},
  onUpdateCaseInfo: (String, String, String, String, String) -> Unit,
  onUpdateIntensity: (String, Int) -> Unit,
  onRemoveItem: (String) -> Unit,
  onClearTotality: () -> Unit,
  onLoadPreset: (KentRepertoryDataset.TotalityPreset) -> Unit,
  onNavigateToBrowser: () -> Unit,
  onNavigateToRepertorization: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isExpandedPatientInfo by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("totality_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 0. RootChart Clinical Studio Hero Banner (from photo IMG_2902)
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rootchart_hero_banner"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PaperSurfaceLight),
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderLight)
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              RootChartLogo(size = 34.dp)
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "RootChart",
                  style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = CharcoalTextLight,
                    fontSize = 18.sp
                  )
                )
                Text(
                  text = "CLINICAL REPERTORIZATION STUDIO",
                  style = MaterialTheme.typography.labelSmall.copy(
                    color = TerracottaPrimaryLight,
                    letterSpacing = 1.0.sp,
                    fontWeight = FontWeight.Bold,
                    fontSize = 9.sp
                  )
                )
              }
            }

            // Quick Role Switcher
            Surface(
              onClick = onOpenProfileDialog,
              shape = RoundedCornerShape(20.dp),
              color = TerracottaContainerLight,
              modifier = Modifier.testTag("hero_profile_badge")
            ) {
              Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
              ) {
                Text(
                  text = practitionerProfile.role,
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = TerracottaPrimaryLight
                  )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                  imageVector = Icons.Default.Person,
                  contentDescription = null,
                  tint = TerracottaPrimaryLight,
                  modifier = Modifier.size(13.dp)
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Text(
            text = "A clearer way to take the case.",
            style = MaterialTheme.typography.titleMedium.copy(
              fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
              fontWeight = FontWeight.SemiBold,
              color = CharcoalTextLight,
              fontSize = 17.sp
            )
          )

          Text(
            text = "Move from the patient's story to a thoughtful similimum with a calm, guided protocol.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = CharcoalMutedLight,
              fontSize = 12.sp,
              lineHeight = 16.sp
            )
          )
        }
      }
    }

    // 1. Patient Case Info Header Card
    item {
      ElevatedCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("patient_info_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
          containerColor = MaterialTheme.colorScheme.surface
        )
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Patient Case Record",
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp
                )
              )
            }

            // Acute vs Chronic Segmented Toggle
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf("Acute", "Chronic").forEach { type ->
                val selected = caseInfo.caseType == type
                Surface(
                  onClick = {
                    onUpdateCaseInfo(
                      caseInfo.name,
                      caseInfo.age,
                      caseInfo.gender,
                      caseInfo.complaint,
                      type
                    )
                  },
                  shape = RoundedCornerShape(8.dp),
                  color = if (selected) MaterialTheme.colorScheme.primary
                          else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier.testTag("case_type_btn_${type.lowercase()}")
                ) {
                  Text(
                    text = type,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                      color = if (selected) MaterialTheme.colorScheme.onPrimary
                             else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Patient Name & Age in Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = caseInfo.name,
              onValueChange = { onUpdateCaseInfo(it, caseInfo.age, caseInfo.gender, caseInfo.complaint, caseInfo.caseType) },
              modifier = Modifier
                .weight(2f)
                .testTag("patient_name_input"),
              label = { Text("Patient Name", fontSize = 12.sp) },
              placeholder = { Text("e.g. Ramesh Kumar", fontSize = 12.sp) },
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
              value = caseInfo.age,
              onValueChange = { onUpdateCaseInfo(caseInfo.name, it, caseInfo.gender, caseInfo.complaint, caseInfo.caseType) },
              modifier = Modifier
                .weight(1f)
                .testTag("patient_age_input"),
              label = { Text("Age", fontSize = 12.sp) },
              placeholder = { Text("35", fontSize = 12.sp) },
              singleLine = true,
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Gender selector chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Gender: ",
              style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
            )
            Spacer(modifier = Modifier.width(6.dp))
            listOf("Female", "Male", "Child", "Other").forEach { g ->
              FilterChip(
                selected = caseInfo.gender == g,
                onClick = { onUpdateCaseInfo(caseInfo.name, caseInfo.age, g, caseInfo.complaint, caseInfo.caseType) },
                label = { Text(g, fontSize = 11.sp) },
                modifier = Modifier.padding(end = 6.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(6.dp))

          OutlinedTextField(
            value = caseInfo.complaint,
            onValueChange = { onUpdateCaseInfo(caseInfo.name, caseInfo.age, caseInfo.gender, it, caseInfo.caseType) },
            modifier = Modifier
              .fillMaxWidth()
              .testTag("chief_complaint_input"),
            label = { Text("Chief Complaint & Causation", fontSize = 12.sp) },
            placeholder = { Text("e.g. Acid peptic disease, burning after spicy meals", fontSize = 12.sp) },
            shape = RoundedCornerShape(12.dp),
            maxLines = 2
          )
        }
      }
    }

    // 2. Classical Totality Quick-Load Presets
    item {
      Column {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(bottom = 8.dp)
        ) {
          Icon(
            imageVector = Icons.Default.AutoAwesome,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "High-Yield Clinical Totality Presets",
            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
          )
        }

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          presets.forEach { preset ->
            Surface(
              onClick = { onLoadPreset(preset) },
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f),
              border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f)),
              modifier = Modifier.testTag("preset_${preset.title.take(10).lowercase().replace(" ", "_")}")
            ) {
              Column(modifier = Modifier.padding(10.dp)) {
                Text(
                  text = preset.title,
                  style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                  )
                )
                Text(
                  text = "${preset.rubricIds.size} rubrics • ${preset.caseType}",
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.outline
                  )
                )
              }
            }
          }
        }
      }
    }

    // 3. Totality Symptoms Section Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "Case Totality Symptoms (${totalityItems.size})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
          )
          Text(
            text = "Adjust intensity weight (1 to 3) for repertorization",
            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
          )
        }

        if (totalityItems.isNotEmpty()) {
          IconButton(
            onClick = onClearTotality,
            modifier = Modifier.testTag("clear_totality_button")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Clear All",
              tint = MaterialTheme.colorScheme.error
            )
          }
        }
      }
    }

    // Empty state or Items
    if (totalityItems.isEmpty()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
          shape = RoundedCornerShape(16.dp)
        ) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(
              imageVector = Icons.Default.Healing,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.primary,
              modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "Totality is currently empty",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Add rubrics from Kent's Repertory browser, or load one of the clinical presets above.",
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
              onClick = onNavigateToBrowser,
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.testTag("browse_rubrics_action_btn")
            ) {
              Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Browse Kent Rubrics")
            }
          }
        }
      }
    } else {
      items(totalityItems, key = { it.rubricId }) { item ->
        TotalityItemCard(
          item = item,
          onUpdateIntensity = { newInt -> onUpdateIntensity(item.rubricId, newInt) },
          onRemove = { onRemoveItem(item.rubricId) }
        )
      }

      // Actions bottom: Repertorize Button & Add more
      item {
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedButton(
            onClick = onNavigateToBrowser,
            modifier = Modifier
              .weight(1f)
              .height(50.dp),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add More", fontSize = 13.sp)
          }

          Button(
            onClick = onNavigateToRepertorization,
            modifier = Modifier
              .weight(1.5f)
              .height(50.dp)
              .testTag("repertorize_action_button"),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.GridOn, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Repertorize (${totalityItems.size})", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
        Spacer(modifier = Modifier.height(40.dp))
      }
    }
  }
}

@Composable
fun TotalityItemCard(
  item: CaseTotalityItem,
  onUpdateIntensity: (Int) -> Unit,
  onRemove: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("totality_item_${item.rubricId}"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = item.chapter.uppercase(),
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onSecondaryContainer
                )
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            val miasmColor = when (item.miasm.lowercase()) {
              "psora" -> MiasmPsora
              "sycosis" -> MiasmSycosis
              "syphilis" -> MiasmSyphilis
              "tubercular" -> MiasmTubercular
              else -> MaterialTheme.colorScheme.primary
            }
            Text(
              text = "• ${item.miasm}",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = miasmColor,
                fontSize = 10.sp
              )
            )
          }

          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = item.rubricName,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
          )

          if (item.modalityNote.isNotBlank()) {
            Text(
              text = item.modalityNote,
              style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
              )
            )
          }
        }

        IconButton(
          onClick = onRemove,
          modifier = Modifier
            .size(32.dp)
            .testTag("remove_totality_item_${item.rubricId}")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Remove symptom",
            tint = MaterialTheme.colorScheme.outline
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Intensity Weight Selector (1, 2, 3)
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Weight Factor (Multiplier):",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.outline,
            fontWeight = FontWeight.Medium
          )
        )

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf(1 to "1x Mild", 2 to "2x Marked", 3 to "3x Keynote").forEach { (level, label) ->
            val isSelected = item.userIntensity == level
            Surface(
              onClick = { onUpdateIntensity(level) },
              shape = RoundedCornerShape(8.dp),
              color = if (isSelected) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.surfaceVariant,
              modifier = Modifier.testTag("totality_intensity_${item.rubricId}_$level")
            ) {
              Text(
                text = label,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                         else MaterialTheme.colorScheme.onSurfaceVariant,
                  fontSize = 10.sp
                )
              )
            }
          }
        }
      }
    }
  }
}
