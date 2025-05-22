package com.example.hockeyapplive.data.model

data class Player(
    val playerId: Int,
    val userId: Int?,
    val teamId: Int?,
    val fullName: String?,
    val jerseyNumber: Int?,
    val position: String?,
    val age: Int?,
    val height: Float?,
    val emergencyContact: String?,
    val dateOfBirth: String?,
    val joinDate: String?
)