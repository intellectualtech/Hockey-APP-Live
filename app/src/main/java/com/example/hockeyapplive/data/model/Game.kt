package com.example.hockeyapplive.data.model

data class Game(
    val gameId: Int,
    val team1Id: Int,
    val team2Id: Int,
    val team1Name: String?, // Changed to nullable
    val team2Name: String?, // Changed to nullable
    val team1Score: Int,
    val team2Score: Int,
    val gameDate: String,
    val location: String?,
    val status: String
)