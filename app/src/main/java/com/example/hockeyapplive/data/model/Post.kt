package com.example.hockeyapplive.data.model

data class Post(
    val postId: Int,
    val teamId: Int,
    val content: String,
    val createdAt: String,
    val createdBy: Int?
)