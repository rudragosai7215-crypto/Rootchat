package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_cases")
data class SavedCaseEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val doctorName: String = "Dr. Rudra Goswami",
  val patientName: String,
  val patientAge: Int,
  val patientGender: String, // Male, Female, Child, Other
  val chiefComplaint: String,
  val caseType: String, // Acute or Chronic
  val selectedRubricsSummary: String, // Semi-colon or JSON separated rubrics
  val topRankedRemedies: String, // e.g. "1. Nux-v (14/5), 2. Ars (12/4)"
  val prescribedRemedy: String,
  val potency: String, // e.g. "30C", "200C", "1M"
  val dosage: String, // e.g. "4 pills TDS for 3 days"
  val followUpNotes: String = "",
  val miasmSummary: String = "",
  val createdAtTimestamp: Long = System.currentTimeMillis()
)
