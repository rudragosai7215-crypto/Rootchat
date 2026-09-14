package com.example.data

import kotlinx.coroutines.flow.Flow

class MateriaMedicaRepository(
    private val materiaMedicaDao: MateriaMedicaDao,
    private val remedyDao: RemedyDao
) {
    fun getSectionsForRemedy(abbrev: String): Flow<List<MateriaMedicaEntity>> {
        return materiaMedicaDao.getSectionsForRemedy(abbrev)
    }

    suspend fun getSectionsForRemedySync(abbrev: String): List<MateriaMedicaEntity> {
        val direct = materiaMedicaDao.getSectionsForRemedySync(abbrev)
        if (direct.isNotEmpty()) return direct

        // Try searching by remedy full name from remedyDao
        val remedy = remedyDao.getRemedyByAbbreviation(abbrev)
        if (remedy != null) {
            val byName = materiaMedicaDao.getSectionsForRemedyByName(remedy.fullName)
            if (byName.isNotEmpty()) return byName
        }
        return emptyList()
    }

    fun getAllRemediesSummary(): Flow<List<MateriaMedicaRemedySummary>> {
        return materiaMedicaDao.getAllRemediesSummary()
    }

    suspend fun searchRemedies(query: String): List<MateriaMedicaRemedySummary> {
        return materiaMedicaDao.searchRemediesSummary(query.trim())
    }

    suspend fun searchSymptomText(query: String): List<MateriaMedicaEntity> {
        return materiaMedicaDao.searchSymptomText(query.trim())
    }

    val totalRemediesCount: Flow<Int> get() = materiaMedicaDao.getDistinctRemedyCount()
    val totalSectionsCount: Flow<Int> get() = materiaMedicaDao.getTotalSectionCount()

    fun getTotalSectionCount(): Flow<Int> {
        return materiaMedicaDao.getTotalSectionCount()
    }

    fun getDistinctRemedyCount(): Flow<Int> {
        return materiaMedicaDao.getDistinctRemedyCount()
    }

    suspend fun getRemedyDetails(abbrev: String): RemedyNameEntity? {
        return remedyDao.getRemedyByAbbreviation(abbrev)
    }
}
