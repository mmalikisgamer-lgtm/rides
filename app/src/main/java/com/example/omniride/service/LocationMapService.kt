package com.example.omniride.service

import com.example.omniride.data.model.GeoLocation
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object LocationMapService {

    // Authentic Pakistani Urban Hubs (Islamabad, Rawalpindi, Lahore, Karachi)
    val POPULAR_LOCATIONS = listOf(
        GeoLocation(33.7182, 73.0605, "Blue Area, Islamabad"),
        GeoLocation(33.7088, 73.0504, "Centaurus Mall, Jinnah Ave"),
        GeoLocation(33.7297, 73.0746, "F-6 Super Market, Islamabad"),
        GeoLocation(33.6989, 73.0360, "F-8 Markaz, Islamabad"),
        GeoLocation(33.5973, 73.0479, "Saddar Cantt, Rawalpindi"),
        GeoLocation(31.5204, 74.3587, "Gulberg Main Boulevard, Lahore"),
        GeoLocation(31.4704, 74.4108, "DHA Phase 5 Commercial, Lahore"),
        GeoLocation(24.8138, 67.0300, "Clifton Sea View, Karachi"),
        GeoLocation(31.5216, 74.4036, "Allama Iqbal Int'l Airport, Lahore")
    )

    /**
     * Compute Haversine distance in kilometers between two geo coordinates.
     */
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = r * c
        return (Math.round(distance * 100.0) / 100.0)
    }

    /**
     * Estimate trip duration in minutes given distance and traffic speed (~25 km/h avg).
     */
    fun estimateDurationMinutes(distanceKm: Double): Int {
        val avgSpeedKmH = 22.0
        val hours = distanceKm / avgSpeedKmH
        val minutes = (hours * 60).toInt() + 4 // 4 min traffic buffer
        return maxOf(6, minutes)
    }

    /**
     * Calculate fair recommended fare in Pakistani Rupees (PKR / Rs.) based on vehicle category
     */
    fun calculateRecommendedFare(
        distanceKm: Double,
        isDelivery: Boolean = false,
        serviceType: com.example.omniride.data.model.ServiceType? = null
    ): Double {
        val (baseFare, perKm, minFare) = when {
            isDelivery -> Triple(130.0, 36.0, 150.0)
            serviceType == com.example.omniride.data.model.ServiceType.RIDE_MOTO -> Triple(90.0, 24.0, 120.0)
            serviceType == com.example.omniride.data.model.ServiceType.RIDE_ECONOMY -> Triple(120.0, 32.0, 150.0) // Auto Rickshaw
            serviceType == com.example.omniride.data.model.ServiceType.RIDE_COMFORT -> Triple(160.0, 42.0, 200.0) // Ride Mini (Alto)
            serviceType == com.example.omniride.data.model.ServiceType.RIDE_EV -> Triple(200.0, 48.0, 260.0) // Ride AC (Yaris/City)
            serviceType == com.example.omniride.data.model.ServiceType.RIDE_XL -> Triple(280.0, 62.0, 350.0) // Ride XL (Civic/Corolla)
            else -> Triple(160.0, 44.0, 200.0)
        }
        val estMin = estimateDurationMinutes(distanceKm)
        val rawFare = baseFare + (distanceKm * perKm) + (estMin * 4.0)
        // Round to neat 10s for Pakistani Rupee cash convenience
        val rounded = (Math.round(rawFare / 10.0) * 10).toDouble()
        return maxOf(minFare, rounded)
    }

    /**
     * Interpolates points between start and end for smooth route simulation.
     */
    fun interpolateRoute(
        startLat: Double,
        startLng: Double,
        endLat: Double,
        endLng: Double,
        fraction: Float
    ): Pair<Double, Double> {
        val clamped = fraction.coerceIn(0f, 1f)
        val lat = startLat + (endLat - startLat) * clamped
        val lng = startLng + (endLng - startLng) * clamped
        return Pair(lat, lng)
    }
}
