package com.example.data.repository

import com.example.data.local.KentRepertoryDataset
import com.example.data.local.MateriaMedicaDataset
import com.example.data.local.SavedCaseDao
import com.example.data.local.SavedCaseEntity
import com.example.data.model.CaseTotalityItem
import com.example.data.model.KentRubric
import com.example.data.model.MiasmBreakdown
import com.example.data.model.Remedy
import com.example.data.model.RemedyScore
import com.example.data.model.RepertorizationAnalysis
import kotlinx.coroutines.flow.Flow

class RepertoryRepository(private val savedCaseDao: SavedCaseDao) {

  fun getChapters(): List<String> = KentRepertoryDataset.chapters

  fun getAllRubrics(): List<KentRubric> = KentRepertoryDataset.rubrics

  fun searchRubrics(query: String, chapter: String = "All"): List<KentRubric> {
    return KentRepertoryDataset.rubrics.filter { rubric ->
      val matchesChapter = (chapter == "All" || rubric.chapter.equals(chapter, ignoreCase = true))
      val matchesQuery = query.isBlank() ||
          rubric.rubricName.contains(query, ignoreCase = true) ||
          rubric.subRubric.contains(query, ignoreCase = true) ||
          rubric.modality.contains(query, ignoreCase = true) ||
          rubric.remedies.any { it.remedyAbbr.contains(query, ignoreCase = true) }
      matchesChapter && matchesQuery
    }
  }

  fun getRubricById(id: String): KentRubric? {
    return KentRepertoryDataset.rubrics.find { it.id == id }
  }

  fun getPresets(): List<KentRepertoryDataset.TotalityPreset> = KentRepertoryDataset.presets

  fun getMateriaMedicaList(): List<Remedy> = MateriaMedicaDataset.remedies

  fun getRemedyDetails(abbr: String): Remedy? = MateriaMedicaDataset.getRemedy(abbr)

  // Core Classical Repertorization Calculation Engine
  fun repertorize(totality: List<CaseTotalityItem>): RepertorizationAnalysis {
    if (totality.isEmpty()) {
      return RepertorizationAnalysis(
        scores = emptyList(),
        miasmBreakdown = MiasmBreakdown(25, 25, 25, 25, "Balanced"),
        totalSymptomsEvaluated = 0
      )
    }

    // Map of remedy abbreviation to aggregated stats
    val remedyCoverageMap = mutableMapOf<String, Int>()
    val remedyScoreMap = mutableMapOf<String, Int>()
    val remedyGradesPerRubric = mutableMapOf<String, MutableMap<String, Int>>()

    var psoraCount = 0
    var sycosisCount = 0
    var syphilisCount = 0
    var tubercularCount = 0

    for (item in totality) {
      val rubric = getRubricById(item.rubricId) ?: continue

      // Tally miasm
      when (rubric.miasm.lowercase()) {
        "psora" -> psoraCount += item.userIntensity
        "sycosis" -> sycosisCount += item.userIntensity
        "syphilis" -> syphilisCount += item.userIntensity
        "tubercular" -> tubercularCount += item.userIntensity
        else -> psoraCount += item.userIntensity
      }

      for (remedyGrade in rubric.remedies) {
        val abbr = remedyGrade.remedyAbbr
        remedyCoverageMap[abbr] = (remedyCoverageMap[abbr] ?: 0) + 1
        val points = remedyGrade.grade * item.userIntensity
        remedyScoreMap[abbr] = (remedyScoreMap[abbr] ?: 0) + points

        val gradesForRemedy = remedyGradesPerRubric.getOrPut(abbr) { mutableMapOf() }
        gradesForRemedy[rubric.id] = remedyGrade.grade
      }
    }

    val totalMiasmPoints = (psoraCount + sycosisCount + syphilisCount + tubercularCount).coerceAtLeast(1)
    val psoraPercent = (psoraCount * 100) / totalMiasmPoints
    val sycosisPercent = (sycosisCount * 100) / totalMiasmPoints
    val syphilisPercent = (syphilisCount * 100) / totalMiasmPoints
    val tubercularPercent = 100 - (psoraPercent + sycosisPercent + syphilisPercent)

    val dominantMiasm = when {
      psoraPercent >= sycosisPercent && psoraPercent >= syphilisPercent && psoraPercent >= tubercularPercent -> "Psora"
      sycosisPercent >= psoraPercent && sycosisPercent >= syphilisPercent && sycosisPercent >= tubercularPercent -> "Sycosis"
      syphilisPercent >= psoraPercent && syphilisPercent >= sycosisPercent && syphilisPercent >= tubercularPercent -> "Syphilis"
      else -> "Tubercular"
    }

    val scoredRemedies = remedyCoverageMap.keys.map { abbr ->
      val fullRemedy = MateriaMedicaDataset.getRemedy(abbr)
      val fullName = fullRemedy?.fullName ?: abbr
      val thermal = fullRemedy?.thermal ?: "Chilly"
      val primaryMiasm = fullRemedy?.primaryMiasm ?: "Psora"

      RemedyScore(
        remedyAbbr = abbr,
        remedyFullName = fullName,
        coverageCount = remedyCoverageMap[abbr] ?: 0,
        totalRubrics = totality.size,
        totalScore = remedyScoreMap[abbr] ?: 0,
        thermal = thermal,
        primaryMiasm = primaryMiasm,
        rubricGrades = remedyGradesPerRubric[abbr] ?: emptyMap()
      )
    }.sortedWith(
      // Sort primarily by coverage count, secondarily by total weighted score
      compareByDescending<RemedyScore> { it.coverageCount }
        .thenByDescending { it.totalScore }
    )

    return RepertorizationAnalysis(
      scores = scoredRemedies,
      miasmBreakdown = MiasmBreakdown(
        psoraPercent = psoraPercent,
        sycosisPercent = sycosisPercent,
        syphilisPercent = syphilisPercent,
        tubercularPercent = tubercularPercent,
        dominantMiasm = dominantMiasm
      ),
      totalSymptomsEvaluated = totality.size
    )
  }

  // Room Database operations
  fun getAllSavedCases(): Flow<List<SavedCaseEntity>> = savedCaseDao.getAllCases()

  fun searchSavedCases(query: String): Flow<List<SavedCaseEntity>> = savedCaseDao.searchCases(query)

  suspend fun saveCase(caseEntity: SavedCaseEntity): Long = savedCaseDao.insertCase(caseEntity)

  suspend fun updateCase(caseEntity: SavedCaseEntity) = savedCaseDao.updateCase(caseEntity)

  suspend fun deleteCase(caseEntity: SavedCaseEntity) = savedCaseDao.deleteCase(caseEntity)

  suspend fun deleteCaseById(id: Long) = savedCaseDao.deleteCaseById(id)
}
