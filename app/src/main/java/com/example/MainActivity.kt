package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.ui.RootChartViewModel
import com.example.ui.components.NavTab
import com.example.ui.components.NewCaseBottomSheet
import com.example.ui.components.RootChartBottomBar
import com.example.ui.screens.CaseFlowScreen
import com.example.ui.screens.CaseLibraryScreen
import com.example.ui.screens.EntryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.GlassBackgroundScaffold
import com.example.ui.theme.RootChartTheme

class MainActivity : ComponentActivity() {

    private val viewModel: RootChartViewModel by viewModels()

    @OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            val userProfile by viewModel.userProfile.collectAsState()
            val activeCase by viewModel.activeCase.collectAsState()
            val caseList by viewModel.caseList.collectAsState()
            val totalCount by viewModel.totalCaseCount.collectAsState()
            val acuteCount by viewModel.acuteCaseCount.collectAsState()
            val chronicCount by viewModel.chronicCaseCount.collectAsState()
            val rubricCount by viewModel.rubricCount.collectAsState()
            val remedyCount by viewModel.remedyCount.collectAsState()
            val importProgress by viewModel.importProgress.collectAsState()
            val currentTab by viewModel.currentTab.collectAsState()
            val isSaving by viewModel.isSaving.collectAsState()
            val saveError by viewModel.saveError.collectAsState()

            // Auto-trigger repertory import on first launch if empty
            LaunchedEffect(rubricCount) {
                if (rubricCount == 0 && !importProgress.isImporting) {
                    viewModel.triggerRepertoryImport()
                }
            }

            var hasEnteredStudio by remember { mutableStateOf(false) }

            RootChartTheme(darkTheme = isDark) {
                if (userProfile == null && !hasEnteredStudio) {
                    // Entry Screen for clinician registration
                    EntryScreen(
                        isDark = isDark,
                        onProfileCreated = { name, role ->
                            hasEnteredStudio = true
                            viewModel.saveUserProfile(name, role)
                        }
                    )
                } else if (activeCase != null) {
                    // Active Step-by-Step Case Taking Flow
                    CaseFlowScreen(
                        initialCase = activeCase!!,
                        repertoryRepository = viewModel.repertoryRepository,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        onSaveCase = { updated -> viewModel.saveActiveCase(updated) },
                        onFinishCase = { completed -> viewModel.finishActiveCase(completed) },
                        onCancel = { draft -> viewModel.closeActiveCase(draft) },
                        onImportPrompt = { viewModel.triggerRepertoryImport() },
                        isDark = isDark,
                        isSaving = isSaving,
                        saveError = saveError,
                        onClearSaveError = { viewModel.clearSaveError() }
                    )
                } else {
                    // Main Dashboard Scaffold
                    MainDashboard(
                        viewModel = viewModel,
                        clinicianName = userProfile?.clinicianName?.ifBlank { "Dr. Rudra Goswami" } ?: "Dr. Rudra Goswami",
                        clinicianRole = userProfile?.role ?: "Doctor",
                        totalCases = totalCount,
                        acuteCases = acuteCount,
                        chronicCases = chronicCount,
                        caseList = caseList,
                        rubricCount = rubricCount,
                        remedyCount = remedyCount,
                        importProgress = importProgress,
                        currentTab = currentTab,
                        onTabSelected = { viewModel.selectTab(it) },
                        isDark = isDark
                    )
                }
            }
        }
    }
}

@androidx.compose.material3.ExperimentalMaterial3Api
@Composable
fun MainDashboard(
    viewModel: RootChartViewModel,
    clinicianName: String,
    clinicianRole: String,
    totalCases: Int,
    acuteCases: Int,
    chronicCases: Int,
    caseList: List<com.example.data.CaseEntity>,
    rubricCount: Int,
    remedyCount: Int,
    importProgress: com.example.data.ImportProgress,
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    isDark: Boolean
) {
    var showNewCaseSheet by remember { mutableStateOf(false) }

    GlassBackgroundScaffold(isDark = isDark) {
        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                RootChartBottomBar(
                    currentTab = currentTab,
                    onTabSelected = onTabSelected,
                    onNewCaseClicked = { showNewCaseSheet = true },
                    isDark = isDark
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                Crossfade(targetState = currentTab, label = "TabCrossfade") { tab ->
                    when (tab) {
                        NavTab.HOME -> HomeScreen(
                            clinicianName = clinicianName,
                            clinicianRole = clinicianRole,
                            totalCases = totalCases,
                            acuteCases = acuteCases,
                            chronicCases = chronicCases,
                            caseList = caseList,
                            onStartAcute = { viewModel.startNewCase("ACUTE") },
                            onStartChronic = { viewModel.startNewCase("CHRONIC") },
                            onOpenCase = { id -> viewModel.openExistingCase(id) },
                            onViewAllLibrary = { onTabSelected(NavTab.LIBRARY) },
                            isDark = isDark
                        )

                        NavTab.LIBRARY -> CaseLibraryScreen(
                            caseList = caseList,
                            repertoryRepository = viewModel.repertoryRepository,
                            onOpenCase = { id -> viewModel.openExistingCase(id) },
                            onDuplicateCase = { id -> viewModel.duplicateCase(id) },
                            onDeleteCase = { id -> viewModel.deleteCase(id) },
                            onNewCaseClicked = { showNewCaseSheet = true },
                            isDark = isDark
                        )

                        NavTab.REPORTS -> ReportsScreen(
                            caseList = caseList,
                            totalCases = totalCases,
                            acuteCases = acuteCases,
                            chronicCases = chronicCases,
                            isDark = isDark
                        )

                        NavTab.PROFILE -> ProfileScreen(
                            userProfile = com.example.data.UserProfile(clinicianName = clinicianName, role = clinicianRole),
                            rubricCount = rubricCount,
                            remedyCount = remedyCount,
                            isDark = isDark,
                            onToggleTheme = { viewModel.setDarkTheme(it) },
                            onUpdateProfile = { name, role -> viewModel.saveUserProfile(name, role) },
                            onImportRepertory = { viewModel.triggerRepertoryImport() },
                            importProgress = importProgress
                        )
                    }
                }
            }
        }

        // New Case Modal Bottom Sheet
        if (showNewCaseSheet) {
            NewCaseBottomSheet(
                onDismiss = { showNewCaseSheet = false },
                onSelectAcute = { viewModel.startNewCase("ACUTE") },
                onSelectChronic = { viewModel.startNewCase("CHRONIC") },
                isDark = isDark
            )
        }
    }
}
