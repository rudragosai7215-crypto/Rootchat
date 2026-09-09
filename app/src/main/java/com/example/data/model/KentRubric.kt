package com.example.data.model

data class RemedyGrade(
  val remedyAbbr: String,
  val grade: Int // 3 = Bold (High affinity), 2 = Italic (Moderate), 1 = Roman (Mild)
)

data class KentRubric(
  val id: String,
  val chapter: String,
  val rubricName: String,
  val subRubric: String = "",
  val remedies: List<RemedyGrade>,
  val miasm: String = "Psora", // Psora, Sycosis, Syphilis, Tubercular
  val modality: String = "" // e.g. "< cold", "> warmth", "< motion"
)

data class CaseTotalityItem(
  val rubricId: String,
  val rubricName: String,
  val chapter: String,
  val userIntensity: Int = 2, // 1 = mild, 2 = moderate, 3 = keynote / marked
  val modalityNote: String = "",
  val miasm: String = "Psora"
)
