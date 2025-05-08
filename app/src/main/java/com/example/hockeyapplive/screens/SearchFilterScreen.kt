package com.example.hockeyapplive.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun SearchFilterScreen(navController: NavController) {
    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
    val filterOptions = listOf("All", "Teams", "Events", "Players")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Search & Filter", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = searchQuery,
            onValueChange = setSearchQuery,
            label = { Text("Search") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            filterOptions.forEach { option ->
                Button(onClick = { /* Apply filter */ }) {
                    Text(option)
                }
            }
        }

        Button(
            onClick = { navController.navigate("events") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            Text("View Events")
        }
    }
}