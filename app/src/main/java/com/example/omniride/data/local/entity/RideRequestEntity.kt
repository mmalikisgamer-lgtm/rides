package com.example.omniride.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.data.model.PaymentStatus
import com.example.omniride.data.model.RideStatus
import com.example.omniride.data.model.ServiceType

@Entity(tableName = "ride_requests")
data class RideRequestEntity(
    @PrimaryKey
    val id: String,
    val customerId: String,
    val customerName: String,
    val customerPhone: String,
    val customerRating: Float = 4.95f,
    val serviceType: ServiceType = ServiceType.RIDE_ECONOMY,
    val isDelivery: Boolean = false,
    
    // Routing & Locations
    val pickupAddress: String,
    val pickupLat: Double,
    val pickupLng: Double,
    val dropoffAddress: String,
    val dropoffLat: Double,
    val dropoffLng: Double,
    val distanceKm: Double,
    val estimatedDurationMin: Int,
    
    // inDrive style Bidding & Fare negotiation
    val recommendedFare: Double,
    val customerOfferedFare: Double,
    val finalAgreedFare: Double = 0.0,
    
    // Status & Assignment
    val status: RideStatus = RideStatus.REQUESTED,
    val driverId: String? = null,
    val driverName: String? = null,
    val driverPhone: String? = null,
    val driverRating: Float? = null,
    val vehicleModel: String? = null,
    val vehiclePlate: String? = null,
    val driverCurrentLat: Double? = null,
    val driverCurrentLng: Double? = null,
    
    // Delivery specifics (when isDelivery == true)
    val packageDescription: String = "",
    val packageWeightKg: Double = 0.0,
    val packageCategory: String = "", // Food, Documents, Electronics, Gifts
    val recipientName: String = "",
    val recipientPhone: String = "",
    val specialInstructions: String = "",
    
    // Payments & Safety
    val paymentMethod: PaymentMethod = PaymentMethod.CASH,
    val paymentStatus: PaymentStatus = PaymentStatus.PENDING,
    val cancellationReason: String? = null,
    val driverRatingGiven: Float? = null,
    val feedbackComment: String? = null,
    val emergencyAlertTriggered: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
