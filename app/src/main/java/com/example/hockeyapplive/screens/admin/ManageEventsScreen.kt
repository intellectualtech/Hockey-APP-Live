package com.example.hockeyapplive.screens.admin

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.Event
import java.text.SimpleDateFormat
import java.util.*

// Professional navy blue color scheme
private val NavyBlue = Color(0xFF0A2463)
private val LightNavyBlue = Color(0xFF1E3A8A)
private val AccentBlue = Color(0xFF3E92CC)
private val LightGray = Color(0xFFF5F5F5)
private val DarkText = Color(0xFF333333)
private val SuccessGreen = Color(0xFF10B981)
private val WarningOrange = Color(0xFFF59E0B)
private val DangerRed = Color(0xFFEF4444)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageEventsScreen(navController: NavController, context: Context) {
    val dbHelper = remember { DatabaseHelper(context) }
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var newTitle by remember { mutableStateOf("") }
    var newDescription by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    // Edit dialog state
    var showEditDialog by remember { mutableStateOf(false) }
    var editingEvent by remember { mutableStateOf<Event?>(null) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }
    var editTime by remember { mutableStateOf("") }

    // Postpone dialog state
    var showPostponeDialog by remember { mutableStateOf(false) }
    var postponingEvent by remember { mutableStateOf<Event?>(null) }
    var postponeDate by remember { mutableStateOf("") }
    var postponeTime by remember { mutableStateOf("") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val displayDateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val displayTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    // Custom theme colors for this screen
    val customColorScheme = darkColorScheme(
        primary = AccentBlue,
        onPrimary = Color.White,
        secondary = LightNavyBlue,
        background = Color.White,
        surface = Color.White,
        onBackground = DarkText,
        onSurface = DarkText
    )

    // Fetch events when the screen is composed
    LaunchedEffect(Unit) {
        events = dbHelper.getAllEvents()
    }

    // Clear messages after 3 seconds
    LaunchedEffect(successMessage, errorMessage) {
        if (successMessage != null || errorMessage != null) {
            kotlinx.coroutines.delay(3000)
            successMessage = null
            errorMessage = null
        }
    }

    MaterialTheme(colorScheme = customColorScheme) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Manage Events", fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = NavyBlue,
                        titleContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = NavyBlue,
                    contentColor = Color.White
                ) {
                    NavigationBarItem(
                        icon = { Icon(Icons.Filled.Dashboard, contentDescription = "Dashboard") },
                        label = { Text("Dashboard") },
                        selected = false,
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
                        selected = false,
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
                        selected = false,
                        onClick = { navController.navigate("manage_players") },
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
                        selected = true,
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
                        selected = false,
                        onClick = { navController.navigate("admin_profile") },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AccentBlue,
                            unselectedIconColor = Color.White.copy(alpha = 0.8f),
                            selectedTextColor = AccentBlue,
                            unselectedTextColor = Color.White.copy(alpha = 0.8f),
                            indicatorColor = Color.White.copy(alpha = 0.1f)
                        )
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                // Success/Error Messages
                successMessage?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.CheckCircle,
                                contentDescription = "Success",
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it,
                                color = SuccessGreen,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                errorMessage?.let {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = DangerRed.copy(alpha = 0.1f)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Filled.Error,
                                contentDescription = "Error",
                                tint = DangerRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = it,
                                color = DangerRed,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                // Add New Event Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = LightGray),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "Add Event",
                                tint = NavyBlue,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Add New Event",
                                style = MaterialTheme.typography.titleLarge,
                                color = NavyBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedTextField(
                            value = newTitle,
                            onValueChange = { newTitle = it },
                            label = { Text("Event Title") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = NavyBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = newDescription,
                            onValueChange = { newDescription = it },
                            label = { Text("Event Description") },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AccentBlue,
                                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                                focusedLabelColor = AccentBlue,
                                unfocusedLabelColor = NavyBlue
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DatePickerField(
                                selectedDate = selectedDate,
                                onDateSelected = { selectedDate = it },
                                modifier = Modifier.weight(1f)
                            )

                            TimePickerField(
                                selectedTime = selectedTime,
                                onTimeSelected = { selectedTime = it },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                if (newTitle.isBlank() || selectedDate.isBlank() || selectedTime.isBlank()) {
                                    errorMessage = "Title, date, and time are required"
                                } else {
                                    try {
                                        val dateTime = "$selectedDate $selectedTime:00"
                                        val currentUserId = 1 // Assuming admin user ID
                                        dbHelper.insertEvent(newTitle, newDescription, dateTime, "Other", currentUserId)
                                        events = dbHelper.getAllEvents()
                                        newTitle = ""
                                        newDescription = ""
                                        selectedDate = ""
                                        selectedTime = ""
                                        successMessage = "Event added successfully!"
                                        errorMessage = null
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to add event: ${e.message}"
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NavyBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(8.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Add Event",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }

                // Events List Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Events (${events.size})",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )

                    if (events.isNotEmpty()) {
                        TextButton(
                            onClick = { events = dbHelper.getAllEvents() },
                            colors = ButtonDefaults.textButtonColors(contentColor = AccentBlue)
                        ) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Refresh")
                        }
                    }
                }

                if (events.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(containerColor = LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Filled.EventNote,
                                contentDescription = "No Events",
                                modifier = Modifier.size(48.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No events found",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Create your first event using the form above",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(events) { event ->
                            EventCard(
                                event = event,
                                onEdit = {
                                    editingEvent = event
                                    editTitle = event.eventName
                                    editDescription = event.eventDescription ?: ""
                                    val parts = event.eventDateTime.split(" ")
                                    editDate = parts[0]
                                    editTime = if (parts.size > 1) parts[1].substring(0, 5) else ""
                                    showEditDialog = true
                                },
                                onPostpone = {
                                    postponingEvent = event
                                    showPostponeDialog = true
                                },
                                onDelete = {
                                    try {
                                        dbHelper.deleteEvent(event.eventId)
                                        events = dbHelper.getAllEvents()
                                        successMessage = "Event deleted successfully!"
                                    } catch (e: Exception) {
                                        errorMessage = "Failed to delete event: ${e.message}"
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Edit Dialog
        if (showEditDialog && editingEvent != null) {
            EditEventDialog(
                event = editingEvent!!,
                title = editTitle,
                description = editDescription,
                date = editDate,
                time = editTime,
                onTitleChange = { editTitle = it },
                onDescriptionChange = { editDescription = it },
                onDateChange = { editDate = it },
                onTimeChange = { editTime = it },
                onSave = {
                    try {
                        if (editTitle.isBlank() || editDate.isBlank() || editTime.isBlank()) {
                            errorMessage = "Title, date, and time are required"
                            return@EditEventDialog
                        }
                        val dateTime = "$editDate $editTime:00"
                        val updatedEvent = editingEvent!!.copy(
                            eventName = editTitle,
                            eventDescription = editDescription,
                            eventDateTime = dateTime,
                            eventType = editingEvent!!.eventType,
                            createdBy = editingEvent!!.createdBy,
                            createdAt = editingEvent!!.createdAt
                        )
                        if (dbHelper.updateEvent(updatedEvent)) {
                            events = dbHelper.getAllEvents()
                            successMessage = "Event updated successfully!"
                            showEditDialog = false
                            editingEvent = null
                        } else {
                            errorMessage = "Failed to update event"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Failed to update event: ${e.message}"
                    }
                },
                onDismiss = {
                    showEditDialog = false
                    editingEvent = null
                }
            )
        }

        // Postpone Dialog
        if (showPostponeDialog && postponingEvent != null) {
            PostponeEventDialog(
                event = postponingEvent!!,
                date = postponeDate,
                time = postponeTime,
                onDateChange = { postponeDate = it },
                onTimeChange = { postponeTime = it },
                onSave = {
                    try {
                        if (postponeDate.isBlank() || postponeTime.isBlank()) {
                            errorMessage = "Date and time are required"
                            return@PostponeEventDialog
                        }
                        val dateTime = "$postponeDate $postponeTime:00"
                        val updatedEvent = postponingEvent!!.copy(
                            eventDateTime = dateTime,
                            createdBy = postponingEvent!!.createdBy,
                            createdAt = postponingEvent!!.createdAt
                        )
                        if (dbHelper.updateEvent(updatedEvent)) {
                            events = dbHelper.getAllEvents()
                            successMessage = "Event postponed successfully!"
                            showPostponeDialog = false
                            postponingEvent = null
                            postponeDate = ""
                            postponeTime = ""
                        } else {
                            errorMessage = "Failed to postpone event"
                        }
                    } catch (e: Exception) {
                        errorMessage = "Failed to postpone event: ${e.message}"
                    }
                },
                onDismiss = {
                    showPostponeDialog = false
                    postponingEvent = null
                    postponeDate = ""
                    postponeTime = ""
                }
            )
        }
    }

    // Clean up
    DisposableEffect(Unit) {
        onDispose {
            dbHelper.close()
        }
    }
}

@Composable
fun EventCard(
    event: Event,
    onEdit: () -> Unit,
    onPostpone: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.eventName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = NavyBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Schedule,
                            contentDescription = "Date",
                            modifier = Modifier.size(16.dp),
                            tint = AccentBlue
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = event.eventDateTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = event.eventDescription ?: "No description",
                        style = MaterialTheme.typography.bodyMedium,
                        color = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Edit Button
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AccentBlue
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(AccentBlue.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Edit",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Edit", style = MaterialTheme.typography.bodySmall)
                }

                // Postpone Button
                OutlinedButton(
                    onClick = onPostpone,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = WarningOrange
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(WarningOrange.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Schedule,
                        contentDescription = "Postpone",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Postpone", style = MaterialTheme.typography.bodySmall)
                }

                // Delete Button
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = DangerRed
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = SolidColor(DangerRed.copy(alpha = 0.5f))
                    ),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Delete", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerField(
    selectedDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val displayFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        OutlinedTextField(
            value = if (selectedDate.isNotEmpty()) {
                try {
                    displayFormat.format(dateFormat.parse(selectedDate)!!)
                } catch (e: Exception) {
                    selectedDate
                }
            } else "",
            onValueChange = { },
            label = { Text("Select Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.DateRange,
                    contentDescription = "Select Date",
                    modifier = Modifier.size(20.dp),
                    tint = AccentBlue
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = NavyBlue
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Invisible clickable overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable {
                    DatePickerDialog(
                        context,
                        { _, year, month, dayOfMonth ->
                            calendar.set(year, month, dayOfMonth)
                            onDateSelected(dateFormat.format(calendar.time))
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                    ).show()
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    selectedTime: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val displayFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
    ) {
        OutlinedTextField(
            value = if (selectedTime.isNotEmpty()) {
                try {
                    displayFormat.format(timeFormat.parse(selectedTime)!!)
                } catch (e: Exception) {
                    selectedTime
                }
            } else "",
            onValueChange = { },
            label = { Text("Select Time") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            trailingIcon = {
                Icon(
                    Icons.Filled.Schedule,
                    contentDescription = "Select Time",
                    modifier = Modifier.size(20.dp),
                    tint = AccentBlue
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AccentBlue,
                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                focusedLabelColor = AccentBlue,
                unfocusedLabelColor = NavyBlue
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true
        )

        // Invisible clickable overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Transparent)
                .clickable {
                    TimePickerDialog(
                        context,
                        { _, hourOfDay, minute ->
                            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                            calendar.set(Calendar.MINUTE, minute)
                            onTimeSelected(timeFormat.format(calendar.time))
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false // 12-hour format with AM/PM
                    ).show()
                }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventDialog(
    event: Event,
    title: String,
    description: String,
    date: String,
    time: String,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Edit Event",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Event Title") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = onDescriptionChange,
                    label = { Text("Event Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                        focusedLabelColor = AccentBlue,
                        unfocusedLabelColor = NavyBlue
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatePickerField(
                        selectedDate = date,
                        onDateSelected = onDateChange,
                        modifier = Modifier.weight(1f)
                    )

                    TimePickerField(
                        selectedTime = time,
                        onTimeSelected = onTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Save",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostponeEventDialog(
    event: Event,
    date: String,
    time: String,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = LightGray),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Postpone Event: ${event.eventName}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = NavyBlue
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DatePickerField(
                        selectedDate = date,
                        onDateSelected = onDateChange,
                        modifier = Modifier.weight(1f)
                    )

                    TimePickerField(
                        selectedTime = time,
                        onTimeSelected = onTimeChange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DangerRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Cancel",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Button(
                        onClick = onSave,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            "Postpone",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}