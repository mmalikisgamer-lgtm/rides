package com.example.omniride.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
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
import com.example.omniride.data.model.KycStatus
import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.data.model.RideStatus
import com.example.omniride.data.model.ServiceType
import com.example.omniride.data.model.UserRole
import com.example.omniride.data.repository.OmniRideRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class OmniRideViewModel(application: Application) : AndroidViewModel(application) {

    private val db = OmniRideDatabase.getInstance(application)
    val repository = OmniRideRepository(db)

    private val _currentRole = MutableStateFlow(UserRole.CUSTOMER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Customer Persona: "user_cust_01", Driver Persona: "user_drv_01", Admin Persona: "user_admin_01"
    private val _currentUserId = MutableStateFlow("user_cust_01")
    val currentUser: StateFlow<UserEntity?> = _currentUserId.flatMapLatest { id ->
        repository.getUser(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentDriver: StateFlow<DriverEntity?> = repository.getDriver("drv_marcus")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeCustomerRide: StateFlow<RideRequestEntity?> = _currentUserId.flatMapLatest { id ->
        repository.getActiveRideForCustomer(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val activeDriverRide: StateFlow<RideRequestEntity?> = repository.getActiveRideForDriver("drv_marcus")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val availableRequestsForDrivers: StateFlow<List<RideRequestEntity>> =
        repository.getAvailableRequestsForDrivers()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDrivers: StateFlow<List<DriverEntity>> = repository.allDrivers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingKycDocs: StateFlow<List<DriverDocumentEntity>> = repository.pendingKycDocs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allComplaints: StateFlow<List<ComplaintEntity>> = repository.allComplaints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAuditLogs: StateFlow<List<AuditLogEntity>> = repository.allAuditLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val platformSettings: StateFlow<List<PlatformSettingEntity>> = repository.platformSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Active Ride dependent flows
    val activeRideOffers: StateFlow<List<RideOfferEntity>> = activeCustomerRide.flatMapLatest { ride ->
        if (ride != null) repository.getOffersForRide(ride.id) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = activeCustomerRide.flatMapLatest { custRide ->
        val rideId = custRide?.id
        if (rideId != null) repository.getMessagesForRide(rideId) else flowOf(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // App Navigation sub-screens / dialogs
    private val _isLandingOpen = MutableStateFlow(false)
    val isLandingOpen: StateFlow<Boolean> = _isLandingOpen.asStateFlow()

    private val _isSafetyDialogOpen = MutableStateFlow(false)
    val isSafetyDialogOpen: StateFlow<Boolean> = _isSafetyDialogOpen.asStateFlow()

    private val _isComplaintDialogOpen = MutableStateFlow(false)
    val isComplaintDialogOpen: StateFlow<Boolean> = _isComplaintDialogOpen.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfEmpty()
        }
    }

    fun switchRole(newRole: UserRole) {
        _currentRole.value = newRole
        when (newRole) {
            UserRole.CUSTOMER -> _currentUserId.value = "user_cust_01"
            UserRole.DRIVER -> _currentUserId.value = "user_drv_01"
            UserRole.ADMIN -> _currentUserId.value = "user_admin_01"
        }
    }

    fun setLandingOpen(open: Boolean) {
        _isLandingOpen.value = open
    }

    fun setSafetyDialogOpen(open: Boolean) {
        _isSafetyDialogOpen.value = open
    }

    fun setComplaintDialogOpen(open: Boolean) {
        _isComplaintDialogOpen.value = open
    }

    fun requestRide(
        serviceType: ServiceType,
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoffAddress: String,
        dropoffLat: Double,
        dropoffLng: Double,
        offeredFare: Double,
        paymentMethod: PaymentMethod
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.createRideRequest(
                customer = user,
                serviceType = serviceType,
                isDelivery = false,
                pickupAddress = pickupAddress,
                pickupLat = pickupLat,
                pickupLng = pickupLng,
                dropoffAddress = dropoffAddress,
                dropoffLat = dropoffLat,
                dropoffLng = dropoffLng,
                offeredFare = offeredFare,
                paymentMethod = paymentMethod
            )
        }
    }

    fun requestDelivery(
        serviceType: ServiceType,
        pickupAddress: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoffAddress: String,
        dropoffLat: Double,
        dropoffLng: Double,
        offeredFare: Double,
        paymentMethod: PaymentMethod,
        packageCategory: String,
        packageWeightKg: Double,
        packageDetails: String,
        recipientPhone: String,
        specialInstructions: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.createRideRequest(
                customer = user,
                serviceType = serviceType,
                isDelivery = true,
                pickupAddress = pickupAddress,
                pickupLat = pickupLat,
                pickupLng = pickupLng,
                dropoffAddress = dropoffAddress,
                dropoffLat = dropoffLat,
                dropoffLng = dropoffLng,
                offeredFare = offeredFare,
                paymentMethod = paymentMethod,
                packageDetails = "$packageCategory: $packageDetails",
                packageWeightKg = packageWeightKg,
                recipientPhone = recipientPhone,
                specialInstructions = specialInstructions
            )
        }
    }

    fun acceptOffer(offer: RideOfferEntity) {
        viewModelScope.launch {
            val ride = activeCustomerRide.value ?: return@launch
            repository.acceptOffer(offer, ride)
        }
    }

    fun driverStartRide(rideId: String) {
        viewModelScope.launch {
            repository.startRideTrip(rideId)
        }
    }

    fun driverCompleteRide(rideId: String) {
        viewModelScope.launch {
            repository.completeRideTrip(rideId)
        }
    }

    fun cancelActiveRide(rideId: String, reason: String) {
        viewModelScope.launch {
            repository.cancelRide(rideId, reason)
        }
    }

    fun sendChatMessage(rideId: String, text: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.sendChatMessage(rideId, user, text)
        }
    }

    fun submitRating(rideId: String, rating: Float, comment: String) {
        viewModelScope.launch {
            repository.submitRating(rideId, rating, comment)
        }
    }

    fun triggerSos(rideId: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.triggerSos(rideId, user)
        }
    }

    fun toggleDriverOnline(isOnline: Boolean) {
        viewModelScope.launch {
            val driver = currentDriver.value ?: return@launch
            repository.setDriverOnline(driver.driverId, isOnline)
        }
    }

    fun driverSubmitCounterOffer(rideId: String, counterFare: Double) {
        viewModelScope.launch {
            val driver = currentDriver.value ?: return@launch
            repository.submitDriverOffer(rideId, driver, counterFare)
        }
    }

    fun submitComplaint(rideId: String, subject: String, category: String, desc: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            repository.submitComplaint(rideId, user, subject, category, desc)
        }
    }

    fun adminApproveKyc(docId: String, driverId: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.name ?: "Operations Admin"
            repository.adminReviewDocument(docId, driverId, KycStatus.APPROVED, "Verified and approved by admin", admin)
        }
    }

    fun adminRejectKyc(docId: String, driverId: String, notes: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.name ?: "Operations Admin"
            repository.adminReviewDocument(docId, driverId, KycStatus.REJECTED, notes, admin)
        }
    }

    fun adminResolveComplaint(complaintId: String, notes: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.name ?: "Operations Admin"
            repository.adminResolveComplaint(complaintId, "RESOLVED", notes, admin)
        }
    }

    fun updatePlatformSetting(key: String, value: String) {
        viewModelScope.launch {
            val admin = currentUser.value?.name ?: "Operations Admin"
            repository.updatePlatformSetting(key, value, admin)
        }
    }
}
