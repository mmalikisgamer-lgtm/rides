package com.example.omniride.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.omniride.data.local.entity.DriverEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.data.model.RideStatus
import com.example.omniride.ui.components.GlassCard
import com.example.omniride.ui.components.OmniGradientButton
import com.example.omniride.ui.components.QuickStatCard
import com.example.omniride.ui.components.StatusPill
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavy900
import com.example.ui.theme.DarkNavy950
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun DriverDashboardScreen(
    driver: DriverEntity?,
    activeRide: RideRequestEntity?,
    availableRequests: List<RideRequestEntity>,
    onToggleOnline: (Boolean) -> Unit,
    onSubmitCounterOffer: (String, Double) -> Unit,
    onStartTrip: (String) -> Unit,
    onCompleteTrip: (String) -> Unit
) {
    var counterOfferDialogRideId by remember { mutableStateOf<String?>(null) }
    var counterPriceInput by remember { mutableDoubleStateOf(350.0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy950)
            .padding(16.dp)
    ) {
        // Driver Header & Online/Offline Switch
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = driver?.fullName ?: "Muhammad Usman",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.VerifiedUser, contentDescription = "KYC Approved", tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                    }
                    Text(
                        text = "${driver?.vehicleMake ?: "Toyota"} ${driver?.vehicleModel ?: "Corolla Altis"} • ${driver?.plateNumber ?: "ICT-AB-789"}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Online/Offline Pill Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (driver?.isOnline == true) "ONLINE" else "OFFLINE",
                        color = if (driver?.isOnline == true) EmeraldGreen else Color(0xFF64748B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = driver?.isOnline == true,
                        onCheckedChange = { onToggleOnline(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DarkNavy950,
                            checkedTrackColor = EmeraldGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF334155)
                        ),
                        modifier = Modifier.testTag("switch_driver_online")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 0% Platform Commission Earnings Banner in PKR
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickStatCard(
                title = "Today's Net",
                value = "Rs. ${String.format("%,.0f", driver?.todayEarnings ?: 7450.00)}",
                subtitle = "0% Platform Cut",
                icon = Icons.Default.AttachMoney,
                accentColor = EmeraldGreen,
                modifier = Modifier.weight(1f)
            )
            QuickStatCard(
                title = "Weekly Volume",
                value = "Rs. ${String.format("%,.0f", driver?.weekEarnings ?: 46200.00)}",
                subtitle = "${driver?.totalTrips ?: 412} trips completed",
                icon = Icons.Default.TrendingUp,
                accentColor = CyanAccent,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Active Ride Section (If driver currently has an active ride)
        if (activeRide != null) {
            Text(
                text = "Active Assigned Trip",
                color = EmeraldGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = EmeraldGreen.copy(alpha = 0.6f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatusPill(status = activeRide.status)
                        Text(
                            text = "Rs. ${String.format("%,.0f", if (activeRide.finalAgreedFare > 0) activeRide.finalAgreedFare else activeRide.customerOfferedFare)}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Passenger: ${activeRide.customerName} (${activeRide.customerRating} ★)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Pickup: ${activeRide.pickupAddress}",
                        color = CyanAccent,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Drop-off: ${activeRide.dropoffAddress}",
                        color = EmeraldGreen,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    if (activeRide.status == RideStatus.DRIVER_ASSIGNED || activeRide.status == RideStatus.DRIVER_ARRIVING || activeRide.status == RideStatus.DRIVER_ARRIVED) {
                        OmniGradientButton(
                            text = "Start Trip Navigation",
                            onClick = { onStartTrip(activeRide.id) },
                            icon = Icons.Default.Navigation,
                            testTag = "btn_driver_start_trip"
                        )
                    } else if (activeRide.status == RideStatus.IN_PROGRESS) {
                        OmniGradientButton(
                            text = "Arrived at Destination • Complete & Collect",
                            onClick = { onCompleteTrip(activeRide.id) },
                            icon = Icons.Default.CheckCircle,
                            testTag = "btn_driver_complete_trip"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Available Requests Radar Feed
        Text(
            text = "Nearby Requests Radar (${availableRequests.size})",
            color = TextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        if (driver?.isOnline != true) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.PowerSettingsNew, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("You are currently Offline", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Toggle Online switch above to start receiving ride & package requests.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else if (availableRequests.isEmpty()) {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Scanning radar for nearby requests...", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("You keep 100% of your earnings. No platform cut.", color = EmeraldGreen, fontSize = 12.sp)
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(availableRequests) { req ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (req.isDelivery) Icons.Default.LocalShipping else Icons.Default.DirectionsCar,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (req.isDelivery) "DELIVERY" else "PASSENGER RIDE",
                                        color = CyanAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Rs. ${String.format("%,.0f", req.customerOfferedFare)}",
                                    color = EmeraldGreen,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "From: ${req.pickupAddress}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "To: ${req.dropoffAddress}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${req.distanceKm} km • ~${req.estimatedDurationMin} min",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            if (req.isDelivery && req.packageDescription.isNotBlank()) {
                                Text(
                                    text = "Item: ${req.packageDescription} (${req.packageWeightKg} kg)",
                                    color = AmberAlert,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Accept Customer's Exact Offer
                                Button(
                                    onClick = { onSubmitCounterOffer(req.id, req.customerOfferedFare) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f).testTag("btn_accept_req_${req.id}")
                                ) {
                                    Text("Accept Rs. ${String.format("%,.0f", req.customerOfferedFare)}", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }

                                // Submit inDrive Counter Offer
                                OutlinedButton(
                                    onClick = {
                                        counterOfferDialogRideId = req.id
                                        counterPriceInput = req.customerOfferedFare + 50.0
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, CyanAccent),
                                    modifier = Modifier.weight(1f).testTag("btn_counter_req_${req.id}")
                                ) {
                                    Text("Counter-Offer", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }

                            // Counter offer inline panel
                            if (counterOfferDialogRideId == req.id) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    OutlinedTextField(
                                        value = counterPriceInput.toString(),
                                        onValueChange = { counterPriceInput = it.toDoubleOrNull() ?: counterPriceInput },
                                        label = { Text("Your Counter (Rs.)") },
                                        modifier = Modifier.weight(1f),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = CyanAccent,
                                            unfocusedBorderColor = DarkCardBorder
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Button(
                                        onClick = {
                                            onSubmitCounterOffer(req.id, counterPriceInput)
                                            counterOfferDialogRideId = null
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Send", color = DarkNavy950, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
