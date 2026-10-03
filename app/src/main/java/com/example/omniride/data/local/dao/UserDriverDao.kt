package com.example.omniride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.omniride.data.local.entity.DriverDocumentEntity
import com.example.omniride.data.local.entity.DriverEntity
import com.example.omniride.data.local.entity.UserEntity
import com.example.omniride.data.model.KycStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserById(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM users")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET walletBalance = walletBalance + :amount WHERE id = :userId")
    suspend fun updateWalletBalance(userId: String, amount: Double)
}

@Dao
interface DriverDao {
    @Query("SELECT * FROM drivers")
    fun getAllDrivers(): Flow<List<DriverEntity>>

    @Query("SELECT * FROM drivers WHERE driverId = :driverId")
    fun getDriverById(driverId: String): Flow<DriverEntity?>

    @Query("SELECT * FROM drivers WHERE isOnline = 1")
    fun getOnlineDrivers(): Flow<List<DriverEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDriver(driver: DriverEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDrivers(drivers: List<DriverEntity>)

    @Update
    suspend fun updateDriver(driver: DriverEntity)

    @Query("UPDATE drivers SET isOnline = :isOnline WHERE driverId = :driverId")
    suspend fun setOnlineStatus(driverId: String, isOnline: Boolean)

    @Query("UPDATE drivers SET currentLat = :lat, currentLng = :lng WHERE driverId = :driverId")
    suspend fun updateLocation(driverId: String, lat: Double, lng: Double)

    @Query("UPDATE drivers SET kycStatus = :status WHERE driverId = :driverId")
    suspend fun updateKycStatus(driverId: String, status: KycStatus)

    @Query("UPDATE drivers SET todayEarnings = todayEarnings + :fare, totalTrips = totalTrips + 1 WHERE driverId = :driverId")
    suspend fun recordTripEarnings(driverId: String, fare: Double)
}

@Dao
interface DriverDocumentDao {
    @Query("SELECT * FROM driver_documents WHERE driverId = :driverId")
    fun getDocumentsForDriver(driverId: String): Flow<List<DriverDocumentEntity>>

    @Query("SELECT * FROM driver_documents ORDER BY uploadedAt DESC")
    fun getAllDocuments(): Flow<List<DriverDocumentEntity>>

    @Query("SELECT * FROM driver_documents WHERE status = 'PENDING'")
    fun getPendingDocuments(): Flow<List<DriverDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(doc: DriverDocumentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(docs: List<DriverDocumentEntity>)

    @Query("UPDATE driver_documents SET status = :status, reviewNotes = :notes, reviewedAt = :reviewedAt WHERE id = :docId")
    suspend fun updateDocumentStatus(docId: String, status: KycStatus, notes: String, reviewedAt: Long = System.currentTimeMillis())
}
