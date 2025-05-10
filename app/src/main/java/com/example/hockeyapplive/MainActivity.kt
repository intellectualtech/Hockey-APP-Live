package com.example.hockeyapplive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hockeyapplive.data.DataSource
import com.example.hockeyapplive.screens.*
import com.example.hockeyapplive.screens.admin.AdminDashboardScreen
import com.example.hockeyapplive.screens.admin.ManageEventsScreen
import com.example.hockeyapplive.screens.admin.ManageFeedbackScreen
import com.example.hockeyapplive.screens.admin.ManageTeamsScreen
import com.example.hockeyapplive.ui.theme.HockeyAPPLiveTheme

class MainActivity : ComponentActivity() {
    private lateinit var dataSource: DataSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataSource = DataSource(this)
        enableEdgeToEdge()
        setContent {
            HockeyAPPLiveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(dataSource)
                }
            }
        }
    }

    @Composable
    private fun AppNavigation(dataSource: DataSource) {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "welcome") {
            composable("welcome") { WelcomeScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController) }
            composable("login") { LoginScreen(navController, dataSourceInstance = dataSource) }
            composable("register") { RegisterScreen(navController, dataSource) }
            composable("settings") { SettingsScreen(navController) }
            composable("chat") { ChatScreen(navController) }
            composable("search_filter") { SearchFilterScreen(navController) }
            composable("events") { EventsScreen(navController) }
            composable("team_registration") { TeamRegistrationScreen(navController, dataSource) }
            composable(
                "playerRegistration/{teamName}",
                arguments = listOf(navArgument("teamName") { type = NavType.StringType })
            ) { backStackEntry ->
                PlayerRegistrationScreen(
                    navController = navController,
                    teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                )
            }
            composable("admin_dashboard") { AdminDashboardScreen(navController) }
            composable("manage_teams") { ManageTeamsScreen(navController) }
            composable("manage_events") { ManageEventsScreen(navController) }
            composable("manage_feedback") { ManageFeedbackScreen(navController) }
            composable("admin") { AdminDashboardScreen(navController) }
            composable("coach") { TeamRegistrationScreen(navController, dataSource) }
            composable("player") { PlayerRegistrationScreen(navController, "") }
        }
    }

    // Rest of MainActivity.kt...
}