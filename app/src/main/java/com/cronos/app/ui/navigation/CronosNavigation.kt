package com.cronos.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cronos.app.ui.screens.auth.LoginScreen
import com.cronos.app.ui.screens.onboarding.RoleSelectionScreen
import com.cronos.app.ui.screens.onboarding.ParticipantOnboardingScreen
import com.cronos.app.ui.screens.onboarding.OrganizerOnboardingScreen
import com.cronos.app.ui.screens.onboarding.ObserverOnboardingScreen
import com.cronos.app.ui.screens.dashboard.DashboardScreen
import com.cronos.app.ui.screens.events.EventsScreen
import com.cronos.app.ui.screens.events.CreateEventScreen
import com.cronos.app.ui.screens.profile.ProfileScreen
import com.cronos.app.ui.screens.rating.RatingScreen
import com.cronos.app.ui.screens.qr.QrScannerScreen
import com.cronos.app.ui.screens.inspector.InspectorScreen
import com.cronos.app.ui.screens.organizer.OrganizerProfileScreen
import com.cronos.app.ui.screens.stats.StatsScreen
import com.cronos.app.ui.screens.leaderboard.LeaderboardScreen
import com.cronos.app.ui.screens.messenger.MessengerScreen
import com.cronos.app.ui.screens.admin.AdminProfileScreen
import com.cronos.app.ui.screens.admin.AdminMessengerScreen
import com.cronos.app.ui.screens.admin.AnticheatScreen
import com.cronos.app.ui.screens.ai.AiHubScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object RoleSelection : Screen("role_selection")
    object ParticipantOnboarding : Screen("participant_onboarding")
    object OrganizerOnboarding : Screen("organizer_onboarding")
    object ObserverOnboarding : Screen("observer_onboarding")
    object Dashboard : Screen("dashboard")
    object Events : Screen("events")
    object CreateEvent : Screen("create_event")
    object Rating : Screen("rating")
    object Profile : Screen("profile")
    object QrScanner : Screen("qr_scanner")
    object Inspector : Screen("inspector")
    object OrganizerProfile : Screen("organizer_profile")
    object Stats : Screen("stats")
    object Leaderboard : Screen("leaderboard")
    object Messenger : Screen("messenger")
    object AdminProfile : Screen("admin_profile")
    object AdminMessenger : Screen("admin_messenger")
    object AiHub : Screen("ai_hub")
    object Anticheat : Screen("anticheat")
}

@Composable
fun CronosNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegistrationSuccess = {
                    navController.navigate(Screen.RoleSelection.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.RoleSelection.route) {
            RoleSelectionScreen(
                onRoleSelected = { role ->
                    val dest = when (role) {
                        "participant" -> Screen.ParticipantOnboarding.route
                        "organizer" -> Screen.OrganizerOnboarding.route
                        else -> Screen.ObserverOnboarding.route
                    }
                    navController.navigate(dest) {
                        popUpTo(Screen.RoleSelection.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ParticipantOnboarding.route) {
            ParticipantOnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ParticipantOnboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.OrganizerOnboarding.route) {
            OrganizerOnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.OrganizerOnboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.ObserverOnboarding.route) {
            ObserverOnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.ObserverOnboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Events.route) {
            EventsScreen(navController = navController)
        }
        composable(Screen.Rating.route) {
            RatingScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.CreateEvent.route) {
            CreateEventScreen(navController = navController)
        }
        composable(Screen.QrScanner.route) {
            QrScannerScreen(navController = navController)
        }
        composable(Screen.Inspector.route) {
            InspectorScreen(navController = navController)
        }
        composable(Screen.OrganizerProfile.route) {
            OrganizerProfileScreen(navController = navController)
        }
        composable(Screen.Stats.route) {
            StatsScreen(navController = navController)
        }
        composable(Screen.Leaderboard.route) {
            LeaderboardScreen(navController = navController)
        }
        composable(Screen.Messenger.route) {
            MessengerScreen(navController = navController)
        }
        composable(Screen.AdminProfile.route) {
            AdminProfileScreen(navController = navController)
        }
        composable(Screen.AdminMessenger.route) {
            AdminMessengerScreen(navController = navController)
        }
        composable(Screen.AiHub.route) {
            AiHubScreen(navController = navController)
        }
        composable(Screen.Anticheat.route) {
            AnticheatScreen(navController = navController)
        }
    }
}
