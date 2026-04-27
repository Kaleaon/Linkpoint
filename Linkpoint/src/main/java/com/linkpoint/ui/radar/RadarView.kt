package com.linkpoint.ui.radar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.linkpoint.protocol.types.LLVector3
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Custom view that displays a radar showing nearby avatars and objects.
 * 
 * The radar shows:
 * - Your position (center)
 * - Nearby avatars as dots (green for friends, blue for others)
 * - Distance rings
 * - Cardinal directions
 * 
 * Based on the reference viewer's radar feature.
 */
class RadarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // Paints
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 0, 0, 0)
        style = Paint.Style.FILL
    }
    
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(100, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }
    
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }
    
    private val selfPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
        style = Paint.Style.FILL
    }
    
    private val friendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
        style = Paint.Style.FILL
    }
    
    private val strangerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.CYAN
        style = Paint.Style.FILL
    }
    
    private val objectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.RED
        style = Paint.Style.FILL
    }
    
    // Radar configuration
    var range: Float = 96f  // Meters
        set(value) {
            field = value
            invalidate()
        }
    
    var heading: Float = 0f  // Radians
        set(value) {
            field = value
            invalidate()
        }
    
    // Data
    private val blips = mutableListOf<RadarBlip>()
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        radius = min(w, h) / 2f * 0.9f
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw background
        canvas.drawCircle(centerX, centerY, radius, backgroundPaint)
        
        // Draw range rings
        drawRangeRings(canvas)
        
        // Draw cardinal directions
        drawDirections(canvas)
        
        // Draw self at center
        canvas.drawCircle(centerX, centerY, 8f, selfPaint)
        
        // Draw blips
        for (blip in blips) {
            drawBlip(canvas, blip)
        }
    }
    
    private fun drawRangeRings(canvas: Canvas) {
        val ringCount = 3
        for (i in 1..ringCount) {
            val ringRadius = radius * i / ringCount
            canvas.drawCircle(centerX, centerY, ringRadius, gridPaint)
            
            // Draw range label
            val rangeLabel = "${(range * i / ringCount).toInt()}m"
            canvas.drawText(rangeLabel, centerX + ringRadius - 20, centerY - 5, textPaint)
        }
        
        // Draw crosshairs
        canvas.drawLine(centerX, centerY - radius, centerX, centerY + radius, gridPaint)
        canvas.drawLine(centerX - radius, centerY, centerX + radius, centerY, gridPaint)
    }
    
    private fun drawDirections(canvas: Canvas) {
        val offset = radius + 20
        textPaint.textSize = 28f
        
        // Apply heading rotation
        canvas.save()
        canvas.rotate(-Math.toDegrees(heading.toDouble()).toFloat(), centerX, centerY)
        
        canvas.drawText("N", centerX, centerY - offset, textPaint)
        canvas.drawText("S", centerX, centerY + offset + 20, textPaint)
        canvas.drawText("E", centerX + offset, centerY + 8, textPaint)
        canvas.drawText("W", centerX - offset, centerY + 8, textPaint)
        
        canvas.restore()
        textPaint.textSize = 24f
    }
    
    private fun drawBlip(canvas: Canvas, blip: RadarBlip) {
        // Calculate position on radar
        val distance = blip.distance
        if (distance > range) return  // Out of range
        
        // Normalize distance to radar radius
        val normalizedDist = distance / range * radius
        
        // Calculate angle relative to heading
        val angle = blip.bearing - heading
        
        // Convert to screen coordinates
        val x = centerX + normalizedDist * sin(angle)
        val y = centerY - normalizedDist * cos(angle)
        
        // Choose paint based on type
        val paint = when (blip.type) {
            BlipType.FRIEND -> friendPaint
            BlipType.STRANGER -> strangerPaint
            BlipType.OBJECT -> objectPaint
            BlipType.SELF -> selfPaint
        }
        
        // Draw blip
        val blipSize = if (blip.type == BlipType.OBJECT) 4f else 6f
        canvas.drawCircle(x, y, blipSize, paint)
        
        // Draw name for avatars if close
        if (blip.type != BlipType.OBJECT && distance < range / 2) {
            textPaint.textSize = 20f
            canvas.drawText(blip.name, x, y - 12, textPaint)
        }
    }
    
    /**
     * Update the radar with new blips.
     */
    fun updateBlips(newBlips: List<RadarBlip>) {
        blips.clear()
        blips.addAll(newBlips)
        invalidate()
    }
    
    /**
     * Clear all blips.
     */
    fun clearBlips() {
        blips.clear()
        invalidate()
    }
    
    /**
     * Set the radar range in meters.
     */
    fun setRadarRange(meters: Float) {
        range = meters
    }
}

/**
 * Represents a blip on the radar.
 */
data class RadarBlip(
    val id: String,
    val name: String,
    val type: BlipType,
    val distance: Float,
    val bearing: Float,  // Radians from north
    val altitude: Float  // Height relative to self
)

enum class BlipType {
    SELF,
    FRIEND,
    STRANGER,
    OBJECT
}

/**
 * Helper to calculate radar blips from world positions.
 */
object RadarUtils {
    
    /**
     * Calculate distance between two positions.
     */
    fun calculateDistance(from: LLVector3, to: LLVector3): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        val dz = to.z - from.z
        return sqrt(dx * dx + dy * dy + dz * dz)
    }
    
    /**
     * Calculate bearing from one position to another.
     */
    fun calculateBearing(from: LLVector3, to: LLVector3): Float {
        val dx = to.x - from.x
        val dy = to.y - from.y
        return atan2(dx, dy)
    }
    
    /**
     * Create a radar blip from world data.
     */
    fun createBlip(
        id: String,
        name: String,
        isFriend: Boolean,
        isObject: Boolean,
        selfPosition: LLVector3,
        targetPosition: LLVector3
    ): RadarBlip {
        val distance = calculateDistance(selfPosition, targetPosition)
        val bearing = calculateBearing(selfPosition, targetPosition)
        val altitudeDiff = targetPosition.z - selfPosition.z
        
        val type = when {
            isObject -> BlipType.OBJECT
            isFriend -> BlipType.FRIEND
            else -> BlipType.STRANGER
        }
        
        return RadarBlip(
            id = id,
            name = name,
            type = type,
            distance = distance,
            bearing = bearing,
            altitude = altitudeDiff
        )
    }
}
