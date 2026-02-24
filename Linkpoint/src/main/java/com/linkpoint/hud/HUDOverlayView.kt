package com.linkpoint.hud

import android.content.Context
import android.graphics.Bitmap
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
import com.linkpoint.LinkpointApp
import com.linkpoint.assets.TexturePriority
import com.linkpoint.protocol.types.LLVector3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * HUDOverlayView - Renders HUD elements as an overlay on the 3D world view.
 * 
 * This view:
 * - Draws HUD textures at their screen positions
 * - Handles touch events for HUD interaction
 * - Supports HUD visibility toggling
 * 
 * Based on the reference viewer's HUD rendering.
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

    private val hudTexturePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val hudHandlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.argb(220, 230, 230, 230)
        style = Paint.Style.FILL
    }

    private val textureCache = ConcurrentHashMap<UUID, Bitmap>()
    private val textureRequestJobs = ConcurrentHashMap<UUID, Job>()
    private val textureFailureTimestamps = ConcurrentHashMap<UUID, Long>()
    
    // Touch tracking
    private var touchDownTime: Long = 0
    private var touchedHudId: Int? = null
    private var activeHudId: Int? = null
    private var interactionMode: InteractionMode = InteractionMode.NONE
    private var pendingDrag = false
    private var downTouchX = 0f
    private var downTouchY = 0f
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var activeHudRect: RectF? = null
    private var originalLayoutEntry: HudLayoutEntry? = null
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

        val currentIds = huds.map { it.localId }.toSet()
        val iterator = screenBoundsMap.keys.iterator()
        while (iterator.hasNext()) {
            val hudId = iterator.next()
            if (hudId !in currentIds) {
                iterator.remove()
            }
        }
    }
    
    private fun drawHUD(canvas: Canvas, hud: HUDObject) {
        val layoutEntry = hudManager?.getLayoutEntry(hud.attachmentPoint)
        val rect = if (layoutEntry != null && layoutEntry.width > 0f && layoutEntry.height > 0f) {
            val viewWidth = width.toFloat()
            val viewHeight = height.toFloat()
            val left = layoutEntry.x * viewWidth
            val top = layoutEntry.y * viewHeight
            RectF(
                left,
                top,
                left + layoutEntry.width * viewWidth,
                top + layoutEntry.height * viewHeight
            )
        } else {
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
            
            RectF(left, top, left + hudWidth, top + hudHeight)
        }
        
        // Draw HUD background
        canvas.drawRoundRect(rect, 8f, 8f, hudBackgroundPaint)
        canvas.drawRoundRect(rect, 8f, 8f, hudBorderPaint)

        // Try to load and draw the HUD texture; fall back to name label
        val textureId = hudManager?.getPrimaryTextureId(hud)
        if (textureId != null) {
            requestTexture(textureId)
            val bitmap = textureCache[textureId]
            if (bitmap != null) {
                canvas.drawBitmap(bitmap, null, rect, hudTexturePaint)
            } else {
                // Texture not yet loaded - show name as placeholder
                canvas.drawText(
                    hud.name.take(15),
                    rect.centerX(),
                    rect.centerY() + hudTextPaint.textSize / 3,
                    hudTextPaint
                )
            }
        } else {
            // No texture available - show name as placeholder
            canvas.drawText(
                hud.name.take(15),
                rect.centerX(),
                rect.centerY() + hudTextPaint.textSize / 3,
                hudTextPaint
            )
        }

        if (hud.localId == activeHudId) {
            drawResizeHandle(canvas, rect)
        }
        
        // Store bounds for hit testing
        hud.screenBounds = rect
    }

    private fun drawResizeHandle(canvas: Canvas, rect: RectF) {
        val handleLeft = rect.right - handleSizePx
        val handleTop = rect.bottom - handleSizePx
        val handleRect = RectF(handleLeft, handleTop, rect.right, rect.bottom)
        canvas.drawRoundRect(handleRect, handleSizePx / 4f, handleSizePx / 4f, hudHandlePaint)
        val lineOffset = handleSizePx / 3f
        canvas.drawLine(
            handleRect.left + lineOffset,
            handleRect.bottom - lineOffset,
            handleRect.right - lineOffset,
            handleRect.top + lineOffset,
            hudBorderPaint
        )
    }

    private fun requestTexture(textureId: UUID) {
        if (textureCache.containsKey(textureId) || textureRequestJobs.containsKey(textureId)) return

        val lastFailure = textureFailureTimestamps[textureId]
        val now = System.currentTimeMillis()
        if (lastFailure != null && now - lastFailure < 30000L) return

        val app = LinkpointApp.getInstance()
        val job = app.applicationScope.launch(Dispatchers.IO) {
            val bitmap = app.textureManager.getTexture(textureId, TexturePriority.HIGH)
            if (bitmap != null) {
                textureCache[textureId] = bitmap
                textureFailureTimestamps.remove(textureId)
                postInvalidate()
            } else {
                textureFailureTimestamps[textureId] = System.currentTimeMillis()
            }
            textureRequestJobs.remove(textureId)
        }
        textureRequestJobs[textureId] = job
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
                    val inResizeHandle = isInResizeHandle(bounds, event.x, event.y)
                    if (inResizeHandle || bounds.contains(event.x, event.y)) {
                        touchedHudId = hud.localId
                        activeHudId = hud.localId
                        interactionMode = if (inResizeHandle) InteractionMode.RESIZE else InteractionMode.NONE
                        pendingDrag = !inResizeHandle
                        activeHudRect = RectF(bounds)
                        originalLayoutEntry = manager.getLayoutEntry(hud.attachmentPoint)
                        downTouchX = event.x
                        downTouchY = event.y
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
                    val distance = max(kotlin.math.abs(event.x - downTouchX), kotlin.math.abs(event.y - downTouchY))
                    if (distance > touchSlop) {
                        interactionMode = InteractionMode.DRAG
                    }
                }
                val viewWidth = width.toFloat()
                val viewHeight = height.toFloat()
                when (interactionMode) {
                    InteractionMode.DRAG -> {
                        val maxLeft = max(0f, viewWidth - bounds.width())
                        val maxTop = max(0f, viewHeight - bounds.height())
                        val newLeft = (bounds.left + deltaX).coerceIn(0f, maxLeft)
                        val newTop = (bounds.top + deltaY).coerceIn(0f, maxTop)
                        bounds.offsetTo(newLeft, newTop)
                    }
                    InteractionMode.RESIZE -> {
                        val minWidth = min(minHudSizePx, viewWidth)
                        val minHeight = min(minHudSizePx, viewHeight)
                        var newWidth = (bounds.width() + deltaX).coerceAtMost(viewWidth)
                        var newHeight = (bounds.height() + deltaY).coerceAtMost(viewHeight)
                        newWidth = max(minWidth, newWidth)
                        newHeight = max(minHeight, newHeight)
                        if (bounds.left + newWidth > viewWidth) {
                            bounds.left = max(0f, viewWidth - newWidth)
                        }
                        if (bounds.top + newHeight > viewHeight) {
                            bounds.top = max(0f, viewHeight - newHeight)
                        }
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
                val hudId = activeHudId
                if (hudId != null) {
                    val hud = manager.getHUD(hudId)
                    if (hud != null) {
                        val originalEntry = originalLayoutEntry
                        if (originalEntry != null) {
                            manager.setLayoutEntry(hud.attachmentPoint, originalEntry)
                        } else {
                            manager.clearLayoutEntry(hud.attachmentPoint)
                        }
                        invalidate()
                    }
                }
                resetInteraction()
            }
        }
        
        return super.onTouchEvent(event)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w != oldw || h != oldh) {
            resetInteraction()
        }
    }

    private fun resetInteraction() {
        touchedHudId = null
        activeHudId = null
        interactionMode = InteractionMode.NONE
        pendingDrag = false
        activeHudRect = null
        originalLayoutEntry = null
    }

    private fun updateLayoutForHud(hudId: Int, bounds: RectF) {
        val manager = hudManager ?: return
        if (width == 0 || height == 0) return
        val hud = manager.getHUD(hudId) ?: return
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()
        val normalizedLeft = bounds.left / viewWidth
        val normalizedTop = bounds.top / viewHeight
        val normalizedWidth = bounds.width() / viewWidth
        val normalizedHeight = bounds.height() / viewHeight
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

    /**
     * Refresh the HUD display.
     */
    fun refresh() {
        invalidate()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        textureRequestJobs.values.forEach { it.cancel() }
        textureRequestJobs.clear()
        textureCache.clear()
        textureFailureTimestamps.clear()
    }
}

// Extension property to store screen bounds on HUDObject
// Using ConcurrentHashMap for thread safety since bounds may be accessed from multiple threads
private var HUDObject.screenBounds: RectF?
    get() = screenBoundsMap[this.localId]
    set(value) { screenBoundsMap[this.localId] = value }

private val screenBoundsMap = java.util.concurrent.ConcurrentHashMap<Int, RectF?>()
