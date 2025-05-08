package com.example.hockeyapplive.screens



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
fun ChatScreen(navController: NavController) {
    val clubs = listOf(
        "Windhoek Hockey Club" to "Stand up for what you believe in",
        "Odimbo Hockey Club" to "One day you're seventeen",
        "Okahandja Hockey Club" to "Some people are a little different. I think that's cool.",
        "Ongwediva Hockey Club" to "Last .",
        "Omundja hockey club" to "Meet me at the Rivercourt",
        "Rehobot Hockey club" to "In your life, you're gonna go to some great places, and do some wonderful things.",
        "Olupe hockey club" to "Every song ends, is that any reason not to enjoy the music?"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(text = "Chats", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium)

        LazyColumn {
            items(clubs) { (club, message) ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(text = club, style = androidx.compose.material3.MaterialTheme.typography.titleMedium)
                    Text(text = message, style = androidx.compose.material3.MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Button(
            onClick = { navController.navigate("feedback") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Go to Feedback")
        }
    }
}