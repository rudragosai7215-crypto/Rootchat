package com.example.ui.screens.caseflow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.ClinicalFormData
import com.example.data.LsmcRow
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

@Composable
fun AcuteLsmcStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "CHIEF COMPLAINTS (LSMC FORM WITH O.D.P.)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Text(
                text = "Record complaints in complete homeopathic 4-dimensional symptomatology: Location with extension, Sensation/character, Modalities (< Aggravation / > Amelioration), and Concomitant phenomena.",
                fontSize = 12.sp,
                color = subtextColor
            )

            // O.D.P. field (Onset, Duration, Progress)
            ClinicalTextField(
                label = "O.D.P. (ONSET, DURATION, PROGRESSION)",
                value = formData.acuteOdp,
                onValueChange = { onUpdateForm(formData.copy(acuteOdp = it)) },
                placeholder = "e.g. Sudden violent onset after cold dry wind 6 hours ago; rapidly progressing with high fever...",
                testTag = "input_acute_odp",
                isDark = isDark,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "4-COLUMN LSMC TABLE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary,
                fontFamily = FontFamily.Serif
            )

            // Scrollable 4-column Table
            val horizontalScrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(horizontalScrollState)
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, tableBorderColor, RoundedCornerShape(12.dp))
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .background(if (isDark) Color(0x33B5502F) else Color(0x20B5502F))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = "LOCATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.width(180.dp)
                    )
                    Text(
                        text = "SENSATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.width(180.dp)
                    )
                    Text(
                        text = "MODALITY (< / >)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.width(180.dp)
                    )
                    Text(
                        text = "CONCOMITANT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        modifier = Modifier.width(180.dp)
                    )
                    Spacer(modifier = Modifier.width(44.dp))
                }

                // Table Rows
                formData.lsmcRows.forEachIndexed { index, row ->
                    Row(
                        modifier = Modifier
                            .background(if (index % 2 == 1) (if (isDark) Color(0x15FFFFFF) else Color(0x0A000000)) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${index + 1}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = subtextColor,
                            modifier = Modifier.width(32.dp)
                        )

                        // Location
                        TableInputCell(
                            value = row.location,
                            onValueChange = { newVal ->
                                val updated = formData.lsmcRows.toMutableList()
                                updated[index] = row.copy(location = newVal)
                                onUpdateForm(formData.copy(lsmcRows = updated))
                            },
                            placeholder = "e.g. Right throat, extends to ear",
                            modifier = Modifier.width(180.dp),
                            isDark = isDark,
                            testTag = "input_lsmc_loc_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sensation
                        TableInputCell(
                            value = row.sensation,
                            onValueChange = { newVal ->
                                val updated = formData.lsmcRows.toMutableList()
                                updated[index] = row.copy(sensation = newVal)
                                onUpdateForm(formData.copy(lsmcRows = updated))
                            },
                            placeholder = "e.g. Burning, splinter-like stinging",
                            modifier = Modifier.width(180.dp),
                            isDark = isDark,
                            testTag = "input_lsmc_sens_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Modality
                        TableInputCell(
                            value = row.modality,
                            onValueChange = { newVal ->
                                val updated = formData.lsmcRows.toMutableList()
                                updated[index] = row.copy(modality = newVal)
                                onUpdateForm(formData.copy(lsmcRows = updated))
                            },
                            placeholder = "e.g. < Cold drinks, > Warm sips",
                            modifier = Modifier.width(180.dp),
                            isDark = isDark,
                            testTag = "input_lsmc_mod_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Concomitant
                        TableInputCell(
                            value = row.concomitant,
                            onValueChange = { newVal ->
                                val updated = formData.lsmcRows.toMutableList()
                                updated[index] = row.copy(concomitant = newVal)
                                onUpdateForm(formData.copy(lsmcRows = updated))
                            },
                            placeholder = "e.g. Profuse cold sweat, restless fear",
                            modifier = Modifier.width(180.dp),
                            isDark = isDark,
                            testTag = "input_lsmc_concom_$index"
                        )

                        // Delete button
                        IconButton(
                            onClick = {
                                if (formData.lsmcRows.size > 1) {
                                    val updated = formData.lsmcRows.toMutableList()
                                    updated.removeAt(index)
                                    onUpdateForm(formData.copy(lsmcRows = updated))
                                } else {
                                    // Reset single row
                                    onUpdateForm(formData.copy(lsmcRows = listOf(LsmcRow())))
                                }
                            },
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_delete_lsmc_$index")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete row",
                                tint = subtextColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Button to Add Row
            Button(
                onClick = {
                    val updated = formData.lsmcRows.toMutableList()
                    updated.add(LsmcRow())
                    onUpdateForm(formData.copy(lsmcRows = updated))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_add_lsmc_row")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Complaint Row", fontSize = 12.5.sp)
            }
        }
    }
}

@Composable
fun TableInputCell(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    isDark: Boolean,
    testTag: String
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val borderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 11.5.sp, maxLines = 1) },
        singleLine = true,
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TerracottaPrimary,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            focusedContainerColor = if (isDark) Color(0x33000000) else Color(0x20FFFFFF),
            unfocusedContainerColor = if (isDark) Color(0x20000000) else Color(0x10FFFFFF)
        ),
        modifier = modifier.testTag(testTag)
    )
}
