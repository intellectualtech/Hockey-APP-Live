package com.example.hockeyapp.data

data class Subscription(
    val subscriptionId: Int,
    val userId: Int,
    val teamId: Int,
    val subscribedAt: String
)