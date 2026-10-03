package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey
    val id: String,
    val actor: String,
    val action: String,
    val targetType: String,
    val targetId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "platform_settings")
data class PlatformSettingEntity(
    @PrimaryKey
    val settingKey: String,
    val settingValue: String,
    val description: String,
    val category: String,
    val updatedAt: Long = System.currentTimeMillis()
)
