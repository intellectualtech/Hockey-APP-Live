package com.example.hockeyapplive.data.datasource

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.net.Uri
import com.example.hockeyapplive.data.db.DatabaseHelper
import com.example.hockeyapplive.data.model.TeamRegistration
import com.example.hockeyapplive.data.model.User

class DataSource(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    val db: SQLiteDatabase
        get() = dbHelper.writableDatabase

    fun authenticateUser(usernameOrEmail: String, password: String): User? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM Users WHERE (email = ? OR fullName = ?) AND userPassword = ?",
            arrayOf(usernameOrEmail, usernameOrEmail, password)
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
            cursor.close()
            user
        } else {
            cursor.close()
            null
        }
    }

    fun insertUser(fullName: String, email: String, password: String, userType: String, isVerified: Boolean) {
        val values = ContentValues().apply {
            put("fullName", fullName)
            put("email", email)
            put("userPassword", password)
            put("user_type", userType)
            put("isVerified", if (isVerified) 1 else 0)
        }
        db.insert("Users", null, values)
    }
    object TeamRegistrations {
        val CONTENT_URI: Uri = Uri.parse("content://com.example.hockeyapplive.provider/team_registrations")
    }

    fun registerUser(fullName: String, email: String, password: String, userType: String): Result<Unit> {
        return try {
            insertUser(fullName, email, password, userType, isVerified = false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getPendingRegistrations(): List<TeamRegistration> {
        val pendingRegistrations = mutableListOf<TeamRegistration>()
        val cursor = db.query(
            "TeamRegistrations",
            null,
            "status = ?",
            arrayOf("Pending"),
            null,
            null,
            null
        )
        cursor.use {
            while (it.moveToNext()) {
                val registration = TeamRegistration(
                    registrationID = it.getInt(it.getColumnIndexOrThrow("registration_id")),
                    teamName = it.getString(it.getColumnIndexOrThrow("team_name")),
                    coachName = it.getString(it.getColumnIndexOrThrow("coachName")),
                    contactEmail = it.getString(it.getColumnIndexOrThrow("contact_email")),
                    createdAt = it.getString(it.getColumnIndexOrThrow("created_at")),
                    status = it.getString(it.getColumnIndexOrThrow("status")),
                    coachUserID = it.getInt(it.getColumnIndexOrThrow("coach_user_id"))
                )
                pendingRegistrations.add(registration)
            }
        }
        return pendingRegistrations
    }

    fun updateRegistrationStatus(registrationID: String, status: String) {
        val values = ContentValues().apply {
            put("status", status)
        }
        db.update(
            "TeamRegistrations",
            values,
            "registration_id = ?",
            arrayOf(registrationID)
        )
    }

    fun close() {
        dbHelper.close()
    }


}