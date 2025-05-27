package com.example.hockeyapplive.screens.admin

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.datasource.DataSource
import com.example.hockeyapplive.data.model.CoachTeamDetails
import com.example.hockeyapplive.data.model.Game
import com.example.hockeyapplive.data.model.Team
import com.example.hockeyapplive.data.db.DatabaseHelper

import com.example.hockeyapplive.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// Professional color scheme with enhanced gradients
private object AppColors {
    val NavyBlue = Color(0xFF0A2463)
    val LightNavyBlue = Color(0xFF1E3A8A)
    val AccentBlue = Color(0xFF3E92CC)
    val SkyBlue = Color(0xFF87CEEB)
    val LightGray = Color(0xFFF8F9FA)
    val MediumGray = Color(0xFFE9ECEF)
    val DarkText = Color(0xFF212529)
    val ErrorRed = Color(0xFFDC3545)
    val SuccessGreen = Color(0xFF28A745)
    val White = Color(0xFFFFFFFF)

    // Team logo color combinations
    val TeamColors = listOf(
        listOf(Color(0xFF1E88E5), Color(0xFF42A5F5)), // Blue gradient
        listOf(Color(0xFFE53935), Color(0xFFEF5350)), // Red gradient
        listOf(Color(0xFF43A047), Color(0xFF66BB6A)), // Green gradient
        listOf(Color(0xFFFF9800), Color(0xFFFFB74D)), // Orange gradient
        listOf(Color(0xFF8E24AA), Color(0xFFBA68C8)), // Purple gradient
        listOf(Color(0xFF00ACC1), Color(0xFF4DD0E1)), // Cyan gradient
        listOf(Color(0xFFFFD54F), Color(0xFFFFF176)), // Yellow gradient
        listOf(Color(0xFF5D4037), Color(0xFF8D6E63)), // Brown gradient
    )
}

// Team logo generator function
@Composable
private fun TeamLogo(
    teamName: String,
    size: Int = 48,
    modifier: Modifier = Modifier
) {
    val initials = teamName.split(" ")
        .take(2)
        .mapNotNull { it.firstOrNull()?.uppercaseChar() }
        .joinToString("")
        .take(2)

    val colorIndex = teamName.hashCode().mod(AppColors.TeamColors.size).let {
        if (it < 0) it + AppColors.TeamColors.size else it
    }
    val colors = AppColors.TeamColors[colorIndex]

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = colors
                )
            )
            .border(
                width = 2.dp,
                color = Color.White,
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontSize = (size * 0.35).sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTeamsScreen(navController: NavController, context: Context) {
    // State and dependencies initialization
    val dbHelper = remember { DatabaseHelper(context) }
    val dataSource = remember { DataSource(context) }
    var teams by remember { mutableStateOf<List<Team>>(emptyList()) }
    var showAddTeamDialog by remember { mutableStateOf(false) }
    var showDetailsDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }
    var teamGames by remember { mutableStateOf<List<Game>>(emptyList()) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Enhanced theme colors
    val customColorScheme = lightColorScheme(
        primary = AppColors.AccentBlue,
        onPrimary = Color.White,
        secondary = AppColors.LightNavyBlue,
        background = AppColors.LightGray,
        surface = AppColors.White,
        onBackground = AppColors.DarkText,
        onSurface = AppColors.DarkText,
        surfaceVariant = AppColors.MediumGray
    )

    // Function to fetch teams from the database using DatabaseHelper
    suspend fun fetchTeams(): List<Team> {
        return try {
            dbHelper.getAllTeams()
        } catch (e: Exception) {
            scope.launch {
                snackbarHostState.showSnackbar("Failed to fetch teams: ${e.message}")
            }
            emptyList()
        }
    }

    // Function to convert UTC timestamp to CAT (UTC+2) and format it
    fun formatGameDate(utcDate: String?): String {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val utcTime = utcDate?.let { OffsetDateTime.parse(it) } ?: return "N/A"
                val catTime = utcTime.plusHours(2) // Convert to CAT (UTC+2)
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                catTime.format(formatter) + " CAT"
            } else {
                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                sdf.timeZone = java.util.TimeZone.getTimeZone("UTC")
                val date = utcDate?.let { sdf.parse(it) } ?: return "N/A"
                val calendar = java.util.Calendar.getInstance()
                calendar.time = date
                calendar.add(java.util.Calendar.HOUR_OF_DAY, 2) // Convert to CAT (UTC+2)
                val outputFormat = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
                outputFormat.format(calendar.time) + " CAT"
            }
        } catch (e: Exception) {
            utcDate ?: "N/A" // Fallback to "N/A" if parsing fails or input is null
        }
    }

    // Fetch teams when the screen is composed
    LaunchedEffect(Unit) {
        teams = fetchTeams()
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Manage Teams",
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AppColors.NavyBlue,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.shadow(
                        elevation = 8.dp,
                        spotColor = AppColors.NavyBlue.copy(alpha = 0.3f)
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = AppColors.NavyBlue,
                    contentColor = Color.White,
                    modifier = Modifier.shadow(
                        elevation = 16.dp,
                        spotColor = AppColors.NavyBlue.copy(alpha = 0.3f)
                    )
                ) {
                    val navItems = listOf(
                        Triple("Dashboard", Icons.Filled.Dashboard, "admin_dashboard"),
                        Triple("Teams", Icons.Filled.Groups, "manage_teams"),
                        Triple("Players", Icons.Filled.SportsHockey, "manage_players"),
                        Triple("Events", Icons.Filled.EventNote, "manage_events"),
                        Triple("Profile", Icons.Filled.AccountCircle, "admin_profile ")
                    )

                    navItems.forEach { (label, icon, route) ->
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label, fontSize = 12.sp) },
                            selected = route == "manage_teams",
                            onClick = { navController.navigate(route) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppColors.AccentBlue,
                                unselectedIconColor = Color.White.copy(alpha = 0.8f),
                                selectedTextColor = AppColors.AccentBlue,
                                unselectedTextColor = Color.White.copy(alpha = 0.8f),
                                indicatorColor = Color.White.copy(alpha = 0.15f)
                            )
                        )
                    }
                }
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                AppColors.LightGray,
                                AppColors.White
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start
                ) {
                    // Professional Header Section
                    ProfessionalHeader(
                        teamsCount = teams.size,
                        onAddTeamClick = { showAddTeamDialog = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Teams Section Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Teams Registry",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.NavyBlue
                        )

                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = AppColors.AccentBlue.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "${teams.size} Total",
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                style = MaterialTheme.typography.labelMedium,
                                color = AppColors.AccentBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Teams List
                    if (teams.isEmpty()) {
                        ProfessionalEmptyState()
                    } else {
                        ProfessionalTeamsList(
                            teams = teams,
                            onViewDetails = { team ->
                                selectedTeam = team
                                teamGames = dataSource.getGamesForTeam(team.teamId)
                                showDetailsDialog = true
                                isEditing = false
                            },
                            onEditTeam = { team ->
                                selectedTeam = team
                                teamGames = dataSource.getGamesForTeam(team.teamId)
                                showDetailsDialog = true
                                isEditing = true
                            },
                            onDeleteTeam = { team ->
                                scope.launch {
                                    try {
                                        dbHelper.deleteTeam(team.teamId)
                                        teams = fetchTeams()
                                        snackbarHostState.showSnackbar("Team deleted successfully")
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Failed to delete team: ${e.message}")
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Professional Back Button
                    ProfessionalBackButton(
                        onClick = { navController.navigate("admin_dashboard") }
                    )

                    // Add Team Dialog
                    if (showAddTeamDialog) {
                        ProfessionalAddTeamDialog(
                            onDismiss = { showAddTeamDialog = false },
                            onTeamAdded = { coachTeamDetails ->
                                scope.launch {
                                    val result = dataSource.insertTeamWithCoachDetails(coachTeamDetails)
                                    if (result.isSuccess) {
                                        teams = fetchTeams()
                                        showAddTeamDialog = false
                                        snackbarHostState.showSnackbar("Team added successfully")
                                    } else {
                                        snackbarHostState.showSnackbar("Failed to add team: ${result.exceptionOrNull()?.message}")
                                    }
                                }
                            },
                            snackbarHostState = snackbarHostState,
                            scope = scope
                        )
                    }

                    // Team Details/Edit Dialog
                    if (showDetailsDialog && selectedTeam != null) {
                        ProfessionalTeamDetailsDialog(
                            team = selectedTeam!!,
                            teamGames = teamGames,
                            isEditing = isEditing,
                            onEditModeChange = { isEditing = it },
                            onDismiss = {
                                showDetailsDialog = false
                                isEditing = false
                                teamGames = emptyList()
                            },
                            onSave = { updatedTeam ->
                                scope.launch {
                                    try {
                                        val success = dbHelper.updateTeam(updatedTeam)
                                        if (success) {
                                            teams = fetchTeams()
                                            showDetailsDialog = false
                                            isEditing = false
                                            teamGames = emptyList()
                                            snackbarHostState.showSnackbar("Team updated successfully")
                                        } else {
                                            snackbarHostState.showSnackbar("Failed to update team")
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar("Failed to update team: ${e.message}")
                                    }
                                }
                            },
                            formatGameDate = { formatGameDate(it) }
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
            dataSource.close()
        }
    }
}

// Rest of the file (ProfessionalHeader, ProfessionalEmptyState, ProfessionalTeamsList, etc.) remains unchanged
@Composable
private fun ProfessionalHeader(
    teamsCount: Int,
    onAddTeamClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = AppColors.NavyBlue.copy(alpha = 0.15f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            AppColors.NavyBlue,
                            AppColors.LightNavyBlue
                        )
                    )
                )
                .padding(24.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Team Management",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Manage your hockey teams efficiently",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }

                    Icon(
                        Icons.Filled.Groups,
                        contentDescription = "Teams",
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = onAddTeamClick,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = AppColors.NavyBlue
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Add New Team",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfessionalEmptyState() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Filled.Groups,
                contentDescription = "No teams",
                modifier = Modifier.size(64.dp),
                tint = AppColors.MediumGray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Teams Found",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.DarkText
            )
            Text(
                text = "Start by adding your first hockey team",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ProfessionalTeamsList(
    teams: List<Team>,
    onViewDetails: (Team) -> Unit,
    onEditTeam: (Team) -> Unit,
    onDeleteTeam: (Team) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(teams) { team ->
            ProfessionalTeamCard(
                team = team,
                onViewDetails = { onViewDetails(team) },
                onEditTeam = { onEditTeam(team) },
                onDeleteTeam = { onDeleteTeam(team) }
            )
        }
    }
}

@Composable
private fun ProfessionalTeamCard(
    team: Team,
    onViewDetails: () -> Unit,
    onEditTeam: () -> Unit,
    onDeleteTeam: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = AppColors.NavyBlue.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header with logo and team info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TeamLogo(
                    teamName = team.teamName,
                    size = 56
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = team.teamName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.NavyBlue
                    )

                    team.coachName?.let { coachName ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Person,
                                contentDescription = "Coach",
                                modifier = Modifier.size(16.dp),
                                tint = AppColors.AccentBlue
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Coach: $coachName",
                                style = MaterialTheme.typography.bodyMedium,
                                color = AppColors.DarkText.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Tag,
                            contentDescription = "ID",
                            modifier = Modifier.size(16.dp),
                            tint = AppColors.AccentBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "ID: ${team.teamId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.DarkText.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Team stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                team.yearsOfExistence?.let { years ->
                    ProfessionalStatChip(
                        label = "Years",
                        value = years.toString(),
                        icon = Icons.Filled.History
                    )
                }

                team.gamesPlayed?.let { games ->
                    ProfessionalStatChip(
                        label = "Games",
                        value = games.toString(),
                        icon = Icons.Filled.SportsHockey
                    )
                }

                team.fieldAddress?.let {
                    ProfessionalStatChip(
                        label = "Location",
                        value = "Set",
                        icon = Icons.Filled.Place
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Divider(
                color = AppColors.MediumGray.copy(alpha = 0.5f),
                thickness = 1.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ProfessionalActionButton(
                    onClick = onViewDetails,
                    icon = Icons.Filled.RemoveRedEye,
                    text = "View",
                    containerColor = AppColors.AccentBlue,
                    modifier = Modifier.weight(1f)
                )

                ProfessionalActionButton(
                    onClick = onEditTeam,
                    icon = Icons.Filled.Edit,
                    text = "Edit",
                    containerColor = AppColors.SuccessGreen,
                    modifier = Modifier.weight(1f)
                )

                ProfessionalActionButton(
                    onClick = onDeleteTeam,
                    icon = Icons.Filled.Delete,
                    text = "Delete",
                    containerColor = AppColors.ErrorRed,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ProfessionalStatChip(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .shadow(elevation = 2.dp, shape = RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(16.dp),
                tint = AppColors.AccentBlue
            )
            Text(
                text = "$value $label",
                style = MaterialTheme.typography.labelMedium,
                color = AppColors.DarkText
            )
        }
    }
}

@Composable
private fun ProfessionalActionButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(40.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ProfessionalBackButton(
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.NavyBlue,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Back to Dashboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfessionalAddTeamDialog(
    onDismiss: () -> Unit,
    onTeamAdded: (CoachTeamDetails) -> Unit,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    var showTeamDetails by remember { mutableStateOf(false) }
    var fullName by remember { mutableStateOf("") }
    var idNo by remember { mutableStateOf("") }
    var yearOfExperience by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var teamYearsOfExistence by remember { mutableStateOf("") }
    var addressOfFieldOrCourt by remember { mutableStateOf("") }
    var numberOfGamesPlayed by remember { mutableStateOf("") }
    var referenceOfCoachGame by remember { mutableStateOf("") }

    var fullNameError by remember { mutableStateOf<String?>(null) }
    var idNoError by remember { mutableStateOf<String?>(null) }
    var yearOfExperienceError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var teamNameError by remember { mutableStateOf<String?>(null) }
    var teamYearsOfExistenceError by remember { mutableStateOf<String?>(null) }
    var addressOfFieldOrCourtError by remember { mutableStateOf<String?>(null) }
    var numberOfGamesPlayedError by remember { mutableStateOf<String?>(null) }
    var referenceOfCoachGameError by remember { mutableStateOf<String?>(null) }

    fun validateCoachDetails(): Boolean {
        var isValid = true
        fullNameError = if (fullName.isBlank()) {
            isValid = false
            "Full name is required"
        } else null
        idNoError = if (idNo.isBlank()) {
            isValid = false
            "ID number is required"
        } else null
        yearOfExperienceError = if (yearOfExperience.isBlank()) {
            isValid = false
            "Years of experience is required"
        } else if (!yearOfExperience.all { it.isDigit() }) {
            isValid = false
            "Years of experience must be a number"
        } else null
        emailError = if (email.isBlank()) {
            isValid = false
            "Email is required"
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            isValid = false
            "Invalid email format"
        } else null
        return isValid
    }

    fun validateTeamDetails(): Boolean {
        var isValid = true
        teamNameError = if (teamName.isBlank()) {
            isValid = false
            "Team name is required"
        } else null
        teamYearsOfExistenceError = if (teamYearsOfExistence.isBlank()) {
            isValid = false
            "Team years of existence is required"
        } else if (!teamYearsOfExistence.all { it.isDigit() }) {
            isValid = false
            "Team years of existence must be a number"
        } else null
        addressOfFieldOrCourtError = if (addressOfFieldOrCourt.isBlank()) {
            isValid = false
            "Address is required"
        } else null
        numberOfGamesPlayedError = if (numberOfGamesPlayed.isBlank()) {
            isValid = false
            "Number of games is required"
        } else if (!numberOfGamesPlayed.all { it.isDigit() }) {
            isValid = false
            "Number of games must be a number"
        } else null
        referenceOfCoachGameError = if (referenceOfCoachGame.isBlank()) {
            isValid = false
            "Reference is required"
        } else null
        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (!showTeamDetails) "Add Team - Coach Details" else "Add Team - Team Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.NavyBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (!showTeamDetails) {
                    ProfessionalFormTextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Full Name *",
                        error = fullNameError,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = idNo,
                        onValueChange = { idNo = it },
                        label = "ID Number *",
                        error = idNoError,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = yearOfExperience,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) yearOfExperience = it },
                        label = "Years of Experience *",
                        error = yearOfExperienceError,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email *",
                        error = emailError,
                        keyboardType = KeyboardType.Email
                    )
                } else {
                    ProfessionalFormTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = "Team Name *",
                        error = teamNameError,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = teamYearsOfExistence,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) teamYearsOfExistence = it },
                        label = "Years of Existence *",
                        error = teamYearsOfExistenceError,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = addressOfFieldOrCourt,
                        onValueChange = { addressOfFieldOrCourt = it },
                        label = "Field Address *",
                        error = addressOfFieldOrCourtError,
                        keyboardType = KeyboardType.Text,
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = numberOfGamesPlayed,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) numberOfGamesPlayed = it },
                        label = "Games Played *",
                        error = numberOfGamesPlayedError,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = referenceOfCoachGame,
                        onValueChange = { referenceOfCoachGame = it },
                        label = "Coach Reference *",
                        error = referenceOfCoachGameError,
                        keyboardType = KeyboardType.Text,
                        minLines = 2
                    )
                }
            }
        },
        confirmButton = {
            if (!showTeamDetails) {
                Button(
                    onClick = {
                        if (validateCoachDetails()) {
                            showTeamDetails = true
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fix the errors in coach details")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Next", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                Button(
                    onClick = {
                        if (validateTeamDetails()) {
                            val createdAt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                            } else {
                                val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                                sdf.format(java.util.Date())
                            }
                            onTeamAdded(
                                CoachTeamDetails(
                                    fullName = fullName,
                                    idNo = idNo,
                                    yearsOfExperience = yearOfExperience.toIntOrNull() ?: 0,
                                    email = email,
                                    teamName = teamName,
                                    teamYearsOfExistence = teamYearsOfExistence.toIntOrNull() ?: 0,
                                    addressOfFieldOrCourt = addressOfFieldOrCourt,
                                    numberOfGamesPlayed = numberOfGamesPlayed.toIntOrNull() ?: 0,
                                    referenceOfCoachGame = referenceOfCoachGame,
                                    createdAt = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        OffsetDateTime.now().toLocalDateTime()
                                    } else {
                                        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                                        val currentDate = sdf.format(java.util.Date())
                                        java.time.LocalDateTime.parse(currentDate, java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                                    }
                                )
                            )
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar("Please fix the errors in team details")
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Submit", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    if (showTeamDetails) {
                        showTeamDetails = false
                    } else {
                        onDismiss()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.NavyBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (showTeamDetails) "Back" else "Cancel",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = AppColors.LightGray,
        titleContentColor = AppColors.NavyBlue,
        textContentColor = AppColors.DarkText
    )
}

@Composable
private fun ProfessionalFormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = AppColors.DarkText) },
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.White, shape = RoundedCornerShape(8.dp)),
        isError = error != null,
        supportingText = { error?.let { Text(it, color = AppColors.ErrorRed) } },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        minLines = minLines,
        colors = TextFieldDefaults.colors(
            focusedContainerColor = AppColors.White,
            unfocusedContainerColor = AppColors.White,
            errorContainerColor = AppColors.White,
            focusedIndicatorColor = AppColors.AccentBlue,
            unfocusedIndicatorColor = AppColors.MediumGray,
            errorIndicatorColor = AppColors.ErrorRed
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfessionalTeamDetailsDialog(
    team: Team,
    teamGames: List<Game>,
    isEditing: Boolean,
    onEditModeChange: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Team) -> Unit,
    formatGameDate: (String?) -> String
) {
    var editedTeamName by remember { mutableStateOf(team.teamName) }
    var editedContactEmail by remember { mutableStateOf(team.contactEmail ?: "") }
    var editedYearsOfExistence by remember { mutableStateOf(team.yearsOfExistence?.toString() ?: "") }
    var editedFieldAddress by remember { mutableStateOf(team.fieldAddress ?: "") }
    var editedGamesPlayed by remember { mutableStateOf(team.gamesPlayed?.toString() ?: "") }
    var editedCoachReference by remember { mutableStateOf(team.coachReference ?: "") }
    var editedCoachIdNo by remember { mutableStateOf(team.coachIdNo ?: "") }
    var editedCoachExperienceYears by remember { mutableStateOf(team.coachExperienceYears?.toString() ?: "") }
    var editErrorMessage by remember { mutableStateOf<String?>(null) }

    fun validateEditDetails(): Boolean {
        editErrorMessage = null
        if (editedTeamName.isBlank()) {
            editErrorMessage = "Team name cannot be empty"
            return false
        }
        if (editedContactEmail.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(editedContactEmail).matches()) {
            editErrorMessage = "Invalid email format"
            return false
        }
        if (editedYearsOfExistence.isNotBlank() && !editedYearsOfExistence.all { it.isDigit() }) {
            editErrorMessage = "Years of existence must be a number"
            return false
        }
        if (editedGamesPlayed.isNotBlank() && !editedGamesPlayed.all { it.isDigit() }) {
            editErrorMessage = "Games played must be a number"
            return false
        }
        if (editedCoachExperienceYears.isNotBlank() && !editedCoachExperienceYears.all { it.isDigit() }) {
            editErrorMessage = "Coach experience years must be a number"
            return false
        }
        return true
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "Edit Team" else "Team Details",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.NavyBlue
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                if (isEditing) {
                    ProfessionalFormTextField(
                        value = editedTeamName,
                        onValueChange = { editedTeamName = it },
                        label = "Team Name *",
                        error = editErrorMessage,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedContactEmail,
                        onValueChange = { editedContactEmail = it },
                        label = "Contact Email",
                        error = null,
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedYearsOfExistence,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) editedYearsOfExistence = it },
                        label = "Years of Existence",
                        error = null,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedFieldAddress,
                        onValueChange = { editedFieldAddress = it },
                        label = "Field Address",
                        error = null,
                        keyboardType = KeyboardType.Text,
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedGamesPlayed,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) editedGamesPlayed = it },
                        label = "Games Played",
                        error = null,
                        keyboardType = KeyboardType.Number
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedCoachReference,
                        onValueChange = { editedCoachReference = it },
                        label = "Coach Reference",
                        error = null,
                        keyboardType = KeyboardType.Text,
                        minLines = 2
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedCoachIdNo,
                        onValueChange = { editedCoachIdNo = it },
                        label = "Coach ID Number",
                        error = null,
                        keyboardType = KeyboardType.Text
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfessionalFormTextField(
                        value = editedCoachExperienceYears,
                        onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) editedCoachExperienceYears = it },
                        label = "Coach Experience Years",
                        error = null,
                        keyboardType = KeyboardType.Number
                    )
                } else {
                    Text(
                        text = "Team ID: ${team.teamId}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Team Name: ${team.teamName}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = AppColors.DarkText
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    team.coachName?.let { coachName ->
                        Text(
                            text = "Coach Name: $coachName",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.contactEmail?.let { email ->
                        Text(
                            text = "Contact Email: $email",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.yearsOfExistence?.let { years ->
                        Text(
                            text = "Years of Existence: $years",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.fieldAddress?.let { address ->
                        Text(
                            text = "Field Address: $address",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.gamesPlayed?.let { games ->
                        Text(
                            text = "Games Played: $games",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.coachReference?.let { reference ->
                        Text(
                            text = "Coach Reference: $reference",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.coachIdNo?.let { idNo ->
                        Text(
                            text = "Coach ID Number: $idNo",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    team.coachExperienceYears?.let { years ->
                        Text(
                            text = "Coach Experience Years: $years",
                            style = MaterialTheme.typography.bodyLarge,
                            color = AppColors.DarkText
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Game History",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.NavyBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    if (teamGames.isEmpty()) {
                        Text(
                            text = "No games played",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp)
                        ) {
                            items(teamGames) { game ->
                                ProfessionalGameCard(game = game, formatGameDate = formatGameDate)
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
                        if (validateEditDetails()) {
                            onSave(
                                team.copy(
                                    teamName = editedTeamName,
                                    contactEmail = editedContactEmail.takeIf { it.isNotBlank() },
                                    yearsOfExistence = editedYearsOfExistence.toIntOrNull(),
                                    fieldAddress = editedFieldAddress.takeIf { it.isNotBlank() },
                                    gamesPlayed = editedGamesPlayed.toIntOrNull(),
                                    coachReference = editedCoachReference.takeIf { it.isNotBlank() },
                                    coachIdNo = editedCoachIdNo.takeIf { it.isNotBlank() },
                                    coachExperienceYears = editedCoachExperienceYears.toIntOrNull()
                                )
                            )
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            } else {
                Button(
                    onClick = { onEditModeChange(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.AccentBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Edit", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                }
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.NavyBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (isEditing) "Cancel" else "Close",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = AppColors.LightGray,
        titleContentColor = AppColors.NavyBlue,
        textContentColor = AppColors.DarkText
    )
}

@Composable
private fun ProfessionalGameCard(
    game: Game,
    formatGameDate: (String?) -> String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.White)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${game.team1Name ?: "Unknown Team"} vs ${game.team2Name ?: "Unknown Team"}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.DarkText
                )
                game.team1Score?.let { score1 ->
                    game.team2Score?.let { score2 ->
                        Text(
                            text = "$score1 - $score2",
                            style = MaterialTheme.typography.bodySmall,
                            color = AppColors.AccentBlue
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Date: ${formatGameDate(game.gameDate)}",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.DarkText.copy(alpha = 0.7f)
            )
            game.location?.let { location ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Location: $location",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.DarkText.copy(alpha = 0.7f)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${game.status}",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.DarkText.copy(alpha = 0.7f)
            )
        }
    }
}