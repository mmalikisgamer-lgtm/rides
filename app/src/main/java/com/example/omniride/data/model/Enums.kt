package com.example.omniride.data.model

enum class UserRole {
    CUSTOMER,
    DRIVER,
    ADMIN
}

enum class ServiceType {
    RIDE_ECONOMY,
    RIDE_COMFORT,
    RIDE_XL,
    RIDE_MOTO,
    RIDE_EV,
    DELIVERY_FOOD,
    DELIVERY_PACKAGE,
    DELIVERY_DOCUMENT
}

enum class RideStatus {
    REQUESTED,
    OFFERS_RECEIVED,
    DRIVER_ASSIGNED,
    DRIVER_ARRIVING,
    DRIVER_ARRIVED,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED,
    DISPUTED
}

enum class KycStatus {
    PENDING,
    APPROVED,
    REJECTED
}

enum class PaymentMethod {
    CASH,
    WALLET,
    CARD,
    DIGITAL_TRANSFER
}

enum class PaymentStatus {
    PENDING,
    COMPLETED,
    REFUNDED,
    FAILED
}

enum class DocumentType {
    CNIC_IDENTITY,
    DRIVING_LICENSE,
    VEHICLE_REGISTRATION,
    VEHICLE_PHOTO,
    POLICE_CLEARANCE
}

data class GeoLocation(
    val latitude: Double,
    val longitude: Double,
    val addressName: String
)
