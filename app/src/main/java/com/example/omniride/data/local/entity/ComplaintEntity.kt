package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.UserRole

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey
    val complaintId: String,
    val rideId: String,
    val reporterId: String,
    val reporterName: String,
    val reporterRole: UserRole,
    val subject: String,
    val category: String, // FARE_DISPUTE, SAFETY, RUDE_BEHAVIOR, LOST_ITEM, VEHICLE_CONDITION
    val description: String,
    val status: String = "OPEN", // OPEN, INVESTIGATING, RESOLVED, DISMISSED
    val adminResolutionNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val resolvedAt: Long? = null
)
