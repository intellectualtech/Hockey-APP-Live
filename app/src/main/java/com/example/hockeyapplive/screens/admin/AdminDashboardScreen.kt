package com.example.hockeyapplive.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var pendingRegistrations by remember { mutableStateOf(listOf("user1", "user2", "user3")) }
    var approvedRegistrations by remember { mutableStateOf(listOf<String>()) }
    var rejectedRegistrations by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_today), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = true,
                    onClick = { navController.navigate("admin_dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_preferences), contentDescription = "Teams") },
                    label = { Text("Teams") },
                    selected = false,
                    onClick = { navController.navigate("manage_teams") }
                )
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = "Players") },
                    label = { Text("Players") },
                    selected = false,
                    onClick = { navController.navigate("players") }
                )
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_agenda), contentDescription = "Events") },
                    label = { Text("Events") },
                    selected = false,
                    onClick = { navController.navigate("manage_events") }
                )
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_manage), contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = false,
                    onClick = { navController.navigate("profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Dashboard",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            Text(
                text = "Pending Registrations",
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 16.dp)
            )

            pendingRegistrations.forEach { registration ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Coach: $registration")
                    Button(
                        onClick = {
                            approvedRegistrations = approvedRegistrations + registration
                            pendingRegistrations = pendingRegistrations - registration
                            println("Approved: $registration")
                        }
                    ) {
                        Text("Approve")
                    }
                    Button(
                        onClick = {
                            rejectedRegistrations = rejectedRegistrations + registration
                            pendingRegistrations = pendingRegistrations - registration
                            println("Rejected: $registration")
                        }
                    ) {
                        Text("Reject")
                    }
                }
            }

            Text(
                text = "Approved Registrations: ${approvedRegistrations.joinToString()}",
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = "Rejected Registrations: ${rejectedRegistrations.joinToString()}",
                modifier = Modifier.padding(top = 8.dp)
            )

            Button(
                onClick = { navController.navigate("manage_teams") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Manage Teams")
            }

            Button(
                onClick = { navController.navigate("manage_events") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Manage Events")
            }

            Button(
                onClick = { navController.navigate("manage_feedback") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Manage Feedback")
            }

            /*Button(
                onClick = { navController.navigate("settings") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text("Back to Settings")
            }*/
        }
    }
}