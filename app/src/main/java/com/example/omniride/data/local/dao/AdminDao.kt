package com.example.omniride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.omniride.data.local.entity.AuditLogEntity
import com.example.omniride.data.local.entity.ComplaintEntity
import com.example.omniride.data.local.entity.PlatformSettingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ComplaintDao {
    @Query("SELECT * FROM complaints ORDER BY createdAt DESC")
    fun getAllComplaints(): Flow<List<ComplaintEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComplaint(complaint: ComplaintEntity)

    @Update
    suspend fun updateComplaint(complaint: ComplaintEntity)

    @Query("UPDATE complaints SET status = :status, adminResolutionNotes = :notes, resolvedAt = :resolvedAt WHERE complaintId = :id")
    suspend fun resolveComplaint(id: String, status: String, notes: String, resolvedAt: Long = System.currentTimeMillis())
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface PlatformSettingDao {
    @Query("SELECT * FROM platform_settings")
    fun getAllSettings(): Flow<List<PlatformSettingEntity>>

    @Query("SELECT * FROM platform_settings WHERE settingKey = :key")
    suspend fun getSetting(key: String): PlatformSettingEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSetting(setting: PlatformSettingEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSettings(settings: List<PlatformSettingEntity>)
}
