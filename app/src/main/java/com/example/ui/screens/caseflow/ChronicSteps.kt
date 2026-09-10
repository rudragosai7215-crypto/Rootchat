package com.example.ui.screens.caseflow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import com.example.data.FamilyHistoryRow
import com.example.data.PastHistoryRow
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.SageGreenLight
import com.example.ui.theme.SageGreenPrimary

@Composable
fun ChronicChiefComplaintStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "CHIEF COMPLAINTS (PATIENT'S OWN WORDS) *",
                value = formData.chronicChiefComplaint,
                onValueChange = { onUpdateForm(formData.copy(chronicChiefComplaint = it)) },
                placeholder = "Describe the primary complaints in the exact words of the patient without clinical interpretations...",
                minLines = 6,
                testTag = "input_chronic_chief_complaint",
                isDark = isDark
            )
        }
    }
}

@Composable
fun ChronicOdpStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "ONSET",
                value = formData.onset,
                onValueChange = { onUpdateForm(formData.copy(onset = it)) },
                placeholder = "When did the complaint start? Was it insidious, gradual, or sudden after a specific event?",
                minLines = 2,
                testTag = "input_odp_onset",
                isDark = isDark
            )

            ClinicalTextField(
                label = "DURATION",
                value = formData.duration,
                onValueChange = { onUpdateForm(formData.copy(duration = it)) },
                placeholder = "e.g. 6 months, 3 years, recurring every winter...",
                testTag = "input_odp_duration",
                isDark = isDark
            )

            ClinicalTextField(
                label = "PROGRESS",
                value = formData.progress,
                onValueChange = { onUpdateForm(formData.copy(progress = it)) },
                placeholder = "How has the disease evolved over time? Stable, progressively worsening, seasonal fluctuation?",
                minLines = 3,
                testTag = "input_odp_progress",
                isDark = isDark
            )
        }
    }
}

@Composable
fun ChronicHpcStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Narrate the chronological sequence of all episodes, exacerbations, treatments taken, and their impact on the patient's general constitution.",
                fontSize = 12.sp,
                color = subtextColor
            )
            ClinicalTextField(
                label = "HISTORY OF PRESENTING COMPLAINT (CHRONIC) *",
                value = formData.hpcNarrative,
                onValueChange = { onUpdateForm(formData.copy(hpcNarrative = it)) },
                placeholder = "Comprehensive narrative of presenting complaint...",
                minLines = 8,
                testTag = "input_chronic_hpc",
                isDark = isDark
            )
        }
    }
}

@Composable
fun PastHistoryTableStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val horizontalScrollState = rememberScrollState()

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "PAST MEDICAL & SURGICAL HISTORY",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) SageGreenLight else SageGreenPrimary
            )
            Text(
                text = "Record past childhood diseases, eruptions, suppressions, surgeries, hospitalizations, and treatments (Allopathic, Ayurvedic, etc.).",
                fontSize = 12.sp,
                color = subtextColor
            )

            // 4-Column Table: No, Disease, Duration, Treatment
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
                        .background(if (isDark) Color(0x335B7E68) else Color(0x205B7E68))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = "DISEASE / ILLNESS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(200.dp)
                    )
                    Text(
                        text = "DURATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(150.dp)
                    )
                    Text(
                        text = "TREATMENT TAKEN",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(200.dp)
                    )
                    Spacer(modifier = Modifier.width(44.dp))
                }

                // Table Rows
                formData.pastHistoryRows.forEachIndexed { index, row ->
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
                            modifier = Modifier.width(36.dp)
                        )

                        TableInputCell(
                            value = row.disease,
                            onValueChange = { newVal ->
                                val updated = formData.pastHistoryRows.toMutableList()
                                updated[index] = row.copy(disease = newVal)
                                onUpdateForm(formData.copy(pastHistoryRows = updated))
                            },
                            placeholder = "e.g. Typhoid, Measles, Eczema",
                            modifier = Modifier.width(200.dp),
                            isDark = isDark,
                            testTag = "input_past_disease_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TableInputCell(
                            value = row.duration,
                            onValueChange = { newVal ->
                                val updated = formData.pastHistoryRows.toMutableList()
                                updated[index] = row.copy(duration = newVal)
                                onUpdateForm(formData.copy(pastHistoryRows = updated))
                            },
                            placeholder = "e.g. 3 weeks, at age 12",
                            modifier = Modifier.width(150.dp),
                            isDark = isDark,
                            testTag = "input_past_duration_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TableInputCell(
                            value = row.treatment,
                            onValueChange = { newVal ->
                                val updated = formData.pastHistoryRows.toMutableList()
                                updated[index] = row.copy(treatment = newVal)
                                onUpdateForm(formData.copy(pastHistoryRows = updated))
                            },
                            placeholder = "e.g. Antibiotics, Steroid cream",
                            modifier = Modifier.width(200.dp),
                            isDark = isDark,
                            testTag = "input_past_treatment_$index"
                        )

                        IconButton(
                            onClick = {
                                if (formData.pastHistoryRows.size > 1) {
                                    val updated = formData.pastHistoryRows.toMutableList()
                                    updated.removeAt(index)
                                    onUpdateForm(formData.copy(pastHistoryRows = updated))
                                } else {
                                    onUpdateForm(formData.copy(pastHistoryRows = listOf(PastHistoryRow(no = "1"))))
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete row", tint = subtextColor, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val updated = formData.pastHistoryRows.toMutableList()
                    updated.add(PastHistoryRow(no = "${updated.size + 1}"))
                    onUpdateForm(formData.copy(pastHistoryRows = updated))
                },
                colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Past History Row", fontSize = 12.5.sp)
            }
        }
    }
}

@Composable
fun FamilyHistoryTableStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder
    val horizontalScrollState = rememberScrollState()

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text(
                text = "FAMILY HISTORY TABLE",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) SageGreenLight else SageGreenPrimary
            )
            Text(
                text = "Trace hereditary diathesis and familial tendencies: Diabetes, Hypertension, Cancer, Tuberculosis, Asthma, Psychiatric disorders.",
                fontSize = 12.sp,
                color = subtextColor
            )

            // 4-Column Table: No, Relationship, Health/Illness, Alive/Dead
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
                        .background(if (isDark) Color(0x335B7E68) else Color(0x205B7E68))
                        .padding(horizontal = 8.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.width(36.dp)
                    )
                    Text(
                        text = "RELATIONSHIP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(180.dp)
                    )
                    Text(
                        text = "HEALTH / ILLNESS DETAILS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(230.dp)
                    )
                    Text(
                        text = "ALIVE / DEAD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) SageGreenLight else SageGreenPrimary,
                        modifier = Modifier.width(140.dp)
                    )
                    Spacer(modifier = Modifier.width(44.dp))
                }

                // Table Rows
                formData.familyHistoryRows.forEachIndexed { index, row ->
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
                            modifier = Modifier.width(36.dp)
                        )

                        TableInputCell(
                            value = row.relationship,
                            onValueChange = { newVal ->
                                val updated = formData.familyHistoryRows.toMutableList()
                                updated[index] = row.copy(relationship = newVal)
                                onUpdateForm(formData.copy(familyHistoryRows = updated))
                            },
                            placeholder = "e.g. Father, Mother, Paternal Uncle",
                            modifier = Modifier.width(180.dp),
                            isDark = isDark,
                            testTag = "input_family_rel_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TableInputCell(
                            value = row.healthIllness,
                            onValueChange = { newVal ->
                                val updated = formData.familyHistoryRows.toMutableList()
                                updated[index] = row.copy(healthIllness = newVal)
                                onUpdateForm(formData.copy(familyHistoryRows = updated))
                            },
                            placeholder = "e.g. Type 2 DM, Bronchial Asthma",
                            modifier = Modifier.width(230.dp),
                            isDark = isDark,
                            testTag = "input_family_illness_$index"
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        TableInputCell(
                            value = row.aliveDead,
                            onValueChange = { newVal ->
                                val updated = formData.familyHistoryRows.toMutableList()
                                updated[index] = row.copy(aliveDead = newVal)
                                onUpdateForm(formData.copy(familyHistoryRows = updated))
                            },
                            placeholder = "e.g. Alive (Age 68) / Died of MI",
                            modifier = Modifier.width(140.dp),
                            isDark = isDark,
                            testTag = "input_family_status_$index"
                        )

                        IconButton(
                            onClick = {
                                if (formData.familyHistoryRows.size > 1) {
                                    val updated = formData.familyHistoryRows.toMutableList()
                                    updated.removeAt(index)
                                    onUpdateForm(formData.copy(familyHistoryRows = updated))
                                } else {
                                    onUpdateForm(formData.copy(familyHistoryRows = listOf(FamilyHistoryRow(no = "1"))))
                                }
                            },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete row", tint = subtextColor, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val updated = formData.familyHistoryRows.toMutableList()
                    updated.add(FamilyHistoryRow(no = "${updated.size + 1}"))
                    onUpdateForm(formData.copy(familyHistoryRows = updated))
                },
                colors = ButtonDefaults.buttonColors(containerColor = SageGreenPrimary),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Family History Row", fontSize = 12.5.sp)
            }
        }
    }
}
