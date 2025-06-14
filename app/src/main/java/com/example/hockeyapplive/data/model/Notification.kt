package com.example.hockeyapplive.data.model

data class Notification(
    val notificationId: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdAt: String,
    val isRead: Boolean,
    val notificationType: String
)