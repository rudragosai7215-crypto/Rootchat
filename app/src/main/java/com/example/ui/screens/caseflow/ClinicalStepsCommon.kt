package com.example.ui.screens.caseflow

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ClinicalFormData
import com.example.data.SymptomAnalysisItem
import com.example.data.SymptomEvaluationItem
import com.example.ui.theme.CreamIvoryMuted
import com.example.ui.theme.CreamIvoryText
import com.example.ui.theme.DarkBrownMuted
import com.example.ui.theme.DarkBrownText
import com.example.ui.theme.DarkGlassBorder
import com.example.ui.theme.GlassPanel
import com.example.ui.theme.LightGlassBorder
import com.example.ui.theme.SageGreenPrimary
import com.example.ui.theme.TerracottaLight
import com.example.ui.theme.TerracottaPrimary

// -------------------------------------------------------------------------------------------------
// Step 1: Case Identification
// -------------------------------------------------------------------------------------------------
@Composable
fun CaseIdentificationStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val maritalOptions = listOf("Single", "Married", "Widowed", "Divorced", "Child")

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "SR. NO.",
                    value = formData.srNo,
                    onValueChange = { onUpdateForm(formData.copy(srNo = it)) },
                    placeholder = "e.g. 101",
                    testTag = "input_sr_no",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "O.P.D. CASE NO.",
                    value = formData.opdCaseNo,
                    onValueChange = { onUpdateForm(formData.copy(opdCaseNo = it)) },
                    placeholder = "e.g. OPD-2026/045",
                    testTag = "input_opd_no",
                    isDark = isDark,
                    modifier = Modifier.weight(1.3f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "PATIENT NAME *",
                    value = formData.name,
                    onValueChange = { onUpdateForm(formData.copy(name = it)) },
                    placeholder = "Full Name",
                    testTag = "input_patient_name",
                    isDark = isDark,
                    modifier = Modifier.weight(1.8f)
                )
                ClinicalTextField(
                    label = "DATE",
                    value = formData.date,
                    onValueChange = { onUpdateForm(formData.copy(date = it)) },
                    placeholder = "YYYY-MM-DD",
                    testTag = "input_case_date",
                    isDark = isDark,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "AGE",
                    value = formData.age,
                    onValueChange = { onUpdateForm(formData.copy(age = it)) },
                    placeholder = "e.g. 34 yrs",
                    testTag = "input_patient_age",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "SEX",
                    value = formData.sex,
                    onValueChange = { onUpdateForm(formData.copy(sex = it)) },
                    placeholder = "Male / Female / Other",
                    testTag = "input_patient_gender",
                    isDark = isDark,
                    modifier = Modifier.weight(1.2f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "QUALIFICATION",
                    value = formData.qualification,
                    onValueChange = { onUpdateForm(formData.copy(qualification = it)) },
                    placeholder = "e.g. Graduate, M.Sc.",
                    testTag = "input_qualification",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "RELIGION",
                    value = formData.religion,
                    onValueChange = { onUpdateForm(formData.copy(religion = it)) },
                    placeholder = "e.g. Hindu / Christian / Muslim",
                    testTag = "input_religion",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "OCCUPATION",
                    value = formData.occupation,
                    onValueChange = { onUpdateForm(formData.copy(occupation = it)) },
                    placeholder = "e.g. Software Engineer, Farmer",
                    testTag = "input_patient_occupation",
                    isDark = isDark,
                    modifier = Modifier.weight(1.2f)
                )
                ClinicalDropdown(
                    label = "MARITAL STATUS",
                    selectedValue = formData.maritalStatus,
                    options = maritalOptions,
                    onSelect = { onUpdateForm(formData.copy(maritalStatus = it)) },
                    isDark = isDark,
                    modifier = Modifier.weight(1f),
                    testTag = "dropdown_marital_status"
                )
            }

            ClinicalTextField(
                label = "RESIDENTIAL ADDRESS",
                value = formData.address,
                onValueChange = { onUpdateForm(formData.copy(address = it)) },
                placeholder = "Full Address & Contact details",
                testTag = "input_patient_contact",
                isDark = isDark,
                minLines = 2
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Associated Complaints
// -------------------------------------------------------------------------------------------------
@Composable
fun AssociatedComplaintsStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Record any co-existing secondary complaints, side effects, or minor symptoms that accompany the primary disorder.",
                fontSize = 12.sp,
                color = subtextColor
            )
            ClinicalTextField(
                label = "ASSOCIATED COMPLAINTS (IF ANY)",
                value = formData.associatedComplaints,
                onValueChange = { onUpdateForm(formData.copy(associatedComplaints = it)) },
                placeholder = "e.g. Mild lower back stiffness in morning, dry cough on lying down...",
                minLines = 6,
                testTag = "input_associated_complaints",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Personal History (15 individual fields)
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PersonalHistoryStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val thermals = listOf("Chilly", "Hot", "Ambithermal")
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "INDIVIDUAL PERSONAL CHARACTERISTICS & GENERALS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            // Appetite & Thirst
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "APPETITE",
                    value = formData.appetite,
                    onValueChange = { onUpdateForm(formData.copy(appetite = it)) },
                    placeholder = "e.g. Ravenous, easy satiety, loss of appetite",
                    testTag = "input_ph_appetite",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "THIRST",
                    value = formData.thirst,
                    onValueChange = { onUpdateForm(formData.copy(thirst = it)) },
                    placeholder = "e.g. Thirstless, sips often, large cold drinks",
                    testTag = "input_ph_thirst",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // Desire & Aversion
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "DESIRE (CRAVINGS)",
                    value = formData.desire,
                    onValueChange = { onUpdateForm(formData.copy(desire = it)) },
                    placeholder = "e.g. Sweets, salt, pungent, spicy, eggs",
                    testTag = "input_ph_desire",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "AVERSION",
                    value = formData.aversion,
                    onValueChange = { onUpdateForm(formData.copy(aversion = it)) },
                    placeholder = "e.g. Milk, fat/meat, bread, warm food",
                    testTag = "input_ph_aversion",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // Urine & Bowel
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "URINE",
                    value = formData.urine,
                    onValueChange = { onUpdateForm(formData.copy(urine = it)) },
                    placeholder = "e.g. Clear, offensive, burning, nocturnal frequency",
                    testTag = "input_ph_urine",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "BOWEL (STOOL)",
                    value = formData.bowel,
                    onValueChange = { onUpdateForm(formData.copy(bowel = it)) },
                    placeholder = "e.g. Constipated hard dry, ineffectual urging",
                    testTag = "input_ph_bowel",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // Perspiration
            ClinicalTextField(
                label = "PERSPIRATION",
                value = formData.perspiration,
                onValueChange = { onUpdateForm(formData.copy(perspiration = it)) },
                placeholder = "e.g. Profuse head/chest, staining yellow, offensive foot sweat",
                testTag = "input_ph_perspiration",
                isDark = isDark
            )

            // Sleep & Dream
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "SLEEP",
                    value = formData.sleep,
                    onValueChange = { onUpdateForm(formData.copy(sleep = it)) },
                    placeholder = "e.g. Insomnia 2-4 AM, restless, starts on falling asleep",
                    testTag = "input_ph_sleep",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "DREAM",
                    value = formData.dream,
                    onValueChange = { onUpdateForm(formData.copy(dream = it)) },
                    placeholder = "e.g. Falling, snakes, anxious business, ghosts",
                    testTag = "input_ph_dream",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // Thermal State (with Quick Selector Chips)
            Column {
                Text(
                    text = "THERMAL STATE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    color = if (isDark) TerracottaLight else TerracottaPrimary,
                    fontFamily = FontFamily.Serif
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    thermals.forEach { state ->
                        val isSelected = formData.thermalState.equals(state, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000)))
                                .clickable { onUpdateForm(formData.copy(thermalState = state)) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = state,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                softWrap = false,
                                color = if (isSelected) Color.White else textColor
                            )
                        }
                    }
                }
            }

            // Tendency to
            ClinicalTextField(
                label = "TENDENCY TO",
                value = formData.tendencyTo,
                onValueChange = { onUpdateForm(formData.copy(tendencyTo = it)) },
                placeholder = "e.g. Catch cold easily, suppuration, hemorrhage, skin eruptions",
                testTag = "input_ph_tendency",
                isDark = isDark
            )

            // Addiction & Allergy
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "ADDICTION",
                    value = formData.addiction,
                    onValueChange = { onUpdateForm(formData.copy(addiction = it)) },
                    placeholder = "e.g. Tobacco, alcohol, tea, none",
                    testTag = "input_ph_addiction",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "ALLERGY",
                    value = formData.allergy,
                    onValueChange = { onUpdateForm(formData.copy(allergy = it)) },
                    placeholder = "e.g. Dust, pollen, NSAIDs, sulfur",
                    testTag = "input_ph_allergy",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            // Milestones & Vaccinations
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "MILESTONES",
                    value = formData.milestones,
                    onValueChange = { onUpdateForm(formData.copy(milestones = it)) },
                    placeholder = "e.g. Normal walking/dentition / Delayed",
                    testTag = "input_ph_milestones",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "VACCINATIONS",
                    value = formData.vaccinations,
                    onValueChange = { onUpdateForm(formData.copy(vaccinations = it)) },
                    placeholder = "e.g. Fully vaccinated, bad effects of vaccination",
                    testTag = "input_ph_vaccinations",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Vital Examination
// -------------------------------------------------------------------------------------------------
@Composable
fun VitalExaminationStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "CLINICAL VITAL SIGNS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "TEMPERATURE",
                    value = formData.temperature,
                    onValueChange = { onUpdateForm(formData.copy(temperature = it)) },
                    placeholder = "e.g. 98.6 °F",
                    testTag = "input_vital_temp",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "PULSE",
                    value = formData.pulse,
                    onValueChange = { onUpdateForm(formData.copy(pulse = it)) },
                    placeholder = "e.g. 78 bpm, full / thready",
                    testTag = "input_vital_pulse",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                ClinicalTextField(
                    label = "R.R. (RESPIRATORY RATE)",
                    value = formData.rr,
                    onValueChange = { onUpdateForm(formData.copy(rr = it)) },
                    placeholder = "e.g. 18 /min",
                    testTag = "input_vital_rr",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "B.P. (BLOOD PRESSURE)",
                    value = formData.bp,
                    onValueChange = { onUpdateForm(formData.copy(bp = it)) },
                    placeholder = "e.g. 120/80 mmHg",
                    testTag = "input_vital_bp",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Menstrual History (if applicable)
// -------------------------------------------------------------------------------------------------
@Composable
fun MenstrualHistoryStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val tableBorderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Header with toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "MENSTRUAL HISTORY (IF APPLICABLE)",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Text(
                        text = if (formData.menstrualApplicable) "Active / Completed" else "Skipped / Not applicable for this patient",
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }

                Switch(
                    checked = formData.menstrualApplicable,
                    onCheckedChange = { onUpdateForm(formData.copy(menstrualApplicable = it)) },
                    colors = SwitchDefaults.colors(checkedTrackColor = TerracottaPrimary),
                    modifier = Modifier.testTag("switch_menstrual_applicable")
                )
            }

            AnimatedVisibility(visible = formData.menstrualApplicable) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClinicalTextField(
                            label = "MENARCHE",
                            value = formData.menarche,
                            onValueChange = { onUpdateForm(formData.copy(menarche = it)) },
                            placeholder = "e.g. At 13 years",
                            testTag = "input_menarche",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        ClinicalTextField(
                            label = "L.M.P. (LAST MENSTRUAL PERIOD)",
                            value = formData.lmp,
                            onValueChange = { onUpdateForm(formData.copy(lmp = it)) },
                            placeholder = "e.g. 15th of last month",
                            testTag = "input_lmp",
                            isDark = isDark,
                            modifier = Modifier.weight(1.3f)
                        )
                    }

                    // Cycle Toggle (Regular / Irregular) & Duration
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "CYCLE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = if (isDark) TerracottaLight else TerracottaPrimary,
                                fontFamily = FontFamily.Serif
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (formData.cycleRegular) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000)))
                                        .clickable { onUpdateForm(formData.copy(cycleRegular = true)) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Regular",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (formData.cycleRegular) Color.White else textColor
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (!formData.cycleRegular) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000)))
                                        .clickable { onUpdateForm(formData.copy(cycleRegular = false)) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "Irregular",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        softWrap = false,
                                        color = if (!formData.cycleRegular) Color.White else textColor
                                    )
                                }
                            }
                        }

                        ClinicalTextField(
                            label = "DURATION",
                            value = formData.menstrualDuration,
                            onValueChange = { onUpdateForm(formData.copy(menstrualDuration = it)) },
                            placeholder = "e.g. 4-5 days / 28 days cycle",
                            testTag = "input_menstrual_duration",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Character, Flow & Quantity
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClinicalTextField(
                            label = "CHARACTER OF MENSES",
                            value = formData.characterOfMenses,
                            onValueChange = { onUpdateForm(formData.copy(characterOfMenses = it)) },
                            placeholder = "e.g. Clotted, stringy, acrid, dark",
                            testTag = "input_menstrual_character",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        ClinicalTextField(
                            label = "FLOW & QUANTITY",
                            value = formData.flowAndQuantity,
                            onValueChange = { onUpdateForm(formData.copy(flowAndQuantity = it)) },
                            placeholder = "e.g. Scanty, profuse, night only",
                            testTag = "input_menstrual_flow",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Colour & Odour
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ClinicalTextField(
                            label = "COLOUR",
                            value = formData.menstrualColour,
                            onValueChange = { onUpdateForm(formData.copy(menstrualColour = it)) },
                            placeholder = "e.g. Bright red, pitch black, pale",
                            testTag = "input_menstrual_colour",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        ClinicalTextField(
                            label = "ODOUR",
                            value = formData.menstrualOdour,
                            onValueChange = { onUpdateForm(formData.copy(menstrualOdour = it)) },
                            placeholder = "e.g. Offensive, pungent, odorless",
                            testTag = "input_menstrual_odour",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // 4-Column Concomitants Table: Before, Beginning, During, After menses
                    Text(
                        text = "4-COLUMN CONCOMITANTS TABLE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        fontFamily = FontFamily.Serif
                    )

                    val scroll = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(scroll)
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, tableBorderColor, RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .background(if (isDark) Color(0x33B5502F) else Color(0x20B5502F))
                                .padding(horizontal = 8.dp, vertical = 8.dp)
                        ) {
                            Text("BEFORE MENSES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.width(170.dp))
                            Text("BEGINNING OF MENSES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.width(170.dp))
                            Text("DURING MENSES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.width(170.dp))
                            Text("AFTER MENSES", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = textColor, modifier = Modifier.width(170.dp))
                        }
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                            TableInputCell(
                                value = formData.concomitantBefore,
                                onValueChange = { onUpdateForm(formData.copy(concomitantBefore = it)) },
                                placeholder = "e.g. Sadness, headache",
                                modifier = Modifier.width(170.dp),
                                isDark = isDark,
                                testTag = "input_concom_before"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TableInputCell(
                                value = formData.concomitantBeginning,
                                onValueChange = { onUpdateForm(formData.copy(concomitantBeginning = it)) },
                                placeholder = "e.g. Severe cramps, vomiting",
                                modifier = Modifier.width(170.dp),
                                isDark = isDark,
                                testTag = "input_concom_begin"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TableInputCell(
                                value = formData.concomitantDuring,
                                onValueChange = { onUpdateForm(formData.copy(concomitantDuring = it)) },
                                placeholder = "e.g. Diarrhea, weeping",
                                modifier = Modifier.width(170.dp),
                                isDark = isDark,
                                testTag = "input_concom_during"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            TableInputCell(
                                value = formData.concomitantAfter,
                                onValueChange = { onUpdateForm(formData.copy(concomitantAfter = it)) },
                                placeholder = "e.g. Extreme prostration",
                                modifier = Modifier.width(170.dp),
                                isDark = isDark,
                                testTag = "input_concom_after"
                            )
                        }
                    }

                    // Leucorrhoea
                    Text(
                        text = "LEUCORRHOEA DETAILS",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                        fontFamily = FontFamily.Serif
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ClinicalTextField(
                            label = "CHARACTER",
                            value = formData.leucorrhoeaCharacter,
                            onValueChange = { onUpdateForm(formData.copy(leucorrhoeaCharacter = it)) },
                            placeholder = "e.g. Bland, acrid, albuminous",
                            testTag = "input_leuco_char",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        ClinicalTextField(
                            label = "OCCURRENCE",
                            value = formData.leucorrhoeaOccurrence,
                            onValueChange = { onUpdateForm(formData.copy(leucorrhoeaOccurrence = it)) },
                            placeholder = "e.g. Mid-cycle, before menses",
                            testTag = "input_leuco_occ",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                        ClinicalTextField(
                            label = "PAIN",
                            value = formData.leucorrhoeaPain,
                            onValueChange = { onUpdateForm(formData.copy(leucorrhoeaPain = it)) },
                            placeholder = "e.g. Sacral aching",
                            testTag = "input_leuco_pain",
                            isDark = isDark,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Obstetric History (if applicable)
// -------------------------------------------------------------------------------------------------
@Composable
fun ObstetricHistoryStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PATIENT'S OBSTETRIC HISTORY (IF APPLICABLE)",
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp,
                        letterSpacing = 1.4.sp,
                        color = if (isDark) TerracottaLight else TerracottaPrimary
                    )
                    Text(
                        text = if (formData.obstetricApplicable) "Active / Recorded" else "Skipped / Not applicable for this patient",
                        fontSize = 11.sp,
                        color = subtextColor
                    )
                }

                Switch(
                    checked = formData.obstetricApplicable,
                    onCheckedChange = { onUpdateForm(formData.copy(obstetricApplicable = it)) },
                    colors = SwitchDefaults.colors(checkedTrackColor = TerracottaPrimary),
                    modifier = Modifier.testTag("switch_obstetric_applicable")
                )
            }

            AnimatedVisibility(visible = formData.obstetricApplicable) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    ClinicalTextField(
                        label = "G P A L (GRAVIDA, PARA, ABORTIONS, LIVING)",
                        value = formData.gpal,
                        onValueChange = { onUpdateForm(formData.copy(gpal = it)) },
                        placeholder = "e.g. G2 P1 A1 L1 — Full term normal delivery in 2022; 1 spontaneous abortion at 8 weeks in 2024",
                        minLines = 3,
                        testTag = "input_gpal",
                        isDark = isDark
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// General Physical Examination (10 fields)
// -------------------------------------------------------------------------------------------------
@Composable
fun GeneralPhysicalExamStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "GENERAL PHYSICAL EXAMINATION (G.P.E.)",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClinicalTextField(
                    label = "CONSCIOUSNESS",
                    value = formData.consciousness,
                    onValueChange = { onUpdateForm(formData.copy(consciousness = it)) },
                    placeholder = "e.g. Alert, oriented, drowsy",
                    testTag = "input_gpe_consciousness",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "BUILT",
                    value = formData.built,
                    onValueChange = { onUpdateForm(formData.copy(built = it)) },
                    placeholder = "e.g. Moderate, emaciated, obese",
                    testTag = "input_gpe_built",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClinicalTextField(
                    label = "HEIGHT",
                    value = formData.height,
                    onValueChange = { onUpdateForm(formData.copy(height = it)) },
                    placeholder = "e.g. 172 cm",
                    testTag = "input_gpe_height",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "WEIGHT",
                    value = formData.weight,
                    onValueChange = { onUpdateForm(formData.copy(weight = it)) },
                    placeholder = "e.g. 68 kg",
                    testTag = "input_gpe_weight",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClinicalTextField(
                    label = "SKIN",
                    value = formData.skin,
                    onValueChange = { onUpdateForm(formData.copy(skin = it)) },
                    placeholder = "e.g. Dry, moist, pale, eruptions",
                    testTag = "input_gpe_skin",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "CONJUNCTIVA",
                    value = formData.conjunctiva,
                    onValueChange = { onUpdateForm(formData.copy(conjunctiva = it)) },
                    placeholder = "e.g. Pink, pale (pallor), icteric",
                    testTag = "input_gpe_conjunctiva",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClinicalTextField(
                    label = "NAILS",
                    value = formData.nails,
                    onValueChange = { onUpdateForm(formData.copy(nails = it)) },
                    placeholder = "e.g. Clubbing, koilonychia, brittle",
                    testTag = "input_gpe_nails",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "TONGUE",
                    value = formData.tongue,
                    onValueChange = { onUpdateForm(formData.copy(tongue = it)) },
                    placeholder = "e.g. White coated, mapped, strawberry, dry red",
                    testTag = "input_gpe_tongue",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ClinicalTextField(
                    label = "TEETH",
                    value = formData.teeth,
                    onValueChange = { onUpdateForm(formData.copy(teeth = it)) },
                    placeholder = "e.g. Intact, caries, sordes, bleeding gums",
                    testTag = "input_gpe_teeth",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
                ClinicalTextField(
                    label = "LYMPH NODES",
                    value = formData.lymphNodes,
                    onValueChange = { onUpdateForm(formData.copy(lymphNodes = it)) },
                    placeholder = "e.g. Non-palpable / Cervical enlarged",
                    testTag = "input_gpe_lymph",
                    isDark = isDark,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Systemic Examination (4 systems)
// -------------------------------------------------------------------------------------------------
@Composable
fun SystemicExamStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = "SYSTEMIC EXAMINATION",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            ClinicalTextField(
                label = "RESPIRATORY SYSTEM (R.S.)",
                value = formData.respiratorySystem,
                onValueChange = { onUpdateForm(formData.copy(respiratorySystem = it)) },
                placeholder = "e.g. Vesicular breath sounds bilaterally, wheeze / rhonchi in right base...",
                minLines = 2,
                testTag = "input_sys_rs",
                isDark = isDark
            )

            ClinicalTextField(
                label = "CARDIO VASCULAR SYSTEM (C.V.S.)",
                value = formData.cardiovascularSystem,
                onValueChange = { onUpdateForm(formData.copy(cardiovascularSystem = it)) },
                placeholder = "e.g. S1 S2 heard, no murmurs / gallop...",
                minLines = 2,
                testTag = "input_sys_cvs",
                isDark = isDark
            )

            ClinicalTextField(
                label = "GASTRO INTESTINAL SYSTEM (G.I.S.)",
                value = formData.gastrointestinalSystem,
                onValueChange = { onUpdateForm(formData.copy(gastrointestinalSystem = it)) },
                placeholder = "e.g. Soft, non-tender, no hepatosplenomegaly, normal bowel sounds...",
                minLines = 2,
                testTag = "input_sys_gis",
                isDark = isDark
            )

            ClinicalTextField(
                label = "CENTRAL NERVOUS SYSTEM (C.N.S.)",
                value = formData.centralNervousSystem,
                onValueChange = { onUpdateForm(formData.copy(centralNervousSystem = it)) },
                placeholder = "e.g. Higher functions intact, cranial nerves normal, reflexes 2+...",
                minLines = 2,
                testTag = "input_sys_cns",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Investigation (4 labeled sections)
// -------------------------------------------------------------------------------------------------
@Composable
fun InvestigationStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "CLINICAL INVESTIGATIONS & LABORATORY FINDINGS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )

            ClinicalTextField(
                label = "BLOOD INVESTIGATIONS",
                value = formData.investigationBlood,
                onValueChange = { onUpdateForm(formData.copy(investigationBlood = it)) },
                placeholder = "e.g. Hb: 12.4 gm%, TLC: 8,400, ESR: 18 mm/hr, Fasting Blood Sugar: 94 mg/dl...",
                minLines = 3,
                testTag = "input_inv_blood",
                isDark = isDark
            )

            ClinicalTextField(
                label = "URINE INVESTIGATIONS",
                value = formData.investigationUrine,
                onValueChange = { onUpdateForm(formData.copy(investigationUrine = it)) },
                placeholder = "e.g. Routine: Albumin nil, Sugar nil, Pus cells 1-2 / hpf...",
                minLines = 2,
                testTag = "input_inv_urine",
                isDark = isDark
            )

            ClinicalTextField(
                label = "RADIOLOGY (X-RAY / USG / CT / MRI)",
                value = formData.investigationRadiology,
                onValueChange = { onUpdateForm(formData.copy(investigationRadiology = it)) },
                placeholder = "e.g. Chest X-Ray PA: Clear lung fields, USG Abdomen: Normal liver...",
                minLines = 3,
                testTag = "input_inv_radiology",
                isDark = isDark
            )

            ClinicalTextField(
                label = "OTHER INVESTIGATIONS (ECG, BIOPSY, ALLERGY TESTS)",
                value = formData.investigationOther,
                onValueChange = { onUpdateForm(formData.copy(investigationOther = it)) },
                placeholder = "e.g. 12-lead ECG normal sinus rhythm...",
                minLines = 2,
                testTag = "input_inv_other",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Diagnosis Steps
// -------------------------------------------------------------------------------------------------
@Composable
fun ProvisionalDiagnosisStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "PROVISIONAL CLINICAL DIAGNOSIS *",
                value = formData.provisionalDiagnosis,
                onValueChange = { onUpdateForm(formData.copy(provisionalDiagnosis = it)) },
                placeholder = "e.g. Acute Allergic Rhinitis / Acute Bronchitis / Enteric Fever...",
                minLines = 4,
                testTag = "input_provisional_diagnosis",
                isDark = isDark
            )
        }
    }
}

@Composable
fun FinalDiagnosisStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalTextField(
                label = "FINAL CLINICAL DIAGNOSIS (WITH NOSOLOGICAL CODE) *",
                value = formData.finalDiagnosis,
                onValueChange = { onUpdateForm(formData.copy(finalDiagnosis = it)) },
                placeholder = "e.g. Chronic Bronchial Asthma / Rheumatoid Arthritis...",
                minLines = 4,
                testTag = "input_final_diagnosis",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Observation Step (Clinician's observation)
// -------------------------------------------------------------------------------------------------
@Composable
fun ObservationStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "Record the clinician's own objective observations of the patient's demeanor, body language, facial micro-expressions, posture, speech tempo, tone, emotional expression, and manner of relating during the consultation (distinct from what the patient verbally reports).",
                fontSize = 12.sp,
                color = subtextColor
            )

            ClinicalTextField(
                label = "CLINICIAN'S CLINICAL OBSERVATION *",
                value = formData.clinicianObservation,
                onValueChange = { onUpdateForm(formData.copy(clinicianObservation = it)) },
                placeholder = "e.g. Patient sits on the very edge of the chair, biting nails nervously. Rapid anxious speech, avoids eye contact when discussing family. Extremely orderly clothing with neat pocket handkerchief...",
                minLines = 8,
                testTag = "input_clinician_observation",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Mental & Life Span (Patient's account)
// -------------------------------------------------------------------------------------------------
@Composable
fun MentalLifeSpanStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean,
    isAcute: Boolean
) {
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = if (isAcute) {
                    "Patient's own account of their acute mental state, fears, anxieties, mood alterations, and psychological reactions during this illness."
                } else {
                    "Comprehensive account of patient's mental disposition, childhood environment, grief, mortification, life milestones, personality temperament, and interpersonal relationships."
                },
                fontSize = 12.sp,
                color = subtextColor
            )

            ClinicalTextField(
                label = if (isAcute) "ACUTE MENTAL STATE & PERSONALITY *" else "MENTAL & LIFE SPAN NARRATIVE *",
                value = formData.mentalLifeSpan,
                onValueChange = { onUpdateForm(formData.copy(mentalLifeSpan = it)) },
                placeholder = "Enter patient's subjective psychological state in their own words...",
                minLines = 9,
                testTag = "input_mental_life_span",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Miasmatic Evaluation (Chronic)
// -------------------------------------------------------------------------------------------------
@Composable
fun MiasmaticEvaluationStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val miasms = listOf("Psoric", "Sycotic", "Syphilitic", "Tubercular", "Mixed")

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            ClinicalDropdown(
                label = "PREDOMINANT MIASM *",
                selectedValue = formData.predominantMiasm,
                options = miasms,
                onSelect = { onUpdateForm(formData.copy(predominantMiasm = it)) },
                isDark = isDark,
                testTag = "dropdown_predominant_miasm"
            )

            ClinicalTextField(
                label = "MIASMATIC REASONING & CLINICAL ANALYSIS NOTES",
                value = formData.miasmaticNotes,
                onValueChange = { onUpdateForm(formData.copy(miasmaticNotes = it)) },
                placeholder = "Detail the clinical justification: Psoric functional disturbances and itch, Sycotic overgrowth/incoordination, Syphilitic destructive degeneration, or Tubercular rapid wasting...",
                minLines = 6,
                testTag = "input_miasmatic_notes",
                isDark = isDark
            )
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Analysis of Symptoms
// -------------------------------------------------------------------------------------------------
@Composable
fun AnalysisOfSymptomsStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val types = listOf("Mental General", "Mental Particular", "Physical General", "Physical Particular")
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val borderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "ANALYSIS OF SYMPTOMS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Text(
                text = "Classify each extracted symptom into Hahnemannian hierarchy: Type (Mental General / Particular, Physical General / Particular) and Frequency (Common / Uncommon).",
                fontSize = 12.sp,
                color = subtextColor
            )

            if (formData.symptomAnalysisList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No symptoms analyzed yet. Tap '+ Add Symptom' below.", fontSize = 12.sp, color = subtextColor)
                }
            } else {
                formData.symptomAnalysisList.forEachIndexed { index, item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x15FFFFFF) else Color(0x0A000000))
                            .padding(12.dp),
                        color = Color.Transparent
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "SYMPTOM #${index + 1}",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary
                                )
                                IconButton(
                                    onClick = {
                                        val updated = formData.symptomAnalysisList.toMutableList()
                                        updated.removeAt(index)
                                        onUpdateForm(formData.copy(symptomAnalysisList = updated))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove symptom", tint = subtextColor, modifier = Modifier.size(16.dp))
                                }
                            }

                            ClinicalTextField(
                                label = "SYMPTOM DESCRIPTION",
                                value = item.symptomText,
                                onValueChange = { newVal ->
                                    val updated = formData.symptomAnalysisList.toMutableList()
                                    updated[index] = item.copy(symptomText = newVal)
                                    onUpdateForm(formData.copy(symptomAnalysisList = updated))
                                },
                                placeholder = "e.g. Extreme thirst for large quantities of ice cold water",
                                testTag = "input_analysis_text_$index",
                                isDark = isDark
                            )

                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                ClinicalDropdown(
                                    label = "SYMPTOM TYPE",
                                    selectedValue = item.type,
                                    options = types,
                                    onSelect = { newType ->
                                        val updated = formData.symptomAnalysisList.toMutableList()
                                        updated[index] = item.copy(type = newType)
                                        onUpdateForm(formData.copy(symptomAnalysisList = updated))
                                    },
                                    isDark = isDark,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "dropdown_analysis_type_$index"
                                )

                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "FREQUENCY",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        listOf("Common", "Uncommon").forEach { freq ->
                                            val isSelected = item.frequency.equals(freq, ignoreCase = true)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(if (isSelected) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000)))
                                                    .clickable {
                                                        val updated = formData.symptomAnalysisList.toMutableList()
                                                        updated[index] = item.copy(frequency = freq)
                                                        onUpdateForm(formData.copy(symptomAnalysisList = updated))
                                                    }
                                                    .padding(vertical = 10.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = freq,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    color = if (isSelected) Color.White else textColor
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val updated = formData.symptomAnalysisList.toMutableList()
                    updated.add(SymptomAnalysisItem(type = "Physical General", frequency = "Common"))
                    onUpdateForm(formData.copy(symptomAnalysisList = updated))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_add_analysis_symptom")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Symptom to Analysis")
            }
        }
    }
}

// -------------------------------------------------------------------------------------------------
// Evaluation of Symptoms
// -------------------------------------------------------------------------------------------------
@Composable
fun EvaluationOfSymptomsStep(
    formData: ClinicalFormData,
    onUpdateForm: (ClinicalFormData) -> Unit,
    isDark: Boolean
) {
    val grades = listOf("+", "++", "+++")
    val textColor = if (isDark) CreamIvoryText else DarkBrownText
    val subtextColor = if (isDark) CreamIvoryMuted else DarkBrownMuted
    val borderColor = if (isDark) DarkGlassBorder else LightGlassBorder

    GlassPanel(modifier = Modifier.fillMaxWidth(), isDark = isDark) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text(
                text = "EVALUATION OF SYMPTOMS",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 11.5.sp,
                letterSpacing = 1.4.sp,
                color = if (isDark) TerracottaLight else TerracottaPrimary
            )
            Text(
                text = "Select from analyzed symptoms (or add new ones) and assign clinical intensity grades (+ / ++ / +++).",
                fontSize = 12.sp,
                color = subtextColor
            )

            // Button to populate from Analysis if Evaluation is currently empty
            if (formData.symptomAnalysisList.isNotEmpty() && formData.symptomEvaluationList.isEmpty()) {
                OutlinedButton(
                    onClick = {
                        val imported = formData.symptomAnalysisList.map {
                            SymptomEvaluationItem(
                                symptomText = it.symptomText,
                                grade = if (it.frequency == "Uncommon") "+++" else "++",
                                type = it.type,
                                frequency = it.frequency
                            )
                        }
                        onUpdateForm(formData.copy(symptomEvaluationList = imported))
                    },
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Auto-populate from Step Analysis Symptoms (${formData.symptomAnalysisList.size})")
                }
            }

            if (formData.symptomEvaluationList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No symptoms evaluated yet.", fontSize = 12.sp, color = subtextColor)
                }
            } else {
                formData.symptomEvaluationList.forEachIndexed { index, item ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                            .background(if (isDark) Color(0x15FFFFFF) else Color(0x0A000000))
                            .padding(12.dp),
                        color = Color.Transparent
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Display badge: "[Symptom] [grade] ([Type], [Common/Uncommon])"
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isDark) Color(0x33B5502F) else Color(0x18B5502F))
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                color = Color.Transparent
                            ) {
                                Text(
                                    text = item.displayLabel(),
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = if (isDark) TerracottaLight else TerracottaPrimary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ClinicalTextField(
                                    label = "SYMPTOM TEXT",
                                    value = item.symptomText,
                                    onValueChange = { newVal ->
                                        val updated = formData.symptomEvaluationList.toMutableList()
                                        updated[index] = item.copy(symptomText = newVal)
                                        onUpdateForm(formData.copy(symptomEvaluationList = updated))
                                    },
                                    placeholder = "Symptom",
                                    testTag = "input_eval_text_$index",
                                    isDark = isDark,
                                    modifier = Modifier.weight(1.8f)
                                )

                                Column(modifier = Modifier.weight(1.2f)) {
                                    Text(
                                        text = "GRADE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.2.sp,
                                        color = if (isDark) TerracottaLight else TerracottaPrimary,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        grades.forEach { g ->
                                            val isSelected = item.grade == g
                                            Box(
                                                modifier = Modifier
                                                    .defaultMinSize(minWidth = 28.dp)
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isSelected) TerracottaPrimary else (if (isDark) Color(0x22FFFFFF) else Color(0x10000000)))
                                                    .clickable {
                                                        val updated = formData.symptomEvaluationList.toMutableList()
                                                        updated[index] = item.copy(grade = g)
                                                        onUpdateForm(formData.copy(symptomEvaluationList = updated))
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = g,
                                                    fontSize = 12.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1,
                                                    softWrap = false,
                                                    color = if (isSelected) Color.White else textColor
                                                )
                                            }
                                        }
                                    }
                                }

                                IconButton(
                                    onClick = {
                                        val updated = formData.symptomEvaluationList.toMutableList()
                                        updated.removeAt(index)
                                        onUpdateForm(formData.copy(symptomEvaluationList = updated))
                                    },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete item", tint = subtextColor, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val updated = formData.symptomEvaluationList.toMutableList()
                    updated.add(SymptomEvaluationItem(grade = "+", type = "Physical General", frequency = "Common"))
                    onUpdateForm(formData.copy(symptomEvaluationList = updated))
                },
                colors = ButtonDefaults.buttonColors(containerColor = TerracottaPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("btn_add_eval_symptom")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add Symptom to Evaluation")
            }
        }
    }
}
