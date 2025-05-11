package com.example.hockeyapplive.data.datasource

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.User
import java.time.LocalDateTime

class DataSource(context: Context) {

    private val dbHelper: DatabaseHelper = DatabaseHelper(context)
    private var db: SQLiteDatabase? = null

    init {
        db = dbHelper.writableDatabase
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun authenticateUser(emailOrUsername: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE (email = ? OR fullName = ?) AND userPassword = ?",
            arrayOf(emailOrUsername, emailOrUsername, password)
        )

        return if (cursor.moveToFirst()) {
            val user = User(
                userID = cursor.getInt(cursor.getColumnIndexOrThrow("userID")),
                fullName = cursor.getString(cursor.getColumnIndexOrThrow("fullName")),
                email = cursor.getString(cursor.getColumnIndexOrThrow("email")),
                userPassword = cursor.getString(cursor.getColumnIndexOrThrow("userPassword")),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow("created_at")),
                lastLogin = cursor.getString(cursor.getColumnIndexOrThrow("last_login")),
                userType = cursor.getString(cursor.getColumnIndexOrThrow("user_type")),
                isVerified = cursor.getInt(cursor.getColumnIndexOrThrow("isVerified")) == 1
            )
            // Update last_login timestamp
            val values = ContentValues().apply {
                put("last_login", LocalDateTime.now().toString())
            }
            db.update("Users", values, "userID = ?", arrayOf(user.userID.toString()))
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun registerUser(fullName: String, email: String, password: String, userType: String, isVerified: Boolean = false): Result<Int> {
        return try {
            val db = dbHelper.writableDatabase
            val values = ContentValues().apply {
                put("fullName", fullName)
                put("email", email)
                put("userPassword", password)
                put("user_type", userType)
                put("isVerified", if (isVerified) 1 else 0)
                put("created_at", LocalDateTime.now().toString())
                putNull("last_login")
            }

            val result = db.insert("Users", null, values)
            if (result == -1L) {
                Result.failure(Exception("Failed to register user. Email may already exist."))
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
        return if (cursor.moveToFirst()) {
            val userId = cursor.getInt(cursor.getColumnIndexOrThrow("userID"))
            cursor.close()
            userId
        } else {
            cursor.close()
            null
        }
    }

    fun insertTeamRegistration(
        teamName: String,
        coachName: String,
        contactEmail: String,
        status: String,
        coachUserId: Int? = null
    ): Result<Unit> {
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
                Result.failure(Exception("Failed to insert team registration."))
            } else {
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(Exception("Team registration failed: ${e.message}"))
        }
    }



    fun close() {
        db?.close()
        dbHelper.close()
    }
}