package com.example.hockeyapp.data

data class Post(
    val postId: Int,
    val teamId: Int,
    val content: String,
    val createdAt: String,
    val createdBy: Int?
)