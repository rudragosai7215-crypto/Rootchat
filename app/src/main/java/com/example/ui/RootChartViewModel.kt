package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CaseEntity
import com.example.data.CaseRepository
import com.example.data.ImportProgress
import com.example.data.RepertoryJsonImporter
import com.example.data.RepertoryRepository
import com.example.data.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class RootChartViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userProfileDao = db.userProfileDao()
    val caseRepository = CaseRepository(db.caseDao())
    val repertoryRepository = RepertoryRepository(db.rubricDao(), db.remedyDao())

    val userProfile: StateFlow<UserProfile?> = userProfileDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val caseList: StateFlow<List<CaseEntity>> = caseRepository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCaseCount: StateFlow<Int> = caseRepository.totalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val acuteCaseCount: StateFlow<Int> = caseRepository.acuteCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val chronicCaseCount: StateFlow<Int> = caseRepository.chronicCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val rubricCount: StateFlow<Int> = repertoryRepository.rubricCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val remedyCount: StateFlow<Int> = repertoryRepository.remedyCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Dark Mode Theme preference
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
    }

    // Current navigation tab
    private val _currentTab = MutableStateFlow(com.example.ui.components.NavTab.HOME)
    val currentTab: StateFlow<com.example.ui.components.NavTab> = _currentTab.asStateFlow()

    fun selectTab(tab: com.example.ui.components.NavTab) {
        _currentTab.value = tab
    }

    // Active Case in editing / flow
    private val _activeCase = MutableStateFlow<CaseEntity?>(null)
    val activeCase: StateFlow<CaseEntity?> = _activeCase.asStateFlow()

    // Import Progress
    private val _importProgress = MutableStateFlow(ImportProgress())
    val importProgress: StateFlow<ImportProgress> = _importProgress.asStateFlow()

    fun saveUserProfile(name: String, role: String) {
        val finalName = if (name.isBlank()) "Dr. Clinician" else name.trim()
        val finalRole = if (role.isBlank()) "Doctor" else role
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            userProfileDao.insertProfile(
                UserProfile(id = 1, clinicianName = finalName, role = finalRole)
            )
        }
    }

    fun startNewCase(caseType: String) {
        val clinicianName = userProfile.value?.clinicianName ?: "Doctor"
        val newCase = CaseEntity(
            id = UUID.randomUUID().toString(),
            caseType = caseType.uppercase(),
            clinicianName = clinicianName,
            dateCreated = System.currentTimeMillis(),
            dateModified = System.currentTimeMillis()
        )
        _activeCase.value = newCase
    }

    fun openExistingCase(caseId: String) {
        viewModelScope.launch {
            val existing = caseRepository.getCaseByIdSync(caseId)
            _activeCase.value = existing
        }
    }

    fun saveActiveCase(caseEntity: CaseEntity) {
        viewModelScope.launch {
            _activeCase.value = caseEntity
            caseRepository.saveCase(caseEntity)
        }
    }

    fun finishActiveCase(caseEntity: CaseEntity) {
        viewModelScope.launch {
            val completed = caseEntity.copy(
                isCompleted = true,
                dateModified = System.currentTimeMillis()
            )
            caseRepository.saveCase(completed)
            _activeCase.value = null
            _currentTab.value = com.example.ui.components.NavTab.LIBRARY
        }
    }

    fun closeActiveCase() {
        _activeCase.value = null
    }

    fun duplicateCase(caseId: String) {
        viewModelScope.launch {
            caseRepository.duplicateCase(caseId)
        }
    }

    fun deleteCase(caseId: String) {
        viewModelScope.launch {
            caseRepository.deleteCase(caseId)
            if (_activeCase.value?.id == caseId) {
                _activeCase.value = null
            }
        }
    }

    fun triggerRepertoryImport() {
        viewModelScope.launch {
            RepertoryJsonImporter.importRepertoryDatasets(
                context = getApplication(),
                rubricDao = db.rubricDao(),
                remedyDao = db.remedyDao(),
                onProgress = { progress ->
                    _importProgress.value = progress
                }
            )
        }
    }

    fun importDatasetFromUri(uri: Uri, isKent: Boolean) {
        viewModelScope.launch {
            RepertoryJsonImporter.importFromUri(
                context = getApplication(),
                uri = uri,
                isKent = isKent,
                rubricDao = db.rubricDao(),
                remedyDao = db.remedyDao(),
                onProgress = { progress ->
                    _importProgress.value = progress
                }
            )
        }
    }
}
