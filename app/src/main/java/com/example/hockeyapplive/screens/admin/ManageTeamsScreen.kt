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

// Data class to represent a Team with necessary fields, defined outside the composable
data class Team(val teamId: Int, val teamName: String)

// Professional navy blue color scheme - keeping consistent with ManageEventsScreen
private val NavyBlue = Color(0xFF0A2463)
private val LightNavyBlue = Color(0xFF1E3A8A)
private val AccentBlue = Color(0xFF3E92CC)
private val LightGray = Color(0xFFF5F5F5)
private val DarkText = Color(0xFF333333)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamsScreen(navController: NavController, context: Context) {
    val dbHelper = remember { DatabaseHelper(context) }
    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var newTeam by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editedTeamName by remember { mutableStateOf("") }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }

    // Custom theme colors for this screen - consistent with ManageEventsScreen
    val customColorScheme = darkColorScheme(
        primary = AccentBlue,
        onPrimary = Color.White,
        secondary = LightNavyBlue,
        background = Color.White,
        surface = Color.White,
        onBackground = DarkText,
        onSurface = DarkText
    )

    // Function to fetch teams from the database
    fun fetchTeams(): List<Team> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT team_id, team_name FROM Teams", null)
        val teamList = mutableListOf<Team>()
        try {
            if (cursor.moveToFirst()) {
                do {
                    val teamId = cursor.getInt(cursor.getColumnIndexOrThrow("team_id"))
                    val teamName = cursor.getString(cursor.getColumnIndexOrThrow("team_name"))
                    teamList.add(Team(teamId, teamName))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return teamList
    }

    // Fetch teams when the screen is composed
    LaunchedEffect(Unit) {
        teams = fetchTeams()
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manage Teams", fontWeight = FontWeight.Bold) },
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
                        selected = true,
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
                        selected = false,
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
                            text = "Add New Team",
                            style = MaterialTheme.typography.titleLarge,
                            color = NavyBlue,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = newTeam,
                            onValueChange = { newTeam = it },
                            label = { Text("Team Name") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = errorMessage != null,
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
                                if (newTeam.isBlank()) {
                                    errorMessage = "Team name cannot be empty"
                                } else {
                                    try {
                                        val db = dbHelper.writableDatabase
                                        // Check if team name already exists
                                        val cursor = db.rawQuery(
                                            "SELECT COUNT(*) FROM Teams WHERE team_name = ?",
                                            arrayOf(newTeam)
                                        )
                                        val exists = try {
                                            cursor.moveToFirst() && cursor.getInt(0) > 0
                                        } finally {
                                            cursor.close()
                                        }

                                        if (exists) {
                                            errorMessage = "Team name already exists"
                                        } else {
                                            // Insert new team with minimal required fields
                                            db.execSQL(
                                                "INSERT INTO Teams (team_name) VALUES (?)",
                                                arrayOf(newTeam)
                                            )
                                            // Refresh the team list
                                            teams = fetchTeams()
                                            newTeam = ""
                                            errorMessage = null
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to add team: ${e.message}"
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
                                Text("Add Team")
                            }
                        }
                    }
                }

                Text(
                    text = "Existing Teams",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (teams.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No teams found",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn {
                        items(teams) { team ->
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
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Icon(
                                            Icons.Filled.Groups,
                                            contentDescription = "Team",
                                            tint = NavyBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = team.teamName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold,
                                            color = NavyBlue
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Row {
                                        OutlinedButton(
                                            onClick = {
                                                selectedTeam = team
                                                editedTeamName = team.teamName
                                                isEditing = false
                                                showDetailsDialog = true
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = AccentBlue
                                            ),
                                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                                brush = SolidColor(AccentBlue.copy(alpha = 0.5f))
                                            ),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Filled.Info,
                                                    contentDescription = "Details",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Details")
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                selectedTeam = team
                                                editedTeamName = team.teamName
                                                isEditing = true
                                                showDetailsDialog = true
                                            },
                                            colors = ButtonDefaults.outlinedButtonColors(
                                                contentColor = AccentBlue
                                            ),
                                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                                brush = SolidColor(AccentBlue.copy(alpha = 0.5f))
                                            ),
                                            modifier = Modifier.padding(end = 8.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    Icons.Filled.Edit,
                                                    contentDescription = "Edit",
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Edit")
                                            }
                                        }

                                        OutlinedButton(
                                            onClick = {
                                                try {
                                                    val db = dbHelper.writableDatabase
                                                    db.execSQL(
                                                        "DELETE FROM Teams WHERE team_id = ?",
                                                        arrayOf(team.teamId.toString())
                                                    )
                                                    // Refresh the team list
                                                    teams = fetchTeams()
                                                } catch (e: Exception) {
                                                    errorMessage = "Failed to delete team: ${e.message}"
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

                // Team Details/Edit Dialog
                if (showDetailsDialog && selectedTeam != null) {
                    AlertDialog(
                        onDismissRequest = {
                            showDetailsDialog = false
                            isEditing = false
                            editErrorMessage = null
                        },
                        title = {
                            Text(
                                text = if (isEditing) "Edit Team" else "Team Details",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = NavyBlue
                            )
                        },
                        text = {
                            Column {
                                if (isEditing) {
                                    OutlinedTextField(
                                        value = editedTeamName,
                                        onValueChange = { editedTeamName = it },
                                        label = { Text("Team Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        isError = editErrorMessage != null,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentBlue,
                                            unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                            focusedLabelColor = AccentBlue,
                                            unfocusedLabelColor = NavyBlue
                                        )
                                    )
                                    editErrorMessage?.let {
                                        Text(
                                            text = it,
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodySmall,
                                            modifier = Modifier.padding(top = 4.dp, start = 4.dp)
                                        )
                                    }
                                } else {
                                    Text(
                                        text = "Team ID: ${selectedTeam?.teamId}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DarkText
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Team Name: ${selectedTeam?.teamName}",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = DarkText
                                    )
                                }
                            }
                        },
                        confirmButton = {
                            if (isEditing) {
                                Button(
                                    onClick = {
                                        if (editedTeamName.isBlank()) {
                                            editErrorMessage = "Team name cannot be empty"
                                        } else {
                                            try {
                                                val db = dbHelper.writableDatabase
                                                // Check if team name already exists (excluding current team)
                                                val cursor = db.rawQuery(
                                                    "SELECT COUNT(*) FROM Teams WHERE team_name = ? AND team_id != ?",
                                                    arrayOf(editedTeamName, selectedTeam?.teamId.toString())
                                                )
                                                val exists = try {
                                                    cursor.moveToFirst() && cursor.getInt(0) > 0
                                                } finally {
                                                    cursor.close()
                                                }

                                                if (exists) {
                                                    editErrorMessage = "Team name already exists"
                                                } else {
                                                    // Update team name
                                                    db.execSQL(
                                                        "UPDATE Teams SET team_name = ? WHERE team_id = ?",
                                                        arrayOf(editedTeamName, selectedTeam?.teamId.toString())
                                                    )
                                                    // Refresh the team list
                                                    teams = fetchTeams()
                                                    showDetailsDialog = false
                                                    isEditing = false
                                                    editErrorMessage = null
                                                }
                                            } catch (e: Exception) {
                                                editErrorMessage = "Failed to update team: ${e.message}"
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NavyBlue,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Save")
                                }
                            } else {
                                Button(
                                    onClick = {
                                        isEditing = true
                                        editedTeamName = selectedTeam?.teamName ?: ""
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = NavyBlue,
                                        contentColor = Color.White
                                    )
                                ) {
                                    Text("Edit")
                                }
                            }
                        },
                        dismissButton = {
                            Button(
                                onClick = {
                                    showDetailsDialog = false
                                    isEditing = false
                                    editErrorMessage = null
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyBlue,
                                    contentColor = Color.White
                                )
                            ) {
                                Text(if (isEditing) "Cancel" else "Close")
                            }
                        },
                        containerColor = LightGray,
                        titleContentColor = NavyBlue,
                        textContentColor = DarkText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { navController.navigate("admin_dashboard") },
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
                            Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Back to Dashboard")
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