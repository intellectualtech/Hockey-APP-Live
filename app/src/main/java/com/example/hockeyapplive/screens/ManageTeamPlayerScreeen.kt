package com.example.hockeyapplive.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Player
import com.example.hockeyapplive.data.model.TeamRegistration
import com.example.hockeyapplive.data.model.User
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamPlayerScreen(navController: NavController) {
    val context = LocalContext.current
    val dbHelper = remember { DatabaseHelper(context) }
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var selectedTeam by remember { mutableStateOf<TeamRegistration?>(null) }
    var teamSelectionError by remember { mutableStateOf<String?>(null) }
    var selectedPlayer by remember { mutableStateOf<Player?>(null) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var editedPlayerName by remember { mutableStateOf("") }
    var editedJerseyNumber by remember { mutableStateOf("") }
    var editedPosition by remember { mutableStateOf("") }
    var editedAge by remember { mutableStateOf("") }
    var editedHeight by remember { mutableStateOf("") }
    var editedEmergencyContact by remember { mutableStateOf("") }
    var editedDateOfBirth by remember { mutableStateOf("") }
    var editedJoinDate by remember { mutableStateOf("") }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showRegistrationDialog by remember { mutableStateOf(false) }
    var newPlayerName by remember { mutableStateOf("") }
    var newJerseyNumber by remember { mutableStateOf("") }
    var newPosition by remember { mutableStateOf("") }
    var newAge by remember { mutableStateOf("") }
    var newHeight by remember { mutableStateOf("") }
    var newEmergencyContact by remember { mutableStateOf("") }
    var newDateOfBirth by remember { mutableStateOf("") }
    var newJoinDate by remember { mutableStateOf("") }
    var registrationErrorMessage by remember { mutableStateOf<String?>(null) }
    var registrationStep by remember { mutableStateOf(1) }

    val PrimaryNavy = Color(0xFF0B1426)
    val SecondaryNavy = Color(0xFF1A2B4A)
    val AccentBlue = Color(0xFF2563EB)
    val LightBlue = Color(0xFF3B82F6)
    val SurfaceLight = Color(0xFFF8FAFC)
    val SurfaceWhite = Color(0xFFFFFFFF)
    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val TextTertiary = Color(0xFF94A3B8)
    val BorderLight = Color(0xFFE2E8F0)
    val SuccessGreen = Color(0xFF10B981)
    val WarningOrange = Color(0xFFF59E0B)
    val ErrorRed = Color(0xFFEF4444)
    val HoverGray = Color(0xFFF1F5F9)
    val NavyBlue = Color(0xFF3D5A80)
    val White = Color(0xFFFFFFFF)

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(SurfaceLight, SurfaceWhite, HoverGray),
        startY = 0f,
        endY = 1500f
    )

    val cardGradient = Brush.verticalGradient(
        colors = listOf(SurfaceWhite, Color(0xFFFCFCFD))
    )

    val customColorScheme = lightColorScheme(
        primary = AccentBlue,
        onPrimary = Color.White,
        secondary = SecondaryNavy,
        background = SurfaceLight,
        surface = SurfaceWhite,
        onBackground = TextPrimary,
        onSurface = TextPrimary,
        error = ErrorRed,
        outline = BorderLight
    )

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        isLenient = false
    }

    fun isValidDate(dateStr: String): Boolean = try {
        dateFormat.parse(dateStr)
        true
    } catch (e: Exception) {
        false
    }

    fun validateStep1(): Boolean {
        return newPlayerName.isNotBlank() &&
                newJerseyNumber.isNotBlank() &&
                newPosition.isNotBlank() &&
                newAge.isNotBlank() &&
                newJerseyNumber.toIntOrNull() != null && newJerseyNumber.toInt() > 0 &&
                newAge.toIntOrNull() != null && newAge.toInt() > 0
    }

    suspend fun fetchData() {
        isLoading = true
        try {
            val loggedInUserId = dbHelper.getLoggedInUserId(context)
            if (loggedInUserId == -1) {
                teamSelectionError = "User not logged in. Please log in again."
                players = emptyList()
                snackbarHostState.showSnackbar("No logged-in user found.")
                return
            }

            val allTeams = dbHelper.getAllTeamRegistrationsAndTeams()
            if (allTeams.isEmpty()) {
                teamSelectionError = "No teams found in the database."
                players = emptyList()
                snackbarHostState.showSnackbar("No teams available in the database.")
                return
            }

            val filteredTeams = allTeams.filter { it.coachUserID == loggedInUserId }
            selectedTeam = filteredTeams.firstOrNull()
            if (selectedTeam != null) {
                players = dbHelper.getPlayersByTeamId(selectedTeam!!.registrationID).filter { it.teamId == selectedTeam!!.registrationID }
                teamSelectionError = null
                if (players.isEmpty()) {
                    snackbarHostState.showSnackbar("No players found for team ${selectedTeam!!.teamName}.")
                } else {
                    snackbarHostState.showSnackbar("Data loaded successfully for team ${selectedTeam!!.teamName}.")
                }
            } else {
                teamSelectionError = "No team assigned to this coach (User ID: $loggedInUserId)."
                players = emptyList()
                snackbarHostState.showSnackbar("No team assigned to you.")
            }
        } catch (e: Exception) {
            teamSelectionError = "Failed to load team data: ${e.message}"
            players = emptyList()
            snackbarHostState.showSnackbar("Failed to load data: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        scope.launch {
            fetchData()
        }
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Filled.SportsHockey,
                                contentDescription = "Players",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Player Management",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    "Manage players for ${selectedTeam?.teamName ?: "Unknown Team"}",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PrimaryNavy,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.shadow(12.dp),
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = NavyBlue,
                    contentColor = White,
                    modifier = Modifier.shadow(16.dp)
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home", tint = White.copy(alpha = 0.7f)) },
                        label = { Text("Home", color = White.copy(alpha = 0.7f)) },
                        selected = false,
                        onClick = { navController.navigate("onboarding") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            indicatorColor = AccentBlue,
                            unselectedIconColor = White.copy(alpha = 0.7f),
                            unselectedTextColor = White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Event, contentDescription = "Events", tint = White.copy(alpha = 0.7f)) },
                        label = { Text("Events", color = White.copy(alpha = 0.7f)) },
                        selected = false,
                        onClick = { navController.navigate("events") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            indicatorColor = AccentBlue,
                            unselectedIconColor = White.copy(alpha = 0.7f),
                            unselectedTextColor = White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Event, contentDescription = "Manage Team", tint = White) },
                        label = { Text("Manage Team", color = White) },
                        selected = true,
                        onClick = { navController.navigate("manage_team_players") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            indicatorColor = AccentBlue,
                            unselectedIconColor = White.copy(alpha = 0.7f),
                            unselectedTextColor = White.copy(alpha = 0.7f)
                        )
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Profile", tint = White.copy(alpha = 0.7f)) },
                        label = { Text("Profile", color = White.copy(alpha = 0.7f)) },
                        selected = false,
                        onClick = { navController.navigate("settings") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = White,
                            selectedTextColor = White,
                            indicatorColor = AccentBlue,
                            unselectedIconColor = White.copy(alpha = 0.7f),
                            unselectedTextColor = White.copy(alpha = 0.7f)
                        )
                    )
                }
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    snackbar = { snackbarData ->
                        Snackbar(
                            snackbarData = snackbarData,
                            containerColor = PrimaryNavy,
                            contentColor = Color.White,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundGradient)
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp)
                            .shadow(8.dp, RoundedCornerShape(20.dp)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(cardGradient)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(28.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 24.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                Brush.radialGradient(
                                                    colors = listOf(
                                                        AccentBlue.copy(alpha = 0.15f),
                                                        AccentBlue.copy(alpha = 0.05f)
                                                    )
                                                )
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.PersonAdd,
                                            contentDescription = "Add Player",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(20.dp))
                                    Column {
                                        Text(
                                            text = "Register New Player",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Add a new player to your team roster for ${selectedTeam?.teamName ?: "Unknown Team"}",
                                            style = MaterialTheme.typography.bodyLarge,
                                            color = TextSecondary
                                        )
                                    }
                                }

                                if (isLoading) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp),
                                                color = AccentBlue,
                                                strokeWidth = 3.dp
                                            )
                                            Spacer(modifier = Modifier.height(16.dp))
                                            Text(
                                                "Loading team data...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                } else if (teamSelectionError != null) {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 16.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = WarningOrange.copy(alpha = 0.05f)
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        border = CardDefaults.outlinedCardBorder().copy(
                                            brush = SolidColor(WarningOrange.copy(alpha = 0.2f))
                                        )
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(20.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.Warning,
                                                contentDescription = "Error",
                                                tint = WarningOrange,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = "Error",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = teamSelectionError!!,
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (selectedTeam == null) {
                                                teamSelectionError = "Team data not loaded"
                                            } else if (selectedTeam?.status != "Approved") {
                                                teamSelectionError = "Cannot register players for a team that is not approved"
                                            } else {
                                                newPlayerName = ""
                                                newJerseyNumber = ""
                                                newPosition = ""
                                                newAge = ""
                                                newHeight = ""
                                                newEmergencyContact = ""
                                                newDateOfBirth = ""
                                                newJoinDate = ""
                                                registrationErrorMessage = null
                                                registrationStep = 1
                                                showRegistrationDialog = true
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentBlue,
                                            contentColor = Color.White,
                                            disabledContainerColor = BorderLight,
                                            disabledContentColor = TextTertiary
                                        ),
                                        shape = RoundedCornerShape(14.dp),
                                        elevation = ButtonDefaults.buttonElevation(
                                            defaultElevation = 4.dp,
                                            pressedElevation = 12.dp,
                                            disabledElevation = 0.dp
                                        ),
                                        enabled = selectedTeam != null
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                Icons.Filled.Add,
                                                contentDescription = "Register",
                                                modifier = Modifier.size(22.dp)
                                            )
                                            Spacer(modifier = Modifier.width(12.dp))
                                            Text(
                                                "Register New Player",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Registered Players",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            Text(
                                text = "${players.size} ${if (players.size == 1) "player" else "players"} registered",
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(28.dp),
                                color = AccentBlue,
                                strokeWidth = 3.dp
                            )
                        }
                    }

                    if (players.isEmpty() && !isLoading) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                            shape = RoundedCornerShape(20.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(56.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(80.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            Brush.radialGradient(
                                                colors = listOf(
                                                    TextTertiary.copy(alpha = 0.1f),
                                                    TextTertiary.copy(alpha = 0.05f)
                                                )
                                            )
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Filled.SportsHockey,
                                        contentDescription = "No players",
                                        tint = TextTertiary,
                                        modifier = Modifier.size(40.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = "No players registered yet",
                                    style = MaterialTheme.typography.titleLarge,
                                    color = TextSecondary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Add a new player using the button above.",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = TextTertiary
                                )
                            }
                        }
                    } else if (!isLoading) {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(players, key = { player -> player.playerId ?: 0 }) { player ->
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .shadow(6.dp, RoundedCornerShape(18.dp)),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                                    shape = RoundedCornerShape(18.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(cardGradient)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(24.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(48.dp)
                                                        .clip(RoundedCornerShape(14.dp))
                                                        .background(
                                                            Brush.radialGradient(
                                                                colors = listOf(
                                                                    AccentBlue.copy(alpha = 0.15f),
                                                                    AccentBlue.copy(alpha = 0.05f)
                                                                )
                                                            )
                                                        ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Filled.SportsHockey,
                                                        contentDescription = "Player",
                                                        tint = AccentBlue,
                                                        modifier = Modifier.size(24.dp)
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(20.dp))
                                                Column {
                                                    Text(
                                                        text = player.fullName ?: "Unknown Player",
                                                        style = MaterialTheme.typography.titleLarge,
                                                        fontWeight = FontWeight.Bold,
                                                        color = TextPrimary
                                                    )
                                                    Text(
                                                        text = "Player ID: ${player.playerId}",
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        color = TextSecondary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }

                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                                            ) {
                                                OutlinedButton(
                                                    onClick = {
                                                        selectedPlayer = player
                                                        editedPlayerName = player.fullName ?: ""
                                                        editedJerseyNumber = player.jerseyNumber?.toString() ?: ""
                                                        editedPosition = player.position ?: ""
                                                        editedAge = player.age?.toString() ?: ""
                                                        editedHeight = player.height?.toString() ?: ""
                                                        editedEmergencyContact = player.emergencyContact ?: ""
                                                        editedDateOfBirth = player.dateOfBirth ?: ""
                                                        editedJoinDate = player.joinDate ?: ""
                                                        isEditing = false
                                                        showDetailsDialog = true
                                                    },
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = AccentBlue,
                                                        containerColor = AccentBlue.copy(alpha = 0.05f)
                                                    ),
                                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                                        brush = SolidColor(AccentBlue.copy(alpha = 0.3f))
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.size(44.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Info,
                                                        contentDescription = "Details",
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        selectedPlayer = player
                                                        editedPlayerName = player.fullName ?: ""
                                                        editedJerseyNumber = player.jerseyNumber?.toString() ?: ""
                                                        editedPosition = player.position ?: ""
                                                        editedAge = player.age?.toString() ?: ""
                                                        editedHeight = player.height?.toString() ?: ""
                                                        editedEmergencyContact = player.emergencyContact ?: ""
                                                        editedDateOfBirth = player.dateOfBirth ?: ""
                                                        editedJoinDate = player.joinDate ?: ""
                                                        isEditing = true
                                                        showDetailsDialog = true
                                                    },
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = SuccessGreen,
                                                        containerColor = SuccessGreen.copy(alpha = 0.05f)
                                                    ),
                                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                                        brush = SolidColor(SuccessGreen.copy(alpha = 0.3f))
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.size(44.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Edit,
                                                        contentDescription = "Edit",
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }

                                                OutlinedButton(
                                                    onClick = {
                                                        scope.launch {
                                                            try {
                                                                if (player.playerId != null) {
                                                                    dbHelper.deletePlayer(player.playerId)
                                                                    fetchData()
                                                                    snackbarHostState.showSnackbar("Player deleted successfully")
                                                                } else {
                                                                    snackbarHostState.showSnackbar("Invalid player ID")
                                                                }
                                                            } catch (e: Exception) {
                                                                snackbarHostState.showSnackbar("Failed to delete player: ${e.message}")
                                                            }
                                                        }
                                                    },
                                                    colors = ButtonDefaults.outlinedButtonColors(
                                                        contentColor = ErrorRed,
                                                        containerColor = ErrorRed.copy(alpha = 0.05f)
                                                    ),
                                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                                        brush = SolidColor(ErrorRed.copy(alpha = 0.3f))
                                                    ),
                                                    shape = RoundedCornerShape(10.dp),
                                                    modifier = Modifier.size(44.dp),
                                                    contentPadding = PaddingValues(0.dp)
                                                ) {
                                                    Icon(
                                                        Icons.Filled.Delete,
                                                        contentDescription = "Delete",
                                                        modifier = Modifier.size(18.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    OutlinedButton(
                        onClick = {
                            val userId = dbHelper.getLoggedInUserId(context)
                            val user = if (userId != -1) dbHelper.getUserById(userId) else null
                            when (user?.userType) {
                                "Coach" -> navController.navigate("onboarding")
                                "Player" -> navController.navigate("settings")
                                else -> navController.popBackStack()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = SecondaryNavy.copy(alpha = 0.05f),
                            contentColor = SecondaryNavy
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(SecondaryNavy.copy(alpha = 0.3f))
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "Back",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (showDetailsDialog && selectedPlayer != null) {
                        val player = selectedPlayer!!
                        AlertDialog(
                            onDismissRequest = {
                                showDetailsDialog = false
                                isEditing = false
                                editErrorMessage = null
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(
                                                if (isEditing) SuccessGreen.copy(alpha = 0.1f)
                                                else AccentBlue.copy(alpha = 0.1f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            if (isEditing) Icons.Filled.Edit else Icons.Filled.Info,
                                            contentDescription = if (isEditing) "Edit" else "Details",
                                            tint = if (isEditing) SuccessGreen else AccentBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = if (isEditing) "Edit Player Details" else "Player Details",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    if (isEditing) {
                                        OutlinedTextField(
                                            value = editedPlayerName,
                                            onValueChange = { editedPlayerName = it },
                                            label = { Text("Player Name") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedJerseyNumber,
                                            onValueChange = { if (it.all { char -> char.isDigit() }) editedJerseyNumber = it },
                                            label = { Text("Jersey Number") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedPosition,
                                            onValueChange = { editedPosition = it },
                                            label = { Text("Position") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedAge,
                                            onValueChange = { if (it.all { char -> char.isDigit() }) editedAge = it },
                                            label = { Text("Age") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedHeight,
                                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) editedHeight = it },
                                            label = { Text("Height (cm)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedEmergencyContact,
                                            onValueChange = { editedEmergencyContact = it },
                                            label = { Text("Emergency Contact") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedDateOfBirth,
                                            onValueChange = { editedDateOfBirth = it },
                                            label = { Text("Date of Birth (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = editedJoinDate,
                                            onValueChange = { editedJoinDate = it },
                                            label = { Text("Join Date (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = editErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Done
                                            )
                                        )

                                        editErrorMessage?.let {
                                            Row(
                                                modifier = Modifier.padding(top = 8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    Icons.Filled.Error,
                                                    contentDescription = "Error",
                                                    tint = ErrorRed,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = it,
                                                    color = ErrorRed,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Medium
                                                )
                                            }
                                        }
                                    } else {
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = CardDefaults.cardColors(containerColor = SurfaceLight),
                                            shape = RoundedCornerShape(16.dp),
                                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(20.dp),
                                                verticalArrangement = Arrangement.spacedBy(12.dp)
                                            ) {
                                                Text(
                                                    text = "Player Information",
                                                    style = MaterialTheme.typography.titleLarge,
                                                    fontWeight = FontWeight.Bold,
                                                    color = TextPrimary,
                                                    modifier = Modifier.padding(bottom = 8.dp)
                                                )
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Player ID: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.playerId}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "User ID: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.userId ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Team ID: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.teamId ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Full Name: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.fullName ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Jersey Number: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.jerseyNumber ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Position: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.position ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Age: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.age ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Height: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.height ?: "N/A"} cm",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Emergency Contact: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.emergencyContact ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Date of Birth: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.dateOfBirth ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                                Row(modifier = Modifier.padding(vertical = 4.dp)) {
                                                    Text(
                                                        text = "Join Date: ",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        fontWeight = FontWeight.Medium,
                                                        color = TextSecondary
                                                    )
                                                    Text(
                                                        text = "${player.joinDate ?: "N/A"}",
                                                        style = MaterialTheme.typography.bodyLarge,
                                                        color = TextPrimary,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                if (isEditing) {
                                    Button(
                                        onClick = {
                                            if (editedPlayerName.isBlank()) {
                                                editErrorMessage = "Player name cannot be empty"
                                                return@Button
                                            }
                                            if (editedJerseyNumber.isBlank()) {
                                                editErrorMessage = "Jersey number cannot be empty"
                                                return@Button
                                            }
                                            if (editedPosition.isBlank()) {
                                                editErrorMessage = "Position cannot be empty"
                                                return@Button
                                            }
                                            if (editedAge.isBlank()) {
                                                editErrorMessage = "Age cannot be empty"
                                                return@Button
                                            }
                                            if (editedHeight.isBlank()) {
                                                editErrorMessage = "Height cannot be empty"
                                                return@Button
                                            }
                                            if (editedEmergencyContact.isBlank()) {
                                                editErrorMessage = "Emergency contact cannot be empty"
                                                return@Button
                                            }
                                            if (editedDateOfBirth.isBlank()) {
                                                editErrorMessage = "Date of birth cannot be empty"
                                                return@Button
                                            }
                                            if (editedJoinDate.isBlank()) {
                                                editErrorMessage = "Join date cannot be empty"
                                                return@Button
                                            }

                                            val jerseyNum = editedJerseyNumber.toIntOrNull()
                                            val ageNum = editedAge.toIntOrNull()
                                            val heightNum = editedHeight.toFloatOrNull()

                                            if (jerseyNum == null || jerseyNum <= 0) {
                                                editErrorMessage = "Invalid jersey number"
                                                return@Button
                                            }
                                            if (ageNum == null || ageNum <= 0) {
                                                editErrorMessage = "Invalid age"
                                                return@Button
                                            }
                                            if (heightNum == null || heightNum <= 0) {
                                                editErrorMessage = "Invalid height"
                                                return@Button
                                            }

                                            if (!isValidDate(editedDateOfBirth)) {
                                                editErrorMessage = "Date of birth must be in YYYY-MM-DD format"
                                                return@Button
                                            }
                                            if (!isValidDate(editedJoinDate)) {
                                                editErrorMessage = "Join date must be in YYYY-MM-DD format"
                                                return@Button
                                            }

                                            val currentDate = dateFormat.parse("2025-05-26")
                                            val dobDate = dateFormat.parse(editedDateOfBirth)
                                            val joinDate = dateFormat.parse(editedJoinDate)

                                            if (dobDate.after(currentDate)) {
                                                editErrorMessage = "Date of birth cannot be in the future"
                                                return@Button
                                            }
                                            if (joinDate.after(currentDate)) {
                                                editErrorMessage = "Join date cannot be in the future"
                                                return@Button
                                            }

                                            scope.launch {
                                                try {
                                                    if (player.playerId != null) {
                                                        val updatedPlayer = Player(
                                                            playerId = player.playerId,
                                                            userId = player.userId,
                                                            teamId = player.teamId,
                                                            fullName = editedPlayerName,
                                                            jerseyNumber = jerseyNum,
                                                            position = editedPosition,
                                                            age = ageNum,
                                                            height = heightNum,
                                                            emergencyContact = editedEmergencyContact,
                                                            dateOfBirth = editedDateOfBirth,
                                                            joinDate = editedJoinDate
                                                        )
                                                        if (dbHelper.updatePlayerDetails(updatedPlayer)) {
                                                            fetchData()
                                                            showDetailsDialog = false
                                                            isEditing = false
                                                            editErrorMessage = null
                                                            snackbarHostState.showSnackbar("Player updated successfully")
                                                        } else {
                                                            editErrorMessage = "Failed to update player"
                                                        }
                                                    } else {
                                                        editErrorMessage = "Player ID not available for update"
                                                    }
                                                } catch (e: Exception) {
                                                    editErrorMessage = "Failed to update player: ${e.message}"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SuccessGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "Save Changes",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            isEditing = true
                                            editedPlayerName = player.fullName ?: ""
                                            editedJerseyNumber = player.jerseyNumber?.toString() ?: ""
                                            editedPosition = player.position ?: ""
                                            editedAge = player.age?.toString() ?: ""
                                            editedHeight = player.height?.toString() ?: ""
                                            editedEmergencyContact = player.emergencyContact ?: ""
                                            editedDateOfBirth = player.dateOfBirth ?: ""
                                            editedJoinDate = player.joinDate ?: ""
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentBlue,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "Edit Player",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            dismissButton = {
                                OutlinedButton(
                                    onClick = {
                                        showDetailsDialog = false
                                        isEditing = false
                                        editErrorMessage = null
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = SurfaceWhite,
                                        contentColor = TextSecondary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = SolidColor(BorderLight)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        if (isEditing) "Cancel" else "Close",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            containerColor = SurfaceWhite,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    if (showRegistrationDialog) {
                        AlertDialog(
                            onDismissRequest = {
                                showRegistrationDialog = false
                                registrationStep = 1
                                registrationErrorMessage = null
                            },
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(14.dp))
                                            .background(AccentBlue.copy(alpha = 0.1f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Filled.PersonAdd,
                                            contentDescription = "Register",
                                            tint = AccentBlue,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = "Register New Player",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                            },
                            text = {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Step ${registrationStep} of 2",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (registrationStep == 1) "Basic Info" else "Additional Info",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = TextSecondary
                                        )
                                    }

                                    if (registrationStep == 1) {
                                        OutlinedTextField(
                                            value = newPlayerName,
                                            onValueChange = { newPlayerName = it },
                                            label = { Text("Player Name") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newJerseyNumber,
                                            onValueChange = { if (it.all { char -> char.isDigit() }) newJerseyNumber = it },
                                            label = { Text("Jersey Number") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newPosition,
                                            onValueChange = { newPosition = it },
                                            label = { Text("Position") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newAge,
                                            onValueChange = { if (it.all { char -> char.isDigit() }) newAge = it },
                                            label = { Text("Age") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Done
                                            )
                                        )
                                    } else {
                                        OutlinedTextField(
                                            value = newHeight,
                                            onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) newHeight = it },
                                            label = { Text("Height (cm)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newEmergencyContact,
                                            onValueChange = { newEmergencyContact = it },
                                            label = { Text("Emergency Contact") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Phone,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newDateOfBirth,
                                            onValueChange = { newDateOfBirth = it },
                                            label = { Text("Date of Birth (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Next
                                            )
                                        )

                                        OutlinedTextField(
                                            value = newJoinDate,
                                            onValueChange = { newJoinDate = it },
                                            label = { Text("Join Date (YYYY-MM-DD)") },
                                            modifier = Modifier.fillMaxWidth(),
                                            isError = registrationErrorMessage != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(12.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Text,
                                                imeAction = ImeAction.Done
                                            )
                                        )
                                    }

                                    registrationErrorMessage?.let {
                                        Row(
                                            modifier = Modifier.padding(top = 8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Filled.Error,
                                                contentDescription = "Error",
                                                tint = ErrorRed,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = it,
                                                color = ErrorRed,
                                                style = MaterialTheme.typography.bodySmall,
                                                fontWeight = FontWeight.Medium
                                            )
                                        }
                                    }
                                }
                            },
                            confirmButton = {
                                if (registrationStep == 1) {
                                    Button(
                                        onClick = {
                                            if (validateStep1()) {
                                                val jerseyNum = newJerseyNumber.toInt()
                                                if (players.any { it.jerseyNumber == jerseyNum }) {
                                                    registrationErrorMessage = "Jersey number $jerseyNum is already taken"
                                                } else {
                                                    registrationStep = 2
                                                    registrationErrorMessage = null
                                                }
                                            } else {
                                                registrationErrorMessage = "Please fill all fields with valid values"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentBlue,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "Next",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = {
                                            if (newPlayerName.isBlank()) {
                                                registrationErrorMessage = "Player name cannot be empty"
                                                return@Button
                                            }
                                            if (newJerseyNumber.isBlank()) {
                                                registrationErrorMessage = "Jersey number cannot be empty"
                                                return@Button
                                            }
                                            if (newPosition.isBlank()) {
                                                registrationErrorMessage = "Position cannot be empty"
                                                return@Button
                                            }
                                            if (newAge.isBlank()) {
                                                registrationErrorMessage = "Age cannot be empty"
                                                return@Button
                                            }
                                            if (newHeight.isBlank()) {
                                                registrationErrorMessage = "Height cannot be empty"
                                                return@Button
                                            }
                                            if (newEmergencyContact.isBlank()) {
                                                registrationErrorMessage = "Emergency contact cannot be empty"
                                                return@Button
                                            }
                                            if (newDateOfBirth.isBlank()) {
                                                registrationErrorMessage = "Date of birth cannot be empty"
                                                return@Button
                                            }
                                            if (newJoinDate.isBlank()) {
                                                registrationErrorMessage = "Join date cannot be empty"
                                                return@Button
                                            }

                                            val jerseyNum = newJerseyNumber.toIntOrNull()
                                            val ageNum = newAge.toIntOrNull()
                                            val heightNum = newHeight.toFloatOrNull()

                                            if (jerseyNum == null || jerseyNum <= 0) {
                                                registrationErrorMessage = "Invalid jersey number"
                                                return@Button
                                            }
                                            if (ageNum == null || ageNum <= 0) {
                                                registrationErrorMessage = "Invalid age"
                                                return@Button
                                            }
                                            if (heightNum == null || heightNum <= 0) {
                                                registrationErrorMessage = "Invalid height"
                                                return@Button
                                            }

                                            if (!isValidDate(newDateOfBirth)) {
                                                registrationErrorMessage = "Date of birth must be in YYYY-MM-DD format"
                                                return@Button
                                            }
                                            if (!isValidDate(newJoinDate)) {
                                                registrationErrorMessage = "Join date must be in YYYY-MM-DD format"
                                                return@Button
                                            }

                                            val currentDate = dateFormat.parse("2025-05-26")
                                            val dobDate = dateFormat.parse(newDateOfBirth)
                                            val joinDate = dateFormat.parse(newJoinDate)

                                            if (dobDate.after(currentDate)) {
                                                registrationErrorMessage = "Date of birth cannot be in the future"
                                                return@Button
                                            }
                                            if (joinDate.after(currentDate)) {
                                                registrationErrorMessage = "Join date cannot be in the future"
                                                return@Button
                                            }

                                            scope.launch {
                                                try {
                                                    val loggedInUserId = dbHelper.getLoggedInUserId(context)
                                                    if (loggedInUserId == -1) {
                                                        registrationErrorMessage = "User not logged in"
                                                        return@launch
                                                    }

                                                    if (selectedTeam == null) {
                                                        registrationErrorMessage = "No team selected for registration"
                                                        return@launch
                                                    }

                                                    // Use the coach's team ID for player registration
                                                    val coachTeamId = selectedTeam!!.registrationID
                                                    val email = "${newPlayerName.replace(" ", "").lowercase()}@hockeyapp.com"
                                                    val defaultPassword = "tempPass123"
                                                    val newUser = User(
                                                        userID = 0,
                                                        fullName = newPlayerName,
                                                        email = email,
                                                        userPassword = defaultPassword,
                                                        createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                                                        lastLogin = null,
                                                        userType = "Player",
                                                        contactNumber = newEmergencyContact.toLongOrNull() ?: 0L,
                                                        isVerified = false
                                                    )
                                                    val userId = dbHelper.insertUser(newUser)
                                                    if (userId != -1) {
                                                        val newPlayer = Player(
                                                            playerId = 0,
                                                            userId = userId,
                                                            teamId = coachTeamId,
                                                            fullName = newPlayerName,
                                                            jerseyNumber = jerseyNum,
                                                            position = newPosition,
                                                            age = ageNum,
                                                            height = heightNum,
                                                            emergencyContact = newEmergencyContact,
                                                            dateOfBirth = newDateOfBirth,
                                                            joinDate = newJoinDate
                                                        )
                                                        val insertedPlayerId = dbHelper.insertPlayer(
                                                            userId = userId,
                                                            teamId = coachTeamId,
                                                            fullName = newPlayerName,
                                                            jerseyNumber = jerseyNum,
                                                            position = newPosition,
                                                            age = ageNum,
                                                            height = heightNum,
                                                            emergencyContact = newEmergencyContact,
                                                            dateOfBirth = newDateOfBirth,
                                                            joinDate = newJoinDate
                                                        )
                                                        if (insertedPlayerId > 0) {
                                                            fetchData()
                                                            showRegistrationDialog = false
                                                            registrationStep = 1
                                                            registrationErrorMessage = null
                                                            snackbarHostState.showSnackbar("Player registered successfully")
                                                        } else {
                                                            dbHelper.deleteUser(userId)
                                                            registrationErrorMessage = "Failed to register player"
                                                        }
                                                    } else {
                                                        registrationErrorMessage = "Failed to create user account"
                                                    }
                                                } catch (e: Exception) {
                                                    registrationErrorMessage = "Failed to register player: ${e.message}"
                                                }
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SuccessGreen,
                                            contentColor = Color.White
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "Register Player",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            },
                            dismissButton = {
                                if (registrationStep == 2) {
                                    Button(
                                        onClick = {
                                            registrationStep = 1
                                            registrationErrorMessage = null
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SecondaryNavy.copy(alpha = 0.1f),
                                            contentColor = SecondaryNavy
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .height(48.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            "Back",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                                OutlinedButton(
                                    onClick = {
                                        showRegistrationDialog = false
                                        registrationStep = 1
                                        registrationErrorMessage = null
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = SurfaceWhite,
                                        contentColor = TextSecondary
                                    ),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = SolidColor(BorderLight)
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .fillMaxWidth()
                                ) {
                                    Text(
                                        "Cancel",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            },
                            containerColor = SurfaceWhite,
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}