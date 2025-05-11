package com.example.hockeyapplive.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.datasource.DataSource
import com.example.hockeyapplive.data.model.User
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, context: android.content.Context) {
    val (username, setUsername) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }
    val (errorMessage, setErrorMessage) = remember { mutableStateOf<String?>(null) }
    val (isLoading, setIsLoading) = remember { mutableStateOf(false) }
    val (showRegisterDialog, setShowRegisterDialog) = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val dataSource = remember { DataSource(context) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "Demo credentials:\n" +
                        "Coach: demo@example.com / password123\n" +
                        "Player: player@example.com / player123\n" +
                        "Admin: admin@example.com / admin123",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = username,
                onValueChange = setUsername,
                label = { Text("Username or Email") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = password,
                onValueChange = setPassword,
                label = { Text("Password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                enabled = !isLoading
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            Button(
                onClick = {
                    setErrorMessage(null)
                    setIsLoading(true)
                    scope.launch {
                        try {
                            val user = dataSource.authenticateUser(username, password)
                            if (user != null) {
                                if (user.isVerified) {
                                    // Successful login, navigate based on user type
                                    when (user.userType) {
                                        "Admin" -> navController.navigate("admin_dashboard") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        "Coach" -> navController.navigate("onboarding") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        "Player" -> navController.navigate("settings") {
                                            popUpTo(navController.graph.startDestinationId)
                                            launchSingleTop = true
                                        }
                                        else -> setErrorMessage("Unknown user type")
                                    }
                                } else {
                                    setErrorMessage("Account not verified. Please check your email.")
                                }
                            } else {
                                // Failed login, show error
                                setErrorMessage("Invalid username/email or password")
                            }
                        } catch (e: Exception) {
                            setErrorMessage("An error occurred during login: ${e.message}")
                        } finally {
                            setIsLoading(false)
                            dataSource.close()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Login")
                }
            }

            TextButton(
                onClick = { setShowRegisterDialog(true) },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Don't have an account? Register")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    navController.navigate("admin_dashboard") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Text("Admin")
            }

            Button(
                onClick = {
                    navController.navigate("onboarding") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                )
            ) {
                Text("Onboard")
            }

            Button(
                onClick = {
                    navController.navigate("register") {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text("Register")
            }
        }
    }

    // Registration Dialog
    if (showRegisterDialog) {
        val (regFullName, setRegFullName) = remember { mutableStateOf("") }
        val (regEmail, setRegEmail) = remember { mutableStateOf("") }
        val (regPassword, setRegPassword) = remember { mutableStateOf("") }
        val (regUserType, setRegUserType) = remember { mutableStateOf("Player") }
        val (regError, setRegError) = remember { mutableStateOf<String?>(null) }
        var expanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { setShowRegisterDialog(false) },
            title = { Text("Register") },
            text = {
                Column {
                    OutlinedTextField(
                        value = regFullName,
                        onValueChange = setRegFullName,
                        label = { Text("Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = regEmail,
                        onValueChange = setRegEmail,
                        label = { Text("Email") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                    OutlinedTextField(
                        value = regPassword,
                        onValueChange = setRegPassword,
                        label = { Text("Password") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = regUserType,
                            onValueChange = {},
                            label = { Text("User Type") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            readOnly = true,
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            }
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Player") },
                                onClick = {
                                    setRegUserType("Player")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Coach") },
                                onClick = {
                                    setRegUserType("Coach")
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Admin") },
                                onClick = {
                                    setRegUserType("Admin")
                                    expanded = false
                                }
                            )
                        }
                    }
                    if (regError != null) {
                        Text(
                            text = regError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (regFullName.isBlank() || regEmail.isBlank() || regPassword.isBlank()) {
                            setRegError("All fields are required")
                        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
                            setRegError("Invalid email format")
                        } else if (regPassword.length < 6) {
                            setRegError("Password must be at least 6 characters")
                        } else {
                            scope.launch {
                                val result = dataSource.registerUser(regFullName, regEmail, regPassword, regUserType)
                                result.onSuccess {
                                    setShowRegisterDialog(false)
                                    snackbarHostState.showSnackbar("Registration successful. Please log in.")
                                    setUsername(regEmail)
                                    setPassword(regPassword)
                                }.onFailure { e ->
                                    setRegError(e.message ?: "Registration failed")
                                }
                            }
                        }
                    }
                ) {
                    Text("Register")
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowRegisterDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}