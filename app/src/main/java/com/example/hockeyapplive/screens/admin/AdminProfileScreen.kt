package com.example.hockeyapplive.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.TextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.User
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun AdminProfileScreen(navController: NavController, userId: Int) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var user by remember { mutableStateOf<User?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Fetch user details using the provided userId
    LaunchedEffect(userId) {
        scope.launch {
            try {
                if (userId <= 0) {
                    message = "Invalid user ID."
                    return@launch
                }
                user = dbHelper.getUserById(userId)
                if (user == null) {
                    message = "User not found."
                }
            } catch (e: Exception) {
                message = "Failed to load profile: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_today), contentDescription = "Home") },
                    label = { Text("Home") },
                    selected = false,
                    onClick = { navController.navigate("admin_dashboard") }
                )
                NavigationBarItem(
                    icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_preferences), contentDescription = "Teams") },
                    label = { Text("Teams") },
                    selected = false,
                    onClick = { navController.navigate("manage_teams") }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = "Players") },
                    label = { Text("Players") },
                    selected = false,
                    onClick = { navController.navigate("manage_players") }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = android.R.drawable.ic_menu_agenda), contentDescription = "Events") },
                    label = { Text("Events") },
                    selected = false,
                    onClick = { navController.navigate("manage_events") }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(id = android.R.drawable.ic_menu_manage), contentDescription = "Profile") },
                    label = { Text("Profile") },
                    selected = true,
                    onClick = { navController.navigate("admin_profile/$userId") }
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Admin Profile",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp))
            } else {
                message?.let {
                    Text(
                        text = it,
                        color = if (it.startsWith("Failed") || it.contains("not found") || it.contains("Invalid")) {
                            androidx.compose.ui.graphics.Color.Red
                        } else {
                            androidx.compose.ui.graphics.Color.Green
                        },
                        modifier = Modifier.padding(top = 16.dp),
                        textAlign = TextAlign.Center
                    )
                }

                user?.let { currentUser ->
                    var name by remember { mutableStateOf(currentUser.fullName) }
                    var email by remember { mutableStateOf(currentUser.email) }
                    var contact by remember { mutableStateOf(currentUser.contactNumber ?: "") }

                    TextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        maxLines = 1
                    )

                    TextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        maxLines = 1
                    )

                    TextField(
                        value = contact.toString(),
                        onValueChange = { newValue -> contact =
                            (newValue.toIntOrNull() ?: 0) as Any
                        },
                        label = { Text("Contact") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        maxLines = 1
                    )

                    Button(
                        onClick = {
                            scope.launch {
                                try {
                                    if (name.isBlank()) {
                                        message = "Name cannot be empty"
                                        return@launch
                                    }
                                    if (email.isBlank() || !email.contains("@")) {
                                        message = "Invalid email"
                                        return@launch
                                    }
                                    if (contact.toString().isNotBlank() && contact.toString().length < 7) {
                                        message = "Contact number must be at least 7 characters"
                                        return@launch
                                    }

                                    isSaving = true
                                    message = null

                                    val success = contact.toString().takeIf { it.isNotEmpty() }?.let {
                                        dbHelper.updateUser(
                                            userId = currentUser.userID,
                                            fullName = name,
                                            email = email,
                                            contactNumber = it.toIntOrNull() ?: 0
                                        )
                                    }

                                } catch (e: Exception) {
                                    message = "Failed to save profile: ${e.message}"
                                } finally {
                                    isSaving = false
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        enabled = !isSaving
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 8.dp),
                                color = androidx.compose.ui.graphics.Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Filled.Save,
                                contentDescription = "Save",
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                        Text("Save")
                    }

                    Button(
                        onClick = { navController.navigate("admin_dashboard") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text("Back to Dashboard")
                    }
                }
            }
        }
    }

    // Clean up database helper
    androidx.compose.runtime.DisposableEffect(Unit) {
        onDispose {
            dbHelper.close()
        }
    }
}