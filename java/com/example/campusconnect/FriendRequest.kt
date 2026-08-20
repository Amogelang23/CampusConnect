package com.example.campusconnect

data class FriendRequest(
    val fromUid: String? = null,
    val fromName: String? = null,
    val fromProfile: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)