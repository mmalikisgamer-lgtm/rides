package com.example.omniride.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.omniride.ui.components.GlassCard
import com.example.omniride.ui.components.OmniGradientButton
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
fun LandingWebScreen(
    onCloseLanding: () -> Unit,
    onLaunchCustomerApp: () -> Unit,
    onLaunchDriverApp: () -> Unit
) {
    var expandedFaqIndex by remember { mutableIntStateOf(-1) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy950)
            .verticalScroll(rememberScrollState())
    ) {
        // Sticky Top Web Nav Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = DarkNavy900,
            border = BorderStroke(1.dp, DarkCardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = CyanAccent.copy(alpha = 0.2f),
                        modifier = Modifier.size(34.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(20.dp))
                        }
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "OMNIRIDE",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onLaunchCustomerApp,
                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("btn_web_launch_app")
                    ) {
                        Text("Open App", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onCloseLanding) {
                        Icon(Icons.Default.Close, contentDescription = "Close Web View", tint = TextSecondary)
                    }
                }
            }
        }

        // Hero Section with Generated City Grid Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Smart City Transportation Network",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xBB070B14))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EmeraldGreen.copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, EmeraldGreen)
                ) {
                    Text(
                        text = "0% PLATFORM COMMISSION MODEL",
                        color = EmeraldGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Fair Ride-Hailing & Courier Delivery",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "Direct passenger fare bidding. Drivers keep 100% of their platform earnings.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp
                )
            }
        }

        Column(modifier = Modifier.padding(18.dp)) {
            // Quick Call to Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onLaunchCustomerApp,
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Text("Book Ride / Delivery", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                Button(
                    onClick = onLaunchDriverApp,
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).height(46.dp)
                ) {
                    Text("Drive & Earn 100%", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pillar 1: Why 0% Platform Commission
            Text("The Economic Architecture", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AttachMoney, contentDescription = null, tint = EmeraldGreen)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("True 0% Commission for Drivers", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Legacy platforms take 25% to 35% cuts from every trip. OmniRide charges 0.00% commission. The driver keeps 100% of the net fare after mandatory provincial transport sales taxes (5%). Operates natively in Pakistani Rupees (PKR / Rs.).",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pillar 2: inDrive-Style Fair Bidding
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = CyanAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Passenger Price Negotiation", color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Passengers enter their pickup, destination, and offer their own price. Nearby drivers review the request and can accept directly or propose a counter-offer. The passenger picks the best driver based on rating, vehicle, and price.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Pillar 3: On-Demand Courier Delivery
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AmberAlert)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Food & Package Courier Network", color = AmberAlert, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Deliver documents, food, parcels, and fragile goods across town with real-time GPS tracking, masked recipient contact, and digital delivery confirmation.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pillar 4: Safety Center Standards
            Text("Safety & Verification Protocols", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFEF4444))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Multi-Tier Safety Shield", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("• In-App 911 SOS beacon with instant live coordinate dispatch", color = TextSecondary, fontSize = 12.sp)
                    Text("• Live Trip Sharing link with real-time GPS progression", color = TextSecondary, fontSize = 12.sp)
                    Text("• Masked proxy phone numbers protecting personal contact privacy", color = TextSecondary, fontSize = 12.sp)
                    Text("• Biometric KYC Driver Verification (CNIC, License, Vehicle Registration)", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Full-Stack Architecture Specifications (Fulfilling production deployment readiness)
            Text("Full-Stack Architecture & API Specs", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricIndigo.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Code, contentDescription = null, tint = ElectricIndigo)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cross-Platform Deployable Engine", color = ElectricIndigo, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Mobile Runtime: Kotlin Jetpack Compose (Android) + Room local DB", color = TextSecondary, fontSize = 12.sp)
                    Text("• Web Runtime: Responsive M3 UI / Webview bridge compatible with Next.js/Vercel", color = TextSecondary, fontSize = 12.sp)
                    Text("• Backend API: Retrofit / OkHttp REST contracts with Moshi serialization", color = TextSecondary, fontSize = 12.sp)
                    Text("• Real-Time Telemetry: WebSocket coordinate stream with Haversine GPS interpolation", color = TextSecondary, fontSize = 12.sp)
                    Text("• Database: Room SQLite relational schema with Foreign Keys and transactional DAOs", color = TextSecondary, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Interactive FAQ Accordion
            Text("Frequently Asked Questions", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            val faqs = listOf(
                "How does the inDrive-style fare bidding work?" to "Passengers propose a preferred fare when requesting a ride. Nearby registered drivers receive the offer and can accept directly or send back a counter-offer. The passenger compares ratings, car models, and prices before accepting.",
                "How can OmniRide afford 0% platform commission?" to "OmniRide uses an ultra-lean automated dispatch model. Drivers keep 100% of their platform earnings, paying only legally mandatory government transport taxes (5%) and third-party payment gateway fees if paying via credit card.",
                "How are driver identities and documents verified?" to "Every driver must complete KYC onboarding by uploading their commercial driving license, government ID/CNIC, vehicle registration, and photo inspection. The operations admin team verifies every document prior to granting online status.",
                "Is OmniRide available on Web, Android, and iOS?" to "Yes. The platform architecture is modularly decoupled into API interfaces, reactive StateFlow repositories, and responsive Compose components engineered to deploy seamlessly across Web, Android, and cross-platform clients."
            )

            faqs.forEachIndexed { index, (question, answer) ->
                val isExpanded = expandedFaqIndex == index
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { expandedFaqIndex = if (isExpanded) -1 else index }
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(question, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, modifier = Modifier.weight(1f))
                            Icon(
                                imageVector = if (isExpanded) Icons.Default.Close else Icons.Default.ArrowDownward,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        AnimatedVisibility(visible = isExpanded) {
                            Column {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(answer, color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("OmniRide Global Mobility Platform", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("Terms of Service • Privacy Policy • Driver Safety Charter • 0% Commission Rules", color = Color(0xFF64748B), fontSize = 10.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Production Build v1.0.0 • Secured with AES-256 & TLS 1.3", color = Color(0xFF475569), fontSize = 10.sp)
            }
        }
    }
}
