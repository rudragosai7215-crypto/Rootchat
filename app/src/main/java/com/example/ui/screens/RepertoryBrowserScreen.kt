package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaseTotalityItem
import com.example.data.model.KentRubric
import com.example.data.model.RemedyGrade
import com.example.ui.theme.Grade1Color
import com.example.ui.theme.Grade2Color
import com.example.ui.theme.Grade3Color
import com.example.ui.theme.MiasmPsora
import com.example.ui.theme.MiasmSycosis
import com.example.ui.theme.MiasmSyphilis
import com.example.ui.theme.MiasmTubercular
import com.example.ui.theme.TerracottaPrimaryLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepertoryBrowserScreen(
  rubrics: List<KentRubric>,
  chapters: List<String>,
  selectedChapter: String,
  searchQuery: String,
  totalityItems: List<CaseTotalityItem>,
  onSearchQueryChanged: (String) -> Unit,
  onChapterSelected: (String) -> Unit,
  onAddRubric: (KentRubric, Int) -> Unit,
  onRemoveRubric: (String) -> Unit,
  onRemedyClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxSize()
      .testTag("repertory_browser_screen")
  ) {
    // Search & Filter Header
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface)
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("rubric_search_input"),
        placeholder = { Text("Search symptom, rubric, modality, remedy...", fontSize = 14.sp) },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.primary
          )
        },
        trailingIcon = {
          if (searchQuery.isNotEmpty()) {
            IconButton(
              onClick = { onSearchQueryChanged("") },
              modifier = Modifier.testTag("clear_search_button")
            ) {
              Icon(Icons.Default.Clear, contentDescription = "Clear search")
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

      Spacer(modifier = Modifier.height(10.dp))

      // Chapter Filter Chips (Horizontal Scroll)
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        chapters.forEach { chapter ->
          val isSelected = chapter == selectedChapter
          FilterChip(
            selected = isSelected,
            onClick = { onChapterSelected(chapter) },
            label = { Text(chapter, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
              selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier.testTag("chapter_chip_${chapter.lowercase().replace(" ", "_")}")
          )
        }
      }
    }

    // Results Count & Rubric List
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "${rubrics.size} Kent Rubrics found",
        style = MaterialTheme.typography.bodySmall.copy(
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          fontWeight = FontWeight.Medium
        )
      )
      Text(
        text = "Grades: 3=Bold Red, 2=Blue, 1=Gray",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.outline,
          fontStyle = FontStyle.Italic
        )
      )
    }

    if (rubrics.isEmpty()) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(32.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            modifier = Modifier.size(54.dp),
            tint = MaterialTheme.colorScheme.outlineVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "No matching homeopathic rubrics found",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Try searching by clinical term (e.g. anxiety, nausea, headache, thirst, cold) or select 'All' chapters.",
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
          .testTag("rubrics_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        items(rubrics, key = { it.id }) { rubric ->
          val addedItem = totalityItems.find { it.rubricId == rubric.id }
          RubricCard(
            rubric = rubric,
            addedItem = addedItem,
            onAddRubric = { intensity -> onAddRubric(rubric, intensity) },
            onRemoveRubric = { onRemoveRubric(rubric.id) },
            onRemedyClick = onRemedyClick
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RubricCard(
  rubric: KentRubric,
  addedItem: CaseTotalityItem?,
  onAddRubric: (Int) -> Unit,
  onRemoveRubric: () -> Unit,
  onRemedyClick: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val isAdded = addedItem != null
  var selectedIntensity by remember(addedItem) {
    mutableStateOf(addedItem?.userIntensity ?: 2)
  }

  ElevatedCard(
    modifier = modifier
      .fillMaxWidth()
      .testTag("rubric_card_${rubric.id}"),
    colors = CardDefaults.elevatedCardColors(
      containerColor = if (isAdded) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f)
                       else MaterialTheme.colorScheme.surface
    ),
    shape = RoundedCornerShape(14.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Top row: Chapter badge, Miasm tag & Add/Added button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.primaryContainer
          ) {
            Text(
              text = rubric.chapter.uppercase(),
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 10.sp
              )
            )
          }
          Spacer(modifier = Modifier.width(6.dp))
          val miasmColor = when (rubric.miasm.lowercase()) {
            "psora" -> MiasmPsora
            "sycosis" -> MiasmSycosis
            "syphilis" -> MiasmSyphilis
            "tubercular" -> MiasmTubercular
            else -> MaterialTheme.colorScheme.primary
          }
          Surface(
            shape = RoundedCornerShape(6.dp),
            color = miasmColor.copy(alpha = 0.12f)
          ) {
            Text(
              text = rubric.miasm,
              modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = miasmColor,
                fontSize = 10.sp
              )
            )
          }
        }

        // Action button
        if (!isAdded) {
          Button(
            onClick = { onAddRubric(selectedIntensity) },
            shape = RoundedCornerShape(10.dp),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
            modifier = Modifier
              .height(34.dp)
              .testTag("add_rubric_btn_${rubric.id}")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
          }
        } else {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              shape = RoundedCornerShape(10.dp),
              color = MaterialTheme.colorScheme.primary,
              modifier = Modifier
                .clickable { onRemoveRubric() }
                .testTag("remove_rubric_btn_${rubric.id}")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Added",
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("In Totality", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Rubric Name
      Text(
        text = rubric.rubricName,
        style = MaterialTheme.typography.titleMedium.copy(
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
      )

      if (rubric.subRubric.isNotBlank()) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = rubric.subRubric,
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
          )
        )
      }

      if (rubric.modality.isNotBlank()) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Modalities: ${rubric.modality}",
          style = MaterialTheme.typography.labelSmall.copy(
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp
          )
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Remedy badges with Grades
      Text(
        text = "Key Remedies (${rubric.remedies.size}):",
        style = MaterialTheme.typography.labelSmall.copy(
          color = MaterialTheme.colorScheme.outline,
          fontWeight = FontWeight.SemiBold
        )
      )
      Spacer(modifier = Modifier.height(6.dp))

      FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        rubric.remedies.sortedByDescending { it.grade }.forEach { rg ->
          RemedyGradeChip(remedyGrade = rg, onClick = { onRemedyClick(rg.remedyAbbr) })
        }
      }

      // If added, show quick intensity selector
      AnimatedVisibility(visible = isAdded) {
        Column(modifier = Modifier.padding(top = 10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "Case Symptom Intensity Weight:",
              style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
              listOf(1 to "1 (Mild)", 2 to "2 (Marked)", 3 to "3 (Keynote)").forEach { (level, label) ->
                val active = (addedItem?.userIntensity == level)
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                  modifier = Modifier
                    .clickable { onAddRubric(level) }
                    .testTag("intensity_${rubric.id}_$level")
                ) {
                  Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                      color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
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
  }
}

@Composable
fun RemedyGradeChip(
  remedyGrade: RemedyGrade,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val (chipColor, textColor, isBold) = when (remedyGrade.grade) {
    3 -> Triple(Grade3Color.copy(alpha = 0.14f), Grade3Color, true)
    2 -> Triple(Grade2Color.copy(alpha = 0.12f), Grade2Color, true)
    else -> Triple(Grade1Color.copy(alpha = 0.08f), Grade1Color, false)
  }

  Surface(
    shape = RoundedCornerShape(6.dp),
    color = chipColor,
    modifier = modifier
      .clickable(onClick = onClick)
      .testTag("remedy_chip_${remedyGrade.remedyAbbr}")
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = remedyGrade.remedyAbbr,
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
          fontStyle = if (remedyGrade.grade == 2) FontStyle.Italic else FontStyle.Normal,
          color = textColor,
          fontSize = 11.sp
        )
      )
      Spacer(modifier = Modifier.width(3.dp))
      Text(
        text = "${remedyGrade.grade}",
        style = MaterialTheme.typography.labelSmall.copy(
          fontWeight = FontWeight.ExtraBold,
          color = textColor.copy(alpha = 0.8f),
          fontSize = 9.sp
        )
      )
    }
  }
}
