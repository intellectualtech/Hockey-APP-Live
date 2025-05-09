package com.example.hockeyapp.data

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

class DataSource(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase
        get() = dbHelper.writableDatabase

    // Users
    fun insertUser(user: User): Long {
        val values = ContentValues().apply {
            put("fullName", user.fullName)
            put("email", user.email)
            put("userPassword", user.userPassword)
            put("created_at", user.createdAt)
            put("last_login", user.lastLogin)
            put("user_type", user.userType)
            put("isVerified", if (user.isVerified) 1 else 0)
        }
        return db.insert("Users", null, values)
    }

    fun getUserByEmail(email: String): User? {
        val cursor = db.rawQuery("SELECT * FROM Users WHERE email = ?", arrayOf(email))
        return cursor.use {
            if (it.moveToFirst()) {
                User(
                    userID = it.getInt(it.getColumnIndexOrThrow("userID")),
                    fullName = it.getString(it.getColumnIndexOrThrow("fullName")),
                    email = it.getString(it.getColumnIndexOrThrow("email")),
                    userPassword = it.getString(it.getColumnIndexOrThrow("userPassword")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                    lastLogin = it.getString(it.getColumnIndexOrThrow("last_login")),
                    userType = it.getString(it.getColumnIndexOrThrow("user_type")),
                    isVerified = it.getInt(it.getColumnIndexOrThrow("isVerified")) == 1
                )
            } else null
        }
    }

    fun updateUserPassword(userID: Int, newPassword: String, isVerified: Boolean): Int {
        val values = ContentValues().apply {
            put("userPassword", newPassword)
            put("isVerified", if (isVerified) 1 else 0)
        }
        return db.update("Users", values, "userID = ?", arrayOf(userID.toString()))
    }

    fun updateLastLogin(userID: Int, timestamp: String): Int {
        val values = ContentValues().apply {
            put("last_login", timestamp)
        }
        return db.update("Users", values, "userID = ?", arrayOf(userID.toString()))
    }

    // TeamRegistrations
    fun insertTeamRegistration(registration: TeamRegistration): Long {
        val values = ContentValues().apply {
            put("team_name", registration.teamName)
            put("coachName", registration.coachName)
            put("contact_email", registration.contactEmail)
            put("created_at", registration.createdAt)
            put("status", registration.status)
            put("coach_userID", registration.coachUserID)
        }
        return db.insert("TeamRegistrations", null, values)
    }

    fun getPendingRegistrations(): List<TeamRegistration> {
        val registrations = mutableListOf<TeamRegistration>()
        val cursor = db.rawQuery("SELECT * FROM TeamRegistrations WHERE status = 'Pending'", null)
        cursor.use {
            while (it.moveToNext()) {
                registrations.add(
                    TeamRegistration(
                        registrationID = it.getInt(it.getColumnIndexOrThrow("registrationID")),
                        teamName = it.getString(it.getColumnIndexOrThrow("team_name")),
                        coachName = it.getString(it.getColumnIndexOrThrow("coachName")),
                        contactEmail = it.getString(it.getColumnIndexOrThrow("contact_email")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                        status = it.getString(it.getColumnIndexOrThrow("status")),
                        coachUserID = it.getIntOrNull("coach_userID")
                    )
                )
            }
        }
        return registrations
    }

    fun updateRegistrationStatus(registrationID: Int, status: String, coachUserID: Int? = null): Int {
        val values = ContentValues().apply {
            put("status", status)
            put("coach_userID", coachUserID)
        }
        return db.update("TeamRegistrations", values, "registrationID = ?", arrayOf(registrationID.toString()))
    }

    // Teams
    fun insertTeam(team: Team): Long {
        val values = ContentValues().apply {
            put("team_name", team.teamName)
            put("created_at", team.createdAt)
            put("coach_id", team.coachId)
            put("contact_email", team.contactEmail)
        }
        return db.insert("Teams", null, values)
    }

    fun getTeamByCoach(coachId: Int): Team? {
        val cursor = db.rawQuery("SELECT * FROM Teams WHERE coach_id = ?", arrayOf(coachId.toString()))
        return cursor.use {
            if (it.moveToFirst()) {
                Team(
                    teamId = it.getInt(it.getColumnIndexOrThrow("team_id")),
                    teamName = it.getString(it.getColumnIndexOrThrow("team_name")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                    coachId = it.getIntOrNull("coach_id"),
                    contactEmail = it.getString(it.getColumnIndexOrThrow("contact_email"))
                )
            } else null
        }
    }

    // Players
    fun insertPlayer(player: Player): Long {
        val values = ContentValues().apply {
            put("user_id", player.userId)
            put("team_id", player.teamId)
            put("jersey_number", player.jerseyNumber)
            put("position", player.position)
            put("date_of_birth", player.dateOfBirth)
            put("join_date", player.joinDate)
        }
        return db.insert("Players", null, values)
    }

    fun getPlayersByTeam(teamId: Int): List<Player> {
        val players = mutableListOf<Player>()
        val cursor = db.rawQuery("SELECT * FROM Players WHERE team_id = ?", arrayOf(teamId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                players.add(
                    Player(
                        playerId = it.getInt(it.getColumnIndexOrThrow("player_id")),
                        userId = it.getIntOrNull("user_id"),
                        teamId = it.getIntOrNull("team_id"),
                        jerseyNumber = it.getIntOrNull("jersey_number"),
                        position = it.getString(it.getColumnIndexOrThrow("position")),
                        dateOfBirth = it.getString(it.getColumnIndexOrThrow("date_of_birth")),
                        joinDate = it.getString(it.getColumnIndexOrThrow("join_date"))
                    )
                )
            }
        }
        return players
    }

    // Posts
    fun insertPost(post: Post): Long {
        val values = ContentValues().apply {
            put("team_id", post.teamId)
            put("content", post.content)
            put("created_at", post.createdAt)
            put("created_by", post.createdBy)
        }
        return db.insert("Posts", null, values)
    }

    fun getPostsForFan(userId: Int): List<Post> {
        val posts = mutableListOf<Post>()
        val query = """
            SELECT p.* FROM Posts p
            JOIN Subscriptions s ON p.team_id = s.team_id
            WHERE s.user_id = ?
            ORDER BY p.created_at DESC
        """
        val cursor = db.rawQuery(query, arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                posts.add(
                    Post(
                        postId = it.getInt(it.getColumnIndexOrThrow("post_id")),
                        teamId = it.getInt(it.getColumnIndexOrThrow("team_id")),
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                        createdBy = it.getIntOrNull("created_by")
                    )
                )
            }
        }
        return posts
    }

    // Subscriptions
    fun subscribeUserToTeam(userId: Int, teamId: Int): Long {
        val values = ContentValues().apply {
            put("user_id", userId)
            put("team_id", teamId)
            put("subscribed_at", System.currentTimeMillis().toString())
        }
        return db.insert("Subscriptions", null, values)
    }

    fun unsubscribeUserFromTeam(userId: Int, teamId: Int): Int {
        return db.delete("Subscriptions", "user_id = ? AND team_id = ?", arrayOf(userId.toString(), teamId.toString()))
    }

    // Notifications
    fun insertNotification(notification: Notification): Long {
        val values = ContentValues().apply {
            put("user_id", notification.userId)
            put("title", notification.title)
            put("content", notification.content)
            put("created_at", notification.createdAt)
            put("is_read", if (notification.isRead) 1 else 0)
            put("notification_type", notification.notificationType)
        }
        return db.insert("Notifications", null, values)
    }

    fun getNotificationsForUser(userId: Int): List<Notification> {
        val notifications = mutableListOf<Notification>()
        val cursor = db.rawQuery("SELECT * FROM Notifications WHERE user_id = ? ORDER BY created_at DESC", arrayOf(userId.toString()))
        cursor.use {
            while (it.moveToNext()) {
                notifications.add(
                    Notification(
                        notificationId = it.getInt(it.getColumnIndexOrThrow("notification_id")),
                        userId = it.getInt(it.getColumnIndexOrThrow("user_id")),
                        title = it.getString(it.getColumnIndexOrThrow("title")),
                        content = it.getString(it.getColumnIndexOrThrow("content")),
                        createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                        isRead = it.getInt(it.getColumnIndexOrThrow("is_read")) == 1,
                        notificationType = it.getString(it.getColumnIndexOrThrow("notification_type"))
                    )
                )
            }
        }
        return notifications
    }

    // Utility
    fun getAdminUserId(): Int? {
        val cursor = db.rawQuery("SELECT userID FROM Users WHERE user_type = 'Admin' LIMIT 1", null)
        return cursor.use {
            if (it.moveToFirst()) it.getInt(it.getColumnIndexOrThrow("userID")) else null
        }
    }

    private fun Cursor.getIntOrNull(columnName: String): Int? {
        val index = getColumnIndex(columnName)
        return if (index != -1 && !isNull(index)) getInt(index) else null
    }

    fun close() {
        dbHelper.close()
    }
}