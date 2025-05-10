package com.example.hockeyapplive.data

data class Team(
    val teamId: Int,
    val teamName: String,
    val createdAt: String,
    val coachId: Int?,
    val contactEmail: String?
)