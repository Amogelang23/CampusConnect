package com.example.campusconnect

data class User(
    val uid: String = "",
    val email: String = "",
    val name: String = "",
    val bio: String = "",
    val profileImage: String? = null
)