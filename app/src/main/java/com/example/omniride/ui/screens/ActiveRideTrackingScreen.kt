package com.example.omniride.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.omniride.data.local.entity.ChatMessageEntity
import com.example.omniride.data.local.entity.RideOfferEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.data.model.RideStatus
import com.example.omniride.service.PaymentService
import com.example.omniride.service.SafetyService
import com.example.omniride.ui.components.GlassCard
import com.example.omniride.ui.components.InteractiveMapCanvas
import com.example.omniride.ui.components.OmniGradientButton
import com.example.omniride.ui.components.OriginalMapView
import com.example.omniride.ui.components.StatusPill
import com.example.ui.theme.AmberAlert
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
fun ActiveRideTrackingScreen(
    ride: RideRequestEntity,
    offers: List<RideOfferEntity>,
    messages: List<ChatMessageEntity>,
    onAcceptOffer: (RideOfferEntity) -> Unit,
    onCancelRide: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onStartRide: (String) -> Unit,
    onCompleteRide: (String) -> Unit,
    onSubmitRating: (Float, String) -> Unit,
    onOpenSafety: () -> Unit
) {
    var chatExpanded by remember { mutableStateOf(false) }
    var chatInput by remember { mutableStateOf("") }
    var ratingStars by remember { mutableFloatStateOf(5.0f) }
    var reviewComment by remember { mutableStateOf("Very smooth trip and polite driver!") }
    var ratingSubmitted by remember { mutableStateOf(false) }

    val fare = if (ride.finalAgreedFare > 0) ride.finalAgreedFare else ride.customerOfferedFare
    val fareBreakdown = remember(fare, ride.paymentMethod) {
        PaymentService.computeFareBreakdown(fare, ride.paymentMethod)
    }

    val driverFraction = when (ride.status) {
        RideStatus.REQUESTED, RideStatus.OFFERS_RECEIVED -> 0.0f
        RideStatus.DRIVER_ASSIGNED, RideStatus.DRIVER_ARRIVING -> 0.15f
        RideStatus.DRIVER_ARRIVED -> 0.25f
        RideStatus.IN_PROGRESS -> 0.65f
        RideStatus.COMPLETED -> 1.0f
        else -> 0.0f
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy950)
    ) {
        // Map Canvas Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.42f)
        ) {
            OriginalMapView(
                pickupLat = ride.pickupLat,
                pickupLng = ride.pickupLng,
                pickupTitle = ride.pickupAddress,
                dropoffLat = ride.dropoffLat,
                dropoffLng = ride.dropoffLng,
                dropoffTitle = ride.dropoffAddress,
                driverFraction = driverFraction,
                driverVehicleModel = ride.vehicleModel,
                driverVehiclePlate = ride.vehiclePlate,
                statusText = "${ride.status.name} • ${ride.distanceKm} km",
                modifier = Modifier.fillMaxSize()
            )

            // Top Status Bar Overlay
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xDD0B1325),
                border = BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatusPill(status = ride.status)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Rs. ${String.format("%,.0f", fare)}",
                        color = EmeraldGreen,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Safety Center Quick Access Pill
            Surface(
                onClick = onOpenSafety,
                shape = CircleShape,
                color = Color(0xDD0B1325),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(42.dp)
                    .testTag("btn_tracking_safety")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Security, contentDescription = "Safety Center", tint = CyanAccent, modifier = Modifier.size(20.dp))
                }
            }
        }

        // Action Sheet
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.58f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = DarkNavy900,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // If Ride is searching or offers received
                if (ride.status == RideStatus.REQUESTED) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Broadcasting offer to nearby drivers...",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your offer: Rs. ${String.format("%,.0f", ride.customerOfferedFare)} • 0% Platform Commission guarantee",
                                color = CyanAccent,
                                fontSize = 12.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                            Button(
                                onClick = { onCancelRide("Cancelled by user while searching") },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.testTag("btn_cancel_request")
                            ) {
                                Text("Cancel Request", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Incoming Driver Counter-Offers (inDrive Core Feature)
                if (ride.status == RideStatus.OFFERS_RECEIVED && offers.isNotEmpty()) {
                    Text(
                        text = "Driver Counter-Offers (${offers.size})",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    offers.forEach { offer ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp),
                            borderColor = CyanAccent.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = CyanAccent.copy(alpha = 0.2f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = CyanAccent)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(offer.driverName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Verified, contentDescription = "Verified", tint = EmeraldGreen, modifier = Modifier.size(14.dp))
                                    }
                                    Text(
                                        text = "${offer.driverRating} ★ (${offer.driverCompletedTrips} trips) • ${offer.vehicleModel}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "ETA ~${offer.etaMinutes} min • ${offer.distanceToPickupKm} km away",
                                        color = EmeraldGreen,
                                        fontSize = 11.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Rs. ${String.format("%,.0f", offer.offeredFare)}",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Button(
                                        onClick = { onAcceptOffer(offer) },
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("btn_accept_offer_${offer.offerId}")
                                    ) {
                                        Text("Accept", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Active Driver Details Card (When Driver Assigned)
                if (ride.driverName != null) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EmeraldGreen.copy(alpha = 0.2f),
                                    modifier = Modifier.size(46.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = EmeraldGreen)
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(ride.driverName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = EmeraldGreen.copy(alpha = 0.2f)
                                        ) {
                                            Text(
                                                text = "VERIFIED",
                                                color = EmeraldGreen,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${ride.vehicleModel ?: "Model 3"} • Plate: ${ride.vehiclePlate ?: "7XYZ892"}",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "Phone: ${SafetyService.maskPhoneNumber(ride.driverPhone ?: "+14155550188")}",
                                        color = CyanAccent,
                                        fontSize = 11.sp
                                    )
                                }
                                // Chat Toggle Button
                                IconButton(
                                    onClick = { chatExpanded = !chatExpanded },
                                    modifier = Modifier.background(DarkNavy900, CircleShape).testTag("btn_toggle_chat")
                                ) {
                                    Icon(Icons.Default.Message, contentDescription = "Chat", tint = CyanAccent)
                                }
                            }

                            // Expandable In-App Chat
                            AnimatedVisibility(visible = chatExpanded) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                ) {
                                    Text("Secure In-App Chat", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = DarkNavy950,
                                        modifier = Modifier.fillMaxWidth().height(120.dp)
                                    ) {
                                        LazyColumn(modifier = Modifier.padding(8.dp)) {
                                            items(messages) { msg ->
                                                val isCustomer = msg.senderRole.name == "CUSTOMER"
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = if (isCustomer) Arrangement.End else Arrangement.Start
                                                ) {
                                                    Surface(
                                                        shape = RoundedCornerShape(10.dp),
                                                        color = if (isCustomer) CyanAccent.copy(alpha = 0.25f) else Color(0xFF1E293B),
                                                        modifier = Modifier.padding(vertical = 2.dp)
                                                    ) {
                                                        Text(
                                                            text = "${msg.senderName}: ${msg.messageText}",
                                                            color = Color.White,
                                                            fontSize = 11.sp,
                                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        OutlinedTextField(
                                            value = chatInput,
                                            onValueChange = { chatInput = it },
                                            placeholder = { Text("Message driver...", fontSize = 12.sp) },
                                            modifier = Modifier.weight(1f),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedTextColor = TextPrimary,
                                                unfocusedTextColor = TextPrimary,
                                                focusedBorderColor = CyanAccent,
                                                unfocusedBorderColor = DarkCardBorder
                                            )
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        IconButton(
                                            onClick = {
                                                if (chatInput.isNotBlank()) {
                                                    onSendMessage(chatInput)
                                                    chatInput = ""
                                                }
                                            },
                                            modifier = Modifier.background(CyanAccent, CircleShape).testTag("btn_send_chat")
                                        ) {
                                            Icon(Icons.Default.Send, contentDescription = "Send", tint = DarkNavy950)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Transparent 0% Platform Commission Callout
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Fare Transparency", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text("0% Platform Cut", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Agreed Fare:", color = TextSecondary, fontSize = 12.sp)
                                Text("Rs. ${String.format("%,.0f", fareBreakdown.grossFare)}", color = TextPrimary, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Platform Commission:", color = TextSecondary, fontSize = 12.sp)
                                Text("Rs. 0 (0%)", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Provincial Services Tax (5%):", color = TextSecondary, fontSize = 12.sp)
                                Text("Rs. ${String.format("%,.0f", fareBreakdown.regulatoryTax)}", color = TextSecondary, fontSize = 12.sp)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Driver Net Earnings:", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Rs. ${String.format("%,.0f", fareBreakdown.driverNetEarnings)}", color = EmeraldGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Trip Lifecycle Action Buttons (Simulator helpers)
                if (ride.status == RideStatus.DRIVER_ASSIGNED || ride.status == RideStatus.DRIVER_ARRIVING || ride.status == RideStatus.DRIVER_ARRIVED) {
                    OmniGradientButton(
                        text = "Simulate: Start Trip",
                        onClick = { onStartRide(ride.id) },
                        testTag = "btn_start_trip"
                    )
                } else if (ride.status == RideStatus.IN_PROGRESS) {
                    OmniGradientButton(
                        text = "Complete Trip & Pay Rs. ${String.format("%,.0f", fare)}",
                        onClick = { onCompleteRide(ride.id) },
                        testTag = "btn_complete_trip"
                    )
                }

                // Completed Rating Sheet
                if (ride.status == RideStatus.COMPLETED) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = EmeraldGreen
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Trip Completed Successfully!", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("Receipt: Rs. ${String.format("%,.0f", fare)} via ${ride.paymentMethod.name}", color = TextSecondary, fontSize = 12.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            if (!ratingSubmitted) {
                                Text("Rate your driver", color = CyanAccent, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                Row(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    (1..5).forEach { star ->
                                        IconButton(onClick = { ratingStars = star.toFloat() }) {
                                            Icon(
                                                imageVector = Icons.Default.Star,
                                                contentDescription = "$star stars",
                                                tint = if (star <= ratingStars) AmberAlert else Color(0xFF475569)
                                            )
                                        }
                                    }
                                }
                                OutlinedTextField(
                                    value = reviewComment,
                                    onValueChange = { reviewComment = it },
                                    label = { Text("Add comments / compliments") },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = CyanAccent,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        ratingSubmitted = true
                                        onSubmitRating(ratingStars, reviewComment)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("btn_submit_rating")
                                ) {
                                    Text("Submit Rating & Close", color = DarkNavy950, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Text("Thank you! Rating submitted.", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
