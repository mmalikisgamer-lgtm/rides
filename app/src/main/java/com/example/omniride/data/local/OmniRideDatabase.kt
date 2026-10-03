package com.example.omniride.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.omniride.data.local.dao.AuditLogDao
import com.example.omniride.data.local.dao.ChatDao
import com.example.omniride.data.local.dao.ComplaintDao
import com.example.omniride.data.local.dao.DriverDao
import com.example.omniride.data.local.dao.DriverDocumentDao
import com.example.omniride.data.local.dao.PlatformSettingDao
import com.example.omniride.data.local.dao.RideDao
import com.example.omniride.data.local.dao.RideOfferDao
import com.example.omniride.data.local.dao.UserDao
import com.example.omniride.data.local.entity.AuditLogEntity
import com.example.omniride.data.local.entity.ChatMessageEntity
import com.example.omniride.data.local.entity.ComplaintEntity
import com.example.omniride.data.local.entity.DriverDocumentEntity
import com.example.omniride.data.local.entity.DriverEntity
import com.example.omniride.data.local.entity.PlatformSettingEntity
import com.example.omniride.data.local.entity.RideOfferEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        DriverEntity::class,
        DriverDocumentEntity::class,
        RideRequestEntity::class,
        RideOfferEntity::class,
        ChatMessageEntity::class,
        ComplaintEntity::class,
        AuditLogEntity::class,
        PlatformSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class OmniRideDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun driverDao(): DriverDao
    abstract fun driverDocumentDao(): DriverDocumentDao
    abstract fun rideDao(): RideDao
    abstract fun rideOfferDao(): RideOfferDao
    abstract fun chatDao(): ChatDao
    abstract fun complaintDao(): ComplaintDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun platformSettingDao(): PlatformSettingDao

    companion object {
        @Volatile
        private var INSTANCE: OmniRideDatabase? = null

        fun getInstance(context: Context): OmniRideDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    OmniRideDatabase::class.java,
                    "omniride_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
