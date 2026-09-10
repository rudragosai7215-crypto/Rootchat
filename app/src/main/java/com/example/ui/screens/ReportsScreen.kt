package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CaseEntity
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

@Composable
fun ReportsScreen(
    caseList: List<CaseEntity>,
    totalCases: Int,
    acuteCases: Int,
    chronicCases: Int,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    // Calculate Top Prescribed Remedies
    val topRemedies = remember(caseList) {
        caseList
            .filter { it.prescribedRemedy.isNotBlank() }
            .groupBy { it.prescribedRemedyFullName.ifEmpty { it.prescribedRemedy }.uppercase() }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(6)
    }

    // Calculate Potency Distribution
    val topPotencies = remember(caseList) {
        caseList
            .filter { it.potency.isNotBlank() }
            .groupBy { it.potency.uppercase() }
            .mapValues { it.value.size }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "CLINICAL ANALYTICS",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 2.sp,
                    color = if (isDark) TerracottaLight else TerracottaPrimary
                )
                Text(
                    text = "Practice Reports",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = textColor
                )
                Text(
                    text = "Prescription patterns & clinical case distributions",
                    fontSize = 12.sp,
                    color = subtextColor,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Case Distribution Card
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "CASE RATIO DISTRIBUTION",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    if (totalCases == 0) {
                        Text("No cases recorded yet to compute ratios.", fontSize = 12.5.sp, color = subtextColor)
                    } else {
                        val acutePct = (acuteCases.toFloat() / totalCases * 100).toInt()
                        val chronicPct = 100 - acutePct

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Acute Protocols: $acuteCases ($acutePct%)", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) TerracottaLight else TerracottaPrimary)
                            Text("Chronic Protocols: $chronicCases ($chronicPct%)", fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) SageGreenLight else SageGreenPrimary)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        LinearProgressIndicator(
                            progress = { if (totalCases > 0) acuteCases.toFloat() / totalCases else 0f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = TerracottaPrimary,
                            trackColor = SageGreenPrimary
                        )
                    }
                }
            }
        }

        // Top Prescribed Remedies
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "MOST PRESCRIBED SIMILIMUMS",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (topRemedies.isEmpty()) {
                        Text("Prescribe remedies in the protocol flow to see frequency rankings.", fontSize = 12.5.sp, color = subtextColor)
                    } else {
                        val maxCount = topRemedies.maxOfOrNull { it.second } ?: 1
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            topRemedies.forEach { (remedy, count) ->
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(remedy, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = textColor)
                                        Text("$count ${if (count == 1) "case" else "cases"}", fontSize = 12.sp, color = subtextColor)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    LinearProgressIndicator(
                                        progress = { count.toFloat() / maxCount },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(3.dp)),
                                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                                        trackColor = if (isDark) Color(0x33FFFFFF) else Color(0x22000000)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Potency Distribution
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "POTENCY PREFERENCES",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (topPotencies.isEmpty()) {
                        Text("Potency distribution will populate as prescriptions are written.", fontSize = 12.5.sp, color = subtextColor)
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            topPotencies.forEach { (potency, count) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isDark) Color(0x22FFFFFF) else Color(0x15000000))
                                        .padding(10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(potency, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = textColor)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text("$count", fontSize = 11.sp, color = if (isDark) TerracottaLight else TerracottaPrimary)
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
