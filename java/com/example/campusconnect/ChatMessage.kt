package com.example.campusconnect

data class ChatMessage(
    val senderId: String? = null,
    val senderName: String? = null,
    val receiverId: String? = null,
    val message: String? = null,
    val imageUrl: String? = null,
    val timestamp: Long = 0L
)