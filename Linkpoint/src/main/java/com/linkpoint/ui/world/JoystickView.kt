package com.linkpoint.ui.world

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Virtual joystick control for avatar movement.
 * 
 * Provides a touch-based joystick that can be used for:
 * - Movement (forward/backward/strafe)
 * - Camera rotation
 * 
 * Based on the reference viewer's joystick implementation.
 */
class JoystickView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    /**
     * Listener for joystick events.
     */
    interface JoystickListener {
        /**
         * Called when joystick position changes.
         * @param x Horizontal position (-1 to 1)
         * @param y Vertical position (-1 to 1)
         */
        fun onJoystickMoved(x: Float, y: Float)
        
        /**
         * Called when joystick is released.
         */
        fun onJoystickReleased()
    }
    
    var listener: JoystickListener? = null
    
    // Paints
    private val basePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(100, 128, 128, 128)
        style = Paint.Style.FILL
    }
    
    private val stickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 200, 200, 200)
        style = Paint.Style.FILL
    }
    
    private val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(150, 255, 255, 255)
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }

    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(80, 255, 255, 255)
        style = Paint.Style.FILL
    }

    // Dimensions
    private var centerX = 0f
    private var centerY = 0f
    private var baseRadius = 0f
    private var stickRadius = 0f
    
    // Current stick position
    private var stickX = 0f
    private var stickY = 0f
    
    // Touch state
    private var isTouching = false
    
    // Deadzone (prevents drift)
    var deadzone = 0.15f
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        centerX = w / 2f
        centerY = h / 2f
        baseRadius = min(w, h) / 2f * 0.9f
        stickRadius = baseRadius * 0.4f
        
        // Reset stick to center
        stickX = centerX
        stickY = centerY
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        // Draw base circle
        canvas.drawCircle(centerX, centerY, baseRadius, basePaint)
        canvas.drawCircle(centerX, centerY, baseRadius, borderPaint)
        
        // Draw direction indicators
        drawDirectionIndicators(canvas)
        
        // Draw stick
        canvas.drawCircle(stickX, stickY, stickRadius, stickPaint)
        canvas.drawCircle(stickX, stickY, stickRadius, borderPaint)
    }
    
    private fun drawDirectionIndicators(canvas: Canvas) {
        val indicatorSize = baseRadius * 0.15f
        val offset = baseRadius * 0.7f
        
        // Up arrow
        canvas.drawCircle(centerX, centerY - offset, indicatorSize, indicatorPaint)
        // Down arrow
        canvas.drawCircle(centerX, centerY + offset, indicatorSize, indicatorPaint)
        // Left arrow
        canvas.drawCircle(centerX - offset, centerY, indicatorSize, indicatorPaint)
        // Right arrow
        canvas.drawCircle(centerX + offset, centerY, indicatorSize, indicatorPaint)
    }
    
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                isTouching = true

                val maxDistance = baseRadius - stickRadius
                if (maxDistance <= 0f) {
                    // View hasn't been measured yet; nothing meaningful to report.
                    return true
                }

                val dx = event.x - centerX
                val dy = event.y - centerY
                val distance = sqrt(dx * dx + dy * dy)

                if (distance <= maxDistance) {
                    stickX = event.x
                    stickY = event.y
                } else {
                    val ratio = maxDistance / distance
                    stickX = centerX + dx * ratio
                    stickY = centerY + dy * ratio
                }

                val normalizedX = (stickX - centerX) / maxDistance
                val normalizedY = -(stickY - centerY) / maxDistance // Invert Y
                
                // Apply deadzone
                val outputX = if (kotlin.math.abs(normalizedX) < deadzone) 0f else normalizedX
                val outputY = if (kotlin.math.abs(normalizedY) < deadzone) 0f else normalizedY
                
                listener?.onJoystickMoved(outputX, outputY)
                invalidate()
            }
            
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                isTouching = false
                
                // Return stick to center
                stickX = centerX
                stickY = centerY
                
                listener?.onJoystickReleased()
                invalidate()
            }
        }
        
        return true
    }
    
    /**
     * Get current X position (-1 to 1).
     */
    fun getXPosition(): Float {
        val maxDistance = baseRadius - stickRadius
        if (maxDistance <= 0f) return 0f
        return (stickX - centerX) / maxDistance
    }

    /**
     * Get current Y position (-1 to 1).
     */
    fun getYPosition(): Float {
        val maxDistance = baseRadius - stickRadius
        if (maxDistance <= 0f) return 0f
        return -(stickY - centerY) / maxDistance
    }
    
    /**
     * Check if joystick is being touched.
     */
    fun isTouching(): Boolean = isTouching
}
