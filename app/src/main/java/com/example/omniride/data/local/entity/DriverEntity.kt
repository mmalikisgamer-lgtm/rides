package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.KycStatus

@Entity(tableName = "drivers")
data class DriverEntity(
    @PrimaryKey
    val driverId: String,
    val userId: String,
    val fullName: String,
    val phone: String,
    val vehicleMake: String,
    val vehicleModel: String,
    val vehicleYear: Int,
    val vehicleColor: String,
    val plateNumber: String,
    val vehicleCategory: String, // ECONOMY, COMFORT, MOTO, etc.
    val isOnline: Boolean = false,
    val currentLat: Double = 37.7749,
    val currentLng: Double = -122.4194,
    val kycStatus: KycStatus = KycStatus.APPROVED,
    val rating: Float = 4.92f,
    val totalTrips: Int = 348,
    val acceptanceRate: Float = 94.5f,
    val todayEarnings: Double = 142.50,
    val weekEarnings: Double = 890.00,
    val monthEarnings: Double = 3420.00,
    val cashEarnings: Double = 980.00,
    val digitalEarnings: Double = 2440.00,
    val platformFeePaid: Double = 0.00, // 0% platform commission!
    val taxesAndRegulatoryFees: Double = 68.40
)
