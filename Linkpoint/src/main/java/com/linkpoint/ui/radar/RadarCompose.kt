package com.linkpoint.ui.radar

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Compose version of the RadarView that displays nearby avatars and objects.
 * 
 * Features an animated sweeping effect inspired by real radar displays.
 * Uses trigonometry and Compose animations for smooth radar sweep.
 * 
 * The radar shows:
 * - Your position (center)
 * - Animated sweep effect
 * - Nearby avatars as dots (green for friends, blue for others)
 * - Distance rings
 * - Cardinal directions
 * 
 * Based on: https://proandroiddev.com/extraordinary-animations-using-trigonometry-and-coroutines-radar-animation
 * 
 * @param modifier Modifier for the radar
 * @param size Size of the radar (diameter)
 * @param range Radar range in meters
 * @param heading Current heading in radians
 * @param blips List of radar blips to display
 * @param enableSweepAnimation Whether to show the animated radar sweep
 * @param sweepDurationMs Duration of one full sweep rotation in milliseconds
 * @param backgroundColor Background color of the radar
 * @param gridColor Color of the grid lines
 * @param sweepColor Color of the radar sweep
 * @param selfColor Color of the self indicator
 * @param friendColor Color of friend blips
 * @param strangerColor Color of stranger blips
 * @param objectColor Color of object blips
 */
@Composable
fun Radar(
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    range: Float = 96f,
    heading: Float = 0f,
    blips: List<RadarBlip> = emptyList(),
    enableSweepAnimation: Boolean = true,
    sweepDurationMs: Int = 2000,
    backgroundColor: Color = Color(0xB4000000),  // 180 alpha black
    gridColor: Color = Color(0x64FFFFFF),  // 100 alpha white
    sweepColor: Color = Color(0xFF00FF00),  // Green sweep
    textColor: Color = Color.White,
    selfColor: Color = Color.Yellow,
    friendColor: Color = Color.Green,
    strangerColor: Color = Color.Cyan,
    objectColor: Color = Color.Red
) {
    // Animated sweep angle using infinite transition
    val sweepAngle by if (enableSweepAnimation) {
        val infiniteTransition = rememberInfiniteTransition(label = "radarSweep")
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = sweepDurationMs, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            ),
            label = "sweepAngle"
        )
    } else {
        remember { androidx.compose.runtime.mutableFloatStateOf(0f) }
    }
    
    // Helper function to convert Compose Color to Android Color int
    fun toAndroidColor(color: Color): Int {
        return android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
    }
    
    val textPaint = remember(textColor) {
        android.graphics.Paint().apply {
            color = toAndroidColor(textColor)
            textSize = 24f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    
    val directionPaint = remember(textColor) {
        android.graphics.Paint().apply {
            color = toAndroidColor(textColor)
            textSize = 28f
            textAlign = android.graphics.Paint.Align.CENTER
            isAntiAlias = true
        }
    }
    
    Canvas(
        modifier = modifier.size(size)
    ) {
        val canvasSize = this.size
        val centerX = canvasSize.width / 2f
        val centerY = canvasSize.height / 2f
        val radius = min(canvasSize.width, canvasSize.height) / 2f * 0.9f
        
        // Draw background
        drawCircle(
            color = backgroundColor,
            radius = radius,
            center = Offset(centerX, centerY)
        )
        
        // Draw animated radar sweep (if enabled)
        if (enableSweepAnimation) {
            drawRadarSweep(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                sweepAngle = sweepAngle,
                sweepColor = sweepColor
            )
        }
        
        // Draw range rings
        drawRangeRings(
            centerX = centerX,
            centerY = centerY,
            radius = radius,
            range = range,
            gridColor = gridColor,
            textPaint = textPaint
        )
        
        // Draw cardinal directions (rotated by heading)
        drawDirections(
            centerX = centerX,
            centerY = centerY,
            radius = radius,
            heading = heading,
            directionPaint = directionPaint
        )
        
        // Draw self at center
        drawCircle(
            color = selfColor,
            radius = 8f,
            center = Offset(centerX, centerY)
        )
        
        // Draw blips
        blips.forEach { blip ->
            drawBlip(
                blip = blip,
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                range = range,
                heading = heading,
                friendColor = friendColor,
                strangerColor = strangerColor,
                objectColor = objectColor,
                selfColor = selfColor,
                textPaint = textPaint
            )
        }
    }
}

/**
 * Draws the animated radar sweep effect.
 * Creates a gradient arc that rotates around the center, simulating a radar scan.
 */
private fun DrawScope.drawRadarSweep(
    centerX: Float,
    centerY: Float,
    radius: Float,
    sweepAngle: Float,
    sweepColor: Color
) {
    // Draw sweep arc with gradient fade effect
    val sweepGradient = Brush.sweepGradient(
        0f to sweepColor.copy(alpha = 0f),
        0.15f to sweepColor.copy(alpha = 0.3f),
        0.16f to sweepColor.copy(alpha = 0.6f),
        0.17f to sweepColor.copy(alpha = 0.8f),
        1f to sweepColor.copy(alpha = 0f),
        center = Offset(centerX, centerY)
    )
    
    rotate(degrees = sweepAngle - 90f, pivot = Offset(centerX, centerY)) {
        drawArc(
            brush = sweepGradient,
            startAngle = 0f,
            sweepAngle = 60f,
            useCenter = true,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = Size(radius * 2, radius * 2)
        )
    }
    
    // Draw sweep line
    val sweepRadians = Math.toRadians(sweepAngle.toDouble())
    val lineEndX = centerX + radius * cos(sweepRadians).toFloat()
    val lineEndY = centerY + radius * sin(sweepRadians).toFloat()
    
    drawLine(
        color = sweepColor.copy(alpha = 0.8f),
        start = Offset(centerX, centerY),
        end = Offset(lineEndX, lineEndY),
        strokeWidth = 2f
    )
}

private fun DrawScope.drawRangeRings(
    centerX: Float,
    centerY: Float,
    radius: Float,
    range: Float,
    gridColor: Color,
    textPaint: android.graphics.Paint
) {
    val ringCount = 3
    for (i in 1..ringCount) {
        val ringRadius = radius * i / ringCount
        
        // Draw ring
        drawCircle(
            color = gridColor,
            radius = ringRadius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1f)
        )
        
        // Draw range label using native canvas
        val rangeLabel = "${(range * i / ringCount).toInt()}m"
        drawContext.canvas.nativeCanvas.drawText(
            rangeLabel,
            centerX + ringRadius - 20,
            centerY - 5,
            textPaint
        )
    }
    
    // Draw crosshairs
    drawLine(
        color = gridColor,
        start = Offset(centerX, centerY - radius),
        end = Offset(centerX, centerY + radius),
        strokeWidth = 1f
    )
    drawLine(
        color = gridColor,
        start = Offset(centerX - radius, centerY),
        end = Offset(centerX + radius, centerY),
        strokeWidth = 1f
    )
}

private fun DrawScope.drawDirections(
    centerX: Float,
    centerY: Float,
    radius: Float,
    heading: Float,
    directionPaint: android.graphics.Paint
) {
    val offset = radius + 20
    
    // Apply heading rotation
    rotate(
        degrees = -Math.toDegrees(heading.toDouble()).toFloat(),
        pivot = Offset(centerX, centerY)
    ) {
        drawContext.canvas.nativeCanvas.apply {
            drawText("N", centerX, centerY - offset, directionPaint)
            drawText("S", centerX, centerY + offset + 20, directionPaint)
            drawText("E", centerX + offset, centerY + 8, directionPaint)
            drawText("W", centerX - offset, centerY + 8, directionPaint)
        }
    }
}

private fun DrawScope.drawBlip(
    blip: RadarBlip,
    centerX: Float,
    centerY: Float,
    radius: Float,
    range: Float,
    heading: Float,
    friendColor: Color,
    strangerColor: Color,
    objectColor: Color,
    selfColor: Color,
    textPaint: android.graphics.Paint
) {
    // Skip if out of range
    if (blip.distance > range) return
    
    // Normalize distance to radar radius
    val normalizedDist = blip.distance / range * radius
    
    // Calculate angle relative to heading
    val angle = blip.bearing - heading
    
    // Convert to screen coordinates
    val x = centerX + normalizedDist * sin(angle)
    val y = centerY - normalizedDist * cos(angle)
    
    // Choose color based on type
    val blipColor = when (blip.type) {
        BlipType.FRIEND -> friendColor
        BlipType.STRANGER -> strangerColor
        BlipType.OBJECT -> objectColor
        BlipType.SELF -> selfColor
    }
    
    // Draw blip
    val blipSize = if (blip.type == BlipType.OBJECT) 4f else 6f
    drawCircle(
        color = blipColor,
        radius = blipSize,
        center = Offset(x, y)
    )
    
    // Draw name for avatars if close
    if (blip.type != BlipType.OBJECT && blip.distance < range / 2) {
        textPaint.textSize = 20f
        drawContext.canvas.nativeCanvas.drawText(
            blip.name,
            x,
            y - 12,
            textPaint
        )
    }
}

/**
 * Creates sample radar blips for testing and previews.
 * 
 * This utility function generates a list of demo blips representing
 * different entity types (friend, stranger, object) at various positions.
 * Useful for @Preview composables and UI testing.
 * 
 * @return List of sample RadarBlip objects for demonstration
 */
fun createSampleBlips(): List<RadarBlip> {
    return listOf(
        RadarBlip(
            id = "1",
            name = "Friend1",
            type = BlipType.FRIEND,
            distance = 30f,
            bearing = 0.5f,
            altitude = 0f
        ),
        RadarBlip(
            id = "2",
            name = "Stranger1",
            type = BlipType.STRANGER,
            distance = 50f,
            bearing = 2.0f,
            altitude = 5f
        ),
        RadarBlip(
            id = "3",
            name = "Object",
            type = BlipType.OBJECT,
            distance = 20f,
            bearing = -1.0f,
            altitude = 0f
        )
    )
}
