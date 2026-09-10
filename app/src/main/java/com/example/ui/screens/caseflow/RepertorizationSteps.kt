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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
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
import com.example.data.CaseEntity
import com.example.data.ClinicalFormData
import com.example.data.RepertorizationScore
import com.example.data.RepertoryRepository
import com.example.data.RubricEntity
import com.example.data.SelectedRubric
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.TerracottaContainer
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// -------------------------------------------------------------------------------------------------
// Totality of Symptoms Step
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TotalityOfSymptomsStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    isDark: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    // Auto-generate numbered totality synthesis if empty
    LaunchedEffect(formData.symptomEvaluationList, formData.symptomAnalysisList) {
        if (formData.totalityNotes.isBlank()) {
            val sb = StringBuilder()
            val list = if (formData.symptomEvaluationList.isNotEmpty()) {
                formData.symptomEvaluationList.map { it.displayLabel() }
            } else {
                formData.symptomAnalysisList.map { "${it.symptomText} (${it.type}, ${it.frequency})" }
            }
            list.forEachIndexed { index, item ->
                sb.append("${index + 1}. $item\n")
            }
            if (sb.isNotBlank()) {
                onUpdateForm(formData.copy(totalityNotes = sb.toString().trim()))
            }
        }
    }

    // Live Rubric Suggestions based on Totality & Symptoms text
    var liveSuggestions by remember { mutableStateOf<List<RubricEntity>>(emptyList()) }
    var searchJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(formData.totalityNotes, formData.symptomEvaluationList) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            delay(350)
            val keywords = mutableListOf<String>()
            formData.symptomEvaluationList.forEach {
                keywords.addAll(it.symptomText.split("\\s+".toRegex()).filter { w -> w.length > 3 })
            }
            if (keywords.isEmpty()) {
                keywords.addAll(formData.totalityNotes.split("\\s+".toRegex()).filter { w -> w.length > 3 })
            }

            val found = mutableListOf<RubricEntity>()
            for (word in keywords.distinct().take(4)) {
                val res = repertoryRepository.searchRubrics(word)
                found.addAll(res.take(3))
            }
            liveSuggestions = found.distinctBy { it.id }.take(8)
        }
    }

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "TOTALITY OF SYMPTOMS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Text(
                text = "Auto-synthesized from analyzed and evaluated symptoms into a characteristic totality. You can edit, re-order, or add characteristic notes manually.",
                fontSize = 12.sp,
                color = subtextColor
            )

            ClinicalTextField(
                label = "NUMBERED TOTALITY SYNTHESIS *",
                value = formData.totalityNotes,
                onValueChange = { onUpdateForm(formData.copy(totalityNotes = it)) },
                placeholder = "1. Characteristic mental generals...\n2. Physical generals & modalities...\n3. Characteristic particular symptoms...",
                minLines = 8,
                testTag = "input_totality_notes",
                isDark = isDark
            )

            // Live Rubric Suggestions Card
            if (liveSuggestions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x33B5502F) else Color(0x18B5502F))
                        .border(1.dp, if (isDark) TerracottaLight.copy(alpha = 0.4f) else TerracottaPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    color = Color.Transparent
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = if (isDark) TerracottaLight else TerracottaPrimary, modifier = Modifier.size(16.dp))
                            Text(
                                text = "LIVE RUBRIC SUGGESTIONS (TAP TO ADD TO REPERTORY)",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                                color = if (isDark) TerracottaLight else TerracottaPrimary
                            )
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            liveSuggestions.forEach { rub ->
                                val isSelected = selectedRubrics.any { it.id == rub.id }
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) TerracottaPrimary else (if (isDark) Color(0x33000000) else Color(0x30FFFFFF)))
                                        .clickable {
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
                                        }
                                        .padding(horizontal = 8.dp, vertical = 5.dp),
                                    color = Color.Transparent
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(
                                            text = "[${rub.chapter}] ${rub.rubricText}",
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else textColor
                                        )
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Close else Icons.Default.Add,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else (if (isDark) TerracottaLight else TerracottaPrimary),
                                            modifier = Modifier.size(14.dp)
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
}

// -------------------------------------------------------------------------------------------------
// Repertorization Step
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RepertorizationStep(
    selectedRubrics: List<SelectedRubric>,
    onAddRubric: (SelectedRubric) -> Unit,
    onRemoveRubric: (Long) -> Unit,
    repertoryRepository: RepertoryRepository,
    repertorizationResults: List<RepertorizationScore>,
    rubricCount: Int,
    remedyCount: Int,
    isDark: Boolean
) {
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<RubricEntity>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Dialogs state
    var showBrowseDialog by remember { mutableStateOf(false) }
    var rubricForPreview by remember { mutableStateOf<RubricEntity?>(null) }
    var expandedRemedies by remember { mutableStateOf(setOf<String>()) }
    var differentiationPair by remember { mutableStateOf<Pair<RepertorizationScore, RepertorizationScore>?>(null) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    // Perform live search when query changes
    LaunchedEffect(searchQuery) {
        val q = searchQuery.trim()
        if (q.isBlank()) {
            searchResults = emptyList()
            isSearching = false
        } else {
            isSearching = true
            delay(250)
            searchResults = repertoryRepository.searchRubrics(q)
            isSearching = false
        }
    }

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header Row with Repertory Stats and Browse Repertory button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "HOMEOPATHIC REPERTORIZATION",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Text(
                        text = "$rubricCount rubrics · $remedyCount remedies in database",
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }

                Button(
                    onClick = { showBrowseDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("btn_browse_repertory")
                ) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Browse Chapters", fontSize = 12.sp)
                }
            }

            // Carried-over & Selected Rubrics Chips Area
            Text(
                text = "SELECTED CASE RUBRICS (${selectedRubrics.size})",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary,
                fontFamily = FontFamily.Serif
            )

            if (selectedRubrics.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, tableBorderColor, RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    color = Color.Transparent
                ) {
                    Text(
                        text = "No rubrics selected yet. Search below or tap 'Browse Chapters' to add rubrics from Kent's Repertory.",
                        fontSize = 12.sp,
                        color = subtextColor
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedRubrics.forEach { rubric ->
                        RubricRemovableChip(
                            rubric = rubric,
                            onRemove = { onRemoveRubric(rubric.id) },
                            onClick = {
                                coroutineScope.launch {
                                    val rubEntity = repertoryRepository.getRubricById(rubric.id)
                                    if (rubEntity != null) {
                                        rubricForPreview = rubEntity
                                    }
                                }
                            },
                            isDark = isDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Search Bar for Full Repertory Database
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search full repertory by keywords (e.g. 'fear dark', 'head bursting', 'thirst')...", fontSize = 12.5.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = subtextColor) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear search", tint = subtextColor)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TerracottaPrimary,
                    unfocusedBorderColor = tableBorderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_rubrics")
            )

            // Search Results Dropdown / List
            if (searchResults.isNotEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x33000000) else Color(0x18000000))
                        .padding(8.dp),
                    color = Color.Transparent
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        searchResults.forEach { rub ->
                            val isSelected = selectedRubrics.any { it.id == rub.id }
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) TerracottaPrimary.copy(alpha = 0.2f) else Color.Transparent)
                                    .clickable { rubricForPreview = rub }
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                color = Color.Transparent
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = rub.chapter.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) TerracottaLight else TerracottaPrimary
                                        )
                                        Text(
                                            text = rub.rubricText,
                                            fontSize = 12.5.sp,
                                            color = textColor
                                        )
                                    }

                                    IconButton(
                                        onClick = {
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
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.Close else Icons.Default.Add,
                                            contentDescription = null,
                                            tint = if (isSelected) Color(0xFFE53935) else (if (isDark) TerracottaLight else TerracottaPrimary)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Compute Tied Groups
            val tiedGroups = remember(repertorizationResults) {
                repertorizationResults.groupBy { it.score }.filter { it.value.size > 1 }
            }

            // Tie Detection Alert Banner
            if (tiedGroups.isNotEmpty() && repertorizationResults.isNotEmpty()) {
                val highestTiedScore = tiedGroups.keys.maxOrNull() ?: 0
                val highestTiedGroup = tiedGroups[highestTiedScore] ?: emptyList()
                if (highestTiedGroup.size >= 2) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (isDark) Color(0x66B5502F) else Color(0x44B5502F), RoundedCornerShape(12.dp)),
                        color = if (isDark) Color(0x22B5502F) else Color(0x12B5502F)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CompareArrows,
                                        contentDescription = null,
                                        tint = if (isDark) TerracottaLight else TerracottaPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Text(
                                        text = "TIE DETECTED (${highestTiedGroup.size} REMEDIES AT $highestTiedScore PTS)",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        letterSpacing = 0.8.sp,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary
                                    )
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${highestTiedGroup.joinToString(" & ") { it.abbreviation.uppercase() }} have equal scores. Compare coverage and grade hierarchy side-by-side.",
                                    fontSize = 11.5.sp,
                                    color = textColor
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    differentiationPair = Pair(highestTiedGroup[0], highestTiedGroup[1])
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Differentiate", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Repertorization Results Grid (Live recalculation)
            Text(
                text = "REPERTORIZATION CHART & REMEDY RANKING",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary,
                fontFamily = FontFamily.Serif
            )

            if (repertorizationResults.isEmpty()) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp))
                        .padding(20.dp),
                    color = Color.Transparent
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Add rubrics to compute similimum hierarchy.",
                            fontSize = 12.sp,
                            color = subtextColor
                        )
                    }
                }
            } else {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp)),
                    color = Color.Transparent
                ) {
                    Column {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isDark) Color(0x33B5502F) else Color(0x20B5502F))
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RANK & REMEDY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.weight(1.8f)
                            )
                            Text(
                                text = "COVERAGE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.width(76.dp)
                            )
                            Text(
                                text = "SCORE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor,
                                modifier = Modifier.width(52.dp)
                            )
                        }

                        // Top 15 Remedies with Reasoning & Differentiation
                        repertorizationResults.take(15).forEachIndexed { index, rem ->
                            val isTop3 = index < 3
                            val isTied = tiedGroups.containsKey(rem.score)
                            val isExpanded = expandedRemedies.contains(rem.abbreviation)
                            val otherTiedRemedy = if (isTied) {
                                tiedGroups[rem.score]?.firstOrNull { it.abbreviation != rem.abbreviation }
                            } else {
                                val targetIdx = if (index == 0 && repertorizationResults.size > 1) 1 else 0
                                repertorizationResults.getOrNull(targetIdx)
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        if (isTop3) (if (isDark) Color(0x18B5502F) else Color(0x10B5502F))
                                        else (if (index % 2 == 1) (if (isDark) Color(0x10FFFFFF) else Color(0x06000000)) else Color.Transparent)
                                    )
                            ) {
                                // Main Summary Row
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1.8f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Text(
                                                text = "#${index + 1} ${rem.abbreviation.uppercase()}",
                                                fontWeight = if (isTop3) FontWeight.Bold else FontWeight.SemiBold,
                                                fontSize = 13.5.sp,
                                                color = textColor
                                            )
                                            if (isTop3) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (isDark) TerracottaLight else TerracottaPrimary)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "TOP ${index + 1}",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = Color.White
                                                    )
                                                }
                                            }
                                            if (isTied) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (isDark) Color(0x33B5502F) else Color(0x22B5502F))
                                                        .border(1.dp, if (isDark) TerracottaLight else TerracottaPrimary, RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text(
                                                        text = "TIED",
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) TerracottaLight else TerracottaPrimary
                                                    )
                                                }
                                            }
                                        }
                                        Text(
                                            text = rem.fullName,
                                            fontSize = 11.sp,
                                            color = subtextColor,
                                            maxLines = 1
                                        )
                                    }

                                    Text(
                                        text = "${rem.coverage}/${selectedRubrics.size}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = textColor,
                                        modifier = Modifier.width(76.dp)
                                    )

                                    Text(
                                        text = "${rem.score}",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isTop3) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor,
                                        modifier = Modifier.width(52.dp)
                                    )
                                }

                                // Quick Action Row: Why this remedy? & Differentiate
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Expandable "Why this remedy?" toggle button
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(1.dp, if (isExpanded) TerracottaPrimary else tableBorderColor, RoundedCornerShape(6.dp))
                                            .clickable {
                                                expandedRemedies = if (isExpanded) {
                                                    expandedRemedies - rem.abbreviation
                                                } else {
                                                    expandedRemedies + rem.abbreviation
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isExpanded) (if (isDark) Color(0x33B5502F) else Color(0x18B5502F)) else Color.Transparent
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "Why this remedy?",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isExpanded) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor
                                            )
                                            Icon(
                                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                contentDescription = null,
                                                tint = if (isExpanded) (if (isDark) TerracottaLight else TerracottaPrimary) else subtextColor,
                                                modifier = Modifier.size(14.dp)
                                            )
                                        }
                                    }

                                    // Differentiate button
                                    if (otherTiedRemedy != null) {
                                        Surface(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .border(
                                                    1.dp,
                                                    if (isTied) (if (isDark) TerracottaLight else TerracottaPrimary) else tableBorderColor,
                                                    RoundedCornerShape(6.dp)
                                                )
                                                .clickable {
                                                    differentiationPair = Pair(rem, otherTiedRemedy)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp),
                                            color = if (isTied) (if (isDark) Color(0x22B5502F) else Color(0x12B5502F)) else Color.Transparent
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.CompareArrows,
                                                    contentDescription = null,
                                                    tint = if (isTied) (if (isDark) TerracottaLight else TerracottaPrimary) else subtextColor,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Text(
                                                    text = if (isTied) "Differentiate (vs ${otherTiedRemedy.abbreviation.uppercase()})" else "Differentiate",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isTied) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (isTied) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor
                                                )
                                            }
                                        }
                                    }
                                }

                                // Expandable "Why this remedy?" section
                                AnimatedVisibility(
                                    visible = isExpanded,
                                    enter = expandVertically() + fadeIn(),
                                    exit = shrinkVertically() + fadeOut()
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(if (isDark) Color(0x24000000) else Color(0x0C000000))
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val matchedRubrics = selectedRubrics.filter { (rem.gradeBreakdown[it.id] ?: 0) > 0 }
                                        val unmatchedRubrics = selectedRubrics.filter { (rem.gradeBreakdown[it.id] ?: 0) == 0 }

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "REMEDY REASONING · MATCHED RUBRICS (${matchedRubrics.size})",
                                                fontFamily = FontFamily.Serif,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                letterSpacing = 1.sp,
                                                color = if (isDark) TerracottaLight else TerracottaPrimary
                                            )
                                            Text(
                                                text = "${rem.score} pts total",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = subtextColor
                                            )
                                        }

                                        if (matchedRubrics.isEmpty()) {
                                            Text(
                                                text = "No rubrics in this case contain this remedy.",
                                                fontSize = 11.sp,
                                                color = subtextColor
                                            )
                                        } else {
                                            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                                matchedRubrics.forEach { rubric ->
                                                    val grade = rem.gradeBreakdown[rubric.id] ?: 1
                                                    val gradeBadgeColor = when (grade) {
                                                        3 -> Color(0xFFD32F2F)
                                                        2 -> if (isDark) TerracottaLight else TerracottaPrimary
                                                        else -> if (isDark) Color(0xFFAAAAAA) else Color(0xFF666666)
                                                    }
                                                    val gradeLabel = when (grade) {
                                                        3 -> "Grade 3 (3 pts) · Keynote"
                                                        2 -> "Grade 2 (2 pts) · Moderate"
                                                        else -> "Grade 1 (1 pt) · Plain"
                                                    }

                                                    Row(
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .clip(RoundedCornerShape(6.dp))
                                                            .background(if (isDark) Color(0x14FFFFFF) else Color(0x08000000))
                                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Column(modifier = Modifier.weight(1f)) {
                                                            Text(
                                                                text = "${rubric.chapter.uppercase()} › ${rubric.rubricText}",
                                                                fontSize = 11.5.sp,
                                                                fontWeight = if (grade == 3) FontWeight.Bold else FontWeight.Medium,
                                                                color = textColor
                                                            )
                                                        }
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                        Box(
                                                            modifier = Modifier
                                                                .clip(RoundedCornerShape(4.dp))
                                                                .background(gradeBadgeColor.copy(alpha = 0.16f))
                                                                .border(1.dp, gradeBadgeColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                                        ) {
                                                            Text(
                                                                text = gradeLabel,
                                                                fontSize = 9.5.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = gradeBadgeColor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                        }

                                        if (unmatchedRubrics.isNotEmpty()) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(top = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Info,
                                                    contentDescription = null,
                                                    tint = subtextColor,
                                                    modifier = Modifier.size(13.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Unmatched symptoms in case: ${unmatchedRubrics.joinToString("; ") { it.rubricText.take(30) }}",
                                                    fontSize = 10.sp,
                                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                                    color = subtextColor,
                                                    maxLines = 2
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
        }
    }

    // Repertory Browse Dialog
    if (showBrowseDialog) {
        RepertoryBrowseDialog(
            onDismiss = { showBrowseDialog = false },
            selectedRubrics = selectedRubrics,
            onAddRubric = onAddRubric,
            onRemoveRubric = onRemoveRubric,
            repertoryRepository = repertoryRepository,
            isDark = isDark
        )
    }

    // Rubric Remedies Preview Dialog
    rubricForPreview?.let { rub ->
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
            onDismiss = { rubricForPreview = null },
            repertoryRepository = repertoryRepository,
            isDark = isDark
        )
    }

    // Remedy Differentiation Dialog (Tie-Breaking & Comparative Matrix)
    differentiationPair?.let { (rem1, rem2) ->
        RemedyDifferentiationDialog(
            initialRemedy1 = rem1,
            initialRemedy2 = rem2,
            allCandidates = repertorizationResults.take(15),
            selectedRubrics = selectedRubrics,
            onDismiss = { differentiationPair = null },
            isDark = isDark
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Remedy Differentiation Dialog (Tie-Breaking & Grade Hierarchy Comparison)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RemedyDifferentiationDialog(
    initialRemedy1: RepertorizationScore,
    initialRemedy2: RepertorizationScore,
    allCandidates: List<RepertorizationScore>,
    selectedRubrics: List<SelectedRubric>,
    onDismiss: () -> Unit,
    isDark: Boolean
) {
    var rem1 by remember { mutableStateOf(initialRemedy1) }
    var rem2 by remember { mutableStateOf(initialRemedy2) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val surfaceBg = if (isDark) Color(0xFF1F1713) else Color(0xFFF9F6F0)

    val scrollState = rememberScrollState()

    // Comparative calculations
    val rem1LeadsCount = remember(rem1, rem2, selectedRubrics) {
        selectedRubrics.count { rub ->
            val g1 = rem1.gradeBreakdown[rub.id] ?: 0
            val g2 = rem2.gradeBreakdown[rub.id] ?: 0
            g1 > g2
        }
    }
    val rem2LeadsCount = remember(rem1, rem2, selectedRubrics) {
        selectedRubrics.count { rub ->
            val g1 = rem1.gradeBreakdown[rub.id] ?: 0
            val g2 = rem2.gradeBreakdown[rub.id] ?: 0
            g2 > g1
        }
    }
    val tiedCount = remember(rem1, rem2, selectedRubrics) {
        selectedRubrics.count { rub ->
            val g1 = rem1.gradeBreakdown[rub.id] ?: 0
            val g2 = rem2.gradeBreakdown[rub.id] ?: 0
            g1 == g2 && g1 > 0
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
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, tableBorderColor, RoundedCornerShape(20.dp)),
            color = surfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(18.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            imageVector = Icons.Default.CompareArrows,
                            contentDescription = null,
                            tint = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                text = "REMEDY DIFFERENTIATION & TIE-BREAKING",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                letterSpacing = 1.1.sp,
                                color = if (isDark) TerracottaLight else TerracottaPrimary
                            )
                            Text(
                                text = "Side-by-side rubric coverage and grade hierarchy comparison",
                                fontSize = 11.sp,
                                color = subtextColor
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = subtextColor)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Side-by-side remedy selector cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Remedy 1 Card
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, if (isDark) Color(0x55B5502F) else Color(0x33B5502F), RoundedCornerShape(14.dp)),
                            color = if (isDark) Color(0x22B5502F) else Color(0x12B5502F)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "REMEDY A",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rem1.abbreviation.uppercase(),
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = textColor
                                )
                                Text(
                                    text = rem1.fullName,
                                    fontSize = 11.sp,
                                    color = subtextColor,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "${rem1.score} PTS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary
                                    )
                                    Text(
                                        text = "• ${rem1.coverage}/${selectedRubrics.size} cov",
                                        fontSize = 11.5.sp,
                                        color = subtextColor
                                    )
                                }
                            }
                        }

                        // Remedy 2 Card
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, if (isDark) Color(0x555B7E68) else Color(0x335B7E68), RoundedCornerShape(14.dp)),
                            color = if (isDark) Color(0x225B7E68) else Color(0x125B7E68)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "REMEDY B",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = rem2.abbreviation.uppercase(),
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp,
                                    color = textColor
                                )
                                Text(
                                    text = rem2.fullName,
                                    fontSize = 11.sp,
                                    color = subtextColor,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "${rem2.score} PTS",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                    )
                                    Text(
                                        text = "• ${rem2.coverage}/${selectedRubrics.size} cov",
                                        fontSize = 11.5.sp,
                                        color = subtextColor
                                    )
                                }
                            }
                        }
                    }

                    // Candidate switch chips (if user wants to compare with another tied/top remedy)
                    if (allCandidates.size > 2) {
                        Column {
                            Text(
                                text = "SWITCH COMPARISON CANDIDATES:",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                                color = subtextColor
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                allCandidates.forEach { cand ->
                                    val isA = cand.abbreviation == rem1.abbreviation
                                    val isB = cand.abbreviation == rem2.abbreviation
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .border(
                                                1.dp,
                                                if (isA) TerracottaPrimary else if (isB) Color(0xFF2E7D32) else tableBorderColor,
                                                RoundedCornerShape(6.dp)
                                            )
                                            .clickable {
                                                if (!isA && !isB) {
                                                    rem2 = cand
                                                }
                                            }
                                            .padding(horizontal = 7.dp, vertical = 3.dp),
                                        color = if (isA) (if (isDark) Color(0x33B5502F) else Color(0x18B5502F))
                                        else if (isB) (if (isDark) Color(0x335B7E68) else Color(0x185B7E68))
                                        else Color.Transparent
                                    ) {
                                        Text(
                                            text = "${cand.abbreviation.uppercase()} (${cand.score}p)",
                                            fontSize = 10.5.sp,
                                            fontWeight = if (isA || isB) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isA) (if (isDark) TerracottaLight else TerracottaPrimary)
                                            else if (isB) (if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32))
                                            else textColor
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Comparison Metrics Banner
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isDark) Color(0x15FFFFFF) else Color(0x0A000000))
                            .padding(10.dp),
                        color = Color.Transparent
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$rem1LeadsCount",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary
                                )
                                Text(
                                    text = "${rem1.abbreviation.uppercase()} Leads",
                                    fontSize = 10.sp,
                                    color = subtextColor
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$tiedCount",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor
                                )
                                Text(
                                    text = "Equal Grade",
                                    fontSize = 10.sp,
                                    color = subtextColor
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$rem2LeadsCount",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                )
                                Text(
                                    text = "${rem2.abbreviation.uppercase()} Leads",
                                    fontSize = 10.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    }

                    // Side-by-Side Rubric Coverage Table
                    Text(
                        text = "RUBRIC COVERAGE & GRADE HIERARCHY MATRIX",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.5.sp,
                        letterSpacing = 1.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp)),
                        color = Color.Transparent
                    ) {
                        Column {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) Color(0x33B5502F) else Color(0x20B5502F))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "RUBRIC / SYMPTOM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.weight(1.8f)
                                )
                                Text(
                                    text = rem1.abbreviation.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary,
                                    modifier = Modifier.width(64.dp)
                                )
                                Text(
                                    text = rem2.abbreviation.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
                                    modifier = Modifier.width(64.dp)
                                )
                                Text(
                                    text = "DIFFERENTIAL",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = textColor,
                                    modifier = Modifier.width(74.dp)
                                )
                            }

                            // Table Rows
                            selectedRubrics.forEachIndexed { idx, rubric ->
                                val g1 = rem1.gradeBreakdown[rubric.id] ?: 0
                                val g2 = rem2.gradeBreakdown[rubric.id] ?: 0

                                val isRem1Leading = g1 > g2
                                val isRem2Leading = g2 > g1

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (idx % 2 == 1) (if (isDark) Color(0x0CFFFFFF) else Color(0x06000000))
                                            else Color.Transparent
                                        )
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Rubric Name & Chapter
                                    Column(modifier = Modifier.weight(1.8f)) {
                                        Text(
                                            text = rubric.rubricText,
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = textColor,
                                            maxLines = 2
                                        )
                                        Text(
                                            text = rubric.chapter.uppercase(),
                                            fontSize = 9.sp,
                                            color = subtextColor
                                        )
                                    }

                                    // Remedy 1 Grade Column (with highlight if leading)
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isRem1Leading) (if (isDark) Color(0x33B5502F) else Color(0x22B5502F))
                                                else Color.Transparent
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (g1 > 0) "Gr $g1" + (if (isRem1Leading && g2 > 0) " (+${g1-g2})" else "") else "—",
                                            fontSize = 11.sp,
                                            fontWeight = if (isRem1Leading) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isRem1Leading) (if (isDark) TerracottaLight else TerracottaPrimary)
                                            else if (g1 > 0) textColor else subtextColor
                                        )
                                    }

                                    // Remedy 2 Grade Column (with highlight if leading)
                                    Box(
                                        modifier = Modifier
                                            .width(64.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (isRem2Leading) (if (isDark) Color(0x335B7E68) else Color(0x225B7E68))
                                                else Color.Transparent
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = if (g2 > 0) "Gr $g2" + (if (isRem2Leading && g1 > 0) " (+${g2-g1})" else "") else "—",
                                            fontSize = 11.sp,
                                            fontWeight = if (isRem2Leading) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isRem2Leading) (if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32))
                                            else if (g2 > 0) textColor else subtextColor
                                        )
                                    }

                                    // Outcome Badge
                                    Box(
                                        modifier = Modifier
                                            .width(74.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                when {
                                                    g1 > 0 && g2 == 0 -> if (isDark) Color(0x33B5502F) else Color(0x20B5502F)
                                                    g2 > 0 && g1 == 0 -> if (isDark) Color(0x335B7E68) else Color(0x205B7E68)
                                                    g1 > g2 -> if (isDark) Color(0x22B5502F) else Color(0x14B5502F)
                                                    g2 > g1 -> if (isDark) Color(0x225B7E68) else Color(0x145B7E68)
                                                    g1 > 0 && g1 == g2 -> if (isDark) Color(0x18FFFFFF) else Color(0x0E000000)
                                                    else -> Color.Transparent
                                                }
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = when {
                                                g1 > 0 && g2 == 0 -> "Only ${rem1.abbreviation.uppercase()}"
                                                g2 > 0 && g1 == 0 -> "Only ${rem2.abbreviation.uppercase()}"
                                                g1 > g2 -> "${rem1.abbreviation.uppercase()} +${g1-g2}"
                                                g2 > g1 -> "${rem2.abbreviation.uppercase()} +${g2-g1}"
                                                g1 > 0 && g1 == g2 -> "Equal (${g1})"
                                                else -> "Neither"
                                            },
                                            fontSize = 9.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when {
                                                g1 > 0 && g2 == 0 || g1 > g2 -> if (isDark) TerracottaLight else TerracottaPrimary
                                                g2 > 0 && g1 == 0 || g2 > g1 -> if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                                                else -> subtextColor
                                            },
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Kentian & Hahnemannian Tie-Breaking Guideline
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, if (isDark) Color(0x33D4AF37) else Color(0x33B8860B), RoundedCornerShape(12.dp)),
                        color = if (isDark) Color(0x15D4AF37) else Color(0x0DD4AF37)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "KENTIAN PRINCIPLE FOR TIE-BREAKING",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    letterSpacing = 0.8.sp,
                                    color = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "When total mathematical scores are tied, Dr. J.T. Kent advises breaking the tie by examining symptom hierarchy: give precedence to the remedy covering Generals (thermal reaction, modalities, desires/aversions) and Mentals with higher grades over common or particular physical symptoms.",
                                fontSize = 11.sp,
                                color = textColor,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Prescription Step
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrescriptionStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    repertorizationResults: List<RepertorizationScore>,
    isDark: Boolean,
    isAcute: Boolean
) {
    val top3 = remember(repertorizationResults) { repertorizationResults.take(3) }
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val potencies = listOf("30C", "200C", "1M", "10M", "LM 1", "6X", "Q")

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = if (isAcute) "SELECTION OF ACUTE SIMILIMUM" else "SELECTION OF CONSTITUTIONAL SIMILIMUM",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            // Top-3 quick-select chips
            if (top3.isNotEmpty()) {
                Text(
                    text = "TOP-3 REPERTORIZED CANDIDATES (TAP TO QUICK-SELECT)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = subtextColor,
                    fontFamily = FontFamily.Serif
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    top3.forEachIndexed { idx, rem ->
                        val isSelected = formData.prescribedRemedy.equals(rem.abbreviation, ignoreCase = true)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) TerracottaPrimary.copy(alpha = 0.35f) else Color(0x22FFFFFF))
                                .border(1.dp, if (isSelected) TerracottaPrimary else (if (isDark) DarkGlassBorder else LightGlassBorder), RoundedCornerShape(12.dp))
                                .clickable {
                                    onUpdateForm(
                                        formData.copy(
                                            prescribedRemedy = rem.abbreviation.uppercase(),
                                            prescribedRemedyFullName = rem.fullName
                                        )
                                    )
                                }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                                .testTag("chip_top_${idx + 1}_remedy"),
                            color = Color.Transparent
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "#${idx + 1} ${rem.abbreviation.uppercase()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isSelected) TerracottaPrimary else textColor
                                )
                                Text(
                                    text = "(${rem.score} pts)",
                                    fontSize = 10.5.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    }
                }
            }

            ClinicalTextField(
                label = "PRESCRIBED REMEDY (ABBREVIATION) *",
                value = formData.prescribedRemedy,
                onValueChange = { onUpdateForm(formData.copy(prescribedRemedy = it)) },
                placeholder = "e.g. ACON, BELL, ARS, PULS, NUX-V",
                testTag = "input_prescribed_remedy",
                isDark = isDark
            )

            ClinicalTextField(
                label = "REMEDY FULL SCIENTIFIC NAME",
                value = formData.prescribedRemedyFullName,
                onValueChange = { onUpdateForm(formData.copy(prescribedRemedyFullName = it)) },
                placeholder = "e.g. Aconitum Napellus",
                testTag = "input_prescribed_remedy_fullname",
                isDark = isDark
            )

            // Potency Quick Chips
            Text(
                text = "POTENCY QUICK SELECT",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = subtextColor,
                fontFamily = FontFamily.Serif
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                potencies.forEach { p ->
                    val isPotencySelected = formData.potency.equals(p, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isPotencySelected) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x15000000)))
                            .clickable { onUpdateForm(formData.copy(potency = p)) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = p,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isPotencySelected) Color.White else textColor
                        )
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "POTENCY",
                    value = formData.potency,
                    onValueChange = { onUpdateForm(formData.copy(potency = it)) },
                    placeholder = "e.g. 200C / 1M / LM 1",
                    testTag = "input_potency",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "DOSE",
                    value = formData.dose,
                    onValueChange = { onUpdateForm(formData.copy(dose = it)) },
                    placeholder = "e.g. 4 pills / 2 drops in water",
                    testTag = "input_dose",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            ClinicalTextField(
                label = "REPETITION",
                value = formData.repetition,
                onValueChange = { onUpdateForm(formData.copy(repetition = it)) },
                placeholder = "e.g. Stat single dose / Every 4 hours for 2 days / Weekly once",
                testTag = "input_repetition",
                isDark = isDark
            )

            ClinicalTextField(
                label = "CLINICAL INSTRUCTIONS & DIETARY REGIMEN",
                value = formData.instructions,
                onValueChange = { onUpdateForm(formData.copy(instructions = it)) },
                placeholder = "Avoid strong coffee, camphor, raw onion; Clean mouth before dose; Report if aggravation occurs...",
                minLines = 3,
                testTag = "input_instructions",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Management Step (Advices)
// -------------------------------------------------------------------------------------------------
@Composable
fun ManagementStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "MANAGEMENT (ADVICES, DIET & AUXILIARY MEASURES) *",
                value = formData.managementAdvices,
                onValueChange = { onUpdateForm(formData.copy(managementAdvices = it)) },
                placeholder = "Dietary guidelines, posture, rest, hydration, avoidance of allergens/cold drafts, exercise regimen, and auxiliary supportive care...",
                minLines = 7,
                testTag = "input_management_advices",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Follow Up Step
// -------------------------------------------------------------------------------------------------
@Composable
fun FollowUpStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "SCHEDULED FOLLOW-UP DATE",
                value = formData.followUpDate,
                onValueChange = { onUpdateForm(formData.copy(followUpDate = it)) },
                placeholder = "e.g. In 7 days / In 14 days / In 1 month",
                testTag = "input_follow_up_date",
                isDark = isDark
            )

            ClinicalTextField(
                label = "CURRENT SYMPTOMS AT FOLLOW-UP",
                value = formData.followUpCurrentSymptoms,
                onValueChange = { onUpdateForm(formData.copy(followUpCurrentSymptoms = it)) },
                placeholder = "Record status of presenting symptoms, new complaints, or sensations...",
                minLines = 3,
                testTag = "input_follow_up_symptoms",
                isDark = isDark
            )

            ClinicalTextField(
                label = "IMPROVEMENT STATUS (HAHNEMANNIAN PROGNOSIS & HERING'S LAW)",
                value = formData.followUpImprovementStatus,
                onValueChange = { onUpdateForm(formData.copy(followUpImprovementStatus = it)) },
                placeholder = "Assess improvement: General well-being, energy, sleep, appetite; Direction of cure (from above downwards, within outwards, more important to less important organs); Homeopathic aggravation vs disease aggravation...",
                minLines = 3,
                testTag = "input_follow_up_status",
                isDark = isDark
            )

            ClinicalTextField(
                label = "NEXT PRESCRIPTION (SAC LAC / PLACEBO / REPEAT / SECOND SIMILIMUM)",
                value = formData.followUpNextPrescription,
                onValueChange = { onUpdateForm(formData.copy(followUpNextPrescription = it)) },
                placeholder = "e.g. Placebo / Sac Lac 30 daily morning; Wait and watch; Or repeat remedy in water potencies if improvement ceases...",
                minLines = 3,
                testTag = "input_follow_up_next_rx",
                isDark = isDark
            )
        }
    }
}
