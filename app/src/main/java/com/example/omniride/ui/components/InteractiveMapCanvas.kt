package com.example.omniride.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkNavy900
import com.example.ui.theme.DarkNavy950
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GlassNavyOverlay

@Composable
fun InteractiveMapCanvas(
    modifier: Modifier = Modifier,
    pickupLabel: String = "Pickup Point",
    dropoffLabel: String = "Destination",
    driverVehicleModel: String? = null,
    driverVehiclePlate: String? = null,
    driverFraction: Float = 0.5f, // 0.0 at pickup -> 1.0 at dropoff
    isRadarScanning: Boolean = false,
    statusText: String = "Live Telemetry"
) {
    var panOffsetX by remember { mutableFloatStateOf(0f) }
    var panOffsetY by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "map_radar")
    val radarPulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radar_pulse"
    )

    Box(
        modifier = modifier
            .background(DarkNavy950)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    panOffsetX += dragAmount.x
                    panOffsetY += dragAmount.y
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize().testTag("interactive_map_canvas")) {
            val width = size.width
            val height = size.height
            val centerX = (width / 2f) + panOffsetX
            val centerY = (height / 2f) + panOffsetY

            // 1. Draw sleek futuristic urban grid background
            val gridSpacing = 48.dp.toPx() * zoomScale
            var x = (centerX % gridSpacing)
            while (x < width) {
                drawLine(
                    color = Color(0x1838BDF8),
                    start = Offset(x, 0f),
                    end = Offset(x, height),
                    strokeWidth = 1.dp.toPx()
                )
                x += gridSpacing
            }
            var y = (centerY % gridSpacing)
            while (y < height) {
                drawLine(
                    color = Color(0x1838BDF8),
                    start = Offset(0f, y),
                    end = Offset(width, y),
                    strokeWidth = 1.dp.toPx()
                )
                y += gridSpacing
            }

            // 2. Main thoroughfares / arterials
            val mainRoadPath1 = Path().apply {
                moveTo(0f, centerY - 80f * zoomScale)
                cubicTo(
                    centerX - 100f * zoomScale, centerY - 120f * zoomScale,
                    centerX + 80f * zoomScale, centerY + 60f * zoomScale,
                    width, centerY + 100f * zoomScale
                )
            }
            drawPath(
                path = mainRoadPath1,
                color = Color(0x3306B6D4),
                style = Stroke(width = 10.dp.toPx() * zoomScale)
            )

            val mainRoadPath2 = Path().apply {
                moveTo(centerX - 120f * zoomScale, 0f)
                cubicTo(
                    centerX - 60f * zoomScale, centerY - 60f * zoomScale,
                    centerX + 40f * zoomScale, centerY + 140f * zoomScale,
                    centerX + 180f * zoomScale, height
                )
            }
            drawPath(
                path = mainRoadPath2,
                color = Color(0x2210B981),
                style = Stroke(width = 8.dp.toPx() * zoomScale)
            )

            // 3. Define Pickup and Dropoff Screen Coordinates
            val pickupX = centerX - 120f * zoomScale
            val pickupY = centerY + 80f * zoomScale

            val dropoffX = centerX + 130f * zoomScale
            val dropoffY = centerY - 90f * zoomScale

            // 4. Draw Route connecting Pickup & Dropoff
            val routePath = Path().apply {
                moveTo(pickupX, pickupY)
                cubicTo(
                    pickupX + 30f * zoomScale, pickupY - 140f * zoomScale,
                    dropoffX - 40f * zoomScale, dropoffY + 120f * zoomScale,
                    dropoffX, dropoffY
                )
            }

            // Route halo/glow
            drawPath(
                path = routePath,
                color = Color(0x3300A859),
                style = Stroke(width = 9.dp.toPx() * zoomScale)
            )
            // Dashed sharp route line
            drawPath(
                path = routePath,
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFF59E0B), Color(0xFF00A859)),
                    start = Offset(pickupX, pickupY),
                    end = Offset(dropoffX, dropoffY)
                ),
                style = Stroke(
                    width = 4.dp.toPx() * zoomScale,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f), 0f)
                )
            )

            // 5. Radar pulse at pickup if searching/scanning
            if (isRadarScanning) {
                val maxRadius = 140.dp.toPx() * zoomScale
                val currentRadius = maxRadius * radarPulse
                drawCircle(
                    color = EmeraldGreen.copy(alpha = (1f - radarPulse) * 0.45f),
                    radius = currentRadius,
                    center = Offset(pickupX, pickupY),
                    style = Stroke(width = 2.dp.toPx())
                )
            }

            // 6. Pickup Marker (Gold & Emerald Glow)
            drawCircle(
                color = Color(0xFFF59E0B).copy(alpha = 0.25f),
                radius = 22.dp.toPx() * zoomScale,
                center = Offset(pickupX, pickupY)
            )
            drawCircle(
                color = Color(0xFFF59E0B),
                radius = 9.dp.toPx() * zoomScale,
                center = Offset(pickupX, pickupY)
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx() * zoomScale,
                center = Offset(pickupX, pickupY)
            )

            // 7. Dropoff Marker (Emerald Green Flag Glow)
            drawCircle(
                color = EmeraldGreen.copy(alpha = 0.35f),
                radius = 22.dp.toPx() * zoomScale,
                center = Offset(dropoffX, dropoffY)
            )
            drawCircle(
                color = EmeraldGreen,
                radius = 9.dp.toPx() * zoomScale,
                center = Offset(dropoffX, dropoffY)
            )
            drawCircle(
                color = Color.White,
                radius = 4.dp.toPx() * zoomScale,
                center = Offset(dropoffX, dropoffY)
            )

            // 8. Vehicle Position Interpolation along route
            val t = driverFraction.coerceIn(0f, 1f)
            val driverX = (1 - t) * (1 - t) * (1 - t) * pickupX +
                    3 * (1 - t) * (1 - t) * t * (pickupX + 30f * zoomScale) +
                    3 * (1 - t) * t * t * (dropoffX - 40f * zoomScale) +
                    t * t * t * dropoffX

            val driverY = (1 - t) * (1 - t) * (1 - t) * pickupY +
                    3 * (1 - t) * (1 - t) * t * (pickupY - 140f * zoomScale) +
                    3 * (1 - t) * t * t * (dropoffY + 120f * zoomScale) +
                    t * t * t * dropoffY

            // Draw Driver Vehicle Marker
            drawCircle(
                color = Color(0x55F59E0B),
                radius = 20.dp.toPx() * zoomScale,
                center = Offset(driverX, driverY)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0xFFFBBF24), Color(0xFFD97706)),
                    center = Offset(driverX, driverY),
                    radius = 12.dp.toPx() * zoomScale
                ),
                radius = 11.dp.toPx() * zoomScale,
                center = Offset(driverX, driverY)
            )
            // Car center core
            drawCircle(
                color = Color(0xFF0F172A),
                radius = 4.dp.toPx() * zoomScale,
                center = Offset(driverX, driverY)
            )
        }

        // Top Status Badge
        Surface(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
            shape = RoundedCornerShape(20.dp),
            color = GlassNavyOverlay,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x3338BDF8))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(EmeraldGreen, CircleShape)
                )
                Text(
                    text = " $statusText",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Zoom / Recenter Controls on Right Edge
        Surface(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp),
            shape = RoundedCornerShape(14.dp),
            color = GlassNavyOverlay,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0x22FFFFFF))
        ) {
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.padding(4.dp)
            ) {
                IconButton(
                    onClick = { zoomScale = (zoomScale + 0.2f).coerceAtMost(2.5f) },
                    modifier = Modifier.size(36.dp).testTag("btn_map_zoom_in")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Zoom In", tint = Color.White)
                }
                IconButton(
                    onClick = { zoomScale = (zoomScale - 0.2f).coerceAtLeast(0.6f) },
                    modifier = Modifier.size(36.dp).testTag("btn_map_zoom_out")
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Zoom Out", tint = Color.White)
                }
                IconButton(
                    onClick = {
                        panOffsetX = 0f
                        panOffsetY = 0f
                        zoomScale = 1.0f
                    },
                    modifier = Modifier.size(36.dp).testTag("btn_map_recenter")
                ) {
                    Icon(Icons.Default.MyLocation, contentDescription = "Recenter", tint = CyanAccent)
                }
            }
        }
    }
}
