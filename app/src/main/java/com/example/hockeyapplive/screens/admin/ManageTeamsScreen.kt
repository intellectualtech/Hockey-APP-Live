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
fun ManageTeamsScreen(navController: NavController) {
    val teams = remember {
        mutableStateOf(listOf(
            "Windhoek Club", "Bongoloo Hockey Club", "Nust Hockey Club",
            "Okahandja Hockey Club", "Swakop Hockey Club", "Omupe Hockey Club",
            "Ongo Hockey Club"
        ))
    }
    val (newTeam, setNewTeam) = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Manage Teams", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = newTeam,
            onValueChange = setNewTeam,
            label = { Text("Add New Team") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        Button(
            onClick = {
                if (newTeam.isNotBlank()) {
                    teams.value = teams.value + newTeam
                    setNewTeam("")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Add Team")
        }

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(teams.value) { team ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = team, style = androidx.compose.material3.MaterialTheme.typography.bodyLarge)
                    Button(
                        onClick = {
                            teams.value = teams.value.filter { it != team }
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