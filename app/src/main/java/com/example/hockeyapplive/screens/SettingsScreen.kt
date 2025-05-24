package com.example.hockeyapplive.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.R
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Team
import com.example.hockeyapplive.data.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }

    var coach by remember { mutableStateOf<User?>(null) }
    var team by remember { mutableStateOf<Team?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val loggedInUserId = 1

    val coroutineScope = rememberCoroutineScope()

    fun loadCoachAndTeam() {
        coroutineScope.launch {
            isLoading = true
            errorMessage = null
            try {
                coach = dbHelper.getUserById(loggedInUserId)
                coach?.let { user ->
                    team = dbHelper.getTeamByCoachId(user.userID)
                }
            } catch (e: Exception) {
                errorMessage = "Failed to load profile: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadCoachAndTeam()
    }

    var showEditProfileDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
                Button(
                    onClick = { loadCoachAndTeam() },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                                contentDescription = "Profile Picture",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = coach?.fullName ?: "Unknown Coach",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "@${coach?.email?.split("@")?.firstOrNull() ?: "unknown"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Email: ${coach?.email ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Text(
                                    text = "Phone: ${coach?.contactNumber ?: "N/A"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                team?.let {
                                    Text(
                                        text = "Team: ${it.teamName}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Button(
                        onClick = { showEditProfileDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Profile",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Edit Profile")
                        }
                    }
                }
            }
        }
    }

    if (showEditProfileDialog) {
        val (name, setName) = remember { mutableStateOf(coach?.fullName ?: "Unknown Coach") }
        val (email, setEmail) = remember { mutableStateOf(coach?.email ?: "N/A") }
        val (phone, setPhone) = remember { mutableStateOf(coach?.contactNumber?.toString() ?: "N/A") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = name,
                        onValueChange = setName,
                        label = { Text("Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = setEmail,
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = phone,
                        onValueChange = setPhone,
                        label = { Text("Phone") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        coach = coach?.copy(
                            fullName = name,
                            email = email,
                            contactNumber = phone.toLongOrNull() ?: 0L
                        )
                        coach?.let { updatedCoach ->
                            coroutineScope.launch {
                                dbHelper.updateUser(
                                    updatedCoach.userID,
                                    updatedCoach.fullName,
                                    updatedCoach.email,
                                    updatedCoach.contactNumber?.toInt() ?: 0
                                )
                            }
                        }
                        showEditProfileDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
// Bottom navigation bar composable
@Composable
fun SettingsBottomBar(navController: NavController) {
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
            onClick = { navController.navigate("manage_players") },
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
            onClick = { navController.navigate("manage_feedback") },
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
            onClick = { navController.navigate("settings") },
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


data class SettingsOption(val title: String, val icon: ImageVector, val onClick: () -> Unit)

@Composable
fun SettingsOptionRow(option: SettingsOption) {
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