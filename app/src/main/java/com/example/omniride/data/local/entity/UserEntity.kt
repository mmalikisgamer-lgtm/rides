package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val role: UserRole,
    val rating: Float = 5.0f,
    val isVerified: Boolean = true,
    val walletBalance: Double = 120.0,
    val avatarUrl: String = "",
    val emergencyContactPhone: String = "+1 800 555 0199",
    val createdAt: Long = System.currentTimeMillis()
)
