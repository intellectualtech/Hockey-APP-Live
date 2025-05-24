package com.example.hockeyapplive.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hockeyapplive.data.model.Event
import com.example.hockeyapplive.data.model.Player
import com.example.hockeyapplive.data.model.Team
import com.example.hockeyapplive.data.model.TeamRegistration
import com.example.hockeyapplive.data.model.User
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "HockeyApp.db"
        const val DATABASE_VERSION = 10 // Current version
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(db: SQLiteDatabase) {
        // Users table with contact_number as INTEGER
        db.execSQL("""
            CREATE TABLE Users (
                userID INTEGER PRIMARY KEY AUTOINCREMENT,
                fullName TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                userPassword TEXT NOT NULL,
                created_at TEXT DEFAULT (datetime('now')),
                last_login TEXT,
                user_type TEXT NOT NULL CHECK(user_type IN ('Player', 'Coach', 'Admin')),
                isVerified INTEGER DEFAULT 0,
                contact_number INTEGER
            )
        """)

        // TeamRegistrations table with additional fields
        db.execSQL("""
            CREATE TABLE TeamRegistrations (
                registrationID INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT NOT NULL,
                coachName TEXT NOT NULL,
                contact_email TEXT NOT NULL,
                created_at TEXT DEFAULT (datetime('now')),
                status TEXT DEFAULT 'Pending' CHECK(status IN ('Pending', 'Approved', 'Rejected')),
                coach_userID INTEGER,
                years_of_existence INTEGER,
                field_address TEXT,
                games_played INTEGER,
                coach_reference TEXT,
                coach_id_no TEXT,
                coach_experience_years INTEGER,
                FOREIGN KEY (coach_userID) REFERENCES Users(userID) ON DELETE SET NULL
            )
        """)

        // Teams table
        db.execSQL("""
            CREATE TABLE Teams (
                team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_name TEXT NOT NULL,
                created_at TEXT DEFAULT (datetime('now')),
                coach_id INTEGER,
                contact_email TEXT,
                years_of_existence INTEGER,
                field_address TEXT,
                games_played INTEGER,
                coach_reference TEXT,
                coach_id_no TEXT,
                coach_experience_years INTEGER,
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
                age INTEGER,
                height REAL,
                emergency_contact TEXT,
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
                created_at TEXT DEFAULT (datetime('now')),
                FOREIGN KEY (created_by) REFERENCES Users(userID) ON DELETE SET NULL
            )
        """)

        // Posts table
        db.execSQL("""
            CREATE TABLE Posts (
                post_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team_id INTEGER,
                content TEXT NOT NULL,
                created_at TEXT DEFAULT (datetime('now')),
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
                subscribed_at TEXT DEFAULT (datetime('now')),
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
                created_at TEXT DEFAULT (datetime('now')),
                is_read INTEGER DEFAULT 0,
                notification_type TEXT NOT NULL CHECK(notification_type IN ('Event', 'Message', 'Team', 'Registration', 'System')),
                FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE
            )
        """)

        // Games table
        db.execSQL("""
            CREATE TABLE Games (
                game_id INTEGER PRIMARY KEY AUTOINCREMENT,
                team1_id INTEGER NOT NULL,
                team2_id INTEGER NOT NULL,
                team1_score INTEGER NOT NULL,
                team2_score INTEGER NOT NULL,
                game_date TEXT NOT NULL,
                location TEXT,
                status TEXT NOT NULL CHECK(status IN ('Scheduled', 'Completed', 'Cancelled')),
                FOREIGN KEY (team1_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                FOREIGN KEY (team2_id) REFERENCES Teams(team_id) ON DELETE CASCADE
            )
        """)

        // Insert demo data with current timestamp (10:44 AM CAT, May 23, 2025)
        val timestampFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val now = LocalDateTime.now().format(timestampFormatter) // 2025-05-23 10:44:00

        // Users
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, created_at, last_login, user_type, isVerified, contact_number)
            VALUES ('Demo Coach', 'coach@example.com', 'coach123', '$now', NULL, 'Coach', 1, 8112345670)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, created_at, last_login, user_type, isVerified, contact_number)
            VALUES ('John Player', 'player@example.com', 'player123', '$now', NULL, 'Player', 1, 8198765430)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, created_at, last_login, user_type, isVerified, contact_number)
            VALUES ('Admin User', 'admin@example.com', 'admin123', '$now', NULL, 'Admin', 1, 8155555550)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, created_at, last_login, user_type, isVerified, contact_number)
            VALUES ('Jane Coach', 'jane.coach@example.com', 'jane123', '$now', NULL, 'Coach', 0, 8176543210)
        """)
        db.execSQL("""
            INSERT INTO Users (fullName, email, userPassword, created_at, last_login, user_type, isVerified, contact_number)
            VALUES ('Mike Player', 'mike.player@example.com', 'mike123', '$now', NULL, 'Player', 1, 8187654320)
        """)

        // Teams
        db.execSQL("""
            INSERT INTO Teams (team_name, created_at, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Windhoek HC', '$now', 1, 'coach@example.com', 5, '123 Main St, Windhoek', 20, 'Coach John, 2024 Tournament', 'ID123456', 10)
        """)
        db.execSQL("""
            INSERT INTO Teams (team_name, created_at, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Odibo HC', '$now', 4, 'jane.coach@example.com', 3, '456 North Rd, Odibo', 15, 'Coach Jane, 2023 Match', 'ID789012', 8)
        """)
        db.execSQL("""
            INSERT INTO Teams (team_name, created_at, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Okahandja HC', '$now', 1, 'coach@example.com', 2, '789 South Ave, Okahandja', 10, 'Coach Mike, 2022 Friendly', 'ID345678', 5)
        """)

        // TeamRegistrations
        db.execSQL("""
            INSERT INTO TeamRegistrations (team_name, coachName, contact_email, created_at, status, coach_userID, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Windhoek HC', 'Demo Coach', 'coach@example.com', '$now', 'Approved', 1, 5, '123 Main St, Windhoek', 20, 'Coach John, 2024 Tournament', 'ID123456', 10)
        """)
        db.execSQL("""
            INSERT INTO TeamRegistrations (team_name, coachName, contact_email, created_at, status, coach_userID, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Odibo HC', 'Jane Coach', 'jane.coach@example.com', '$now', 'Pending', 4, 3, '456 North Rd, Odibo', 15, 'Coach Jane, 2023 Match', 'ID789012', 8)
        """)
        db.execSQL("""
            INSERT INTO TeamRegistrations (team_name, coachName, contact_email, created_at, status, coach_userID, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
            VALUES ('Rundu HC', 'Demo Coach', 'coach@example.com', '$now', 'Rejected', 1, 2, '789 South Ave, Rundu', 10, 'Coach Mike, 2022 Friendly', 'ID345678', 5)
        """)

        // Events
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by, created_at)
            VALUES ('Windhoek HC vs Odibo HC', 'Match between Windhoek and Odibo', '2025-05-20 19:00:00', 'Match', 1, '$now')
        """)
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by, created_at)
            VALUES ('Owan Tournament', 'Tournament in Ohangwena', '2025-05-20 10:00:00', 'Tournament', 1, '$now')
        """)
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by, created_at)
            VALUES ('Team Training', 'Training session for Windhoek HC', '2025-05-21 09:00:00', 'Training', 4, '$now')
        """)
        db.execSQL("""
            INSERT INTO Events (event_name, event_description, event_DateTime, event_type, created_by, created_at)
            VALUES ('Coach Meeting', 'Strategy meeting for coaches', '2025-05-22 14:00:00', 'Meeting', 3, '$now')
        """)

        // Players
        db.execSQL("""
            INSERT INTO Players (user_id, team_id, jersey_number, position, age, height, emergency_contact, date_of_birth, join_date)
            VALUES (2, 1, 10, 'Forward', 30, 180.5, '123-456-7890', '1995-01-01', '2025-01-01')
        """)
        db.execSQL("""
            INSERT INTO Players (user_id, team_id, jersey_number, position, age, height, emergency_contact, date_of_birth, join_date)
            VALUES (5, 2, 7, 'Defender', 25, 175.0, '987-654-3210', '2000-03-15', '2025-02-01')
        """)

        // Games
        db.execSQL("""
            INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
            VALUES (1, 2, 3, 2, '2025-05-20 15:00:00', 'Arena A', 'Completed')
        """)
        db.execSQL("""
            INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
            VALUES (1, 3, 1, 4, '2025-05-18 12:00:00', 'Arena B', 'Completed')
        """)
        db.execSQL("""
            INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
            VALUES (2, 3, 0, 0, '2025-05-25 14:00:00', 'Arena C', 'Scheduled')
        """)
        db.execSQL("""
            INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
            VALUES (1, 2, 0, 0, '2025-05-19 16:00:00', 'Arena A', 'Cancelled')
        """)

        // Posts
        db.execSQL("""
            INSERT INTO Posts (team_id, content, created_at, created_by)
            VALUES (1, 'Great win against Odibo HC!', '$now', 1)
        """)
        db.execSQL("""
            INSERT INTO Posts (team_id, content, created_at, created_by)
            VALUES (2, 'Training session scheduled for tomorrow.', '$now', 4)
        """)

        // Subscriptions
        db.execSQL("""
            INSERT INTO Subscriptions (user_id, team_id, subscribed_at)
            VALUES (2, 1, '$now')
        """)
        db.execSQL("""
            INSERT INTO Subscriptions (user_id, team_id, subscribed_at)
            VALUES (5, 2, '$now')
        """)
        db.execSQL("""
            INSERT INTO Subscriptions (user_id, team_id, subscribed_at)
            VALUES (3, 1, '$now')
        """)

        // Notifications
        db.execSQL("""
            INSERT INTO Notifications (user_id, title, content, created_at, is_read, notification_type)
            VALUES (2, 'New Match Scheduled', 'Windhoek HC vs Odibo HC on 2025-05-20', '$now', 0, 'Event')
        """)
        db.execSQL("""
            INSERT INTO Notifications (user_id, title, content, created_at, is_read, notification_type)
            VALUES (3, 'Team Registration Approved', 'Your registration for Windhoek HC has been approved.', '$now', 1, 'Registration')
        """)
        db.execSQL("""
            INSERT INTO Notifications (user_id, title, content, created_at, is_read, notification_type)
            VALUES (5, 'Team Update', 'New training session added for Odibo HC.', '$now', 0, 'Team')
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("""
                CREATE TABLE Games (
                    game_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team1_id INTEGER NOT NULL,
                    team2_id INTEGER NOT NULL,
                    team1_score INTEGER NOT NULL,
                    team2_score INTEGER NOT NULL,
                    game_date TEXT NOT NULL,
                    location TEXT,
                    status TEXT NOT NULL CHECK(status IN ('Scheduled', 'Completed', 'Cancelled')),
                    FOREIGN KEY (team1_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                    FOREIGN KEY (team2_id) REFERENCES Teams(team_id) ON DELETE CASCADE
                )
            """)
            db.execSQL("""
                INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
                VALUES (1, 2, 3, 2, '2025-05-20 15:00:00', 'Arena A', 'Completed')
            """)
            db.execSQL("""
                INSERT INTO Games (team1_id, team2_id, team1_score, team2_score, game_date, location, status)
                VALUES (1, 3, 1, 4, '2025-05-18 12:00:00', 'Arena B', 'Completed')
            """)
        }
        if (oldVersion < 3) {
            db.execSQL("ALTER TABLE Teams ADD COLUMN years_of_existence INTEGER")
            db.execSQL("ALTER TABLE Teams ADD COLUMN field_address TEXT")
            db.execSQL("ALTER TABLE Teams ADD COLUMN games_played INTEGER")
            db.execSQL("ALTER TABLE Teams ADD COLUMN coach_reference TEXT")
            db.execSQL("ALTER TABLE Teams ADD COLUMN coach_id_no TEXT")
            db.execSQL("ALTER TABLE Teams ADD COLUMN coach_experience_years INTEGER")
        }
        if (oldVersion < 4) {
            db.execSQL("ALTER TABLE Players ADD COLUMN age INTEGER")
            db.execSQL("ALTER TABLE Players ADD COLUMN height REAL")
            db.execSQL("ALTER TABLE Players ADD COLUMN emergency_contact TEXT")
        }
        if (oldVersion < 5) {
            db.execSQL("ALTER TABLE Users RENAME TO Users_old")
            db.execSQL("""
                CREATE TABLE Users (
                    userID INTEGER PRIMARY KEY AUTOINCREMENT,
                    fullName TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    userPassword TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now')),
                    last_login TEXT,
                    user_type TEXT NOT NULL CHECK(user_type IN ('Player', 'Coach', 'Admin')),
                    isVerified INTEGER DEFAULT 0
                )
            """)
            db.execSQL("""
                INSERT INTO Users (userID, fullName, email, userPassword, created_at, last_login, user_type, isVerified)
                SELECT userID, fullName, email, userPassword, created_at, last_login, user_type, isVerified
                FROM Users_old
            """)
            db.execSQL("DROP TABLE Users_old")

            db.execSQL("ALTER TABLE Teams RENAME TO Teams_old")
            db.execSQL("""
                CREATE TABLE Teams (
                    team_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_name TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now')),
                    coach_id INTEGER,
                    contact_email TEXT,
                    years_of_existence INTEGER,
                    field_address TEXT,
                    games_played INTEGER,
                    coach_reference TEXT,
                    coach_id_no TEXT,
                    coach_experience_years INTEGER,
                    FOREIGN KEY (coach_id) REFERENCES Users(userID) ON DELETE SET NULL,
                    UNIQUE (team_name, coach_id)
                )
            """)
            db.execSQL("""
                INSERT INTO Teams (team_id, team_name, created_at, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years)
                SELECT team_id, team_name, created_at, coach_id, contact_email, years_of_existence, field_address, games_played, coach_reference, coach_id_no, coach_experience_years
                FROM Teams_old
            """)
            db.execSQL("DROP TABLE Teams_old")

            db.execSQL("ALTER TABLE TeamRegistrations RENAME TO TeamRegistrations_old")
            db.execSQL("""
                CREATE TABLE TeamRegistrations (
                    registrationID INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_name TEXT NOT NULL,
                    coachName TEXT NOT NULL,
                    contact_email TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now')),
                    status TEXT DEFAULT 'Pending' CHECK(status IN ('Pending', 'Approved', 'Rejected')),
                    coach_userID INTEGER,
                    FOREIGN KEY (coach_userID) REFERENCES Users(userID) ON DELETE SET NULL
                )
            """)
            db.execSQL("""
                INSERT INTO TeamRegistrations (registrationID, team_name, coachName, contact_email, created_at, status, coach_userID)
                SELECT registrationID, team_name, coachName, contact_email, created_at, status, coach_userID
                FROM TeamRegistrations_old
            """)
            db.execSQL("DROP TABLE TeamRegistrations_old")

            db.execSQL("ALTER TABLE Events RENAME TO Events_old")
            db.execSQL("""
                CREATE TABLE Events (
                    event_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    event_name TEXT NOT NULL,
                    event_description TEXT,
                    event_DateTime TEXT NOT NULL,
                    event_type TEXT NOT NULL CHECK(event_type IN ('Match', 'Tournament', 'Training', 'Meeting')),
                    created_by INTEGER,
                    created_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY (created_by) REFERENCES Users(userID) ON DELETE SET NULL
                )
            """)
            db.execSQL("""
                INSERT INTO Events (event_id, event_name, event_description, event_DateTime, event_type, created_by, created_at)
                SELECT event_id, event_name, event_description, event_DateTime, event_type, created_by, created_at
                FROM Events_old
            """)
            db.execSQL("DROP TABLE Events_old")

            db.execSQL("ALTER TABLE Posts RENAME TO Posts_old")
            db.execSQL("""
                CREATE TABLE Posts (
                    post_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team_id INTEGER,
                    content TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now')),
                    created_by INTEGER,
                    FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                    FOREIGN KEY (created_by) REFERENCES Users(userID) ON DELETE SET NULL
                )
            """)
            db.execSQL("""
                INSERT INTO Posts (post_id, team_id, content, created_at, created_by)
                SELECT post_id, team_id, content, created_at, created_by
                FROM Posts_old
            """)
            db.execSQL("DROP TABLE Posts_old")

            db.execSQL("ALTER TABLE Subscriptions RENAME TO Subscriptions_old")
            db.execSQL("""
                CREATE TABLE Subscriptions (
                    subscription_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    team_id INTEGER,
                    subscribed_at TEXT DEFAULT (datetime('now')),
                    FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE,
                    FOREIGN KEY (team_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                    UNIQUE (user_id, team_id)
                )
            """)
            db.execSQL("""
                INSERT INTO Subscriptions (subscription_id, user_id, team_id, subscribed_at)
                SELECT subscription_id, user_id, team_id, subscribed_at
                FROM Subscriptions_old
            """)
            db.execSQL("DROP TABLE Subscriptions_old")

            db.execSQL("ALTER TABLE Notifications RENAME TO Notifications_old")
            db.execSQL("""
                CREATE TABLE Notifications (
                    notification_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER,
                    title TEXT NOT NULL,
                    content TEXT NOT NULL,
                    created_at TEXT DEFAULT (datetime('now')),
                    is_read INTEGER DEFAULT 0,
                    notification_type TEXT NOT NULL CHECK(notification_type IN ('Event', 'Message', 'Team', 'Registration', 'System')),
                    FOREIGN KEY (user_id) REFERENCES Users(userID) ON DELETE CASCADE
                )
            """)
            db.execSQL("""
                INSERT INTO Notifications (notification_id, user_id, title, content, created_at, is_read, notification_type)
                SELECT notification_id, user_id, title, content, created_at, is_read, notification_type
                FROM Notifications_old
            """)
            db.execSQL("DROP TABLE Notifications_old")
        }
        if (oldVersion < 6) {
            db.execSQL("ALTER TABLE Users ADD COLUMN contact_number INTEGER")
            db.execSQL("UPDATE Users SET contact_number = 8112345670 WHERE user_type = 'Coach'")
            db.execSQL("UPDATE Users SET contact_number = 8198765430 WHERE user_type = 'Player'")
            db.execSQL("UPDATE Users SET contact_number = 8155555550 WHERE user_type = 'Admin'")
        }
        if (oldVersion < 7) {
            db.execSQL("ALTER TABLE Games RENAME TO Games_old")
            db.execSQL("""
                CREATE TABLE Games (
                    game_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    team1_id INTEGER NOT NULL,
                    team2_id INTEGER NOT NULL,
                    team1_score INTEGER NOT NULL,
                    team2_score INTEGER NOT NULL,
                    game_date TEXT NOT NULL,
                    location TEXT,
                    status TEXT NOT NULL CHECK(status IN ('Scheduled', 'Completed', 'Cancelled')),
                    FOREIGN KEY (team1_id) REFERENCES Teams(team_id) ON DELETE CASCADE,
                    FOREIGN KEY (team2_id) REFERENCES Teams(team_id) ON DELETE CASCADE
                )
            """)
            db.execSQL("""
                INSERT INTO Games (game_id, team1_id, team2_id, team1_score, team2_score, game_date, location, status)
                SELECT game_id, team1_id, team2_id, team1_score, team2_score,
                       REPLACE(REPLACE(game_date, 'T', ' '), 'Z', '') AS game_date,
                       location, status
                FROM Games_old
            """)
            db.execSQL("DROP TABLE Games_old")
        }
        if (oldVersion < 8) {
            db.execSQL("""
                UPDATE Users SET contact_number = 
                    CASE 
                        WHEN contact_number > 2147483647 THEN 2147483647
                        WHEN contact_number < -2147483648 THEN -2147483648
                        ELSE contact_number
                    END
            """)
        }
        if (oldVersion < 9) {
            db.execSQL("""
                UPDATE Users SET isVerified = 0 WHERE email = 'jane.coach@example.com' AND user_type = 'Coach'
            """)
        }
        if (oldVersion < 10) {
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN years_of_existence INTEGER")
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN field_address TEXT")
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN games_played INTEGER")
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN coach_reference TEXT")
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN coach_id_no TEXT")
            db.execSQL("ALTER TABLE TeamRegistrations ADD COLUMN coach_experience_years INTEGER")
            db.execSQL("""
                UPDATE TeamRegistrations 
                SET years_of_existence = 5, field_address = '123 Main St, Windhoek', games_played = 20, 
                    coach_reference = 'Coach John, 2024 Tournament', coach_id_no = 'ID123456', coach_experience_years = 10
                WHERE team_name = 'Windhoek HC'
            """)
            db.execSQL("""
                UPDATE TeamRegistrations 
                SET years_of_existence = 3, field_address = '456 North Rd, Odibo', games_played = 15, 
                    coach_reference = 'Coach Jane, 2023 Match', coach_id_no = 'ID789012', coach_experience_years = 8
                WHERE team_name = 'Odibo HC'
            """)
            db.execSQL("""
                UPDATE TeamRegistrations 
                SET years_of_existence = 2, field_address = '789 South Ave, Rundu', games_played = 10, 
                    coach_reference = 'Coach Mike, 2022 Friendly', coach_id_no = 'ID345678', coach_experience_years = 5
                WHERE team_name = 'Rundu HC'
            """)
        }
    }

    fun getAdminUser(): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE user_type = 'Admin' LIMIT 1",
            null
        )
        try {
            if (cursor.moveToFirst()) {
                return User(
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow("userID")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    userPassword = cursor.getString(cursor.getColumnIndexOrThrow("userPassword")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    lastLogin = cursor.getString(cursor.getColumnIndexOrThrow("last_login")),
                    userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type")),
                    isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("isVerified")) == 1,
                    contactNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("contact_number"))) 0L
                    else cursor.getInt(cursor.getColumnIndexOrThrow("contact_number")).toLong()
                )
            }
        } finally {
            cursor.close()
        }
        return null
    }

    fun getUserById(userId: Int): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE userID = ?",
            arrayOf(userId.toString())
        )
        try {
            if (cursor.moveToFirst()) {
                return User(
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow("userID")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    userPassword = cursor.getString(cursor.getColumnIndexOrThrow("userPassword")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    lastLogin = cursor.getString(cursor.getColumnIndexOrThrow("last_login")),
                    userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type")),
                    isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("isVerified")) == 1,
                    contactNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("contact_number"))) 0L
                    else cursor.getInt(cursor.getColumnIndexOrThrow("contact_number")).toLong()
                )
            }
        } finally {
            cursor.close()
        }
        return null
    }

    fun getUserByEmail(email: String): User? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE email = ?",
            arrayOf(email)
        )
        try {
            if (cursor.moveToFirst()) {
                return User(
                    userID = cursor.getInt(cursor.getColumnIndexOrThrow("userID")),
                    fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                    email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                    userPassword = cursor.getString(cursor.getColumnIndexOrThrow("userPassword")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                    lastLogin = cursor.getString(cursor.getColumnIndexOrThrow("last_login")),
                    userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type")),
                    isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("isVerified")) == 1,
                    contactNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("contact_number"))) 0L
                    else cursor.getInt(cursor.getColumnIndexOrThrow("contact_number")).toLong()
                )
            }
        } finally {
            cursor.close()
        }
        return null
    }

    fun setUserVerified(userId: Int, isVerified: Boolean): Boolean {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE userID = ? AND user_type = 'Coach'",
                arrayOf(userId.toString())
            )
            val userExists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (!userExists) throw IllegalArgumentException("Coach with ID $userId does not exist")

            val values = ContentValues().apply {
                put("isVerified", if (isVerified) 1 else 0)
            }
            val rowsAffected = db.update("Users", values, "userID = ?", arrayOf(userId.toString()))
            if (rowsAffected == 0) throw IllegalArgumentException("Failed to update verification status for user ID $userId")

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun insertNotification(userId: Int, title: String, content: String, notificationType: String): Long {
        if (title.isBlank()) throw IllegalArgumentException("Notification title cannot be empty")
        if (content.isBlank()) throw IllegalArgumentException("Notification content cannot be empty")
        if (notificationType !in listOf("Event", "Message", "Team", "Registration", "System")) {
            throw IllegalArgumentException("Invalid notification type")
        }

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE userID = ?",
                arrayOf(userId.toString())
            )
            val userExists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (!userExists) throw IllegalArgumentException("User with ID $userId does not exist")

            val values = ContentValues().apply {
                put("user_id", userId)
                put("title", title)
                put("content", content)
                put("notification_type", notificationType)
                put("is_read", 0)
                put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            }
            val notificationId = db.insert("Notifications", null, values)
            if (notificationId == -1L) throw IllegalArgumentException("Failed to insert notification")

            db.setTransactionSuccessful()
            return notificationId
        } finally {
            db.endTransaction()
        }
    }

    fun updateUser(userId: Int, fullName: String, email: String, contactNumber: Int): Boolean {
        if (fullName.isBlank()) throw IllegalArgumentException("Full name cannot be empty")
        if (email.isBlank() || !email.contains("@")) throw IllegalArgumentException("Invalid email")
        if (contactNumber < 1000000000 || contactNumber > 9999999999) throw IllegalArgumentException("Contact number must be a 10-digit integer")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ? AND userID != ?",
                arrayOf(email, userId.toString())
            )
            val emailExists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (emailExists) throw IllegalArgumentException("Email is already in use by another user")

            val values = ContentValues().apply {
                put("fullName", fullName)
                put("email", email)
                put("contact_number", contactNumber)
            }
            val rowsAffected = db.update("Users", values, "userID = ?", arrayOf(userId.toString()))
            if (rowsAffected == 0) throw IllegalArgumentException("Failed to update user with ID $userId")

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun insertUser(user: User): Int {
        if (user.fullName.isBlank()) throw IllegalArgumentException("Full name cannot be empty")
        if (user.email.isBlank() || !user.email.contains("@")) throw IllegalArgumentException("Invalid email")
        if (user.userPassword.isBlank()) throw IllegalArgumentException("Password cannot be empty")
        if (user.userType !in listOf("Player", "Coach", "Admin")) throw IllegalArgumentException("Invalid user type")
        if (user.contactNumber != null && (user.contactNumber < 1000000000 || user.contactNumber > 9999999999)) {
            throw IllegalArgumentException("Contact number must be a 10-digit integer")
        }

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ?",
                arrayOf(user.email)
            )
            val emailExists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (emailExists) throw IllegalArgumentException("Email is already in use")

            val values = ContentValues().apply {
                put("fullName", user.fullName)
                put("email", user.email)
                put("userPassword", user.userPassword)
                put("created_at", user.createdAt ?: LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                put("last_login", user.lastLogin)
                put("user_type", user.userType)
                put("isVerified", if (user.isVerified) 1 else 0)
                if (user.contactNumber != null) put("contact_number", user.contactNumber)
            }
            val newRowId = db.insert("Users", null, values)
            if (newRowId == -1L) throw IllegalArgumentException("Failed to insert user")

            db.setTransactionSuccessful()
            return newRowId.toInt()
        } finally {
            db.endTransaction()
        }
    }

    fun insertTeamRegistration(
        teamName: String,
        coachName: String,
        contactEmail: String,
        status: String,
        coachUserId: Int? = null,
        yearsOfExistence: Int? = null,
        fieldAddress: String? = null,
        gamesPlayed: Int? = null,
        coachReference: String? = null,
        coachIdNo: String? = null,
        coachExperienceYears: Int? = null
    ): Long {
        if (teamName.isBlank()) throw IllegalArgumentException("Team name cannot be empty")
        if (coachName.isBlank()) throw IllegalArgumentException("Coach name cannot be empty")
        if (contactEmail.isBlank() || !contactEmail.contains("@")) throw IllegalArgumentException("Invalid contact email")
        if (status !in listOf("Pending", "Approved", "Rejected")) throw IllegalArgumentException("Invalid status")
        if (yearsOfExistence != null && yearsOfExistence < 0) throw IllegalArgumentException("Years of existence cannot be negative")
        if (gamesPlayed != null && gamesPlayed < 0) throw IllegalArgumentException("Games played cannot be negative")
        if (coachExperienceYears != null && coachExperienceYears < 0) throw IllegalArgumentException("Coach experience years cannot be negative")

        val db = writableDatabase
        val values = ContentValues().apply {
            put("team_name", teamName)
            put("coachName", coachName)
            put("contact_email", contactEmail)
            put("status", status)
            put("years_of_existence", yearsOfExistence)
            put("field_address", fieldAddress)
            put("games_played", gamesPlayed)
            put("coach_reference", coachReference)
            put("coach_id_no", coachIdNo)
            put("coach_experience_years", coachExperienceYears)
            if (coachUserId != null) {
                put("coach_userID", coachUserId)
            }
        }
        return db.insert("TeamRegistrations", null, values).also {
            if (it == -1L) throw IllegalArgumentException("Failed to insert team registration")
        }
    }

    fun getAllTeamRegistrations(): List<TeamRegistration> {
        val registrations = mutableListOf<TeamRegistration>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM TeamRegistrations", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    registrations.add(
                        TeamRegistration(
                            registrationID = cursor.getInt(cursor.getColumnIndexOrThrow("registrationID")),
                            teamName = cursor.getString(cursor.getColumnIndexOrThrow("team_name")),
                            coachName = cursor.getString(cursor.getColumnIndexOrThrow("coachName")),
                            contactEmail = cursor.getString(cursor.getColumnIndexOrThrow("contact_email")),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                            status = cursor.getString(cursor.getColumnIndexOrThrow("status")),
                            coachUserID = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_userID"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("coach_userID")),
                            yearsOfExistence = if (cursor.isNull(cursor.getColumnIndexOrThrow("years_of_existence"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("years_of_existence")),
                            fieldAddress = cursor.getString(cursor.getColumnIndexOrThrow("field_address")),
                            gamesPlayed = if (cursor.isNull(cursor.getColumnIndexOrThrow("games_played"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("games_played")),
                            coachReference = cursor.getString(cursor.getColumnIndexOrThrow("coach_reference")),
                            coachIdNo = cursor.getString(cursor.getColumnIndexOrThrow("coach_id_no")),
                            coachExperienceYears = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_experience_years"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("coach_experience_years"))
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return registrations
    }

    fun deleteTeamRegistration(registrationId: Int): Int {
        val db = writableDatabase
        return db.delete("TeamRegistrations", "registrationID = ?", arrayOf(registrationId.toString()))
    }

    fun getAllTeams(): List<Team> {
        val teams = mutableListOf<Team>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT t.team_id, t.team_name, t.created_at, t.coach_id, t.contact_email, 
                   t.years_of_existence, t.field_address, t.games_played, t.coach_reference, 
                   t.coach_id_no, t.coach_experience_years, u.fullName AS coachName
            FROM Teams t
            LEFT JOIN Users u ON t.coach_id = u.userID
            ORDER BY t.team_name
        """, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    teams.add(
                        Team(
                            teamId = cursor.getInt(cursor.getColumnIndexOrThrow("team_id")),
                            teamName = cursor.getString(cursor.getColumnIndexOrThrow("team_name")),
                            coachId = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_id"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("coach_id")),
                            contactEmail = cursor.getString(cursor.getColumnIndexOrThrow("contact_email")),
                            yearsOfExistence = if (cursor.isNull(cursor.getColumnIndexOrThrow("years_of_existence"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("years_of_existence")),
                            fieldAddress = cursor.getString(cursor.getColumnIndexOrThrow("field_address")),
                            gamesPlayed = if (cursor.isNull(cursor.getColumnIndexOrThrow("games_played"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("games_played")),
                            coachReference = cursor.getString(cursor.getColumnIndexOrThrow("coach_reference")),
                            coachIdNo = cursor.getString(cursor.getColumnIndexOrThrow("coach_id_no")),
                            coachExperienceYears = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_experience_years"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("coach_experience_years")),
                            coachName = cursor.getString(cursor.getColumnIndexOrThrow("coachName")),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))?.let {
                                LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                            }
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return teams
    }

    fun getTeamByCoachId(coachId: Int): Team? {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT t.team_id, t.team_name, t.created_at, t.coach_id, t.contact_email, " +
                    "t.years_of_existence, t.field_address, t.games_played, t.coach_reference, " +
                    "t.coach_id_no, t.coach_experience_years, u.fullName AS coachName " +
                    "FROM Teams t LEFT JOIN Users u ON t.coach_id = u.userID WHERE t.coach_id = ?",
            arrayOf(coachId.toString())
        )
        try {
            if (cursor.moveToFirst()) {
                return Team(
                    teamId = cursor.getInt(cursor.getColumnIndexOrThrow("team_id")),
                    teamName = cursor.getString(cursor.getColumnIndexOrThrow("team_name")),
                    coachId = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_id"))) null
                    else cursor.getInt(cursor.getColumnIndexOrThrow("coach_id")),
                    contactEmail = cursor.getString(cursor.getColumnIndexOrThrow("contact_email")),
                    yearsOfExistence = if (cursor.isNull(cursor.getColumnIndexOrThrow("years_of_existence"))) null
                    else cursor.getInt(cursor.getColumnIndexOrThrow("years_of_existence")),
                    fieldAddress = cursor.getString(cursor.getColumnIndexOrThrow("field_address")),
                    gamesPlayed = if (cursor.isNull(cursor.getColumnIndexOrThrow("games_played"))) null
                    else cursor.getInt(cursor.getColumnIndexOrThrow("games_played")),
                    coachReference = cursor.getString(cursor.getColumnIndexOrThrow("coach_reference")),
                    coachIdNo = cursor.getString(cursor.getColumnIndexOrThrow("coach_id_no")),
                    coachExperienceYears = if (cursor.isNull(cursor.getColumnIndexOrThrow("coach_experience_years"))) null
                    else cursor.getInt(cursor.getColumnIndexOrThrow("coach_experience_years")),
                    coachName = cursor.getString(cursor.getColumnIndexOrThrow("coachName")),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))?.let {
                        LocalDateTime.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    }
                )
            }
        } finally {
            cursor.close()
        }
        return null
    }

    fun insertTeam(
        teamName: String,
        contactEmail: String,
        yearsOfExistence: Int,
        fieldAddress: String,
        gamesPlayed: Int,
        coachReference: String,
        coachIdNo: String,
        coachExperienceYears: Int,
        coachFullName: String
    ): Long {
        if (teamName.isBlank()) throw IllegalArgumentException("Team name cannot be empty")
        if (contactEmail.isBlank() || !contactEmail.contains("@")) throw IllegalArgumentException("Invalid contact email")
        if (yearsOfExistence < 0) throw IllegalArgumentException("Years of existence cannot be negative")
        if (gamesPlayed < 0) throw IllegalArgumentException("Games played cannot be negative")
        if (coachExperienceYears < 0) throw IllegalArgumentException("Coach experience years cannot be negative")
        if (coachFullName.isBlank()) throw IllegalArgumentException("Coach name cannot be empty")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT userID FROM Users WHERE fullName = ? AND user_type = 'Coach'",
                arrayOf(coachFullName)
            )
            val coachId = if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("userID"))
            } else {
                val userValues = ContentValues().apply {
                    put("fullName", coachFullName)
                    put("email", "${coachFullName.replace(" ", "").lowercase()}@hockeyapp.com")
                    put("userPassword", "default123")
                    put("user_type", "Coach")
                    put("isVerified", 1)
                    put("contact_number", 8100000000) // Default 10-digit Int
                }
                val newCoachId = db.insert("Users", null, userValues)
                if (newCoachId == -1L) throw IllegalArgumentException("Failed to create coach user")
                newCoachId.toInt()
            }
            cursor.close()

            val teamValues = ContentValues().apply {
                put("team_name", teamName)
                put("coach_id", coachId)
                put("contact_email", contactEmail)
                put("years_of_existence", yearsOfExistence)
                put("field_address", fieldAddress)
                put("games_played", gamesPlayed)
                put("coach_reference", coachReference)
                put("coach_id_no", coachIdNo)
                put("coach_experience_years", coachExperienceYears)
                put("created_at", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            }
            val teamId = db.insert("Teams", null, teamValues)
            if (teamId == -1L) throw IllegalArgumentException("Failed to insert team")

            db.setTransactionSuccessful()
            return teamId
        } finally {
            db.endTransaction()
        }
    }

    fun updateTeam(team: Team): Boolean {
        if (team.teamName.isBlank()) throw IllegalArgumentException("Team name cannot be empty")
        if (team.contactEmail?.isBlank() != false || !team.contactEmail!!.contains("@")) throw IllegalArgumentException("Invalid contact email")
        if (team.yearsOfExistence != null && team.yearsOfExistence!! < 0) throw IllegalArgumentException("Years of existence cannot be negative")
        if (team.gamesPlayed != null && team.gamesPlayed!! < 0) throw IllegalArgumentException("Games played cannot be negative")
        if (team.coachExperienceYears != null && team.coachExperienceYears!! < 0) throw IllegalArgumentException("Coach experience years cannot be negative")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Teams WHERE team_name = ? AND team_id != ?",
                arrayOf(team.teamName, team.teamId.toString())
            )
            val exists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (exists) throw IllegalArgumentException("Team name already exists")

            val values = ContentValues().apply {
                put("team_name", team.teamName)
                put("contact_email", team.contactEmail)
                put("years_of_existence", team.yearsOfExistence)
                put("field_address", team.fieldAddress)
                put("games_played", team.gamesPlayed)
                put("coach_reference", team.coachReference)
                put("coach_id_no", team.coachIdNo)
                put("coach_experience_years", team.coachExperienceYears)
            }
            val rowsAffected = db.update("Teams", values, "team_id = ?", arrayOf(team.teamId.toString()))
            if (rowsAffected == 0) throw IllegalArgumentException("Failed to update team with ID ${team.teamId}")

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    fun deleteTeam(teamId: Int): Int {
        val db = writableDatabase
        return db.delete("Teams", "team_id = ?", arrayOf(teamId.toString()))
    }

    fun insertEvent(
        eventName: String,
        eventDescription: String?,
        eventDateTime: String,
        eventType: String,
        createdBy: Int? = null,
        createdAt: String
    ): Long {
        if (eventName.isBlank()) throw IllegalArgumentException("Event name cannot be empty")
        if (eventDateTime.isBlank()) throw IllegalArgumentException("Event date and time cannot be empty")
        if (eventType !in listOf("Match", "Tournament", "Training", "Meeting")) throw IllegalArgumentException("Invalid event type")

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
        return db.insert("Events", null, values).also {
            if (it == -1L) throw IllegalArgumentException("Failed to insert event")
        }
    }

    fun getAllEvents(): List<Event> {
        val events = mutableListOf<Event>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM Events", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    events.add(
                        Event(
                            eventId = cursor.getInt(cursor.getColumnIndexOrThrow("event_id")),
                            eventName = cursor.getString(cursor.getColumnIndexOrThrow("event_name")),
                            eventDescription = cursor.getString(cursor.getColumnIndexOrThrow("event_description")) ?: "No description",
                            eventDateTime = cursor.getString(cursor.getColumnIndexOrThrow("event_DateTime")),
                            eventType = cursor.getString(cursor.getColumnIndexOrThrow("event_type")),
                            createdBy = if (cursor.isNull(cursor.getColumnIndexOrThrow("created_by"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("created_by")),
                            createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at"))
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return events
    }

    fun deleteEvent(eventId: Int): Int {
        val db = writableDatabase
        return db.delete("Events", "event_id = ?", arrayOf(eventId.toString()))
    }

    fun updateEvent(event: Event): Boolean {
        if (event.eventName.isBlank()) throw IllegalArgumentException("Event name cannot be empty")
        if (event.eventDateTime.isBlank()) throw IllegalArgumentException("Event date and time cannot be empty")
        if (event.eventType !in listOf("Match", "Tournament", "Training", "Meeting")) throw IllegalArgumentException("Invalid event type")

        val db = writableDatabase
        val values = ContentValues().apply {
            put("event_name", event.eventName)
            put("event_description", event.eventDescription)
            put("event_DateTime", event.eventDateTime)
            put("event_type", event.eventType)
            put("created_by", event.createdBy)
        }
        val rowsAffected = db.update(
            "Events",
            values,
            "event_id = ?",
            arrayOf(event.eventId.toString())
        )
        return rowsAffected > 0
    }

    fun insertPlayer(
        userId: Int?,
        teamId: Int,
        fullName: String,
        jerseyNumber: Int,
        position: String,
        age: Int,
        height: Float,
        emergencyContact: String,
        dateOfBirth: String,
        joinDate: String
    ): Int {
        if (fullName.isBlank()) throw IllegalArgumentException("Player name cannot be empty")
        if (jerseyNumber <= 0) throw IllegalArgumentException("Jersey number must be positive")
        if (position.isBlank()) throw IllegalArgumentException("Position cannot be empty")
        if (age <= 0) throw IllegalArgumentException("Age must be positive")
        if (height <= 0) throw IllegalArgumentException("Height must be positive")
        if (emergencyContact.isBlank()) throw IllegalArgumentException("Emergency contact cannot be empty")
        if (dateOfBirth.isBlank()) throw IllegalArgumentException("Date of birth cannot be empty")
        if (joinDate.isBlank()) throw IllegalArgumentException("Join date cannot be empty")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val teamCursor = db.rawQuery(
                "SELECT COUNT(*) FROM Teams WHERE team_id = ?",
                arrayOf(teamId.toString())
            )
            val teamExists = teamCursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (!teamExists) throw IllegalArgumentException("Team with ID $teamId does not exist")

            if (userId != null) {
                val userCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Users WHERE userID = ?",
                    arrayOf(userId.toString())
                )
                val userExists = userCursor.use { c ->
                    c.moveToFirst() && c.getInt(0) > 0
                }
                if (!userExists) throw IllegalArgumentException("User with ID $userId does not exist")
            }

            if (userId != null) {
                val newEmail = "${fullName.replace(" ", "").lowercase()}@hockeyapp.com"
                val emailCursor = db.rawQuery(
                    "SELECT COUNT(*) FROM Users WHERE email = ? AND userID != ?",
                    arrayOf(newEmail, userId.toString())
                )
                val emailExists = emailCursor.use { c ->
                    c.moveToFirst() && c.getInt(0) > 0
                }
                if (emailExists) throw IllegalArgumentException("Player with this name already exists")

                val userValues = ContentValues().apply {
                    put("fullName", fullName)
                    put("email", newEmail)
                }
                val userRowsAffected = db.update("Users", userValues, "userID = ?", arrayOf(userId.toString()))
                if (userRowsAffected == 0) throw IllegalArgumentException("Failed to update user details for user ID $userId")
            } else {
                val newEmail = "${fullName.replace(" ", "").lowercase()}@hockeyapp.com"
                val userValues = ContentValues().apply {
                    put("fullName", fullName)
                    put("email", newEmail)
                    put("userPassword", "default123")
                    put("user_type", "Player")
                    put("isVerified", 1)
                    put("contact_number", 8100000000) // Default 10-digit Int
                }
                val newUserId = db.insert("Users", null, userValues)
                if (newUserId == -1L) throw IllegalArgumentException("Failed to create user")
            }

            val playerValues = ContentValues().apply {
                if (userId != null) put("user_id", userId)
                else {
                    val newUserId = db.rawQuery(
                        "SELECT userID FROM Users WHERE email = ?",
                        arrayOf("${fullName.replace(" ", "").lowercase()}@hockeyapp.com")
                    ).use { c ->
                        if (c.moveToFirst()) c.getInt(c.getColumnIndexOrThrow("userID"))
                        else throw IllegalArgumentException("Failed to retrieve newly created user ID")
                    }
                    put("user_id", newUserId)
                }
                put("team_id", teamId)
                put("jersey_number", jerseyNumber)
                put("position", position)
                put("age", age)
                put("height", height)
                put("emergency_contact", emergencyContact)
                put("date_of_birth", dateOfBirth)
                put("join_date", joinDate)
            }
            val playerId = db.insert("Players", null, playerValues)
            if (playerId == -1L) throw IllegalArgumentException("Failed to insert player")

            db.setTransactionSuccessful()
            return playerId.toInt()
        } finally {
            db.endTransaction()
        }
    }

    fun insertPlayerWithUser(
        fullName: String,
        teamId: Int,
        jerseyNumber: Int,
        position: String,
        age: Int,
        height: Float,
        emergencyContact: String,
        dateOfBirth: String
    ): Long {
        if (fullName.isBlank()) throw IllegalArgumentException("Player name cannot be empty")
        if (jerseyNumber <= 0) throw IllegalArgumentException("Jersey number must be positive")
        if (position.isBlank()) throw IllegalArgumentException("Position cannot be empty")
        if (age <= 0) throw IllegalArgumentException("Age must be positive")
        if (height <= 0) throw IllegalArgumentException("Height must be positive")
        if (emergencyContact.isBlank()) throw IllegalArgumentException("Emergency contact cannot be empty")
        if (dateOfBirth.isBlank()) throw IllegalArgumentException("Date of birth cannot be empty")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val email = "${fullName.replace(" ", "").lowercase()}@hockeyapp.com"
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ?",
                arrayOf(email)
            )
            val exists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (exists) throw IllegalArgumentException("Player with this name already exists")

            val userValues = ContentValues().apply {
                put("fullName", fullName)
                put("email", email)
                put("userPassword", "default123")
                put("user_type", "Player")
                put("isVerified", 1)
                put("contact_number", 8100000000) // Default 10-digit Int
            }
            val userId = db.insert("Users", null, userValues)
            if (userId == -1L) throw IllegalArgumentException("Failed to create user")

            val currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            val playerValues = ContentValues().apply {
                put("user_id", userId)
                put("team_id", teamId)
                put("jersey_number", jerseyNumber)
                put("position", position)
                put("age", age)
                put("height", height)
                put("emergency_contact", emergencyContact)
                put("date_of_birth", dateOfBirth)
                put("join_date", currentDate)
            }
            val playerId = db.insert("Players", null, playerValues)
            if (playerId == -1L) throw IllegalArgumentException("Failed to create player")

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
            SELECT p.player_id, p.user_id, p.team_id, u.fullName, p.jersey_number, p.position, 
                   p.age, p.height, p.emergency_contact, p.date_of_birth, p.join_date
            FROM Players p 
            LEFT JOIN Users u ON p.user_id = u.userID
        """, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    players.add(
                        Player(
                            playerId = cursor.getInt(cursor.getColumnIndexOrThrow("player_id")),
                            userId = if (cursor.isNull(cursor.getColumnIndexOrThrow("user_id"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            teamId = if (cursor.isNull(cursor.getColumnIndexOrThrow("team_id"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("team_id")),
                            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                            jerseyNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("jersey_number"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("jersey_number")),
                            position = cursor.getString(cursor.getColumnIndexOrThrow("position")),
                            age = if (cursor.isNull(cursor.getColumnIndexOrThrow("age"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("age")),
                            height = if (cursor.isNull(cursor.getColumnIndexOrThrow("height"))) null
                            else cursor.getFloat(cursor.getColumnIndexOrThrow("height")),
                            emergencyContact = cursor.getString(cursor.getColumnIndexOrThrow("emergency_contact")),
                            dateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                            joinDate = cursor.getString(cursor.getColumnIndexOrThrow("join_date"))
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return players
    }

    fun getPlayersByTeamId(teamId: Int): List<Player> {
        val players = mutableListOf<Player>()
        val db = readableDatabase
        val cursor = db.rawQuery("""
            SELECT p.player_id, p.user_id, p.team_id, u.fullName, p.jersey_number, p.position, 
                   p.age, p.height, p.emergency_contact, p.date_of_birth, p.join_date
            FROM Players p 
            LEFT JOIN Users u ON p.user_id = u.userID
            WHERE p.team_id = ?
        """, arrayOf(teamId.toString()))
        try {
            if (cursor.moveToFirst()) {
                do {
                    players.add(
                        Player(
                            playerId = cursor.getInt(cursor.getColumnIndexOrThrow("player_id")),
                            userId = if (cursor.isNull(cursor.getColumnIndexOrThrow("user_id"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("user_id")),
                            teamId = if (cursor.isNull(cursor.getColumnIndexOrThrow("team_id"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("team_id")),
                            fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                            jerseyNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("jersey_number"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("jersey_number")),
                            position = cursor.getString(cursor.getColumnIndexOrThrow("position")),
                            age = if (cursor.isNull(cursor.getColumnIndexOrThrow("age"))) null
                            else cursor.getInt(cursor.getColumnIndexOrThrow("age")),
                            height = if (cursor.isNull(cursor.getColumnIndexOrThrow("height"))) null
                            else cursor.getFloat(cursor.getColumnIndexOrThrow("height")),
                            emergencyContact = cursor.getString(cursor.getColumnIndexOrThrow("emergency_contact")),
                            dateOfBirth = cursor.getString(cursor.getColumnIndexOrThrow("date_of_birth")),
                            joinDate = cursor.getString(cursor.getColumnIndexOrThrow("join_date"))
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return players
    }

    fun updatePlayerName(userId: Int, newName: String): Boolean {
        if (newName.isBlank()) throw IllegalArgumentException("New name cannot be empty")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val newEmail = "${newName.replace(" ", "").lowercase()}@hockeyapp.com"
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ? AND userID != ?",
                arrayOf(newEmail, userId.toString())
            )
            val exists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (exists) throw IllegalArgumentException("Player with this name already exists")

            val values = ContentValues().apply {
                put("fullName", newName)
                put("email", newEmail)
            }
            val rowsAffected = db.update("Users", values, "userID = ?", arrayOf(userId.toString()))
            if (rowsAffected == 0) throw IllegalArgumentException("Failed to update player name for user ID $userId")

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

    fun updatePlayer(
        playerId: Int,
        fullName: String,
        jerseyNumber: Int,
        position: String,
        age: Int,
        height: Float,
        emergencyContact: String,
        dateOfBirth: String,
        joinDate: String
    ): Boolean {
        if (fullName.isBlank()) throw IllegalArgumentException("Player name cannot be empty")
        if (jerseyNumber <= 0) throw IllegalArgumentException("Jersey number must be positive")
        if (position.isBlank()) throw IllegalArgumentException("Position cannot be empty")
        if (age <= 0) throw IllegalArgumentException("Age must be positive")
        if (height <= 0) throw IllegalArgumentException("Height must be positive")
        if (emergencyContact.isBlank()) throw IllegalArgumentException("Emergency contact cannot be empty")
        if (dateOfBirth.isBlank()) throw IllegalArgumentException("Date of birth cannot be empty")
        if (joinDate.isBlank()) throw IllegalArgumentException("Join date cannot be empty")

        val db = writableDatabase
        db.beginTransaction()
        try {
            val cursor = db.rawQuery(
                "SELECT user_id FROM Players WHERE player_id = ?",
                arrayOf(playerId.toString())
            )
            if (!cursor.moveToFirst()) {
                cursor.close()
                throw IllegalArgumentException("Player not found with ID $playerId")
            }
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("user_id"))
            cursor.close()

            val newEmail = "${fullName.replace(" ", "").lowercase()}@hockeyapp.com"
            val emailCursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ? AND userID != ?",
                arrayOf(newEmail, userId.toString())
            )
            val emailExists = emailCursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (emailExists) throw IllegalArgumentException("Player with this name already exists")

            val userValues = ContentValues().apply {
                put("fullName", fullName)
                put("email", newEmail)
            }
            val userRowsAffected = db.update("Users", userValues, "userID = ?", arrayOf(userId.toString()))
            if (userRowsAffected == 0) throw IllegalArgumentException("Failed to update user details for user ID $userId")

            val playerValues = ContentValues().apply {
                put("jersey_number", jerseyNumber)
                put("position", position)
                put("age", age)
                put("height", height)
                put("emergency_contact", emergencyContact)
                put("date_of_birth", dateOfBirth)
                put("join_date", joinDate)
            }
            val playerRowsAffected = db.update("Players", playerValues, "player_id = ?", arrayOf(playerId.toString()))
            if (playerRowsAffected == 0) throw IllegalArgumentException("Failed to update player details for player ID $playerId")

            db.setTransactionSuccessful()
            return true
        } finally {
            db.endTransaction()
        }
    }

    override fun close() {
        super.close()
        writableDatabase.close()
    }
}