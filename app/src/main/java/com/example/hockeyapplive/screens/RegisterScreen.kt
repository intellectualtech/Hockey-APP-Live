package com.example.hockeyapplive.screens

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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var fullname by remember { mutableStateOf("") }
    var idNo by remember { mutableStateOf("") }
    var yearOfExperience by remember { mutableStateOf("") }
    var teamName by remember { mutableStateOf("") }
    var teamYearsOfExistin by remember { mutableStateOf("") }
    var addressOfFieldOrCort by remember { mutableStateOf("") }
    var numberOfGamesYouPlayed by remember { mutableStateOf("") }
    var referenceOfCoachGameYouPlayedWithPast by remember { mutableStateOf("") }

    var fullnameError by remember { mutableStateOf<String?>(null) }
    var idNoError by remember { mutableStateOf<String?>(null) }
    var yearOfExperienceError by remember { mutableStateOf<String?>(null) }
    var teamNameError by remember { mutableStateOf<String?>(null) }
    var teamYearsOfExistinError by remember { mutableStateOf<String?>(null) }
    var addressOfFieldOrCortError by remember { mutableStateOf<String?>(null) }
    var numberOfGamesYouPlayedError by remember { mutableStateOf<String?>(null) }
    var referenceOfCoachGameYouPlayedWithPastError by remember { mutableStateOf<String?>(null) }

    var showTeamDetails by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) } // State for dialog visibility

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun validateCoachDetails(): Boolean {
        var isValid = true

        fullnameError = if (fullname.isBlank()) {
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
                            value = fullname,
                            onValueChange = { fullname = it },
                            label = { Text("fullname *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = fullnameError != null,
                            supportingText = { if (fullnameError != null) Text(fullnameError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = idNo,
                            onValueChange = { idNo = it },
                            label = { Text("id no *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = idNoError != null,
                            supportingText = { if (idNoError != null) Text(idNoError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = yearOfExperience,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) yearOfExperience = it },
                            label = { Text("Year of experience *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = yearOfExperienceError != null,
                            supportingText = { if (yearOfExperienceError != null) Text(yearOfExperienceError!!) }
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
                            label = { Text("team name *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamNameError != null,
                            supportingText = { if (teamNameError != null) Text(teamNameError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = teamYearsOfExistin,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) teamYearsOfExistin = it },
                            label = { Text("team years of existin *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = teamYearsOfExistinError != null,
                            supportingText = { if (teamYearsOfExistinError != null) Text(teamYearsOfExistinError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = addressOfFieldOrCort,
                            onValueChange = { addressOfFieldOrCort = it },
                            label = { Text("address of the field or cort *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = addressOfFieldOrCortError != null,
                            supportingText = { if (addressOfFieldOrCortError != null) Text(addressOfFieldOrCortError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = numberOfGamesYouPlayed,
                            onValueChange = { if (it.isEmpty() || it.all { char -> char.isDigit() }) numberOfGamesYouPlayed = it },
                            label = { Text("number of games you played *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            isError = numberOfGamesYouPlayedError != null,
                            supportingText = { if (numberOfGamesYouPlayedError != null) Text(numberOfGamesYouPlayedError!!) }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = referenceOfCoachGameYouPlayedWithPast,
                            onValueChange = { referenceOfCoachGameYouPlayedWithPast = it },
                            label = { Text("Reference of coach game you played with past *") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = referenceOfCoachGameYouPlayedWithPastError != null,
                            supportingText = { if (referenceOfCoachGameYouPlayedWithPastError != null) Text(referenceOfCoachGameYouPlayedWithPastError!!) }
                        )
                    }
                }

                Button(
                    onClick = {
                        if (validateTeamDetails()) {
                            showDialog = true // Show dialog on successful validation
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
                    navController.navigate("login") // Navigate to login screen
                }) {
                    Text("OK")
                }
            }
        )
    }
}