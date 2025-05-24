package com.example.hockeyapplive.screens.admin

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.navigation.currentBackStackEntryAsState
import com.example.hockeyapplive.R
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.TeamRegistration
import kotlinx.coroutines.launch

// Professional Navy Blue Color Palette
private val NavyBluePrimary = Color(0xFF0A2463)
private val NavyBlueSecondary = Color(0xFF1E3C72)
private val NavyBlueDark = Color(0xFF071E4A)
private val NavyBlueLight = Color(0xFF3A5894)
private val NavyBlueAccent = Color(0xFF4267B2)
private val WhiteText = Color(0xFFF5F5F5)
private val AccentBlue = Color(0xFF4267B2)

@Composable
fun AdminDashboardScreen(navController: NavController, context: Context) {
    val dbHelper = remember { DatabaseHelper(context) }
    var adminUserId by remember { mutableStateOf<Int?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Fetch admin user ID
    LaunchedEffect(Unit) {
        val adminUser = dbHelper.getAdminUser()
        if (adminUser != null) {
            adminUserId = adminUser.userID
        } else {
            scope.launch {
                snackbarHostState.showSnackbar("Error: Admin user not found")
            }
        }
    }

    // Fetch data from the database
    var pendingRegistrations by remember { mutableStateOf<List<TeamRegistration>>(emptyList()) }
    var approvedRegistrations by remember { mutableStateOf<List<TeamRegistration>>(emptyList()) }
    var rejectedRegistrations by remember { mutableStateOf<List<TeamRegistration>>(emptyList()) }
    var totalTeams by remember { mutableStateOf(0) }
    var totalCoaches by remember { mutableStateOf(0) }
    var totalPlayers by remember { mutableStateOf(0) }
    var selectedRegistration by remember { mutableStateOf<TeamRegistration?>(null) }
    var viewedRegistrations by remember { mutableStateOf(setOf<Int>()) }
    var showErrorPopup by remember { mutableStateOf(false) }

    // Load data when the screen is composed
    LaunchedEffect(Unit) {
        val allRegistrations = dbHelper.getAllTeamRegistrations()
        pendingRegistrations = allRegistrations.filter { it.status == "Pending" }
        approvedRegistrations = allRegistrations.filter { it.status == "Approved" }
        rejectedRegistrations = allRegistrations.filter { it.status == "Rejected" }

        // Fetch total teams
        val teamsCursor = dbHelper.readableDatabase.rawQuery("SELECT COUNT(*) FROM Teams", null)
        totalTeams = if (teamsCursor.moveToFirst()) teamsCursor.getInt(0) else 0
        teamsCursor.close()

        // Fetch total verified coaches
        val coachesCursor = dbHelper.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM Users WHERE user_type = 'Coach' AND isVerified = 1",
            null
        )
        totalCoaches = if (coachesCursor.moveToFirst()) coachesCursor.getInt(0) else 0
        coachesCursor.close()

        // Fetch total players
        val playersCursor = dbHelper.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM Players",
            null
        )
        totalPlayers = if (playersCursor.moveToFirst()) playersCursor.getInt(0) else 0
        playersCursor.close()
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    val navigationItems = listOf(
        Triple("Home", android.R.drawable.ic_menu_today, "admin_dashboard"),
        Triple("Teams", android.R.drawable.ic_menu_preferences, "manage_teams"),
        Triple("Players", android.R.drawable.ic_menu_myplaces, "players"),
        Triple("Events", android.R.drawable.ic_menu_agenda, "manage_events"),
        Triple("Admin Settings", android.R.drawable.ic_menu_manage, "admin_settings_screen")
    )

    // Popup Dialog for Submitted Details
    if (selectedRegistration != null) {
        AlertDialog(
            onDismissRequest = {
                selectedRegistration?.let {
                    viewedRegistrations = viewedRegistrations + it.registrationID
                }
                selectedRegistration = null
            },
            containerColor = Color.White,
            titleContentColor = NavyBluePrimary,
            textContentColor = Color.DarkGray,
            title = { Text("Registration Details for ${selectedRegistration?.teamName}", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text("Team Name: ${selectedRegistration?.teamName ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coach Name: ${selectedRegistration?.coachName ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Contact Email: ${selectedRegistration?.contactEmail ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Status: ${selectedRegistration?.status ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Created At: ${selectedRegistration?.createdAt ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Years of Existence: ${selectedRegistration?.yearsOfExistence?.toString() ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Field Address: ${selectedRegistration?.fieldAddress ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Games Played: ${selectedRegistration?.gamesPlayed?.toString() ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coach Reference: ${selectedRegistration?.coachReference ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coach ID Number: ${selectedRegistration?.coachIdNo ?: "N/A"}")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Coach Experience (Years): ${selectedRegistration?.coachExperienceYears?.toString() ?: "N/A"}")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRegistration?.let {
                            viewedRegistrations = viewedRegistrations + it.registrationID
                        }
                        selectedRegistration = null
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyBluePrimary,
                        contentColor = WhiteText
                    )
                ) {
                    Text("Close")
                }
            }
        )
    }

    // Error Popup for Attempting to Approve/Reject Without Viewing Details
    if (showErrorPopup) {
        AlertDialog(
            onDismissRequest = { showErrorPopup = false },
            containerColor = Color.White,
            titleContentColor = NavyBluePrimary,
            textContentColor = Color.DarkGray,
            title = { Text("Action Required", fontWeight = FontWeight.Bold) },
            text = { Text("Please view the full details first.") },
            confirmButton = {
                Button(
                    onClick = { showErrorPopup = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyBluePrimary,
                        contentColor = WhiteText
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerShape = RectangleShape,
                drawerContainerColor = NavyBlueDark
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Column {
                        Text(
                            "Hockey App Live",
                            style = MaterialTheme.typography.headlineSmall,
                            color = WhiteText,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Admin Portal",
                            style = MaterialTheme.typography.bodyLarge,
                            color = WhiteText
                        )
                    }
                }
                Divider(color = NavyBlueLight, thickness = 1.dp)
                Spacer(modifier = Modifier.height(16.dp))
                navigationItems.forEach { (label, icon, route) ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = label,
                                tint = WhiteText
                            )
                        },
                        label = { Text(label, color = WhiteText) },
                        selected = currentRoute == route,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            unselectedContainerColor = NavyBlueDark,
                            selectedContainerColor = NavyBlueSecondary,
                            unselectedTextColor = WhiteText,
                            selectedTextColor = WhiteText,
                            unselectedIconColor = WhiteText,
                            selectedIconColor = AccentBlue
                        ),
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                    )
                }
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = WhiteText
                        )
                    },
                    label = { Text("Profile", color = WhiteText) },
                    selected = currentRoute?.startsWith("admin_profile") == true,
                    onClick = {
                        adminUserId?.let { userId ->
                            navController.navigate("admin_profile/$userId") {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        } ?: scope.launch {
                            snackbarHostState.showSnackbar("Error: Admin user not found")
                        }
                    },
                    colors = NavigationDrawerItemDefaults.colors(
                        unselectedContainerColor = NavyBlueDark,
                        selectedContainerColor = NavyBlueSecondary,
                        unselectedTextColor = WhiteText,
                        selectedTextColor = WhiteText,
                        unselectedIconColor = WhiteText,
                        selectedIconColor = AccentBlue
                    ),
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            containerColor = Color(0xFFF5F7FA),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                NavigationBar(
                    containerColor = NavyBluePrimary,
                    contentColor = WhiteText
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = currentRoute == "admin_dashboard",
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
                        selected = currentRoute == "manage_teams",
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
                        selected = currentRoute == "players",
                        onClick = { navController.navigate("manage_players?teamName=") },
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
                        selected = currentRoute == "manage_events",
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
                        selected = currentRoute?.startsWith("admin_profile") == true,
                        onClick = {
                            adminUserId?.let { userId ->
                                navController.navigate("admin_profile/$userId")
                            } ?: scope.launch {
                                snackbarHostState.showSnackbar("Error: Admin user not found")
                            }
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            },
            topBar = {
                Surface(
                    color = NavyBluePrimary,
                    shadowElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Toggle Side Nav",
                                tint = WhiteText
                            )
                        }
                        Text(
                            text = "Admin Dashboard",
                            style = MaterialTheme.typography.titleLarge,
                            color = WhiteText,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    Icon(
                        painter = painterResource(id = R.drawable.logo11),
                        contentDescription = "Admin Dashboard Image",
                        modifier = Modifier
                            .width(100.dp)
                            .padding(vertical = 8.dp),
                        tint = NavyBluePrimary
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cards for Totals
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyBluePrimary),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = totalTeams.toString(),
                                    color = WhiteText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total Teams",
                                    color = WhiteText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyBlueSecondary),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = totalCoaches.toString(),
                                    color = WhiteText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Coaches",
                                    color = WhiteText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .height(100.dp)
                                .padding(horizontal = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = NavyBlueLight),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = totalPlayers.toString(),
                                    color = WhiteText,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Total Players",
                                    color = WhiteText,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }

                // Pending Registrations Section
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Pending Registrations",
                                style = MaterialTheme.typography.titleLarge,
                                color = NavyBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(
                                color = NavyBlueLight.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            if (pendingRegistrations.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No pending registrations",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                pendingRegistrations.forEach { registration ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (viewedRegistrations.contains(registration.registrationID))
                                                Color(0xFFEEF5FF) else Color(0xFFF9F9F9)
                                        ),
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Button(
                                                onClick = { selectedRegistration = registration },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = NavyBluePrimary,
                                                    contentColor = WhiteText
                                                ),
                                                modifier = Modifier.weight(1.2f)
                                            ) {
                                                Text(text = registration.teamName)
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                enabled = viewedRegistrations.contains(registration.registrationID),
                                                onClick = {
                                                    if (viewedRegistrations.contains(registration.registrationID)) {
                                                        try {
                                                            val db = dbHelper.writableDatabase
                                                            db.beginTransaction()
                                                            try {
                                                                // Update TeamRegistrations status to Approved
                                                                db.execSQL(
                                                                    "UPDATE TeamRegistrations SET status = 'Approved' WHERE registrationID = ?",
                                                                    arrayOf(registration.registrationID)
                                                                )
                                                                // Set the coach as verified
                                                                registration.coachUserID?.let { coachId ->
                                                                    dbHelper.setUserVerified(coachId, true)
                                                                    // Insert notification for the coach
                                                                    dbHelper.insertNotification(
                                                                        userId = coachId,
                                                                        title = "Account Verified",
                                                                        content = "Your account has been verified. You can now log in to manage your team ${registration.teamName}.",
                                                                        notificationType = "Registration"
                                                                    )
                                                                } ?: throw IllegalArgumentException("Coach user ID is null")
                                                                // Insert into Teams table with all details
                                                                db.execSQL(
                                                                    """
                                                                        INSERT INTO Teams (team_name, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
                                                                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                                                                    """,
                                                                    arrayOf(
                                                                        registration.teamName,
                                                                        registration.coachUserID,
                                                                        registration.contactEmail,
                                                                        registration.yearsOfExistence,
                                                                        registration.fieldAddress,
                                                                        registration.gamesPlayed,
                                                                        registration.coachReference,
                                                                        registration.coachIdNo,
                                                                        registration.coachExperienceYears
                                                                    )
                                                                )
                                                                db.setTransactionSuccessful()
                                                                // Refresh data
                                                                pendingRegistrations = pendingRegistrations.filter { it.registrationID != registration.registrationID }
                                                                approvedRegistrations = approvedRegistrations + registration.copy(status = "Approved")
                                                                totalTeams += 1
                                                                // Refresh total coaches (only verified ones)
                                                                val coachesCursor = dbHelper.readableDatabase.rawQuery(
                                                                    "SELECT COUNT(*) FROM Users WHERE user_type = 'Coach' AND isVerified = 1",
                                                                    null
                                                                )
                                                                totalCoaches = if (coachesCursor.moveToFirst()) coachesCursor.getInt(0) else 0
                                                                coachesCursor.close()
                                                                scope.launch {
                                                                    snackbarHostState.showSnackbar("Team approved successfully. Coach can now log in.")
                                                                }
                                                            } finally {
                                                                db.endTransaction()
                                                            }
                                                        } catch (e: Exception) {
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar("Error approving team: ${e.message}")
                                                            }
                                                        }
                                                    } else {
                                                        showErrorPopup = true
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFF0A8043),
                                                    contentColor = WhiteText,
                                                    disabledContainerColor = Color(0xFFCCDFD3),
                                                    disabledContentColor = Color.Gray
                                                ),
                                                modifier = Modifier.weight(0.9f)
                                            ) {
                                                Text("Approve")
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Button(
                                                enabled = viewedRegistrations.contains(registration.registrationID),
                                                onClick = {
                                                    if (viewedRegistrations.contains(registration.registrationID)) {
                                                        try {
                                                            val db = dbHelper.writableDatabase
                                                            // Update status to Rejected
                                                            db.execSQL(
                                                                "UPDATE TeamRegistrations SET status = 'Rejected' WHERE registrationID = ?",
                                                                arrayOf(registration.registrationID)
                                                            )
                                                            // Refresh data
                                                            pendingRegistrations = pendingRegistrations.filter { it.registrationID != registration.registrationID }
                                                            rejectedRegistrations = rejectedRegistrations + registration.copy(status = "Rejected")
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar("Team rejected successfully.")
                                                            }
                                                        } catch (e: Exception) {
                                                            scope.launch {
                                                                snackbarHostState.showSnackbar("Error rejecting team: ${e.message}")
                                                            }
                                                        }
                                                    } else {
                                                        showErrorPopup = true
                                                    }
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = Color(0xFFB71C1C),
                                                    contentColor = WhiteText,
                                                    disabledContainerColor = Color(0xFFEBD6D6),
                                                    disabledContentColor = Color.Gray
                                                ),
                                                modifier = Modifier.weight(0.9f)
                                            ) {
                                                Text("Reject")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Registration Status Summary
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Registration Status",
                                style = MaterialTheme.typography.titleMedium,
                                color = NavyBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(
                                color = NavyBlueLight.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Approved",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0A8043)
                                    )
                                    Text(
                                        text = if (approvedRegistrations.isEmpty()) "None" else approvedRegistrations.joinToString { it.teamName },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rejected",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFB71C1C)
                                    )
                                    Text(
                                        text = if (rejectedRegistrations.isEmpty()) "None" else rejectedRegistrations.joinToString { it.teamName },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color.DarkGray
                                    )
                                }
                            }
                        }
                    }
                }

                // Quick Action Buttons
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = "Quick Actions",
                                style = MaterialTheme.typography.titleMedium,
                                color = NavyBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Divider(
                                color = NavyBlueLight.copy(alpha = 0.3f),
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Button(
                                onClick = { navController.navigate("manage_teams") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyBluePrimary,
                                    contentColor = WhiteText
                                )
                            ) {
                                Text("Manage Teams")
                            }

                            Button(
                                onClick = { navController.navigate("manage_events") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyBlueSecondary,
                                    contentColor = WhiteText
                                )
                            ) {
                                Text("Manage Events")
                            }

                            Button(
                                onClick = { navController.navigate("manage_feedback") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NavyBlueLight,
                                    contentColor = WhiteText
                                )
                            ) {
                                Text("Manage Feedback")
                            }
                        }
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