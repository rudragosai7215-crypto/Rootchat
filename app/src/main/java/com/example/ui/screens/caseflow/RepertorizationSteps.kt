package com.example.ui.screens.caseflow

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.screens.materiamedica.MateriaMedicaHighlightUtils
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseEntity
import com.example.data.ClinicalFormData
import com.example.data.MateriaMedicaRepository
import com.example.data.RepertorizationScore
import com.example.data.RepertoryRepository
import com.example.data.RubricEntity
import com.example.data.SelectedRubric
import com.example.ui.screens.materiamedica.MateriaMedicaViewerDialog
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
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

    LaunchedEffect(formData.totalityNotes, formData.symptomEvaluationList, formData.symptomAnalysisList) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            delay(350)

            // 1. Extract complete symptom phrases from Totality
            val exactPhrases = mutableListOf<String>()

            // From symptom evaluation list
            formData.symptomEvaluationList.forEach { item ->
                val phrase = item.symptomText.trim()
                if (phrase.length >= 3) exactPhrases.add(phrase)
            }

            // From symptom analysis list if evaluation list is empty
            if (exactPhrases.isEmpty()) {
                formData.symptomAnalysisList.forEach { item ->
                    val phrase = item.symptomText.trim()
                    if (phrase.length >= 3) exactPhrases.add(phrase)
                }
            }

            // From totalityNotes lines (e.g. "1. Throbbing headache morning", "Anxiety night")
            formData.totalityNotes.lines().forEach { line ->
                val cleanedLine = line.replace("^\\s*(\\d+[.)\\-\\s]+|[-*•]\\s*)".toRegex(), "").trim()
                if (cleanedLine.length >= 3 && !cleanedLine.startsWith("//")) {
                    exactPhrases.add(cleanedLine)
                }
            }

            // 2. Extract individual related words from those phrases
            val relatedWords = mutableListOf<String>()
            exactPhrases.forEach { phrase ->
                val words = phrase.split("[^a-zA-Z0-9]+".toRegex())
                    .map { it.lowercase().trim() }
                    .filter { it.length >= 4 && it !in MateriaMedicaHighlightUtils.STOP_WORDS }
                relatedWords.addAll(words)
            }

            val exactPhraseMatches = mutableListOf<RubricEntity>()
            val wordLevelMatches = mutableListOf<RubricEntity>()

            // Priority 1 (Highest): Search exact phrases from Totality
            for (phrase in exactPhrases.distinct().take(5)) {
                val results = repertoryRepository.searchRubrics(phrase)
                val cleanPhrase = phrase.lowercase().replace("[-:,;./>]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
                for (rub in results) {
                    val rubClean = rub.rubricText.lowercase().replace("[-:,;./>]".toRegex(), " ").replace("\\s+".toRegex(), " ").trim()
                    if (rubClean.contains(cleanPhrase) || rub.rubricText.contains(phrase, ignoreCase = true)) {
                        exactPhraseMatches.add(rub)
                    } else {
                        wordLevelMatches.add(rub)
                    }
                }
            }

            // Priority 2 (Lower priority): Search individual related words
            for (word in relatedWords.distinct().take(6)) {
                val results = repertoryRepository.searchRubrics(word)
                wordLevelMatches.addAll(results.take(3))
            }

            // Reorder/reprioritize so exact phrase matches always come before word-level matches
            val finalExact = exactPhraseMatches.distinctBy { it.id }
            val finalWord = wordLevelMatches
                .filter { wRub -> finalExact.none { it.id == wRub.id } }
                .distinctBy { it.id }

            liveSuggestions = (finalExact + finalWord).take(12)
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
    isDark: Boolean,
    materiaMedicaRepository: MateriaMedicaRepository? = null,
    caseEntity: CaseEntity? = null,
    repertoryType: String = RepertoryRepository.REPERTORY_ORIGINAL_KENT
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<RubricEntity>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }

    // Dialogs state
    var showBrowseDialog by remember { mutableStateOf(false) }
    var rubricForPreview by remember { mutableStateOf<RubricEntity?>(null) }
    var expandedRemedies by remember { mutableStateOf(setOf<String>()) }
    var differentiationPair by remember { mutableStateOf<Pair<RepertorizationScore, RepertorizationScore>?>(null) }
    var viewingMateriaMedicaRemedy by remember { mutableStateOf<String?>(null) }
    var viewingMateriaMedicaFullName by remember { mutableStateOf<String?>(null) }
    var viewingTiedGroupDialog by remember { mutableStateOf<List<RepertorizationScore>?>(null) }

    var selectedTab by remember { mutableIntStateOf(0) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    val effectiveRubricCount = if (repertoryType == RepertoryRepository.REPERTORY_ORIGINAL_KENT) {
        RepertoryRepository.ORIGINAL_KENT_RUBRIC_COUNT
    } else {
        rubricCount.coerceAtLeast(RepertoryRepository.EXTENDED_KENT_RUBRIC_COUNT)
    }

    // Perform live search when query changes
    LaunchedEffect(searchQuery, repertoryType) {
        val q = searchQuery.trim()
        if (q.isBlank()) {
            searchResults = emptyList()
            isSearching = false
        } else {
            isSearching = true
            delay(250)
            searchResults = repertoryRepository.searchRubrics(q, repertoryType = repertoryType)
            isSearching = false
        }
    }

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Tab Selector: Tab 0 = Select Rubrics, Tab 1 = Results
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        height = 2.dp
                    )
                },
                divider = {
                    HorizontalDivider(
                        color = if (isDark) Color(0x18FFFFFF) else Color(0x10000000),
                        thickness = 0.8.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.ListAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Select Rubrics (${selectedRubrics.size})",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                    unselectedContentColor = subtextColor,
                    modifier = Modifier.testTag("tab_select_rubrics")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Results (${repertorizationResults.size})",
                                fontSize = 13.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                    unselectedContentColor = subtextColor,
                    modifier = Modifier.testTag("tab_results")
                )
            }

            if (selectedTab == 0) {
                // ==================== TAB 0: SELECT RUBRICS ====================
                // Header Row with Repertory Stats and Browse Repertory button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (repertoryType == RepertoryRepository.REPERTORY_ORIGINAL_KENT) {
                                "HOMOEOPATHIC REPERTORY · ORIGINAL KENT (1897)"
                            } else {
                                "HOMOEOPATHIC REPERTORY · EXTENDED KENT"
                            },
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp,
                            letterSpacing = 1.4.sp,
                            color = if (isDark) TerracottaLight else TerracottaPrimary
                        )
                        Text(
                            text = if (repertoryType == RepertoryRepository.REPERTORY_ORIGINAL_KENT) {
                                "$effectiveRubricCount rubrics · 37 classical chapters · $remedyCount remedies in database"
                            } else {
                                "$effectiveRubricCount rubrics · 41 chapters · $remedyCount remedies in database"
                            },
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

                // Totality Accordion (Part 9)
                var isTotalityExpanded by remember { mutableStateOf(true) }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, if (isDark) Color(0x1FFFFFFF) else Color(0x14000000), RoundedCornerShape(12.dp)),
                    color = if (isDark) Color(0x14FFFFFF) else Color(0x0A000000)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { isTotalityExpanded = !isTotalityExpanded },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "TOTALITY",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.2.sp,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary,
                                    fontFamily = FontFamily.Serif
                                )
                                Surface(
                                    color = if (isDark) Color(0x2B401E17) else Color(0x14B5502F),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "${selectedRubrics.size} rubrics",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.5.dp)
                                    )
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Immediate recalculation active",
                                    fontSize = 9.5.sp,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                    color = subtextColor
                                )
                                Icon(
                                    imageVector = if (isTotalityExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = if (isTotalityExpanded) "Collapse" else "Expand",
                                    tint = subtextColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        AnimatedVisibility(visible = isTotalityExpanded) {
                            Column(modifier = Modifier.fillMaxWidth().padding(top = 10.dp)) {
                                if (selectedRubrics.isEmpty()) {
                                    Text(
                                        text = "No rubrics selected yet. Search below or tap 'Browse Chapters' to add rubrics from Kent's Repertory.",
                                        fontSize = 11.5.sp,
                                        color = subtextColor,
                                        modifier = Modifier.padding(vertical = 4.dp)
                                    )
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
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Search Bar for Full Repertory Database
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by keywords or symptoms (e.g. 'fear dark', 'ringworm', 'bursting')...", fontSize = 12.5.sp) },
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

                // Search Results Dropdown / List with Highlighting
                if (isSearching) {
                    Text("Searching entire repertory...", fontSize = 11.5.sp, color = subtextColor)
                } else if (searchResults.isNotEmpty()) {
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
                                            HighlightedRubricText(
                                                text = rub.chapter.uppercase(),
                                                query = searchQuery,
                                                baseColor = if (isDark) TerracottaLight else TerracottaPrimary,
                                                highlightColor = if (isDark) Color(0xFFFFCC80) else Color(0xFFBF360C),
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            HighlightedRubricText(
                                                text = rub.rubricText,
                                                query = searchQuery,
                                                baseColor = textColor,
                                                highlightColor = if (isDark) TerracottaLight else TerracottaPrimary,
                                                fontSize = 12.5.sp,
                                                fontWeight = FontWeight.Medium
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
                } else if (searchQuery.isNotBlank() && !isSearching) {
                    Text("No rubrics found matching '$searchQuery'. Try a broader keyword or browse chapters.", fontSize = 11.5.sp, color = subtextColor)
                }

                // Quick Switch to Tab 1 (Results)
                if (selectedRubrics.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = { selectedTab = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_switch_to_results")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("View Repertorization Results (${repertorizationResults.size} Remedies)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else {
                // ==================== TAB 1: RESULTS ====================
                if (selectedRubrics.isEmpty()) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp))
                            .padding(24.dp),
                        color = Color.Transparent
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ListAlt,
                                contentDescription = null,
                                tint = if (isDark) TerracottaLight else TerracottaPrimary,
                                modifier = Modifier.size(36.dp)
                            )
                            Text(
                                text = "NO RUBRICS SELECTED YET",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                letterSpacing = 1.sp,
                                color = if (isDark) TerracottaLight else TerracottaPrimary
                            )
                            Text(
                                text = "Select symptoms and rubrics in the 'Select Rubrics' tab to compute repertorization results and remedy hierarchy.",
                                fontSize = 12.sp,
                                color = subtextColor,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                            Button(
                                onClick = { selectedTab = 0 },
                                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Text("Go to Select Rubrics", fontSize = 12.5.sp)
                                }
                            }
                        }
                    }
                } else {
                    // Compute Tied Groups
                    val tiedGroups = remember(repertorizationResults) {
                        repertorizationResults.groupBy { it.score }.filter { it.value.size > 1 }
                    }

                    // TIE GROUP Clean Summary Card (Matching Reference Design)
                    if (tiedGroups.isNotEmpty() && repertorizationResults.isNotEmpty()) {
                        val highestTiedScore = tiedGroups.keys.maxOrNull() ?: 0
                        val highestTiedGroup = tiedGroups[highestTiedScore] ?: emptyList()
                        if (highestTiedGroup.size >= 2) {
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(16.dp))
                                    .border(
                                        1.dp,
                                        if (isDark) Color(0x1FFFFFFF) else Color(0x14000000),
                                        RoundedCornerShape(16.dp)
                                    ),
                                color = if (isDark) Color(0x2A401E17) else Color(0x14B5502F),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(if (isDark) TerracottaLight.copy(alpha = 0.2f) else TerracottaPrimary.copy(alpha = 0.15f))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = "TIE GROUP",
                                                fontSize = 9.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                letterSpacing = 1.sp,
                                                color = if (isDark) TerracottaLight else TerracottaPrimary
                                            )
                                        }

                                        Text(
                                            text = "Grade ${highestTiedGroup.firstOrNull()?.gradeBreakdown?.values?.maxOrNull() ?: 3}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) TerracottaLight else TerracottaPrimary
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text(
                                        text = "${highestTiedGroup.size} remedies at $highestTiedScore pts",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = textColor
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "— View all ${highestTiedGroup.size} remedies →",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDark) TerracottaLight else TerracottaPrimary,
                                            modifier = Modifier
                                                .clickable { viewingTiedGroupDialog = highestTiedGroup }
                                                .padding(vertical = 4.dp)
                                        )

                                        Button(
                                            onClick = {
                                                differentiationPair = Pair(highestTiedGroup[0], highestTiedGroup[1])
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = "Differentiate",
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                softWrap = false
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Section Title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REMEDY RANKINGS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = if (isDark) TerracottaLight else TerracottaPrimary,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "${repertorizationResults.size} candidates",
                            fontSize = 11.sp,
                            color = subtextColor
                        )
                    }

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
                        // Individual Remedy Cards (Matching Reference Image)
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                                val maxGrade = rem.gradeBreakdown.values.maxOrNull() ?: 1

                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(16.dp))
                                        .border(
                                            1.dp,
                                            if (isTop3) (if (isDark) TerracottaLight.copy(alpha = 0.4f) else TerracottaPrimary.copy(alpha = 0.35f))
                                            else tableBorderColor,
                                            RoundedCornerShape(16.dp)
                                        ),
                                    color = if (isDark) Color(0x351F1B18) else Color(0x28FFFFFF),
                                    shape = RoundedCornerShape(16.dp),
                                    shadowElevation = if (isTop3) 3.dp else 1.dp
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        // Row 1: Rank, Remedy Abbreviation, Grade Badge
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    color = if (isDark) TerracottaLight else TerracottaPrimary
                                                )
                                                Text(
                                                    text = rem.abbreviation.uppercase(),
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 18.sp,
                                                    color = textColor
                                                )
                                            }

                                            // Grade Pill
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(
                                                        when (maxGrade) {
                                                            3 -> if (isDark) Color(0x33B5502F) else Color(0x20B5502F)
                                                            2 -> if (isDark) Color(0x334A6B56) else Color(0x204A6B56)
                                                            else -> if (isDark) Color(0x22FFFFFF) else Color(0x10000000)
                                                        }
                                                    )
                                                    .border(
                                                        1.dp,
                                                        when (maxGrade) {
                                                            3 -> if (isDark) TerracottaLight else TerracottaPrimary
                                                            2 -> if (isDark) SageGreenLight else SageGreenPrimary
                                                            else -> tableBorderColor
                                                        },
                                                        RoundedCornerShape(6.dp)
                                                    )
                                                    .padding(horizontal = 8.dp, vertical = 2.5.dp)
                                            ) {
                                                Text(
                                                    text = "Grade $maxGrade",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = when (maxGrade) {
                                                        3 -> if (isDark) TerracottaLight else TerracottaPrimary
                                                        2 -> if (isDark) SageGreenLight else SageGreenPrimary
                                                        else -> subtextColor
                                                    }
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(3.dp))

                                        // Row 2: Full Latin Remedy Name
                                        Text(
                                            text = rem.fullName.ifBlank { rem.abbreviation.uppercase() },
                                            fontSize = 13.sp,
                                            color = subtextColor
                                        )

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Row 3: Score & Coverage with proper hierarchy
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(
                                                    text = "SCORE",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.8.sp,
                                                    color = subtextColor
                                                )
                                                Text(
                                                    text = "${rem.score}",
                                                    fontFamily = FontFamily.Serif,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 21.sp,
                                                    color = if (isTop3) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(36.dp))

                                            Column {
                                                Text(
                                                    text = "COVERAGE",
                                                    fontSize = 9.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    letterSpacing = 0.8.sp,
                                                    color = subtextColor
                                                )
                                                Text(
                                                    text = "${rem.coverage}/${selectedRubrics.size}",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = textColor
                                                )
                                            }

                                            Spacer(modifier = Modifier.weight(1f))

                                            IconButton(
                                                onClick = {
                                                    viewingMateriaMedicaRemedy = rem.abbreviation
                                                    viewingMateriaMedicaFullName = rem.fullName
                                                },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.ArrowForward,
                                                    contentDescription = "View Materia Medica",
                                                    tint = subtextColor,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Row 4: Action Buttons (FlowRow preventing narrow vertical wraps)
                                        FlowRow(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            // Why this remedy? Toggle
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .border(1.dp, if (isExpanded) TerracottaPrimary else tableBorderColor, RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        expandedRemedies = if (isExpanded) {
                                                            expandedRemedies - rem.abbreviation
                                                        } else {
                                                            expandedRemedies + rem.abbreviation
                                                        }
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                color = if (isExpanded) (if (isDark) Color(0x33B5502F) else Color(0x18B5502F)) else Color.Transparent
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Text(
                                                        text = "Why this remedy?",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (isExpanded) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor,
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                    Icon(
                                                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                                        contentDescription = null,
                                                        tint = if (isExpanded) (if (isDark) TerracottaLight else TerracottaPrimary) else subtextColor,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }

                                            // Materia Medica Action Button
                                            Surface(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .border(1.dp, if (isDark) Color(0x66D4AF37) else Color(0x44B8860B), RoundedCornerShape(8.dp))
                                                    .clickable {
                                                        viewingMateriaMedicaRemedy = rem.abbreviation
                                                        viewingMateriaMedicaFullName = rem.fullName
                                                    }
                                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                                color = if (isDark) Color(0x22D4AF37) else Color(0x12D4AF37)
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Filled.AutoStories,
                                                        contentDescription = null,
                                                        tint = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B),
                                                        modifier = Modifier.size(13.dp)
                                                    )
                                                    Text(
                                                        text = "Materia Medica",
                                                        fontSize = 11.5.sp,
                                                        fontWeight = FontWeight.SemiBold,
                                                        color = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B),
                                                        maxLines = 1,
                                                        softWrap = false
                                                    )
                                                }
                                            }

                                            // Differentiate Button — Single-line guaranteed, never wraps vertically!
                                            if (otherTiedRemedy != null) {
                                                Surface(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .border(
                                                            1.dp,
                                                            if (isTied) (if (isDark) TerracottaLight else TerracottaPrimary) else tableBorderColor,
                                                            RoundedCornerShape(8.dp)
                                                        )
                                                        .clickable {
                                                            differentiationPair = Pair(rem, otherTiedRemedy)
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                                    color = if (isTied) (if (isDark) Color(0x2A401E17) else Color(0x18B5502F)) else Color.Transparent
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
                                                            text = "Differentiate",
                                                            fontSize = 11.5.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isTied) (if (isDark) TerracottaLight else TerracottaPrimary) else textColor,
                                                            maxLines = 1,
                                                            softWrap = false
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
                                                    .padding(top = 12.dp)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isDark) Color(0x22000000) else Color(0x0A000000))
                                                    .padding(12.dp),
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
                                                        text = "MATCHED RUBRICS (${matchedRubrics.size})",
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
                                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                                                    .clip(RoundedCornerShape(8.dp))
                                                                    .background(if (isDark) Color(0x14FFFFFF) else Color(0x08000000))
                                                                    .padding(horizontal = 10.dp, vertical = 7.dp),
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
                                                            text = "Unmatched: ${unmatchedRubrics.joinToString("; ") { it.rubricText.take(30) }}",
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
            materiaMedicaRepository = materiaMedicaRepository,
            isDark = isDark,
            repertoryType = repertoryType
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
            onOpenMateriaMedica = { abbr ->
                viewingMateriaMedicaRemedy = abbr
            },
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
            materiaMedicaRepository = materiaMedicaRepository,
            caseEntity = caseEntity,
            isDark = isDark
        )
    }

    // Direct Materia Medica Viewer Dialog from active repertorization
    if (viewingMateriaMedicaRemedy != null) {
        val repo = materiaMedicaRepository ?: remember {
            val appDb = com.example.data.AppDatabase.getDatabase(context)
            MateriaMedicaRepository(appDb.materiaMedicaDao(), appDb.remedyDao())
        }
        MateriaMedicaViewerDialog(
            remedyAbbreviation = viewingMateriaMedicaRemedy!!,
            initialFullName = viewingMateriaMedicaFullName,
            selectedRubrics = selectedRubrics,
            caseEntity = caseEntity,
            materiaMedicaRepository = repo,
            onDismiss = {
                viewingMateriaMedicaRemedy = null
                viewingMateriaMedicaFullName = null
            },
            isDark = isDark
        )
    }

    // Tied Group Remedies Dialog
    viewingTiedGroupDialog?.let { tiedList ->
        TiedGroupListDialog(
            remedies = tiedList,
            score = tiedList.firstOrNull()?.score ?: 0,
            onDismiss = { viewingTiedGroupDialog = null },
            onSelectRemedy = { rem ->
                viewingTiedGroupDialog = null
                val other = tiedList.firstOrNull { it.abbreviation != rem.abbreviation }
                if (other != null) {
                    differentiationPair = Pair(rem, other)
                }
            },
            onOpenMateriaMedica = { abbr, full ->
                viewingTiedGroupDialog = null
                viewingMateriaMedicaRemedy = abbr
                viewingMateriaMedicaFullName = full
            },
            isDark = isDark
        )
    }
}

// -------------------------------------------------------------------------------------------------
// Tied Group Remedies Dialog
// -------------------------------------------------------------------------------------------------
@Composable
fun TiedGroupListDialog(
    remedies: List<RepertorizationScore>,
    score: Int,
    onDismiss: () -> Unit,
    onSelectRemedy: (RepertorizationScore) -> Unit,
    onOpenMateriaMedica: (String, String) -> Unit,
    isDark: Boolean
) {
    var filterQuery by remember { mutableStateOf("") }
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    val filtered = remember(filterQuery, remedies) {
        if (filterQuery.isBlank()) remedies
        else {
            val q = filterQuery.trim().lowercase()
            remedies.filter {
                it.abbreviation.lowercase().contains(q) ||
                it.fullName.lowercase().contains(q)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f)
                .clip(RoundedCornerShape(20.dp)),
            color = if (isDark) Color(0xFF1E1B18) else Color(0xFFFAF7F2),
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Tied Remedies Group",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = textColor
                        )
                        Text(
                            text = "${remedies.size} remedies tied with score of $score pts",
                            fontSize = 12.sp,
                            color = if (isDark) TerracottaLight else TerracottaPrimary
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = textColor)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = filterQuery,
                    onValueChange = { filterQuery = it },
                    placeholder = { Text("Filter ${remedies.size} remedies...", fontSize = 13.sp) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = subtextColor) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TerracottaPrimary,
                        unfocusedBorderColor = tableBorderColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filtered, key = { it.abbreviation }) { rem ->
                        val maxGrade = rem.gradeBreakdown.values.maxOrNull() ?: 1
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = if (isDark) Color(0x22FFFFFF) else Color(0x10000000),
                            border = BorderStroke(1.dp, tableBorderColor)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = rem.abbreviation.uppercase(),
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = textColor
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(if (isDark) Color(0x2AFFFFFF) else Color(0x14000000))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "Grade $maxGrade",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = subtextColor
                                            )
                                        }
                                    }
                                    Text(
                                        text = rem.fullName.ifBlank { rem.abbreviation.uppercase() },
                                        fontSize = 11.5.sp,
                                        color = subtextColor,
                                        maxLines = 1
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable { onOpenMateriaMedica(rem.abbreviation, rem.fullName) }
                                            .border(1.dp, if (isDark) Color(0x66D4AF37) else Color(0x44B8860B), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        color = if (isDark) Color(0x22D4AF37) else Color(0x12D4AF37)
                                    ) {
                                        Text(
                                            text = "MM",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
                                        )
                                    }

                                    Button(
                                        onClick = { onSelectRemedy(rem) },
                                        colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = "Diff",
                                            fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
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
    materiaMedicaRepository: MateriaMedicaRepository? = null,
    caseEntity: CaseEntity? = null,
    isDark: Boolean
) {
    val context = LocalContext.current
    var rem1 by remember { mutableStateOf(initialRemedy1) }
    var rem2 by remember { mutableStateOf(initialRemedy2) }
    var viewingMateriaMedicaRemedy by remember { mutableStateOf<String?>(null) }
    var viewingMateriaMedicaFullName by remember { mutableStateOf<String?>(null) }

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
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, if (isDark) Color(0x66D4AF37) else Color(0x44B8860B), RoundedCornerShape(6.dp))
                                        .clickable {
                                            viewingMateriaMedicaRemedy = rem1.abbreviation
                                            viewingMateriaMedicaFullName = rem1.fullName
                                        }
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    color = if (isDark) Color(0x22D4AF37) else Color(0x12D4AF37)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoStories,
                                            contentDescription = null,
                                            tint = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "View MM",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
                                        )
                                    }
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
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .border(1.dp, if (isDark) Color(0x66D4AF37) else Color(0x44B8860B), RoundedCornerShape(6.dp))
                                        .clickable {
                                            viewingMateriaMedicaRemedy = rem2.abbreviation
                                            viewingMateriaMedicaFullName = rem2.fullName
                                        }
                                        .padding(vertical = 4.dp, horizontal = 6.dp),
                                    color = if (isDark) Color(0x22D4AF37) else Color(0x12D4AF37)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.AutoStories,
                                            contentDescription = null,
                                            tint = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B),
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "View MM",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) Color(0xFFD4AF37) else Color(0xFFB8860B)
                                        )
                                    }
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

    if (viewingMateriaMedicaRemedy != null) {
        val repo = materiaMedicaRepository ?: remember {
            val appDb = com.example.data.AppDatabase.getDatabase(context)
            MateriaMedicaRepository(appDb.materiaMedicaDao(), appDb.remedyDao())
        }
        MateriaMedicaViewerDialog(
            remedyAbbreviation = viewingMateriaMedicaRemedy!!,
            initialFullName = viewingMateriaMedicaFullName,
            selectedRubrics = selectedRubrics,
            caseEntity = caseEntity,
            materiaMedicaRepository = repo,
            onDismiss = {
                viewingMateriaMedicaRemedy = null
                viewingMateriaMedicaFullName = null
            },
            isDark = isDark
        )
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
