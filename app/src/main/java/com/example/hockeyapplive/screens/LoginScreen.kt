package com.example.hockeyapplive.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.DataSource
import com.example.hockeyapplive.data.User
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController, dataSourceInstance: DataSource) {
    val scope = rememberCoroutineScope()
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Separate function to handle login
    fun handleLogin() {
        if (username.isBlank() || password.isBlank()) {
            errorMessage = "Username and password are required"
            return
        }
        isLoading = true
        errorMessage = null
        scope.launch {
            try {
                val user = dataSourceInstance.authenticateUser(username, password)
                if (user != null) {
                    when (user.userType) {
                        "Admin" -> navController.navigate("admin") { popUpTo("login") { inclusive = true } }
                        "Coach" -> navController.navigate("coach") { popUpTo("login") { inclusive = true } }
                        "Player" -> navController.navigate("player") { popUpTo("login") { inclusive = true } }
                    }
                } else {
                    errorMessage = "Invalid username or password"
                }
            } catch (e: Exception) {
                errorMessage = "Login failed: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to Hockey APP",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username or Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            isError = errorMessage != null,
            singleLine = true
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            isError = errorMessage != null,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        errorMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        ElevatedButton(
            onClick = { handleLogin() },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
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
            onClick = { navController.navigate("register") },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Don't have an account? Register")
        }
    }
}