package com.example.ui.screens.materiamedica

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.LibraryBooks
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ImportProgress
import com.example.data.MateriaMedicaRemedySummary
import com.example.data.MateriaMedicaRepository
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MateriaMedicaBrowserScreen(
    materiaMedicaRepository: MateriaMedicaRepository,
    importProgress: ImportProgress = ImportProgress(),
    onImportRequested: ((Uri) -> Unit)? = null,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val cardBg = if (isDark) DarkGlassSurface else LightGlassSurface

    var searchQuery by remember { mutableStateOf("") }
    var selectedLetter by remember { mutableStateOf<Char?>(null) }
    var viewingRemedyAbbrev by remember { mutableStateOf<String?>(null) }
    var viewingRemedyName by remember { mutableStateOf<String?>(null) }

    val allSummaries by materiaMedicaRepository.getAllRemediesSummary().collectAsState(initial = emptyList())
    val totalSections by materiaMedicaRepository.getTotalSectionCount().collectAsState(initial = 0)
    val distinctRemedies by materiaMedicaRepository.getDistinctRemedyCount().collectAsState(initial = 0)

    // Filter summaries by search query and letter
    val filteredSummaries = remember(allSummaries, searchQuery, selectedLetter) {
        allSummaries.filter { rem ->
            val matchesLetter = selectedLetter == null || rem.remedyFullName.startsWith(selectedLetter.toString(), ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                    rem.remedyAbbreviation.contains(searchQuery, ignoreCase = true) ||
                    rem.remedyFullName.contains(searchQuery, ignoreCase = true)
            matchesLetter && matchesSearch
        }
    }

    val alphabet = ('A'..'Z').toList()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoStories,
                        contentDescription = null,
                        tint = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Materia Medica",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = textColor
                    )
                }
                Text(
                    text = if (distinctRemedies > 0) {
                        "$distinctRemedies Remedies · $totalSections Sections"
                    } else {
                        "William Boericke (1906)"
                    },
                    fontSize = 12.sp,
                    color = subtextColor
                )
            }

            // Pre-bundled Status Badge
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, if (isDark) Color(0x335B7E68) else Color(0x225B7E68), RoundedCornerShape(8.dp)),
                color = if (isDark) Color(0x225B7E68) else Color(0x125B7E68)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Pre-bundled & Offline",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Mandatory Source Attribution Banner
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
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "Source: Boericke's Pocket Manual of Homoeopathic Materia Medica (1906, Public Domain)",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = subtextColor
                )
            }
        }

        // Import progress status
        if (importProgress.isImporting) {
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, if (isDark) TerracottaLight else TerracottaPrimary, RoundedCornerShape(8.dp)),
                color = if (isDark) Color(0x22B5502F) else Color(0x12B5502F)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = importProgress.currentStatus,
                        fontSize = 12.sp,
                        color = textColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Search Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Search remedy by name or abbreviation (e.g. Arsenicum, ars)...", fontSize = 12.sp, color = subtextColor)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = subtextColor,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = subtextColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isDark) TerracottaLight else TerracottaPrimary,
                unfocusedBorderColor = tableBorderColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Alphabet Quick Jump Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
        ) {
            item {
                LetterChip(
                    label = "ALL",
                    isSelected = selectedLetter == null,
                    onClick = { selectedLetter = null },
                    isDark = isDark
                )
            }
            items(alphabet) { char ->
                val hasRemedies = allSummaries.any { it.remedyFullName.startsWith(char.toString(), ignoreCase = true) }
                LetterChip(
                    label = char.toString(),
                    isSelected = selectedLetter == char,
                    hasRemedies = hasRemedies,
                    onClick = {
                        selectedLetter = if (selectedLetter == char) null else char
                    },
                    isDark = isDark
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Content Area
        if (allSummaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, tableBorderColor, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    CircularProgressIndicator(
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.size(36.dp)
                    )

                    Text(
                        text = "Accessing Boericke Materia Medica...",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )

                    Text(
                        text = "Loading pre-bundled remedy monograph sections from the clinical database...",
                        fontSize = 12.sp,
                        color = subtextColor
                    )
                }
            }
        } else if (filteredSummaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No remedies match \"$searchQuery\"",
                    fontSize = 13.sp,
                    color = subtextColor
                )
            }
        } else {
            // Remedy List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredSummaries, key = { it.remedyAbbreviation }) { rem ->
                    RemedySummaryCard(
                        summary = rem,
                        onClick = {
                            viewingRemedyAbbrev = rem.remedyAbbreviation
                            viewingRemedyName = rem.remedyFullName
                        },
                        isDark = isDark,
                        tableBorderColor = tableBorderColor,
                        cardBg = cardBg,
                        textColor = textColor,
                        subtextColor = subtextColor
                    )
                }
            }
        }
    }

    // Modal Remedy Full Profile Dialog
    if (viewingRemedyAbbrev != null) {
        MateriaMedicaViewerDialog(
            remedyAbbreviation = viewingRemedyAbbrev!!,
            initialFullName = viewingRemedyName,
            materiaMedicaRepository = materiaMedicaRepository,
            onDismiss = {
                viewingRemedyAbbrev = null
                viewingRemedyName = null
            },
            onImportRequested = onImportRequested,
            isDark = isDark
        )
    }
}

@Composable
private fun RemedySummaryCard(
    summary: MateriaMedicaRemedySummary,
    onClick: () -> Unit,
    isDark: Boolean,
    tableBorderColor: Color,
    cardBg: Color,
    textColor: Color,
    subtextColor: Color
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = cardBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isDark) Color(0x33B5502F) else Color(0x18B5502F))
                        .border(1.dp, if (isDark) TerracottaLight else TerracottaPrimary, RoundedCornerShape(6.dp))
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = summary.remedyAbbreviation.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                }

                Column {
                    Text(
                        text = summary.remedyFullName,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.5.sp,
                        color = textColor
                    )
                    Text(
                        text = "${summary.sectionCount} sections · Boericke (1906)",
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }
            }

            Icon(
                imageVector = Icons.Filled.MenuBook,
                contentDescription = "Read profile",
                tint = subtextColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun LetterChip(
    label: String,
    isSelected: Boolean,
    hasRemedies: Boolean = true,
    onClick: () -> Unit,
    isDark: Boolean
) {
    val activeBg = if (isDark) TerracottaLight else TerracottaPrimary
    val inactiveBg = if (isDark) Color(0x1FFFFFFF) else Color(0x0C000000)
    val activeBorder = if (isDark) TerracottaLight else TerracottaPrimary
    val inactiveBorder = if (isDark) DarkGlassBorder else LightGlassBorder

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, if (isSelected) activeBorder else inactiveBorder, RoundedCornerShape(8.dp))
            .clickable(enabled = hasRemedies, onClick = onClick),
        color = if (isSelected) activeBg else inactiveBg
    ) {
        Text(
            text = label,
            fontSize = 10.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) Color.White else (if (hasRemedies) (if (isDark) CreamIvoryText else DarkBrownText) else (if (isDark) Color(0x44FFFFFF) else Color(0x44000000))),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
