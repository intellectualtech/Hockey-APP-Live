package com.example.hockeyapplive.screens

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Event
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Custom Color Scheme
private val LightNavyBlue = Color(0xFF4A6FA5)
private val DarkNavyBlue = Color(0xFF2C4A73)
private val LightBlue = Color(0xFFE3F2FD)
private val AccentBlue = Color(0xFF1976D2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(navController: NavController) {
    // Initialize DatabaseHelper using the current context
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    // State management for events, loading, and error
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    // Function to load events from the database
    fun loadEvents() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                events = dbHelper.getAllEvents()
            } catch (e: Exception) {
                errorMessage = "Failed to load events: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    // Load events when screen is first displayed
    LaunchedEffect(Unit) {
        loadEvents()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Hockey Events",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LightNavyBlue
                ),
                actions = {
                    IconButton(
                        onClick = { loadEvents() }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            FooterNavigation(navController)
        },
        containerColor = Color(0xFFF8F9FA)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    LoadingScreen()
                }
                errorMessage != null -> {
                    ErrorScreen(
                        message = errorMessage!!,
                        onRetry = { loadEvents() }
                    )
                }
                events.isEmpty() -> {
                    EmptyEventsScreen()
                }
                else -> {
                    EventsList(
                        events = events,
                        onEventClick = { event ->
                            navController.navigate("event_details/${event.eventId}")
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EventsList(
    events: List<Event>,
    onEventClick: (Event) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Event Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = DarkNavyBlue
                    )

                    if (!event.eventDescription.isNullOrBlank()) {
                        Text(
                            text = event.eventDescription,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                // Event Type Badge
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = LightBlue,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = event.eventType,
                        style = MaterialTheme.typography.labelMedium,
                        color = AccentBlue,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Event Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        contentDescription = "Date",
                        tint = LightNavyBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatDateTime(event.eventDateTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FooterNavigation(navController: NavController) {
    NavigationBar(
        containerColor = LightNavyBlue,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Home,
                    contentDescription = "Home",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "Home",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = { navController.navigate("onbonmbarding") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = DarkNavyBlue
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Event,
                    contentDescription = "Events",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "Events",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = true,
            onClick = { },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = DarkNavyBlue
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Sports,
                    contentDescription = "Teams",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "Teams",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = { navController.navigate("manage_team_players?teamId={teamId}") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = DarkNavyBlue
            )
        )

        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Settings,
                    contentDescription = "Admin",
                    tint = Color.White
                )
            },
            label = {
                Text(
                    "profile",
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall
                )
            },
            selected = false,
            onClick = { navController.navigate("settings") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                selectedTextColor = Color.White,
                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                indicatorColor = DarkNavyBlue
            )
        )
    }
}

@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                color = LightNavyBlue,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading events...",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.Error,
                contentDescription = "Error",
                tint = Color.Red,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Error Loading Events",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DarkNavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = LightNavyBlue
                )
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again", color = Color.White)
            }
        }
    }
}

@Composable
private fun EmptyEventsScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                Icons.Default.EventBusy,
                contentDescription = "No Events",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Events Available",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = DarkNavyBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check back later for upcoming hockey events",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

private fun formatDateTime(dateTimeString: String): String {
    return try {
        val dateTime = LocalDateTime.parse(dateTimeString)
        val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' HH:mm")
        dateTime.format(formatter)
    } catch (e: Exception) {
        dateTimeString // Return original if parsing fails
    }
}