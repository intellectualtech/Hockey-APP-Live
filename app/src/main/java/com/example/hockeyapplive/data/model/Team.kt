package com.example.hockeyapplive.data.model

import java.time.LocalDateTime

data class Team(
    val teamId: Int,
    val teamName: String,
    val coachId: Int?,
    val contactEmail: String?,
    val yearsOfExistence: Int?,
    val fieldAddress: String?,
    val gamesPlayed: Int?,
    val coachReference: String?,
    val coachIdNo: String?,
    val coachExperienceYears: Int?,
    val coachName: String?,
    val createdAt: LocalDateTime?,
)