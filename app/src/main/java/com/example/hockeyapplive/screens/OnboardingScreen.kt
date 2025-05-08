package com.example.hockeyapplive.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun OnboardingScreen(navController: NavController) {
    val teams = listOf(
        "Windhoek Club", "Bongoloo Hockey Club", "Nust Hockey Club",
        "Okahandja Hockey Club", "Swakop Hockey Club", "Omupe Hockey Club",
        "Ongo Hockey Club"
    )
    val (selectedTeam, setSelectedTeam) = remember { mutableStateOf("") }
    val (expanded, setExpanded) = remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Personalise your experience", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)
        Text(text = "Choose your best Hockey Team", modifier = Modifier.padding(top = 8.dp))

        Button(onClick = { setExpanded(true) }) {
            Text(text = if (selectedTeam.isEmpty()) "Select Team" else selectedTeam)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { setExpanded(false) }
        ) {
            teams.forEach { team ->
                DropdownMenuItem(
                    text = { Text(team) },
                    onClick = {
                        setSelectedTeam(team)
                        setExpanded(false)
                    }
                )
            }
        }

        Button(
            onClick = { navController.navigate("login") },
            modifier = Modifier.padding(top = 16.dp),
            enabled = selectedTeam.isNotEmpty()
        ) {
            Text("Next")
        }
    }
}