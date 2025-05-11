package com.example.hockeyapplive.data

data class Player(
    val playerId: Int,
    val userId: Int?,
    val teamId: Int?,
    val jerseyNumber: Int?,
    val position: String?,
    val dateOfBirth: String?,
    val joinDate: String?
)