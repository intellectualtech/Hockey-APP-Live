package com.example.hockeyapplive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.hockeyapplive.screens.ChatScreen
import com.example.hockeyapplive.screens.EventsScreen
import com.example.hockeyapplive.screens.FeedbackScreen
import com.example.hockeyapplive.screens.LoginScreen
import com.example.hockeyapplive.screens.OnboardingScreen
import com.example.hockeyapplive.screens.SearchFilterScreen
import com.example.hockeyapplive.screens.SettingsScreen
import com.example.hockeyapplive.screens.admin.AdminDashboardScreen
import com.example.hockeyapplive.screens.admin.ManageEventsScreen
import com.example.hockeyapplive.screens.admin.ManageFeedbackScreen
import com.example.hockeyapplive.screens.admin.ManageTeamsScreen
import com.example.hockeyapplive.ui.theme.HockeyAPPLiveTheme
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

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
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "welcome") {
                        composable("welcome") { WelcomeScreen(navController) }
                        composable("onboarding") { OnboardingScreen(navController) }
                        composable("login") { LoginScreen(navController) }
                        composable("settings") { SettingsScreen(navController) }
                        composable("chat") { ChatScreen(navController) }

                        composable("search_filter") { SearchFilterScreen(navController) }
                        composable("events") { EventsScreen(navController) }
                        composable("admin_dashboard") { AdminDashboardScreen(navController) }
                        composable("manage_teams") { ManageTeamsScreen(navController) }
                        composable("manage_events") { ManageEventsScreen(navController) }
                        composable("manage_feedback") { ManageFeedbackScreen(navController) }
                    }
                }
            }
        }
    }

    @Composable
    private fun WelcomeScreen(navController: NavController, welcomeMessage: String = "Welcome to Hockey App Live!", buttonText: String = "Get Started") {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = welcomeMessage,
                style = MaterialTheme.typography.headlineMedium
            )
            Button(
                onClick = { navController.navigate("onboarding") },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(buttonText)
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    private fun WelcomeScreenPreview() {
        HockeyAPPLiveTheme {
            WelcomeScreen(
                navController = rememberNavController(),
                welcomeMessage = "Welcome to the Ultimate Hockey Experience!",
                buttonText = "Start Your Journey"
            )
        }
    }
}