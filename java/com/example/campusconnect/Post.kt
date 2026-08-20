package com.example.campusconnect

data class Post(
    val postId: String? = null,
    val userId: String? = null,
    val userName: String? = null,
    val userProfile: String? = null,
    val text: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = 0L,
    val likes: Int = 0,
    val comments: Map<String, String>? = null
)

data class Comment(
    val userId: String? = null,
    val userName: String? = null,
    val text: String? = null,
    val timestamp: Long = 0L
)