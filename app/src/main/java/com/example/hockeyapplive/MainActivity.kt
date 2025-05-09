package com.example.hockeyapplive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.hockeyapplive.screens.ChatScreen
import com.example.hockeyapplive.screens.EventsScreen
import com.example.hockeyapplive.screens.LoginScreen
import com.example.hockeyapplive.screens.OnboardingScreen
import com.example.hockeyapplive.screens.PlayerRegistrationScreen
import com.example.hockeyapplive.screens.RegisterScreen
import com.example.hockeyapplive.screens.SearchFilterScreen
import com.example.hockeyapplive.screens.SettingsScreen

import com.example.hockeyapplive.screens.TeamRegistrationScreen
import com.example.hockeyapplive.screens.admin.AdminDashboardScreen
import com.example.hockeyapplive.screens.admin.ManageEventsScreen
import com.example.hockeyapplive.screens.admin.ManageFeedbackScreen
import com.example.hockeyapplive.screens.admin.ManageTeamsScreen
import com.example.hockeyapplive.ui.theme.HockeyAPPLiveTheme

/**
 * Main entry point for the Hockey App Live application.
 * Defines the navigation structure and initial UI components.
 */
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

    /**
     * Defines the application's navigation structure and routes.
     */
    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "welcome") {
            // Authentication & Onboarding
            composable("welcome") { WelcomeScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("register") { RegisterScreen(navController) }

            // Main App Features
            composable("settings") { SettingsScreen(navController) }
            composable("chat") { ChatScreen(navController) }
            composable("search_filter") { SearchFilterScreen(navController) }
            composable("events") { EventsScreen(navController) }

            // Team Management
           // composable("team_registration") { TeamRegistrationScreen(navController) }
            composable(
                "playerRegistration/{teamName}",
                arguments = listOf(navArgument("teamName") { type = NavType.StringType })
            ) { backStackEntry ->
                PlayerRegistrationScreen(
                    navController = navController,
                    teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                )
            }

            // Admin Features
            composable("admin_dashboard") { AdminDashboardScreen(navController) }
            composable("manage_teams") { ManageTeamsScreen(navController) }
            composable("manage_events") { ManageEventsScreen(navController) }
            composable("manage_feedback") { ManageFeedbackScreen(navController) }

            // NOTE: Commented out pending implementation
            // composable("event_entry") { EventEntryScreen(navController) }
            // composable("feedback") { FeedbackScreen(navController) }
        }
    }

    /**
     * The welcome screen shown when the app is first launched.
     * Provides navigation options to key app features.
     *
     * @param navController Navigation controller for routing between screens
     * @param welcomeMessage Custom welcome message to display
     */
    @Composable
    private fun WelcomeScreen(
        navController: NavController,
        welcomeMessage: String = "Welcome to Hockey App Live!"
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = welcomeMessage,
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Authentication buttons
            NavigationButton(
                text = "Go to Onboarding",
                onClick = { navController.navigate("onboarding") }
            )

            NavigationButton(
                text = "Login",
                onClick = { navController.navigate("login") }
            )

            NavigationButton(
                text = "Register",
                onClick = { navController.navigate("register") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Feature navigation buttons
            NavigationButton(
                text = "Team Registration",
                onClick = { navController.navigate("team_registration") }
            )

            NavigationButton(
                text = "Event Entry",
                onClick = { navController.navigate("event_entry") }
            )

            NavigationButton(
                text = "Settings",
                onClick = { navController.navigate("settings") }
            )

            NavigationButton(
                text = "Admin Dashboard",
                onClick = { navController.navigate("admin_dashboard") }
            )

            NavigationButton(
                text = "Dashboard",
                onClick = { navController.navigate("dashboard") }
            )
        }
    }

    /**
     * Reusable button component for navigation options.
     *
     * @param text Button text to display
     * @param onClick Action to perform when clicked
     */
    @Composable
    private fun NavigationButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier.padding(vertical = 6.dp)
        ) {
            Text(text)
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun WelcomeScreenPreview() {
        HockeyAPPLiveTheme {
            WelcomeScreen(
                navController = rememberNavController(),
                welcomeMessage = "Welcome to the Ultimate Hockey Experience!"
            )
        }
    }
}