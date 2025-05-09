package com.example.hockeyapp.data

data class Event(
    val eventId: Int,
    val eventName: String,
    val eventDescription: String?,
    val eventDateTime: String,
    val eventType: String,
    val createdBy: Int?,
    val createdAt: String
)