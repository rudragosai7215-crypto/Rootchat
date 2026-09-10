package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.DarkGlassSurface
import com.example.ui.theme.GlassBackgroundScaffold
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.LightGlassSurface
import com.example.ui.theme.SageGreenContainer
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaContainer
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

@Composable
fun EntryScreen(
    isDark: Boolean,
    onProfileCreated: (name: String, role: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var clinicianName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Doctor") } // "Doctor" or "Student"
    var errorText by remember { mutableStateOf<String?>(null) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    GlassBackgroundScaffold(isDark = isDark) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // App Icon
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(26.dp))
                    .border(2.dp, if (isDark) TerracottaLight.copy(alpha = 0.6f) else TerracottaPrimary.copy(alpha = 0.6f), RoundedCornerShape(26.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_rootchart_icon),
                    contentDescription = "RootChart Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "RootChart",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp,
                color = textColor
            )

            Text(
                text = "CLINICAL REPERTORIZATION STUDIO",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                letterSpacing = 2.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            Text(
                text = "Classical Wisdom. Modern Practice.",
                fontFamily = FontFamily.Serif,
                fontSize = 13.sp,
                color = subtextColor,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Glass Profile Setup Card
            GlassPanel(
                modifier = Modifier.fillMaxWidth(),
                isDark = isDark
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Clinician Registration",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = textColor
                    )
                    Text(
                        text = "Set up your local offline profile to sign clinical case records.",
                        fontSize = 12.5.sp,
                        color = subtextColor,
                        modifier = Modifier.padding(top = 4.dp, bottom = 18.dp)
                    )

                    // Clinician Name Input
                    Text(
                        text = "CLINICIAN NAME",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = clinicianName,
                        onValueChange = {
                            clinicianName = it
                            errorText = null
                        },
                        placeholder = { Text("e.g. Dr. Rudra Goswami") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TerracottaPrimary,
                            unfocusedBorderColor = inputBorderColor,
                            focusedTextColor = textColor,
                            unfocusedTextColor = textColor,
                            focusedContainerColor = if (isDark) Color(0x33000000) else Color(0x22FFFFFF),
                            unfocusedContainerColor = if (isDark) Color(0x22000000) else Color(0x11FFFFFF)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_clinician_name")
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Role Selector (Doctor / Student)
                    Text(
                        text = "CLINICAL ROLE",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        RoleOptionCard(
                            roleName = "Doctor",
                            subtitle = "Practicing Homoeopath",
                            icon = Icons.Filled.LocalHospital,
                            selected = selectedRole == "Doctor",
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            testTag = "role_doctor",
                            onClick = { selectedRole = "Doctor" }
                        )

                        RoleOptionCard(
                            roleName = "Student",
                            subtitle = "BHMS / MD Scholar",
                            icon = Icons.Filled.School,
                            selected = selectedRole == "Student",
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                            testTag = "role_student",
                            onClick = { selectedRole = "Student" }
                        )
                    }

                    if (errorText != null) {
                        Text(
                            text = errorText!!,
                            color = Color(0xFFE53935),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Enter Studio Button
                    Button(
                        onClick = {
                            if (clinicianName.trim().isEmpty()) {
                                errorText = "Please enter your name to proceed."
                            } else {
                                onProfileCreated(clinicianName.trim(), selectedRole)
                            }
                        },
                        enabled = clinicianName.trim().isNotEmpty(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TerracottaPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("btn_enter_studio")
                    ) {
                        Text(
                            text = "Enter Repertorization Studio",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun RoleOptionCard(
    roleName: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    val borderColor = if (selected) {
        if (isDark) TerracottaLight else TerracottaPrimary
    } else {
        if (isDark) DarkGlassBorder else LightGlassBorder
    }

    val bgColor = if (selected) {
        TerracottaContainer.copy(alpha = 0.4f)
    } else {
        if (isDark) Color(0x22FFFFFF) else Color(0x15FFFFFF)
    }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(if (selected) 2.dp else 1.dp, borderColor, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) TerracottaPrimary else if (isDark) CreamIvoryMuted else DarkBrownMuted,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = roleName,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = textColor,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = if (isDark) CreamIvoryMuted else DarkBrownMuted,
                textAlign = TextAlign.Center,
                maxLines = 1,
                softWrap = false
            )
        }
    }
}
