package com.example.hockeyapplive.screens


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun PlayerRegistrationScreen(navController: NavController, teamName: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Player Registration for $teamName",
            style = MaterialTheme.typography.headlineMedium
        )
        Button(
            onClick = { navController.navigate("welcome") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Back to Welcome")
        }
    }
}