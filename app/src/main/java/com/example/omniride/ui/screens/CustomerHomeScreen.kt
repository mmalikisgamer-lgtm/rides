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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.ElectricCar
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.omniride.data.model.GeoLocation
import com.example.omniride.data.model.PaymentMethod
import com.example.omniride.data.model.ServiceType
import com.example.omniride.service.LocationMapService
import com.example.omniride.ui.components.GlassCard
import com.example.omniride.ui.components.InteractiveMapCanvas
import com.example.omniride.ui.components.OmniGradientButton
import com.example.omniride.ui.components.OriginalMapView
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    onRequestRide: (
        serviceType: ServiceType,
        pickup: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoff: String,
        dropoffLat: Double,
        dropoffLng: Double,
        offeredFare: Double,
        paymentMethod: PaymentMethod
    ) -> Unit,
    onRequestDelivery: (
        serviceType: ServiceType,
        pickup: String,
        pickupLat: Double,
        pickupLng: Double,
        dropoff: String,
        dropoffLat: Double,
        dropoffLng: Double,
        offeredFare: Double,
        paymentMethod: PaymentMethod,
        category: String,
        weightKg: Double,
        details: String,
        recipientPhone: String,
        instructions: String
    ) -> Unit,
    onOpenSafety: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Ride, 1 = Delivery
    val locations = LocationMapService.POPULAR_LOCATIONS

    var pickupLoc by remember { mutableStateOf(locations[0]) }
    var dropoffLoc by remember { mutableStateOf(locations[1]) }
    var pickupExpanded by remember { mutableStateOf(false) }
    var dropoffExpanded by remember { mutableStateOf(false) }

    var selectedVehicle by remember { mutableStateOf(ServiceType.RIDE_ECONOMY) }
    var selectedPayment by remember { mutableStateOf(PaymentMethod.CASH) }

    val distanceKm = remember(pickupLoc, dropoffLoc) {
        LocationMapService.calculateDistanceKm(pickupLoc.latitude, pickupLoc.longitude, dropoffLoc.latitude, dropoffLoc.longitude)
    }
    val estDurationMin = remember(distanceKm) {
        LocationMapService.estimateDurationMinutes(distanceKm)
    }
    val recommendedFare = remember(distanceKm, selectedTab, selectedVehicle) {
        LocationMapService.calculateRecommendedFare(
            distanceKm = distanceKm,
            isDelivery = (selectedTab == 1),
            serviceType = selectedVehicle
        )
    }

    var customOfferedFare by remember(recommendedFare) { mutableDoubleStateOf(recommendedFare) }

    // Delivery specifics
    var packageCategory by remember { mutableStateOf("Small Parcel") }
    var packageWeight by remember { mutableStateOf("2.5") }
    var packageDetails by remember { mutableStateOf("Laptop & documents folder") }
    var recipientPhone by remember { mutableStateOf("+92 300 5550199") }
    var specialInstructions by remember { mutableStateOf("Leave at reception desk") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy950)
    ) {
        // Upper Map Preview: Original Real OpenStreetMap
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.40f)
        ) {
            OriginalMapView(
                pickupLat = pickupLoc.latitude,
                pickupLng = pickupLoc.longitude,
                pickupTitle = pickupLoc.addressName,
                dropoffLat = dropoffLoc.latitude,
                dropoffLng = dropoffLoc.longitude,
                dropoffTitle = dropoffLoc.addressName,
                statusText = "$distanceKm km • ~$estDurationMin min",
                modifier = Modifier.fillMaxSize()
            )

            // Safety Center trigger pill
            Surface(
                onClick = onOpenSafety,
                shape = RoundedCornerShape(18.dp),
                color = Color(0xDD0B1325),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f)),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .testTag("btn_top_safety")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Safety SOS", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // Lower Booking Sheet (iOS 26 Frosted Glass Panel)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.60f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = DarkNavy900,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // Tab Switcher: Ride vs Delivery
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = DarkCardSurface,
                    contentColor = CyanAccent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = CyanAccent
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DirectionsCar, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Book a Ride", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Courier Delivery", fontWeight = FontWeight.Bold)
                            }
                        }
                    )
                }

                // Locations Dropdowns
                GlassCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Pickup Location Selector
                        ExposedDropdownMenuBox(
                            expanded = pickupExpanded,
                            onExpandedChange = { pickupExpanded = !pickupExpanded }
                        ) {
                            OutlinedTextField(
                                value = pickupLoc.addressName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Pickup Location", color = CyanAccent) },
                                leadingIcon = {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanAccent)
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = pickupExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = DarkCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("input_pickup_location")
                            )
                            ExposedDropdownMenu(
                                expanded = pickupExpanded,
                                onDismissRequest = { pickupExpanded = false },
                                modifier = Modifier.background(DarkCardSurface)
                            ) {
                                locations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc.addressName, color = TextPrimary) },
                                        onClick = {
                                            pickupLoc = loc
                                            pickupExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Dropoff Location Selector
                        ExposedDropdownMenuBox(
                            expanded = dropoffExpanded,
                            onExpandedChange = { dropoffExpanded = !dropoffExpanded }
                        ) {
                            OutlinedTextField(
                                value = dropoffLoc.addressName,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Destination", color = EmeraldGreen) },
                                leadingIcon = {
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = EmeraldGreen)
                                },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropoffExpanded) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = DarkCardBorder
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                                    .testTag("input_dropoff_location")
                            )
                            ExposedDropdownMenu(
                                expanded = dropoffExpanded,
                                onDismissRequest = { dropoffExpanded = false },
                                modifier = Modifier.background(DarkCardSurface)
                            ) {
                                locations.forEach { loc ->
                                    DropdownMenuItem(
                                        text = { Text(loc.addressName, color = TextPrimary) },
                                        onClick = {
                                            dropoffLoc = loc
                                            dropoffExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Vehicle Category Selection (Ride tab)
                if (selectedTab == 0) {
                    Text(
                        text = "Select Vehicle Category",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val categories = listOf(
                            Triple(ServiceType.RIDE_MOTO, "Bike / Moto", Icons.Default.TwoWheeler),
                            Triple(ServiceType.RIDE_ECONOMY, "Auto Rickshaw", Icons.Default.DirectionsCar),
                            Triple(ServiceType.RIDE_COMFORT, "Ride Mini", Icons.Default.DirectionsCar),
                            Triple(ServiceType.RIDE_EV, "Ride AC", Icons.Default.ElectricCar),
                            Triple(ServiceType.RIDE_XL, "Ride XL", Icons.Default.DirectionsCar)
                        )
                        items(categories) { (type, name, icon) ->
                            val isSelected = selectedVehicle == type
                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else DarkCardSurface,
                                border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else DarkCardBorder),
                                modifier = Modifier.clickable { selectedVehicle = type }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = null,
                                        tint = if (isSelected) EmeraldGreen else TextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = name,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // Delivery Package Configuration
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Delivery Specifications", color = EmeraldGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = packageCategory,
                                    onValueChange = { packageCategory = it },
                                    label = { Text("Item Type", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = EmeraldGreen,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                                OutlinedTextField(
                                    value = packageWeight,
                                    onValueChange = { packageWeight = it },
                                    label = { Text("Weight (kg)", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary,
                                        focusedBorderColor = EmeraldGreen,
                                        unfocusedBorderColor = DarkCardBorder
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = packageDetails,
                                onValueChange = { packageDetails = it },
                                label = { Text("Package Description", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = recipientPhone,
                                onValueChange = { recipientPhone = it },
                                label = { Text("Recipient Phone (+92)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = EmeraldGreen,
                                    unfocusedBorderColor = DarkCardBorder
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // inDrive Bidding Engine (Offer your price in PKR)
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = EmeraldGreen.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column {
                                Text("Offer Your Fare (PKR)", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("Suggested: Rs. ${String.format("%,.0f", recommendedFare)}", color = TextSecondary, fontSize = 12.sp)
                            }
                            // Fare increment / decrement stepper in Rs. 50 chunks
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { customOfferedFare = maxOf(100.0, customOfferedFare - 50.0) },
                                    modifier = Modifier.size(36.dp).background(DarkNavy900, CircleShape).testTag("btn_fare_minus")
                                ) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = EmeraldGreen)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Rs. ${String.format("%,.0f", customOfferedFare)}",
                                    color = EmeraldGreen,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                IconButton(
                                    onClick = { customOfferedFare += 50.0 },
                                    modifier = Modifier.size(36.dp).background(DarkNavy900, CircleShape).testTag("btn_fare_plus")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase", tint = EmeraldGreen)
                                }
                            }
                        }

                        // Quick inDrive Fare Bidding Chips
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                "Auto" to recommendedFare,
                                "+Rs. 50" to recommendedFare + 50.0,
                                "+Rs. 100" to recommendedFare + 100.0,
                                "+Rs. 150" to recommendedFare + 150.0
                            ).forEach { (label, value) ->
                                val isSelected = Math.abs(customOfferedFare - value) < 1.0
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else DarkNavy900,
                                    border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else DarkCardBorder),
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { customOfferedFare = value }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelected) Color.White else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 7.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Payment Method Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        PaymentMethod.CASH to "Cash on Trip",
                        PaymentMethod.WALLET to "JazzCash / Wallet",
                        PaymentMethod.CARD to "Debit / Raast"
                    ).forEach { (method, label) ->
                        val isSelected = selectedPayment == method
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EmeraldGreen.copy(alpha = 0.25f) else DarkCardSurface,
                            border = BorderStroke(1.dp, if (isSelected) EmeraldGreen else DarkCardBorder),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedPayment = method }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Request Action Button
                OmniGradientButton(
                    text = if (selectedTab == 0) "Find Drivers for Rs. ${String.format("%,.0f", customOfferedFare)}"
                           else "Request Courier for Rs. ${String.format("%,.0f", customOfferedFare)}",
                    onClick = {
                        if (selectedTab == 0) {
                            onRequestRide(
                                selectedVehicle,
                                pickupLoc.addressName,
                                pickupLoc.latitude,
                                pickupLoc.longitude,
                                dropoffLoc.addressName,
                                dropoffLoc.latitude,
                                dropoffLoc.longitude,
                                customOfferedFare,
                                selectedPayment
                            )
                        } else {
                            onRequestDelivery(
                                ServiceType.DELIVERY_PACKAGE,
                                pickupLoc.addressName,
                                pickupLoc.latitude,
                                pickupLoc.longitude,
                                dropoffLoc.addressName,
                                dropoffLoc.latitude,
                                dropoffLoc.longitude,
                                customOfferedFare,
                                selectedPayment,
                                packageCategory,
                                packageWeight.toDoubleOrNull() ?: 2.0,
                                packageDetails,
                                recipientPhone,
                                specialInstructions
                            )
                        }
                    },
                    icon = if (selectedTab == 0) Icons.Default.DirectionsCar else Icons.Default.LocalShipping,
                    testTag = "btn_submit_request"
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
