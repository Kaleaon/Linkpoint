package com.linkpoint.ui.render

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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.R
import com.linkpoint.graphics.filament.FilamentSurfaceView
import com.linkpoint.slproto.types.LLVector3

/**
 * FilamentWorldViewActivity - Filament-based world rendering activity for Linkpoint
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
    
    // Filament rendering
    private lateinit var filamentSurfaceView: FilamentSurfaceView
    
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
            // Create simple layout programmatically
            val rootLayout = FrameLayout(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            
            // Create Filament surface view
            filamentSurfaceView = FilamentSurfaceView(this)
            rootLayout.addView(filamentSurfaceView)
            
            // Add loading overlay
            loadingOverlay = FrameLayout(this).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                setBackgroundColor(0x80000000.toInt())
                visibility = View.VISIBLE
            }
            
            loadingText = TextView(this).apply {
                text = "Initializing Filament..."
                textSize = 18f
                setTextColor(0xFFFFFFFF.toInt())
            }
            loadingOverlay.addView(loadingText, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                android.view.Gravity.CENTER
            ))
            rootLayout.addView(loadingOverlay)
            
            setContentView(rootLayout)
            
            // Setup Filament rendering
            setupFilamentRendering()
            
            // Setup gesture controls
            setupGestureDetectors()
            setupControls()
            
            // Hide loading after a short delay
            mainHandler.postDelayed({
                hideLoading()
            }, 500)
            
            Log.i(TAG, "Linkpoint FilamentWorldViewActivity created successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create activity", e)
            finish()
        }
    }

    private fun setupFilamentRendering() {
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

    private fun hideLoading() {
        runOnUiThread {
            loadingOverlay.visibility = View.GONE
        }
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
