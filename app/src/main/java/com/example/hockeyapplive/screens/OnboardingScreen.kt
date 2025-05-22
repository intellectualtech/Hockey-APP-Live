package com.example.hockeyapplive.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.R
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Event
import kotlinx.coroutines.launch

// Define professional light navy blue color palette
object HockeyAppTheme {
    val LightNavyBlue = Color(0xFF3D5A80)
    val LighterNavyBlue = Color(0xFF98C1D9)
    val AccentBlue = Color(0xFF2B4570)
    val White = Color(0xFFFFFFFF)
    val LightGray = Color(0xFFF5F5F5)
    val TextColor = Color(0xFF1D3557)
    val CardBackground = Color(0xFFF8F9FA)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(navController: NavController, context: Context) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val dbHelper = remember { DatabaseHelper(context) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Apply custom color scheme
    val colorScheme = darkColorScheme(
        primary = HockeyAppTheme.LightNavyBlue,
        secondary = HockeyAppTheme.LighterNavyBlue,
        tertiary = HockeyAppTheme.AccentBlue,
        background = HockeyAppTheme.White,
        surface = HockeyAppTheme.CardBackground,
        onPrimary = HockeyAppTheme.White,
        onSecondary = HockeyAppTheme.TextColor,
        onBackground = HockeyAppTheme.TextColor,
        onSurface = HockeyAppTheme.TextColor
    )

    // Fetch events from the database
    fun fetchEvents(): List<Event> {
        return dbHelper.getAllEvents()
    }

    LaunchedEffect(Unit) {
        try {
            events = fetchEvents()
        } catch (e: Exception) {
            errorMessage = "Failed to load events: ${e.message}"
        }
    }

    MaterialTheme(colorScheme = colorScheme) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.width(300.dp),
                    drawerContainerColor = HockeyAppTheme.White
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Header with logo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(HockeyAppTheme.LightNavyBlue, RoundedCornerShape(8.dp))
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Namibia Hockey Union",
                                style = MaterialTheme.typography.headlineMedium,
                                color = HockeyAppTheme.White,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        NavigationDrawerItem(
                            label = { Text("Home") },
                            selected = true,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("onboarding")
                            },
                            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Teams") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("team_registration")
                            },
                            icon = { Icon(Icons.Filled.Group, contentDescription = "Teams") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Players") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("teamName" )
                            },
                            icon = { Icon(Icons.Filled.Person, contentDescription = "Players") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Events") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("events")
                            },
                            icon = { Icon(Icons.Filled.Event, contentDescription = "Events") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Feedback") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("manage_feedback")
                            },
                            icon = { Icon(Icons.Filled.Feedback, contentDescription = "Feedback") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Profile") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("settings")
                            },
                            icon = { Icon(Icons.Filled.AccountCircle, contentDescription = "Profile") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )
                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            selected = false,
                            onClick = {
                                scope.launch { drawerState.close() }
                                navController.navigate("settings")
                            },
                            icon = { Icon(Icons.Filled.Settings, contentDescription = "Settings") },
                            colors = NavigationDrawerItemDefaults.colors(
                                selectedContainerColor = HockeyAppTheme.LighterNavyBlue,
                                unselectedContainerColor = HockeyAppTheme.White,
                                selectedIconColor = HockeyAppTheme.TextColor,
                                unselectedIconColor = HockeyAppTheme.TextColor.copy(alpha = 0.7f)
                            )
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Footer in drawer
                        Text(
                            text = "© 2025 Namibia Hockey Union",
                            style = MaterialTheme.typography.bodySmall,
                            color = HockeyAppTheme.TextColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                "Namibia Hockey Union",
                                color = HockeyAppTheme.White,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Menu",
                                    tint = HockeyAppTheme.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.mediumTopAppBarColors(
                            containerColor = HockeyAppTheme.LightNavyBlue
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = HockeyAppTheme.LightNavyBlue,
                        contentColor = HockeyAppTheme.White
                    ) {
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home", tint = HockeyAppTheme.White) },
                            label = { Text("Home", color = HockeyAppTheme.White) },
                            selected = true,
                            onClick = { navController.navigate("onboarding") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Group, contentDescription = "Teams", tint = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            label = { Text("Teams", color = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            selected = false,
                            onClick = { navController.navigate("team_registration") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Players", tint = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            label = { Text("Players", color = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            selected = false,
                            onClick = { navController.navigate("playerRegistration") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Event, contentDescription = "Events", tint = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            label = { Text("Events", color = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            selected = false,
                            onClick = { navController.navigate("events") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.Feedback, contentDescription = "Feedback", tint = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            label = { Text("Feedback", color = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            selected = false,
                            onClick = { navController.navigate("feedback") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
                        )
                        NavigationBarItem(
                            icon = { Icon(imageVector = Icons.Default.AccountCircle, contentDescription = "Profile", tint = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            label = { Text("Profile", color = HockeyAppTheme.White.copy(alpha = 0.7f)) },
                            selected = false,
                            onClick = { navController.navigate("login") },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = HockeyAppTheme.White,
                                selectedTextColor = HockeyAppTheme.White,
                                indicatorColor = HockeyAppTheme.AccentBlue,
                                unselectedIconColor = HockeyAppTheme.White.copy(alpha = 0.7f),
                                unselectedTextColor = HockeyAppTheme.White.copy(alpha = 0.7f)
                            )
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
                    // Quick Action Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(
                            onClick = { navController.navigate("playerRegistration") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HockeyAppTheme.LightNavyBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(end = 8.dp)
                        ) {
                            Text("Player Registration")
                        }
                        Button(
                            onClick = { navController.navigate("events") }, // Fallback to events screen
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HockeyAppTheme.LightNavyBlue
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).padding(start = 8.dp)
                        ) {
                            Text("Event Entry")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Events Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Upcoming Events",
                            style = MaterialTheme.typography.titleLarge,
                            color = HockeyAppTheme.TextColor,
                            fontWeight = FontWeight.Bold
                        )

                        TextButton(onClick = { navController.navigate("events") }) {
                            Text(
                                text = "View All",
                                color = HockeyAppTheme.LightNavyBlue
                            )
                        }
                    }

                    if (events.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No upcoming events found",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Gray
                            )
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            events.forEachIndexed { index, event ->
                                EventImageCard(
                                    imageRes = when (index % 4) {
                                        0 -> R.drawable.image1
                                        1 -> R.drawable.image2
                                        2 -> R.drawable.image3
                                        else -> R.drawable.image4
                                    },
                                    description = "${event.eventName}\n${event.eventDateTime}",
                                    onClick = { navController.navigate("events") }
                                )
                            }
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Announcements Section
                    Text(
                        text = "Latest Announcements",
                        style = MaterialTheme.typography.titleLarge,
                        color = HockeyAppTheme.TextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HockeyAppTheme.CardBackground
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Season Start",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HockeyAppTheme.LightNavyBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "The new hockey season starts on June 1st!",
                                    color = HockeyAppTheme.TextColor
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Event,
                                contentDescription = null,
                                tint = HockeyAppTheme.LightNavyBlue
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HockeyAppTheme.CardBackground
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "National Team Selection",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HockeyAppTheme.LightNavyBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "National team trials WILL BE held next month",
                                    color = HockeyAppTheme.TextColor
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = HockeyAppTheme.LightNavyBlue
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = HockeyAppTheme.CardBackground
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "New Rules Update",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = HockeyAppTheme.LightNavyBlue,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    "Check out the updated rules for the upcoming season",
                                    color = HockeyAppTheme.TextColor
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = HockeyAppTheme.LightNavyBlue
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Quick Links
                    Text(
                        text = "Quick Links",
                        style = MaterialTheme.typography.titleLarge,
                        color = HockeyAppTheme.TextColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.Start)
                    )

                    val contextForIntent = LocalContext.current
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://namibiahockey.org"))
                                contextForIntent.startActivity(intent)
                                println("Intent to open website succeeded")
                            } catch (e: Exception) {
                                errorMessage = "Failed to open website: ${e.message}"
                                println("Intent failed: ${e.message}")
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HockeyAppTheme.LightNavyBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Visit Namibia Hockey Website")
                    }

                    Button(
                        onClick = { navController.navigate("events") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HockeyAppTheme.LightNavyBlue
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("View Upcoming Events")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Footer
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = HockeyAppTheme.LightGray,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "© 2025 Namibia Hockey Union. All rights reserved.",
                            style = MaterialTheme.typography.bodySmall,
                            color = HockeyAppTheme.TextColor.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(
                            onClick = {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://namibiahockey.org"))
                                    contextForIntent.startActivity(intent)
                                    println("Footer Intent to open website succeeded")
                                } catch (e: Exception) {
                                    errorMessage = "Failed to open website: ${e.message}"
                                    println("Footer Intent failed: ${e.message}")
                                }
                            }
                        ) {
                            Text(
                                text = "Visit Namibia Hockey Website",
                                color = HockeyAppTheme.LightNavyBlue,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 8.dp)
                        )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventImageCard(imageRes: Int, description: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(250.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = HockeyAppTheme.CardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = description,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HockeyAppTheme.LightNavyBlue)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = HockeyAppTheme.White,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}