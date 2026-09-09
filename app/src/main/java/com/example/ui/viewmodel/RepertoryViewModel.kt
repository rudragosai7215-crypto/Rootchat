package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.KentRepertoryDataset
import com.example.data.local.KentRubricDataLoader
import com.example.data.local.SavedCaseEntity
import com.example.data.model.CaseTotalityItem
import com.example.data.model.KentRubric
import com.example.data.model.Remedy
import com.example.data.model.RepertorizationAnalysis
import com.example.data.repository.RepertoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
  LOGIN,
  MAIN_DASHBOARD,
  CASE_WORKFLOW,
  MATERIA_MEDICA,
  BROWSER,
  TOTALITY,
  REPERTORIZATION,
  LIBRARY
}

enum class CaseWorkflowStep(val title: String, val stepNumber: Int) {
  PRELIMINARY("Preliminary", 1),
  CASE_DETAILS("Case Recording", 2),
  TOTALITY("Totality", 3),
  REPERTORIZATION("Repertorize", 4),
  PRESCRIPTION("Prescription", 5),
  FOLLOW_UP("Follow Up", 6)
}

data class PatientCaseInfo(
  val name: String = "",
  val age: String = "",
  val gender: String = "Female",
  val phone: String = "",
  val address: String = "",
  val occupation: String = "",
  val maritalStatus: String = "",
  val date: String = "",
  val complaint: String = "",
  val caseType: String = "Acute", // "Acute" vs "Chronic"

  // ACUTE SPECIFIC STEPS (1 to 9):
  // 3. HPI Acute
  val acuteHpi: String = "",
  // 4. Etiology (Exciting cause)
  val acuteCause: String = "",
  // 5. Particular symptoms (LSMC)
  val acuteLocation: String = "",
  val acuteSensation: String = "",
  val acuteModalities: String = "",
  val acuteConcomitants: String = "",
  // 6. Acute mental symptoms
  val acuteMentalSymptoms: String = "",
  // 7. Physical generals
  val acuteThermal: String = "Neutral", // Chilly, Hot, Throws off covers, Neutral
  val acuteThirst: String = "Normal", // Large quantities, Small sips, Thirstless, Normal
  val acuteTongue: String = "",
  val acutePerspiration: String = "",
  val acuteAppetite: String = "",
  val acuteSleep: String = "",
  // 8. Clinical examination
  val acuteGeneralAppearance: String = "",
  val acutePulse: String = "",
  val acuteTemperature: String = "",
  val acuteBloodPressure: String = "",
  val acuteRespiratoryRate: String = "",
  val acuteSystemicExam: String = "",
  // 9. Clinical diagnosis
  val acuteClinicalDiagnosis: String = "",

  // CHRONIC SPECIFIC STEPS (1 to 13):
  // 3. HPI Chronic
  val chronicHpi: String = "",
  // 4. Past history
  val chronicPastHistory: String = "",
  // 5. Family history
  val chronicFamilyHistory: String = "",
  // 6. Personal history
  val chronicPersonalHistory: String = "",
  // 7. Mental generals
  val chronicMindDisposition: String = "",
  val chronicEmotionalTriggers: String = "",
  val chronicFears: String = "",
  val chronicConsolation: String = "",
  // 8. Physical generals
  val chronicThermal: String = "Chilly", // Chilly, Hot, Ambithermal
  val chronicThirst: String = "",
  val chronicCravings: String = "",
  val chronicAversions: String = "",
  val chronicBowels: String = "",
  val chronicSleep: String = "",
  val chronicPerspiration: String = "",
  // 9. Particular symptoms (LSMC)
  val chronicLocation: String = "",
  val chronicSensation: String = "",
  val chronicModalities: String = "",
  val chronicConcomitants: String = "",
  // 10. Female history
  val isFemaleHistoryApplicable: Boolean = true,
  val chronicFemaleHistory: String = "",
  // 11. Clinical examination
  val chronicClinicalExam: String = "",
  val chronicVitals: String = "",
  val chronicInvestigations: String = "",
  // 12. Final diagnosis
  val chronicFinalDiagnosis: String = "",
  // 13. Miasmatic evaluation
  val chronicMiasm: String = "Psoric", // Psoric, Sycotic, Syphilitic, Tubercular, Mixed
  val chronicMiasmNotes: String = "",

  // SHARED FINAL STEPS (10-12 Acute, 14-17 Chronic):
  val prescribedRemedy: String = "",
  val prescribedPotency: String = "200C",
  val prescribedDosage: String = "4 pills TDS for 3 days",
  val prescribedDiet: String = "Avoid raw onion, garlic, coffee, strong spices",
  val followUpDuration: String = "After 7 days",
  val followUpNotes: String = ""
)

data class PractitionerProfile(
  val name: String = "Dr. Rudra Goswami",
  val role: String = "Doctor" // "Doctor", "Student"
)

data class UiNotification(
  val message: String,
  val isError: Boolean = false
)

class RepertoryViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application)
  val repository = RepertoryRepository(database.savedCaseDao(), database.kentRubricDao())
  private val prefs = application.getSharedPreferences("rootchart_local_storage", android.content.Context.MODE_PRIVATE)

  // Navigation - start at Login as requested
  private val _currentScreen = MutableStateFlow(AppScreen.LOGIN)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  // Active Case Taking Workflow Step Index (0-based: 0..11 for Acute, 0..16 for Chronic)
  private val _currentWorkflowStepIndex = MutableStateFlow(0)
  val currentWorkflowStepIndex: StateFlow<Int> = _currentWorkflowStepIndex.asStateFlow()

  // Legacy enum step for backwards compatibility if needed
  private val _currentWorkflowStep = MutableStateFlow(CaseWorkflowStep.PRELIMINARY)
  val currentWorkflowStep: StateFlow<CaseWorkflowStep> = _currentWorkflowStep.asStateFlow()

  // Selected Saved Case for viewing/editing from Main Dashboard
  private val _selectedSavedCase = MutableStateFlow<SavedCaseEntity?>(null)
  val selectedSavedCase: StateFlow<SavedCaseEntity?> = _selectedSavedCase.asStateFlow()

  private val _showCaseDetailModal = MutableStateFlow(false)
  val showCaseDetailModal: StateFlow<Boolean> = _showCaseDetailModal.asStateFlow()

  // Practitioner Profile (RootChart Clinical Studio)
  private val _practitionerProfile = MutableStateFlow(
    PractitionerProfile(
      name = prefs.getString("clinician_name", "Dr. Rudra Goswami") ?: "Dr. Rudra Goswami",
      role = prefs.getString("clinician_role", "Doctor") ?: "Doctor"
    )
  )
  val practitionerProfile: StateFlow<PractitionerProfile> = _practitionerProfile.asStateFlow()

  private val _showProfileDialog = MutableStateFlow(false)
  val showProfileDialog: StateFlow<Boolean> = _showProfileDialog.asStateFlow()

  // Kent Rubric Browser
  private val _searchQuery = MutableStateFlow("")
  val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

  private val _selectedChapter = MutableStateFlow("All")
  val selectedChapter: StateFlow<String> = _selectedChapter.asStateFlow()

  private val _browserRubrics = MutableStateFlow<List<KentRubric>>(repository.getAllRubrics())
  val browserRubrics: StateFlow<List<KentRubric>> = _browserRubrics.asStateFlow()

  init {
    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      val loaded = KentRubricDataLoader.loadAllRubrics(application)
      repository.setLoadedRubrics(loaded)
      val dbRubrics = repository.searchDatabaseRubrics("", "All", 150)
      if (dbRubrics.isNotEmpty()) {
        _browserRubrics.value = dbRubrics
      } else {
        _browserRubrics.value = repository.searchRubrics(_searchQuery.value, _selectedChapter.value)
      }
    }
  }

  // Active Patient Case Info
  private val _caseInfo = MutableStateFlow(PatientCaseInfo())
  val caseInfo: StateFlow<PatientCaseInfo> = _caseInfo.asStateFlow()

  // Active Totality
  private val _totalityItems = MutableStateFlow<List<CaseTotalityItem>>(emptyList())
  val totalityItems: StateFlow<List<CaseTotalityItem>> = _totalityItems.asStateFlow()

  // Analysis result
  private val _repertorizationAnalysis = MutableStateFlow(repository.repertorize(emptyList()))
  val repertorizationAnalysis: StateFlow<RepertorizationAnalysis> = _repertorizationAnalysis.asStateFlow()

  // Selected Remedy for detailed Materia Medica view
  private val _selectedRemedy = MutableStateFlow<Remedy?>(null)
  val selectedRemedy: StateFlow<Remedy?> = _selectedRemedy.asStateFlow()

  // Materia Medica search
  private val _mmSearchQuery = MutableStateFlow("")
  val mmSearchQuery: StateFlow<String> = _mmSearchQuery.asStateFlow()

  // Library search
  private val _librarySearchQuery = MutableStateFlow("")
  val librarySearchQuery: StateFlow<String> = _librarySearchQuery.asStateFlow()

  // Room Database Cases - strictly filtered by logged in clinician
  val savedCases: StateFlow<List<SavedCaseEntity>> = combine(
    repository.getAllSavedCases(),
    _librarySearchQuery,
    _practitionerProfile
  ) { cases, query, profile ->
    val doctorCases = cases.filter { it.doctorName.equals(profile.name, ignoreCase = true) }
    if (query.isBlank()) doctorCases
    else doctorCases.filter {
      it.patientName.contains(query, ignoreCase = true) ||
      it.chiefComplaint.contains(query, ignoreCase = true) ||
      it.prescribedRemedy.contains(query, ignoreCase = true)
    }
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Snackbar notifications
  private val _notification = MutableStateFlow<UiNotification?>(null)
  val notification: StateFlow<UiNotification?> = _notification.asStateFlow()

  // Prescription Dialog State
  private val _showPrescriptionDialog = MutableStateFlow(false)
  val showPrescriptionDialog: StateFlow<Boolean> = _showPrescriptionDialog.asStateFlow()

  init {
    updateBrowserRubrics()
  }

  fun setScreen(screen: AppScreen) {
    _currentScreen.value = screen
  }

  fun updatePractitionerProfile(name: String, role: String) {
    val cleanName = name.ifBlank { "Dr. Rudra Goswami" }.trim()
    prefs.edit().putString("clinician_name", cleanName).putString("clinician_role", role).apply()
    _practitionerProfile.value = PractitionerProfile(cleanName, role)
    _showProfileDialog.value = false
    showNotification("Practitioner profile updated")
  }

  fun openProfileDialog() {
    _showProfileDialog.value = true
  }

  fun closeProfileDialog() {
    _showProfileDialog.value = false
  }

  fun onSearchQueryChanged(query: String) {
    _searchQuery.value = query
    updateBrowserRubrics()
  }

  fun onChapterSelected(chapter: String) {
    _selectedChapter.value = chapter
    updateBrowserRubrics()
  }

  private fun updateBrowserRubrics() {
    viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
      val results = repository.searchDatabaseRubrics(_searchQuery.value, _selectedChapter.value, 150)
      _browserRubrics.value = results.ifEmpty {
        repository.searchRubrics(_searchQuery.value, _selectedChapter.value)
      }
    }
  }

  fun loginPractitioner(name: String, role: String) {
    val cleanName = name.ifBlank { "Dr. Rudra Goswami" }.trim()
    val cleanRole = if (role.equals("Student", ignoreCase = true)) "Student" else "Doctor"
    prefs.edit()
      .putString("clinician_name", cleanName)
      .putString("clinician_role", cleanRole)
      .apply()
    _practitionerProfile.value = PractitionerProfile(cleanName, cleanRole)
    _currentScreen.value = AppScreen.MAIN_DASHBOARD
    showNotification("Welcome to RootChart Studio, ${_practitionerProfile.value.name}")
  }

  fun logoutPractitioner() {
    _currentScreen.value = AppScreen.LOGIN
  }

  fun getTotalWorkflowSteps(): Int {
    return if (_caseInfo.value.caseType.equals("Acute", ignoreCase = true)) 12 else 17
  }

  fun startNewCase(caseType: String) {
    _caseInfo.value = PatientCaseInfo(caseType = caseType, date = "Today")
    _totalityItems.value = emptyList()
    _currentWorkflowStepIndex.value = 0
    _currentWorkflowStep.value = CaseWorkflowStep.PRELIMINARY
    recalculate()
    _currentScreen.value = AppScreen.CASE_WORKFLOW
  }

  fun setWorkflowStep(step: CaseWorkflowStep) {
    _currentWorkflowStep.value = step
  }

  fun setWorkflowStepIndex(index: Int) {
    val max = getTotalWorkflowSteps()
    _currentWorkflowStepIndex.value = index.coerceIn(0, max - 1)
  }

  fun nextWorkflowStep() {
    val max = getTotalWorkflowSteps()
    if (_currentWorkflowStepIndex.value < max - 1) {
      _currentWorkflowStepIndex.value++
    }
  }

  fun prevWorkflowStep() {
    if (_currentWorkflowStepIndex.value > 0) {
      _currentWorkflowStepIndex.value--
    } else {
      _currentScreen.value = AppScreen.MAIN_DASHBOARD
    }
  }

  fun openSavedCaseDetails(case: SavedCaseEntity) {
    _selectedSavedCase.value = case
    _showCaseDetailModal.value = true
  }

  fun closeSavedCaseDetails() {
    _showCaseDetailModal.value = false
    _selectedSavedCase.value = null
  }

  fun saveCaseFromWorkflow() {
    val currentInfo = _caseInfo.value
    val analysis = _repertorizationAnalysis.value

    val rubricsSummary = _totalityItems.value.ifEmpty {
      listOf(CaseTotalityItem("gen_01", "Generalities - Physical generals", "Generalities", 2, "", "Psora"))
    }.joinToString("; ") {
      "${it.rubricName} (Grade ${it.userIntensity})"
    }

    val topRemediesSummary = analysis.scores.take(4).joinToString(", ") {
      "${it.remedyAbbr} (${it.totalScore} pts, ${it.coverageCount}/${it.totalRubrics})"
    }.ifBlank { "Repertorized totality" }

    val miasmSummary = "Dominant: ${analysis.miasmBreakdown.dominantMiasm} " +
        "(P:${analysis.miasmBreakdown.psoraPercent}% Sy:${analysis.miasmBreakdown.sycosisPercent}% " +
        "Syph:${analysis.miasmBreakdown.syphilisPercent}% Tub:${analysis.miasmBreakdown.tubercularPercent}%)"

    val prescribed = currentInfo.prescribedRemedy.ifBlank {
      analysis.scores.firstOrNull()?.remedyAbbr ?: "Nux-v"
    }

    val caseEntity = SavedCaseEntity(
      doctorName = _practitionerProfile.value.name,
      patientName = currentInfo.name.ifBlank { "Anonymous Patient" },
      patientAge = currentInfo.age.toIntOrNull() ?: 30,
      patientGender = currentInfo.gender,
      chiefComplaint = currentInfo.complaint.ifBlank { "Acute/Chronic clinical complaint" },
      caseType = currentInfo.caseType,
      selectedRubricsSummary = rubricsSummary,
      topRankedRemedies = topRemediesSummary,
      prescribedRemedy = prescribed,
      potency = currentInfo.prescribedPotency.ifBlank { "200C" },
      dosage = currentInfo.prescribedDosage.ifBlank { "4 pills TDS for 3 days" },
      followUpNotes = "${currentInfo.followUpDuration}. ${currentInfo.followUpNotes}".trim(),
      miasmSummary = miasmSummary
    )

    viewModelScope.launch {
      repository.saveCase(caseEntity)
      _totalityItems.value = emptyList()
      recalculate()
      showNotification("Case successfully saved for ${caseEntity.patientName}!")
      _currentScreen.value = AppScreen.MAIN_DASHBOARD
    }
  }

  fun getSuggestedRubricsForText(text: String): List<KentRubric> {
    if (text.isBlank()) return repository.getAllRubrics().take(8)
    val tokens = text.lowercase().split(" ", ",", ";", "\n").filter { it.length > 2 }
    if (tokens.isEmpty()) return repository.getAllRubrics().take(8)
    return repository.getAllRubrics().filter { rubric ->
      tokens.any { token ->
        rubric.rubricName.contains(token, ignoreCase = true) ||
        rubric.subRubric.contains(token, ignoreCase = true) ||
        rubric.chapter.contains(token, ignoreCase = true) ||
        rubric.modality.contains(token, ignoreCase = true)
      }
    }.take(15)
  }

  fun updateCaseInfo(updated: PatientCaseInfo) {
    _caseInfo.value = updated
  }

  fun updateCaseInfo(
    name: String = _caseInfo.value.name,
    age: String = _caseInfo.value.age,
    gender: String = _caseInfo.value.gender,
    complaint: String = _caseInfo.value.complaint,
    caseType: String = _caseInfo.value.caseType
  ) {
    _caseInfo.value = _caseInfo.value.copy(
      name = name,
      age = age,
      gender = gender,
      complaint = complaint,
      caseType = caseType
    )
  }

  fun addRubricToTotality(rubric: KentRubric, intensity: Int = 2) {
    val existingIndex = _totalityItems.value.indexOfFirst { it.rubricId == rubric.id }
    if (existingIndex >= 0) {
      // Update intensity if already exists
      _totalityItems.update { list ->
        list.map { if (it.rubricId == rubric.id) it.copy(userIntensity = intensity) else it }
      }
      showNotification("Updated ${rubric.rubricName} intensity to $intensity")
    } else {
      val newItem = CaseTotalityItem(
        rubricId = rubric.id,
        rubricName = rubric.rubricName,
        chapter = rubric.chapter,
        userIntensity = intensity,
        modalityNote = rubric.modality,
        miasm = rubric.miasm
      )
      _totalityItems.update { it + newItem }
      showNotification("Added '${rubric.rubricName}' to Case Totality")
    }
    recalculate()
  }

  fun removeRubricFromTotality(rubricId: String) {
    _totalityItems.update { list -> list.filterNot { it.rubricId == rubricId } }
    recalculate()
    showNotification("Removed symptom from totality")
  }

  fun updateRubricIntensity(rubricId: String, newIntensity: Int) {
    _totalityItems.update { list ->
      list.map { if (it.rubricId == rubricId) it.copy(userIntensity = newIntensity) else it }
    }
    recalculate()
  }

  fun updateRubricModality(rubricId: String, note: String) {
    _totalityItems.update { list ->
      list.map { if (it.rubricId == rubricId) it.copy(modalityNote = note) else it }
    }
  }

  fun loadPreset(preset: KentRepertoryDataset.TotalityPreset) {
    val newItems = preset.rubricIds.mapNotNull { id ->
      repository.getRubricById(id)?.let { rubric ->
        CaseTotalityItem(
          rubricId = rubric.id,
          rubricName = rubric.rubricName,
          chapter = rubric.chapter,
          userIntensity = 2,
          modalityNote = rubric.modality,
          miasm = rubric.miasm
        )
      }
    }
    _totalityItems.value = newItems
    _caseInfo.update {
      it.copy(
        complaint = preset.defaultComplaint,
        caseType = preset.caseType
      )
    }
    recalculate()
    showNotification("Loaded preset: ${preset.title}")
    _currentScreen.value = AppScreen.TOTALITY
  }

  fun clearTotality() {
    _totalityItems.value = emptyList()
    recalculate()
    showNotification("Case totality cleared")
  }

  private fun recalculate() {
    _repertorizationAnalysis.value = repository.repertorize(_totalityItems.value)
  }

  fun openPrescriptionDialog() {
    _showPrescriptionDialog.value = true
  }

  fun closePrescriptionDialog() {
    _showPrescriptionDialog.value = false
  }

  fun selectRemedy(remedy: Remedy?) {
    _selectedRemedy.value = remedy
  }

  fun onMMSearchChanged(query: String) {
    _mmSearchQuery.value = query
  }

  fun onLibrarySearchChanged(query: String) {
    _librarySearchQuery.value = query
  }

  fun saveCasePrescription(
    remedy: String,
    potency: String,
    dosage: String,
    followUp: String
  ) {
    val currentInfo = _caseInfo.value
    val analysis = _repertorizationAnalysis.value

    val rubricsSummary = _totalityItems.value.joinToString("; ") {
      "${it.rubricName} (Grade ${it.userIntensity})"
    }

    val topRemediesSummary = analysis.scores.take(4).joinToString(", ") {
      "${it.remedyAbbr} (${it.totalScore} pts, ${it.coverageCount}/${it.totalRubrics})"
    }

    val miasmSummary = "Dominant: ${analysis.miasmBreakdown.dominantMiasm} " +
        "(P:${analysis.miasmBreakdown.psoraPercent}% Sy:${analysis.miasmBreakdown.sycosisPercent}% " +
        "Syph:${analysis.miasmBreakdown.syphilisPercent}% Tub:${analysis.miasmBreakdown.tubercularPercent}%)"

    val caseEntity = SavedCaseEntity(
      doctorName = _practitionerProfile.value.name,
      patientName = currentInfo.name.ifBlank { "Anonymous Patient" },
      patientAge = currentInfo.age.toIntOrNull() ?: 30,
      patientGender = currentInfo.gender,
      chiefComplaint = currentInfo.complaint.ifBlank { "General repertorized complaint" },
      caseType = currentInfo.caseType,
      selectedRubricsSummary = rubricsSummary,
      topRankedRemedies = topRemediesSummary,
      prescribedRemedy = remedy,
      potency = potency,
      dosage = dosage,
      followUpNotes = followUp,
      miasmSummary = miasmSummary
    )

    viewModelScope.launch {
      repository.saveCase(caseEntity)
      _showPrescriptionDialog.value = false
      showNotification("Case successfully saved to Patient Library!")
      _currentScreen.value = AppScreen.LIBRARY
    }
  }

  fun deleteSavedCase(caseEntity: SavedCaseEntity) {
    viewModelScope.launch {
      repository.deleteCase(caseEntity)
      showNotification("Patient case removed")
    }
  }

  fun updateFollowUpNotes(caseId: Long, newNotes: String) {
    viewModelScope.launch {
      val existing = database.savedCaseDao().getCaseById(caseId) ?: return@launch
      database.savedCaseDao().updateCase(existing.copy(followUpNotes = newNotes))
      showNotification("Follow-up notes updated")
    }
  }

  fun generateCaseReport(caseEntity: SavedCaseEntity): String {
    val practitioner = _practitionerProfile.value
    return buildString {
      appendLine("==========================================")
      appendLine("ROOTCHART CLINICAL CASE REPORT")
      appendLine("Clinical Repertorization Studio")
      appendLine("Practitioner: ${practitioner.name} (${practitioner.role})")
      appendLine("==========================================")
      appendLine("Patient: ${caseEntity.patientName} (${caseEntity.patientAge}y / ${caseEntity.patientGender})")
      appendLine("Case Type: ${caseEntity.caseType}")
      appendLine("Chief Complaint: ${caseEntity.chiefComplaint}")
      appendLine("------------------------------------------")
      appendLine("SELECTED TOTALITY RUBRICS:")
      caseEntity.selectedRubricsSummary.split("; ").forEachIndexed { i, r ->
        appendLine(" ${i + 1}. $r")
      }
      appendLine("------------------------------------------")
      appendLine("TOP REPERTORIZED REMEDIES:")
      appendLine(" ${caseEntity.topRankedRemedies}")
      appendLine("Miasmatic Evaluation: ${caseEntity.miasmSummary}")
      appendLine("------------------------------------------")
      appendLine("Rx (PRESCRIBED REMEDY):")
      appendLine(" Medicine: ${caseEntity.prescribedRemedy} ${caseEntity.potency}")
      appendLine(" Dosage: ${caseEntity.dosage}")
      if (caseEntity.followUpNotes.isNotBlank()) {
        appendLine("Follow-up / Regimen: ${caseEntity.followUpNotes}")
      }
      appendLine("Diet: Avoid raw onion, garlic, coffee; clean tongue.")
      appendLine("==========================================")
    }
  }

  fun showNotification(msg: String, isError: Boolean = false) {
    _notification.value = UiNotification(msg, isError)
  }

  fun clearNotification() {
    _notification.value = null
  }
}
