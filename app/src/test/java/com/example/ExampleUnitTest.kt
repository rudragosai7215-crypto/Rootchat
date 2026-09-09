package com.example

import com.example.data.local.KentRepertoryDataset
import com.example.data.local.MateriaMedicaDataset
import com.example.data.local.SavedCaseDao
import com.example.data.local.SavedCaseEntity
import com.example.data.model.CaseTotalityItem
import com.example.data.repository.RepertoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

  private val fakeDao = object : SavedCaseDao {
    private val list = mutableListOf<SavedCaseEntity>()
    override fun getAllCases(): Flow<List<SavedCaseEntity>> = flowOf(list)
    override suspend fun getCaseById(caseId: Long): SavedCaseEntity? = list.find { it.id == caseId }
    override fun searchCases(query: String): Flow<List<SavedCaseEntity>> = flowOf(list)
    override suspend fun insertCase(caseEntity: SavedCaseEntity): Long {
      val id = (list.size + 1).toLong()
      list.add(caseEntity.copy(id = id))
      return id
    }
    override suspend fun updateCase(caseEntity: SavedCaseEntity) {
      val idx = list.indexOfFirst { it.id == caseEntity.id }
      if (idx >= 0) list[idx] = caseEntity
    }
    override suspend fun deleteCase(caseEntity: SavedCaseEntity) {
      list.removeAll { it.id == caseEntity.id }
    }
    override suspend fun deleteCaseById(caseId: Long) {
      list.removeAll { it.id == caseId }
    }
  }

  private val repository = RepertoryRepository(fakeDao)

  @Test
  fun repertoryDataset_containsChaptersAndRubrics() {
    val chapters = repository.getChapters()
    assertTrue(chapters.contains("Mind"))
    assertTrue(chapters.contains("Head"))
    assertTrue(chapters.contains("Stomach"))

    val allRubrics = repository.getAllRubrics()
    assertTrue(allRubrics.size >= 25)
  }

  @Test
  fun repertorizationEngine_calculatesSimillimumAccurately() {
    // Select Gastric/Dyspepsia symptoms for Nux-v
    val dyspepsiaPreset = KentRepertoryDataset.presets.first { it.title.contains("Dyspepsia", ignoreCase = true) }
    val totalityItems = dyspepsiaPreset.rubricIds.map { id ->
      val r = repository.getRubricById(id)!!
      CaseTotalityItem(
        rubricId = r.id,
        rubricName = r.rubricName,
        chapter = r.chapter,
        userIntensity = 3
      )
    }

    val analysis = repository.repertorize(totalityItems)
    assertTrue("Analysis should return scores", analysis.scores.isNotEmpty())
    val topRemedy = analysis.scores.first()
    assertEquals("Nux-v should be leading remedy for classical acute dyspepsia preset", "Nux-v", topRemedy.remedyAbbr)
    assertTrue(topRemedy.coverageCount >= 3)
    assertTrue(topRemedy.totalScore > 0)
  }

  @Test
  fun materiaMedica_containsPolycrests() {
    val remedies = repository.getMateriaMedicaList()
    assertTrue(remedies.any { it.abbreviation == "Nux-v" })
    assertTrue(remedies.any { it.abbreviation == "Ars" })
    assertTrue(remedies.any { it.abbreviation == "Puls" })
    assertTrue(remedies.any { it.abbreviation == "Sulph" })

    val nux = repository.getRemedyDetails("Nux-v")
    assertNotNull(nux)
    assertEquals("Nux Vomica", nux?.fullName)
    assertEquals("Chilly", nux?.thermal)
  }
}
