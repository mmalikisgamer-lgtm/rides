package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ride_offers")
data class RideOfferEntity(
    @PrimaryKey
    val offerId: String,
    val rideId: String,
    val driverId: String,
    val driverName: String,
    val driverRating: Float,
    val driverCompletedTrips: Int,
    val vehicleModel: String,
    val vehiclePlate: String,
    val offeredFare: Double,
    val etaMinutes: Int,
    val distanceToPickupKm: Double,
    val isAccepted: Boolean = false,
    val isDeclined: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
