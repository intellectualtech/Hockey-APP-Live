package com.example.hockeyapplive.screens

import android.content.ContentValues
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.datasource.DataSource
import com.example.hockeyapplive.data.model.TeamRegistration
import com.example.hockeyapplive.data.model.User
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
        val user = User(
            userID = 0, // Will be auto-incremented by database
            fullName = fullName,
            email = email,
            userPassword = defaultPassword,
            createdAt = "", // Database will set this
            lastLogin = null,
            userType = "Coach",
            isVerified = false
        )
        dataSource.insertUser(fullName, email, defaultPassword, "Coach", false)

        val teamRegistration = TeamRegistration(
            registrationID = 0, // Will be auto-incremented by database
            teamName = teamName,
            coachName = fullName,
            contactEmail = email,
            createdAt = "", // Database will set this
            status = "Pending",
            coachUserID = 0 // Will be set after user is inserted if needed
        )
        dataSource.insertTeamRegistration(teamName, fullName, email, "Pending")

        scope.launch {
            snackbarHostState.showSnackbar("Registration submitted. Awaiting approval.")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Register Team",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (!showTeamDetails) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Coach Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text("Full Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = fullNameError != null,
                            supportingText = { if (fullNameError != null) Text(fullNameError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = idNo,
                            onValueChange = { idNo = it },
                            label = { Text("ID Number *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = idNoError != null,
                            supportingText = { if (idNoError != null) Text(idNoError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = yearOfExperience,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) yearOfExperience = it },
                            label = { Text("Year of Experience *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = yearOfExperienceError != null,
                            supportingText = { if (yearOfExperienceError != null) Text(yearOfExperienceError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text("Email *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            isError = emailError != null,
                            supportingText = { if (emailError != null) Text(emailError!!) }
                        )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Next")
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Team Details",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        OutlinedTextField(
                            value = teamName,
                            onValueChange = { teamName = it },
                            label = { Text("Team Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamNameError != null,
                            supportingText = { if (teamNameError != null) Text(teamNameError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = teamYearsOfExistin,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) teamYearsOfExistin = it },
                            label = { Text("Team Years of Existence *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamYearsOfExistinError != null,
                            supportingText = { if (teamYearsOfExistinError != null) Text(teamYearsOfExistinError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = addressOfFieldOrCort,
                            onValueChange = { addressOfFieldOrCort = it },
                            label = { Text("Address of Field or Court *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = addressOfFieldOrCortError != null,
                            supportingText = { if (addressOfFieldOrCortError != null) Text(addressOfFieldOrCortError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = numberOfGamesYouPlayed,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) numberOfGamesYouPlayed = it },
                            label = { Text("Number of Games Played *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = numberOfGamesYouPlayedError != null,
                            supportingText = { if (numberOfGamesYouPlayedError != null) Text(numberOfGamesYouPlayedError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = referenceOfCoachGameYouPlayedWithPast,
                            onValueChange = { referenceOfCoachGameYouPlayedWithPast = it },
                            label = { Text("Reference of Coach/Game Played with Past *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = referenceOfCoachGameYouPlayedWithPastError != null,
                            supportingText = { if (referenceOfCoachGameYouPlayedWithPastError != null) Text(referenceOfCoachGameYouPlayedWithPastError!!) }
                        )
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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                ) {
                    Text("Submit")
                }

                Button(
                    onClick = { showTeamDetails = false },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Back")
                }
            }
        }
    }

    // Dialog to show the message
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Submission Successful") },
            text = { Text("Your application will take up to 48 hours to be approved.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    navController.navigate("login") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }) {
                    Text("OK")
                }
            }
        )
    }
}

// Extension function to insert TeamRegistration
fun DataSource.insertTeamRegistration(
    teamName: String,
    coachName: String,
    contactEmail: String,
    status: String
) {
    val values = ContentValues().apply {
        put("team_name", teamName)
        put("coachName", coachName)
        put("contact_email", contactEmail)
        put("status", status)
    }
    db.insert("TeamRegistrations", null, values)
}