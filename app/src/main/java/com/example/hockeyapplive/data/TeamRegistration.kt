package com.example.hockeyapp.data

data class TeamRegistration(
    val registrationID: Int,
    val teamName: String,
    val coachName: String,
    val contactEmail: String,
    val createdAt: String,
    val status: String,
    val coachUserID: Int?
)