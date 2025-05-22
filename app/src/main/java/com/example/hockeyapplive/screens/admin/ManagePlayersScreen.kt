package com.example.hockeyapplive.screens.admin

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Player
import com.example.hockeyapplive.data.model.Team
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagePlayersScreen(
    navController: NavController,
    navBackStackEntry: NavBackStackEntry,
    context: Context
) {
    val dbHelper = remember { DatabaseHelper(context) }
    var players by remember { mutableStateOf<List<Player>>(emptyList()) }
    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var isTeamDropdownExpanded by remember { mutableStateOf(false) }
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

    // Enhanced Professional Color Palette
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
    val BorderMedium = Color(0xFFCBD5E1)
    val SuccessGreen = Color(0xFF10B981)
    val WarningOrange = Color(0xFFF59E0B)
    val ErrorRed = Color(0xFFEF4444)
    val HoverGray = Color(0xFFF1F5F9)

    // Professional gradient backgrounds
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(SurfaceLight, SurfaceWhite, HoverGray),
        startY = 0f,
        endY = 1500f
    )

    val cardGradient = Brush.verticalGradient(
        colors = listOf(SurfaceWhite, Color(0xFFFCFCFD))
    )

    // Enhanced theme colors
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

    // Date formatter for validation (YYYY-MM-DD)
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
        isLenient = false // Strict parsing
    }

    // Function to validate date format
    fun isValidDate(dateStr: String): Boolean = try {
        dateFormat.parse(dateStr)
        true
    } catch (e: Exception) {
        false
    }

    // Fetch teamName from navigation argument
    val teamName = navBackStackEntry.arguments?.getString("teamName")

    // Function to fetch players and teams from the database
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun fetchData() {
        isLoading = true
        try {
            teams = dbHelper.getAllTeams().map { team ->
                // Ensure createdAt is properly handled as LocalDateTime
                team.copy() // Create a copy to avoid modifying the original data
            }
            selectedTeam = teams.find { it.teamName == teamName }
            if (selectedTeam != null) {
                players = dbHelper.getPlayersByTeamId(selectedTeam!!.teamId)
            } else {
                players = emptyList()
                teamSelectionError = "Team '$teamName' not found"
            }
        } catch (e: Exception) {
            // Handle the specific parsing error and provide a fallback
            if (e.message?.contains("Unable to obtain OffsetDateTime") == true) {
                snackbarHostState.showSnackbar("Failed to load data: Timestamp parsing error. Using default data.")
                teams = emptyList() // Fallback to empty list to avoid crash
                players = emptyList()
            } else {
                snackbarHostState.showSnackbar("Failed to load data: ${e.message}")
                players = emptyList()
            }
        } finally {
            isLoading = false
        }
    }

    // Fetch data when the screen is composed or teamName changes
    LaunchedEffect(teamName) {
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
                                    "Player Management - ${teamName ?: "All Teams"}",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.headlineSmall
                                )
                                Text(
                                    "Manage player registrations",
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
                    containerColor = PrimaryNavy,
                    contentColor = Color.White,
                    modifier = Modifier.shadow(16.dp)
                ) {
                    val navigationItems = listOf(
                        Triple(Icons.Filled.Dashboard, "Dashboard", "admin_dashboard"),
                        Triple(Icons.Filled.Groups, "Teams", "manage_teams"),
                        Triple(Icons.Filled.SportsHockey, "Players", "manage_players"),
                        Triple(Icons.Filled.EventNote, "Events", "manage_events"),
                        Triple(Icons.Filled.AccountCircle, "Profile", "admin_profile")
                    )

                    navigationItems.forEach { (icon, label, route) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label, modifier = Modifier.size(24.dp)) },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium)) },
                            selected = route == "manage_players",
                            onClick = { navController.navigate(route) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = LightBlue,
                                unselectedIconColor = Color.White.copy(alpha = 0.7f),
                                selectedTextColor = LightBlue,
                                unselectedTextColor = Color.White.copy(alpha = 0.7f),
                                indicatorColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
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
                    // Enhanced Header Section
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
                                            text = "Add a new player to your team roster",
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
                                                "Loading teams...",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = TextSecondary
                                            )
                                        }
                                    }
                                } else if (teams.isEmpty()) {
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
                                                contentDescription = "No teams",
                                                tint = WarningOrange,
                                                modifier = Modifier.size(28.dp)
                                            )
                                            Spacer(modifier = Modifier.width(16.dp))
                                            Column {
                                                Text(
                                                    text = "No teams available",
                                                    style = MaterialTheme.typography.titleMedium,
                                                    color = TextPrimary,
                                                    fontWeight = FontWeight.SemiBold
                                                )
                                                Text(
                                                    text = "Please create a team first in the Teams section to register players.",
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = TextSecondary
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    // Enhanced Team Selection Dropdown (pre-selected based on teamName)
                                    ExposedDropdownMenuBox(
                                        expanded = isTeamDropdownExpanded,
                                        onExpandedChange = { isTeamDropdownExpanded = !isTeamDropdownExpanded },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        OutlinedTextField(
                                            readOnly = true,
                                            value = selectedTeam?.teamName ?: "",
                                            onValueChange = {},
                                            label = { Text("Select Team", style = MaterialTheme.typography.bodyMedium) },
                                            placeholder = { Text("Choose a team to manage players", style = MaterialTheme.typography.bodyMedium, color = TextTertiary) },
                                            leadingIcon = {
                                                Icon(
                                                    Icons.Filled.Groups,
                                                    contentDescription = "Team",
                                                    tint = if (selectedTeam != null) AccentBlue else TextTertiary,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isTeamDropdownExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            isError = teamSelectionError != null,
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AccentBlue,
                                                unfocusedBorderColor = BorderLight,
                                                focusedLabelColor = AccentBlue,
                                                unfocusedLabelColor = TextSecondary,
                                                cursorColor = AccentBlue,
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                errorBorderColor = ErrorRed,
                                                errorLabelColor = ErrorRed
                                            ),
                                            shape = RoundedCornerShape(14.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge.copy(
                                                color = TextPrimary,
                                                fontWeight = FontWeight.Medium
                                            )
                                        )

                                        ExposedDropdownMenu(
                                            expanded = isTeamDropdownExpanded,
                                            onDismissRequest = { isTeamDropdownExpanded = false },
                                            modifier = Modifier
                                                .background(SurfaceWhite)
                                                .clip(RoundedCornerShape(14.dp))
                                        ) {
                                            teams.forEach { team ->
                                                DropdownMenuItem(
                                                    text = {
                                                        Row(
                                                            verticalAlignment = Alignment.CenterVertically,
                                                            modifier = Modifier.padding(vertical = 4.dp)
                                                        ) {
                                                            Icon(
                                                                Icons.Filled.Groups,
                                                                contentDescription = "Team",
                                                                tint = AccentBlue,
                                                                modifier = Modifier.size(20.dp)
                                                            )
                                                            Spacer(modifier = Modifier.width(12.dp))
                                                            Text(
                                                                team.teamName,
                                                                style = MaterialTheme.typography.bodyLarge,
                                                                color = TextPrimary,
                                                                fontWeight = FontWeight.Medium
                                                            )
                                                        }
                                                    },
                                                    onClick = {
                                                        selectedTeam = team
                                                        isTeamDropdownExpanded = false
                                                        teamSelectionError = null
                                                        scope.launch {
                                                            players = dbHelper.getPlayersByTeamId(team.teamId)
                                                        }
                                                    },
                                                    modifier = Modifier.padding(horizontal = 8.dp)
                                                )
                                            }
                                        }
                                    }

                                    teamSelectionError?.let {
                                        Row(
                                            modifier = Modifier.padding(top = 8.dp, start = 4.dp),
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

                                    Spacer(modifier = Modifier.height(24.dp))

                                    // Enhanced Register Button
                                    Button(
                                        onClick = {
                                            if (selectedTeam == null) {
                                                teamSelectionError = "Please select a team first"
                                            } else {
                                                navController.navigate("playerRegistration?teamId=${selectedTeam!!.teamId}")
                                                teamSelectionError = null
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
                                        enabled = teams.isNotEmpty()
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

                    // Enhanced Players List Header
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

                    // Enhanced Players List
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
                                if (teams.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Select a team above to manage players.",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = TextTertiary,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(players) { player ->
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
                                                // Enhanced Details Button
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

                                                // Enhanced Edit Button
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

                                                // Enhanced Delete Button
                                                OutlinedButton(
                                                    onClick = {
                                                        scope.launch {
                                                            try {
                                                                dbHelper.deletePlayer(player.playerId)
                                                                fetchData()
                                                                snackbarHostState.showSnackbar("Player deleted successfully")
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

                    // Enhanced Back to Dashboard Button
                    OutlinedButton(
                        onClick = { navController.navigate("admin_dashboard") },
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
                                "Back to Dashboard",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Enhanced Player Details/Edit Dialog
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
                                        // Player Name
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

                                        // Jersey Number
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

                                        // Position
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

                                        // Age
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

                                        // Height
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

                                        // Emergency Contact
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

                                        // Date of Birth
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

                                        // Join Date
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
                                            // Validation
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

                                            // Date validation
                                            if (!isValidDate(editedDateOfBirth)) {
                                                editErrorMessage = "Date of birth must be in YYYY-MM-DD format"
                                                return@Button
                                            }
                                            if (!isValidDate(editedJoinDate)) {
                                                editErrorMessage = "Join date must be in YYYY-MM-DD format"
                                                return@Button
                                            }

                                            // Validate dates are not in the future (relative to May 22, 2025)
                                            val currentDate = dateFormat.parse("2025-05-22")
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
                                                        dbHelper.updatePlayer(
                                                            playerId = player.playerId,
                                                            fullName = editedPlayerName,
                                                            jerseyNumber = jerseyNum,
                                                            position = editedPosition,
                                                            age = ageNum,
                                                            height = heightNum,
                                                            emergencyContact = editedEmergencyContact,
                                                            dateOfBirth = editedDateOfBirth,
                                                            joinDate = editedJoinDate
                                                        )
                                                        fetchData()
                                                        showDetailsDialog = false
                                                        isEditing = false
                                                        editErrorMessage = null
                                                        snackbarHostState.showSnackbar("Player updated successfully")
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