package com.example.hockeyapplive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hockeyapplive.screens.ChatScreen
import com.example.hockeyapplive.screens.EventsScreen
import com.example.hockeyapplive.screens.LoginScreen
import com.example.hockeyapplive.screens.OnboardingScreen
import com.example.hockeyapplive.screens.PlayerRegistrationScreen
import com.example.hockeyapplive.screens.RegisterScreen
import com.example.hockeyapplive.screens.SearchFilterScreen
import com.example.hockeyapplive.screens.SettingsScreen
import com.example.hockeyapplive.screens.SplashScreen
import com.example.hockeyapplive.screens.TeamRegistrationScreen
import com.example.hockeyapplive.screens.admin.AdminDashboardScreen
import com.example.hockeyapplive.screens.admin.ManageEventsScreen
import com.example.hockeyapplive.screens.admin.ManageFeedbackScreen
import com.example.hockeyapplive.screens.admin.ManageTeamsScreen
import com.example.hockeyapplive.ui.theme.HockeyAPPLiveTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HockeyAPPLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }

    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController, context = this@MainActivity) }
            composable("login") { LoginScreen(navController, context = this@MainActivity) }
            composable("register") { RegisterScreen(navController, context = this@MainActivity) }
            composable("settings") { SettingsScreen(navController) }
            composable("chat") { ChatScreen(navController) }
            composable("search_filter") { SearchFilterScreen(navController) }
            composable("events") { EventsScreen(navController) }
            composable("team_registration") { TeamRegistrationScreen(navController) }
            composable(
                "playerRegistration/{teamName}",
                arguments = listOf(navArgument("teamName") { type = NavType.StringType })
            ) { backStackEntry ->
                PlayerRegistrationScreen(
                    navController = navController,
                    teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                )
            }
            composable("admin_dashboard") { AdminDashboardScreen(navController, context = this@MainActivity) }
            composable("manage_teams") { ManageTeamsScreen(navController = navController, context = this@MainActivity) }
            composable("manage_events") { ManageEventsScreen(navController, context = this@MainActivity) }
            composable("manage_feedback") { ManageFeedbackScreen(navController) }
        }
    }
}