package com.example.hockeyapplive.data.model

import java.time.LocalDateTime

data class CoachTeamDetails(
    val fullName: String,
    val idNo: String,
    val yearsOfExperience: Int,
    val email: String,
    val teamName: String,
    val teamYearsOfExistence: Int,
    val addressOfFieldOrCourt: String,
    val numberOfGamesPlayed: Int,
    val referenceOfCoachGame: String,
    val createdAt: LocalDateTime
)