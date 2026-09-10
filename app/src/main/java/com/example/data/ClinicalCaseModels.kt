package com.example.data

import org.json.JSONArray
import org.json.JSONObject

// -------------------------------------------------------------------------------------------------
// Nested Sub-Models for Rich Clinical Case Taking
// -------------------------------------------------------------------------------------------------

data class LsmcRow(
    val location: String = "",
    val sensation: String = "",
    val modality: String = "",
    val concomitant: String = ""
)

data class PastHistoryRow(
    val no: String = "1",
    val disease: String = "",
    val duration: String = "",
    val treatment: String = ""
)

data class FamilyHistoryRow(
    val no: String = "1",
    val relationship: String = "",
    val healthIllness: String = "",
    val aliveDead: String = ""
)

data class SymptomAnalysisItem(
    val symptomText: String = "",
    val type: String = "Physical General", // Mental General, Mental Particular, Physical General, Physical Particular
    val frequency: String = "Common" // Common, Uncommon
)

data class SymptomEvaluationItem(
    val symptomText: String = "",
    val grade: String = "+", // +, ++, +++
    val type: String = "Physical General",
    val frequency: String = "Common"
) {
    fun displayLabel(): String {
        return "$symptomText $grade ($type, $frequency)"
    }
}

// -------------------------------------------------------------------------------------------------
// Complete High-Level Clinical State for Acute & Chronic
// -------------------------------------------------------------------------------------------------

data class ClinicalFormData(
    // 1. Identification
    val srNo: String = "",
    val opdCaseNo: String = "",
    val name: String = "",
    val date: String = "",
    val age: String = "",
    val sex: String = "",
    val qualification: String = "",
    val religion: String = "",
    val occupation: String = "",
    val address: String = "",
    val maritalStatus: String = "Single",

    // 2. Chief Complaints
    // Acute: LSMC 4-column table with O.D.P.
    val lsmcRows: List<LsmcRow> = listOf(LsmcRow()),
    val acuteOdp: String = "",
    // Chronic: Single chief complaint + separate ODP + HPC narrative
    val chronicChiefComplaint: String = "",
    val onset: String = "",
    val duration: String = "",
    val progress: String = "",
    val hpcNarrative: String = "",

    // 3. Associated Complaints
    val associatedComplaints: String = "",

    // Chronic History Tables
    val pastHistoryRows: List<PastHistoryRow> = listOf(PastHistoryRow(no = "1")),
    val familyHistoryRows: List<FamilyHistoryRow> = listOf(FamilyHistoryRow(no = "1")),

    // 4 / 8. Personal History (15 individual fields)
    val appetite: String = "",
    val thirst: String = "",
    val desire: String = "",
    val aversion: String = "",
    val urine: String = "",
    val bowel: String = "",
    val perspiration: String = "",
    val sleep: String = "",
    val dream: String = "",
    val thermalState: String = "Ambithermal", // Chilly, Hot, Ambithermal
    val tendencyTo: String = "",
    val addiction: String = "",
    val allergy: String = "",
    val milestones: String = "",
    val vaccinations: String = "",

    // Vital Examination
    val temperature: String = "",
    val pulse: String = "",
    val rr: String = "",
    val bp: String = "",

    // Menstrual History (if applicable)
    val menstrualApplicable: Boolean = false,
    val menarche: String = "",
    val lmp: String = "",
    val cycleRegular: Boolean = true, // Regular / Irregular toggle
    val menstrualDuration: String = "",
    val characterOfMenses: String = "",
    val flowAndQuantity: String = "",
    val menstrualColour: String = "",
    val menstrualOdour: String = "",
    val concomitantBefore: String = "",
    val concomitantBeginning: String = "",
    val concomitantDuring: String = "",
    val concomitantAfter: String = "",
    val leucorrhoeaCharacter: String = "",
    val leucorrhoeaOccurrence: String = "",
    val leucorrhoeaPain: String = "",

    // Obstetric History (if applicable)
    val obstetricApplicable: Boolean = false,
    val gpal: String = "",

    // General Physical Examination (10 fields)
    val consciousness: String = "",
    val height: String = "",
    val weight: String = "",
    val built: String = "",
    val skin: String = "",
    val conjunctiva: String = "",
    val nails: String = "",
    val tongue: String = "",
    val teeth: String = "",
    val lymphNodes: String = "",

    // Systemic Examination (4 systems)
    val respiratorySystem: String = "",
    val cardiovascularSystem: String = "",
    val gastrointestinalSystem: String = "",
    val centralNervousSystem: String = "",

    // Investigation (4 labeled free-text sections)
    val investigationBlood: String = "",
    val investigationUrine: String = "",
    val investigationRadiology: String = "",
    val investigationOther: String = "",

    // Diagnosis
    val provisionalDiagnosis: String = "",
    val finalDiagnosis: String = "",

    // Observation (clinician's observation)
    val clinicianObservation: String = "",

    // Mental & Life Span (patient's account)
    val mentalLifeSpan: String = "",

    // Miasmatic Evaluation (Chronic)
    val predominantMiasm: String = "Psoric", // Psoric, Sycotic, Syphilitic, Tubercular, Mixed
    val miasmaticNotes: String = "",

    // Analysis & Evaluation of Symptoms
    val symptomAnalysisList: List<SymptomAnalysisItem> = emptyList(),
    val symptomEvaluationList: List<SymptomEvaluationItem> = emptyList(),

    // Totality of Symptoms
    val totalityNotes: String = "",

    // Prescription
    val prescribedRemedy: String = "",
    val prescribedRemedyFullName: String = "",
    val potency: String = "",
    val dose: String = "",
    val repetition: String = "",
    val instructions: String = "",

    // Management & Follow up
    val managementAdvices: String = "",
    val followUpDate: String = "",
    val followUpCurrentSymptoms: String = "",
    val followUpImprovementStatus: String = "",
    val followUpNextPrescription: String = ""
) {
    fun toCaseEntity(base: CaseEntity): CaseEntity {
        // Build JSON representation for extended / table structures
        val identJson = JSONObject().apply {
            put("srNo", srNo)
            put("opdCaseNo", opdCaseNo)
            put("qualification", qualification)
            put("religion", religion)
            put("maritalStatus", maritalStatus)
            put("address", address)
        }

        val lsmcArray = JSONArray()
        for (r in lsmcRows) {
            lsmcArray.put(JSONObject().apply {
                put("l", r.location)
                put("s", r.sensation)
                put("m", r.modality)
                put("c", r.concomitant)
            })
        }

        val pastArray = JSONArray()
        for (p in pastHistoryRows) {
            pastArray.put(JSONObject().apply {
                put("no", p.no)
                put("d", p.disease)
                put("dur", p.duration)
                put("t", p.treatment)
            })
        }

        val famArray = JSONArray()
        for (f in familyHistoryRows) {
            famArray.put(JSONObject().apply {
                put("no", f.no)
                put("rel", f.relationship)
                put("hi", f.healthIllness)
                put("ad", f.aliveDead)
            })
        }

        val personalJson = JSONObject().apply {
            put("appetite", appetite)
            put("thirst", thirst)
            put("desire", desire)
            put("aversion", aversion)
            put("urine", urine)
            put("bowel", bowel)
            put("perspiration", perspiration)
            put("sleep", sleep)
            put("dream", dream)
            put("thermal", thermalState)
            put("tendencyTo", tendencyTo)
            put("addiction", addiction)
            put("allergy", allergy)
            put("milestones", milestones)
            put("vaccinations", vaccinations)
        }

        val vitalsJson = JSONObject().apply {
            put("temp", temperature)
            put("pulse", pulse)
            put("rr", rr)
            put("bp", bp)
        }

        val femaleJson = JSONObject().apply {
            put("mApp", menstrualApplicable)
            put("menarche", menarche)
            put("lmp", lmp)
            put("cycleReg", cycleRegular)
            put("dur", menstrualDuration)
            put("char", characterOfMenses)
            put("flow", flowAndQuantity)
            put("colour", menstrualColour)
            put("odour", menstrualOdour)
            put("concomBefore", concomitantBefore)
            put("concomBegin", concomitantBeginning)
            put("concomDuring", concomitantDuring)
            put("concomAfter", concomitantAfter)
            put("leucoChar", leucorrhoeaCharacter)
            put("leucoOcc", leucorrhoeaOccurrence)
            put("leucoPain", leucorrhoeaPain)
            put("obsApp", obstetricApplicable)
            put("gpal", gpal)
        }

        val clinicalExamJson = JSONObject().apply {
            put("conscious", consciousness)
            put("height", height)
            put("weight", weight)
            put("built", built)
            put("skin", skin)
            put("conjunctiva", conjunctiva)
            put("nails", nails)
            put("tongue", tongue)
            put("teeth", teeth)
            put("lymph", lymphNodes)
            put("resp", respiratorySystem)
            put("cvs", cardiovascularSystem)
            put("gis", gastrointestinalSystem)
            put("cns", centralNervousSystem)
            put("invBlood", investigationBlood)
            put("invUrine", investigationUrine)
            put("invRad", investigationRadiology)
            put("invOther", investigationOther)
            put("obs", clinicianObservation)
        }

        val analysisArray = JSONArray()
        for (a in symptomAnalysisList) {
            analysisArray.put(JSONObject().apply {
                put("s", a.symptomText)
                put("t", a.type)
                put("f", a.frequency)
            })
        }

        val evaluationArray = JSONArray()
        for (e in symptomEvaluationList) {
            evaluationArray.put(JSONObject().apply {
                put("s", e.symptomText)
                put("g", e.grade)
                put("t", e.type)
                put("f", e.frequency)
            })
        }

        val totalityJson = JSONObject().apply {
            put("analysis", analysisArray)
            put("evaluation", evaluationArray)
            put("notes", totalityNotes)
        }

        val followUpJson = JSONObject().apply {
            put("date", followUpDate)
            put("symptoms", followUpCurrentSymptoms)
            put("status", followUpImprovementStatus)
            put("nextRx", followUpNextPrescription)
            put("advice", managementAdvices)
        }

        val hpiCombined = if (base.caseType == "ACUTE") {
            acuteOdp
        } else {
            JSONObject().apply {
                put("onset", onset)
                put("duration", duration)
                put("progress", progress)
                put("narrative", hpcNarrative)
            }.toString()
        }

        val diagnosisCombined = if (finalDiagnosis.isNotBlank()) {
            "Provisional: $provisionalDiagnosis | Final: $finalDiagnosis"
        } else {
            provisionalDiagnosis
        }

        val chiefComplaintData = if (base.caseType == "ACUTE") {
            if (lsmcRows.isNotEmpty()) {
                lsmcArray.toString()
            } else ""
        } else {
            chronicChiefComplaint
        }

        return base.copy(
            patientName = name,
            patientAge = age,
            patientGender = sex,
            patientContact = address,
            patientOccupation = identJson.toString(),
            caseDate = date,
            chiefComplaint = chiefComplaintData,
            hpi = hpiCombined,
            etiology = associatedComplaints,
            lsmcLocation = lsmcRows.firstOrNull()?.location ?: "",
            lsmcSensation = lsmcRows.firstOrNull()?.sensation ?: "",
            lsmcModality = lsmcRows.firstOrNull()?.modality ?: "",
            lsmcConcomitant = lsmcRows.firstOrNull()?.concomitant ?: "",
            mentalSymptoms = mentalLifeSpan,
            physicalGenerals = "Thermal: $thermalState | Appetite: $appetite | Thirst: $thirst",
            thermalState = thermalState,
            thirstState = thirst,
            cravingsAversions = "Desire: $desire | Aversion: $aversion",
            pastHistory = pastArray.toString(),
            familyHistory = famArray.toString(),
            personalHistory = personalJson.toString(),
            femaleHistory = femaleJson.toString(),
            miasmaticEvaluation = predominantMiasm,
            miasmaticNotes = miasmaticNotes,
            clinicalExamination = clinicalExamJson.toString(),
            vitals = vitalsJson.toString(),
            clinicalDiagnosis = diagnosisCombined,
            totalitySymptomNotes = totalityJson.toString(),
            prescribedRemedy = prescribedRemedy,
            prescribedRemedyFullName = prescribedRemedyFullName,
            potency = potency,
            dose = dose,
            repetition = repetition,
            instructions = instructions,
            followUpDate = followUpDate,
            followUpNotes = followUpJson.toString()
        )
    }

    companion object {
        fun fromCaseEntity(caseEntity: CaseEntity): ClinicalFormData {
            var srNo = ""
            var opdCaseNo = ""
            var qualification = ""
            var religion = ""
            var maritalStatus = "Single"
            var address = caseEntity.patientContact

            if (caseEntity.patientOccupation.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.patientOccupation)
                    srNo = obj.optString("srNo", "")
                    opdCaseNo = obj.optString("opdCaseNo", "")
                    qualification = obj.optString("qualification", "")
                    religion = obj.optString("religion", "")
                    maritalStatus = obj.optString("maritalStatus", "Single")
                    val addr = obj.optString("address", "")
                    if (addr.isNotBlank()) address = addr
                } catch (_: Exception) {}
            }

            // Chief complaint & LSMC rows
            val lsmcList = mutableListOf<LsmcRow>()
            var chronicChiefComplaint = ""
            if (caseEntity.caseType == "ACUTE") {
                if (caseEntity.chiefComplaint.startsWith("[")) {
                    try {
                        val arr = JSONArray(caseEntity.chiefComplaint)
                        for (i in 0 until arr.length()) {
                            val o = arr.getJSONObject(i)
                            lsmcList.add(
                                LsmcRow(
                                    location = o.optString("l", ""),
                                    sensation = o.optString("s", ""),
                                    modality = o.optString("m", ""),
                                    concomitant = o.optString("c", "")
                                )
                            )
                        }
                    } catch (_: Exception) {}
                }
                if (lsmcList.isEmpty()) {
                    lsmcList.add(
                        LsmcRow(
                            location = caseEntity.lsmcLocation,
                            sensation = caseEntity.lsmcSensation,
                            modality = caseEntity.lsmcModality,
                            concomitant = caseEntity.lsmcConcomitant
                        )
                    )
                }
            } else {
                chronicChiefComplaint = caseEntity.chiefComplaint
            }

            // ODP / HPC
            var acuteOdp = ""
            var onset = ""
            var duration = ""
            var progress = ""
            var hpcNarrative = ""
            if (caseEntity.caseType == "ACUTE") {
                acuteOdp = caseEntity.hpi
            } else {
                if (caseEntity.hpi.startsWith("{")) {
                    try {
                        val obj = JSONObject(caseEntity.hpi)
                        onset = obj.optString("onset", "")
                        duration = obj.optString("duration", "")
                        progress = obj.optString("progress", "")
                        hpcNarrative = obj.optString("narrative", "")
                    } catch (_: Exception) {
                        hpcNarrative = caseEntity.hpi
                    }
                } else {
                    hpcNarrative = caseEntity.hpi
                }
            }

            // Past History Rows
            val pastList = mutableListOf<PastHistoryRow>()
            if (caseEntity.pastHistory.startsWith("[")) {
                try {
                    val arr = JSONArray(caseEntity.pastHistory)
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        pastList.add(
                            PastHistoryRow(
                                no = o.optString("no", "${i + 1}"),
                                disease = o.optString("d", ""),
                                duration = o.optString("dur", ""),
                                treatment = o.optString("t", "")
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
            if (pastList.isEmpty()) {
                pastList.add(PastHistoryRow(no = "1", disease = caseEntity.pastHistory))
            }

            // Family History Rows
            val famList = mutableListOf<FamilyHistoryRow>()
            if (caseEntity.familyHistory.startsWith("[")) {
                try {
                    val arr = JSONArray(caseEntity.familyHistory)
                    for (i in 0 until arr.length()) {
                        val o = arr.getJSONObject(i)
                        famList.add(
                            FamilyHistoryRow(
                                no = o.optString("no", "${i + 1}"),
                                relationship = o.optString("rel", ""),
                                healthIllness = o.optString("hi", ""),
                                aliveDead = o.optString("ad", "")
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
            if (famList.isEmpty()) {
                famList.add(FamilyHistoryRow(no = "1", healthIllness = caseEntity.familyHistory))
            }

            // Personal History
            var appetite = ""
            var thirst = caseEntity.thirstState
            var desire = ""
            var aversion = ""
            var urine = ""
            var bowel = ""
            var perspiration = ""
            var sleep = ""
            var dream = ""
            var thermalState = if (caseEntity.thermalState.isNotBlank()) caseEntity.thermalState else "Ambithermal"
            var tendencyTo = ""
            var addiction = ""
            var allergy = ""
            var milestones = ""
            var vaccinations = ""

            if (caseEntity.personalHistory.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.personalHistory)
                    appetite = obj.optString("appetite", "")
                    thirst = obj.optString("thirst", thirst)
                    desire = obj.optString("desire", "")
                    aversion = obj.optString("aversion", "")
                    urine = obj.optString("urine", "")
                    bowel = obj.optString("bowel", "")
                    perspiration = obj.optString("perspiration", "")
                    sleep = obj.optString("sleep", "")
                    dream = obj.optString("dream", "")
                    thermalState = obj.optString("thermal", thermalState)
                    tendencyTo = obj.optString("tendencyTo", "")
                    addiction = obj.optString("addiction", "")
                    allergy = obj.optString("allergy", "")
                    milestones = obj.optString("milestones", "")
                    vaccinations = obj.optString("vaccinations", "")
                } catch (_: Exception) {}
            }

            // Vitals
            var temp = ""
            var pulse = ""
            var rr = ""
            var bp = ""
            if (caseEntity.vitals.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.vitals)
                    temp = obj.optString("temp", "")
                    pulse = obj.optString("pulse", "")
                    rr = obj.optString("rr", "")
                    bp = obj.optString("bp", "")
                } catch (_: Exception) {}
            } else if (caseEntity.vitals.isNotBlank()) {
                bp = caseEntity.vitals
            }

            // Menstrual / Obstetric
            var menstrualApplicable = false
            var menarche = ""
            var lmp = ""
            var cycleRegular = true
            var menstrualDuration = ""
            var characterOfMenses = ""
            var flowAndQuantity = ""
            var menstrualColour = ""
            var menstrualOdour = ""
            var concomBefore = ""
            var concomBegin = ""
            var concomDuring = ""
            var concomAfter = ""
            var leucoChar = ""
            var leucoOcc = ""
            var leucoPain = ""
            var obstetricApplicable = false
            var gpal = ""

            if (caseEntity.femaleHistory.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.femaleHistory)
                    menstrualApplicable = obj.optBoolean("mApp", false)
                    menarche = obj.optString("menarche", "")
                    lmp = obj.optString("lmp", "")
                    cycleRegular = obj.optBoolean("cycleReg", true)
                    menstrualDuration = obj.optString("dur", "")
                    characterOfMenses = obj.optString("char", "")
                    flowAndQuantity = obj.optString("flow", "")
                    menstrualColour = obj.optString("colour", "")
                    menstrualOdour = obj.optString("odour", "")
                    concomBefore = obj.optString("concomBefore", "")
                    concomBegin = obj.optString("concomBegin", "")
                    concomDuring = obj.optString("concomDuring", "")
                    concomAfter = obj.optString("concomAfter", "")
                    leucoChar = obj.optString("leucoChar", "")
                    leucoOcc = obj.optString("leucoOcc", "")
                    leucoPain = obj.optString("leucoPain", "")
                    obstetricApplicable = obj.optBoolean("obsApp", false)
                    gpal = obj.optString("gpal", "")
                } catch (_: Exception) {}
            }

            // Physical / Systemic Examination / Observation
            var consciousness = ""
            var height = ""
            var weight = ""
            var built = ""
            var skin = ""
            var conjunctiva = ""
            var nails = ""
            var tongue = ""
            var teeth = ""
            var lymphNodes = ""
            var respiratorySystem = ""
            var cardiovascularSystem = ""
            var gastrointestinalSystem = ""
            var centralNervousSystem = ""
            var investigationBlood = ""
            var investigationUrine = ""
            var investigationRadiology = ""
            var investigationOther = ""
            var clinicianObservation = ""

            if (caseEntity.clinicalExamination.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.clinicalExamination)
                    consciousness = obj.optString("conscious", "")
                    height = obj.optString("height", "")
                    weight = obj.optString("weight", "")
                    built = obj.optString("built", "")
                    skin = obj.optString("skin", "")
                    conjunctiva = obj.optString("conjunctiva", "")
                    nails = obj.optString("nails", "")
                    tongue = obj.optString("tongue", "")
                    teeth = obj.optString("teeth", "")
                    lymphNodes = obj.optString("lymph", "")
                    respiratorySystem = obj.optString("resp", "")
                    cardiovascularSystem = obj.optString("cvs", "")
                    gastrointestinalSystem = obj.optString("gis", "")
                    centralNervousSystem = obj.optString("cns", "")
                    investigationBlood = obj.optString("invBlood", "")
                    investigationUrine = obj.optString("invUrine", "")
                    investigationRadiology = obj.optString("invRad", "")
                    investigationOther = obj.optString("invOther", "")
                    clinicianObservation = obj.optString("obs", "")
                } catch (_: Exception) {}
            } else if (caseEntity.clinicalExamination.isNotBlank()) {
                clinicianObservation = caseEntity.clinicalExamination
            }

            // Diagnosis
            var provisionalDiagnosis = caseEntity.clinicalDiagnosis
            var finalDiagnosis = ""
            if (caseEntity.clinicalDiagnosis.contains("Provisional:")) {
                val parts = caseEntity.clinicalDiagnosis.split("|")
                provisionalDiagnosis = parts.getOrNull(0)?.replace("Provisional:", "")?.trim() ?: ""
                finalDiagnosis = parts.getOrNull(1)?.replace("Final:", "")?.trim() ?: ""
            }

            // Analysis / Evaluation / Totality
            val analysisList = mutableListOf<SymptomAnalysisItem>()
            val evaluationList = mutableListOf<SymptomEvaluationItem>()
            var totalityNotes = ""

            if (caseEntity.totalitySymptomNotes.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.totalitySymptomNotes)
                    val aArr = obj.optJSONArray("analysis")
                    if (aArr != null) {
                        for (i in 0 until aArr.length()) {
                            val o = aArr.getJSONObject(i)
                            analysisList.add(
                                SymptomAnalysisItem(
                                    symptomText = o.optString("s", ""),
                                    type = o.optString("t", "Physical General"),
                                    frequency = o.optString("f", "Common")
                                )
                            )
                        }
                    }
                    val eArr = obj.optJSONArray("evaluation")
                    if (eArr != null) {
                        for (i in 0 until eArr.length()) {
                            val o = eArr.getJSONObject(i)
                            evaluationList.add(
                                SymptomEvaluationItem(
                                    symptomText = o.optString("s", ""),
                                    grade = o.optString("g", "+"),
                                    type = o.optString("t", "Physical General"),
                                    frequency = o.optString("f", "Common")
                                )
                            )
                        }
                    }
                    totalityNotes = obj.optString("notes", "")
                } catch (_: Exception) {}
            } else {
                totalityNotes = caseEntity.totalitySymptomNotes
            }

            // Follow-up
            var followUpCurrentSymptoms = ""
            var followUpImprovementStatus = ""
            var followUpNextPrescription = ""
            var managementAdvices = ""
            if (caseEntity.followUpNotes.startsWith("{")) {
                try {
                    val obj = JSONObject(caseEntity.followUpNotes)
                    followUpCurrentSymptoms = obj.optString("symptoms", "")
                    followUpImprovementStatus = obj.optString("status", "")
                    followUpNextPrescription = obj.optString("nextRx", "")
                    managementAdvices = obj.optString("advice", "")
                } catch (_: Exception) {}
            } else {
                followUpCurrentSymptoms = caseEntity.followUpNotes
            }

            return ClinicalFormData(
                srNo = srNo,
                opdCaseNo = opdCaseNo,
                name = caseEntity.patientName,
                date = caseEntity.caseDate,
                age = caseEntity.patientAge,
                sex = caseEntity.patientGender,
                qualification = qualification,
                religion = religion,
                occupation = if (caseEntity.patientOccupation.startsWith("{")) "" else caseEntity.patientOccupation,
                address = address,
                maritalStatus = maritalStatus,
                lsmcRows = if (lsmcList.isEmpty()) listOf(LsmcRow()) else lsmcList,
                acuteOdp = acuteOdp,
                chronicChiefComplaint = chronicChiefComplaint,
                onset = onset,
                duration = duration,
                progress = progress,
                hpcNarrative = hpcNarrative,
                associatedComplaints = caseEntity.etiology,
                pastHistoryRows = if (pastList.isEmpty()) listOf(PastHistoryRow(no = "1")) else pastList,
                familyHistoryRows = if (famList.isEmpty()) listOf(FamilyHistoryRow(no = "1")) else famList,
                appetite = appetite,
                thirst = thirst,
                desire = desire,
                aversion = aversion,
                urine = urine,
                bowel = bowel,
                perspiration = perspiration,
                sleep = sleep,
                dream = dream,
                thermalState = thermalState,
                tendencyTo = tendencyTo,
                addiction = addiction,
                allergy = allergy,
                milestones = milestones,
                vaccinations = vaccinations,
                temperature = temp,
                pulse = pulse,
                rr = rr,
                bp = bp,
                menstrualApplicable = menstrualApplicable,
                menarche = menarche,
                lmp = lmp,
                cycleRegular = cycleRegular,
                menstrualDuration = menstrualDuration,
                characterOfMenses = characterOfMenses,
                flowAndQuantity = flowAndQuantity,
                menstrualColour = menstrualColour,
                menstrualOdour = menstrualOdour,
                concomitantBefore = concomBefore,
                concomitantBeginning = concomBegin,
                concomitantDuring = concomDuring,
                concomitantAfter = concomAfter,
                leucorrhoeaCharacter = leucoChar,
                leucorrhoeaOccurrence = leucoOcc,
                leucorrhoeaPain = leucoPain,
                obstetricApplicable = obstetricApplicable,
                gpal = gpal,
                consciousness = consciousness,
                height = height,
                weight = weight,
                built = built,
                skin = skin,
                conjunctiva = conjunctiva,
                nails = nails,
                tongue = tongue,
                teeth = teeth,
                lymphNodes = lymphNodes,
                respiratorySystem = respiratorySystem,
                cardiovascularSystem = cardiovascularSystem,
                gastrointestinalSystem = gastrointestinalSystem,
                centralNervousSystem = centralNervousSystem,
                investigationBlood = investigationBlood,
                investigationUrine = investigationUrine,
                investigationRadiology = investigationRadiology,
                investigationOther = investigationOther,
                provisionalDiagnosis = provisionalDiagnosis,
                finalDiagnosis = finalDiagnosis,
                clinicianObservation = clinicianObservation,
                mentalLifeSpan = caseEntity.mentalSymptoms,
                predominantMiasm = if (caseEntity.miasmaticEvaluation.isNotBlank()) caseEntity.miasmaticEvaluation else "Psoric",
                miasmaticNotes = caseEntity.miasmaticNotes,
                symptomAnalysisList = analysisList,
                symptomEvaluationList = evaluationList,
                totalityNotes = totalityNotes,
                prescribedRemedy = caseEntity.prescribedRemedy,
                prescribedRemedyFullName = caseEntity.prescribedRemedyFullName,
                potency = caseEntity.potency,
                dose = caseEntity.dose,
                repetition = caseEntity.repetition,
                instructions = caseEntity.instructions,
                managementAdvices = managementAdvices,
                followUpDate = caseEntity.followUpDate,
                followUpCurrentSymptoms = followUpCurrentSymptoms,
                followUpImprovementStatus = followUpImprovementStatus,
                followUpNextPrescription = followUpNextPrescription
            )
        }
    }
}
