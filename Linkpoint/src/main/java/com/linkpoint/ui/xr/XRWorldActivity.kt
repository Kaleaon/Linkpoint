package com.linkpoint.ui.xr

import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.xr.XRManager
import com.linkpoint.xr.ControllerState
import com.linkpoint.protocol.types.LLVector3
import com.linkpoint.protocol.types.LLQuaternion

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
    
    // Track trigger state to prevent rapid-fire interactions
    private val triggerState = mutableMapOf<ControllerState.Hand, Boolean>()

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
            app.renderManager.initializeOnRenderThread(surfaceView)
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
        app.renderManager.dispatcher.post(object : Runnable {
            override fun run() {
                if (isRendering) {
                    renderXRFrame()
                    app.renderManager.dispatcher.postDelayed(this, 11) // ~90fps for VR
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
        
        // Ensure we have access to managers
        if (!app.isAvatarManagerInitialized() || !app.isObjectManagerInitialized()) return

        val myAvatar = app.avatarManager.getMyAvatar() ?: return

        for (controller in controllers) {
            val (thumbX, thumbY) = controller.thumbstick[0] to controller.thumbstick[1]
            
            // Map controls based on hand
            when (controller.hand) {
                com.linkpoint.xr.ControllerState.Hand.LEFT -> {
                    // Left stick moves character (WASD equivalent)
                    app.avatarManager.movementController.setJoystickInput(thumbX, thumbY)
                }
                com.linkpoint.xr.ControllerState.Hand.RIGHT -> {
                    // Right stick turns character (Yaw)
                    app.avatarManager.movementController.setRotationInput(thumbX)

                    // Handle trigger for interaction (typically right hand dominant)
                    val isTriggerPressed = controller.triggerValue > 0.5f
                    val wasTriggerPressed = triggerState[controller.hand] ?: false

                    if (isTriggerPressed && !wasTriggerPressed) {
                        // Trigger just pressed - Perform raycast interaction
                        performInteraction(controller, myAvatar)
                    }

                    // Update state
                    triggerState[controller.hand] = isTriggerPressed
                }
            }
        }
    }
    
    private fun performInteraction(controller: ControllerState, avatar: com.linkpoint.avatar.Avatar) {
        // 1. Get controller pose in local tracking space
        // Controller orientation (quaternion x,y,z,w)
        val ctrlRot = LLQuaternion(
            controller.orientation[0],
            controller.orientation[1],
            controller.orientation[2],
            controller.orientation[3]
        )

        // Controller position (x,y,z)
        val ctrlPos = LLVector3(
            controller.position[0],
            controller.position[1],
            controller.position[2]
        )

        // 2. Convert to World Space
        // Assuming controller pose is relative to the avatar/tracking origin
        // WorldRot = AvatarRot * ControllerRot
        val worldRot = avatar.rotation * ctrlRot

        // WorldPos = AvatarPos + (AvatarRot * ControllerPos)
        // Note: This assumes tracking origin aligns with Avatar position (feet/root)
        val worldPos = avatar.position + (avatar.rotation.rotate(ctrlPos))

        // 3. Calculate Ray Direction
        // OpenXR/AndroidXR controllers typically point down -Z
        val forwardLocal = LLVector3(0f, 0f, -1f)
        val rayDirection = worldRot.rotate(forwardLocal)

        // 4. Perform Raycast
        val hit = app.objectManager.raycast(worldPos, rayDirection, maxDistance = 10f)

        if (hit != null) {
            Log.d(TAG, "Interaction ray hit object: localId=${hit.localId}, dist=${hit.distance}")

            // Calculate approximate normal/binormal for the touch
            // Ideally we'd get surface normal from raycast, but for now we approximate
            // Normal points back along the ray (towards user)
            val normal = rayDirection * -1f
            val binormal = LLVector3.unitZ().cross(normal).normalize()

            // Send object touch/grab
            app.objectManager.touchObject(hit.localId, hit.hitPosition, normal, binormal)

            // Visual feedback could be added here (e.g., haptic pulse or highlight)
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
