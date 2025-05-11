package com.example.hockeyapplive.data.model

data class Subscription(
    val subscriptionId: Int,
    val userId: Int,
    val teamId: Int,
    val subscribedAt: String
)