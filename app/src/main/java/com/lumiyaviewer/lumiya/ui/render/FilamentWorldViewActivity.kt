package com.lumiyaviewer.lumiya.ui.render

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.render.filament.FilamentSurfaceView
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager

/**
 * FilamentWorldViewActivity - Filament-based world rendering activity
 * 
 * This is a modernized version of WorldViewActivity that uses Filament for rendering
 * instead of the legacy OpenGL ES renderer.
 */
class FilamentWorldViewActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "FilamentWorldView"
        private const val CAMERA_MOVE_SPEED = 5.0f
        private const val CAMERA_ROTATE_SPEED = 1.0f
        private const val MIN_ZOOM = 1.0f
        private const val MAX_ZOOM = 100.0f
    }

    // UI Components
    private lateinit var surfaceContainer: FrameLayout
    private lateinit var loadingOverlay: FrameLayout
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loadingText: TextView
    private lateinit var controlsLayout: View
    
    // Filament rendering
    private lateinit var filamentSurfaceView: FilamentSurfaceView
    
    // Optional connections (will be null for standalone testing)
    private var userManager: UserManager? = null
    private var agentCircuit: SLAgentCircuit? = null
    
    private val mainHandler = Handler(Looper.getMainLooper())
    private var gestureDetector: GestureDetector? = null
    private var scaleGestureDetector: ScaleGestureDetector? = null
    
    // Camera state
    private val cameraPosition = LLVector3(0f, -20f, 10f)
    private var cameraRotationX = 0f
    private var cameraRotationY = 0f
    private var cameraZoom = 10f
    
    // Movement state
    private var isMovingForward = false
    private var isMovingBackward = false
    private var isTurningLeft = false
    private var isTurningRight = false
    private var isFlying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            // Use a simple layout for testing
            setContentView(R.layout.activity_filament_world_view)
            
            // Initialize UI
            initializeUI()
            
            // Setup Filament rendering
            setupFilamentRendering()
            
            // Setup gesture controls
            setupGestureDetectors()
            setupControls()
            
            showLoading("Initializing Filament renderer...")
            
            // Hide loading after a short delay (once Filament is ready)
            mainHandler.postDelayed({
                hideLoading()
            }, 500)
            
            Log.i(TAG, "FilamentWorldViewActivity created successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create activity", e)
            finish()
        }
    }
    
    private fun initializeUI() {
        // Try to bind views, but don't fail if layout doesn't have them
        try {
            surfaceContainer = findViewById(R.id.surfaceContainer)
        } catch (e: Exception) {
            Log.w(TAG, "surfaceContainer not found in layout")
        }
        
        try {
            loadingOverlay = findViewById(R.id.loadingOverlay)
            loadingProgressBar = findViewById(R.id.loadingProgressBar)
            loadingText = findViewById(R.id.loadingText)
        } catch (e: Exception) {
            Log.w(TAG, "Loading overlay not found in layout")
        }
        
        try {
            controlsLayout = findViewById(R.id.controlsLayout)
        } catch (e: Exception) {
            Log.w(TAG, "Controls layout not found in layout")
        }
    }

    private fun setupFilamentRendering() {
        // Create Filament surface view
        filamentSurfaceView = FilamentSurfaceView(this)
        
        // Add to container if available, otherwise set as content view
        if (::surfaceContainer.isInitialized) {
            surfaceContainer.addView(filamentSurfaceView)
        }
        
        // Initialize world renderer
        filamentSurfaceView.initializeWorldRenderer()
        
        // Set initial camera position
        updateCameraPosition()
        
        Log.i(TAG, "Filament rendering setup complete")
    }

    private fun setupGestureDetectors() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                cameraRotationY += distanceX * CAMERA_ROTATE_SPEED * 0.1f
                cameraRotationX += distanceY * CAMERA_ROTATE_SPEED * 0.1f
                cameraRotationX = cameraRotationX.coerceIn(-89f, 89f)
                updateCameraPosition()
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Handle double tap (e.g., teleport to location)
                Log.i(TAG, "Double tap detected")
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                cameraZoom /= detector.scaleFactor
                cameraZoom = cameraZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
                updateCameraPosition()
                return true
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControls() {
        filamentSurfaceView.setOnTouchListener { _, event ->
            gestureDetector?.onTouchEvent(event)
            scaleGestureDetector?.onTouchEvent(event)
            true
        }
    }

    private fun updateCameraPosition() {
        // Update camera in world renderer
        filamentSurfaceView.getWorldRenderer()?.setCameraPosition(
            cameraPosition,
            cameraRotationX,
            cameraRotationY
        )
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            if (::loadingText.isInitialized) {
                loadingText.text = message
            }
            if (::loadingOverlay.isInitialized) {
                loadingOverlay.visibility = View.VISIBLE
            }
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            if (::loadingOverlay.isInitialized) {
                loadingOverlay.visibility = View.GONE
            }
        }
    }

    fun setUserManager(manager: UserManager) {
        this.userManager = manager
    }

    fun setAgentCircuit(circuit: SLAgentCircuit) {
        this.agentCircuit = circuit
    }

    override fun onResume() {
        super.onResume()
        filamentSurfaceView.onResume()
        Log.i(TAG, "Activity resumed")
    }

    override fun onPause() {
        filamentSurfaceView.onPause()
        super.onPause()
        Log.i(TAG, "Activity paused")
    }

    override fun onDestroy() {
        filamentSurfaceView.destroy()
        super.onDestroy()
        Log.i(TAG, "Activity destroyed")
    }
}
