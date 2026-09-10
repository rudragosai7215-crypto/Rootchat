package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseEntity
import com.example.data.PdfExporter
import com.example.data.RepertoryRepository
import com.example.ui.theme.CaseTypeBadge
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.TerracottaContainer
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class LibraryFilter {
    ALL,
    ACUTE,
    CHRONIC,
    COMPLETED,
    DRAFTS
}

@Composable
fun CaseLibraryScreen(
    caseList: List<CaseEntity>,
    repertoryRepository: RepertoryRepository,
    onOpenCase: (String) -> Unit,
    onDuplicateCase: (String) -> Unit,
    onDeleteCase: (String) -> Unit,
    onNewCaseClicked: () -> Unit,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(LibraryFilter.ALL) }
    var caseToDelete by remember { mutableStateOf<CaseEntity?>(null) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    val filteredCases = remember(caseList, searchQuery, selectedFilter) {
        caseList.filter { c ->
            // Filter chip match
            val filterMatch = when (selectedFilter) {
                LibraryFilter.ALL -> true
                LibraryFilter.ACUTE -> c.caseType.equals("ACUTE", ignoreCase = true)
                LibraryFilter.CHRONIC -> c.caseType.equals("CHRONIC", ignoreCase = true)
                LibraryFilter.COMPLETED -> c.isCompleted
                LibraryFilter.DRAFTS -> !c.isCompleted
            }

            // Search query match
            val searchMatch = if (searchQuery.isBlank()) {
                true
            } else {
                val q = searchQuery.lowercase().trim()
                c.patientName.lowercase().contains(q) ||
                c.chiefComplaint.lowercase().contains(q) ||
                c.clinicalDiagnosis.lowercase().contains(q) ||
                c.prescribedRemedy.lowercase().contains(q) ||
                c.prescribedRemedyFullName.lowercase().contains(q)
            }

            filterMatch && searchMatch
        }
    }

    // Delete Confirmation Dialog
    if (caseToDelete != null) {
        AlertDialog(
            onDismissRequest = { caseToDelete = null },
            title = {
                Text(
                    text = "Delete Case Record?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "Are you sure you want to permanently delete the case for ${caseToDelete?.patientName.orEmpty()}? This action cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        caseToDelete?.let { onDeleteCase(it.id) }
                        caseToDelete = null
                        Toast.makeText(context, "Case record deleted", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Delete", color = Color(0xFFE53935), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { caseToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Library Title Header
        item {
            Column {
                Text(
                    text = "CLINICAL RECORDS",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 2.sp,
                    color = if (isDark) TerracottaLight else TerracottaPrimary
                )
                Text(
                    text = "Case Library",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = textColor
                )
                Text(
                    text = "${filteredCases.size} of ${caseList.size} cases available offline",
                    fontSize = 12.sp,
                    color = subtextColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search patient, diagnosis, complaint, remedy...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = subtextColor
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = subtextColor)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
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
                    .testTag("library_search_input")
            )
        }

        // Filter Chips Row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LibraryFilter.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = when (filter) {
                                    LibraryFilter.ALL -> "All Cases"
                                    LibraryFilter.ACUTE -> "Acute"
                                    LibraryFilter.CHRONIC -> "Chronic"
                                    LibraryFilter.COMPLETED -> "Completed"
                                    LibraryFilter.DRAFTS -> "Drafts"
                                },
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = if (isDark) TerracottaLight else TerracottaPrimary,
                            selectedLabelColor = Color.White
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = inputBorderColor,
                            selectedBorderColor = TerracottaPrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }
        }

        // Cases List
        if (filteredCases.isEmpty()) {
            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            tint = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No cases matching filter" else "No clinical cases found",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try adjusting your search criteria." else "Start your first case using the '+' button below.",
                            fontSize = 12.sp,
                            color = subtextColor
                        )
                    }
                }
            }
        } else {
            items(filteredCases, key = { it.id }) { item ->
                LibraryCaseCard(
                    caseEntity = item,
                    onOpen = { onOpenCase(item.id) },
                    onDuplicate = { onDuplicateCase(item.id) },
                    onDelete = { caseToDelete = item },
                    onExportPdf = {
                        coroutineScope.launch {
                            val selectedRubrics = RepertoryRepository.parseSelectedRubrics(item.selectedRubricsJson)
                            val scores = repertoryRepository.computeRepertorization(selectedRubrics)
                            val pdfFile = withContext(Dispatchers.IO) {
                                PdfExporter.exportCaseToPdf(context, item, selectedRubrics, scores)
                            }
                            if (pdfFile != null) {
                                PdfExporter.sharePdf(context, pdfFile)
                            } else {
                                Toast.makeText(context, "Error creating PDF export", Toast.LENGTH_SHORT).show()
                            }
                        }
                    },
                    isDark = isDark
                )
            }
        }
    }
}

@Composable
private fun LibraryCaseCard(
    caseEntity: CaseEntity,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onExportPdf: () -> Unit,
    isDark: Boolean
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val accentColor = if (isDark) TerracottaLight else TerracottaPrimary
    val dateStr = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(caseEntity.dateModified))

    val finalDiagnosisText = remember(caseEntity.clinicalDiagnosis) {
        val raw = caseEntity.clinicalDiagnosis
        when {
            raw.contains("Final:") -> {
                val parts = raw.split("|")
                val finalPart = parts.find { it.contains("Final:") }?.replace("Final:", "")?.trim()
                if (!finalPart.isNullOrBlank()) finalPart else "Not specified"
            }
            raw.contains("Provisional:") -> {
                val provPart = raw.replace("Provisional:", "").trim()
                if (provPart.isNotBlank()) provPart else "Not specified"
            }
            raw.isNotBlank() -> raw.trim()
            else -> "Not specified"
        }
    }

    val remedyText = remember(caseEntity.prescribedRemedy, caseEntity.prescribedRemedyFullName, caseEntity.potency) {
        val name = caseEntity.prescribedRemedyFullName.ifBlank { caseEntity.prescribedRemedy }.trim()
        if (name.isBlank()) {
            "Pending Similimum"
        } else if (caseEntity.potency.isNotBlank()) {
            "$name ${caseEntity.potency}"
        } else {
            name
        }
    }

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        isDark = isDark,
        shape = RoundedCornerShape(18.dp),
        onClick = onOpen
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Top Row: Badges and Menu
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CaseTypeBadge(caseType = caseEntity.caseType, isDark = isDark)
                    if (caseEntity.isCompleted) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isDark) Color(0x335B7E68) else Color(0x225B7E68))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "COMPLETE",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) TerracottaLight else TerracottaPrimary
                            )
                        }
                    }
                }

                // Options Menu (PDF, Duplicate, Delete)
                Box {
                    IconButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options",
                            tint = subtextColor
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                        modifier = Modifier.background(if (isDark) DarkGlassSurface else LightGlassSurface)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open / Edit Case") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onOpen()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Export as PDF") },
                            leadingIcon = { Icon(Icons.Default.PictureAsPdf, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onExportPdf()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Duplicate Case") },
                            leadingIcon = { Icon(Icons.Default.ContentCopy, contentDescription = null) },
                            onClick = {
                                menuExpanded = false
                                onDuplicate()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Case", color = Color(0xFFE53935)) },
                            leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFE53935)) },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            // 1. Patient Name (clearly labeled)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "PATIENT NAME",
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = caseEntity.patientName.ifBlank { "Unnamed Patient" },
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    val demographic = buildString {
                        if (caseEntity.patientAge.isNotBlank()) append("${caseEntity.patientAge}y")
                        if (caseEntity.patientGender.isNotBlank()) {
                            if (isNotEmpty()) append(" • ")
                            append(caseEntity.patientGender)
                        }
                    }
                    if (demographic.isNotBlank()) {
                        Text(
                            text = "($demographic)",
                            fontSize = 12.sp,
                            color = subtextColor
                        )
                    }
                }
            }

            // 2. Date (clearly labeled)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "DATE:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = subtextColor,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }

            // 3. Final Diagnosis (clearly labeled)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "FINAL DIAGNOSIS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = subtextColor,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = finalDiagnosisText,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textColor,
                    maxLines = 2
                )
            }

            // 4. Remedy (clearly labeled)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "REMEDY:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = accentColor,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = remedyText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }
        }
    }
}
