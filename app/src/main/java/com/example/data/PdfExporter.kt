package com.example.data

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfExporter {

    fun exportCaseToPdf(
        context: Context,
        case: CaseEntity,
        selectedRubrics: List<SelectedRubric>,
        repertorizationResults: List<RepertorizationScore>
    ): File? {
        val filename = "RootChart_Case_${case.patientName.replace("\\s+".toRegex(), "_")}_${System.currentTimeMillis()}.pdf"
        val pdfDir = File(context.cacheDir, "case_reports")
        if (!pdfDir.exists()) pdfDir.mkdirs()
        val file = File(pdfDir, filename)

        var pdfDocument: PdfDocument? = null
        try {
            val doc = PdfDocument()
            pdfDocument = doc
            val pageWidth = 595 // Standard A4 points (72 dpi)
            val pageHeight = 842
            var pageNumber = 1

            var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            var page = doc.startPage(pageInfo)
            var canvas = page.canvas

            val paint = Paint().apply {
                isAntiAlias = true
            }

            var y = 45f
            val leftMargin = 40f
            val rightMargin = 555f
            val contentWidth = rightMargin - leftMargin

            fun checkPageBreak(neededHeight: Float) {
                if (y + neededHeight > pageHeight - 40f) {
                    doc.finishPage(page)
                    pageNumber++
                    pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                    page = doc.startPage(pageInfo)
                    canvas = page.canvas
                    y = 45f
                }
            }

        fun drawSectionHeader(title: String, accentColor: Int = 0xFFB5502F.toInt()) {
            checkPageBreak(32f)
            paint.color = accentColor
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 12f
            canvas.drawText(title.uppercase(Locale.getDefault()), leftMargin, y, paint)
            y += 4f
            paint.strokeWidth = 1f
            canvas.drawLine(leftMargin, y, rightMargin, y, paint)
            y += 14f
        }

        fun drawField(label: String, value: String) {
            if (value.isBlank()) return
            checkPageBreak(20f)
            paint.color = Color.DKGRAY
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9.5f
            val labelStr = "$label: "
            canvas.drawText(labelStr, leftMargin, y, paint)
            val labelWidth = paint.measureText(labelStr)

            paint.color = Color.BLACK
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)

            // Clean paragraph & multiline wrapping with newline support
            val paragraphs = value.replace("\r\n", "\n").replace('\r', '\n').split('\n')
            var isFirstLine = true

            for (rawParagraph in paragraphs) {
                val paragraph = rawParagraph.trim()
                if (paragraph.isEmpty()) {
                    y += 6f
                    checkPageBreak(12f)
                    continue
                }
                val words = paragraph.split("\\s+".toRegex()).filter { it.isNotEmpty() }
                var line = ""
                var currentX = if (isFirstLine) leftMargin + labelWidth else leftMargin + 10f

                for (word in words) {
                    val testLine = if (line.isEmpty()) word else "$line $word"
                    if (currentX + paint.measureText(testLine) > rightMargin) {
                        if (line.isNotEmpty()) {
                            canvas.drawText(line, currentX, y, paint)
                            y += 13f
                            checkPageBreak(16f)
                        }
                        currentX = leftMargin + 10f
                        line = word
                    } else {
                        line = testLine
                    }
                }
                if (line.isNotEmpty()) {
                    canvas.drawText(line, currentX, y, paint)
                    y += 13f
                    checkPageBreak(16f)
                }
                isFirstLine = false
            }
            y += 3f
        }

        // --- Document Header ---
        paint.color = 0xFFB5502F.toInt()
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 18f
        canvas.drawText("ROOTCHART", leftMargin, y, paint)
        y += 16f

        paint.color = Color.GRAY
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        paint.textSize = 9.5f
        canvas.drawText("Clinical Repertorization Studio • Classical Wisdom. Modern Practice.", leftMargin, y, paint)
        y += 12f

        paint.color = Color.LTGRAY
        paint.strokeWidth = 1.5f
        canvas.drawLine(leftMargin, y, rightMargin, y, paint)
        y += 18f

        val accentColor = if (case.caseType.uppercase() == "ACUTE") 0xFFB5502F.toInt() else 0xFF5B7E68.toInt()
        val form = ClinicalFormData.fromCaseEntity(case)

        // 1. CASE IDENTIFICATION
        drawSectionHeader("1. Case Identification (${case.caseType})", accentColor)
        val patName = form.name.ifBlank { case.patientName }.ifBlank { "Unnamed Patient" }
        drawField("Patient Name", patName)
        val patAge = form.age.ifBlank { case.patientAge }
        val patGender = form.sex.ifBlank { case.patientGender }
        if (patAge.isNotBlank() || patGender.isNotBlank()) {
            drawField("Age / Gender", "$patAge / $patGender".trim().trim('/'))
        }
        drawField("Sr. No.", form.srNo)
        drawField("OPD / Case No.", form.opdCaseNo)
        val caseDate = form.date.ifBlank { case.caseDate }
        if (caseDate.isNotBlank()) {
            drawField("Case Date", caseDate)
        } else {
            val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(case.dateCreated))
            drawField("Recorded On", dateFormatted)
        }
        drawField("Marital Status", if (form.maritalStatus != "Single" || form.name.isNotBlank()) form.maritalStatus else "")
        drawField("Occupation", form.occupation)
        drawField("Qualification", form.qualification)
        drawField("Religion", form.religion)
        drawField("Address / Contact", form.address.ifBlank { case.patientContact })
        drawField("Attending Clinician", case.clinicianName.ifBlank { "Homoeopath" })

        // 2. CHIEF COMPLAINTS & PARTICULAR SYMPTOMS (LSMC)
        drawSectionHeader("2. Chief Complaints", accentColor)
        if (case.caseType.uppercase() == "ACUTE") {
            val validLsmc = form.lsmcRows.filter { it.location.isNotBlank() || it.sensation.isNotBlank() || it.modality.isNotBlank() || it.concomitant.isNotBlank() }
            if (validLsmc.isNotEmpty()) {
                validLsmc.forEachIndexed { i, row ->
                    checkPageBreak(30f)
                    val label = if (validLsmc.size > 1) "Symptom #${i + 1}" else "Particular Symptom"
                    val desc = buildString {
                        if (row.location.isNotBlank()) append("Loc: ${row.location}  ")
                        if (row.sensation.isNotBlank()) append("Sens: ${row.sensation}  ")
                        if (row.modality.isNotBlank()) append("Mod: ${row.modality}  ")
                        if (row.concomitant.isNotBlank()) append("Concom: ${row.concomitant}")
                    }.trim()
                    drawField(label, desc)
                }
            } else if (case.chiefComplaint.isNotBlank() && !case.chiefComplaint.startsWith("[")) {
                drawField("Chief Complaint", case.chiefComplaint)
            }
            if (case.lsmcLocation.isNotBlank() || case.lsmcSensation.isNotBlank() || case.lsmcModality.isNotBlank() || case.lsmcConcomitant.isNotBlank()) {
                if (validLsmc.isEmpty()) {
                    drawField("Location", case.lsmcLocation)
                    drawField("Sensation", case.lsmcSensation)
                    drawField("Modality (< Agg / > Amel)", case.lsmcModality)
                    drawField("Concomitant", case.lsmcConcomitant)
                }
            }
        } else {
            drawField("Chief Complaint", form.chronicChiefComplaint.ifBlank { if (!case.chiefComplaint.startsWith("[")) case.chiefComplaint else "" })
        }
        drawField("Associated Complaints / Etiology", form.associatedComplaints.ifBlank { case.etiology })

        // 3. O.D.P. / HISTORY OF PRESENT ILLNESS
        val hasOdp = if (case.caseType.uppercase() == "ACUTE") {
            form.acuteOdp.isNotBlank() || case.hpi.isNotBlank()
        } else {
            form.onset.isNotBlank() || form.duration.isNotBlank() || form.progress.isNotBlank() || form.hpcNarrative.isNotBlank() || case.hpi.isNotBlank()
        }
        if (hasOdp) {
            drawSectionHeader("3. History of Present Illness (O.D.P.)", accentColor)
            if (case.caseType.uppercase() == "ACUTE") {
                drawField("Onset, Duration, Progress (O.D.P.)", form.acuteOdp.ifBlank { case.hpi })
            } else {
                drawField("Onset", form.onset)
                drawField("Duration", form.duration)
                drawField("Progress", form.progress)
                val hpcText = form.hpcNarrative.ifBlank { if (!case.hpi.startsWith("{")) case.hpi else "" }
                drawField("HPC Narrative", hpcText)
            }
        }

        // 4. CHRONIC SPECIFIC: PAST HISTORY
        if (case.caseType.uppercase() == "CHRONIC") {
            val validPast = form.pastHistoryRows.filter { it.disease.isNotBlank() || it.duration.isNotBlank() || it.treatment.isNotBlank() }
            if (validPast.isNotEmpty() || (case.pastHistory.isNotBlank() && !case.pastHistory.startsWith("["))) {
                drawSectionHeader("4. Past Medical History", accentColor)
                if (validPast.isNotEmpty()) {
                    validPast.forEachIndexed { idx, p ->
                        val pStr = buildString {
                            if (p.disease.isNotBlank()) append(p.disease)
                            if (p.duration.isNotBlank()) append(" (Duration: ${p.duration})")
                            if (p.treatment.isNotBlank()) append(" - Treatment: ${p.treatment}")
                        }
                        drawField("Past Illness #${p.no.ifBlank { "${idx + 1}" }}", pStr)
                    }
                } else if (!case.pastHistory.startsWith("[")) {
                    drawField("Past Medical History", case.pastHistory)
                }
            }
        }

        // 5. CHRONIC SPECIFIC: FAMILY HISTORY
        if (case.caseType.uppercase() == "CHRONIC") {
            val validFam = form.familyHistoryRows.filter { it.relationship.isNotBlank() || it.healthIllness.isNotBlank() }
            if (validFam.isNotEmpty() || (case.familyHistory.isNotBlank() && !case.familyHistory.startsWith("["))) {
                drawSectionHeader("5. Family History", accentColor)
                if (validFam.isNotEmpty()) {
                    validFam.forEachIndexed { idx, f ->
                        val fStr = buildString {
                            if (f.healthIllness.isNotBlank()) append(f.healthIllness)
                            if (f.aliveDead.isNotBlank()) append(" [${f.aliveDead}]")
                        }
                        drawField("Relative #${f.no.ifBlank { "${idx + 1}" }} (${f.relationship})", fStr)
                    }
                } else if (!case.familyHistory.startsWith("[")) {
                    drawField("Family History", case.familyHistory)
                }
            }
        }

        // 6. PERSONAL HISTORY
        val hasPersonalData = form.appetite.isNotBlank() || form.thirst.isNotBlank() || form.desire.isNotBlank() ||
                form.aversion.isNotBlank() || form.urine.isNotBlank() || form.bowel.isNotBlank() ||
                form.perspiration.isNotBlank() || form.sleep.isNotBlank() || form.dream.isNotBlank() ||
                form.thermalState.isNotBlank() || form.tendencyTo.isNotBlank() || form.addiction.isNotBlank() ||
                form.allergy.isNotBlank() || form.milestones.isNotBlank() || form.vaccinations.isNotBlank() ||
                case.physicalGenerals.isNotBlank() || case.thermalState.isNotBlank() || case.thirstState.isNotBlank()

        if (hasPersonalData) {
            drawSectionHeader("6. Personal History & Generals", accentColor)
            drawField("Thermal State", form.thermalState.ifBlank { case.thermalState })
            drawField("Thirst", form.thirst.ifBlank { case.thirstState })
            drawField("Appetite", form.appetite)
            drawField("Desires / Cravings", form.desire)
            drawField("Aversions", form.aversion)
            drawField("Urine", form.urine)
            drawField("Bowel / Stool", form.bowel)
            drawField("Perspiration", form.perspiration)
            drawField("Sleep", form.sleep)
            drawField("Dreams", form.dream)
            drawField("Tendency To", form.tendencyTo)
            drawField("Addictions / Habits", form.addiction)
            drawField("Allergies", form.allergy)
            drawField("Milestones", form.milestones)
            drawField("Vaccinations", form.vaccinations)
            if (case.cravingsAversions.isNotBlank() && form.desire.isBlank() && form.aversion.isBlank()) {
                drawField("Cravings & Aversions", case.cravingsAversions)
            }
            if (case.physicalGenerals.isNotBlank() && !case.physicalGenerals.startsWith("Thermal:")) {
                drawField("Physical Generals Notes", case.physicalGenerals)
            }
        }

        // 7. VITAL EXAMINATION
        val hasVitals = form.temperature.isNotBlank() || form.pulse.isNotBlank() || form.rr.isNotBlank() || form.bp.isNotBlank() || (case.vitals.isNotBlank() && !case.vitals.startsWith("{"))
        if (hasVitals) {
            drawSectionHeader("7. Vital Examination", accentColor)
            drawField("Blood Pressure (BP)", form.bp)
            drawField("Pulse", form.pulse)
            drawField("Temperature", form.temperature)
            drawField("Respiratory Rate (RR)", form.rr)
            if (!case.vitals.startsWith("{") && case.vitals.isNotBlank()) {
                drawField("Vitals Summary", case.vitals)
            }
        }

        // 8. MENSTRUAL & OBSTETRIC HISTORY
        val hasMenstrual = form.menstrualApplicable || form.menarche.isNotBlank() || form.lmp.isNotBlank() || form.menstrualDuration.isNotBlank() || form.characterOfMenses.isNotBlank()
        val hasObstetric = form.obstetricApplicable || form.gpal.isNotBlank()
        if (hasMenstrual || hasObstetric) {
            drawSectionHeader("8. Menstrual & Obstetric History", accentColor)
            if (hasMenstrual) {
                drawField("Menarche", form.menarche)
                drawField("Last Menstrual Period (LMP)", form.lmp)
                drawField("Cycle Rhythm", if (form.cycleRegular) "Regular" else "Irregular")
                drawField("Duration of Menses", form.menstrualDuration)
                drawField("Character of Menses", form.characterOfMenses)
                drawField("Flow & Quantity", form.flowAndQuantity)
                drawField("Colour & Odour", "${form.menstrualColour} ${if (form.menstrualOdour.isNotBlank()) "• Odour: ${form.menstrualOdour}" else ""}".trim())
                drawField("Concomitants Before Menses", form.concomitantBefore)
                drawField("Concomitants During Menses", form.concomitantDuring)
                drawField("Concomitants After Menses", form.concomitantAfter)
                if (form.leucorrhoeaCharacter.isNotBlank() || form.leucorrhoeaOccurrence.isNotBlank() || form.leucorrhoeaPain.isNotBlank()) {
                    val leucoStr = buildString {
                        if (form.leucorrhoeaCharacter.isNotBlank()) append("Char: ${form.leucorrhoeaCharacter}  ")
                        if (form.leucorrhoeaOccurrence.isNotBlank()) append("Occ: ${form.leucorrhoeaOccurrence}  ")
                        if (form.leucorrhoeaPain.isNotBlank()) append("Pain: ${form.leucorrhoeaPain}")
                    }.trim()
                    drawField("Leucorrhoea", leucoStr)
                }
            }
            if (hasObstetric && form.gpal.isNotBlank()) {
                drawField("Obstetric History (GPAL)", form.gpal)
            }
        }

        // 9. GENERAL PHYSICAL EXAMINATION
        val hasGpe = form.consciousness.isNotBlank() || form.height.isNotBlank() || form.weight.isNotBlank() ||
                form.built.isNotBlank() || form.skin.isNotBlank() || form.conjunctiva.isNotBlank() ||
                form.nails.isNotBlank() || form.tongue.isNotBlank() || form.teeth.isNotBlank() || form.lymphNodes.isNotBlank()
        if (hasGpe) {
            drawSectionHeader("9. General Physical Examination", accentColor)
            drawField("Consciousness", form.consciousness)
            val htWt = "${form.height} ${if (form.weight.isNotBlank()) "• Weight: ${form.weight}" else ""}".trim()
            if (htWt.isNotBlank()) drawField("Height & Weight", htWt)
            drawField("Built / Constitution", form.built)
            drawField("Skin", form.skin)
            drawField("Conjunctiva / Eyes", form.conjunctiva)
            drawField("Nails", form.nails)
            drawField("Tongue", form.tongue)
            drawField("Teeth", form.teeth)
            drawField("Lymph Nodes", form.lymphNodes)
        }

        // 10. SYSTEMIC EXAMINATION
        val hasSysExam = form.respiratorySystem.isNotBlank() || form.cardiovascularSystem.isNotBlank() ||
                form.gastrointestinalSystem.isNotBlank() || form.centralNervousSystem.isNotBlank()
        if (hasSysExam) {
            drawSectionHeader("10. Systemic Examination", accentColor)
            drawField("Respiratory System (R.S.)", form.respiratorySystem)
            drawField("Cardiovascular System (C.V.S.)", form.cardiovascularSystem)
            drawField("Gastrointestinal System (G.I.S.)", form.gastrointestinalSystem)
            drawField("Central Nervous System (C.N.S.)", form.centralNervousSystem)
        }

        // 11. INVESTIGATION
        val hasInv = form.investigationBlood.isNotBlank() || form.investigationUrine.isNotBlank() ||
                form.investigationRadiology.isNotBlank() || form.investigationOther.isNotBlank()
        if (hasInv) {
            drawSectionHeader("11. Investigations", accentColor)
            drawField("Blood Investigations", form.investigationBlood)
            drawField("Urine Examination", form.investigationUrine)
            drawField("Radiology / Imaging", form.investigationRadiology)
            drawField("Other Investigations", form.investigationOther)
        }

        // 12. DIAGNOSIS
        val provDiag = form.provisionalDiagnosis
        val finalDiag = form.finalDiagnosis
        val hasDiag = provDiag.isNotBlank() || finalDiag.isNotBlank() || case.clinicalDiagnosis.isNotBlank()
        if (hasDiag) {
            drawSectionHeader("12. Clinical Diagnosis", accentColor)
            drawField("Provisional Diagnosis", provDiag)
            drawField("Final Diagnosis", finalDiag)
            if (provDiag.isBlank() && finalDiag.isBlank() && case.clinicalDiagnosis.isNotBlank()) {
                drawField("Diagnosis", case.clinicalDiagnosis)
            }
        }

        // 13. CLINICIAN'S OBSERVATION
        if (form.clinicianObservation.isNotBlank()) {
            drawSectionHeader("13. Clinician's Observation", accentColor)
            drawField("Observations", form.clinicianObservation)
        }

        // 14. MENTAL & LIFE SPAN
        val mentalText = form.mentalLifeSpan.ifBlank { case.mentalSymptoms }
        if (mentalText.isNotBlank()) {
            drawSectionHeader("14. Mental & Life Span", accentColor)
            drawField("Mental State & Patient Narrative", mentalText)
        }

        // 15. MIASMATIC EVALUATION (CHRONIC)
        if (case.caseType.uppercase() == "CHRONIC") {
            val miasm = form.predominantMiasm.ifBlank { case.miasmaticEvaluation }
            val mNotes = form.miasmaticNotes.ifBlank { case.miasmaticNotes }
            if (miasm.isNotBlank() || mNotes.isNotBlank()) {
                drawSectionHeader("15. Miasmatic Evaluation", accentColor)
                drawField("Predominant Miasm", miasm)
                drawField("Miasmatic Analysis Notes", mNotes)
            }
        }

        // 16. ANALYSIS OF SYMPTOMS
        if (form.symptomAnalysisList.isNotEmpty()) {
            drawSectionHeader("16. Analysis of Symptoms", accentColor)
            form.symptomAnalysisList.forEachIndexed { index, item ->
                checkPageBreak(16f)
                val line = "${index + 1}. ${item.symptomText} — [${item.type}, ${item.frequency}]"
                drawField("Symptom #${index + 1}", line)
            }
        }

        // 17. EVALUATION OF SYMPTOMS
        if (form.symptomEvaluationList.isNotEmpty()) {
            drawSectionHeader("17. Evaluation of Symptoms", accentColor)
            form.symptomEvaluationList.forEachIndexed { index, item ->
                checkPageBreak(16f)
                val line = "${index + 1}. ${item.symptomText} ${item.grade} — [${item.type}, ${item.frequency}]"
                drawField("Evaluated #${index + 1}", line)
            }
        }

        // 18. TOTALITY OF SYMPTOMS
        val totNotes = form.totalityNotes.ifBlank { if (!case.totalitySymptomNotes.startsWith("{")) case.totalitySymptomNotes else "" }
        if (totNotes.isNotBlank()) {
            drawSectionHeader("18. Totality of Symptoms", accentColor)
            drawField("Totality Synthesis", totNotes)
        }

        // 19. REPERTORIZATION: SELECTED RUBRICS
        drawSectionHeader("19. Repertorization — Selected Rubrics", accentColor)
        if (selectedRubrics.isNotEmpty()) {
            for ((index, rubric) in selectedRubrics.withIndex()) {
                checkPageBreak(16f)
                paint.color = Color.BLACK
                paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
                paint.textSize = 9f
                canvas.drawText("${index + 1}. [${rubric.chapter}] ${rubric.rubricText}", leftMargin + 8f, y, paint)
                y += 13f
            }
            y += 8f
        } else {
            drawField("Selected Rubrics", "None selected")
        }

        // 20. REPERTORIZATION RESULTS MATRIX
        drawSectionHeader("20. Repertorization Matrix (Ranked Remedies)", accentColor)
        if (repertorizationResults.isNotEmpty()) {
            checkPageBreak(22f)
            paint.color = 0xFF443329.toInt()
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 9f
            canvas.drawText("Rank", leftMargin, y, paint)
            canvas.drawText("Remedy", leftMargin + 35f, y, paint)
            canvas.drawText("Full Botanical / Chemical Name", leftMargin + 100f, y, paint)
            canvas.drawText("Score", rightMargin - 70f, y, paint)
            canvas.drawText("Coverage", rightMargin - 25f, y, paint)
            y += 5f
            paint.strokeWidth = 0.6f
            canvas.drawLine(leftMargin, y, rightMargin, y, paint)
            y += 12f

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.textSize = 8.5f

            for ((idx, rem) in repertorizationResults.take(15).withIndex()) {
                checkPageBreak(15f)
                paint.color = if (idx < 3) accentColor else Color.DKGRAY
                canvas.drawText("#${idx + 1}", leftMargin, y, paint)
                canvas.drawText(rem.abbreviation.uppercase(Locale.getDefault()), leftMargin + 35f, y, paint)
                canvas.drawText(rem.fullName.take(35), leftMargin + 100f, y, paint)
                canvas.drawText("${rem.score}", rightMargin - 65f, y, paint)
                canvas.drawText("${rem.coverage}/${selectedRubrics.size}", rightMargin - 25f, y, paint)
                y += 13f
            }
            y += 8f
        }

        // 21. PRESCRIPTION
        drawSectionHeader("21. Prescription (Similimum)", accentColor)
        val remName = form.prescribedRemedyFullName.ifBlank { form.prescribedRemedy }
            .ifBlank { case.prescribedRemedyFullName.ifBlank { case.prescribedRemedy } }
        val remAbbr = form.prescribedRemedy.ifBlank { case.prescribedRemedy }
        val finalRemDisplay = if (remAbbr.isNotBlank() && remAbbr != remName) "$remName ($remAbbr)" else remName.ifBlank { "Pending Similimum" }
        drawField("Selected Similimum", finalRemDisplay)
        val rxPotency = form.potency.ifBlank { case.potency }
        val rxDose = form.dose.ifBlank { case.dose }
        if (rxPotency.isNotBlank() || rxDose.isNotBlank()) {
            drawField("Potency & Dose", "$rxPotency • $rxDose".trim().trim('•').trim())
        }
        drawField("Repetition", form.repetition.ifBlank { case.repetition })
        drawField("Instructions / Regimen", form.instructions.ifBlank { case.instructions })

        // 22. MANAGEMENT & REGIMEN
        if (form.managementAdvices.isNotBlank()) {
            drawSectionHeader("22. Management & Regimen", accentColor)
            drawField("Management Advice", form.managementAdvices)
        }

        // 23. FOLLOW-UP PLAN
        val fuDate = form.followUpDate.ifBlank { case.followUpDate }
        val fuSymptoms = form.followUpCurrentSymptoms
        val fuStatus = form.followUpImprovementStatus
        val fuNextRx = form.followUpNextPrescription
        val fuRaw = if (!case.followUpNotes.startsWith("{")) case.followUpNotes else ""
        if (fuDate.isNotBlank() || fuSymptoms.isNotBlank() || fuStatus.isNotBlank() || fuNextRx.isNotBlank() || fuRaw.isNotBlank()) {
            drawSectionHeader("23. Follow-Up Plan", accentColor)
            drawField("Follow-up Date", fuDate)
            drawField("Current Symptoms", fuSymptoms)
            drawField("Improvement Status", fuStatus)
            drawField("Next Prescription / Plan", fuNextRx)
            if (fuRaw.isNotBlank()) {
                drawField("Follow-up Observations", fuRaw)
            }
        }

        // Finish last page
        doc.finishPage(page)

        FileOutputStream(file).use { out ->
            doc.writeTo(out)
        }
        return file
    } catch (e: Throwable) {
        // Fallback: write text report so case details are preserved and exportable
        try {
            file.writeText(buildFallbackReport(case, selectedRubrics, repertorizationResults))
            return file
        } catch (fallbackEx: Throwable) {
            fallbackEx.printStackTrace()
            return null
        }
    } finally {
        try {
            pdfDocument?.close()
        } catch (_: Throwable) {}
    }
}

private fun buildFallbackReport(
    case: CaseEntity,
    selectedRubrics: List<SelectedRubric>,
    repertorizationResults: List<RepertorizationScore>
): String {
    val form = ClinicalFormData.fromCaseEntity(case)
    val sb = StringBuilder()
    sb.appendLine("ROOTCHART CLINICAL CASE REPORT")
    sb.appendLine("==================================================")
    sb.appendLine("1. Case Identification (${case.caseType})")
    sb.appendLine("Patient: ${form.name.ifBlank { case.patientName }}")
    sb.appendLine("Age / Gender: ${form.age.ifBlank { case.patientAge }} / ${form.sex.ifBlank { case.patientGender }}")
    if (form.srNo.isNotBlank()) sb.appendLine("Sr No: ${form.srNo}")
    if (form.opdCaseNo.isNotBlank()) sb.appendLine("OPD No: ${form.opdCaseNo}")
    sb.appendLine("Date: ${form.date.ifBlank { case.caseDate }}")
    if (form.occupation.isNotBlank()) sb.appendLine("Occupation: ${form.occupation}")
    if (form.address.isNotBlank() || case.patientContact.isNotBlank()) sb.appendLine("Contact: ${form.address.ifBlank { case.patientContact }}")
    sb.appendLine("Clinician: ${case.clinicianName}")

    sb.appendLine("\n2. Chief Complaints")
    if (case.caseType.uppercase() == "ACUTE") {
        form.lsmcRows.forEachIndexed { i, row ->
            if (row.location.isNotBlank() || row.sensation.isNotBlank() || row.modality.isNotBlank() || row.concomitant.isNotBlank()) {
                sb.appendLine("Particular #${i + 1}: Loc: ${row.location} | Sens: ${row.sensation} | Mod: ${row.modality} | Concom: ${row.concomitant}")
            }
        }
    } else {
        sb.appendLine("Chief Complaint: ${form.chronicChiefComplaint.ifBlank { case.chiefComplaint }}")
    }
    if (form.associatedComplaints.isNotBlank() || case.etiology.isNotBlank()) {
        sb.appendLine("Associated Complaints / Etiology: ${form.associatedComplaints.ifBlank { case.etiology }}")
    }

    if (form.acuteOdp.isNotBlank() || form.onset.isNotBlank() || form.duration.isNotBlank() || form.hpcNarrative.isNotBlank()) {
        sb.appendLine("\n3. History of Present Illness (O.D.P.)")
        if (form.acuteOdp.isNotBlank()) sb.appendLine("O.D.P.: ${form.acuteOdp}")
        if (form.onset.isNotBlank()) sb.appendLine("Onset: ${form.onset}")
        if (form.duration.isNotBlank()) sb.appendLine("Duration: ${form.duration}")
        if (form.progress.isNotBlank()) sb.appendLine("Progress: ${form.progress}")
        if (form.hpcNarrative.isNotBlank()) sb.appendLine("HPC: ${form.hpcNarrative}")
    }

    if (case.caseType.uppercase() == "CHRONIC") {
        val validPast = form.pastHistoryRows.filter { it.disease.isNotBlank() }
        if (validPast.isNotEmpty()) {
            sb.appendLine("\n4. Past Medical History")
            validPast.forEach { sb.appendLine("- ${it.disease} (${it.duration}) - ${it.treatment}") }
        }
        val validFam = form.familyHistoryRows.filter { it.relationship.isNotBlank() || it.healthIllness.isNotBlank() }
        if (validFam.isNotEmpty()) {
            sb.appendLine("\n5. Family History")
            validFam.forEach { sb.appendLine("- ${it.relationship}: ${it.healthIllness} [${it.aliveDead}]") }
        }
    }

    sb.appendLine("\n6. Personal History")
    if (form.thermalState.isNotBlank()) sb.appendLine("Thermal: ${form.thermalState}")
    if (form.thirst.isNotBlank()) sb.appendLine("Thirst: ${form.thirst}")
    if (form.appetite.isNotBlank()) sb.appendLine("Appetite: ${form.appetite}")
    if (form.desire.isNotBlank()) sb.appendLine("Desires: ${form.desire}")
    if (form.aversion.isNotBlank()) sb.appendLine("Aversions: ${form.aversion}")
    if (form.sleep.isNotBlank()) sb.appendLine("Sleep: ${form.sleep}")
    if (form.dream.isNotBlank()) sb.appendLine("Dreams: ${form.dream}")
    if (form.perspiration.isNotBlank()) sb.appendLine("Perspiration: ${form.perspiration}")

    if (form.temperature.isNotBlank() || form.bp.isNotBlank() || form.pulse.isNotBlank()) {
        sb.appendLine("\n7. Vital Examination")
        sb.appendLine("BP: ${form.bp} | Pulse: ${form.pulse} | Temp: ${form.temperature} | RR: ${form.rr}")
    }

    if (form.menstrualApplicable || form.obstetricApplicable || form.menarche.isNotBlank()) {
        sb.appendLine("\n8. Menstrual & Obstetric History")
        if (form.menarche.isNotBlank()) sb.appendLine("Menarche: ${form.menarche}")
        if (form.lmp.isNotBlank()) sb.appendLine("LMP: ${form.lmp}")
        if (form.characterOfMenses.isNotBlank()) sb.appendLine("Character: ${form.characterOfMenses}")
        if (form.gpal.isNotBlank()) sb.appendLine("GPAL: ${form.gpal}")
    }

    if (form.provisionalDiagnosis.isNotBlank() || form.finalDiagnosis.isNotBlank() || case.clinicalDiagnosis.isNotBlank()) {
        sb.appendLine("\nDiagnosis: ${form.finalDiagnosis.ifBlank { form.provisionalDiagnosis.ifBlank { case.clinicalDiagnosis } }}")
    }

    if (form.mentalLifeSpan.isNotBlank() || case.mentalSymptoms.isNotBlank()) {
        sb.appendLine("\nMental State: ${form.mentalLifeSpan.ifBlank { case.mentalSymptoms }}")
    }

    if (case.caseType.uppercase() == "CHRONIC" && (form.predominantMiasm.isNotBlank() || case.miasmaticEvaluation.isNotBlank())) {
        sb.appendLine("\nMiasmatic Evaluation: ${form.predominantMiasm.ifBlank { case.miasmaticEvaluation }}")
    }

    if (form.totalityNotes.isNotBlank() || case.totalitySymptomNotes.isNotBlank()) {
        sb.appendLine("\nTotality of Symptoms: ${form.totalityNotes.ifBlank { case.totalitySymptomNotes }}")
    }

    if (selectedRubrics.isNotEmpty()) {
        sb.appendLine("\nSelected Rubrics (${selectedRubrics.size}):")
        for (r in selectedRubrics) {
            sb.appendLine("- [${r.chapter}] ${r.rubricText}")
        }
    }
    if (repertorizationResults.isNotEmpty()) {
        sb.appendLine("\nTop Repertorization Results:")
        for (res in repertorizationResults.take(15)) {
            sb.appendLine("- ${res.fullName} (${res.abbreviation}): Score ${res.score}, Coverage ${res.coverage}")
        }
    }

    val finalRem = form.prescribedRemedyFullName.ifBlank { form.prescribedRemedy }.ifBlank { case.prescribedRemedyFullName.ifBlank { case.prescribedRemedy } }
    if (finalRem.isNotBlank()) {
        sb.appendLine("\nPrescription: $finalRem")
        sb.appendLine("Potency: ${form.potency.ifBlank { case.potency }} | Dose: ${form.dose.ifBlank { case.dose }} | Repetition: ${form.repetition.ifBlank { case.repetition }}")
        if (form.instructions.isNotBlank() || case.instructions.isNotBlank()) {
            sb.appendLine("Instructions: ${form.instructions.ifBlank { case.instructions }}")
        }
    }

    if (form.managementAdvices.isNotBlank()) {
        sb.appendLine("\nManagement & Advice: ${form.managementAdvices}")
    }

    if (form.followUpDate.isNotBlank() || case.followUpDate.isNotBlank() || form.followUpNextPrescription.isNotBlank()) {
        sb.appendLine("\nFollow-Up Plan:")
        if (form.followUpDate.isNotBlank() || case.followUpDate.isNotBlank()) sb.appendLine("Date: ${form.followUpDate.ifBlank { case.followUpDate }}")
        if (form.followUpCurrentSymptoms.isNotBlank()) sb.appendLine("Symptoms: ${form.followUpCurrentSymptoms}")
        if (form.followUpNextPrescription.isNotBlank()) sb.appendLine("Plan: ${form.followUpNextPrescription}")
    }

    return sb.toString()
}

    fun sharePdf(context: Context, file: File) {
        val uri: Uri = try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            Toast.makeText(context, "Error generating file provider URI: ${e.message}", Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "RootChart Clinical Case Report - ${file.name}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share RootChart Clinical PDF")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }
}
