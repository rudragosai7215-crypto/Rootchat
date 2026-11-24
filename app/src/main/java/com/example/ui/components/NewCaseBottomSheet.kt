package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.SageGreenContainer
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaContainer
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewCaseBottomSheet(
    onDismiss: () -> Unit,
    onSelectAcute: () -> Unit,
    onSelectChronic: () -> Unit,
    isDark: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState()
) {
    val sheetBg = if (isDark) DarkGlassSurface else LightGlassSurface
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        scrimColor = Color(0x66000000),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp, top = 6.dp)
        ) {
            Text(
                text = "Initiate Clinical Protocol",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Select Protocol Type",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = textColor
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Acute Protocol Card
            ProtocolCard(
                title = "Acute Protocol",
                badge = "ACUTE",
                description = "Rapid totality, LS/MC particular modalities, exciting cause & sudden keynotes.",
                accentColor = if (isDark) TerracottaLight else TerracottaPrimary,
                icon = Icons.Filled.FlashOn,
                isDark = isDark,
                testTag = "btn_start_acute_protocol",
                onClick = {
                    onDismiss()
                    onSelectAcute()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Chronic Protocol Card
            ProtocolCard(
                title = "Chronic Protocol",
                badge = "CHRONIC",
                description = "Comprehensive Hahnemannian case: miasmatic inquiry, past/family history & constitutional similimum.",
                accentColor = if (isDark) SageGreenLight else SageGreenPrimary,
                icon = Icons.Filled.Spa,
                isDark = isDark,
                testTag = "btn_start_chronic_protocol",
                onClick = {
                    onDismiss()
                    onSelectChronic()
                }
            )
        }
    }
}

@Composable
private fun ProtocolCard(
    title: String,
    badge: String,
    description: String,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isDark: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val borderColor = accentColor.copy(alpha = if (isDark) 0.35f else 0.25f)
    val surfaceColor = if (isDark) Color(0xFF1B201E) else Color(0xFFF9F7F4)
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .testTag(testTag),
        color = surfaceColor,
        shape = RoundedCornerShape(14.dp),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(accentColor)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = textColor
                        )
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
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = description,
                        fontSize = 11.sp,
                        lineHeight = 15.sp,
                        color = subtextColor
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
