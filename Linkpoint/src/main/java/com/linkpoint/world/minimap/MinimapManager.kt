package com.linkpoint.world.minimap

import android.graphics.*
import android.util.Log
import com.linkpoint.protocol.messages.UDPConnectionFixed
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Minimap System - Renders a top-down view of the current region.
 * 
 * Based on Lumiya's Minimap.java
 * 
 * Features:
 * - Region terrain view
 * - Avatar positions (self, friends, others)
 * - Object markers
 * - Parcel boundaries
 * - For-sale land markers
 * - Rotation based on camera direction
 */
class MinimapManager(
    private val udpConnection: UDPConnectionFixed
) {
    companion object {
        private const val TAG = "MinimapManager"
        
        // Map sizes
        const val MINIMAP_SIZE = 256
        const val REGION_SIZE = 256f
        
        // Marker types
        const val MARKER_SELF = 0
        const val MARKER_FRIEND = 1
        const val MARKER_OTHER = 2
        const val MARKER_OBJECT = 3
        const val MARKER_LAND_FOR_SALE = 4
        
        // Colors
        val COLOR_SELF = Color.rgb(0, 255, 0)
        val COLOR_FRIEND = Color.rgb(0, 200, 255)
        val COLOR_OTHER = Color.rgb(255, 255, 0)
        val COLOR_OBJECT = Color.rgb(200, 200, 200)
        val COLOR_LAND_SALE = Color.rgb(255, 255, 0)
        val COLOR_WATER = Color.rgb(0, 80, 150)
        val COLOR_LAND = Color.rgb(80, 120, 60)
        val COLOR_PARCEL_BORDER = Color.rgb(255, 0, 0)
    }
    
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Region terrain texture
    private var terrainBitmap: Bitmap? = null
    
    // Avatar markers
    private val avatarMarkers = ConcurrentHashMap<UUID, MinimapMarker>()
    
    // Object markers
    private val objectMarkers = ConcurrentHashMap<Int, MinimapMarker>()
    
    // Current position and rotation
    private var selfPosition = PointF(128f, 128f)
    private var cameraRotation = 0f
    
    // Rendered minimap
    private val _minimapBitmap = MutableStateFlow<Bitmap?>(null)
    val minimapBitmap: StateFlow<Bitmap?> = _minimapBitmap
    
    // Map mode
    private var rotateWithCamera = true
    private var zoomLevel = 1f
    
    init {
        // Create default terrain
        createDefaultTerrain()
    }
    
    private fun createDefaultTerrain() {
        terrainBitmap = Bitmap.createBitmap(MINIMAP_SIZE, MINIMAP_SIZE, Bitmap.Config.ARGB_8888).apply {
            val canvas = Canvas(this)
            canvas.drawColor(COLOR_LAND)
        }
    }
    
    /**
     * Set terrain texture from region data.
     */
    fun setTerrainTexture(bitmap: Bitmap) {
        terrainBitmap?.recycle()
        terrainBitmap = Bitmap.createScaledBitmap(bitmap, MINIMAP_SIZE, MINIMAP_SIZE, true)
        renderMinimap()
    }
    
    /**
     * Update self position.
     */
    fun updateSelfPosition(x: Float, y: Float, z: Float) {
        selfPosition = PointF(x, y)
        avatarMarkers[UUID(0, 0)] = MinimapMarker(
            id = UUID(0, 0),
            x = x,
            y = y,
            z = z,
            type = MARKER_SELF,
            name = "You"
        )
        renderMinimap()
    }
    
    /**
     * Update camera rotation.
     */
    fun updateCameraRotation(rotation: Float) {
        cameraRotation = rotation
        if (rotateWithCamera) {
            renderMinimap()
        }
    }
    
    /**
     * Update avatar position.
     */
    fun updateAvatarPosition(avatarId: UUID, name: String, x: Float, y: Float, z: Float, isFriend: Boolean) {
        avatarMarkers[avatarId] = MinimapMarker(
            id = avatarId,
            x = x,
            y = y,
            z = z,
            type = if (isFriend) MARKER_FRIEND else MARKER_OTHER,
            name = name
        )
        renderMinimap()
    }
    
    /**
     * Remove avatar marker.
     */
    fun removeAvatarMarker(avatarId: UUID) {
        avatarMarkers.remove(avatarId)
        renderMinimap()
    }
    
    /**
     * Update object marker.
     */
    fun updateObjectMarker(localId: Int, x: Float, y: Float, z: Float) {
        objectMarkers[localId] = MinimapMarker(
            id = UUID(0, localId.toLong()),
            x = x,
            y = y,
            z = z,
            type = MARKER_OBJECT,
            name = ""
        )
    }
    
    /**
     * Clear all markers except self.
     */
    fun clearMarkers() {
        val self = avatarMarkers[UUID(0, 0)]
        avatarMarkers.clear()
        objectMarkers.clear()
        self?.let { avatarMarkers[UUID(0, 0)] = it }
        renderMinimap()
    }
    
    /**
     * Set map rotation mode.
     */
    fun setRotateWithCamera(rotate: Boolean) {
        rotateWithCamera = rotate
        renderMinimap()
    }
    
    /**
     * Set zoom level.
     */
    fun setZoomLevel(zoom: Float) {
        zoomLevel = zoom.coerceIn(0.5f, 4f)
        renderMinimap()
    }
    
    /**
     * Render the minimap bitmap.
     */
    private fun renderMinimap() {
        scope.launch {
            val bitmap = Bitmap.createBitmap(MINIMAP_SIZE, MINIMAP_SIZE, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            
            // Save canvas state
            canvas.save()
            
            // Apply rotation if enabled
            if (rotateWithCamera) {
                canvas.rotate(-cameraRotation, MINIMAP_SIZE / 2f, MINIMAP_SIZE / 2f)
            }
            
            // Draw terrain
            terrainBitmap?.let {
                canvas.drawBitmap(it, 0f, 0f, null)
            }
            
            // Restore canvas for markers (markers don't rotate)
            canvas.restore()
            
            // Draw avatar markers
            val markerPaint = Paint().apply {
                isAntiAlias = true
                style = Paint.Style.FILL
            }
            
            val textPaint = Paint().apply {
                isAntiAlias = true
                textSize = 10f
                color = Color.WHITE
                textAlign = Paint.Align.CENTER
                setShadowLayer(2f, 1f, 1f, Color.BLACK)
            }
            
            // Draw other avatars first
            for (marker in avatarMarkers.values) {
                if (marker.type == MARKER_SELF) continue
                drawMarker(canvas, marker, markerPaint, textPaint)
            }
            
            // Draw self last (on top)
            avatarMarkers[UUID(0, 0)]?.let {
                drawMarker(canvas, it, markerPaint, textPaint)
            }
            
            _minimapBitmap.value = bitmap
        }
    }
    
    private fun drawMarker(canvas: Canvas, marker: MinimapMarker, paint: Paint, textPaint: Paint) {
        val x = (marker.x / REGION_SIZE) * MINIMAP_SIZE
        val y = MINIMAP_SIZE - (marker.y / REGION_SIZE) * MINIMAP_SIZE // Flip Y
        
        paint.color = when (marker.type) {
            MARKER_SELF -> COLOR_SELF
            MARKER_FRIEND -> COLOR_FRIEND
            MARKER_OTHER -> COLOR_OTHER
            MARKER_OBJECT -> COLOR_OBJECT
            else -> Color.WHITE
        }
        
        val size = if (marker.type == MARKER_SELF) 8f else 6f
        
        // Draw marker
        if (marker.type == MARKER_SELF) {
            // Draw arrow for self
            val path = Path().apply {
                moveTo(x, y - size)
                lineTo(x - size / 2, y + size / 2)
                lineTo(x + size / 2, y + size / 2)
                close()
            }
            
            // Rotate arrow based on camera
            val matrix = Matrix()
            matrix.setRotate(cameraRotation, x, y)
            path.transform(matrix)
            
            canvas.drawPath(path, paint)
        } else {
            // Draw circle for others
            canvas.drawCircle(x, y, size / 2, paint)
        }
        
        // Draw name (only for non-self with space)
        if (marker.type != MARKER_SELF && marker.name.isNotEmpty()) {
            canvas.drawText(marker.name.take(10), x, y - size, textPaint)
        }
    }
    
    /**
     * Get marker at position.
     */
    fun getMarkerAt(screenX: Float, screenY: Float, mapWidth: Float, mapHeight: Float): MinimapMarker? {
        val mapX = (screenX / mapWidth) * REGION_SIZE
        val mapY = REGION_SIZE - (screenY / mapHeight) * REGION_SIZE
        
        // Find closest marker within 5 meters
        return avatarMarkers.values.minByOrNull {
            val dx = it.x - mapX
            val dy = it.y - mapY
            dx * dx + dy * dy
        }?.takeIf {
            val dx = it.x - mapX
            val dy = it.y - mapY
            dx * dx + dy * dy < 25 // Within 5 meters
        }
    }
    
    // ==================== PARCEL OVERLAY ====================
    
    // Parcel overlay for minimap rendering
    private var parcelOverlayData: ByteArray? = null
    
    /**
     * Handle parcel overlay data for minimap display
     */
    fun handleParcelOverlay(sequenceId: Int, data: ByteArray) {
        // Initialize overlay if needed
        if (parcelOverlayData == null) {
            parcelOverlayData = ByteArray(2048)
        }
        
        // Copy overlay segment
        val stripSize = 512
        val offset = sequenceId * stripSize
        
        parcelOverlayData?.let { overlay ->
            if (offset >= 0 && offset + data.size <= overlay.size) {
                System.arraycopy(data, 0, overlay, offset, minOf(data.size, overlay.size - offset))
                Log.d(TAG, "Minimap parcel overlay updated: sequence=$sequenceId")
                
                // Parcel overlay will be used in rendering when getParcelOverlay() is called
            }
        }
    }
    
    /**
     * Get parcel overlay for rendering
     */
    fun getParcelOverlay(): ByteArray? = parcelOverlayData?.copyOf()
    
    fun shutdown() {
        scope.cancel()
        terrainBitmap?.recycle()
        _minimapBitmap.value?.recycle()
    }
}

/**
 * Minimap marker data.
 */
data class MinimapMarker(
    val id: UUID,
    val x: Float,
    val y: Float,
    val z: Float,
    val type: Int,
    val name: String
)
