package com.example.hockeyapplive.screens.admin

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.db.Event

// Professional navy blue color scheme
private val NavyBlue = Color(0xFF0A2463)
private val LightNavyBlue = Color(0xFF1E3A8A)
private val AccentBlue = Color(0xFF3E92CC)
private val LightGray = Color(0xFFF5F5F5)
private val DarkText = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(navController: NavController, context: Context) {
    val dbHelper = remember { DatabaseHelper(context) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var newDate by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Custom theme colors for this screen
    val customColorScheme = darkColorScheme(
        primary = AccentBlue,
        onPrimary = Color.White,
        secondary = LightNavyBlue,
        background = Color.White,
        surface = Color.White,
        onBackground = DarkText,
        onSurface = DarkText
    )

    // Fetch events when the screen is composed
    LaunchedEffect(Unit) {
        events = dbHelper.getAllEvents()
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manage Events", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyBlue,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = NavyBlue,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = false,
                        onClick = { navController.navigate("admin_dashboard") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Groups, contentDescription = "Teams") },
                        label = { Text("Teams") },
                        selected = false,
                        onClick = { navController.navigate("manage_teams") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.SportsHockey, contentDescription = "Players") },
                        label = { Text("Players") },
                        selected = false,
                        onClick = { navController.navigate("manage_players") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.EventNote, contentDescription = "Events") },
                        label = { Text("Events") },
                        selected = true,
                        onClick = { navController.navigate("manage_events") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { navController.navigate("admin_profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
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
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGray)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Add New Event",
                            style = MaterialTheme.typography.titleLarge,
                            color = NavyBlue,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Event Title") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null && newTitle.isBlank(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = NavyBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("Event Description") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null && newDescription.isBlank(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = NavyBlue
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newDate,
                            onValueChange = { newDate = it },
                            label = { Text("Event Date & Time (YYYY-MM-DD HH:MM:SS)") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null && newDate.isBlank(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = NavyBlue
                            )
                        )

                        errorMessage?.let {
                            Text(
                                text = it,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (newTitle.isBlank() || newDescription.isBlank() || newDate.isBlank()) {
                                    errorMessage = "All fields are required"
                                } else {
                                    try {
                                        val currentUserId = 1 // Assuming the admin user ID is 1
                                        dbHelper.insertEvent(newTitle, newDescription, newDate, "Other", currentUserId)
                                        events = dbHelper.getAllEvents()
                                        newTitle = ""
                                        newDescription = ""
                                        newDate = ""
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to add event: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Add Event")
                            }
                        }
                    }
                }

                Text(
                    text = "Existing Events",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (events.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No events found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn {
                        items(events) { event ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = event.eventName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NavyBlue
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = event.eventDateTime,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = Color.Gray
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = event.eventDescription,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    OutlinedButton(
                                        onClick = {
                                            try {
                                                dbHelper.deleteEvent(event.eventId)
                                                events = dbHelper.getAllEvents()
                                            } catch (e: Exception) {
                                                errorMessage = "Failed to delete event: ${e.message}"
                                            }
                                        },
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = Color.Red
                                        ),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(
                                            brush = SolidColor(Color.Red.copy(alpha = 0.5f))
                                        )
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Filled.Delete,
                                                contentDescription = "Delete",
                                                modifier = Modifier.size(18.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            dbHelper.close()
        }
    }
}