package com.example.hockeyapplive

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.hockeyapplive.screens.ChatScreen
import com.example.hockeyapplive.screens.ManageTeamPlayerScreen
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
import com.example.hockeyapplive.screens.admin.AdminProfileScreen
import com.example.hockeyapplive.screens.admin.ManageEventsScreen
import com.example.hockeyapplive.screens.admin.ManageFeedbackScreen
import com.example.hockeyapplive.screens.admin.ManagePlayersScreen
import com.example.hockeyapplive.screens.admin.ManageTeamsScreen
import com.example.hockeyapplive.ui.theme.HockeyAPPLiveTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun AppNavigation() {
        val navController = rememberNavController()

        NavHost(navController = navController, startDestination = "splash") {
            composable("splash") { SplashScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController, context = this@MainActivity) }
            composable("login") { LoginScreen(navController) }
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
                val teamName = backStackEntry.arguments?.getString("teamName") ?: ""
                PlayerRegistrationScreen(
                    navController = navController,
                    teamName = teamName
                )
            }

            composable("admin_dashboard") { AdminDashboardScreen(navController, context = this@MainActivity) }
            composable("manage_teams") { ManageTeamsScreen(navController = navController, context = this@MainActivity) }
            composable("manage_events") { ManageEventsScreen(navController, context = this@MainActivity) }
            composable("manage_feedback") { ManageFeedbackScreen(navController) }
            composable(
                "manage_players?teamName={teamName}",
                arguments = listOf(
                    navArgument("teamName") {
                        type = NavType.StringType
                        defaultValue = ""
                    }
                )
            ) { navBackStackEntry ->
                val context = LocalContext.current
                ManagePlayersScreen(
                    navController = navController,
                    navBackStackEntry = navBackStackEntry,
                    context = context
                )
            }

            composable(
                "manage_team_players?teamId={teamId}",
                arguments = listOf(
                    navArgument("teamId") {
                        type = NavType.IntType
                        defaultValue = -1 // Default to -1 if not provided
                    }
                )
            ) { backStackEntry ->
                val teamId = backStackEntry.arguments?.getInt("teamId") ?: -1
                ManageTeamPlayerScreen(
                    navController = navController,
                    //teamId = teamId
                )
            }

            composable(
                "admin_profile/{userId}",
                arguments = listOf(
                    navArgument("userId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0
                AdminProfileScreen(navController = navController, userId = userId)
            }
        }
    }
}