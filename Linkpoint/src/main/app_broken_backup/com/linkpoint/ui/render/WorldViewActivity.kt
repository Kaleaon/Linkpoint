package com.linkpoint.ui.render

import kotlin.math.*

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import com.linkpoint.R
import com.linkpoint.render.WorldViewRenderer
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.types.LLVector3
import com.linkpoint.slproto.users.manager.UserManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * Converted from Java to Kotlin
 * WorldViewActivity - Main 3D world rendering activity
 */
class WorldViewActivity : AppCompatActivity(), GLSurfaceView.Renderer {

    companion object {
        private const val TAG = "WorldViewActivity"
        private const val CAMERA_MOVE_SPEED = 5.0f
        private const val CAMERA_ROTATE_SPEED = 1.0f
        private const val MIN_ZOOM = 1.0f
        private const val MAX_ZOOM = 100.0f
    }

    @BindView(R.id.glSurfaceView) lateinit var glSurfaceView: GLSurfaceView
    @BindView(R.id.loadingOverlay) lateinit var loadingOverlay: FrameLayout
    @BindView(R.id.loadingProgressBar) lateinit var loadingProgressBar: ProgressBar
    @BindView(R.id.loadingText) lateinit var loadingText: TextView
    @BindView(R.id.controlsLayout) lateinit var controlsLayout: View
    @BindView(R.id.buttonMoveForward) lateinit var buttonMoveForward: ImageButton
    @BindView(R.id.buttonMoveBackward) lateinit var buttonMoveBackward: ImageButton
    @BindView(R.id.buttonTurnLeft) lateinit var buttonTurnLeft: ImageButton
    @BindView(R.id.buttonTurnRight) lateinit var buttonTurnRight: ImageButton
    @BindView(R.id.buttonFly) lateinit var buttonFly: ImageButton
    @BindView(R.id.buttonSettings) lateinit var buttonSettings: ImageButton

    private var worldViewRenderer: WorldViewRenderer? = null
    private var userManager: UserManager? = null
    private var agentCircuit: SLAgentCircuit? = null
    
    private val mainHandler = Handler()
    private var gestureDetector: GestureDetector? = null
    private var scaleGestureDetector: ScaleGestureDetector? = null
    
    private val cameraPosition = LLVector3(128f, 128f, 25f)
    private var cameraRotationX = 0f
    private var cameraRotationY = 0f
    private var cameraZoom = 10f
    
    private var isMovingForward = false
    private var isMovingBackward = false
    private var isTurningLeft = false
    private var isTurningRight = false
    private var isFlying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_view)
        ButterKnife.bind(this)

        setupGLSurfaceView()
        setupGestureDetectors()
        setupControls()
        
        showLoading("Connecting to world...")
    }

    private fun setupGLSurfaceView() {
        glSurfaceView.setEGLContextClientVersion(2)
        glSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        glSurfaceView.setRenderer(this)
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
    }

    private fun setupGestureDetectors() {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                cameraRotationY += distanceX * CAMERA_ROTATE_SPEED * 0.1f
                cameraRotationX += distanceY * CAMERA_ROTATE_SPEED * 0.1f
                cameraRotationX = cameraRotationX.coerceIn(-89f, 89f)
                return true
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                // Handle double tap (e.g., teleport to location)
                return true
            }
        })

        scaleGestureDetector = ScaleGestureDetector(this, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                cameraZoom /= detector.scaleFactor
                cameraZoom = cameraZoom.coerceIn(MIN_ZOOM, MAX_ZOOM)
                return true
            }
        })
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupControls() {
        glSurfaceView.setOnTouchListener { _, event ->
            gestureDetector?.onTouchEvent(event)
            scaleGestureDetector?.onTouchEvent(event)
            true
        }
    }

    @OnClick(R.id.buttonMoveForward)
    fun onMoveForwardClick() {
        isMovingForward = true
    }

    @OnClick(R.id.buttonMoveBackward)
    fun onMoveBackwardClick() {
        isMovingBackward = true
    }

    @OnClick(R.id.buttonTurnLeft)
    fun onTurnLeftClick() {
        isTurningLeft = true
    }

    @OnClick(R.id.buttonTurnRight)
    fun onTurnRightClick() {
        isTurningRight = true
    }

    @OnClick(R.id.buttonFly)
    fun onFlyClick() {
        isFlying = !isFlying
        buttonFly.isSelected = isFlying
        // Send fly state to server
    }

    @OnClick(R.id.buttonSettings)
    fun onSettingsClick() {
        // Open settings
    }

    // GLSurfaceView.Renderer implementation
    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        worldViewRenderer = WorldViewRenderer(this, userManager, agentCircuit)
        worldViewRenderer?.onSurfaceCreated(gl, config)
        
        mainHandler.post {
            hideLoading()
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        worldViewRenderer?.onSurfaceChanged(gl, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        updateCameraPosition()
        worldViewRenderer?.onDrawFrame(gl)
    }

    private fun updateCameraPosition() {
        val moveSpeed = CAMERA_MOVE_SPEED * 0.016f // Assuming 60fps
        
        if (isMovingForward) {
            val radians = Math.toRadians(cameraRotationY.toDouble())
            cameraPosition.x += (sin(radians) * moveSpeed).toFloat()
            cameraPosition.y += (cos(radians) * moveSpeed).toFloat()
        }
        
        if (isMovingBackward) {
            val radians = Math.toRadians(cameraRotationY.toDouble())
            cameraPosition.x -= (sin(radians) * moveSpeed).toFloat()
            cameraPosition.y -= (cos(radians) * moveSpeed).toFloat()
        }
        
        if (isTurningLeft) {
            cameraRotationY -= CAMERA_ROTATE_SPEED
        }
        
        if (isTurningRight) {
            cameraRotationY += CAMERA_ROTATE_SPEED
        }
        
        if (isFlying) {
            cameraPosition.z += moveSpeed
        }
        
        worldViewRenderer?.setCameraPosition(cameraPosition, cameraRotationX, cameraRotationY, cameraZoom)
    }

    private fun showLoading(message: String) {
        runOnUiThread {
            loadingText.text = message
            loadingOverlay.visibility = View.VISIBLE
        }
    }

    private fun hideLoading() {
        runOnUiThread {
            loadingOverlay.visibility = View.GONE
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
        glSurfaceView.onResume()
    }

    override fun onPause() {
        glSurfaceView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        worldViewRenderer?.cleanup()
        super.onDestroy()
    }
}
