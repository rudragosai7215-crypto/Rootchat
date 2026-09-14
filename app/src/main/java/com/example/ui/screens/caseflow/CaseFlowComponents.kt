package com.example.ui.screens.caseflow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import com.example.data.RepertoryRepository
import com.example.data.RubricEntity
import com.example.data.SelectedRubric
import com.example.data.MateriaMedicaRepository
import com.example.ui.components.ChapterSemanticIcon
import com.example.ui.components.CircularChapterItem
import com.example.ui.screens.BotanicalLeafBranch
import com.example.ui.screens.materiamedica.MateriaMedicaViewerDialog
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.Grade1Color
import com.example.ui.theme.Grade2Color
import com.example.ui.theme.Grade3Color
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.launch

@Composable
fun ClinicalTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    testTag: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    minLines: Int = 1,
    readOnly: Boolean = false
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = if (isDark) TerracottaLight else TerracottaPrimary,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 13.sp) },
            singleLine = minLines == 1,
            minLines = minLines,
            readOnly = readOnly,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TerracottaPrimary,
                unfocusedBorderColor = inputBorderColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedContainerColor = if (isDark) Color(0x33000000) else Color(0x20FFFFFF),
                unfocusedContainerColor = if (isDark) Color(0x20000000) else Color(0x10FFFFFF)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag(testTag)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClinicalDropdown(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    testTag: String = "dropdown"
) {
    var expanded by remember { mutableStateOf(false) }
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    Column(modifier = modifier) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = if (isDark) TerracottaLight else TerracottaPrimary,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(4.dp))
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                singleLine = true,
                maxLines = 1,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(12.dp),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 13.sp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerracottaPrimary,
                    unfocusedBorderColor = inputBorderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedContainerColor = if (isDark) Color(0x33000000) else Color(0x20FFFFFF),
                    unfocusedContainerColor = if (isDark) Color(0x20000000) else Color(0x10FFFFFF)
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
                    .testTag(testTag)
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                maxLines = 1,
                                softWrap = false
                            )
                        },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun RubricRemovableChip(
    rubric: SelectedRubric,
    onRemove: () -> Unit,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val borderColor = if (isDark) TerracottaLight.copy(alpha = 0.5f) else TerracottaPrimary.copy(alpha = 0.5f)

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isDark) Color(0x33B5502F) else Color(0x20B5502F))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() },
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = rubric.chapter,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Text(
                text = rubric.rubricText,
                fontSize = 12.sp,
                color = textColor,
                maxLines = 1
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remove rubric",
                tint = if (isDark) CreamIvoryMuted else DarkBrownMuted,
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onRemove() }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RubricDetailDialog(
    rubricText: String,
    chapter: String,
    remediesJson: String,
    isSelected: Boolean,
    onToggleSelect: () -> Unit,
    onDismiss: () -> Unit,
    repertoryRepository: RepertoryRepository,
    onOpenMateriaMedica: ((String) -> Unit)? = null,
    isDark: Boolean
) {
    val remediesMap = remember(remediesJson) { RepertoryRepository.parseRubricRemedies(remediesJson) }

    // RootChart Design System Palette
    val tomeBgColor = if (isDark) Color(0xFF1E1B18) else Color(0xFFFAF7F2)
    val tomeCardBg = if (isDark) Color(0x22FFFFFF) else Color(0x0C000000)
    val goldAccent = if (isDark) TerracottaLight else TerracottaPrimary
    val tomeBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    // Group remedies by grade: 3, 2, 1
    val grade3 = remember(remediesMap) { remediesMap.filter { it.value >= 3 }.keys.sorted() }
    val grade2 = remember(remediesMap) { remediesMap.filter { it.value == 2 }.keys.sorted() }
    val grade1 = remember(remediesMap) { remediesMap.filter { it.value == 1 }.keys.sorted() }

    var selectedRemedyAbbr by remember { mutableStateOf<String?>(null) }
    var selectedRemedyFullName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedRemedyAbbr) {
        val abbr = selectedRemedyAbbr
        if (abbr != null) {
            selectedRemedyFullName = repertoryRepository.getRemedyFullName(abbr)
        } else {
            selectedRemedyFullName = null
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .heightIn(max = 580.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, tomeBorderColor, RoundedCornerShape(16.dp)),
            color = tomeBgColor
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Classic Tome Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        // Part 15: Breadcrumbs (Chapter > Rubric > Sub-rubric > Medicines > Totality)
                        val cleanRubric = rubricText.split(',').firstOrNull()?.trim() ?: rubricText
                        Text(
                            text = "${chapter.uppercase()} > $cleanRubric > Medicines > Totality",
                            fontFamily = FontFamily.Serif,
                            fontSize = 10.sp,
                            letterSpacing = 0.5.sp,
                            color = goldAccent.copy(alpha = 0.85f),
                            maxLines = 1,
                            modifier = Modifier.padding(bottom = 3.dp)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "❖ SECTION · ${chapter.uppercase()}",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.5.sp,
                                letterSpacing = 1.4.sp,
                                color = goldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rubricText,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            lineHeight = 22.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "${remediesMap.size} medicines listed in Kent's Repertorium Homoeopathicum",
                            fontFamily = FontFamily.Serif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor, modifier = Modifier.size(18.dp))
                    }
                }

                // Classical Filigree Divider
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(tomeBorderColor.copy(alpha = 0.5f)))
                    Surface(
                        color = tomeBgColor,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "❦ MATERIA MEDICA GRADES ❦",
                            fontSize = 9.sp,
                            letterSpacing = 1.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            fontFamily = FontFamily.Serif,
                            color = goldAccent
                        )
                    }
                }

                // Remedy Name Tooltip Card (if tapped)
                if (selectedRemedyAbbr != null) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(tomeCardBg)
                            .border(1.dp, goldAccent, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = selectedRemedyFullName ?: selectedRemedyAbbr!!.uppercase(),
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Abbreviation: ${selectedRemedyAbbr} · Grade: ${remediesMap[selectedRemedyAbbr] ?: 1} pts",
                                    fontSize = 10.5.sp,
                                    color = subtextColor
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                if (onOpenMateriaMedica != null) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, goldAccent, RoundedCornerShape(6.dp))
                                            .clickable {
                                                onOpenMateriaMedica(selectedRemedyAbbr!!)
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isDark) Color(0x33D4AF37) else Color(0x1AD4AF37)
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Filled.AutoStories,
                                                contentDescription = null,
                                                tint = goldAccent,
                                                modifier = Modifier.size(12.dp)
                                            )
                                            Text(
                                                text = "View MM",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = goldAccent
                                            )
                                        }
                                    }
                                }
                                IconButton(onClick = { selectedRemedyAbbr = null }, modifier = Modifier.size(24.dp)) {
                                    Icon(Icons.Default.Close, contentDescription = null, tint = subtextColor, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // List of Remedies grouped by Kentian Grade
                LazyColumn(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .heightIn(max = 320.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (grade3.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "✦ GRADE 3 · CHARACTERISTIC THREE-MARK (3 PTS):",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = Color(0xFFC0392B),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    grade3.forEach { rem ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFFC0392B).copy(alpha = 0.16f))
                                                .border(1.dp, Color(0xFFC0392B), RoundedCornerShape(6.dp))
                                                .clickable { selectedRemedyAbbr = rem }
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                            color = Color.Transparent
                                        ) {
                                            Text(
                                                text = rem.uppercase(),
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Serif,
                                                fontSize = 11.5.sp,
                                                color = Color(0xFFC0392B)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (grade2.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "✦ GRADE 2 · PROVEN TWO-MARK (2 PTS):",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = Color(0xFF386641),
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    grade2.forEach { rem ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(Color(0xFF386641).copy(alpha = 0.14f))
                                                .border(1.dp, Color(0xFF386641), RoundedCornerShape(6.dp))
                                                .clickable { selectedRemedyAbbr = rem }
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                            color = Color.Transparent
                                        ) {
                                            Text(
                                                text = rem.lowercase().replaceFirstChar { it.uppercase() },
                                                fontWeight = FontWeight.SemiBold,
                                                fontFamily = FontFamily.Serif,
                                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                fontSize = 11.5.sp,
                                                color = Color(0xFF386641)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (grade1.isNotEmpty()) {
                        item {
                            Column {
                                Text(
                                    text = "✦ GRADE 1 · CLINICAL ONE-MARK (1 PT):",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Serif,
                                    color = subtextColor,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    grade1.forEach { rem ->
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isDark) Color(0x22FFFFFF) else Color(0x10000000))
                                                .border(1.dp, tomeBorderColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                                                .clickable { selectedRemedyAbbr = rem }
                                                .padding(horizontal = 7.dp, vertical = 3.dp),
                                            color = Color.Transparent
                                        ) {
                                            Text(
                                                text = rem.lowercase(),
                                                fontSize = 11.sp,
                                                fontFamily = FontFamily.Serif,
                                                color = textColor
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Buttons (Part 13: Add to Totality & View Materia Medica)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tomeBorderColor)
                    ) {
                        Text("Close", color = subtextColor, fontFamily = FontFamily.Serif, fontSize = 11.5.sp)
                    }

                    if (onOpenMateriaMedica != null && remediesMap.isNotEmpty()) {
                        val targetRemedy = selectedRemedyAbbr ?: grade3.firstOrNull() ?: grade2.firstOrNull() ?: remediesMap.keys.firstOrNull() ?: ""
                        OutlinedButton(
                            onClick = {
                                if (targetRemedy.isNotBlank()) {
                                    onOpenMateriaMedica(targetRemedy)
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, goldAccent)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AutoStories,
                                contentDescription = null,
                                tint = goldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "View MM",
                                color = goldAccent,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            onToggleSelect()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF9E2A2B) else TerracottaPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Close else Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = if (isSelected) "Remove from Totality" else "+ Add to Totality",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun getChapterSemanticIcon(chapterName: String): ImageVector {
    val norm = chapterName.uppercase().trim()
    return when {
        norm.contains("MIND") -> Icons.Filled.Psychology
        norm.contains("EYE") || norm.contains("VISION") -> Icons.Filled.Visibility
        norm.contains("EAR") || norm.contains("HEAR") -> Icons.Filled.Hearing
        norm.contains("NOSE") || norm.contains("SMELL") -> Icons.Filled.Air
        norm.contains("FACE") || norm.contains("MOUTH") || norm.contains("TEETH") -> Icons.Filled.Face
        norm.contains("STOMACH") || norm.contains("APPETITE") -> Icons.Filled.Restaurant
        norm.contains("ABDOMEN") -> Icons.Filled.Fastfood
        norm.contains("RECTUM") || norm.contains("STOOL") -> Icons.Filled.CleaningServices
        norm.contains("URIN") || norm.contains("BLADDER") || norm.contains("KIDNEY") || norm.contains("URETHRA") -> Icons.Filled.WaterDrop
        norm.contains("GENITALIA") -> Icons.Filled.FavoriteBorder
        norm.contains("RESPIRATION") || norm.contains("COUGH") || norm.contains("EXPECTORATION") || norm.contains("LUNG") -> Icons.Filled.Air
        norm.contains("HEART") || norm.contains("PULSE") || norm.contains("CIRCULATION") -> Icons.Filled.Favorite
        norm.contains("CHEST") -> Icons.Filled.Shield
        norm.contains("BACK") -> Icons.Filled.FitnessCenter
        norm.contains("EXTREMITIES") -> Icons.Filled.DirectionsRun
        norm.contains("SLEEP") || norm.contains("DREAM") -> Icons.Filled.Bedtime
        norm.contains("FEVER") || norm.contains("CHILL") || norm.contains("SWEAT") || norm.contains("PERSPIRATION") -> Icons.Filled.Thermostat
        norm.contains("SKIN") -> Icons.Filled.Spa
        norm.contains("GENERAL") -> Icons.Filled.Healing
        else -> Icons.Filled.MenuBook
    }
}

@Composable
fun RepertoryBrowseDialog(
    onDismiss: () -> Unit,
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    materiaMedicaRepository: MateriaMedicaRepository? = null,
    isDark: Boolean
) {
    var allDbChapters by remember { mutableStateOf<List<String>>(emptyList()) }
    var classicalChapters by remember { mutableStateOf<List<BrowseChapterItem>>(emptyList()) }
    var additionalChapters by remember { mutableStateOf<List<BrowseChapterItem>>(emptyList()) }
    var selectedItem by remember { mutableStateOf<BrowseChapterItem?>(null) }
    var selectedSubSection by remember { mutableStateOf<BrowseSubSection?>(null) }
    var chapterRubrics by remember { mutableStateOf<List<RubricEntity>>(emptyList()) }
    var totalRubricCount by remember { mutableIntStateOf(0) }
    var currentOffset by remember { mutableIntStateOf(0) }
    var isLoadingMore by remember { mutableStateOf(false) }
    var hasMorePages by remember { mutableStateOf(false) }
    var chapterSearchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showIndexDialog by remember { mutableStateOf(false) }
    var selectedCategoryTab by remember { mutableIntStateOf(0) }
    var viewedMateriaMedicaRemedy by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Rubric preview dialog
    var previewRubric by remember { mutableStateOf<RubricEntity?>(null) }

    // Repertory Palette aligned with RootChart Design System
    val tomeBgColor = if (isDark) Color(0xFF181513) else Color(0xFFFAF7F2)
    val tomeSurfaceColor = if (isDark) Color(0xFF221E1B) else Color(0xFFF3ECE0)
    val goldAccent = if (isDark) TerracottaLight else TerracottaPrimary
    val tomeBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    LaunchedEffect(Unit) {
        val dbChs = repertoryRepository.getAllChapters()
        allDbChapters = dbChs
        val (classical, additional) = ClassicalRepertoryCatalog.buildSections(dbChs)
        classicalChapters = classical
        additionalChapters = additional
        if (classical.isNotEmpty() && selectedItem == null) {
            val first = classical.first()
            selectedItem = first
            selectedSubSection = first.subSections.firstOrNull()
        }
    }

    LaunchedEffect(selectedItem, selectedSubSection, chapterSearchQuery) {
        val item = selectedItem
        if (item != null) {
            isLoading = true
            currentOffset = 0
            val targetChapters = selectedSubSection?.rawChapters ?: item.rawChapters
            val (pagedList, total) = repertoryRepository.getRubricsInChaptersPaged(
                chapters = targetChapters,
                query = chapterSearchQuery,
                limit = 80,
                offset = 0
            )
            chapterRubrics = pagedList
            totalRubricCount = total
            currentOffset = pagedList.size
            hasMorePages = currentOffset < total
            isLoading = false
        }
    }

    val loadNextPage: () -> Unit = {
        val item = selectedItem
        if (item != null && !isLoadingMore && hasMorePages) {
            isLoadingMore = true
            coroutineScope.launch {
                val targetChapters = selectedSubSection?.rawChapters ?: item.rawChapters
                val (nextPage, _) = repertoryRepository.getRubricsInChaptersPaged(
                    chapters = targetChapters,
                    query = chapterSearchQuery,
                    limit = 80,
                    offset = currentOffset
                )
                if (nextPage.isNotEmpty()) {
                    val existingIds = chapterRubrics.map { it.id }.toSet()
                    val newItems = nextPage.filter { it.id !in existingIds }
                    chapterRubrics = chapterRubrics + newItems
                    currentOffset += nextPage.size
                    hasMorePages = currentOffset < totalRubricCount
                } else {
                    hasMorePages = false
                }
                isLoadingMore = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .clip(RoundedCornerShape(18.dp))
                .border(2.dp, tomeBorderColor, RoundedCornerShape(18.dp)),
            color = tomeBgColor
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // 1. Classical Header with Back, Title, and Botanical Flourish (Part 4, 8)
                Surface(
                    color = tomeSurfaceColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 10.dp, end = 14.dp, top = 12.dp, bottom = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                IconButton(
                                    onClick = onDismiss,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = textColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = "Browse Repertory",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
                                    )
                                    Text(
                                        text = "Kent's Repertory · Classical",
                                        fontFamily = FontFamily.Serif,
                                        fontStyle = FontStyle.Italic,
                                        fontSize = 11.5.sp,
                                        color = goldAccent
                                    )
                                }
                            }

                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor, modifier = Modifier.size(18.dp))
                            }
                        }

                        BotanicalLeafBranch(
                            tint = goldAccent.copy(alpha = 0.35f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 48.dp)
                                .size(width = 38.dp, height = 24.dp)
                        )
                    }
                }

                // Decorative double border line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.5.dp)
                        .background(tomeBorderColor)
                )

                // 2. Category Selector Tabs (Part 9): Classical Chapters (33), Additional Chapters (2), Index
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = if (selectedCategoryTab == 0) TerracottaPrimary else (if (isDark) Color(0xFF2E2018) else Color(0xFFEBE0D2)),
                            border = BorderStroke(1.dp, if (selectedCategoryTab == 0) goldAccent else tomeBorderColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                selectedCategoryTab = 0
                                if (selectedItem?.isClassical != true) {
                                    val first = classicalChapters.firstOrNull()
                                    if (first != null) {
                                        selectedItem = first
                                        selectedSubSection = first.subSections.firstOrNull()
                                        chapterSearchQuery = ""
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Classical Chapters (${classicalChapters.size})",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedCategoryTab == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                color = if (selectedCategoryTab == 0) Color.White else textColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            color = if (selectedCategoryTab == 1) (if (isDark) SageGreenLight.copy(alpha = 0.35f) else SageGreenPrimary.copy(alpha = 0.25f))
                                   else (if (isDark) Color(0xFF1E2B22) else Color(0xFFDCEEE3)),
                            border = BorderStroke(1.dp, if (selectedCategoryTab == 1) (if (isDark) SageGreenLight else SageGreenPrimary) else tomeBorderColor),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.clickable {
                                selectedCategoryTab = 1
                                if (selectedItem?.isClassical == true) {
                                    val first = additionalChapters.firstOrNull()
                                    if (first != null) {
                                        selectedItem = first
                                        selectedSubSection = null
                                        chapterSearchQuery = ""
                                    }
                                }
                            }
                        ) {
                            Text(
                                text = "Additional Chapters (${additionalChapters.size})",
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (selectedCategoryTab == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp,
                                color = if (selectedCategoryTab == 1) (if (isDark) SageGreenLight else SageGreenPrimary) else textColor,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    TextButton(
                        onClick = { showIndexDialog = true },
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp)
                    ) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = goldAccent, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Index",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.5.sp,
                            color = goldAccent
                        )
                    }
                }

                // 3. Circular Semantic Chapter Icons (Part 5, 8, 10)
                val activeChapterList = if (selectedCategoryTab == 0) classicalChapters else additionalChapters
                if (activeChapterList.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        items(activeChapterList, key = { it.id }) { chItem ->
                            val isChSelected = chItem.id == selectedItem?.id
                            CircularChapterItem(
                                item = chItem,
                                isSelected = isChSelected,
                                isDark = isDark,
                                onClick = {
                                    selectedItem = chItem
                                    selectedSubSection = chItem.subSections.firstOrNull()
                                    chapterSearchQuery = ""
                                }
                            )
                        }
                    }
                }

                // Sub-sections (for composite chapters like Stomach, Urinary Organs, Chest)
                if (selectedItem != null && selectedItem!!.isComposite) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "Sub-sections:",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 10.sp,
                            color = subtextColor
                        )

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(selectedItem!!.subSections) { sub ->
                                val isSubSelected = sub == selectedSubSection
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(
                                            if (isSubSelected) goldAccent.copy(alpha = 0.25f) else (if (isDark) Color(0x33000000) else Color(0x15000000))
                                        )
                                        .border(
                                            1.dp,
                                            if (isSubSelected) goldAccent else tomeBorderColor.copy(alpha = 0.4f),
                                            RoundedCornerShape(6.dp)
                                        )
                                        .clickable {
                                            selectedSubSection = sub
                                            chapterSearchQuery = ""
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp),
                                    color = Color.Transparent
                                ) {
                                    Text(
                                        text = sub.title,
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = if (isSubSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 10.5.sp,
                                        color = if (isSubSelected) (if (isDark) Color.White else DarkBrownText) else textColor
                                    )
                                }
                            }
                        }
                    }
                }

                // 4. Search Bar
                OutlinedTextField(
                    value = chapterSearchQuery,
                    onValueChange = { chapterSearchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search rubrics in ${selectedItem?.title ?: "Kent's Repertory"}...",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontSize = 11.5.sp,
                            color = subtextColor
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = goldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (chapterSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { chapterSearchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = subtextColor, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor,
                        focusedBorderColor = TerracottaPrimary,
                        unfocusedBorderColor = tomeBorderColor,
                        focusedContainerColor = tomeSurfaceColor,
                        unfocusedContainerColor = tomeSurfaceColor
                    )
                )

                // 5. Selected Chapter Info Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ChapterSemanticIcon(
                            chapterName = selectedItem?.title ?: "",
                            tint = if (isDark) goldAccent else TerracottaPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${selectedItem?.orderNumber?.let { "$it. " } ?: ""}${selectedItem?.title?.uppercase() ?: ""}${if (selectedSubSection != null) " · ${selectedSubSection?.title}" else ""}",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = if (isDark) goldAccent else TerracottaPrimary
                        )
                    }

                    Text(
                        text = "$totalRubricCount rubrics →",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.5.sp,
                        color = subtextColor
                    )
                }

                // 3. Classic Rubrics Catalog List
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "Searching classical pages...",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    fontSize = 13.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    } else if (chapterRubrics.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.padding(24.dp)
                            ) {
                                Text(
                                    text = "No rubrics found in section",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "Try clearing the search query or selecting another chapter above.",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(7.dp)
                        ) {
                            item {
                                Text(
                                    text = "Showing ${chapterRubrics.size} of $totalRubricCount rubrics in [${selectedItem?.title ?: ""}${if (selectedSubSection != null) " → ${selectedSubSection?.title}" else ""}]",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 11.sp,
                                    color = subtextColor,
                                    modifier = Modifier.padding(vertical = 2.dp)
                                )
                            }

                            items(chapterRubrics, key = { it.id }) { rub ->
                                val isSelected = selectedRubrics.any { it.id == rub.id }
                                val remMap = remember(rub.remediesJson) { RepertoryRepository.parseRubricRemedies(rub.remediesJson) }
                                val isSubRubric = rub.rubricText.startsWith("-") || rub.rubricText.contains(" - ") || rub.rubricText.contains(", ")

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = if (isSubRubric) 10.dp else 0.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(
                                            if (isSelected) TerracottaPrimary.copy(alpha = 0.16f) else tomeSurfaceColor
                                        )
                                        .border(
                                            if (isSelected) 1.5.dp else 1.dp,
                                            if (isSelected) TerracottaPrimary else tomeBorderColor.copy(alpha = 0.5f),
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { previewRubric = rub }
                                        .padding(horizontal = 14.dp, vertical = 9.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                                if (isSubRubric) {
                                                    Text(
                                                        text = "↳",
                                                        fontSize = 12.sp,
                                                        color = goldAccent,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                HighlightedRubricText(
                                                    text = rub.rubricText,
                                                    query = chapterSearchQuery,
                                                    baseColor = if (isSelected) TerracottaPrimary else textColor,
                                                    highlightColor = if (isDark) Color(0xFFFFCC80) else Color(0xFFBF360C),
                                                    fontSize = 13.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    fontFamily = FontFamily.Serif
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(4.dp))

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Surface(
                                                    color = if (isDark) Color(0x33B5502F) else Color(0x22B5502F),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = rub.chapter,
                                                        fontSize = 9.5.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TerracottaPrimary,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                    )
                                                }

                                                Surface(
                                                    color = if (isDark) Color(0x22D4AF37) else Color(0x22B8860B),
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Kent's Repertory",
                                                        fontSize = 9.5.sp,
                                                        color = goldAccent,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.5.dp)
                                                    )
                                                }

                                                Text(
                                                    text = "·",
                                                    fontSize = 10.sp,
                                                    color = subtextColor
                                                )

                                                Text(
                                                    text = "${remMap.size} remedies",
                                                    fontFamily = FontFamily.Serif,
                                                    fontStyle = FontStyle.Italic,
                                                    fontSize = 10.sp,
                                                    color = subtextColor
                                                )
                                            }
                                        }

                                        // Action Button: Add/Remove Rubric
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isSelected) TerracottaPrimary else (if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) goldAccent else tomeBorderColor,
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable {
                                                    if (isSelected) {
                                                        onRemoveRubric(rub.id)
                                                    } else {
                                                        onAddRubric(
                                                            SelectedRubric(
                                                                id = rub.id,
                                                                chapter = rub.chapter,
                                                                rubricText = rub.rubricText,
                                                                remedies = remMap
                                                            )
                                                        )
                                                    }
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp),
                                            color = Color.Transparent
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = if (isSelected) Icons.Default.Check else Icons.Default.Add,
                                                    contentDescription = null,
                                                    tint = if (isSelected) Color.White else textColor,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = if (isSelected) "Selected" else "Add",
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = if (isSelected) Color.White else textColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            if (hasMorePages) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isDark) Color(0xFF2E2018) else Color(0xFFEBE0D2))
                                                .border(1.dp, goldAccent.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                                .clickable(enabled = !isLoadingMore) { loadNextPage() }
                                                .padding(horizontal = 16.dp, vertical = 8.dp),
                                            color = Color.Transparent
                                        ) {
                                            if (isLoadingMore) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    CircularProgressIndicator(
                                                        modifier = Modifier.size(14.dp),
                                                        strokeWidth = 2.dp,
                                                        color = TerracottaPrimary
                                                    )
                                                    Text(
                                                        text = "Loading next rubrics...",
                                                        fontFamily = FontFamily.Serif,
                                                        fontSize = 11.sp,
                                                        color = subtextColor
                                                    )
                                                }
                                            } else {
                                                Text(
                                                    text = "Load More (${(totalRubricCount - chapterRubrics.size).coerceAtLeast(0)} remaining) ▾",
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 11.sp,
                                                    color = TerracottaPrimary
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 4. Classic Bottom Action Footer
                Surface(
                    color = tomeSurfaceColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "TOTALITY SELECTION",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.2.sp,
                                color = goldAccent
                            )
                            Text(
                                text = "${selectedRubrics.size} rubrics selected for case",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = textColor
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Done Repertorizing",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Rubric Detail Preview Dialog
    previewRubric?.let { rub ->
        val isSelected = selectedRubrics.any { it.id == rub.id }
        RubricDetailDialog(
            rubricText = rub.rubricText,
            chapter = rub.chapter,
            remediesJson = rub.remediesJson,
            isSelected = isSelected,
            onToggleSelect = {
                if (isSelected) {
                    onRemoveRubric(rub.id)
                } else {
                    val remMap = RepertoryRepository.parseRubricRemedies(rub.remediesJson)
                    onAddRubric(
                        SelectedRubric(
                            id = rub.id,
                            chapter = rub.chapter,
                            rubricText = rub.rubricText,
                            remedies = remMap
                        )
                    )
                }
            },
            onDismiss = { previewRubric = null },
            repertoryRepository = repertoryRepository,
            onOpenMateriaMedica = { remedyAbbr ->
                viewedMateriaMedicaRemedy = remedyAbbr
            },
            isDark = isDark
        )
    }

    // Materia Medica Viewer Dialog (when launched from Rubric detail)
    viewedMateriaMedicaRemedy?.let { remAbbr ->
        materiaMedicaRepository?.let { mmRepo ->
            MateriaMedicaViewerDialog(
                remedyAbbreviation = remAbbr,
                selectedRubrics = selectedRubrics,
                materiaMedicaRepository = mmRepo,
                onDismiss = { viewedMateriaMedicaRemedy = null },
                isDark = isDark
            )
        }
    }

    // Full Classical Table of Contents Dialog
    if (showIndexDialog) {
        ClassicalIndexDialog(
            classicalChapters = classicalChapters,
            additionalChapters = additionalChapters,
            selectedItem = selectedItem,
            onSelectChapter = { ch ->
                selectedItem = ch
                selectedSubSection = ch.subSections.firstOrNull()
                chapterSearchQuery = ""
            },
            onDismiss = { showIndexDialog = false },
            isDark = isDark
        )
    }
}

@Composable
fun ClassicalIndexDialog(
    classicalChapters: List<BrowseChapterItem>,
    additionalChapters: List<BrowseChapterItem>,
    selectedItem: BrowseChapterItem?,
    onSelectChapter: (BrowseChapterItem) -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean
) {
    val tomeBgColor = if (isDark) Color(0xFF1B130E) else Color(0xFFFAF6EE)
    val tomeSurfaceColor = if (isDark) Color(0xFF261B14) else Color(0xFFF3ECE0)
    val goldAccent = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
    val tomeBorderColor = if (isDark) Color(0xFF8B5A2B) else Color(0xFFC4A482)
    val textColor = if (isDark) Color(0xFFF7EFE6) else Color(0xFF2B1D16)
    val subtextColor = if (isDark) Color(0xFFC7B8A8) else Color(0xFF755B49)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.85f)
                .clip(RoundedCornerShape(16.dp))
                .border(2.dp, tomeBorderColor, RoundedCornerShape(16.dp)),
            color = tomeBgColor
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Surface(
                    color = tomeSurfaceColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "INDEX REPERTORII",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 1.5.sp,
                                color = goldAccent
                            )
                            Text(
                                text = "Classical Chapter Catalog",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Text(
                                text = "${classicalChapters.size} Classical Chapters • ${additionalChapters.size} Additional Chapters",
                                fontFamily = FontFamily.Serif,
                                fontSize = 11.sp,
                                color = subtextColor
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x33FFFFFF) else Color(0x15000000))
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(tomeBorderColor))

                // Scrollable chapters list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // SECTION 1 Header
                    item {
                        Surface(
                            color = if (isDark) Color(0xFF332216) else Color(0xFFE8DCCD),
                            border = BorderStroke(1.dp, goldAccent.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "SECTION 1 — KENT'S REPERTORY (${classicalChapters.size} Classical Chapters)",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    color = goldAccent
                                )
                                Text(
                                    text = "Displaying chapters in Dr. Kent's classical hierarchy (1. Mind to 33. Generalities)",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 10.5.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    }

                    // Section 1 items
                    items(classicalChapters, key = { it.id }) { chItem ->
                        val isSelected = chItem.id == selectedItem?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) TerracottaPrimary.copy(alpha = 0.18f) else tomeSurfaceColor
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) TerracottaPrimary else tomeBorderColor.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectChapter(chItem)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = if (isSelected) TerracottaPrimary else goldAccent.copy(alpha = 0.2f),
                                        shape = CircleShape,
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = getChapterSemanticIcon(chItem.title),
                                                contentDescription = null,
                                                tint = if (isSelected) Color.White else (if (isDark) goldAccent else DarkBrownText),
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "${chItem.orderNumber}.",
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.5.sp,
                                                color = if (isSelected) TerracottaPrimary else goldAccent
                                            )
                                            Text(
                                                text = chItem.title,
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                                fontSize = 13.5.sp,
                                                color = if (isSelected) TerracottaPrimary else textColor
                                            )
                                        }

                                        if (chItem.isComposite) {
                                            Text(
                                                text = "Sub-sections: ${chItem.subSections.joinToString(", ") { it.title }}",
                                                fontFamily = FontFamily.Serif,
                                                fontStyle = FontStyle.Italic,
                                                fontSize = 10.sp,
                                                color = subtextColor
                                            )
                                        }
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = TerracottaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    // SECTION 2 Header
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = if (isDark) Color(0xFF1E2B22) else Color(0xFFDCEEE3),
                            border = BorderStroke(1.dp, if (isDark) SageGreenLight.copy(alpha = 0.6f) else SageGreenPrimary.copy(alpha = 0.6f)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "SECTION 2 — ADDITIONAL RUBRICS (${additionalChapters.size} Chapters)",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    letterSpacing = 1.sp,
                                    color = if (isDark) SageGreenLight else SageGreenPrimary
                                )
                                Text(
                                    text = "Additional / Extended Rubrics (not part of Kent's original 37 chapters)",
                                    fontFamily = FontFamily.Serif,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 10.5.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    }

                    // Section 2 items
                    items(additionalChapters, key = { it.id }) { addChItem ->
                        val isSelected = addChItem.id == selectedItem?.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) (if (isDark) SageGreenLight.copy(alpha = 0.25f) else SageGreenPrimary.copy(alpha = 0.2f)) else tomeSurfaceColor
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) (if (isDark) SageGreenLight else SageGreenPrimary) else tomeBorderColor.copy(alpha = 0.5f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onSelectChapter(addChItem)
                                    onDismiss()
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            color = Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        color = if (isSelected) (if (isDark) SageGreenLight.copy(alpha = 0.4f) else SageGreenPrimary.copy(alpha = 0.3f)) else (if (isDark) Color(0xFF1F2B23) else Color(0xFFE2EFE7)),
                                        shape = CircleShape,
                                        modifier = Modifier.size(26.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Icon(
                                                imageVector = getChapterSemanticIcon(addChItem.title),
                                                contentDescription = null,
                                                tint = if (isDark) SageGreenLight else SageGreenPrimary,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    Column {
                                        Text(
                                            text = addChItem.title,
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                            fontSize = 13.5.sp,
                                            color = if (isSelected) (if (isDark) SageGreenLight else SageGreenPrimary) else textColor
                                        )
                                        Text(
                                            text = "Supplementary clinical section",
                                            fontFamily = FontFamily.Serif,
                                            fontStyle = FontStyle.Italic,
                                            fontSize = 10.sp,
                                            color = subtextColor
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Active",
                                        tint = if (isDark) SageGreenLight else SageGreenPrimary,
                                        modifier = Modifier.size(18.dp)
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
fun HighlightedRubricText(
    text: String,
    query: String,
    baseColor: Color,
    highlightColor: Color,
    fontSize: TextUnit,
    fontWeight: FontWeight = FontWeight.Normal,
    fontFamily: FontFamily? = null,
    modifier: Modifier = Modifier
) {
    val q = query.trim().lowercase()
    if (q.isBlank()) {
        Text(
            text = text,
            color = baseColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            modifier = modifier
        )
        return
    }

    val cleanedQ = q.replace("[-:,;./]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
    val tokens = cleanedQ.split(" ").filter { it.length >= 2 }
    if (tokens.isEmpty()) {
        Text(
            text = text,
            color = baseColor,
            fontSize = fontSize,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            modifier = modifier
        )
        return
    }

    val annotatedString = buildAnnotatedString {
        val lowerText = text.lowercase()

        // 1. Priority 1 (Highest): Find exact phrase match intervals
        val exactPhrases = mutableListOf<String>()
        if (q.length >= 2) exactPhrases.add(q)
        if (cleanedQ != q && cleanedQ.length >= 2) exactPhrases.add(cleanedQ)

        val exactIntervals = mutableListOf<IntRange>()
        for (phrase in exactPhrases.distinct()) {
            var startIndex = 0
            while (startIndex < lowerText.length) {
                val found = lowerText.indexOf(phrase, startIndex)
                if (found == -1) break
                exactIntervals.add(found until (found + phrase.length))
                startIndex = found + 1
            }
        }

        val mergedExact = if (exactIntervals.isNotEmpty()) {
            val sorted = exactIntervals.sortedBy { it.first }
            val merged = mutableListOf<IntRange>()
            var curr = sorted[0]
            for (i in 1 until sorted.size) {
                val nxt = sorted[i]
                if (nxt.first <= curr.last + 1) {
                    curr = curr.first..maxOf(curr.last, nxt.last)
                } else {
                    merged.add(curr)
                    curr = nxt
                }
            }
            merged.add(curr)
            merged
        } else {
            emptyList()
        }

        // 2. Priority 2 (Lower priority): Find individual word intervals outside exact phrase matches
        val wordIntervals = mutableListOf<IntRange>()
        for (token in tokens) {
            var startIndex = 0
            while (startIndex < lowerText.length) {
                val found = lowerText.indexOf(token, startIndex)
                if (found == -1) break
                val range = found until (found + token.length)
                val overlapsExact = mergedExact.any { ex ->
                    maxOf(ex.first, range.first) <= minOf(ex.last, range.last)
                }
                if (!overlapsExact) {
                    wordIntervals.add(range)
                }
                startIndex = found + 1
            }
        }

        val mergedWord = if (wordIntervals.isNotEmpty()) {
            val sorted = wordIntervals.sortedBy { it.first }
            val merged = mutableListOf<IntRange>()
            var curr = sorted[0]
            for (i in 1 until sorted.size) {
                val nxt = sorted[i]
                if (nxt.first <= curr.last + 1) {
                    curr = curr.first..maxOf(curr.last, nxt.last)
                } else {
                    merged.add(curr)
                    curr = nxt
                }
            }
            merged.add(curr)
            merged
        } else {
            emptyList()
        }

        if (mergedExact.isEmpty() && mergedWord.isEmpty()) {
            append(text)
            return@buildAnnotatedString
        }

        // Combine all intervals with priority indicator
        data class StyledSpan(val range: IntRange, val isExact: Boolean)
        val allSpans = (mergedExact.map { StyledSpan(it, isExact = true) } +
                mergedWord.map { StyledSpan(it, isExact = false) })
            .sortedBy { it.range.first }

        var cursor = 0
        for (span in allSpans) {
            val range = span.range
            if (range.first > cursor) {
                append(text.substring(cursor, range.first))
            }
            withStyle(
                SpanStyle(
                    color = highlightColor,
                    fontWeight = if (span.isExact) FontWeight.ExtraBold else FontWeight.Bold,
                    background = highlightColor.copy(alpha = if (span.isExact) 0.35f else 0.18f)
                )
            ) {
                append(text.substring(range.first, minOf(range.last + 1, text.length)))
            }
            cursor = minOf(range.last + 1, text.length)
        }
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }

    Text(
        text = annotatedString,
        color = baseColor,
        fontSize = fontSize,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        modifier = modifier
    )
}
