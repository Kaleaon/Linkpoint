package com.linkpoint.hud

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.linkpoint.protocol.types.LLVector3

/**
 * HUDOverlayView - Renders HUD elements as an overlay on the 3D world view.
 * 
 * This view:
 * - Draws HUD textures at their screen positions
 * - Handles touch events for HUD interaction
 * - Supports HUD visibility toggling
 * 
 * Based on Lumiya's HUD rendering.
 */
class HUDOverlayView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    interface HUDInteractionListener {
        fun onHUDTouched(hudLocalId: Int, touchPosition: LLVector3)
        fun onHUDLongPressed(hudLocalId: Int)
    }
    
    var listener: HUDInteractionListener? = null
    var hudManager: HUDManager? = null
    
    // Drawing paints
    private val hudBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(180, 40, 40, 40)
        style = Paint.Style.FILL
    }
    
    private val hudBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(200, 100, 100, 100)
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    
    private val hudTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 24f
        textAlign = Paint.Align.CENTER
    }
    
    // Touch tracking
    private var touchDownTime: Long = 0
    private var touchedHudId: Int? = null
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        val manager = hudManager ?: return
        if (!manager.hudsVisible.value) return
        
        val huds = manager.getAllHUDs()
        if (huds.isEmpty()) return
        
        for (hud in huds) {
            drawHUD(canvas, hud)
        }
    }
    
    private fun drawHUD(canvas: Canvas, hud: HUDObject) {
        val screenPos = hudManager?.getScreenPositionForPoint(hud.attachmentPoint) ?: return
        
        // Convert normalized coordinates to pixel coordinates
        val centerX = screenPos.x * width
        val centerY = screenPos.y * height
        
        // Calculate HUD size (based on HUD scale, scaled for screen)
        val hudWidth = hud.scale.x * 150f  // Approximate pixel size
        val hudHeight = hud.scale.y * 150f
        
        // Adjust position based on alignment
        val left: Float
        val top: Float
        
        when (screenPos.alignment) {
            HUDAlignment.TOP_LEFT -> {
                left = centerX
                top = centerY
            }
            HUDAlignment.TOP_CENTER -> {
                left = centerX - hudWidth / 2
                top = centerY
            }
            HUDAlignment.TOP_RIGHT -> {
                left = centerX - hudWidth
                top = centerY
            }
            HUDAlignment.CENTER -> {
                left = centerX - hudWidth / 2
                top = centerY - hudHeight / 2
            }
            HUDAlignment.BOTTOM_LEFT -> {
                left = centerX
                top = centerY - hudHeight
            }
            HUDAlignment.BOTTOM_CENTER -> {
                left = centerX - hudWidth / 2
                top = centerY - hudHeight
            }
            HUDAlignment.BOTTOM_RIGHT -> {
                left = centerX - hudWidth
                top = centerY - hudHeight
            }
        }
        
        val rect = RectF(left, top, left + hudWidth, top + hudHeight)
        
        // Draw HUD background
        canvas.drawRoundRect(rect, 8f, 8f, hudBackgroundPaint)
        canvas.drawRoundRect(rect, 8f, 8f, hudBorderPaint)
        
        // Draw HUD name/label
        canvas.drawText(
            hud.name.take(15),
            rect.centerX(),
            rect.centerY() + hudTextPaint.textSize / 3,
            hudTextPaint
        )
        
        // Store bounds for hit testing
        hud.screenBounds = rect
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val manager = hudManager ?: return false
        if (!manager.hudsVisible.value) return false
        
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchDownTime = System.currentTimeMillis()
                
                // Find which HUD was touched
                val huds = manager.getAllHUDs()
                for (hud in huds) {
                    val bounds = hud.screenBounds ?: continue
                    if (bounds.contains(event.x, event.y)) {
                        touchedHudId = hud.localId
                        return true
                    }
                }
                touchedHudId = null
            }
            
            MotionEvent.ACTION_UP -> {
                val hudId = touchedHudId ?: return false
                val elapsed = System.currentTimeMillis() - touchDownTime
                
                if (elapsed < 500) {
                    // Short tap - touch the HUD
                    val hud = manager.getHUD(hudId)
                    if (hud != null) {
                        val bounds = hud.screenBounds
                        if (bounds != null && bounds.contains(event.x, event.y)) {
                            // Convert touch to HUD local coordinates
                            val localX = (event.x - bounds.left) / bounds.width()
                            val localY = (event.y - bounds.top) / bounds.height()
                            
                            listener?.onHUDTouched(
                                hudId,
                                LLVector3(localX, localY, 0f)
                            )
                            
                            invalidate()
                            return true
                        }
                    }
                } else {
                    // Long press
                    listener?.onHUDLongPressed(hudId)
                    return true
                }
                
                touchedHudId = null
            }
            
            MotionEvent.ACTION_CANCEL -> {
                touchedHudId = null
            }
        }
        
        return super.onTouchEvent(event)
    }
    
    /**
     * Refresh the HUD display.
     */
    fun refresh() {
        invalidate()
    }
}

// Extension property to store screen bounds on HUDObject
// Using ConcurrentHashMap for thread safety since bounds may be accessed from multiple threads
private var HUDObject.screenBounds: RectF?
    get() = screenBoundsMap[this.localId]
    set(value) { screenBoundsMap[this.localId] = value }

private val screenBoundsMap = java.util.concurrent.ConcurrentHashMap<Int, RectF?>()
