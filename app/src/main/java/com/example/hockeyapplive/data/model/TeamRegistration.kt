package com.example.hockeyapplive.data.model



data class TeamRegistration(
    val registrationID: Int,
    val teamName: String,
    val coachName: String,
    val contactEmail: String,
    val createdAt: String,
    val status: String,
    val coachUserID: Int?,
    val yearsOfExistence: Int? = null,
    val fieldAddress: String? = null,
    val gamesPlayed: Int? = null,
    val coachReference: String? = null,
    val coachIdNo: String? = null,
    val coachExperienceYears: Int? = null
)