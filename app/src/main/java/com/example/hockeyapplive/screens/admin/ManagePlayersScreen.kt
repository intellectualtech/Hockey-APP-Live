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
fun ManagePlayersScreen(navController: NavController) {
    val players = remember {
        mutableStateOf(listOf(
            "John Doe", "Jane Smith", "Michael Brown",
            "Sarah Johnson", "David Lee", "Emily Davis",
            "Chris Wilson"
        ))
    }
    val (newPlayer, setNewPlayer) = remember { mutableStateOf("") }

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
                    selected = true,
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
            Text(text = "Manage Players", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

            OutlinedTextField(
                value = newPlayer,
                onValueChange = setNewPlayer,
                label = { Text("Add New Player") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            )

            Button(
                onClick = {
                    if (newPlayer.isNotBlank()) {
                        players.value = players.value + newPlayer
                        setNewPlayer("")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                Text("Add Player")
            }

            LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
                items(players.value) { player ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = player, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                        Button(
                            onClick = {
                                players.value = players.value.filter { it != player }
                            }
                        ) {
                            Text("Delete")
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