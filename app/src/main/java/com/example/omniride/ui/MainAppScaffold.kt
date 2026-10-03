package com.example.omniride.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.omniride.data.model.UserRole
import com.example.omniride.ui.components.RoleBadge
import com.example.omniride.ui.components.SafetyCenterDialog
import com.example.omniride.ui.screens.ActiveRideTrackingScreen
import com.example.omniride.ui.screens.AdminDashboardScreen
import com.example.omniride.ui.screens.CustomerHomeScreen
import com.example.omniride.ui.screens.DriverDashboardScreen
import com.example.omniride.ui.screens.LandingWebScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavy900
import com.example.ui.theme.DarkNavy950
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MainAppScaffold(
    viewModel: OmniRideViewModel
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val currentDriver by viewModel.currentDriver.collectAsState()
    val activeCustomerRide by viewModel.activeCustomerRide.collectAsState()
    val activeDriverRide by viewModel.activeDriverRide.collectAsState()
    val activeOffers by viewModel.activeRideOffers.collectAsState()
    val activeMessages by viewModel.activeChatMessages.collectAsState()
    val availableRequests by viewModel.availableRequestsForDrivers.collectAsState()
    val allDrivers by viewModel.allDrivers.collectAsState()
    val pendingDocs by viewModel.pendingKycDocs.collectAsState()
    val allComplaints by viewModel.allComplaints.collectAsState()
    val allAuditLogs by viewModel.allAuditLogs.collectAsState()
    val platformSettings by viewModel.platformSettings.collectAsState()

    val isLandingOpen by viewModel.isLandingOpen.collectAsState()
    val isSafetyOpen by viewModel.isSafetyDialogOpen.collectAsState()
    val isComplaintOpen by viewModel.isComplaintDialogOpen.collectAsState()

    var showRolePicker by remember { mutableStateOf(false) }

    // Complaint Dialog State
    var complaintSubject by remember { mutableStateOf("Fare Bidding Inquiry") }
    var complaintCategory by remember { mutableStateOf("FARE_DISPUTE") }
    var complaintDesc by remember { mutableStateOf("The driver requested an additional cash toll outside of the agreed inDrive bidding fare.") }

    // Back handler for sub-screens
    BackHandler(enabled = isLandingOpen || isSafetyOpen || isComplaintOpen) {
        if (isLandingOpen) viewModel.setLandingOpen(false)
        if (isSafetyOpen) viewModel.setSafetyDialogOpen(false)
        if (isComplaintOpen) viewModel.setComplaintDialogOpen(false)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkNavy950,
        topBar = {
            if (!isLandingOpen) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = DarkNavy900,
                    border = BorderStroke(1.dp, DarkCardBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Brand Title
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { viewModel.setLandingOpen(true) }
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = CyanAccent.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "OmniRide",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        // Role Switcher Pill & Web Portal Toggle
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Role Switcher Button
                            Surface(
                                onClick = { showRolePicker = !showRolePicker },
                                shape = RoundedCornerShape(12.dp),
                                color = DarkCardSurface,
                                border = BorderStroke(1.dp, DarkCardBorder),
                                modifier = Modifier.testTag("btn_switch_role")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RoleBadge(role = currentRole)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Role",
                                        tint = TextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Web Portal Button
                            IconButton(
                                onClick = { viewModel.setLandingOpen(true) },
                                modifier = Modifier.size(36.dp).testTag("btn_open_web_portal")
                            ) {
                                Icon(
                                    Icons.Default.Language,
                                    contentDescription = "Web Portal & Architecture",
                                    tint = CyanAccent
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Role-Based Screen Router
            Crossfade(targetState = isLandingOpen to currentRole, label = "screen_crossfade") { (landing, role) ->
                if (landing) {
                    LandingWebScreen(
                        onCloseLanding = { viewModel.setLandingOpen(false) },
                        onLaunchCustomerApp = {
                            viewModel.switchRole(UserRole.CUSTOMER)
                            viewModel.setLandingOpen(false)
                        },
                        onLaunchDriverApp = {
                            viewModel.switchRole(UserRole.DRIVER)
                            viewModel.setLandingOpen(false)
                        }
                    )
                } else {
                    when (role) {
                        UserRole.CUSTOMER -> {
                            if (activeCustomerRide != null) {
                                ActiveRideTrackingScreen(
                                    ride = activeCustomerRide!!,
                                    offers = activeOffers,
                                    messages = activeMessages,
                                    onAcceptOffer = { viewModel.acceptOffer(it) },
                                    onCancelRide = { viewModel.cancelActiveRide(activeCustomerRide!!.id, it) },
                                    onSendMessage = { viewModel.sendChatMessage(activeCustomerRide!!.id, it) },
                                    onStartRide = { viewModel.driverStartRide(it) },
                                    onCompleteRide = { viewModel.driverCompleteRide(it) },
                                    onSubmitRating = { rating, comment ->
                                        viewModel.submitRating(activeCustomerRide!!.id, rating, comment)
                                    },
                                    onOpenSafety = { viewModel.setSafetyDialogOpen(true) }
                                )
                            } else {
                                CustomerHomeScreen(
                                    onRequestRide = { type, pAddr, pLat, pLng, dAddr, dLat, dLng, fare, payMethod ->
                                        viewModel.requestRide(type, pAddr, pLat, pLng, dAddr, dLat, dLng, fare, payMethod)
                                    },
                                    onRequestDelivery = { type, pAddr, pLat, pLng, dAddr, dLat, dLng, fare, payMethod, cat, wt, det, rPhone, inst ->
                                        viewModel.requestDelivery(type, pAddr, pLat, pLng, dAddr, dLat, dLng, fare, payMethod, cat, wt, det, rPhone, inst)
                                    },
                                    onOpenSafety = { viewModel.setSafetyDialogOpen(true) }
                                )
                            }
                        }
                        UserRole.DRIVER -> {
                            DriverDashboardScreen(
                                driver = currentDriver,
                                activeRide = activeDriverRide,
                                availableRequests = availableRequests,
                                onToggleOnline = { viewModel.toggleDriverOnline(it) },
                                onSubmitCounterOffer = { rideId, fare ->
                                    viewModel.driverSubmitCounterOffer(rideId, fare)
                                },
                                onStartTrip = { viewModel.driverStartRide(it) },
                                onCompleteTrip = { viewModel.driverCompleteRide(it) }
                            )
                        }
                        UserRole.ADMIN -> {
                            AdminDashboardScreen(
                                drivers = allDrivers,
                                rides = availableRequests,
                                pendingDocs = pendingDocs,
                                complaints = allComplaints,
                                auditLogs = allAuditLogs,
                                settings = platformSettings,
                                onApproveDoc = { docId, drvId -> viewModel.adminApproveKyc(docId, drvId) },
                                onRejectDoc = { docId, drvId, notes -> viewModel.adminRejectKyc(docId, drvId, notes) },
                                onResolveComplaint = { cid, notes -> viewModel.adminResolveComplaint(cid, notes) },
                                onUpdateSetting = { k, v -> viewModel.updatePlatformSetting(k, v) }
                            )
                        }
                    }
                }
            }

            // Role Switching Selector Dialog
            if (showRolePicker) {
                Dialog(onDismissRequest = { showRolePicker = false }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkCardSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text("Switch User Platform Role", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Experience all three distinct role ecosystems:", color = TextSecondary, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(14.dp))

                            listOf(
                                Triple(UserRole.CUSTOMER, "Customer Platform", "Book rides with inDrive fare bidding & send package deliveries"),
                                Triple(UserRole.DRIVER, "Driver Partner Platform", "0% commission earnings, accept requests, submit counter-offers"),
                                Triple(UserRole.ADMIN, "Ops Admin Center", "KYC document review queue, live fleet radar, disputes & 0% settings")
                            ).forEach { (role, title, desc) ->
                                val isCurrent = currentRole == role
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isCurrent) CyanAccent.copy(alpha = 0.2f) else DarkNavy900,
                                    border = BorderStroke(1.dp, if (isCurrent) CyanAccent else DarkCardBorder),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                        .clickable {
                                            viewModel.switchRole(role)
                                            showRolePicker = false
                                        }
                                        .testTag("select_role_${role.name}")
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            RoleBadge(role = role)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(desc, color = TextSecondary, fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Safety Center Dialog
            if (isSafetyOpen) {
                SafetyCenterDialog(
                    rideId = activeCustomerRide?.id ?: "ride_default",
                    emergencyPhone = currentUser?.emergencyContactPhone ?: "+1 800 555 0199",
                    onDismiss = { viewModel.setSafetyDialogOpen(false) },
                    onTriggerSos = {
                        val rideId = activeCustomerRide?.id ?: "ride_manual"
                        viewModel.triggerSos(rideId)
                    },
                    onReportIssue = {
                        viewModel.setSafetyDialogOpen(false)
                        viewModel.setComplaintDialogOpen(true)
                    }
                )
            }

            // Dispute / Complaint Dialog
            if (isComplaintOpen) {
                Dialog(onDismissRequest = { viewModel.setComplaintDialogOpen(false) }) {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = DarkCardSurface,
                        border = BorderStroke(1.dp, DarkCardBorder),
                        modifier = Modifier.fillMaxWidth().padding(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Submit Safety or Fare Dispute", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                IconButton(onClick = { viewModel.setComplaintDialogOpen(false) }) {
                                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            OutlinedTextField(
                                value = complaintSubject,
                                onValueChange = { complaintSubject = it },
                                label = { Text("Subject") },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = complaintDesc,
                                onValueChange = { complaintDesc = it },
                                label = { Text("Describe the Incident") },
                                modifier = Modifier.fillMaxWidth(),
                                minLines = 3,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = {
                                    viewModel.submitComplaint(
                                        activeCustomerRide?.id ?: "ride_support",
                                        complaintSubject,
                                        complaintCategory,
                                        complaintDesc
                                    )
                                    viewModel.setComplaintDialogOpen(false)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth().testTag("btn_submit_complaint")
                            ) {
                                Text("Submit to Operations Team", color = DarkNavy950, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}
