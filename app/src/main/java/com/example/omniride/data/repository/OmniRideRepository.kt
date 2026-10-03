package com.example.omniride.data.repository

import com.example.omniride.data.local.OmniRideDatabase
import com.example.omniride.data.local.entity.AuditLogEntity
import com.example.omniride.data.local.entity.ChatMessageEntity
import com.example.omniride.data.local.entity.ComplaintEntity
import com.example.omniride.data.local.entity.DriverDocumentEntity
import com.example.omniride.data.local.entity.DriverEntity
import com.example.omniride.data.local.entity.PlatformSettingEntity
import com.example.omniride.data.local.entity.RideOfferEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.data.local.entity.UserEntity
import com.example.omniride.data.model.DocumentType
import com.example.omniride.data.model.KycStatus
import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.data.model.PaymentStatus
import com.example.omniride.data.model.RideStatus
import com.example.omniride.data.model.ServiceType
import com.example.omniride.data.model.UserRole
import com.example.omniride.service.LocationMapService
import com.example.omniride.service.PaymentService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import java.util.UUID

class OmniRideRepository(private val db: OmniRideDatabase) {

    private val userDao = db.userDao()
    private val driverDao = db.driverDao()
    private val driverDocumentDao = db.driverDocumentDao()
    private val rideDao = db.rideDao()
    private val offerDao = db.rideOfferDao()
    private val chatDao = db.chatDao()
    private val complaintDao = db.complaintDao()
    private val auditLogDao = db.auditLogDao()
    private val settingDao = db.platformSettingDao()

    val allUsers: Flow<List<UserEntity>> = userDao.getAllUsers()
    val allDrivers: Flow<List<DriverEntity>> = driverDao.getAllDrivers()
    val onlineDrivers: Flow<List<DriverEntity>> = driverDao.getOnlineDrivers()
    val allRides: Flow<List<RideRequestEntity>> = rideDao.getAllRides()
    val pendingKycDocs: Flow<List<DriverDocumentEntity>> = driverDocumentDao.getPendingDocuments()
    val allKycDocs: Flow<List<DriverDocumentEntity>> = driverDocumentDao.getAllDocuments()
    val allComplaints: Flow<List<ComplaintEntity>> = complaintDao.getAllComplaints()
    val allAuditLogs: Flow<List<AuditLogEntity>> = auditLogDao.getAllLogs()
    val platformSettings: Flow<List<PlatformSettingEntity>> = settingDao.getAllSettings()

    fun getUser(userId: String): Flow<UserEntity?> = userDao.getUserById(userId)
    fun getDriver(driverId: String): Flow<DriverEntity?> = driverDao.getDriverById(driverId)
    fun getActiveRideForCustomer(customerId: String): Flow<RideRequestEntity?> = rideDao.getActiveRideForCustomer(customerId)
    fun getActiveRideForDriver(driverId: String): Flow<RideRequestEntity?> = rideDao.getActiveRideForDriver(driverId)
    fun getRideById(rideId: String): Flow<RideRequestEntity?> = rideDao.getRideById(rideId)
    fun getOffersForRide(rideId: String): Flow<List<RideOfferEntity>> = offerDao.getOffersForRide(rideId)
    fun getMessagesForRide(rideId: String): Flow<List<ChatMessageEntity>> = chatDao.getMessagesForRide(rideId)
    fun getDriverDocs(driverId: String): Flow<List<DriverDocumentEntity>> = driverDocumentDao.getDocumentsForDriver(driverId)
    fun getAvailableRequestsForDrivers(): Flow<List<RideRequestEntity>> = rideDao.getAvailableRequestsForDrivers()

    suspend fun initializeSeedDataIfEmpty() {
        val existingUsers = allUsers.firstOrNull()
        if (!existingUsers.isNullOrEmpty()) return

        // 1. Prepopulate default roles and profiles
        val customer = UserEntity(
            id = "user_cust_01",
            name = "Hamza Tariq",
            email = "hamza.tariq@example.pk",
            phone = "+92 300 555 0122",
            role = UserRole.CUSTOMER,
            rating = 4.95f,
            walletBalance = 3500.00,
            emergencyContactPhone = "+92 301 555 0199"
        )
        val driverUser = UserEntity(
            id = "user_drv_01",
            name = "Muhammad Usman",
            email = "usman.driver@omniride.pk",
            phone = "+92 321 555 0188",
            role = UserRole.DRIVER,
            rating = 4.92f,
            walletBalance = 18500.00,
            emergencyContactPhone = "+92 301 555 0199"
        )
        val pendingDriverUser = UserEntity(
            id = "user_drv_02",
            name = "Zubair Ahmed",
            email = "zubair.ahmed@omniride.pk",
            phone = "+92 333 555 0194",
            role = UserRole.DRIVER,
            rating = 5.0f,
            walletBalance = 0.00,
            emergencyContactPhone = "+92 301 555 0199"
        )
        val adminUser = UserEntity(
            id = "user_admin_01",
            name = "Ayesha Khan",
            email = "ayesha.admin@omniride.pk",
            phone = "+92 345 555 0100",
            role = UserRole.ADMIN,
            rating = 5.0f,
            walletBalance = 0.00,
            emergencyContactPhone = "+92 301 555 0199"
        )
        userDao.insertUsers(listOf(customer, driverUser, pendingDriverUser, adminUser))

        // 2. Prepopulate drivers
        val driver1 = DriverEntity(
            driverId = "drv_marcus",
            userId = driverUser.id,
            fullName = "Muhammad Usman",
            phone = driverUser.phone,
            vehicleMake = "Toyota",
            vehicleModel = "Corolla Altis 1.6 (White)",
            vehicleYear = 2023,
            vehicleColor = "Super White",
            plateNumber = "ICT-AB-789",
            vehicleCategory = "RIDE AC / COMFORT",
            isOnline = true,
            currentLat = 33.7182,
            currentLng = 73.0605,
            kycStatus = KycStatus.APPROVED,
            rating = 4.92f,
            totalTrips = 412,
            todayEarnings = 7450.00,
            weekEarnings = 46200.00,
            monthEarnings = 185000.00,
            platformFeePaid = 0.00, // 0% platform commission!
            taxesAndRegulatoryFees = 372.50
        )
        val driver2 = DriverEntity(
            driverId = "drv_samantha",
            userId = pendingDriverUser.id,
            fullName = "Zubair Ahmed",
            phone = pendingDriverUser.phone,
            vehicleMake = "Honda",
            vehicleModel = "Civic Oriel (Grey)",
            vehicleYear = 2022,
            vehicleColor = "Modern Steel",
            plateNumber = "LEA-23-456",
            vehicleCategory = "RIDE AC",
            isOnline = false,
            currentLat = 33.7088,
            currentLng = 73.0504,
            kycStatus = KycStatus.PENDING,
            rating = 5.00f,
            totalTrips = 0,
            todayEarnings = 0.00,
            weekEarnings = 0.00,
            monthEarnings = 0.00,
            platformFeePaid = 0.00
        )
        driverDao.insertDrivers(listOf(driver1, driver2))

        // 3. Prepopulate Driver KYC Documents
        val doc1 = DriverDocumentEntity(
            id = "doc_01",
            driverId = driver2.driverId,
            driverName = driver2.fullName,
            docType = DocumentType.DRIVING_LICENSE,
            title = "Commercial Driver License (LTV)",
            documentNumberMasked = "DL-ICT-****-8821",
            documentUri = "https://images.unsplash.com/photo-1544717305-2782549b5136",
            status = KycStatus.PENDING,
            reviewNotes = "Submitted for annual license renewal review."
        )
        val doc2 = DriverDocumentEntity(
            id = "doc_02",
            driverId = driver2.driverId,
            driverName = driver2.fullName,
            docType = DocumentType.VEHICLE_REGISTRATION,
            title = "Excise Vehicle Smart Card Registration",
            documentNumberMasked = "REG-ICT-****-4821",
            documentUri = "https://images.unsplash.com/photo-1549399542-7e3f8b79c341",
            status = KycStatus.PENDING,
            reviewNotes = "Awaiting Excise & Taxation validation."
        )
        val doc3 = DriverDocumentEntity(
            id = "doc_03",
            driverId = driver1.driverId,
            driverName = driver1.fullName,
            docType = DocumentType.CNIC_IDENTITY,
            title = "NADRA National Identity Card (CNIC)",
            documentNumberMasked = "CNIC-37405-*******-1",
            documentUri = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d",
            status = KycStatus.APPROVED,
            reviewNotes = "NADRA biometric identity verified."
        )
        driverDocumentDao.insertDocuments(listOf(doc1, doc2, doc3))

        // 4. Prepopulate Platform Settings (0% Commission Model, safety rules)
        val settings = listOf(
            PlatformSettingEntity("COMMISSION_RATE", "0.0", "Platform commission deducted from driver earnings (%)", "PRICING"),
            PlatformSettingEntity("REGULATORY_TAX", "5.0", "Mandatory provincial transport sales tax (%)", "TAX"),
            PlatformSettingEntity("BASE_FARE_ECONOMY", "180.0", "Base starting fare in PKR (Rs.)", "PRICING"),
            PlatformSettingEntity("PER_KM_RATE", "48.0", "Per kilometer pricing rate in PKR (Rs.)", "PRICING"),
            PlatformSettingEntity("SAFETY_SOS_ENABLED", "true", "Instant 15 Police & rescue dispatch enabled", "SAFETY"),
            PlatformSettingEntity("DEFAULT_CURRENCY", "PKR", "Operational currency symbol/code (Pakistani Rupee)", "SYSTEM")
        )
        settingDao.upsertSettings(settings)

        // 5. Prepopulate sample audit log
        val auditLog = AuditLogEntity(
            id = UUID.randomUUID().toString(),
            actor = "System Init",
            action = "PLATFORM_BOOTSTRAP",
            targetType = "CONFIGURATION",
            targetId = "GLOBAL",
            details = "Initialized OmniRide 0% Platform Commission architecture in Pakistani Rupees (PKR)."
        )
        auditLogDao.insertLog(auditLog)
    }

    suspend fun createRideRequest(
        customer: UserEntity,
        serviceType: ServiceType,
        isDelivery: Boolean,
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoffAddress: String,
        dropoffLat: Double,
        dropoffLng: Double,
        offeredFare: Double,
        paymentMethod: PaymentMethod,
        packageDetails: String = "",
        packageWeightKg: Double = 0.0,
        recipientPhone: String = "",
        specialInstructions: String = ""
    ): String {
        val distance = LocationMapService.calculateDistanceKm(pickupLat, pickupLng, dropoffLat, dropoffLng)
        val duration = LocationMapService.estimateDurationMinutes(distance)
        val recommended = LocationMapService.calculateRecommendedFare(distance, isDelivery)

        val rideId = "ride_${UUID.randomUUID().toString().take(8)}"
        val request = RideRequestEntity(
            id = rideId,
            customerId = customer.id,
            customerName = customer.name,
            customerPhone = customer.phone,
            customerRating = customer.rating,
            serviceType = serviceType,
            isDelivery = isDelivery,
            pickupAddress = pickupAddress,
            pickupLat = pickupLat,
            pickupLng = pickupLng,
            dropoffAddress = dropoffAddress,
            dropoffLat = dropoffLat,
            dropoffLng = dropoffLng,
            distanceKm = distance,
            estimatedDurationMin = duration,
            recommendedFare = recommended,
            customerOfferedFare = offeredFare,
            status = RideStatus.REQUESTED,
            paymentMethod = paymentMethod,
            packageDescription = packageDetails,
            packageWeightKg = packageWeightKg,
            recipientPhone = recipientPhone,
            specialInstructions = specialInstructions
        )
        rideDao.insertRide(request)

        // Simulate incoming nearby drivers submitting competitive inDrive-style counter offers!
        CoroutineScope(Dispatchers.IO).launch {
            delay(2200)
            val driverMarcus = driverDao.getDriverById("drv_marcus").firstOrNull()
            if (driverMarcus != null) {
                val offer1 = RideOfferEntity(
                    offerId = "off_${UUID.randomUUID().toString().take(6)}",
                    rideId = rideId,
                    driverId = driverMarcus.driverId,
                    driverName = driverMarcus.fullName,
                    driverRating = driverMarcus.rating,
                    driverCompletedTrips = driverMarcus.totalTrips,
                    vehicleModel = "${driverMarcus.vehicleMake} ${driverMarcus.vehicleModel} (${driverMarcus.vehicleColor})",
                    vehiclePlate = driverMarcus.plateNumber,
                    offeredFare = offeredFare, // Driver accepts customer's exact offer!
                    etaMinutes = 4,
                    distanceToPickupKm = 1.2
                )
                val offer2 = RideOfferEntity(
                    offerId = "off_${UUID.randomUUID().toString().take(6)}",
                    rideId = rideId,
                    driverId = "drv_sim_tariq",
                    driverName = "Tariq Mehmood",
                    driverRating = 4.96f,
                    driverCompletedTrips = 789,
                    vehicleModel = "Honda City 1.5 (Silver)",
                    vehiclePlate = "ISB-902",
                    offeredFare = offeredFare + 50.0, // Counter-offer +Rs. 50
                    etaMinutes = 3,
                    distanceToPickupKm = 0.8
                )
                offerDao.insertOffers(listOf(offer1, offer2))
                rideDao.updateStatus(rideId, RideStatus.OFFERS_RECEIVED)
            }
        }

        return rideId
    }

    suspend fun acceptOffer(offer: RideOfferEntity, ride: RideRequestEntity) {
        offerDao.acceptOffer(offer.offerId)
        val updatedRide = ride.copy(
            status = RideStatus.DRIVER_ASSIGNED,
            driverId = offer.driverId,
            driverName = offer.driverName,
            driverPhone = "+92 321 555 0188",
            driverRating = offer.driverRating,
            vehicleModel = offer.vehicleModel,
            vehiclePlate = offer.vehiclePlate,
            finalAgreedFare = offer.offeredFare,
            driverCurrentLat = ride.pickupLat - 0.008,
            driverCurrentLng = ride.pickupLng - 0.006
        )
        rideDao.updateRide(updatedRide)

        // Seed welcome greeting chat message
        chatDao.insertMessage(
            ChatMessageEntity(
                messageId = UUID.randomUUID().toString(),
                rideId = ride.id,
                senderId = offer.driverId,
                senderName = offer.driverName,
                senderRole = UserRole.DRIVER,
                messageText = "Assalam-o-Alaikum! I have accepted your fare offer of Rs. ${String.format("%,.0f", offer.offeredFare)}. Navigating to ${ride.pickupAddress}, arriving in ~${offer.etaMinutes} minutes."
            )
        )

        // Trigger simulation loop of driver arriving
        startDriverJourneySimulation(updatedRide)
    }

    private fun startDriverJourneySimulation(initialRide: RideRequestEntity) {
        CoroutineScope(Dispatchers.IO).launch {
            val rideId = initialRide.id
            delay(1500)
            rideDao.updateStatus(rideId, RideStatus.DRIVER_ARRIVING)

            // Step 1: Smooth driver approach to pickup
            for (step in 1..4) {
                delay(2000)
                val current = rideDao.getRideById(rideId).firstOrNull() ?: break
                if (current.status == RideStatus.CANCELLED) break
                val (lat, lng) = LocationMapService.interpolateRoute(
                    initialRide.pickupLat - 0.008,
                    initialRide.pickupLng - 0.006,
                    initialRide.pickupLat,
                    initialRide.pickupLng,
                    step / 4f
                )
                rideDao.updateDriverTelemetry(rideId, lat, lng)
            }

            // Step 2: Driver arrived at pickup
            delay(1500)
            rideDao.updateStatus(rideId, RideStatus.DRIVER_ARRIVED)
            chatDao.insertMessage(
                ChatMessageEntity(
                    messageId = UUID.randomUUID().toString(),
                    rideId = rideId,
                    senderId = initialRide.driverId ?: "driver",
                    senderName = initialRide.driverName ?: "Driver",
                    senderRole = UserRole.DRIVER,
                    messageText = "I have arrived at your pickup spot! Flash hazard lights are on."
                )
            )
        }
    }

    suspend fun startRideTrip(rideId: String) {
        rideDao.updateStatus(rideId, RideStatus.IN_PROGRESS)
        CoroutineScope(Dispatchers.IO).launch {
            val ride = rideDao.getRideById(rideId).firstOrNull() ?: return@launch
            // Step 3: Advance toward dropoff destination
            for (step in 1..5) {
                delay(2500)
                val current = rideDao.getRideById(rideId).firstOrNull() ?: break
                if (current.status == RideStatus.CANCELLED) break
                val (lat, lng) = LocationMapService.interpolateRoute(
                    ride.pickupLat,
                    ride.pickupLng,
                    ride.dropoffLat,
                    ride.dropoffLng,
                    step / 5f
                )
                rideDao.updateDriverTelemetry(rideId, lat, lng)
            }
        }
    }

    suspend fun completeRideTrip(rideId: String) {
        val ride = rideDao.getRideById(rideId).firstOrNull() ?: return
        val fare = if (ride.finalAgreedFare > 0) ride.finalAgreedFare else ride.customerOfferedFare
        
        // 0% platform commission calculation
        val breakdown = PaymentService.computeFareBreakdown(fare, ride.paymentMethod)
        
        // Update ride status
        val completedRide = ride.copy(
            status = RideStatus.COMPLETED,
            paymentStatus = PaymentStatus.COMPLETED,
            driverCurrentLat = ride.dropoffLat,
            driverCurrentLng = ride.dropoffLng
        )
        rideDao.updateRide(completedRide)

        // Record driver 100% net earnings
        if (ride.driverId != null) {
            driverDao.recordTripEarnings(ride.driverId, breakdown.driverNetEarnings)
        }

        // Settle wallet if wallet was used
        if (ride.paymentMethod == PaymentMethod.WALLET) {
            userDao.updateWalletBalance(ride.customerId, -fare)
        }
    }

    suspend fun cancelRide(rideId: String, reason: String) {
        val ride = rideDao.getRideById(rideId).firstOrNull() ?: return
        rideDao.updateRide(ride.copy(status = RideStatus.CANCELLED, cancellationReason = reason))
    }

    suspend fun sendChatMessage(rideId: String, sender: UserEntity, text: String) {
        val msg = ChatMessageEntity(
            messageId = UUID.randomUUID().toString(),
            rideId = rideId,
            senderId = sender.id,
            senderName = sender.name,
            senderRole = sender.role,
            messageText = text
        )
        chatDao.insertMessage(msg)
    }

    suspend fun submitRating(rideId: String, rating: Float, comment: String) {
        rideDao.submitRating(rideId, rating, comment)
    }

    suspend fun triggerSos(rideId: String, user: UserEntity) {
        rideDao.triggerEmergency(rideId)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actor = user.name,
                action = "EMERGENCY_SOS_TRIGGERED",
                targetType = "RIDE",
                targetId = rideId,
                details = "Emergency SOS button triggered by ${user.role}. Dispatch alerted with live telemetry coordinates."
            )
        )
    }

    suspend fun setDriverOnline(driverId: String, isOnline: Boolean) {
        driverDao.setOnlineStatus(driverId, isOnline)
    }

    suspend fun submitDriverOffer(rideId: String, driver: DriverEntity, counterOfferFare: Double) {
        val offer = RideOfferEntity(
            offerId = "off_${UUID.randomUUID().toString().take(6)}",
            rideId = rideId,
            driverId = driver.driverId,
            driverName = driver.fullName,
            driverRating = driver.rating,
            driverCompletedTrips = driver.totalTrips,
            vehicleModel = "${driver.vehicleMake} ${driver.vehicleModel}",
            vehiclePlate = driver.plateNumber,
            offeredFare = counterOfferFare,
            etaMinutes = 3,
            distanceToPickupKm = 0.9
        )
        offerDao.insertOffer(offer)
        rideDao.updateStatus(rideId, RideStatus.OFFERS_RECEIVED)
    }

    suspend fun submitComplaint(
        rideId: String,
        user: UserEntity,
        subject: String,
        category: String,
        description: String
    ) {
        val complaint = ComplaintEntity(
            complaintId = "cmp_${UUID.randomUUID().toString().take(6)}",
            rideId = rideId,
            reporterId = user.id,
            reporterName = user.name,
            reporterRole = user.role,
            subject = subject,
            category = category,
            description = description
        )
        complaintDao.insertComplaint(complaint)
    }

    suspend fun adminReviewDocument(
        docId: String,
        driverId: String,
        status: KycStatus,
        notes: String,
        adminName: String
    ) {
        driverDocumentDao.updateDocumentStatus(docId, status, notes)
        if (status == KycStatus.APPROVED) {
            driverDao.updateKycStatus(driverId, KycStatus.APPROVED)
        }
        auditLogDao.insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actor = adminName,
                action = if (status == KycStatus.APPROVED) "KYC_DOC_APPROVED" else "KYC_DOC_REJECTED",
                targetType = "DRIVER_DOC",
                targetId = docId,
                details = "Driver $driverId document review outcome: $status. Notes: $notes"
            )
        )
    }

    suspend fun adminResolveComplaint(
        complaintId: String,
        status: String,
        notes: String,
        adminName: String
    ) {
        complaintDao.resolveComplaint(complaintId, status, notes)
        auditLogDao.insertLog(
            AuditLogEntity(
                id = UUID.randomUUID().toString(),
                actor = adminName,
                action = "COMPLAINT_RESOLVED",
                targetType = "COMPLAINT",
                targetId = complaintId,
                details = "Complaint marked as $status. Resolution: $notes"
            )
        )
    }

    suspend fun updatePlatformSetting(key: String, value: String, adminName: String) {
        val existing = settingDao.getSetting(key)
        if (existing != null) {
            settingDao.upsertSetting(existing.copy(settingValue = value, updatedAt = System.currentTimeMillis()))
            auditLogDao.insertLog(
                AuditLogEntity(
                    id = UUID.randomUUID().toString(),
                    actor = adminName,
                    action = "SETTING_UPDATED",
                    targetType = "PLATFORM_SETTING",
                    targetId = key,
                    details = "Changed setting '$key' to '$value'"
                )
            )
        }
    }
}
