package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
        // Clinician greeting & App header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ROOTCHART",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 2.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Text(
                        text = "Welcome, ${clinicianName.ifBlank { "Dr. Rudra Goswami" }}",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = textColor
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0x33FFFFFF) else Color(0x22000000))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = clinicianRole.uppercase(Locale.getDefault()),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = if (isDark) CreamIvoryMuted else DarkBrownMuted
                    )
                }
            }
        }

        // Hero Card with stethoscope image and classical quote
        item {
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark,
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stethoscope Image
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .border(1.5.dp, if (isDark) TerracottaLight.copy(alpha = 0.5f) else TerracottaPrimary.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_stethoscope),
                            contentDescription = "Clinical Stethoscope",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "“You not choose homoeopathy, Homoeopathy choose you.”",
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            lineHeight = 21.sp,
                            color = textColor
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "CLASSICAL REPERTORIZATION STUDIO",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.4.sp,
                            color = if (isDark) TerracottaLight else TerracottaPrimary
                        )
                    }
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
                    label = "TOTAL CASES",
                    count = totalCases,
                    accentColor = textColor,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                // Acute Cases Card (Terracotta)
                StatPill(
                    label = "ACUTE CASES",
                    count = acuteCases,
                    accentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )

                // Chronic Cases Card (Sage Green)
                StatPill(
                    label = "CHRONIC CASES",
                    count = chronicCases,
                    accentColor = if (isDark) SageGreenLight else SageGreenPrimary,
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Section Title: Start New Case
        item {
            Text(
                text = "START NEW CLINICAL PROTOCOL",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                letterSpacing = 1.8.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
        }

        // Two Large "Start New Case" Cards (Acute / Chronic)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Acute Card
                StartCaseBigCard(
                    title = "Acute Case",
                    protocolInfo = "",
                    description = "Exciting cause, LSMC, rapid similimum",
                    badge = "ACUTE",
                    accentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                    containerColor = TerracottaContainer,
                    icon = Icons.Filled.Bolt,
                    isDark = isDark,
                    testTag = "card_start_acute",
                    onClick = onStartAcute,
                    modifier = Modifier.weight(1f)
                )

                // Chronic Card
                StartCaseBigCard(
                    title = "Chronic Case",
                    protocolInfo = "",
                    description = "Miasm, constitutional, past/family history",
                    badge = "CHRONIC",
                    accentColor = if (isDark) SageGreenLight else SageGreenPrimary,
                    containerColor = SageGreenContainer,
                    icon = Icons.Filled.Grass,
                    isDark = isDark,
                    testTag = "card_start_chronic",
                    onClick = onStartChronic,
                    modifier = Modifier.weight(1f)
                )
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
private fun StatPill(
    label: String,
    count: Int,
    accentColor: Color,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    GlassPanel(
        modifier = modifier,
        isDark = isDark,
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$count",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = accentColor
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                color = if (isDark) CreamIvoryMuted else DarkBrownMuted
            )
        }
    }
}

@Composable
private fun StartCaseBigCard(
    title: String,
    protocolInfo: String,
    description: String,
    badge: String,
    accentColor: Color,
    containerColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDark: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(
        modifier = modifier,
        isDark = isDark,
        shape = RoundedCornerShape(22.dp),
        elevation = 6.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(containerColor.copy(alpha = 0.22f))
                .padding(16.dp)
                .testTag(testTag)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(1.dp, accentColor.copy(alpha = 0.6f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .border(1.dp, accentColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = textColor
            )

            if (protocolInfo.isNotBlank()) {
                Text(
                    text = protocolInfo,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                color = subtextColor,
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
                    color = accentColor
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
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
