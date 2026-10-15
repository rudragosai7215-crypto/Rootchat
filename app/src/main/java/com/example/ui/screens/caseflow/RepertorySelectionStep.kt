package com.example.ui.screens.caseflow

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClinicalFormData
import com.example.data.RemedyDao
import com.example.data.RepertoryRepository
import com.example.data.RubricDao
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

/**
 * Step: Selection of Repertory
 *
 * Placed immediately before Homeopathic Repertorization in both Acute and Chronic protocols.
 * Clean, simplified UI with only 2 selectable options:
 * 1. Original Kent Repertory (1897)
 * 2. Extended Kent Repertory
 */
@Composable
fun RepertorySelectionStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    repertoryRepository: RepertoryRepository,
    rubricCount: Int,
    remedyCount: Int,
    onNavigateNext: () -> Unit,
    isDark: Boolean,
    rubricDao: RubricDao? = null,
    remedyDao: RemedyDao? = null
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val accentColor = if (isDark) TerracottaLight else TerracottaPrimary
    val borderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    val selectedType = formData.repertoryType.ifBlank { RepertoryRepository.REPERTORY_ORIGINAL_KENT }
    val isOriginalSelected = selectedType == RepertoryRepository.REPERTORY_ORIGINAL_KENT
    val isExtendedSelected = !isOriginalSelected

    GlassPanel(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("step_repertory_selection"),
        isDark = isDark
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = accentColor.copy(alpha = 0.15f),
                    shape = CircleShape,
                    modifier = Modifier.size(38.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Column {
                    Text(
                        text = "REPERTORY SPECIFICATION",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.3.sp,
                        color = accentColor
                    )
                    Text(
                        text = "Selection of Repertory",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // =========================================================================
            // OPTION 1: ORIGINAL KENT REPERTORY
            // =========================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isOriginalSelected) 2.dp else 1.dp,
                        color = if (isOriginalSelected) accentColor else borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        onUpdateForm(formData.copy(repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT))
                    }
                    .testTag("option_original_kent"),
                color = if (isOriginalSelected) {
                    if (isDark) Color(0x22B5502F) else Color(0x0EB5502F)
                } else {
                    if (isDark) Color(0x10FFFFFF) else Color(0x06000000)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = isOriginalSelected,
                            onClick = {
                                onUpdateForm(formData.copy(repertoryType = RepertoryRepository.REPERTORY_ORIGINAL_KENT))
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = accentColor,
                                unselectedColor = subtextColor
                            ),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Original Kent Repertory",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    if (isOriginalSelected) {
                        Surface(
                            color = accentColor,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SELECTED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // OPTION 2: EXTENDED KENT REPERTORY
            // =========================================================================
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        width = if (isExtendedSelected) 2.dp else 1.dp,
                        color = if (isExtendedSelected) accentColor else borderColor,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable {
                        onUpdateForm(formData.copy(repertoryType = RepertoryRepository.REPERTORY_EXTENDED_KENT))
                    }
                    .testTag("option_extended_kent"),
                color = if (isExtendedSelected) {
                    if (isDark) Color(0x22B5502F) else Color(0x0EB5502F)
                } else {
                    if (isDark) Color(0x10FFFFFF) else Color(0x06000000)
                }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        RadioButton(
                            selected = isExtendedSelected,
                            onClick = {
                                onUpdateForm(formData.copy(repertoryType = RepertoryRepository.REPERTORY_EXTENDED_KENT))
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = accentColor,
                                unselectedColor = subtextColor
                            ),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Extended Kent Repertory",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                    }

                    if (isExtendedSelected) {
                        Surface(
                            color = accentColor,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "SELECTED",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Navigation Button to proceed to Homeopathic Repertorization
            Button(
                onClick = onNavigateNext,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("btn_proceed_to_repertorization"),
                colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Continue to Homeopathic Repertorization",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
