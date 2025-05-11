package com.example.hockeyapplive.data.model

data class Team(
    val teamId: Int,
    val teamName: String,
    val createdAt: String,
    val coachId: Int?,
    val contactEmail: String?
)