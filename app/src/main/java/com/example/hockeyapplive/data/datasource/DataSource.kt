package com.example.hockeyapplive.data.datasource

import android.content.ContentValues
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.CoachTeamDetails
import com.example.hockeyapplive.data.model.Game
import com.example.hockeyapplive.data.model.User
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class DataSource(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)

    fun authenticateUser(email: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE email = ? AND userPassword = ?",
            arrayOf(email.trim(), password)
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
                    contactNumber = if (cursor.isNull(cursor.getColumnIndexOrThrow("contact_number"))) 0
                    else cursor.getInt(cursor.getColumnIndexOrThrow("contact_number"))
                )
            }
        } finally {
            cursor.close()
        }
        return null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateLastLogin(userId: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("last_login", OffsetDateTime.now().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
        }
        db.update("Users", values, "userID = ?", arrayOf(userId.toString()))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(fullName: String, email: String, password: String, userType: String, isVerified: Boolean = false, contactNumber: Long? = null): Result<Int> {
        if (fullName.isBlank()) return Result.failure(Exception("Full name cannot be empty"))
        if (!email.contains("@")) return Result.failure(Exception("Invalid email format"))
        if (password.isBlank()) return Result.failure(Exception("Password cannot be empty"))
        if (userType !in listOf("Player", "Coach", "Admin")) return Result.failure(Exception("Invalid user type"))

        return try {
            val db = dbHelper.writableDatabase
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Users WHERE email = ?",
                arrayOf(email)
            )
            val emailExists = cursor.use { c ->
                c.moveToFirst() && c.getInt(0) > 0
            }
            if (emailExists) {
                return Result.failure(Exception("Email already exists"))
            }

            val values = ContentValues().apply {
                put("fullName", fullName)
                put("email", email)
                put("userPassword", password)
                put("user_type", userType)
                put("isVerified", if (isVerified) 1 else 0)
                put("created_at", OffsetDateTime.now().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                putNull("last_login")
                if (contactNumber != null) {
                    put("contact_number", contactNumber)
                } else {
                    putNull("contact_number")
                }
            }

            val result = db.insert("Users", null, values)
            if (result == -1L) {
                Result.failure(Exception("Failed to register user"))
            } else {
                Result.success(result.toInt())
            }
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed: ${e.message}"))
        }
    }

    fun getUserIdByEmail(email: String): Int? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT userID FROM Users WHERE email = ?",
            arrayOf(email)
        )
        return try {
            if (cursor.moveToFirst()) {
                cursor.getInt(cursor.getColumnIndexOrThrow("userID"))
            } else {
                null
            }
        } finally {
            cursor.close()
        }
    }

    fun insertTeamRegistration(
        teamName: String,
        coachName: String,
        contactEmail: String,
        status: String,
        coachUserId: Int? = null
    ): Result<Unit> {
        if (teamName.isBlank()) return Result.failure(Exception("Team name cannot be empty"))
        if (coachName.isBlank()) return Result.failure(Exception("Coach name cannot be empty"))
        if (!contactEmail.contains("@")) return Result.failure(Exception("Invalid contact email"))
        if (status !in listOf("Pending", "Approved", "Rejected")) return Result.failure(Exception("Invalid status"))

        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("team_name", teamName)
                put("coachName", coachName)
                put("contact_email", contactEmail)
                put("status", status)
                if (coachUserId != null) {
                    put("coach_userID", coachUserId)
                }
            }
            val result = db.insert("TeamRegistrations", null, values)
            if (result == -1L) {
                Result.failure(Exception("Failed to insert team registration"))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Team registration failed: ${e.message}"))
        }
    }

    fun getGamesForTeam(teamId: Int): List<Game> {
        val db = dbHelper.readableDatabase
        val games = mutableListOf<Game>()
        val cursor = db.rawQuery(
            """
            SELECT g.game_id, g.team1_id, g.team2_id, t1.team_name AS team1_name, t2.team_name AS team2_name,
                   g.team1_score, g.team2_score, g.game_date, g.location, g.status
            FROM Games g
            JOIN Teams t1 ON g.team1_id = t1.team_id
            JOIN Teams t2 ON g.team2_id = t2.team_id
            WHERE g.team1_id = ? OR g.team2_id = ?
            """,
            arrayOf(teamId.toString(), teamId.toString())
        )

        try {
            if (cursor.moveToFirst()) {
                do {
                    games.add(
                        Game(
                            gameId = cursor.getInt(cursor.getColumnIndexOrThrow("game_id")),
                            team1Id = cursor.getInt(cursor.getColumnIndexOrThrow("team1_id")),
                            team2Id = cursor.getInt(cursor.getColumnIndexOrThrow("team2_id")),
                            team1Name = cursor.getString(cursor.getColumnIndexOrThrow("team1_name")) ?: "",
                            team2Name = cursor.getString(cursor.getColumnIndexOrThrow("team2_name")) ?: "",
                            team1Score = cursor.getInt(cursor.getColumnIndexOrThrow("team1_score")),
                            team2Score = cursor.getInt(cursor.getColumnIndexOrThrow("team2_score")),
                            gameDate = cursor.getString(cursor.getColumnIndexOrThrow("game_date")),
                            location = cursor.getString(cursor.getColumnIndexOrThrow("location")) ?: "",
                            status = cursor.getString(cursor.getColumnIndexOrThrow("status"))
                        )
                    )
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return games
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertTeamWithCoachDetails(details: CoachTeamDetails): Result<Int> {
        if (details.teamName.isBlank()) return Result.failure(Exception("Team name cannot be empty"))
        if (!details.email.contains("@")) return Result.failure(Exception("Invalid email"))
        if (details.fullName.isBlank()) return Result.failure(Exception("Coach name cannot be empty"))
        if (details.teamYearsOfExistence < 0) return Result.failure(Exception("Years of existence cannot be negative"))
        if (details.numberOfGamesPlayed < 0) return Result.failure(Exception("Games played cannot be negative"))
        if (details.yearsOfExperience < 0) return Result.failure(Exception("Years of experience cannot be negative"))

        return try {
            val db = dbHelper.writableDatabase
            db.beginTransaction()

            // Check if coach exists by email, or register new coach
            var coachId: Int? = getUserIdByEmail(details.email)
            if (coachId == null) {
                val userResult = registerUser(
                    fullName = details.fullName,
                    email = details.email,
                    password = "default123",
                    userType = "Coach",
                    isVerified = true,
                    contactNumber = 2648100000000
                )
                if (userResult.isSuccess) {
                    coachId = userResult.getOrThrow()
                } else {
                    throw Exception("Failed to register coach: ${userResult.exceptionOrNull()?.message}")
                }
            }

            // Check if team name already exists
            val cursor = db.rawQuery(
                "SELECT COUNT(*) FROM Teams WHERE team_name = ?",
                arrayOf(details.teamName)
            )
            val teamExists = try {
                cursor.moveToFirst() && cursor.getInt(0) > 0
            } finally {
                cursor.close()
            }

            if (teamExists) {
                throw Exception("Team name already exists")
            }

            // Insert team with all details
            val teamValues = ContentValues().apply {
                put("team_name", details.teamName)
                put("coach_id", coachId)
                put("contact_email", details.email)
                put("years_of_existence", details.teamYearsOfExistence)
                put("field_address", details.addressOfFieldOrCourt)
                put("games_played", details.numberOfGamesPlayed)
                put("coach_reference", details.referenceOfCoachGame)
                put("coach_id_no", details.idNo)
                put("coach_experience_years", details.yearsOfExperience)
                put("created_at", OffsetDateTime.now().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
            }
            val teamId = db.insert("Teams", null, teamValues)
            if (teamId == -1L) {
                throw Exception("Failed to insert team")
            }

            db.setTransactionSuccessful()
            Result.success(teamId.toInt())
        } catch (e: Exception) {
            Result.failure(Exception("Failed to add team: ${e.message}"))
        } finally {
            val db = dbHelper.writableDatabase
            db.endTransaction()
        }
    }

    fun close() {
        dbHelper.close()
    }
}