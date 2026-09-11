package com.example.ui

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.CaseEntity
import com.example.data.CaseRepository
import com.example.data.ImportProgress
import com.example.data.RepertoryJsonImporter
import com.example.data.RepertoryRepository
import com.example.data.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class RootChartViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val userProfileDao = db.userProfileDao()
    val caseRepository = CaseRepository(db.caseDao())
    val repertoryRepository = RepertoryRepository(db.rubricDao(), db.remedyDao())

    val userProfile: StateFlow<UserProfile?> = userProfileDao.getUserProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val caseList: StateFlow<List<CaseEntity>> = userProfile.flatMapLatest { profile ->
        val clinician = profile?.clinicianName
        if (clinician.isNullOrBlank()) {
            caseRepository.allCases
        } else {
            caseRepository.getCasesForClinician(clinician)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalCaseCount: StateFlow<Int> = userProfile.flatMapLatest { profile ->
        val clinician = profile?.clinicianName
        if (clinician.isNullOrBlank()) {
            caseRepository.totalCount
        } else {
            caseRepository.getTotalCountForClinician(clinician)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val acuteCaseCount: StateFlow<Int> = userProfile.flatMapLatest { profile ->
        val clinician = profile?.clinicianName
        if (clinician.isNullOrBlank()) {
            caseRepository.acuteCount
        } else {
            caseRepository.getAcuteCountForClinician(clinician)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val chronicCaseCount: StateFlow<Int> = userProfile.flatMapLatest { profile ->
        val clinician = profile?.clinicianName
        if (clinician.isNullOrBlank()) {
            caseRepository.chronicCount
        } else {
            caseRepository.getChronicCountForClinician(clinician)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val rubricCount: StateFlow<Int> = repertoryRepository.rubricCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val remedyCount: StateFlow<Int> = repertoryRepository.remedyCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // Dark Mode Theme preference with database persistence
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            userProfileDao.getUserProfile().collect { profile ->
                if (profile != null) {
                    _isDarkTheme.value = profile.isDarkMode
                }
            }
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existing = userProfileDao.getUserProfileSync()
                if (existing != null) {
                    userProfileDao.updateDarkMode(isDark)
                } else {
                    userProfileDao.insertProfile(
                        UserProfile(id = 1, clinicianName = "Dr. Clinician", role = "Doctor", isDarkMode = isDark)
                    )
                }
            } catch (e: Exception) {
                Log.e("RootChartVM", "Failed to persist dark theme", e)
            }
        }
    }

    // Save status and error states
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError: StateFlow<String?> = _saveError.asStateFlow()

    fun clearSaveError() {
        _saveError.value = null
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
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentDark = _isDarkTheme.value
                userProfileDao.insertProfile(
                    UserProfile(id = 1, clinicianName = finalName, role = finalRole, isDarkMode = currentDark)
                )
            } catch (e: Exception) {
                Log.e("RootChartVM", "Failed to save user profile", e)
            }
        }
    }

    fun startNewCase(caseType: String) {
        val clinicianName = userProfile.value?.clinicianName?.ifBlank { "Dr. Clinician" } ?: "Dr. Clinician"
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

    fun saveActiveCase(caseEntity: CaseEntity, onResult: ((Boolean) -> Unit)? = null) {
        _activeCase.value = caseEntity
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            try {
                caseRepository.saveCase(caseEntity)
                _isSaving.value = false
                onResult?.invoke(true)
            } catch (e: Exception) {
                Log.e("RootChartVM", "Failed to save active case", e)
                _saveError.value = "Failed to save case draft: ${e.localizedMessage ?: "Storage error"}"
                _isSaving.value = false
                onResult?.invoke(false)
            }
        }
    }

    fun finishActiveCase(caseEntity: CaseEntity, onResult: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            val completed = caseEntity.copy(
                isCompleted = true,
                dateModified = System.currentTimeMillis()
            )
            try {
                caseRepository.saveCase(completed)
                _isSaving.value = false
                _activeCase.value = null
                _currentTab.value = com.example.ui.components.NavTab.LIBRARY
                onResult?.invoke(true)
            } catch (e: Exception) {
                Log.e("RootChartVM", "Failed to finish and save case", e)
                _saveError.value = "Failed to save completed case: ${e.localizedMessage ?: "Database write failed"}"
                _isSaving.value = false
                // CRITICAL: Keep current case data available! Do NOT set _activeCase.value = null!
                onResult?.invoke(false)
            }
        }
    }

    fun closeActiveCase(saveDraft: CaseEntity? = null) {
        if (saveDraft != null) {
            viewModelScope.launch {
                try {
                    caseRepository.saveCase(saveDraft)
                } catch (e: Exception) {
                    Log.e("RootChartVM", "Failed to save draft before closing", e)
                } finally {
                    _activeCase.value = null
                }
            }
        } else {
            _activeCase.value = null
        }
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
