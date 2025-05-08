package com.example.hockeyapplive.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SettingsScreen(navController: NavController) {
    val settingsOptions = listOf(
        "Saved Messages", "Recent Game", "Devices", "Notifications",
        "Appearance", "Language", "Privacy & Security", "Storage"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Settings", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Text(text = "Lucas Scott", style = androidx.compose.material3.MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
        Text(text = "@lucasscott3", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)

        settingsOptions.forEach { option ->
            Button(
                onClick = { /* Handle settings option click */ },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(option)
            }
        }

        Button(
            onClick = { navController.navigate("chat") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go to Chats")
        }
    }
}