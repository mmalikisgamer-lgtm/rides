package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.UserRole

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val messageId: String,
    val rideId: String,
    val senderId: String,
    val senderName: String,
    val senderRole: UserRole,
    val messageText: String,
    val isRead: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
