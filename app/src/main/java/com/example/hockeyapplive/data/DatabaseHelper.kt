package com.example.hockeyapplive.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

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
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS Users")
        db.execSQL("DROP TABLE IF EXISTS TeamRegistrations")
        db.execSQL("DROP TABLE IF EXISTS Teams")
        db.execSQL("DROP TABLE IF EXISTS Players")
        db.execSQL("DROP TABLE IF EXISTS Events")
        db.execSQL("DROP TABLE IF EXISTS Posts")
        db.execSQL("DROP TABLE IF EXISTS Subscriptions")
        db.execSQL("DROP TABLE IF EXISTS Notifications")
        onCreate(db)
    }
}