package com.example.ui.screens.caseflow

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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RepertoryRepository
import com.example.data.RubricEntity
import com.example.data.SelectedRubric
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
    isDark: Boolean
) {
    val remediesMap = remember(remediesJson) { RepertoryRepository.parseRubricRemedies(remediesJson) }

    // Classic parchment / antique leather palette
    val tomeBgColor = if (isDark) Color(0xFF1F1712) else Color(0xFFFAF6EE)
    val tomeCardBg = if (isDark) Color(0xFF291E18) else Color(0xFFF4ECE0)
    val goldAccent = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
    val tomeBorderColor = if (isDark) Color(0xFF8B5A2B) else Color(0xFFC4A482)
    val textColor = if (isDark) Color(0xFFF7EFE6) else Color(0xFF2B1D16)
    val subtextColor = if (isDark) Color(0xFFC7B8A8) else Color(0xFF755B49)

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
                            text = "${remediesMap.size} remedies listed in Kent's Materia Medica",
                            fontFamily = FontFamily.Serif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.5.sp,
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
                            IconButton(onClick = { selectedRemedyAbbr = null }, modifier = Modifier.size(24.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = subtextColor, modifier = Modifier.size(14.dp))
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

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tomeBorderColor)
                    ) {
                        Text("Close", color = subtextColor, fontFamily = FontFamily.Serif)
                    }

                    Button(
                        onClick = {
                            onToggleSelect()
                            onDismiss()
                        },
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) Color(0xFF9E2A2B) else TerracottaPrimary
                        )
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.Close else Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSelected) "Remove from Case" else "Add Rubric to Case",
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

@Composable
fun RepertoryBrowseDialog(
    onDismiss: () -> Unit,
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    isDark: Boolean
) {
    var chapters by remember { mutableStateOf<List<String>>(emptyList()) }
    var selectedChapter by remember { mutableStateOf<String?>(null) }
    var chapterRubrics by remember { mutableStateOf<List<RubricEntity>>(emptyList()) }
    var chapterSearchQuery by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    // Rubric preview dialog
    var previewRubric by remember { mutableStateOf<RubricEntity?>(null) }

    // Classic Repertory Tome Palette
    val tomeBgColor = if (isDark) Color(0xFF1B130E) else Color(0xFFFAF6EE)
    val tomeSurfaceColor = if (isDark) Color(0xFF261B14) else Color(0xFFF3ECE0)
    val goldAccent = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
    val tomeBorderColor = if (isDark) Color(0xFF8B5A2B) else Color(0xFFC4A482)
    val textColor = if (isDark) Color(0xFFF7EFE6) else Color(0xFF2B1D16)
    val subtextColor = if (isDark) Color(0xFFC7B8A8) else Color(0xFF755B49)

    LaunchedEffect(Unit) {
        chapters = repertoryRepository.getAllChapters()
        if (chapters.isNotEmpty() && selectedChapter == null) {
            selectedChapter = chapters.first()
        }
    }

    LaunchedEffect(selectedChapter, chapterSearchQuery) {
        val ch = selectedChapter
        if (ch != null) {
            isLoading = true
            chapterRubrics = repertoryRepository.searchRubricsInChapter(ch, chapterSearchQuery, limit = 180)
            isLoading = false
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
                // 1. Classical Book Spine Header
                Surface(
                    color = tomeSurfaceColor,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp, end = 18.dp, top = 14.dp, bottom = 12.dp)
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
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, goldAccent, RoundedCornerShape(8.dp)),
                                    color = TerracottaPrimary
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.MenuBook,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }

                                Column {
                                    Text(
                                        text = "REPERTORIUM HOMOEOPATHICUM",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        letterSpacing = 2.sp,
                                        color = goldAccent
                                    )
                                    Text(
                                        text = "Kent's Repertory",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = textColor
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

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse classical rubric sections & select characteristic indications into totality",
                            fontFamily = FontFamily.Serif,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                    }
                }

                // Decorative double border line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.dp)
                        .background(tomeBorderColor)
                )

                // 2. Classic Chapter Ribbon Shelf
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "§ REPERTORY CHAPTERS (${chapters.size})",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.2.sp,
                            color = goldAccent
                        )

                        if (selectedChapter != null) {
                            Text(
                                text = "Active: ${selectedChapter}",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.5.sp,
                                color = TerracottaPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Horizontal Chapter Shelf
                    if (chapters.isNotEmpty()) {
                        val listState = rememberLazyListState()
                        LazyRow(
                            state = listState,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(chapters) { ch ->
                                val isChSelected = ch == selectedChapter
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(
                                            if (isChSelected) TerracottaPrimary else (if (isDark) Color(0xFF2E2018) else Color(0xFFEBE0D2))
                                        )
                                        .border(
                                            1.dp,
                                            if (isChSelected) goldAccent else tomeBorderColor.copy(alpha = 0.7f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable {
                                            selectedChapter = ch
                                            chapterSearchQuery = ""
                                        }
                                        .padding(horizontal = 12.dp, vertical = 6.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        if (isChSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Bookmark,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(13.dp)
                                            )
                                        }
                                        Text(
                                            text = ch.uppercase(),
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = if (isChSelected) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 11.sp,
                                            color = if (isChSelected) Color.White else textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Classic Library Card Catalog Search Bar
                    OutlinedTextField(
                        value = chapterSearchQuery,
                        onValueChange = { chapterSearchQuery = it },
                        placeholder = {
                            Text(
                                text = "Filter sub-rubrics in ${selectedChapter ?: "Chapter"} (e.g. morning, agg., pain)...",
                                fontFamily = FontFamily.Serif,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 12.sp,
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
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = tomeBorderColor,
                            focusedContainerColor = tomeSurfaceColor,
                            unfocusedContainerColor = tomeSurfaceColor
                        )
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
                                    text = "${chapterRubrics.size} rubrics found in section [${selectedChapter ?: ""}]",
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 10.5.sp,
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
                                                Text(
                                                    text = rub.rubricText,
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 13.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) TerracottaPrimary else textColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(3.dp))

                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "${remMap.size} remedies listed",
                                                    fontFamily = FontFamily.Serif,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                    fontSize = 10.5.sp,
                                                    color = subtextColor
                                                )
                                                Text(
                                                    text = "·",
                                                    fontSize = 10.sp,
                                                    color = subtextColor
                                                )
                                                Text(
                                                    text = "Tap to view Materia Medica 📖",
                                                    fontFamily = FontFamily.Serif,
                                                    fontSize = 10.sp,
                                                    color = goldAccent
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
            isDark = isDark
        )
    }
}
