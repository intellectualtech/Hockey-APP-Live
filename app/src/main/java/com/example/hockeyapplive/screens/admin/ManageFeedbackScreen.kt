package com.example.hockeyapplive.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun ManageFeedbackScreen(navController: NavController) {
    val feedbackList = remember {
        mutableStateOf(listOf(
            Feedback("User1", "Looks good", "Could have more components"),
            Feedback("User2", "Easy to use", "Only English")
        ))
    }

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
                    selected = false,
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
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "Manage Feedback", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(feedbackList.value) { feedback ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "User: ${feedback.user}", style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                            Text(text = "Likes: ${feedback.likes}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                            Text(text = "Improvements: ${feedback.improvements}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                        }
                        Button(
                            onClick = {
                                feedbackList.value = feedbackList.value.filter { it != feedback }
                            }
                        ) {
                            Text("Resolve")
                        }
                    }
                }
            }

            Button(
                onClick = { navController.navigate("admin_dashboard") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Back to Dashboard")
            }
        }
    }
}

data class Feedback(val user: String, val likes: String, val improvements: String)