package com.example.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class CaseRepository(
    private val caseDao: CaseDao
) {
    val allCases: Flow<List<CaseEntity>> = caseDao.getAllCases()
    val allClinicians: Flow<List<String>> = caseDao.getAllClinicians()
    val totalCount: Flow<Int> = caseDao.getTotalCaseCount()
    val acuteCount: Flow<Int> = caseDao.getAcuteCaseCount()
    val chronicCount: Flow<Int> = caseDao.getChronicCaseCount()

    fun getCasesForClinician(clinician: String): Flow<List<CaseEntity>> = caseDao.getCasesForClinician(clinician)
    fun getTotalCountForClinician(clinician: String): Flow<Int> = caseDao.getTotalCaseCountForClinician(clinician)
    fun getAcuteCountForClinician(clinician: String): Flow<Int> = caseDao.getAcuteCaseCountForClinician(clinician)
    fun getChronicCountForClinician(clinician: String): Flow<Int> = caseDao.getChronicCaseCountForClinician(clinician)

    fun getCasesForWorkspace(workspaceId: String): Flow<List<CaseEntity>> = caseDao.getCasesForWorkspace(workspaceId)
    fun getTotalCountForWorkspace(workspaceId: String): Flow<Int> = caseDao.getTotalCaseCountForWorkspace(workspaceId)
    fun getAcuteCountForWorkspace(workspaceId: String): Flow<Int> = caseDao.getAcuteCaseCountForWorkspace(workspaceId)
    fun getChronicCountForWorkspace(workspaceId: String): Flow<Int> = caseDao.getChronicCaseCountForWorkspace(workspaceId)

    fun getCaseById(id: String): Flow<CaseEntity?> = caseDao.getCaseById(id)

    suspend fun getCaseByIdSync(id: String): CaseEntity? = withContext(Dispatchers.IO) {
        caseDao.getCaseByIdSync(id)
    }

    suspend fun saveCase(caseEntity: CaseEntity) = withContext(Dispatchers.IO) {
        val updated = caseEntity.copy(dateModified = System.currentTimeMillis())
        caseDao.insertCase(updated)
    }

    suspend fun deleteCase(id: String) = withContext(Dispatchers.IO) {
        caseDao.deleteCaseById(id)
    }

    suspend fun duplicateCase(sourceId: String): String? = withContext(Dispatchers.IO) {
        val original = caseDao.getCaseByIdSync(sourceId) ?: return@withContext null
        val newId = UUID.randomUUID().toString()
        val duplicated = original.copy(
            id = newId,
            patientName = "${original.patientName} (Copy)",
            dateCreated = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis()
        )
        caseDao.insertCase(duplicated)
        newId
    }
}
