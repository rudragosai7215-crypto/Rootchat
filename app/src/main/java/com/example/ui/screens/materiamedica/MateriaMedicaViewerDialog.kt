package com.example.ui.screens.materiamedica

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CaseEntity
import com.example.data.ImportProgress
import com.example.data.MateriaMedicaEntity
import com.example.data.MateriaMedicaRepository
import com.example.data.OorepMateriaMedicaImporter
import com.example.data.RemedyNameEntity
import com.example.data.SelectedRubric
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MateriaMedicaViewerDialog(
    remedyAbbreviation: String,
    initialFullName: String? = null,
    selectedRubrics: List<SelectedRubric> = emptyList(),
    caseEntity: CaseEntity? = null,
    materiaMedicaRepository: MateriaMedicaRepository,
    onDismiss: () -> Unit,
    onImportRequested: ((Uri) -> Unit)? = null,
    isDark: Boolean
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null && onImportRequested != null) {
            onImportRequested(uri)
        }
    }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val surfaceBg = if (isDark) Color(0xFF1B1410) else Color(0xFFF9F6F1)
    val cardBg = if (isDark) DarkGlassSurface else LightGlassSurface

    var sections by remember { mutableStateOf<List<MateriaMedicaEntity>>(emptyList()) }
    var remedyDetails by remember { mutableStateOf<RemedyNameEntity?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var selectedSectionFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showOnlyCaseMatches by remember { mutableStateOf(false) }

    // Case terms (exact phrases + related words) for auto-highlighting
    val caseTerms = remember(selectedRubrics, caseEntity) {
        MateriaMedicaHighlightUtils.extractCaseTerms(selectedRubrics, caseEntity)
    }

    // Load remedy sections from repository
    LaunchedEffect(remedyAbbreviation) {
        isLoading = true
        val details = materiaMedicaRepository.getRemedyDetails(remedyAbbreviation)
        remedyDetails = details

        val loaded = materiaMedicaRepository.getSectionsForRemedySync(remedyAbbreviation)
        sections = loaded
        isLoading = false
    }

    val remedyDisplayName = remedyDetails?.fullName ?: initialFullName ?: remedyAbbreviation.uppercase()
    val remedyCommonName = remedyDetails?.commonName?.takeIf { it.isNotBlank() }

    // Calculate highlighted results across all sections with prioritized exact and word matches
    val sectionHighlightResults = remember(sections, caseTerms, searchQuery, isDark) {
        sections.associate { sec ->
            sec.id to MateriaMedicaHighlightUtils.highlightMatches(
                text = sec.symptomText,
                terms = caseTerms,
                isDark = isDark,
                userQuery = searchQuery
            )
        }
    }
    val totalExactMatchesCount = remember(sectionHighlightResults) {
        sectionHighlightResults.values.sumOf { it.exactMatchCount }
    }
    val totalWordMatchesCount = remember(sectionHighlightResults) {
        sectionHighlightResults.values.sumOf { it.wordMatchCount }
    }
    val totalMatchesCount = remember(totalExactMatchesCount, totalWordMatchesCount) {
        totalExactMatchesCount + totalWordMatchesCount
    }

    // Filter and prioritize sections:
    // 1. Highest priority — exact phrase match: sections with exact matches rank first
    // 2. Lower priority — related word matches: sections with word matches rank below exact matches
    val filteredSections = remember(sections, selectedSectionFilter, searchQuery, showOnlyCaseMatches, sectionHighlightResults) {
        val filtered = sections.filter { sec ->
            val matchesSection = selectedSectionFilter == null || sec.section.equals(selectedSectionFilter, ignoreCase = true)
            val res = sectionHighlightResults[sec.id]
            val totalMatches = res?.totalCount ?: 0
            val matchesSearch = searchQuery.isBlank() ||
                    sec.section.contains(searchQuery, ignoreCase = true) ||
                    sec.symptomText.contains(searchQuery, ignoreCase = true) ||
                    totalMatches > 0
            val matchesCaseFilter = !showOnlyCaseMatches || totalMatches > 0
            matchesSection && matchesSearch && matchesCaseFilter
        }

        filtered.sortedWith(
            compareByDescending<MateriaMedicaEntity> { sectionHighlightResults[it.id]?.exactMatchCount ?: 0 }
                .thenByDescending { sectionHighlightResults[it.id]?.wordMatchCount ?: 0 }
        )
    }

    val availableSections = remember(sections) {
        sections.map { it.section }.distinct()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.94f)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, tableBorderColor, RoundedCornerShape(22.dp)),
            color = surfaceBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header Bar: Title, Source & Close Button
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
                            shape = RoundedCornerShape(10.dp),
                            color = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(38.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Filled.AutoStories,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = remedyDisplayName,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isDark) Color(0x33B5502F) else Color(0x18B5502F))
                                        .border(1.dp, if (isDark) TerracottaLight else TerracottaPrimary, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = remedyAbbreviation.uppercase(),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary
                                    )
                                }
                            }
                            if (remedyCommonName != null) {
                                Text(
                                    text = remedyCommonName,
                                    fontSize = 12.sp,
                                    color = subtextColor
                                )
                            }
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = subtextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Source Attribution Banner (MANDATORY REQUIREMENT)
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, tableBorderColor, RoundedCornerShape(8.dp)),
                    color = if (isDark) Color(0x22000000) else Color(0x0A000000)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.BookmarkBorder,
                            contentDescription = null,
                            tint = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Source: Boericke's Pocket Manual of Homoeopathic Materia Medica (1906, Public Domain)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = subtextColor
                        )
                    }
                }

                // Case Auto-Highlighting Status Banner
                if (caseTerms.exactPhrases.isNotEmpty() || caseTerms.relatedWords.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .border(
                                1.dp,
                                when {
                                    totalExactMatchesCount > 0 -> if (isDark) Color(0xFFFF8F00) else Color(0xFFFF6F00)
                                    totalMatchesCount > 0 -> if (isDark) Color(0xFFD69E2E) else Color(0xFFB7791F)
                                    else -> tableBorderColor
                                },
                                RoundedCornerShape(8.dp)
                            ),
                        color = when {
                            totalExactMatchesCount > 0 -> if (isDark) Color(0x33FF8F00) else Color(0x22FF6F00)
                            totalMatchesCount > 0 -> if (isDark) Color(0x2BD69E2E) else Color(0x1FB7791F)
                            else -> Color.Transparent
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = if (totalMatchesCount > 0) Icons.Outlined.CheckCircle else Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint = when {
                                        totalExactMatchesCount > 0 -> if (isDark) Color(0xFFFFE082) else Color(0xFFBF360C)
                                        totalMatchesCount > 0 -> if (isDark) Color(0xFFFFD54F) else Color(0xFF975A16)
                                        else -> subtextColor
                                    },
                                    modifier = Modifier.size(14.dp)
                                )
                                Text(
                                    text = when {
                                        totalExactMatchesCount > 0 && totalWordMatchesCount > 0 ->
                                            "Auto-highlighting: $totalExactMatchesCount exact phrases (Highest priority) & $totalWordMatchesCount related words"
                                        totalExactMatchesCount > 0 ->
                                            "Auto-highlighting: $totalExactMatchesCount exact phrase matches (Highest priority)"
                                        totalWordMatchesCount > 0 ->
                                            "Auto-highlighting: $totalWordMatchesCount related word matches"
                                        else ->
                                            "No direct verbal matches with case totality terms"
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = if (totalMatchesCount > 0) FontWeight.SemiBold else FontWeight.Normal,
                                    color = if (totalMatchesCount > 0) textColor else subtextColor
                                )
                            }

                            if (totalMatchesCount > 0) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (showOnlyCaseMatches) (if (isDark) TerracottaLight else TerracottaPrimary) else (if (isDark) Color(0x33000000) else Color(0x18000000)),
                                    modifier = Modifier.clickable {
                                        showOnlyCaseMatches = !showOnlyCaseMatches
                                    }
                                ) {
                                    Text(
                                        text = if (showOnlyCaseMatches) "Show All" else "Matches Only",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (showOnlyCaseMatches) Color.White else (if (isDark) TerracottaLight else TerracottaPrimary),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search & Filter Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text("Filter symptoms or sections...", fontSize = 12.sp, color = subtextColor)
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = subtextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = subtextColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isDark) TerracottaLight else TerracottaPrimary,
                        unfocusedBorderColor = tableBorderColor,
                        focusedTextColor = textColor,
                        unfocusedTextColor = textColor
                    ),
                    singleLine = true
                )

                // Section Quick Jump Chips
                if (availableSections.size > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(horizontal = 2.dp)
                    ) {
                        item {
                            SectionChip(
                                label = "All (${sections.size})",
                                isSelected = selectedSectionFilter == null,
                                onClick = { selectedSectionFilter = null },
                                isDark = isDark
                            )
                        }
                        items(availableSections) { secName ->
                            val matchCount = sections.filter { it.section == secName }
                                .sumOf { sectionHighlightResults[it.id]?.totalCount ?: 0 }
                            SectionChip(
                                label = if (matchCount > 0) "$secName ($matchCount★)" else secName,
                                isSelected = selectedSectionFilter.equals(secName, ignoreCase = true),
                                hasMatches = matchCount > 0,
                                onClick = {
                                    selectedSectionFilter = if (selectedSectionFilter.equals(secName, ignoreCase = true)) null else secName
                                },
                                isDark = isDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Content View
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(
                                color = if (isDark) TerracottaLight else TerracottaPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                text = "Loading Boericke Materia Medica...",
                                fontSize = 12.sp,
                                color = subtextColor
                            )
                        }
                    }
                } else if (sections.isEmpty()) {
                    // Empty state: Dump not yet imported or remedy not found
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x18000000) else Color(0x06000000))
                            .border(1.dp, tableBorderColor, RoundedCornerShape(14.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.MenuBook,
                                contentDescription = null,
                                tint = if (isDark) TerracottaLight else TerracottaPrimary,
                                modifier = Modifier.size(44.dp)
                            )
                            Text(
                                text = "No Materia Medica Entry Available",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = textColor
                            )
                            Text(
                                text = "\"$remedyDisplayName\" ($remedyAbbreviation) is not present in Boericke's Pocket Manual of Materia Medica.",
                                fontSize = 12.sp,
                                color = subtextColor,
                                lineHeight = 17.sp,
                                modifier = Modifier.fillMaxWidth(0.85f)
                            )
                        }
                    }
                } else if (filteredSections.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No sections match your search criteria.",
                            fontSize = 13.sp,
                            color = subtextColor
                        )
                    }
                } else {
                    // Display sections in LazyColumn
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredSections, key = { it.id }) { sec ->
                            val highlightRes = sectionHighlightResults[sec.id] ?: MateriaMedicaHighlightUtils.highlightMatches(
                                text = sec.symptomText,
                                terms = caseTerms,
                                isDark = isDark,
                                userQuery = searchQuery
                            )
                            val annotatedText = highlightRes.annotatedText
                            val exactMatchCount = highlightRes.exactMatchCount
                            val wordMatchCount = highlightRes.wordMatchCount
                            val matchCount = highlightRes.totalCount

                            val sectionBorder = when {
                                exactMatchCount > 0 -> if (isDark) Color(0xFFFF8F00) else Color(0xFFFF6F00)
                                wordMatchCount > 0 -> if (isDark) Color(0x66FFB300) else Color(0x55FFA000)
                                else -> tableBorderColor
                            }

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(
                                        1.dp,
                                        sectionBorder,
                                        RoundedCornerShape(12.dp)
                                    ),
                                color = cardBg
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = sec.section.uppercase(),
                                            fontFamily = FontFamily.Serif,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            letterSpacing = 1.sp,
                                            color = if (isDark) TerracottaLight else TerracottaPrimary
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            if (exactMatchCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (isDark) Color(0x66FF8F00) else Color(0x4DFF6F00))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "$exactMatchCount EXACT PHRASE${if (exactMatchCount > 1) "S" else ""}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Black,
                                                        color = if (isDark) Color(0xFFFFE082) else Color(0xFFBF360C)
                                                    )
                                                }
                                            }
                                            if (wordMatchCount > 0) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(if (isDark) Color(0x44FFB300) else Color(0x33FFA000))
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = "$wordMatchCount WORD${if (wordMatchCount > 1) "S" else ""}",
                                                        fontSize = 9.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isDark) Color(0xFFFFD54F) else Color(0xFF8A4700)
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Text(
                                        text = annotatedText,
                                        fontSize = 13.sp,
                                        lineHeight = 19.sp,
                                        color = textColor
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
private fun SectionChip(
    label: String,
    isSelected: Boolean,
    hasMatches: Boolean = false,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val activeBg = if (isDark) TerracottaLight else TerracottaPrimary
    val inactiveBg = if (hasMatches) {
        if (isDark) Color(0x33FFB300) else Color(0x22FFA000)
    } else {
        if (isDark) Color(0x1FFFFFFF) else Color(0x0C000000)
    }
    val activeBorder = if (isDark) TerracottaLight else TerracottaPrimary
    val inactiveBorder = if (hasMatches) {
        if (isDark) Color(0xFFFFB300) else Color(0xFFFFA000)
    } else {
        if (isDark) DarkGlassBorder else LightGlassBorder
    }
    val contentColor = if (isSelected) {
        Color.White
    } else if (hasMatches) {
        if (isDark) Color(0xFFFFD54F) else Color(0xFF8A4700)
    } else {
        if (isDark) CreamIvoryText else DarkBrownText
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, if (isSelected) activeBorder else inactiveBorder, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        color = if (isSelected) activeBg else inactiveBg
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected || hasMatches) FontWeight.Bold else FontWeight.Normal,
            color = contentColor,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
        )
    }
}
