package com.linkpoint.hud

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import kotlin.math.max
import kotlin.math.min
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
    private var activeHudId: Int? = null
    private var interactionMode: InteractionMode = InteractionMode.NONE
    private var pendingDrag = false
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activeHudRect: RectF? = null
    private val minHudSizePx = 48f * resources.displayMetrics.density
    private val handleSizePx = 20f * resources.displayMetrics.density
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private enum class InteractionMode {
        NONE,
        DRAG,
        RESIZE
    }
    
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
        val layoutEntry = hudManager?.getLayoutEntry(hud.attachmentPoint)
        val rect = if (layoutEntry != null && layoutEntry.width > 0f && layoutEntry.height > 0f) {
            val left = layoutEntry.x * width
            val top = layoutEntry.y * height
            RectF(
                left,
                top,
                left + layoutEntry.width * width,
                top + layoutEntry.height * height
            )
        } else {
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
            
            RectF(left, top, left + hudWidth, top + hudHeight)
        }
        
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

        drawResizeHandle(canvas, rect)
        
        // Store bounds for hit testing
        hud.screenBounds = rect
    }

    private fun drawResizeHandle(canvas: Canvas, rect: RectF) {
        val handleRect = RectF(
            rect.right - handleSizePx,
            rect.bottom - handleSizePx,
            rect.right,
            rect.bottom
        )
        canvas.drawRoundRect(handleRect, 6f, 6f, hudBorderPaint)
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
                    if (isInResizeHandle(bounds, event.x, event.y)) {
                        touchedHudId = hud.localId
                        activeHudId = hud.localId
                        interactionMode = InteractionMode.RESIZE
                        activeHudRect = RectF(bounds)
                        lastTouchX = event.x
                        lastTouchY = event.y
                        return true
                    }
                    if (bounds.contains(event.x, event.y)) {
                        touchedHudId = hud.localId
                        activeHudId = hud.localId
                        interactionMode = InteractionMode.NONE
                        pendingDrag = true
                        activeHudRect = RectF(bounds)
                        lastTouchX = event.x
                        lastTouchY = event.y
                        return true
                    }
                }
                touchedHudId = null
                activeHudId = null
                interactionMode = InteractionMode.NONE
            }
            
            MotionEvent.ACTION_MOVE -> {
                val hudId = activeHudId ?: return false
                val bounds = activeHudRect ?: return false
                val deltaX = event.x - lastTouchX
                val deltaY = event.y - lastTouchY
                if (interactionMode == InteractionMode.NONE && pendingDrag) {
                    val distance = max(kotlin.math.abs(deltaX), kotlin.math.abs(deltaY))
                    if (distance > touchSlop) {
                        interactionMode = InteractionMode.DRAG
                    }
                }
                when (interactionMode) {
                    InteractionMode.DRAG -> {
                        val newLeft = clamp(bounds.left + deltaX, 0f, width - bounds.width())
                        val newTop = clamp(bounds.top + deltaY, 0f, height - bounds.height())
                        bounds.offsetTo(newLeft, newTop)
                    }
                    InteractionMode.RESIZE -> {
                        val newWidth = max(minHudSizePx, min(bounds.width() + deltaX, width - bounds.left))
                        val newHeight = max(minHudSizePx, min(bounds.height() + deltaY, height - bounds.top))
                        bounds.right = bounds.left + newWidth
                        bounds.bottom = bounds.top + newHeight
                    }
                    InteractionMode.NONE -> Unit
                }
                activeHudRect = bounds
                lastTouchX = event.x
                lastTouchY = event.y
                updateLayoutForHud(hudId, bounds)
                invalidate()
                return true
            }

            MotionEvent.ACTION_UP -> {
                if (interactionMode != InteractionMode.NONE) {
                    manager.persistLayoutConfig()
                    resetInteraction()
                    return true
                }
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
                            resetInteraction()
                            return true
                        }
                    }
                } else {
                    // Long press
                    listener?.onHUDLongPressed(hudId)
                    resetInteraction()
                    return true
                }
                
                resetInteraction()
            }
            
            MotionEvent.ACTION_CANCEL -> {
                resetInteraction()
            }
        }
        
        return super.onTouchEvent(event)
    }

    private fun resetInteraction() {
        touchedHudId = null
        activeHudId = null
        interactionMode = InteractionMode.NONE
        pendingDrag = false
        activeHudRect = null
    }

    private fun updateLayoutForHud(hudId: Int, bounds: RectF) {
        val manager = hudManager ?: return
        if (width == 0 || height == 0) return
        val hud = manager.getHUD(hudId) ?: return
        val normalizedLeft = bounds.left / width
        val normalizedTop = bounds.top / height
        val normalizedWidth = bounds.width() / width
        val normalizedHeight = bounds.height() / height
        manager.setLayoutEntry(
            hud.attachmentPoint,
            HudLayoutEntry(
                x = normalizedLeft,
                y = normalizedTop,
                width = normalizedWidth,
                height = normalizedHeight
            )
        )
    }

    private fun isInResizeHandle(bounds: RectF, x: Float, y: Float): Boolean {
        return x >= bounds.right - handleSizePx &&
            x <= bounds.right &&
            y >= bounds.bottom - handleSizePx &&
            y <= bounds.bottom
    }

    private fun clamp(value: Float, minValue: Float, maxValue: Float): Float {
        return max(minValue, min(value, maxValue))
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
