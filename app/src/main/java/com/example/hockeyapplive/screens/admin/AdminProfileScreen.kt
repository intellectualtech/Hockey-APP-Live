package com.example.hockeyapplive.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
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
fun AdminProfileScreen(navController: NavController) {
    var name by remember { mutableStateOf("Admin User") }
    var email by remember { mutableStateOf("admin@hockey.com") }
    var contact by remember { mutableStateOf("+264-81-123-4567") }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_today), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
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
                    onClick = { navController.navigate("manage_players") }
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
                    selected = true,
                    onClick = { navController.navigate("admin_profile") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Admin Profile", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            OutlinedTextField(
                value = contact,
                onValueChange = { contact = it },
                label = { Text("Contact") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )

            Button(
                onClick = {
                    println("Profile saved: Name=$name, Email=$email, Contact=$contact")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Save")
            }

            Button(
                onClick = { navController.navigate("admin_dashboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}