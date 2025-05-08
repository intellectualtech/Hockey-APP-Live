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
fun ManageEventsScreen(navController: NavController) {
    val events = remember {
        mutableStateOf(listOf(
            Event("Windhoek HC VS Odibo HC", "MAR 05", "Recife, Brazil"),
            Event("Alicia Keys", "MAR 05", "Olinda, Brazil"),
            Event("Owan Tournament", "Ongoing", "Ohangwena, Odibol"),
            Event("Friendly", "Ongoing", "Okahandja")
        ))
    }
    val (newTitle, setNewTitle) = remember { mutableStateOf("") }
    val (newDate, setNewDate) = remember { mutableStateOf("") }
    val (newLocation, setNewLocation) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Manage Events", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = newTitle,
            onValueChange = setNewTitle,
            label = { Text("Event Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = newDate,
            onValueChange = setNewDate,
            label = { Text("Event Date") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        OutlinedTextField(
            value = newLocation,
            onValueChange = setNewLocation,
            label = { Text("Event Location") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Button(
            onClick = {
                if (newTitle.isNotBlank() && newDate.isNotBlank() && newLocation.isNotBlank()) {
                    events.value = events.value + Event(newTitle, newDate, newLocation)
                    setNewTitle("")
                    setNewDate("")
                    setNewLocation("")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Add Event")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(events.value) { event ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = event.title, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                        Text(text = "${event.date}, ${event.location}", style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                    }
                    Button(
                        onClick = {
                            events.value = events.value.filter { it != event }
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

data class Event(val title: String, val date: String, val location: String)