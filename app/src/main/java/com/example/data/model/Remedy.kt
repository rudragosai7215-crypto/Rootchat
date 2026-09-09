package com.example.data.model

data class Remedy(
  val abbreviation: String,
  val fullName: String,
  val commonName: String,
  val kingdom: String, // Plant, Mineral, Animal, Nosode
  val thermal: String, // Chilly, Hot, Ambi-thermal
  val primaryMiasm: String, // Psora, Sycosis, Syphilis, Tubercular
  val keynotes: List<String>,
  val modalities: String,
  val clinicalUses: String
)

data class RemedyScore(
  val remedyAbbr: String,
  val remedyFullName: String,
  val coverageCount: Int,
  val totalRubrics: Int,
  val totalScore: Int,
  val thermal: String = "Chilly",
  val primaryMiasm: String = "Psora",
  // Map of rubricId to grade (0 if not covered)
  val rubricGrades: Map<String, Int> = emptyMap()
)

data class MiasmBreakdown(
  val psoraPercent: Int,
  val sycosisPercent: Int,
  val syphilisPercent: Int,
  val tubercularPercent: Int,
  val dominantMiasm: String
)

data class RepertorizationAnalysis(
  val scores: List<RemedyScore>,
  val miasmBreakdown: MiasmBreakdown,
  val totalSymptomsEvaluated: Int
)
