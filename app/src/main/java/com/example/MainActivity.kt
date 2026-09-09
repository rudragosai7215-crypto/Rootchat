package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.RepertoryBottomNavBar
import com.example.ui.components.RepertoryTopBar
import com.example.ui.components.RootChartProfileDialog
import com.example.ui.screens.CaseLibraryScreen
import com.example.ui.screens.CaseWorkflowScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainDashboardScreen
import com.example.ui.screens.MateriaMedicaScreen
import com.example.ui.screens.PrescriptionDialog
import com.example.ui.screens.RepertoryBrowserScreen
import com.example.ui.screens.RepertorizationGridScreen
import com.example.ui.screens.TotalityScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.RepertoryViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        RootChartApp()
      }
    }
  }
}

@Composable
fun RootChartApp(
  viewModel: RepertoryViewModel = viewModel()
) {
  val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
  val totalityItems by viewModel.totalityItems.collectAsStateWithLifecycle()
  val browserRubrics by viewModel.browserRubrics.collectAsStateWithLifecycle()
  val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
  val selectedChapter by viewModel.selectedChapter.collectAsStateWithLifecycle()
  val caseInfo by viewModel.caseInfo.collectAsStateWithLifecycle()
  val repertorizationAnalysis by viewModel.repertorizationAnalysis.collectAsStateWithLifecycle()
  val selectedRemedy by viewModel.selectedRemedy.collectAsStateWithLifecycle()
  val mmSearchQuery by viewModel.mmSearchQuery.collectAsStateWithLifecycle()
  val librarySearchQuery by viewModel.librarySearchQuery.collectAsStateWithLifecycle()
  val savedCases by viewModel.savedCases.collectAsStateWithLifecycle()
  val showPrescriptionDialog by viewModel.showPrescriptionDialog.collectAsStateWithLifecycle()
  val practitionerProfile by viewModel.practitionerProfile.collectAsStateWithLifecycle()
  val showProfileDialog by viewModel.showProfileDialog.collectAsStateWithLifecycle()
  val notification by viewModel.notification.collectAsStateWithLifecycle()

  val currentWorkflowStep by viewModel.currentWorkflowStep.collectAsStateWithLifecycle()
  val currentWorkflowStepIndex by viewModel.currentWorkflowStepIndex.collectAsStateWithLifecycle()
  val showMainChromium = currentScreen in listOf(
    AppScreen.BROWSER,
    AppScreen.TOTALITY,
    AppScreen.REPERTORIZATION,
    AppScreen.MATERIA_MEDICA,
    AppScreen.LIBRARY
  )

  val snackbarHostState = remember { SnackbarHostState() }

  LaunchedEffect(notification) {
    notification?.let {
      snackbarHostState.showSnackbar(
        message = it.message,
        duration = SnackbarDuration.Short
      )
      viewModel.clearNotification()
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    topBar = {
      if (showMainChromium) {
        RepertoryTopBar(
          currentScreen = currentScreen,
          totalityCount = totalityItems.size,
          practitionerProfile = practitionerProfile,
          onOpenProfileDialog = { viewModel.openProfileDialog() },
          onTotalityBadgeClicked = {
            if (totalityItems.isNotEmpty()) {
              viewModel.setScreen(AppScreen.REPERTORIZATION)
            } else {
              viewModel.setScreen(AppScreen.TOTALITY)
            }
          }
        )
      }
    },
    bottomBar = {
      if (showMainChromium) {
        RepertoryBottomNavBar(
          currentScreen = currentScreen,
          totalityCount = totalityItems.size,
          onScreenSelected = { viewModel.setScreen(it) },
          modifier = Modifier.navigationBarsPadding()
        )
      }
    },
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(if (showMainChromium) innerPadding else androidx.compose.foundation.layout.PaddingValues(0.dp))
    ) {
      when (currentScreen) {
        AppScreen.LOGIN -> {
          LoginScreen(
            initialName = practitionerProfile.name,
            initialRole = practitionerProfile.role,
            onLogin = { name, role ->
              viewModel.loginPractitioner(name, role)
            }
          )
        }

        AppScreen.MAIN_DASHBOARD -> {
          MainDashboardScreen(
            practitioner = practitionerProfile,
            savedCases = savedCases,
            onStartAcuteCase = { viewModel.startNewCase("Acute") },
            onStartChronicCase = { viewModel.startNewCase("Chronic") },
            onDeleteCase = { viewModel.deleteSavedCase(it) },
            onUpdateFollowUp = { id, notes -> viewModel.updateFollowUpNotes(id, notes) },
            onGenerateReport = { viewModel.generateCaseReport(it) },
            onLogout = { viewModel.logoutPractitioner() }
          )
        }

        AppScreen.CASE_WORKFLOW -> {
          CaseWorkflowScreen(
            currentStep = currentWorkflowStep,
            currentStepIndex = currentWorkflowStepIndex,
            caseInfo = caseInfo,
            totalityItems = totalityItems,
            analysis = repertorizationAnalysis,
            allRubrics = viewModel.repository.getAllRubrics(),
            chapters = viewModel.repository.getChapters(),
            onUpdateCaseInfo = { viewModel.updateCaseInfo(it) },
            onSetStep = { viewModel.setWorkflowStep(it) },
            onSetStepIndex = { viewModel.setWorkflowStepIndex(it) },
            onNextStep = { viewModel.nextWorkflowStep() },
            onPrevStep = { viewModel.prevWorkflowStep() },
            onAddRubric = { rubric, intensity -> viewModel.addRubricToTotality(rubric, intensity) },
            onRemoveRubric = { viewModel.removeRubricFromTotality(it) },
            onUpdateIntensity = { id, intensity -> viewModel.updateRubricIntensity(id, intensity) },
            onSaveCase = { viewModel.saveCaseFromWorkflow() },
            onCancelToDashboard = { viewModel.setScreen(AppScreen.MAIN_DASHBOARD) }
          )
        }

        AppScreen.BROWSER -> {
          RepertoryBrowserScreen(
            rubrics = browserRubrics,
            chapters = viewModel.repository.getChapters(),
            selectedChapter = selectedChapter,
            searchQuery = searchQuery,
            totalityItems = totalityItems,
            onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
            onChapterSelected = { viewModel.onChapterSelected(it) },
            onAddRubric = { rubric, intensity -> viewModel.addRubricToTotality(rubric, intensity) },
            onRemoveRubric = { rubricId -> viewModel.removeRubricFromTotality(rubricId) },
            onRemedyClick = { abbr ->
              viewModel.repository.getRemedyDetails(abbr)?.let { remedy ->
                viewModel.selectRemedy(remedy)
                viewModel.setScreen(AppScreen.MATERIA_MEDICA)
              }
            }
          )
        }

        AppScreen.TOTALITY -> {
          TotalityScreen(
            caseInfo = caseInfo,
            totalityItems = totalityItems,
            presets = viewModel.repository.getPresets(),
            practitionerProfile = practitionerProfile,
            onOpenProfileDialog = { viewModel.openProfileDialog() },
            onUpdateCaseInfo = { name, age, gender, complaint, caseType ->
              viewModel.updateCaseInfo(name, age, gender, complaint, caseType)
            },
            onUpdateIntensity = { id, level -> viewModel.updateRubricIntensity(id, level) },
            onRemoveItem = { id -> viewModel.removeRubricFromTotality(id) },
            onClearTotality = { viewModel.clearTotality() },
            onLoadPreset = { preset -> viewModel.loadPreset(preset) },
            onNavigateToBrowser = { viewModel.setScreen(AppScreen.BROWSER) },
            onNavigateToRepertorization = { viewModel.setScreen(AppScreen.REPERTORIZATION) }
          )
        }

        AppScreen.REPERTORIZATION -> {
          RepertorizationGridScreen(
            analysis = repertorizationAnalysis,
            totalityItems = totalityItems,
            onOpenPrescription = { viewModel.openPrescriptionDialog() },
            onViewRemedyDetails = { abbr ->
              viewModel.repository.getRemedyDetails(abbr)?.let { remedy ->
                viewModel.selectRemedy(remedy)
                viewModel.setScreen(AppScreen.MATERIA_MEDICA)
              }
            },
            onNavigateToBrowser = { viewModel.setScreen(AppScreen.BROWSER) }
          )
        }

        AppScreen.MATERIA_MEDICA -> {
          MateriaMedicaScreen(
            remedies = viewModel.repository.getMateriaMedicaList(),
            searchQuery = mmSearchQuery,
            selectedRemedy = selectedRemedy,
            onSearchQueryChanged = { viewModel.onMMSearchChanged(it) },
            onSelectRemedy = { viewModel.selectRemedy(it) },
            onPrescribeRemedy = { remedy ->
              viewModel.openPrescriptionDialog()
            }
          )
        }

        AppScreen.LIBRARY -> {
          CaseLibraryScreen(
            cases = savedCases,
            searchQuery = librarySearchQuery,
            onSearchQueryChanged = { viewModel.onLibrarySearchChanged(it) },
            onDeleteCase = { caseEntity -> viewModel.deleteSavedCase(caseEntity) },
            onUpdateFollowUp = { id, notes -> viewModel.updateFollowUpNotes(id, notes) },
            onGenerateReport = { caseEntity -> viewModel.generateCaseReport(caseEntity) },
            onShowNotification = { msg -> viewModel.showNotification(msg) },
            onStartNewCase = {
              viewModel.clearTotality()
              viewModel.updateCaseInfo("", "", "Female", "", "Acute")
              viewModel.setScreen(AppScreen.BROWSER)
            }
          )
        }
      }

      // Prescription Dialog
      if (showPrescriptionDialog) {
        PrescriptionDialog(
          topRemedies = repertorizationAnalysis.scores,
          onDismiss = { viewModel.closePrescriptionDialog() },
          onSavePrescription = { remedy, potency, dosage, followUp ->
            viewModel.saveCasePrescription(remedy, potency, dosage, followUp)
          }
        )
      }

      // RootChart Practitioner Profile Modal (from IMG_2902)
      if (showProfileDialog) {
        RootChartProfileDialog(
          currentProfile = practitionerProfile,
          onDismiss = { viewModel.closeProfileDialog() },
          onSave = { name, role ->
            viewModel.updatePractitionerProfile(name, role)
          }
        )
      }
    }
  }
}

@Composable
fun BhmsRepertoryApp(
  viewModel: RepertoryViewModel = viewModel()
) {
  RootChartApp(viewModel)
}
