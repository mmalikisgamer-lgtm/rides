package com.example.omniride.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import com.example.omniride.data.local.entity.AuditLogEntity
import com.example.omniride.data.local.entity.ComplaintEntity
import com.example.omniride.data.local.entity.DriverDocumentEntity
import com.example.omniride.data.local.entity.DriverEntity
import com.example.omniride.data.local.entity.PlatformSettingEntity
import com.example.omniride.data.local.entity.RideRequestEntity
import com.example.omniride.ui.components.GlassCard
import com.example.omniride.ui.components.QuickStatCard
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CrimsonSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavy900
import com.example.ui.theme.DarkNavy950
import com.example.ui.theme.ElectricIndigo
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AdminDashboardScreen(
    drivers: List<DriverEntity>,
    rides: List<RideRequestEntity>,
    pendingDocs: List<DriverDocumentEntity>,
    complaints: List<ComplaintEntity>,
    auditLogs: List<AuditLogEntity>,
    settings: List<PlatformSettingEntity>,
    onApproveDoc: (String, String) -> Unit,
    onRejectDoc: (String, String, String) -> Unit,
    onResolveComplaint: (String, String) -> Unit,
    onUpdateSetting: (String, String) -> Unit
) {
    var adminTab by remember { mutableIntStateOf(0) } // 0=Overview, 1=KYC Queue, 2=Disputes, 3=Settings, 4=Audit Log

    val totalGrossVolume = remember(rides) {
        rides.filter { it.status.name == "COMPLETED" }.sumOf { if (it.finalAgreedFare > 0) it.finalAgreedFare else it.customerOfferedFare }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkNavy950)
            .padding(16.dp)
    ) {
        // Admin Header
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = ElectricIndigo.copy(alpha = 0.2f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Assessment, contentDescription = null, tint = ElectricIndigo)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Operations Command Center", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Real-Time Fleet & Compliance Control", color = CyanAccent, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs
        TabRow(
            selectedTabIndex = adminTab,
            containerColor = DarkCardSurface,
            contentColor = CyanAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[adminTab]),
                    color = CyanAccent
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Tab(selected = adminTab == 0, onClick = { adminTab = 0 }, text = { Text("Overview", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = adminTab == 1, onClick = { adminTab = 1 }, text = { Text("KYC (${pendingDocs.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = adminTab == 2, onClick = { adminTab = 2 }, text = { Text("Disputes (${complaints.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = adminTab == 3, onClick = { adminTab = 3 }, text = { Text("Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
            Tab(selected = adminTab == 4, onClick = { adminTab = 4 }, text = { Text("Audit", fontSize = 11.sp, fontWeight = FontWeight.Bold) })
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Tab Content
        when (adminTab) {
            // Overview KPI Cards
            0 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickStatCard(
                            title = "Active Fleet",
                            value = "${drivers.count { it.isOnline }} Drivers",
                            subtitle = "${drivers.size} registered",
                            icon = Icons.Default.DirectionsCar,
                            accentColor = EmeraldGreen,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = "Pending KYC",
                            value = "${pendingDocs.size} In Queue",
                            subtitle = "Requires review",
                            icon = Icons.Default.AssignmentTurnedIn,
                            accentColor = AmberAlert,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickStatCard(
                            title = "Gross Volume",
                            value = "Rs. ${String.format("%,.0f", totalGrossVolume)}",
                            subtitle = "0% platform fee",
                            icon = Icons.Default.People,
                            accentColor = CyanAccent,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = "Disputes",
                            value = "${complaints.count { it.status == "OPEN" }} Open",
                            subtitle = "${complaints.size} total tickets",
                            icon = Icons.Default.ReportProblem,
                            accentColor = CrimsonSOS,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Registered Drivers Table Preview
                    Text("Driver Partner Fleet Status", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    drivers.forEach { drv ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(drv.fullName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${drv.vehicleMake} ${drv.vehicleModel} (${drv.plateNumber})", color = TextSecondary, fontSize = 11.sp)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (drv.kycStatus.name == "APPROVED") EmeraldGreen.copy(alpha = 0.2f) else AmberAlert.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = drv.kycStatus.name,
                                            color = if (drv.kycStatus.name == "APPROVED") EmeraldGreen else AmberAlert,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = if (drv.isOnline) "🟢 Online" else "⚪ Offline",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // KYC Document Review Queue
            1 -> {
                if (pendingDocs.isEmpty()) {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(30.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("All Driver Documents Verified", color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("No pending KYC items in the verification queue.", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(pendingDocs) { doc ->
                            GlassCard(
                                modifier = Modifier.fillMaxWidth(),
                                borderColor = AmberAlert.copy(alpha = 0.5f)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(doc.title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AmberAlert.copy(alpha = 0.2f)
                                        ) {
                                            Text("PENDING", color = AmberAlert, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Driver: ${doc.driverName} (ID: ${doc.driverId})", color = CyanAccent, fontSize = 12.sp)
                                    Text("Masked Document Number: ${doc.documentNumberMasked}", color = TextSecondary, fontSize = 12.sp)
                                    Text("Notes: ${doc.reviewNotes}", color = TextSecondary, fontSize = 11.sp)

                                    Spacer(modifier = Modifier.height(12.dp))

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Button(
                                            onClick = { onApproveDoc(doc.id, doc.driverId) },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).testTag("btn_approve_doc_${doc.id}")
                                        ) {
                                            Icon(Icons.Default.Check, contentDescription = null, tint = DarkNavy950, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Approve KYC", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                        OutlinedButton(
                                            onClick = { onRejectDoc(doc.id, doc.driverId, "Incomplete or unreadable document upload") },
                                            shape = RoundedCornerShape(10.dp),
                                            border = BorderStroke(1.dp, CrimsonSOS),
                                            modifier = Modifier.weight(1f).testTag("btn_reject_doc_${doc.id}")
                                        ) {
                                            Icon(Icons.Default.Close, contentDescription = null, tint = CrimsonSOS, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reject", color = CrimsonSOS, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Customer & Driver Dispute Tickets
            2 -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(complaints) { ticket ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(ticket.subject, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (ticket.status == "RESOLVED") EmeraldGreen.copy(alpha = 0.2f) else CrimsonSOS.copy(alpha = 0.2f)
                                    ) {
                                        Text(ticket.status, color = if (ticket.status == "RESOLVED") EmeraldGreen else CrimsonSOS, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Reported by ${ticket.reporterName} (${ticket.reporterRole}) • Category: ${ticket.category}", color = TextSecondary, fontSize = 11.sp)
                                Text("Description: ${ticket.description}", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))

                                if (ticket.status != "RESOLVED") {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { onResolveComplaint(ticket.complaintId, "Dispute investigated and resolved with customer satisfaction.") },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("btn_resolve_ticket_${ticket.complaintId}")
                                    ) {
                                        Text("Mark as Resolved", color = DarkNavy950, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                } else {
                                    Text("Resolution: ${ticket.adminResolutionNotes}", color = EmeraldGreen, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Platform Configuration (0% commission, rates, tax)
            3 -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text("Platform Economic & Safety Rules", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    settings.forEach { setting ->
                        GlassCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(setting.settingKey, color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(setting.settingValue, color = EmeraldGreen, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                }
                                Text(setting.description, color = TextSecondary, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            // Real-time Audit Trail
            4 -> {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(auditLogs) { log ->
                        val dateFormatted = remember(log.timestamp) {
                            SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                        }
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.action, color = CyanAccent, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(dateFormatted, color = TextSecondary, fontSize = 11.sp)
                                }
                                Text("Actor: ${log.actor} • Target: ${log.targetType}", color = TextSecondary, fontSize = 11.sp)
                                Text(log.details, color = TextPrimary, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
