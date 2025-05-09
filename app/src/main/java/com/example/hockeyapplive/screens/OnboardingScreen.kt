package com.example.hockeyapplive.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Menu",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    NavigationDrawerItem(
                        label = { Text("Home") },
                        selected = true,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("onboarding_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Teams") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("team_registration_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Players") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("player_registration_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Events") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("events_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Feedback") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("feedback_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Profile") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("login_screen")
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate("settings_screen")
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Namibia Hockey Union") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_today), contentDescription = "Home") },
                        label = { Text("Home") },
                        selected = true,
                        onClick = { navController.navigate("home") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_preferences), contentDescription = "Teams") },
                        label = { Text("Teams") },
                        selected = false,
                        onClick = { navController.navigate("team_registration_screen") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_myplaces), contentDescription = "Players") },
                        label = { Text("Players") },
                        selected = false,
                        onClick = { navController.navigate("player_registration_screen") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_agenda), contentDescription = "Events") },
                        label = { Text("Events") },
                        selected = false,
                        onClick = { navController.navigate("events_screen") }
                    )
                    NavigationBarItem(
                        icon = { Icon(painter = androidx.compose.ui.res.painterResource(id = android.R.drawable.ic_menu_manage), contentDescription = "Profile") },
                        label = { Text("Profile") },
                        selected = false,
                        onClick = { navController.navigate("profile_screen") }
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = { navController.navigate("register_team") }) {
                        Text("Other Team")
                    }
                    Button(onClick = { navController.navigate("register_player") }) {
                        Text("Register Player")
                    }
                    Button(onClick = { navController.navigate("event_entry") }) {
                        Text("Event Entry")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Latest Announcement",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Season Start", style = MaterialTheme.typography.titleMedium)
                            Text("The new hockey season starts on June 1st!")
                        }
                        Text("")
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("National Team Selection", style = MaterialTheme.typography.titleMedium)
                            Text("National team trials WILL BE held next month")
                        }
                        Text("")
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("New Rules Update", style = MaterialTheme.typography.titleMedium)
                            Text("Check out the updated rules for the upcoming season")
                        }
                        Text("")
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Quick Links",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.align(Alignment.Start)
                )

                Button(
                    onClick = { /* Navigate to website */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("Visit Namibia Hockey Website")
                }

                Button(
                    onClick = { navController.navigate("upcoming_events") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text("View Upcoming Events")
                }
            }
        }
    }
}