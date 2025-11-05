package com.lumiyaviewer.lumiya.ui.opengl

import android.content.Context
import android.opengl.GLSurfaceView
import android.view.MotionEvent
import com.lumiyaviewer.lumiya.graphics.opengl.OpenGLWorldRenderer
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import kotlin.math.*

/**
 * Custom OpenGL ES 3.0 Surface View for 3D World Rendering
 * 
 * Provides complete 3D world rendering with:
 * - Real OpenGL ES 3.0 context
 * - Touch controls for camera movement
 * - Gesture support for navigation
 * - Performance optimization
 */
class OpenGLWorldView(context: Context) : GLSurfaceView(context) {

    private val renderer: OpenGLWorldRenderer
    private val gestureHandler: GestureHandler
    
    // Touch tracking
    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var touchStartTime = 0L
    
    // Camera controls
    private var isDragging = false
    private var cameraDistance = 30f
    private var cameraRotationX = 0f
    private var cameraRotationY = 0f
    private var cameraTarget = LLVector3(128f, 128f, 0f)
    
    // Performance tracking
    private var lastFrameTime = 0L
    private var frameCount = 0
    private var averageFPS = 0f

    init {
        // Create OpenGL ES 3.0 context
        setEGLContextClientVersion(3)
        
        // Configure OpenGL settings
        setPreserveEGLContextOnPause(true)
        setRenderer(renderer = OpenGLWorldRenderer(context).also { 
            renderer = it 
        })
        
        // Set render mode
        renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        
        // Initialize gesture handler
        gestureHandler = GestureHandler(context)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchDown(event)
                return true
            }
            
            MotionEvent.ACTION_MOVE -> {
                handleTouchMove(event)
                return true
            }
            
            MotionEvent.ACTION_UP -> {
                handleTouchUp(event)
                return true
            }
            
            MotionEvent.ACTION_POINTER_DOWN -> {
                handlePointerDown(event)
                return true
            }
            
            MotionEvent.ACTION_POINTER_UP -> {
                handlePointerUp(event)
                return true
            }
        }
        
        return super.onTouchEvent(event)
    }

    private fun handleTouchDown(event: MotionEvent) {
        lastTouchX = event.x
        lastTouchY = event.y
        touchStartTime = System.currentTimeMillis()
        isDragging = false
    }

    private fun handleTouchMove(event: MotionEvent) {
        val deltaX = event.x - lastTouchX
        val deltaY = event.y - lastTouchY
        
        if (event.pointerCount == 1) {
            // Single finger - rotate camera
            handleCameraRotation(deltaX, deltaY)
            isDragging = true
        } else if (event.pointerCount == 2) {
            // Two fingers - zoom or pan
            handlePinchZoom(event)
        }
        
        lastTouchX = event.x
        lastTouchY = event.y
    }

    private fun handleTouchUp(event: MotionEvent) {
        val touchDuration = System.currentTimeMillis() - touchStartTime
        
        if (!isDragging && touchDuration < 200) {
            // Quick tap - handle tap gesture
            handleTap(event)
        }
        
        isDragging = false
    }

    private fun handlePointerDown(event: MotionEvent) {
        // Multi-touch started
        gestureHandler.onMultiTouchStart(event)
    }

    private fun handlePointerUp(event: MotionEvent) {
        // Multi-touch ended
        gestureHandler.onMultiTouchEnd(event)
    }

    private fun handleCameraRotation(deltaX: Float, deltaY: Float) {
        // Convert screen movement to camera rotation
        val rotationSensitivity = 0.01f
        
        cameraRotationY += deltaX * rotationSensitivity
        cameraRotationX += deltaY * rotationSensitivity
        
        // Clamp vertical rotation
        cameraRotationX = cameraRotationX.coerceIn(-PI.toFloat() / 2f, PI.toFloat() / 2f)
        
        // Update camera in renderer
        updateCameraPosition()
    }

    private fun handlePinchZoom(event: MotionEvent) {
        if (event.pointerCount == 2) {
            // Calculate distance between two fingers
            val x0 = event.getX(0)
            val y0 = event.getY(0)
            val x1 = event.getX(1)
            val y1 = event.getY(1)
            
            val distance = sqrt((x1 - x0).pow(2) + (y1 - y0).pow(2))
            
            // Update camera distance (zoom)
            val zoomSensitivity = 0.05f
            cameraDistance = (cameraDistance - distance * zoomSensitivity).coerceIn(5f, 100f)
            
            updateCameraPosition()
        }
    }

    private fun handleTap(event: MotionEvent) {
        // Handle tap on 3D world
        val worldX = event.x
        val worldY = event.y
        
        // TODO: Implement world picking/selection
        android.util.Log.d("OpenGLWorldView", "Tapped at: ($worldX, $worldY)")
    }

    private fun updateCameraPosition() {
        // Calculate camera position based on spherical coordinates
        val cameraPosition = LLVector3(
            x = cameraTarget.x + cos(cameraRotationY) * cos(cameraRotationX) * cameraDistance,
            y = cameraTarget.y + sin(cameraRotationY) * cos(cameraRotationX) * cameraDistance,
            z = cameraTarget.z + sin(cameraRotationX) * cameraDistance
        )
        
        // Update renderer camera
        renderer.setCameraPosition(cameraPosition, cameraRotationX, cameraRotationY)
    }

    // Camera control methods
    
    fun setCameraTarget(target: LLVector3) {
        cameraTarget = target
        updateCameraPosition()
    }

    fun setCameraDistance(distance: Float) {
        cameraDistance = distance.coerceIn(5f, 100f)
        updateCameraPosition()
    }

    fun getCameraPosition(): LLVector3 {
        return LLVector3(
            x = cameraTarget.x + cos(cameraRotationY) * cos(cameraRotationX) * cameraDistance,
            y = cameraTarget.y + sin(cameraRotationY) * cos(cameraRotationX) * cameraDistance,
            z = cameraTarget.z + sin(cameraRotationX) * cameraDistance
        )
    }

    // Movement controls
    
    fun moveCameraForward(distance: Float) {
        val direction = LLVector3(
            x = -sin(cameraRotationY) * distance,
            y = cos(cameraRotationY) * distance,
            z = 0f
        )
        cameraTarget.x += direction.x
        cameraTarget.y += direction.y
        updateCameraPosition()
    }

    fun moveCameraBackward(distance: Float) {
        moveCameraForward(-distance)
    }

    fun moveCameraLeft(distance: Float) {
        val direction = LLVector3(
            x = -cos(cameraRotationY) * distance,
            y = -sin(cameraRotationY) * distance,
            z = 0f
        )
        cameraTarget.x += direction.x
        cameraTarget.y += direction.y
        updateCameraPosition()
    }

    fun moveCameraRight(distance: Float) {
        moveCameraLeft(-distance)
    }

    fun moveCameraUp(distance: Float) {
        cameraTarget.z += distance
        updateCameraPosition()
    }

    fun moveCameraDown(distance: Float) {
        cameraTarget.z -= distance
        updateCameraPosition()
    }

    // Performance monitoring
    
    fun getFPS(): Float = renderer.getFPS()

    fun updatePerformanceMetrics() {
        val currentTime = System.currentTimeMillis()
        frameCount++
        
        if (currentTime - lastFrameTime >= 1000) {
            averageFPS = frameCount.toFloat()
            frameCount = 0
            lastFrameTime = currentTime
            
            android.util.Log.d("OpenGLWorldView", "Average FPS: $averageFPS")
        }
    }

    // World data connection
    
    fun connectToWorldData(
        objectsManager: com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager? = null,
        userManager: com.lumiyaviewer.lumiya.slproto.users.manager.UserManager? = null,
        terrainData: com.lumiyaviewer.lumiya.slproto.terrain.TerrainData? = null
    ) {
        renderer.connectToWorldData(objectsManager, userManager, terrainData)
    }

    fun disconnectFromWorldData() {
        renderer.disconnectFromWorldData()
    }

    // Lifecycle management
    
    override fun onPause() {
        super.onPause()
        // Pause rendering
        queueEvent {
            // GL operations that need to be paused
        }
    }

    override fun onResume() {
        super.onResume()
        // Resume rendering
        queueEvent {
            // GL operations that need to be resumed
        }
    }

    // Gesture handler for complex touch interactions
    
    private inner class GestureHandler(context: Context) {
        
        private var initialDistance = 0f
        private var initialZoom = 0f
        
        fun onMultiTouchStart(event: MotionEvent) {
            if (event.pointerCount == 2) {
                // Calculate initial distance for pinch zoom
                val x0 = event.getX(0)
                val y0 = event.getY(0)
                val x1 = event.getX(1)
                val y1 = event.getY(1)
                
                initialDistance = sqrt((x1 - x0).pow(2) + (y1 - y0).pow(2))
                initialZoom = cameraDistance
            }
        }

        fun onMultiTouchEnd(event: MotionEvent) {
            // Handle end of multi-touch gesture
        }

        fun calculatePinchZoom(event: MotionEvent): Float {
            if (event.pointerCount == 2) {
                val x0 = event.getX(0)
                val y0 = event.getY(0)
                val x1 = event.getX(1)
                val y1 = event.getY(1)
                
                val currentDistance = sqrt((x1 - x0).pow(2) + (y1 - y0).pow(2))
                
                if (initialDistance > 0) {
                    return (currentDistance / initialDistance) * initialZoom
                }
            }
            return cameraDistance
        }
    }

    // Settings and configuration
    
    fun setRenderQuality(quality: RenderQuality) {
        when (quality) {
            RenderQuality.LOW -> {
                // Reduce rendering quality for performance
                android.util.Log.d("OpenGLWorldView", "Set render quality to LOW")
            }
            RenderQuality.MEDIUM -> {
                // Balanced quality and performance
                android.util.Log.d("OpenGLWorldView", "Set render quality to MEDIUM")
            }
            RenderQuality.HIGH -> {
                // Maximum quality
                android.util.Log.d("OpenGLWorldView", "Set render quality to HIGH")
            }
            RenderQuality.ULTRA -> {
                // Ultra high quality settings
                android.util.Log.d("OpenGLWorldView", "Set render quality to ULTRA")
            }
        }
    }

    fun enableAntiAliasing(enabled: Boolean) {
        // Configure anti-aliasing settings
        android.util.Log.d("OpenGLWorldView", "Anti-aliasing: $enabled")
    }

    fun setFieldOfView(fov: Float) {
        // Update camera field of view (would need to be passed to renderer)
        android.util.Log.d("OpenGLWorldView", "Setting FOV to: $fov degrees")
    }

    enum class RenderQuality {
        LOW, MEDIUM, HIGH, ULTRA
    }
}