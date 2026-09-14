package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Person
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CaseEntity
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
import com.example.ui.theme.SageGreenContainer
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaContainer
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    clinicianName: String,
    clinicianRole: String,
    totalCases: Int,
    acuteCases: Int,
    chronicCases: Int,
    caseList: List<CaseEntity>,
    onStartAcute: () -> Unit,
    onStartChronic: () -> Unit,
    onOpenCase: (String) -> Unit,
    onViewAllLibrary: () -> Unit,
    onNavigateToReports: (() -> Unit)? = null,
    onNavigateToMateriaMedica: (() -> Unit)? = null,
    onNavigateToProfile: (() -> Unit)? = null,
    onToggleTheme: (() -> Unit)? = null,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    val filteredCases = remember(caseList, searchQuery) {
        if (searchQuery.isBlank()) {
            caseList.take(6)
        } else {
            val q = searchQuery.lowercase().trim()
            caseList.filter {
                it.patientName.lowercase().contains(q) ||
                it.chiefComplaint.lowercase().contains(q) ||
                it.prescribedRemedy.lowercase().contains(q) ||
                it.prescribedRemedyFullName.lowercase().contains(q)
            }.take(10)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Top Row: RootChart Clinical Studio brand + Theme Toggle
        item {
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
                        modifier = Modifier.size(38.dp),
                        shape = RoundedCornerShape(10.dp),
                        color = TerracottaPrimary
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Timeline,
                                contentDescription = "RootChart Logo",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Column {
                        Text(
                            text = "RootChart",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = textColor
                        )
                        Text(
                            text = "CLINICAL STUDIO",
                            fontFamily = FontFamily.SansSerif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 1.8.sp,
                            color = if (isDark) TerracottaLight else TerracottaPrimary
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.dp, if (isDark) DarkGlassBorder else LightGlassBorder, CircleShape)
                        .clickable { onToggleTheme?.invoke() },
                    color = if (isDark) Color(0x22FFFFFF) else Color(0x10000000)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isDark) Icons.Filled.WbSunny else Icons.Filled.NightsStay,
                            contentDescription = "Toggle Theme",
                            tint = if (isDark) Color(0xFFF7D58B) else DarkBrownText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Clinician greeting & Botanical illustration
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Welcome, ${clinicianName.ifBlank { "Rudra" }}",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = textColor
                    )
                    Text(
                        text = "Homeopathy Practitioner",
                        fontSize = 13.sp,
                        color = subtextColor
                    )
                }

                BotanicalLeafBranch(
                    modifier = Modifier.size(64.dp, 44.dp),
                    tint = if (isDark) SageGreenLight.copy(alpha = 0.6f) else SageGreenPrimary.copy(alpha = 0.5f)
                )
            }
        }

        // Hahnemann Quote Card
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark,
                shape = RoundedCornerShape(18.dp),
                elevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Spa,
                        contentDescription = null,
                        tint = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"You are not choosing homeopathy, Homeopathy is choosing you.\"",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.5.sp,
                        lineHeight = 20.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— Dr. Samuel Hahnemann",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.5.sp,
                        color = subtextColor,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }

        // Case Counts Split (Total, Acute, Chronic)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Total Cases Card
                StatPill(
                    label = "Total Cases",
                    count = totalCases,
                    icon = Icons.Outlined.Description,
                    iconColor = if (isDark) SageGreenLight else SageGreenPrimary,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                // Acute Cases Card (Terracotta)
                StatPill(
                    label = "Acute Cases",
                    count = acuteCases,
                    icon = Icons.Filled.LocalFireDepartment,
                    iconColor = if (isDark) TerracottaLight else TerracottaPrimary,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                // Chronic Cases Card (Sage Green)
                StatPill(
                    label = "Chronic Cases",
                    count = chronicCases,
                    icon = Icons.Filled.Spa,
                    iconColor = if (isDark) SageGreenLight else SageGreenPrimary,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title: Start New Clinical Protocol
        item {
            Text(
                text = "Start New Clinical Protocol",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = textColor
            )
        }

        // Two Large "Start New Case" Cards (Acute / Chronic)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Acute Card
                StartCaseBigCard(
                    title = "Acute Case",
                    description = "Exciting course, LS/MC, rapid & short duration.",
                    containerColor = if (isDark) Color(0xFF9E4429) else Color(0xFFB5502F),
                    icon = Icons.Filled.Bolt,
                    testTag = "card_start_acute",
                    onClick = onStartAcute,
                    modifier = Modifier.weight(1f)
                )

                // Chronic Card
                StartCaseBigCard(
                    title = "Chronic Case",
                    description = "Miasm, constitutional, past/family history.",
                    containerColor = if (isDark) Color(0xFF385341) else Color(0xFF4A6B56),
                    icon = Icons.Filled.Spa,
                    testTag = "card_start_chronic",
                    onClick = onStartChronic,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Clinical Workspace Modules
        item {
            Column {
                Text(
                    text = "CLINICAL WORKSPACE",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    letterSpacing = 1.8.sp,
                    color = if (isDark) TerracottaLight else TerracottaPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    WorkspaceQuickPill(
                        title = "Case Library",
                        subtitle = "${caseList.size} cases",
                        icon = Icons.Filled.MedicalServices,
                        accentColor = textColor,
                        isDark = isDark,
                        modifier = Modifier.weight(1f),
                        onClick = onViewAllLibrary
                    )

                    if (onNavigateToReports != null) {
                        WorkspaceQuickPill(
                            title = "Analytics",
                            subtitle = "Reports",
                            icon = Icons.Filled.Analytics,
                            accentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToReports
                        )
                    }

                    if (onNavigateToMateriaMedica != null) {
                        WorkspaceQuickPill(
                            title = "Materia Medica",
                            subtitle = "Browse / Boericke",
                            icon = Icons.Filled.AutoStories,
                            accentColor = if (isDark) SageGreenLight else SageGreenPrimary,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToMateriaMedica
                        )
                    }

                    if (onNavigateToProfile != null) {
                        WorkspaceQuickPill(
                            title = "Profile",
                            subtitle = "Settings",
                            icon = Icons.Filled.AccountCircle,
                            accentColor = textColor,
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            onClick = onNavigateToProfile
                        )
                    }
                }
            }
        }

        // Case Library Preview & Search Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CASE LIBRARY",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        letterSpacing = 1.8.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )

                    if (caseList.isNotEmpty()) {
                        Text(
                            text = "View All (${caseList.size}) →",
                            fontFamily = FontFamily.Serif,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier
                                .clickable { onViewAllLibrary() }
                                .padding(4.dp)
                                .testTag("btn_view_all_library")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Search Box
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by patient, complaint, remedy...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = subtextColor
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Filled.Clear, contentDescription = "Clear search", tint = subtextColor)
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
                        unfocusedContainerColor = if (isDark) Color(0x22000000) else Color(0x10FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("home_search_input")
                )
            }
        }

        // Case List Items or Empty State
        if (filteredCases.isEmpty()) {
            item {
                GlassPanel(
                    modifier = Modifier.fillMaxWidth(),
                    isDark = isDark
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.MedicalServices,
                            contentDescription = null,
                            tint = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "No matching cases found" else "No cases recorded yet",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (searchQuery.isNotEmpty()) "Try searching by a different term" else "Tap Acute or Chronic above to initiate your first clinical case.",
                            fontSize = 12.sp,
                            color = subtextColor,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredCases, key = { it.id }) { item ->
                HomeCaseItem(
                    caseEntity = item,
                    isDark = isDark,
                    onClick = { onOpenCase(item.id) }
                )
            }
        }
    }
}

@Composable
fun BotanicalLeafBranch(
    modifier: Modifier = Modifier,
    tint: Color = SageGreenPrimary.copy(alpha = 0.5f)
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val stemPath = Path().apply {
            moveTo(w * 0.92f, h * 0.08f)
            quadraticTo(w * 0.5f, h * 0.45f, w * 0.08f, h * 0.92f)
        }
        drawPath(
            path = stemPath,
            color = tint,
            style = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round)
        )

        val leafConfigs = listOf(
            Triple(w * 0.82f, h * 0.16f, -30f),
            Triple(w * 0.68f, h * 0.28f, 35f),
            Triple(w * 0.52f, h * 0.42f, -32f),
            Triple(w * 0.38f, h * 0.56f, 38f),
            Triple(w * 0.24f, h * 0.72f, -35f),
            Triple(w * 0.12f, h * 0.86f, 32f)
        )

        for ((lx, ly, rot) in leafConfigs) {
            drawContext.canvas.save()
            drawContext.canvas.translate(lx, ly)
            drawContext.canvas.rotate(rot)
            val leafPath = Path().apply {
                moveTo(0f, 0f)
                quadraticTo(10.dp.toPx(), -5.dp.toPx(), 18.dp.toPx(), 0f)
                quadraticTo(10.dp.toPx(), 5.dp.toPx(), 0f, 0f)
            }
            drawPath(
                path = leafPath,
                color = tint,
                style = Stroke(width = 1.3.dp.toPx())
            )
            drawContext.canvas.restore()
        }
    }
}

@Composable
private fun StatPill(
    label: String,
    count: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    GlassPanel(
        modifier = modifier,
        isDark = isDark,
        shape = RoundedCornerShape(16.dp),
        elevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$count",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = if (isDark) CreamIvoryText else DarkBrownText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = if (isDark) CreamIvoryMuted else DarkBrownMuted,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun StartCaseBigCard(
    title: String,
    description: String,
    containerColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = containerColor,
        shape = RoundedCornerShape(20.dp),
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0x30FFFFFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 10.5.sp,
                lineHeight = 14.5.sp,
                color = Color.White.copy(alpha = 0.85f),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Start Protocol",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.White
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun HomeCaseItem(
    caseEntity: CaseEntity,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(caseEntity.dateModified))

    GlassPanel(
        modifier = Modifier.fillMaxWidth(),
        isDark = isDark,
        shape = RoundedCornerShape(18.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = caseEntity.patientName.ifEmpty { "Unnamed Patient" },
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = textColor
                    )
                    CaseTypeBadge(caseType = caseEntity.caseType, isDark = isDark)
                }

                Spacer(modifier = Modifier.height(3.dp))

                val demographic = buildString {
                    if (caseEntity.patientAge.isNotBlank()) append("${caseEntity.patientAge}y")
                    if (caseEntity.patientGender.isNotBlank()) {
                        if (isNotEmpty()) append(" • ")
                        append(caseEntity.patientGender)
                    }
                    if (isNotEmpty()) append(" • ")
                    append(dateStr)
                }
                Text(
                    text = demographic,
                    fontSize = 11.5.sp,
                    color = subtextColor
                )

                if (caseEntity.chiefComplaint.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Complaint: ${caseEntity.chiefComplaint}",
                        fontSize = 12.sp,
                        color = textColor,
                        maxLines = 1
                    )
                }

                if (caseEntity.prescribedRemedy.isNotBlank() || caseEntity.prescribedRemedyFullName.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Rx: ${caseEntity.prescribedRemedyFullName.ifEmpty { caseEntity.prescribedRemedy }} ${caseEntity.potency}",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Open Case",
                tint = subtextColor,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun WorkspaceQuickPill(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    accentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassPanel(
        modifier = modifier,
        isDark = isDark,
        shape = RoundedCornerShape(14.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 10.5.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) CreamIvoryText else DarkBrownText,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text = subtitle,
                fontSize = 9.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                color = if (isDark) CreamIvoryMuted else DarkBrownMuted,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}
