package com.example.hockeyapplive.data.model

data class User(
    val userID: Int,
    val fullName: String,
    val email: String,
    val userPassword: String,
    val createdAt: String,
    val lastLogin: String?,
    val userType: String,
    val contactNumber: Long,
    val isVerified: Boolean
)




