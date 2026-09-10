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
        val pdfDocument = PdfDocument()
        val pageWidth = 595 // Standard A4 points (72 dpi)
        val pageHeight = 842
        var pageNumber = 1

        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = pdfDocument.startPage(pageInfo)
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
                pdfDocument.finishPage(page)
                pageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
                page = pdfDocument.startPage(pageInfo)
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
            
            // Simple multiline wrapping
            val words = value.split(" ")
            var line = ""
            var currentX = leftMargin + labelWidth

            for (word in words) {
                val testLine = if (line.isEmpty()) word else "$line $word"
                if (currentX + paint.measureText(testLine) > rightMargin) {
                    canvas.drawText(line, currentX, y, paint)
                    y += 13f
                    checkPageBreak(16f)
                    currentX = leftMargin + 10f
                    line = word
                } else {
                    line = testLine
                }
            }
            if (line.isNotEmpty()) {
                canvas.drawText(line, currentX, y, paint)
                y += 15f
            }
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

        // --- Patient & Case Overview ---
        val accentColor = if (case.caseType.uppercase() == "ACUTE") 0xFFB5502F.toInt() else 0xFF5B7E68.toInt()
        drawSectionHeader("Case Record (${case.caseType})", accentColor)

        drawField("Patient Name", case.patientName.ifEmpty { "N/A" })
        drawField("Age / Gender", "${case.patientAge.ifEmpty { "N/A" }} / ${case.patientGender.ifEmpty { "N/A" }}")
        drawField("Contact / Occupation", "${case.patientContact} ${if (case.patientOccupation.isNotBlank()) "• ${case.patientOccupation}" else ""}")
        val dateFormatted = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(case.dateCreated))
        drawField("Recorded On", dateFormatted)
        drawField("Attending Clinician", case.clinicianName.ifEmpty { "Homoeopath" })

        // --- Chief Complaint & Clinical Data ---
        drawSectionHeader("Clinical Observations", accentColor)
        drawField("Chief Complaint", case.chiefComplaint)
        drawField("History of Present Illness", case.hpi)
        drawField("Etiology / Exciting Cause", case.etiology)

        if (case.lsmcLocation.isNotBlank() || case.lsmcSensation.isNotBlank() || case.lsmcModality.isNotBlank() || case.lsmcConcomitant.isNotBlank()) {
            drawSectionHeader("Particular Symptoms (LSMC)", accentColor)
            drawField("Location", case.lsmcLocation)
            drawField("Sensation", case.lsmcSensation)
            drawField("Modality (< Agg / > Amel)", case.lsmcModality)
            drawField("Concomitants", case.lsmcConcomitant)
        }

        drawSectionHeader("Generals & Examination", accentColor)
        drawField("Mental Symptoms", case.mentalSymptoms)
        drawField("Physical Generals", case.physicalGenerals)
        drawField("Thermal / Thirst", "${case.thermalState} • ${case.thirstState}")
        drawField("Cravings & Aversions", case.cravingsAversions)
        drawField("Clinical Examination / Vitals", "${case.clinicalExamination} ${if (case.vitals.isNotBlank()) "(${case.vitals})" else ""}")
        drawField("Clinical Diagnosis", case.clinicalDiagnosis)

        if (case.caseType.uppercase() == "CHRONIC") {
            drawSectionHeader("Chronic Constitutional Data", accentColor)
            drawField("Past Medical History", case.pastHistory)
            drawField("Family History", case.familyHistory)
            drawField("Personal History", case.personalHistory)
            drawField("Female History", case.femaleHistory)
            drawField("Miasmatic Evaluation", "${case.miasmaticEvaluation} ${if (case.miasmaticNotes.isNotBlank()) "• ${case.miasmaticNotes}" else ""}")
        }

        // --- Totality & Rubrics ---
        drawSectionHeader("Totality of Symptoms & Selected Rubrics", accentColor)
        if (case.totalitySymptomNotes.isNotBlank()) {
            drawField("Totality Notes", case.totalitySymptomNotes)
        }

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

        // --- Repertorization Table ---
        drawSectionHeader("Repertorization Matrix (Ranked Remedies)", accentColor)
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

        // --- Prescription ---
        drawSectionHeader("Prescription (Similimum)", accentColor)
        drawField("Selected Similimum", "${case.prescribedRemedyFullName.ifEmpty { case.prescribedRemedy }} (${case.prescribedRemedy})")
        drawField("Potency & Dose", "${case.potency} • ${case.dose}")
        drawField("Repetition", case.repetition)
        drawField("Instructions / Regimen", case.instructions)

        if (case.caseType.uppercase() == "CHRONIC" && (case.followUpNotes.isNotBlank() || case.followUpDate.isNotBlank())) {
            drawSectionHeader("Follow-Up Plan", accentColor)
            drawField("Follow-up Date", case.followUpDate)
            drawField("Follow-up Criteria & Observations", case.followUpNotes)
        }

        // Finish last page
        pdfDocument.finishPage(page)

        // Save PDF to file
        val filename = "RootChart_Case_${case.patientName.replace("\\s+".toRegex(), "_")}_${System.currentTimeMillis()}.pdf"
        val pdfDir = File(context.cacheDir, "case_reports")
        if (!pdfDir.exists()) pdfDir.mkdirs()
        val file = File(pdfDir, filename)

        try {
            FileOutputStream(file).use { out ->
                pdfDocument.writeTo(out)
            }
            pdfDocument.close()
            return file
        } catch (e: Exception) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
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
