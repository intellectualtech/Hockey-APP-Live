package com.example.hockeyapplive.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase

class DataSource(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private val db: SQLiteDatabase
        get() = dbHelper.writableDatabase

    fun authenticateUser(usernameOrEmail: String, password: String): User? {
        val db = dbHelper.readableDatabase // Fix: Use dbHelper.readableDatabase instead of readableDatabase
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

}