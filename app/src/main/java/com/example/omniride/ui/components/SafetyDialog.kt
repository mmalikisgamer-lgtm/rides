package com.example.omniride.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.omniride.service.SafetyService
import com.example.ui.theme.AmberAlert
import com.example.ui.theme.CrimsonSOS
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkCardSurface
import com.example.ui.theme.DarkNavy900
import com.example.ui.theme.DarkNavy950
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SafetyCenterDialog(
    rideId: String,
    emergencyPhone: String,
    onDismiss: () -> Unit,
    onTriggerSos: () -> Unit,
    onReportIssue: () -> Unit
) {
    var shareCopied by remember { mutableStateOf(false) }
    var sosTriggeredState by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = DarkCardSurface,
            border = BorderStroke(1.dp, DarkCardBorder),
            modifier = Modifier.fillMaxWidth().padding(8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0x22EF4444),
                        modifier = Modifier.size(38.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CrimsonSOS,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Safety & Emergency Center",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "24/7 Monitored Dispatch",
                            color = EmeraldGreen,
                            fontSize = 12.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (sosTriggeredState) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = CrimsonSOS.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, CrimsonSOS),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "🚨 SOS BEACON BROADCASTING",
                                color = CrimsonSOS,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Live GPS coordinates sent to police dispatch & your emergency contact. Keep your phone line clear.",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // SOS Emergency Button
                Button(
                    onClick = {
                        sosTriggeredState = true
                        onTriggerSos()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("btn_sos_trigger"),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonSOS),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TRIGGER EMERGENCY SOS (911)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Share Trip
                OutlinedButton(
                    onClick = {
                        shareCopied = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_trip"),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = CyanAccent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (shareCopied) "Trip Link Copied!" else "Share Live Trip with Contacts",
                        color = CyanAccent,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Emergency Contact Call
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = DarkNavy900,
                    border = BorderStroke(1.dp, DarkCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, tint = EmeraldGreen)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Trusted Contact",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Text(
                                text = SafetyService.maskPhoneNumber(emergencyPhone),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Report an Issue
                Button(
                    onClick = {
                        onDismiss()
                        onReportIssue()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("btn_report_issue"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Report Safety Issue or Incident", color = Color.White, fontSize = 13.sp)
                }
            }
        }
    }
}
