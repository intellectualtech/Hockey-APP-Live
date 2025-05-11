package com.example.hockeyapplive.screens.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.R
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.RectangleShape

@Composable
fun AdminDashboardScreen(navController: NavController) {
    var pendingRegistrations by remember { mutableStateOf(listOf("user1@example.com", "user2@example.com", "user3@example.com")) }
    var approvedRegistrations by remember { mutableStateOf(listOf<String>()) }
    var rejectedRegistrations by remember { mutableStateOf(listOf<String>()) }
    var totalTeams by remember { mutableStateOf(15) } // Sample data
    var totalCoaches by remember { mutableStateOf(10) } // Sample data
    var totalPlayers by remember { mutableStateOf(50) } // Sample data
    var selectedRegistration by remember { mutableStateOf<String?>(null) }
    var viewedRegistrations by remember { mutableStateOf(setOf<String>()) }
    var showErrorPopup by remember { mutableStateOf(false) }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navigationItems = listOf(
        Triple("Home", android.R.drawable.ic_menu_today) { navController.navigate("admin_dashboard") },
        Triple("Teams", android.R.drawable.ic_menu_preferences) { navController.navigate("manage_teams") },
        Triple("Players", android.R.drawable.ic_menu_myplaces) { navController.navigate("players") },
        Triple("Events", android.R.drawable.ic_menu_agenda) { navController.navigate("manage_events") },
        Triple("Admin Settings", android.R.drawable.ic_menu_manage) { navController.navigate("admin_settings_screen") }
    )

    // Popup Dialog for Submitted Details
    if (selectedRegistration != null) {
        AlertDialog(
            onDismissRequest = {
                selectedRegistration?.let {
                    viewedRegistrations = viewedRegistrations + it
                }
                selectedRegistration = null
            },
            title = { Text("Registration Details for $selectedRegistration") },
            text = {
                Column {
                    Text("Email: $selectedRegistration")
                    Text("Name: ${selectedRegistration?.split("@")?.get(0) ?: "Unknown"}") // Extract name from email as placeholder
                    Text("Status: Pending") // Placeholder data
                }
            },
            confirmButton = {
                Button(onClick = {
                    selectedRegistration?.let {
                        viewedRegistrations = viewedRegistrations + it
                    }
                    selectedRegistration = null
                }) {
                    Text("Close")
                }
            }
        )
    }

    // Error Popup for Attempting to Approve/Reject Without Viewing Details
    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { showErrorPopup = false },
            title = { Text("Action Required") },
            text = { Text("Please view the full details first.") },
            confirmButton = {
                Button(onClick = { showErrorPopup = false }) {
                    Text("OK")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape // Optional: Remove rounded corners for a flat edge
            ) {
                Spacer(modifier = Modifier.padding(32.dp))
                navigationItems.forEach { (label, icon, onClick) ->
                    NavigationDrawerItem(
                        icon = { Icon(painter = painterResource(id = icon), contentDescription = label) },
                        label = { Text(label) },
                        selected = false,
                        onClick = {
                            onClick()
                            scope.launch { drawerState.close() }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_today), contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { navController.navigate("admin_dashboard") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_preferences), contentDescription = "Teams") },
                        label = { Text("Teams") },
                        selected = false,
                        onClick = { navController.navigate("manage_teams") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = "Players") },
                        label = { Text("Players") },
                        selected = false,
                        onClick = { navController.navigate("players") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_agenda), contentDescription = "Events") },
                        label = { Text("Events") },
                        selected = false,
                        onClick = { navController.navigate("manage_events") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = painterResource(id = android.R.drawable.ic_menu_manage), contentDescription = "Admin Settings") },
                        label = { Text("Admin Settings") },
                        selected = false,
                        onClick = { navController.navigate("admin_settings_screen") }
                    )
                }
            },
            topBar = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Spacer(modifier = Modifier.width(48.dp)) // Placeholder for symmetry
                    Spacer(modifier = Modifier.width(48.dp)) // Placeholder for symmetry
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { scope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Default.Menu,
                            contentDescription = "Toggle Side Nav"
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Admin Dashboard",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(id = R.drawable.logo11), // Placeholder image
                        contentDescription = "Admin Dashboard Image",
                        modifier = Modifier
                            .width(130.dp)
                            .padding(top = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Cards placed before Pending Registrations
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6A0DAD)),
                        shape = RectangleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Teams",
                                color = Color.White
                            )
                            Text(
                                text = totalTeams.toString(),
                                color = Color.White
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6A0DAD)),
                        shape = RectangleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Coaches",
                                color = Color.White
                            )
                            Text(
                                text = totalCoaches.toString(),
                                color = Color.White
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .padding(horizontal = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF6A0DAD)),
                        shape = RectangleShape
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Players",
                                color = Color.White
                            )
                            Text(
                                text = totalPlayers.toString(),
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                // Pending Registrations Section
                Text(
                    text = "Pending Registrations",
                    style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp)
                )
                pendingRegistrations.forEach { registration ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { selectedRegistration = registration }
                        ) {
                            Text(text = registration)
                        }
                        Button(
                            enabled = viewedRegistrations.contains(registration),
                            onClick = {
                                if (viewedRegistrations.contains(registration)) {
                                    approvedRegistrations = approvedRegistrations + registration
                                    pendingRegistrations = pendingRegistrations - registration
                                    println("Approved: $registration")
                                } else {
                                    showErrorPopup = true
                                }
                            }
                        ) {
                            Text("Approve")
                        }
                        Button(
                            enabled = viewedRegistrations.contains(registration),
                            onClick = {
                                if (viewedRegistrations.contains(registration)) {
                                    rejectedRegistrations = rejectedRegistrations + registration
                                    pendingRegistrations = pendingRegistrations - registration
                                    println("Rejected: $registration")
                                } else {
                                    showErrorPopup = true
                                }
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                }
                Text(
                    text = "Approved Registrations: ${approvedRegistrations.joinToString()}",
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "Rejected Registrations: ${rejectedRegistrations.joinToString()}",
                    modifier = Modifier.padding(top = 8.dp)
                )

                Button(
                    onClick = { navController.navigate("manage_teams") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Manage Teams")
                }

                Button(
                    onClick = { navController.navigate("manage_events") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Manage Events")
                }

                Button(
                    onClick = { navController.navigate("manage_feedback") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Manage Feedback")
                }
            }
        }
    }
}