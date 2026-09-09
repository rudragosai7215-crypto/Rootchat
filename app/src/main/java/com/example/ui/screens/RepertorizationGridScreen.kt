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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaseTotalityItem
import com.example.data.model.MiasmBreakdown
import com.example.data.model.RemedyScore
import com.example.data.model.RepertorizationAnalysis
import com.example.ui.theme.GoldTertiaryLight
import com.example.ui.theme.Grade1Color
import com.example.ui.theme.Grade2Color
import com.example.ui.theme.Grade3Color
import com.example.ui.theme.MiasmPsora
import com.example.ui.theme.MiasmSycosis
import com.example.ui.theme.MiasmSyphilis
import com.example.ui.theme.MiasmTubercular
import com.example.ui.theme.TerracottaPrimaryLight

@Composable
fun RepertorizationGridScreen(
  analysis: RepertorizationAnalysis,
  totalityItems: List<CaseTotalityItem>,
  onOpenPrescription: () -> Unit,
  onViewRemedyDetails: (String) -> Unit,
  onNavigateToBrowser: () -> Unit,
  modifier: Modifier = Modifier
) {
  if (totalityItems.isEmpty()) {
    Box(
      modifier = modifier
        .fillMaxSize()
        .padding(32.dp),
      contentAlignment = Alignment.Center
    ) {
      Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
          imageVector = Icons.Default.GridOn,
          contentDescription = null,
          modifier = Modifier.size(56.dp),
          tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(14.dp))
        Text(
          text = "No Symptoms in Case Totality",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "Add rubrics from Kent's Repertory to compute remedy coverage, totality scores, and the classical repertorization matrix.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
          )
        )
        Spacer(modifier = Modifier.height(18.dp))
        Button(
          onClick = onNavigateToBrowser,
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier.testTag("empty_repertorize_browse_btn")
        ) {
          Icon(Icons.Default.Healing, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text("Add Symptoms to Totality")
        }
      }
    }
    return
  }

  val topRemedies = analysis.scores.take(10)

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .testTag("repertorization_grid_screen"),
    contentPadding = PaddingValues(16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Simillimum Prescribe Hero Action
    item {
      val simillimum = topRemedies.firstOrNull()
      if (simillimum != null) {
        ElevatedCard(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("simillimum_hero_card"),
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
          )
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Rank 1",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "LEADING SIMILLIMUM (#1)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 10.sp
                    )
                  )
                  Text(
                    text = "${simillimum.remedyFullName} (${simillimum.remedyAbbr})",
                    style = MaterialTheme.typography.titleMedium.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 17.sp,
                      color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primary
              ) {
                Text(
                  text = "${simillimum.coverageCount}/${simillimum.totalRubrics} Cov • ${simillimum.totalScore} Pts",
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                  style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 11.sp
                  )
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Thermal: ${simillimum.thermal} • Dominant: ${simillimum.primaryMiasm}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                  fontSize = 12.sp
                )
              )

              Button(
                onClick = onOpenPrescription,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                modifier = Modifier
                  .height(36.dp)
                  .testTag("prescribe_simillimum_btn")
              ) {
                Icon(Icons.Default.LocalPharmacy, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Prescribe Rx", fontSize = 12.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // 2. Classical Repertorization Matrix Grid Table
    item {
      ElevatedCard(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("repertorization_matrix_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Classical Repertorization Grid",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Comparing remedies across ${totalityItems.size} totality symptoms",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.outline)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Matrix Table (Scrollable Horizontally)
          val horizontalScrollState = rememberScrollState()
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(10.dp))
              .clip(RoundedCornerShape(10.dp))
              .horizontalScroll(horizontalScrollState)
              .testTag("repertory_matrix_table")
          ) {
            // Table Header Row: Rubric column + Remedy columns
            Row(
              modifier = Modifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(vertical = 8.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Rubric (${totalityItems.size})",
                modifier = Modifier.width(170.dp).padding(start = 8.dp),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
              )
              topRemedies.forEach { remedy ->
                Column(
                  modifier = Modifier
                    .width(58.dp)
                    .clickable { onViewRemedyDetails(remedy.remedyAbbr) },
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = remedy.remedyAbbr,
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${remedy.totalScore}p",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  )
                }
              }
            }

            HorizontalDivider()

            // Rubric Rows
            totalityItems.forEachIndexed { index, item ->
              val rowBg = if (index % 2 == 0) MaterialTheme.colorScheme.surface
                          else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
              Row(
                modifier = Modifier
                  .background(rowBg)
                  .padding(vertical = 6.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                // Rubric title and weight
                Column(modifier = Modifier.width(170.dp).padding(start = 8.dp)) {
                  Text(
                    text = item.rubricName,
                    style = MaterialTheme.typography.bodySmall.copy(
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                  )
                  Text(
                    text = "${item.chapter} (${item.userIntensity}x weight)",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontSize = 9.sp,
                      color = MaterialTheme.colorScheme.outline
                    )
                  )
                }

                // Remedy Grade Cells
                topRemedies.forEach { remedy ->
                  val grade = remedy.rubricGrades[item.rubricId] ?: 0
                  Box(
                    modifier = Modifier.width(58.dp),
                    contentAlignment = Alignment.Center
                  ) {
                    if (grade > 0) {
                      val (badgeBg, badgeText, isBold) = when (grade) {
                        3 -> Triple(Grade3Color.copy(alpha = 0.16f), Grade3Color, true)
                        2 -> Triple(Grade2Color.copy(alpha = 0.14f), Grade2Color, true)
                        else -> Triple(Grade1Color.copy(alpha = 0.1f), Grade1Color, false)
                      }
                      Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = badgeBg,
                        modifier = Modifier.size(24.dp)
                      ) {
                        Box(contentAlignment = Alignment.Center) {
                          Text(
                            text = "$grade",
                            style = MaterialTheme.typography.labelSmall.copy(
                              fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Normal,
                              fontStyle = if (grade == 2) FontStyle.Italic else FontStyle.Normal,
                              color = badgeText,
                              fontSize = 11.sp
                            )
                          )
                        }
                      }
                    } else {
                      Text(
                        text = "–",
                        style = MaterialTheme.typography.labelSmall.copy(
                          color = MaterialTheme.colorScheme.outlineVariant,
                          fontSize = 12.sp
                        )
                      )
                    }
                  }
                }
              }
              HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }

            // Summary Totals Row
            Row(
              modifier = Modifier
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f))
                .padding(vertical = 8.dp, horizontal = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Coverage / Total Score",
                modifier = Modifier.width(170.dp).padding(start = 8.dp),
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
              )
              topRemedies.forEach { remedy ->
                Column(
                  modifier = Modifier.width(58.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${remedy.coverageCount}/${remedy.totalRubrics}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp
                    )
                  )
                  Text(
                    text = "${remedy.totalScore}",
                    style = MaterialTheme.typography.labelSmall.copy(
                      fontWeight = FontWeight.ExtraBold,
                      color = MaterialTheme.colorScheme.primary,
                      fontSize = 11.sp
                    )
                  )
                }
              }
            }
          }
        }
      }
    }

    // 3. Miasmatic Totality Analysis Card
    item {
      val miasms = analysis.miasmBreakdown
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("miasm_analysis_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "Chronic Miasmatic Diagnosis",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
              )
              Text(
                text = "Dominant Miasm: ${miasms.dominantMiasm}",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.SemiBold
                )
              )
            }

            Surface(
              shape = RoundedCornerShape(8.dp),
              color = MaterialTheme.colorScheme.primaryContainer
            ) {
              Text(
                text = "${miasms.dominantMiasm.uppercase()}",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontWeight = FontWeight.Bold,
                  color = MaterialTheme.colorScheme.onPrimaryContainer
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Miasm Progress Bars
          MiasmBarRow(name = "Psora (Functional)", percent = miasms.psoraPercent, color = MiasmPsora)
          Spacer(modifier = Modifier.height(6.dp))
          MiasmBarRow(name = "Sycosis (Hyper/Deposition)", percent = miasms.sycosisPercent, color = MiasmSycosis)
          Spacer(modifier = Modifier.height(6.dp))
          MiasmBarRow(name = "Syphilis (Destructive)", percent = miasms.syphilisPercent, color = MiasmSyphilis)
          Spacer(modifier = Modifier.height(6.dp))
          MiasmBarRow(name = "Tubercular (Hectic)", percent = miasms.tubercularPercent, color = MiasmTubercular)

          Spacer(modifier = Modifier.height(12.dp))
          Text(
            text = "Hahnemannian Rule: Treat the presenting acute exacerbation first. In chronic diseases, eradicate the dominant background miasm with deep-acting anti-miasmatic remedies.",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.outline,
              fontSize = 11.sp,
              fontStyle = FontStyle.Italic
            )
          )
        }
      }
    }

    // 4. Ranked Remedies Leaderboard Cards
    item {
      Text(
        text = "Top Ranked Candidate Remedies",
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
      )
    }

    items(analysis.scores.take(8).size) { index ->
      val remedyScore = analysis.scores[index]
      RemedyRankCard(
        rank = index + 1,
        score = remedyScore,
        onViewDetails = { onViewRemedyDetails(remedyScore.remedyAbbr) },
        onPrescribe = onOpenPrescription
      )
    }

    item {
      Spacer(modifier = Modifier.height(40.dp))
    }
  }
}

@Composable
fun MiasmBarRow(
  name: String,
  percent: Int,
  color: Color,
  modifier: Modifier = Modifier
) {
  Row(
    modifier = modifier.fillMaxWidth(),
    verticalAlignment = Alignment.CenterVertically,
    horizontalArrangement = Arrangement.SpaceBetween
  ) {
    Text(
      text = name,
      style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp, fontWeight = FontWeight.Medium),
      modifier = Modifier.width(160.dp)
    )
    LinearProgressIndicator(
      progress = { (percent.toFloat() / 100f).coerceIn(0f, 1f) },
      modifier = Modifier
        .weight(1f)
        .height(8.dp)
        .clip(RoundedCornerShape(4.dp)),
      color = color,
      trackColor = color.copy(alpha = 0.15f)
    )
    Spacer(modifier = Modifier.width(10.dp))
    Text(
      text = "$percent%",
      style = MaterialTheme.typography.labelSmall.copy(
        fontWeight = FontWeight.Bold,
        color = color,
        fontSize = 11.sp
      ),
      modifier = Modifier.width(36.dp),
      textAlign = TextAlign.End
    )
  }
}

@Composable
fun RemedyRankCard(
  rank: Int,
  score: RemedyScore,
  onViewDetails: () -> Unit,
  onPrescribe: () -> Unit,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("remedy_rank_card_$rank"),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          shape = CircleShape,
          color = when (rank) {
            1 -> GoldTertiaryLight
            2 -> Color(0xFF90A4AE)
            3 -> Color(0xFFB08D57)
            else -> MaterialTheme.colorScheme.surfaceVariant
          },
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Text(
              text = "#$rank",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
              )
            )
          }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = score.remedyFullName,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Surface(
              shape = RoundedCornerShape(4.dp),
              color = MaterialTheme.colorScheme.secondaryContainer
            ) {
              Text(
                text = score.remedyAbbr,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                style = MaterialTheme.typography.labelSmall.copy(
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Coverage: ${score.coverageCount}/${score.totalRubrics} rubrics • Thermal: ${score.thermal}",
            style = MaterialTheme.typography.bodySmall.copy(
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontSize = 11.sp
            )
          )
        }
      }

      Column(horizontalAlignment = Alignment.End) {
        Text(
          text = "${score.totalScore} Pts",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 16.sp
          )
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row {
          IconButton(
            onClick = onViewDetails,
            modifier = Modifier.size(28.dp).testTag("view_remedy_${score.remedyAbbr}")
          ) {
            Icon(Icons.Default.MenuBook, contentDescription = "Materia Medica", modifier = Modifier.size(18.dp))
          }
          IconButton(
            onClick = onPrescribe,
            modifier = Modifier.size(28.dp).testTag("prescribe_remedy_${score.remedyAbbr}")
          ) {
            Icon(Icons.Default.LocalPharmacy, contentDescription = "Prescribe", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
          }
        }
      }
    }
  }
}
