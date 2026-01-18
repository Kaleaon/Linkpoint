package com.linkpoint.ui.xr

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.xr.XRManager

/**
 * XR World Activity - Immersive VR/AR mode
 * Based on Lumiya's CardboardActivity but modernized for Android XR
 */
class XRWorldActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "XRWorldActivity"
    }
    
    private lateinit var xrContainer: FrameLayout
    private lateinit var surfaceView: SurfaceView
    
    private val app by lazy { LinkpointApp.getInstance() }
    private var isRendering = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Immersive mode
        setupImmersiveMode()
        
        setContentView(R.layout.activity_xr_world)
        
        initViews()
        initXR()
        setupBackPressHandler()
    }
    
    private fun setupImmersiveMode() {
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Fullscreen immersive
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        )
    }
    
    private fun initViews() {
        xrContainer = findViewById(R.id.xrContainer)
        
        surfaceView = SurfaceView(this)
        xrContainer.addView(surfaceView)
    }
    
    private fun initXR() {
        // Initialize XR session
        val xrManager = app.xrManager
        val mode = xrManager.getBestMode()
        
        if (xrManager.initSession(mode)) {
            // Initialize renderer for XR
            app.renderManager.initialize(surfaceView)
            isRendering = true
            startXRRenderLoop()
        } else {
            // Fallback to non-XR mode
            android.widget.Toast.makeText(
                this,
                "XR mode not available, using standard view",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
    
    private fun startXRRenderLoop() {
        surfaceView.post(object : Runnable {
            override fun run() {
                if (isRendering) {
                    renderXRFrame()
                    surfaceView.postDelayed(this, 11) // ~90fps for VR
                }
            }
        })
    }
    
    private fun renderXRFrame() {
        val xrManager = app.xrManager
        
        // Get XR frame data
        val frameData = xrManager.beginFrame()
        
        if (frameData != null) {
            // Render in stereo
            app.renderManager.renderXRFrame(frameData)
        } else {
            // Fallback to mono rendering
            app.renderManager.renderFrame()
        }
        
        xrManager.endFrame()
        
        // Handle controller input
        handleControllerInput()
    }
    
    private fun handleControllerInput() {
        val controllers = app.xrManager.getControllers()
        
        for (controller in controllers) {
            // Handle movement with thumbstick
            val (thumbX, thumbY) = controller.thumbstick[0] to controller.thumbstick[1]
            
            if (kotlin.math.abs(thumbX) > 0.1f || kotlin.math.abs(thumbY) > 0.1f) {
                // Move avatar based on thumbstick
                // TODO: Implement movement
            }
            
            // Handle trigger for interaction
            if (controller.triggerValue > 0.5f) {
                // TODO: Handle selection/interaction
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        isRendering = false
    }
    
    override fun onResume() {
        super.onResume()
        setupImmersiveMode()
        isRendering = true
        startXRRenderLoop()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRendering = false
        app.xrManager.shutdown()
    }
    
    /**
     * Setup back press handler using modern OnBackPressedCallback
     */
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Exit XR mode
                finish()
            }
        })
    }
}
