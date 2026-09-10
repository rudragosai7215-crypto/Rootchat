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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ImportProgress
import com.example.data.UserProfile
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
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    rubricCount: Int,
    remedyCount: Int,
    isDark: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onUpdateProfile: (String, String) -> Unit,
    onImportRepertory: () -> Unit,
    importProgress: ImportProgress,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentClinicianName = userProfile.clinicianName
    val currentRole = userProfile.role
    var isEditingName by remember { mutableStateOf(false) }
    var editedName by remember(currentClinicianName) { mutableStateOf(currentClinicianName) }
    var editedRole by remember(currentRole) { mutableStateOf(currentRole) }

    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val inputBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Text(
                    text = "CLINICIAN SETTINGS",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp,
                    letterSpacing = 2.sp,
                    color = if (isDark) TerracottaLight else TerracottaPrimary
                )
                Text(
                    text = "Practitioner Profile",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = textColor
                )
            }
        }

        // Profile Info Card
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x33B5502F) else Color(0x22B5502F))
                                .border(1.5.dp, if (isDark) TerracottaLight else TerracottaPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = if (isDark) TerracottaLight else TerracottaPrimary,
                                modifier = Modifier.size(30.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        if (!isEditingName) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = userProfile.clinicianName,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "${userProfile.role} • Offline Local Profile",
                                    fontSize = 12.sp,
                                    color = subtextColor
                                )
                            }
                            IconButton(
                                onClick = { isEditingName = true },
                                modifier = Modifier.testTag("btn_edit_profile")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = subtextColor)
                            }
                        } else {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = editedName,
                                    onValueChange = { editedName = it },
                                    label = { Text("Clinician Name") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (editedRole == "Doctor") TerracottaPrimary else Color(0x22FFFFFF))
                                            .clickable { editedRole = "Doctor" }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("Doctor", fontSize = 11.sp, color = if (editedRole == "Doctor") Color.White else textColor)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (editedRole == "Student") TerracottaPrimary else Color(0x22FFFFFF))
                                            .clickable { editedRole = "Student" }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                    ) {
                                        Text("Student", fontSize = 11.sp, color = if (editedRole == "Student") Color.White else textColor)
                                    }
                                }
                                Button(
                                    onClick = {
                                        if (editedName.isNotBlank()) {
                                            onUpdateProfile(editedName.trim(), editedRole)
                                            isEditingName = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("btn_save_profile_changes")
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Save Changes", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Appearance Mode (Light / Dark)
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.Brightness4 else Icons.Default.Brightness7,
                            contentDescription = null,
                            tint = if (isDark) TerracottaLight else TerracottaPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Glassmorphic Dark Theme",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = textColor
                            )
                            Text(
                                text = if (isDark) "Moody dark botanical canvas" else "Warm cream botanical canvas",
                                fontSize = 11.5.sp,
                                color = subtextColor
                            )
                        }
                    }

                    Switch(
                        checked = isDark,
                        onCheckedChange = { onToggleTheme(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = TerracottaPrimary
                        ),
                        modifier = Modifier.testTag("switch_dark_mode")
                    )
                }
            }
        }

        // Repertory Database Management
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = if (isDark) TerracottaLight else TerracottaPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "KENT REPERTORY DATABASE",
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                letterSpacing = 1.4.sp,
                                color = if (isDark) TerracottaLight else TerracottaPrimary
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (rubricCount > 0) Color(0x335B7E68) else Color(0x33E53935))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (rubricCount > 0) "READY" else "UNINITIALIZED",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (rubricCount > 0) (if (isDark) SageGreenLight else SageGreenPrimary) else Color(0xFFE53935)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x22FFFFFF) else Color(0x15000000))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$rubricCount",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                Text("Rubrics", fontSize = 10.5.sp, color = subtextColor)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isDark) Color(0x22FFFFFF) else Color(0x15000000))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$remedyCount",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = textColor
                                )
                                Text("Remedy Names", fontSize = 10.5.sp, color = subtextColor)
                            }
                        }
                    }

                    if (importProgress.isImporting) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = importProgress.currentStatus,
                                    fontSize = 11.5.sp,
                                    color = textColor
                                )
                                Text(
                                    text = "${importProgress.processedCount} / ${importProgress.totalCount}",
                                    fontSize = 11.5.sp,
                                    color = subtextColor
                                )
                            }
                            LinearProgressIndicator(
                                progress = {
                                    if (importProgress.totalCount > 0) {
                                        importProgress.processedCount.toFloat() / importProgress.totalCount
                                    } else {
                                        0f
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = TerracottaPrimary
                            )
                        }
                    } else {
                        Button(
                            onClick = onImportRepertory,
                            colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_import_repertory_dataset")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (rubricCount > 0) "Re-index Repertory Datasets" else "Import Repertory Datasets",
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Classical Wisdom Citation
        item {
            GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (isDark) SageGreenLight else SageGreenPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ORGANON OF MEDICINE • § 153",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.5.sp,
                            letterSpacing = 1.2.sp,
                            color = if (isDark) SageGreenLight else SageGreenPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "“In searching after a specific homoeopathic remedy... the more striking, singular, uncommon and peculiar (characteristic) signs and symptoms of the case of disease are chiefly and almost solely to be kept in view.”",
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontSize = 13.sp,
                        lineHeight = 19.sp,
                        color = textColor
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "— Dr. Christian Friedrich Samuel Hahnemann",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = subtextColor
                    )
                }
            }
        }
    }
}
