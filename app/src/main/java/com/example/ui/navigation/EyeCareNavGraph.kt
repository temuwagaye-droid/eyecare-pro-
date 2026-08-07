package com.example.ui.navigation

import android.app.Application
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.bluelight.BlueLightScreen
import com.example.ui.bluelight.BlueLightViewModel
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.dashboard.DashboardViewModel
import com.example.ui.exercises.ExerciseScreen
import com.example.ui.exercises.ExerciseViewModel
import com.example.ui.fatigue.FatigueScanScreen
import com.example.ui.fatigue.FatigueScanViewModel
import com.example.ui.risk.RiskPredictionScreen
import com.example.ui.settings.PremiumUpgradeModal
import com.example.ui.settings.TipsScreen
import com.example.ui.visiontest.VisionTestScreen
import com.example.ui.visiontest.VisionTestViewModel

@Composable
fun EyeCareNavGraph(
    forceElegantDark: Boolean,
    onToggleElegantDark: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    val context = LocalContext.current.applicationContext as Application

    val dashboardVm: DashboardViewModel = viewModel { DashboardViewModel(context) }
    val blueLightVm: BlueLightViewModel = viewModel { BlueLightViewModel(context) }
    val exerciseVm: ExerciseViewModel = viewModel { ExerciseViewModel(context) }
    val fatigueVm: FatigueScanViewModel = viewModel { FatigueScanViewModel(context) }
    val visionTestVm: VisionTestViewModel = viewModel { VisionTestViewModel(context) }

    var showPremiumModal by remember { mutableStateOf(false) }

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            DashboardScreen(
                viewModel = dashboardVm,
                onNavigateToBlueLight = { navController.navigate("bluelight") },
                onNavigateToExercises = { navController.navigate("exercises") },
                onNavigateToFatigue = { navController.navigate("fatigue") },
                onNavigateToVisionTest = { navController.navigate("visiontest") },
                onNavigateToTips = { navController.navigate("tips") },
                onNavigateToRisk = { navController.navigate("risk") }
            )
        }
        composable("bluelight") {
            BlueLightScreen(viewModel = blueLightVm, onBack = { navController.popBackStack() })
        }
        composable("exercises") {
            ExerciseScreen(viewModel = exerciseVm, onBack = { navController.popBackStack() })
        }
        composable("fatigue") {
            FatigueScanScreen(viewModel = fatigueVm, onBack = { navController.popBackStack() })
        }
        composable("visiontest") {
            VisionTestScreen(viewModel = visionTestVm, onBack = { navController.popBackStack() })
        }
        composable("risk") {
            RiskPredictionScreen(onBack = { navController.popBackStack() })
        }
        composable("tips") {
            TipsScreen(
                forceElegantDark = forceElegantDark,
                onToggleElegantDark = onToggleElegantDark,
                onBack = { navController.popBackStack() },
                onOpenPremium = { showPremiumModal = true }
            )
        }
    }

    if (showPremiumModal) {
        PremiumUpgradeModal(onDismiss = { showPremiumModal = false })
    }
}
