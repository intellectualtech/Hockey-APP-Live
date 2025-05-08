package com.example.hockeyapplive.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun EventsScreen(navController: NavController) {
    val events = listOf(
        Event("Windhoek HC VS Odibo HC", "MAR 05", "Recife, Brazil"),
        Event("Alicia Keys", "MAR 05", "Olinda, Brazil"),
        Event("Owan Tournament", "Ongoing", "Ohangwena, Odibol"),
        Event("Friendly", "Ongoing", "Okahandja")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Events", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(events) { event ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = event.title, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(text = event.date, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    Text(text = event.location, style = androidx.compose.material3.MaterialTheme.typography.bodySmall)
                    Button(
                        onClick = { /* Handle buy tickets or view details */ },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text("View Details")
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
            Text("Admin Dashboard")
        }
    }
}

data class Event(val title: String, val date: String, val location: String)