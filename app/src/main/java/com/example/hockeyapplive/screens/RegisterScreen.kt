package com.example.hockeyapplive.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.datasource.DataSource
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, context: android.content.Context) {
    // State for coach details
    var fullName by remember { mutableStateOf("") }
    var idNo by remember { mutableStateOf("") }
    var yearOfExperience by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    // State for team details
    var teamName by remember { mutableStateOf("") }
    var teamYearsOfExistin by remember { mutableStateOf("") }
    var addressOfFieldOrCort by remember { mutableStateOf("") }
    var numberOfGamesYouPlayed by remember { mutableStateOf("") }
    var referenceOfCoachGameYouPlayedWithPast by remember { mutableStateOf("") }

    // Error states
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var idNoError by remember { mutableStateOf<String?>(null) }
    var yearOfExperienceError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var teamNameError by remember { mutableStateOf<String?>(null) }
    var teamYearsOfExistinError by remember { mutableStateOf<String?>(null) }
    var addressOfFieldOrCortError by remember { mutableStateOf<String?>(null) }
    var numberOfGamesYouPlayedError by remember { mutableStateOf<String?>(null) }
    var referenceOfCoachGameYouPlayedWithPastError by remember { mutableStateOf<String?>(null) }

    var showTeamDetails by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val dataSource = remember { DataSource(context) }

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
            "Year of experience is required"
        } else if (!yearOfExperience.all { it.isDigit() }) {
            isValid = false
            "Year of experience must be a number"
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

        teamYearsOfExistinError = if (teamYearsOfExistin.isBlank()) {
            isValid = false
            "Team years of existence is required"
        } else if (!teamYearsOfExistin.all { it.isDigit() }) {
            isValid = false
            "Team years of existence must be a number"
        } else null

        addressOfFieldOrCortError = if (addressOfFieldOrCort.isBlank()) {
            isValid = false
            "Address is required"
        } else null

        numberOfGamesYouPlayedError = if (numberOfGamesYouPlayed.isBlank()) {
            isValid = false
            "Number of games is required"
        } else if (!numberOfGamesYouPlayed.all { it.isDigit() }) {
            isValid = false
            "Number of games must be a number"
        } else null

        referenceOfCoachGameYouPlayedWithPastError = if (referenceOfCoachGameYouPlayedWithPast.isBlank()) {
            isValid = false
            "Reference is required"
        } else null

        return isValid
    }

    fun registerCoachAndTeam() {
        val defaultPassword = "default123" // Temporary default password
        scope.launch {
            try {
                // Register the user (Coach)
                val userResult = dataSource.registerUser(fullName, email, defaultPassword, "Coach")
                when {
                    userResult.isSuccess -> {
                        val userId = userResult.getOrThrow()
                        // After user is registered, insert the team registration
                        val teamResult = dataSource.insertTeamRegistration(
                            teamName = teamName,
                            coachName = fullName,
                            contactEmail = email,
                            status = "Pending",
                            coachUserId = userId
                        )
                        if (teamResult.isSuccess) {
                            snackbarHostState.showSnackbar("Registration submitted. Awaiting approval.")
                        } else {
                            snackbarHostState.showSnackbar("Failed to register team: ${teamResult.exceptionOrNull()?.message}")
                        }
                    }
                    userResult.isFailure -> {
                        val errorMessage = userResult.exceptionOrNull()?.message ?: "Unknown error"
                        snackbarHostState.showSnackbar("Failed to register coach: $errorMessage")
                    }
                }
            } catch (e: Exception) {
                snackbarHostState.showSnackbar("An unexpected error occurred: ${e.message}")
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hockey App Live") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("welcome") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register Team",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            // Progress indicator
            LinearProgressIndicator(
                progress = if (!showTeamDetails) 0.5f else 1.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Text(
                text = if (!showTeamDetails) "Step 1: Coach Details" else "Step 2: Team Details",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (!showTeamDetails) {
                // Coach Details Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Coach",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Coach Details",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Name") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = fullNameError != null,
                            supportingText = { if (fullNameError != null) Text(fullNameError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = idNo,
                            onValueChange = { idNo = it },
                            label = { Text("ID Number *") },
                            leadingIcon = { Icon(Icons.Default.Badge, contentDescription = "ID") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = idNoError != null,
                            supportingText = { if (idNoError != null) Text(idNoError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = yearOfExperience,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) yearOfExperience = it },
                            label = { Text("Years of Experience *") },
                            leadingIcon = { Icon(Icons.Default.Timeline, contentDescription = "Experience") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = yearOfExperienceError != null,
                            supportingText = { if (yearOfExperienceError != null) Text(yearOfExperienceError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email *") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null,
                            supportingText = { if (emailError != null) Text(emailError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(
                        onClick = { navController.navigate("welcome") }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cancel"
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Cancel")
                        }
                    }

                    Button(
                        onClick = {
                            if (validateCoachDetails()) {
                                showTeamDetails = true
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fix the errors in the coach details")
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Next")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = "Next")
                        }
                    }
                }

                // Already have an account section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Already have an account?",
                            style = MaterialTheme.typography.bodyLarge
                        )

                        TextButton(
                            onClick = { navController.navigate("login") }
                        ) {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

            } else {
                // Team Details Form
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = "Team",
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Team Details",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Team Name *") },
                            leadingIcon = { Icon(Icons.Default.Sports, contentDescription = "Team") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamNameError != null,
                            supportingText = { if (teamNameError != null) Text(teamNameError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = teamYearsOfExistin,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) teamYearsOfExistin = it },
                            label = { Text("Team Years of Existence *") },
                            leadingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Years") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamYearsOfExistinError != null,
                            supportingText = { if (teamYearsOfExistinError != null) Text(teamYearsOfExistinError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = addressOfFieldOrCort,
                            onValueChange = { addressOfFieldOrCort = it },
                            label = { Text("Address of Field or Court *") },
                            leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = "Address") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = addressOfFieldOrCortError != null,
                            supportingText = { if (addressOfFieldOrCortError != null) Text(addressOfFieldOrCortError!!) },
                            singleLine = false,
                            minLines = 2,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = numberOfGamesYouPlayed,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) numberOfGamesYouPlayed = it },
                            label = { Text("Number of Games Played *") },
                            leadingIcon = { Icon(Icons.Default.Score, contentDescription = "Games") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = numberOfGamesYouPlayedError != null,
                            supportingText = { if (numberOfGamesYouPlayedError != null) Text(numberOfGamesYouPlayedError!!) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedTextField(
                            value = referenceOfCoachGameYouPlayedWithPast,
                            onValueChange = { referenceOfCoachGameYouPlayedWithPast = it },
                            label = { Text("Reference of Coach/Game Played with Past *") },
                            leadingIcon = { Icon(Icons.Default.Info, contentDescription = "Reference") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = referenceOfCoachGameYouPlayedWithPastError != null,
                            supportingText = { if (referenceOfCoachGameYouPlayedWithPastError != null) Text(referenceOfCoachGameYouPlayedWithPastError!!) },
                            singleLine = false,
                            minLines = 2,
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { showTeamDetails = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back")
                        }
                    }

                    Button(
                        onClick = {
                            if (validateTeamDetails()) {
                                registerCoachAndTeam()
                                showDialog = true
                            } else {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Please fix the errors in the team details")
                                }
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Submit")
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Send, contentDescription = "Submit")
                        }
                    }
                }
            }

            // Disclaimer text
            Text(
                text = "By registering, you agree to our Terms of Service and Privacy Policy.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    // Success Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = { Icon(Icons.Default.CheckCircle, contentDescription = "Success") },
            title = { Text("Submission Successful") },
            text = {
                Text(
                    "Your application has been submitted and will take up to 48 hours to be approved. " +
                            "You will receive an email notification once your registration is approved."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Proceed to Login")
                }
            }
        )
    }
}