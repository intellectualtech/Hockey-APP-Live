package com.example.hockeyapplive.screens

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import kotlinx.coroutines.launch

@Composable
fun LogoutScreen(navController: NavController) {
    val context: Context = LocalContext.current
    val dbHelper = DatabaseHelper(context)
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(HockeyAppTheme.White)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Logout",
                style = MaterialTheme.typography.headlineMedium,
                color = HockeyAppTheme.TextColor,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Are you sure you want to logout?",
                style = MaterialTheme.typography.bodyLarge,
                color = HockeyAppTheme.TextColor
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            try {
                                val isCleared = dbHelper.clearUserSession(context)
                                if (isCleared) {
                                    navController.navigate("login") {
                                        popUpTo(navController.graph.startDestinationId) {
                                            inclusive = true
                                        }
                                    }
                                } else {
                                    snackbarHostState.showSnackbar(
                                        message = "Failed to clear session. Please try again.",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    message = "Error during logout: ${e.message}",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = HockeyAppTheme.LightNavyBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Confirm Logout", color = HockeyAppTheme.White)
                }

                OutlinedButton(
                    onClick = {
                        navController.popBackStack()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, HockeyAppTheme.LightNavyBlue)
                ) {
                    Text("Cancel", color = HockeyAppTheme.LightNavyBlue)
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            dbHelper.close()
        }
    }
}