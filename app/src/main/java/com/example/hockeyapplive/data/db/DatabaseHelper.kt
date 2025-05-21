package com.example.hockeyapplive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.hockeyapplive.data.TeamRegistration

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "HockeyApp.db"
        const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Users table
        db.execSQL("""
            CREATE TABLE Users (
                userID INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                userPassword TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP,
                user_type TEXT NOT NULL CHECK(user_type IN ('Player', 'Coach', 'Admin')),
                isVerified INTEGER DEFAULT 0
            )
        """)

        // TeamRegistrations table
        db.execSQL("""
            CREATE TABLE TeamRegistrations (
                registrationID INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT NOT NULL,
                coachName TEXT NOT NULL,
                contact_email TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                status TEXT DEFAULT 'Pending' CHECK(status IN ('Pending', 'Approved', 'Rejected')),
                coach_userID INTEGER,
                FOREIGN KEY (coach_userID) REFERENCES Users(userID) ON DELETE SET NULL
            )
        """)

        // Teams table
        db.execSQL("""
            CREATE TABLE Teams (
                team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                coach_id INTEGER,
                contact_email TEXT,
                FOREIGN KEY (coach_id) REFERENCES Users(userID) ON DELETE SET NULL,
                UNIQUE (team_name, coach_id)
            )
        """)

        // Players table
        db.execSQL("""
            CREATE TABLE Players (
                player_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                team_id INTEGER,
                jersey_number INTEGER,
                position TEXT,
                date_of_birth TEXT,
                join_date TEXT,
                FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE,
                FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE SET NULL
            )
        """)

        // Events table
        db.execSQL("""
            CREATE TABLE Events (
                event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                event_name TEXT NOT NULL,
                event_description TEXT,
                event_DateTime TEXT NOT NULL,
                event_type TEXT NOT NULL CHECK(event_type IN ('Match', 'Tournament', 'Training', 'Meeting')),
                created_by INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (created_by) REFERENCES Users(userID) ON DELETE SET NULL
            )
        """)

        // Posts table
        db.execSQL("""
            CREATE TABLE Posts (
                post_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_id INTEGER,
                content TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                created_by INTEGER,
                FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                FOREIGN KEY (created_by) REFERENCES Users(userID) ON DELETE SET NULL
            )
        """)

        // Subscriptions table
        db.execSQL("""
            CREATE TABLE Subscriptions (
                subscription_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                team_id INTEGER,
                subscribed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE,
                FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                UNIQUE (user_id, team_id)
            )
        """)

        // Notifications table
        db.execSQL("""
            CREATE TABLE Notifications (
                notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER,
                title TEXT NOT NULL,
                content TEXT NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                is_read INTEGER DEFAULT 0,
                notification_type TEXT NOT NULL CHECK(notification_type IN ('Event', 'Message', 'Team', 'Registration', 'System')),
                FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE
            )
        """)

        // Insert demo data into Users table
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, user_type, isVerified)
            VALUES ('Demo User', 'demo@example.com', 'password123', 'Coach', 1)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, user_type, isVerified)
            VALUES ('John Player', 'player@example.com', 'player123', 'Player', 1)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, user_type, isVerified)
            VALUES ('Admin User', 'admin@example.com', 'admin123', 'Admin', 1)
        """)

        // Insert demo data into TeamRegistrations table
        db.execSQL("""
            INSERT INTO TeamRegistrations (team_name, coachName, contact_email, status, coach_userID)
            VALUES ('Demo Team', 'Demo User', 'demo@example.com', 'Pending', 1)
        """)

        // Insert demo data into Events table
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by)
            VALUES ('Windhoek HC VS Odibo HC', 'Match between Windhoek and Odibo', '2025-05-20 19:00:00', 'Match', 1)
        """)
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by)
            VALUES ('Owan Tournament', 'Tournament in Ohangwena', '2025-05-20 10:00:00', 'Tournament', 1)
        """)
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by)
            VALUES ('Friendly', 'Friendly match in Okahandja', '2025-05-20 14:00:00', 'Match', 1)
        """)

        // Insert demo data into Players table
        db.execSQL("""
            INSERT INTO Players (user_id, team_id, jersey_number, position, date_of_birth, join_date)
            VALUES (2, NULL, 10, 'Forward', '1995-01-01', '2025-01-01')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Notifications")
        db.execSQL("DROP TABLE IF EXISTS Subscriptions")
        db.execSQL("DROP TABLE IF EXISTS Posts")
        db.execSQL("DROP TABLE IF EXISTS Events")
        db.execSQL("DROP TABLE IF EXISTS Players")
        db.execSQL("DROP TABLE IF EXISTS Teams")
        db.execSQL("DROP TABLE IF EXISTS TeamRegistrations")
        db.execSQL("DROP TABLE IF EXISTS Users")
        onCreate(db)
    }

    // Existing TeamRegistration methods
    fun insertTeamRegistration(
        teamName: String,
        coachName: String,
        contactEmail: String,
        status: String,
        coachUserId: Int? = null
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("team_name", teamName)
            put("coachName", coachName)
            put("contact_email", contactEmail)
            put("status", status)
            if (coachUserId != null) {
                put("coach_userID", coachUserId)
            }
        }
        return db.insert("TeamRegistrations", null, values)
    }

    fun getAllTeamRegistrations(): List<TeamRegistration> {
        val registrations = mutableListOf<TeamRegistration>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TeamRegistrations", null)
        if (cursor.moveToFirst()) {
            do {
                val registration = TeamRegistration(
                    registrationID = cursor.getInt(cursor.getColumnIndexOrThrow("registrationID")),
                    teamName = cursor.getString(cursor.getColumnIndexOrThrow("team_name")),
                    coachName = cursor.getString(cursor.getColumnIndexOrThrow("coachName")),
                    contactEmail = cursor.getString(cursor.getColumnIndexOrThrow("contact_email")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                    coachUserID = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_userID"))) null
                    else cursor.getInt(cursor.getColumnIndexOrThrow("coach_userID"))
                )
                registrations.add(registration)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return registrations
    }

    fun deleteTeamRegistration(registrationId: Int): Int {
        val db = writableDatabase
        return db.delete("TeamRegistrations", "registrationID = ?", arrayOf(registrationId.toString()))
    }

    // Existing Event methods
    fun insertEvent(eventName: String, eventDescription: String, eventDateTime: String, eventType: String, createdBy: Int? = null): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("event_name", eventName)
            put("event_description", eventDescription)
            put("event_DateTime", eventDateTime)
            put("event_type", eventType)
            if (createdBy != null) {
                put("created_by", createdBy)
            }
        }
        return db.insert("Events", null, values)
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT event_id, event_name, event_description, event_DateTime, event_type FROM Events", null)
        if (cursor.moveToFirst()) {
            do {
                val eventId = cursor.getInt(cursor.getColumnIndexOrThrow("event_id"))
                val eventName = cursor.getString(cursor.getColumnIndexOrThrow("event_name"))
                val eventDescription = cursor.getString(cursor.getColumnIndexOrThrow("event_description")) ?: "No description"
                val eventDateTime = cursor.getString(cursor.getColumnIndexOrThrow("event_DateTime"))
                val eventType = cursor.getString(cursor.getColumnIndexOrThrow("event_type"))
                events.add(Event(eventId, eventName, eventDescription, eventDateTime, eventType))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return events
    }

    fun deleteEvent(eventId: Int): Int {
        val db = writableDatabase
        return db.delete("Events", "event_id = ?", arrayOf(eventId.toString()))
    }

    // New Player methods
    fun insertPlayer(fullName: String): Long {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Generate a unique email (simple approach for demo purposes)
            val email = "${fullName.replace(" ", "").lowercase()}@hockeyapp.com"
            // Check if email already exists
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ?",
                arrayOf(email)
            )
            val exists = try {
                cursor.moveToFirst() && cursor.getInt(0) > 0
            } finally {
                cursor.close()
            }
            if (exists) {
                throw Exception("Player with this name already exists")
            }

            // Insert into Users table
            val userValues = ContentValues().apply {
                put("fullName", fullName)
                put("email", email)
                put("userPassword", "default123") // Default password (consider a better approach in production)
                put("user_type", "Player")
                put("isVerified", 1)
            }
            val userId = db.insert("Users", null, userValues)
            if (userId == -1L) {
                throw Exception("Failed to create user")
            }

            // Insert into Players table
            val playerValues = ContentValues().apply {
                put("user_id", userId)
                // team_id is null as UI doesn't specify team selection
                // Add other fields if needed (jersey_number, position, etc.)
                put("join_date", "2025-05-21") // Current date for demo
            }
            val playerId = db.insert("Players", null, playerValues)
            if (playerId == -1L) {
                throw Exception("Failed to create player")
            }

            db.setTransactionSuccessful()
            return playerId
        } finally {
            db.endTransaction()
        }
    }

    fun getAllPlayers(): List<Player> {
        val players = mutableListOf<Player>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT p.player_id, p.user_id, u.fullName 
            FROM Players p 
            JOIN Users u ON p.user_id = u.userID
        """, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val playerId = cursor.getInt(cursor.getColumnIndexOrThrow("player_id"))
                    val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
                    val fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName"))
                    players.add(Player(playerId, userId, fullName))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return players
    }

    fun updatePlayerName(userId: Int, newName: String): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Check if email would conflict (generate new email based on name)
            val newEmail = "${newName.replace(" ", "").lowercase()}@hockeyapp.com"
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ? AND userID != ?",
                arrayOf(newEmail, userId.toString())
            )
            val exists = try {
                cursor.moveToFirst() && cursor.getInt(0) > 0
            } finally {
                cursor.close()
            }
            if (exists) {
                throw Exception("Player with this name already exists")
            }

            // Update Users table
            val values = ContentValues().apply {
                put("fullName", newName)
                put("email", newEmail)
            }
            val rowsAffected = db.update("Users", values, "userID = ?", arrayOf(userId.toString()))
            if (rowsAffected == 0) {
                throw Exception("Failed to update player name")
            }

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun deletePlayer(playerId: Int): Int {
        val db = writableDatabase
        return db.delete("Players", "player_id = ?", arrayOf(playerId.toString()))
    }
}

// Existing data classes
data class Event(
    val eventId: Int,
    val eventName: String,
    val eventDescription: String,
    val eventDateTime: String,
    val eventType: String
)

// New data class for Player
data class Player(
    val playerId: Int,
    val userId: Int,
    val fullName: String
)