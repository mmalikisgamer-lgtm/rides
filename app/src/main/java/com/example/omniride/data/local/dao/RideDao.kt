package com.example.omniride.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.omniride.data.local.entity.ChatMessageEntity
import com.example.omniride.data.local.entity.RideOfferEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.data.model.RideStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface RideDao {
    @Query("SELECT * FROM ride_requests WHERE id = :rideId")
    fun getRideById(rideId: String): Flow<RideRequestEntity?>

    @Query("SELECT * FROM ride_requests WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getCustomerRides(customerId: String): Flow<List<RideRequestEntity>>

    @Query("SELECT * FROM ride_requests WHERE customerId = :customerId AND status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveRideForCustomer(customerId: String): Flow<RideRequestEntity?>

    @Query("SELECT * FROM ride_requests WHERE driverId = :driverId AND status NOT IN ('COMPLETED', 'CANCELLED') ORDER BY createdAt DESC LIMIT 1")
    fun getActiveRideForDriver(driverId: String): Flow<RideRequestEntity?>

    @Query("SELECT * FROM ride_requests WHERE status IN ('REQUESTED', 'OFFERS_RECEIVED') ORDER BY createdAt DESC")
    fun getAvailableRequestsForDrivers(): Flow<List<RideRequestEntity>>

    @Query("SELECT * FROM ride_requests ORDER BY createdAt DESC")
    fun getAllRides(): Flow<List<RideRequestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRide(ride: RideRequestEntity)

    @Update
    suspend fun updateRide(ride: RideRequestEntity)

    @Query("UPDATE ride_requests SET status = :status WHERE id = :rideId")
    suspend fun updateStatus(rideId: String, status: RideStatus)

    @Query("UPDATE ride_requests SET driverCurrentLat = :lat, driverCurrentLng = :lng WHERE id = :rideId")
    suspend fun updateDriverTelemetry(rideId: String, lat: Double, lng: Double)

    @Query("UPDATE ride_requests SET driverRatingGiven = :rating, feedbackComment = :comment WHERE id = :rideId")
    suspend fun submitRating(rideId: String, rating: Float, comment: String)

    @Query("UPDATE ride_requests SET emergencyAlertTriggered = 1 WHERE id = :rideId")
    suspend fun triggerEmergency(rideId: String)
}

@Dao
interface RideOfferDao {
    @Query("SELECT * FROM ride_offers WHERE rideId = :rideId ORDER BY offeredFare ASC")
    fun getOffersForRide(rideId: String): Flow<List<RideOfferEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffer(offer: RideOfferEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOffers(offers: List<RideOfferEntity>)

    @Query("UPDATE ride_offers SET isAccepted = 1 WHERE offerId = :offerId")
    suspend fun acceptOffer(offerId: String)

    @Query("UPDATE ride_offers SET isDeclined = 1 WHERE offerId = :offerId")
    suspend fun declineOffer(offerId: String)
}

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat_messages WHERE rideId = :rideId ORDER BY timestamp ASC")
    fun getMessagesForRide(rideId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("UPDATE chat_messages SET isRead = 1 WHERE rideId = :rideId AND senderId != :currentUserId")
    suspend fun markMessagesAsRead(rideId: String, currentUserId: String)
}
