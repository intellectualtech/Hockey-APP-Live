package com.example.hockeyapplive.screens.admin

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminSettingsScreen(navController: NavController) {
    var showManageUsersDialog by remember { mutableStateOf(false) }
    var showAddAnnouncementDialog by remember { mutableStateOf(false) }
    var showAppStatsDialog by remember { mutableStateOf(false) }

    val adminOptions = listOf(
        AdminOption("Manage Users", Icons.Default.People, onClick = { showManageUsersDialog = true }),
        AdminOption("Manage Events", Icons.Default.Event, onClick = { navController.navigate("manage_events") }),
        AdminOption("Manage Teams", Icons.Default.Group, onClick = { navController.navigate("manage_teams") }),
        AdminOption("Add Announcement", Icons.Default.Announcement, onClick = { showAddAnnouncementDialog = true }),
        AdminOption("App Statistics", Icons.Default.BarChart, onClick = { showAppStatsDialog = true }),
        AdminOption("Admin Settings", Icons.Default.Settings, onClick = { /* Navigate to admin-specific settings */ })
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Admin Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Admin Controls",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            items(adminOptions) { option ->
                AdminOptionRow(option)
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                // Back to Dashboard Button
                OutlinedButton(
                    onClick = { navController.navigate("admin_dashboard") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Back to Dashboard",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back to Dashboard")
                    }
                }
            }

            item {
                // Logout Button
                OutlinedButton(
                    onClick = {
                        // Handle logout (e.g., clear user session)
                        navController.navigate("login_screen") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout")
                    }
                }
            }
        }
    }

    // Manage Users Dialog
    if (showManageUsersDialog) {
        AlertDialog(
            onDismissRequest = { showManageUsersDialog = false },
            title = { Text("Manage Users") },
            text = {
                Column {
                    Text("User List (Mock Data):")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("1. Lucas Scott (@lucasscott3) - Active")
                    Text("2. Jane Doe (@janedoe) - Inactive")
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { /* Navigate to user management details */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("View Details")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showManageUsersDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(onClick = { showManageUsersDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Announcement Dialog
    if (showAddAnnouncementDialog) {
        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddAnnouncementDialog = false },
            title = { Text("Add Announcement") },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Save announcement (e.g., add to database)
                        showAddAnnouncementDialog = false
                    },
                    enabled = title.isNotBlank() && description.isNotBlank()
                ) {
                    Text("Publish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAnnouncementDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // App Statistics Dialog
    if (showAppStatsDialog) {
        AlertDialog(
            onDismissRequest = { showAppStatsDialog = false },
            title = { Text("App Statistics") },
            text = {
                Column {
                    Text("Total Users: 150")
                    Text("Active Events: 5")
                    Text("Teams Registered: 20")
                    Text("Announcements Posted: 10")
                }
            },
            confirmButton = {
                Button(onClick = { showAppStatsDialog = false }) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAppStatsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

data class AdminOption(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun AdminOptionRow(option: AdminOption) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { option.onClick() }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = option.icon,
                    contentDescription = option.title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = option.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open ${option.title}",
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}