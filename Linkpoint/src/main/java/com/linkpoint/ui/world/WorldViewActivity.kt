package com.linkpoint.ui.world

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.GestureDetector
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.linkpoint.render.CameraController
import com.linkpoint.render.backend.FilamentBackend
import com.linkpoint.render.backend.OpenGLES3Backend
import com.linkpoint.render.backend.RenderBackend
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import androidx.compose.ui.platform.ComposeView
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.network.NetworkLogger
import com.linkpoint.core.ConnectionState
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.friends.FriendsActivity
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.minimap.MinimapActivity
import com.linkpoint.ui.avatar.MyAvatarActivity
import com.linkpoint.ui.people.NearbyPeopleActivity
import com.linkpoint.render.lumiya.core.LumiyaGLSurfaceView
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.render.RenderDiagnostics
import com.linkpoint.ui.xr.XRWorldActivity
import com.linkpoint.utils.DebugReportService
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Main world view activity - shows the 3D world
 * Based on the reference viewer's WorldViewActivity
 */
class WorldViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    companion object {
        private const val TAG = "WorldViewActivity"
        private const val EXTRA_LAYOUT_EDITOR_MODE = "com.linkpoint.EXTRA_LAYOUT_EDITOR_MODE"

        fun createLayoutEditorIntent(context: android.content.Context): Intent {
            return Intent(context, WorldViewActivity::class.java).apply {
                putExtra(EXTRA_LAYOUT_EDITOR_MODE, true)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
        }
    }
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var surfaceView: SurfaceView
    private var lumiyaSurfaceView: LumiyaGLSurfaceView? = null
    private var renderBackend: RenderBackend? = null
    private lateinit var worldViewportHost: WorldViewportHost
    private lateinit var worldOverlayCompose: ComposeView
    private lateinit var rendererHandoffManager: RendererHandoffManager
    private val worldUiState = MutableStateFlow(WorldUiState())
    
    // HUD elements
    private lateinit var regionNameText: TextView
    private lateinit var avatarNameText: TextView
    private lateinit var btnMenu: ImageButton
    private lateinit var btnChat: ImageButton
    private lateinit var btnMinimap: ImageButton
    private lateinit var btnInventory: ImageButton
    private lateinit var btnXR: ImageButton
    
    // HUD overlay for Second Life HUD attachments
    private lateinit var hudOverlay: com.linkpoint.hud.HUDOverlayView
    
    // Joysticks
    private lateinit var joystickMove: JoystickView
    private lateinit var joystickCamera: JoystickView
    private lateinit var movementButtonsGroup: View
    private lateinit var actionButtonsGroup: View
    
    // Movement control buttons
    private lateinit var btnFly: ImageButton
    private lateinit var btnRun: ImageButton
    private lateinit var btnJump: ImageButton
    private lateinit var btnSit: ImageButton
    
    // Action buttons
    private lateinit var btnGestures: ImageButton
    private lateinit var btnFriends: ImageButton
    private lateinit var btnNearby: ImageButton
    private lateinit var btnCameraMode: ImageButton

    // Camera gesture detectors. The world viewport host (the empty area between
    // the joysticks and action button rails) feeds touches here so drag
    // becomes orbit and pinch becomes zoom; both are consumed by the
    // CameraController in RenderManager.
    private var cameraGestureDetector: GestureDetector? = null
    private var cameraScaleDetector: ScaleGestureDetector? = null
    
    // Movement controller (lazy init after login)
    private val movementController by lazy {
        app.avatarManager.movementController
    }
    
    // Debug floater button
    private var debugFloaterButton: FloatingActionButton? = null
    
    private val app by lazy { LinkpointApp.getInstance() }
    @Volatile private var isRendering = false
    @Volatile private var isSurfaceReady = false
    // OpenGL ES 3 (Lumiya pipeline) is the primary path. Filament remains as
    // an opt-in fallback gated behind the renderer_backend preference.
    private var useSecondaryRenderer: Boolean = true
    private var hudsVisibleFromManager: Boolean = true
    private var isLayoutEditorMode: Boolean = false
    
    // Track current orientation setting to avoid unnecessary changes
    private var currentOrientationPref: String? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize currentOrientationPref from SharedPreferences before applying
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        currentOrientationPref = prefs.getString("screen_orientation", "portrait") ?: "portrait"
        applyScreenOrientation()
        setContentView(R.layout.activity_world_view)
        
        initViews()
        initRendererHandoffManager()
        initDebugFloater()
        initRenderer()
        setupNavigation()
        setupBackPressHandler()
        observeState()

        if (intent.getBooleanExtra(EXTRA_LAYOUT_EDITOR_MODE, false)) {
            enterLayoutEditorMode()
        }
    }
    
    /**
     * Apply screen orientation based on user preference.
     * Default is portrait to avoid black screen issues in landscape mode.
     * Only changes orientation if the preference has changed.
     */
    private fun applyScreenOrientation() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val orientation = prefs.getString("screen_orientation", "portrait") ?: "portrait"

        requestedOrientation = when (orientation) {
            "portrait" -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            "landscape" -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            "auto" -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
            else -> ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        currentOrientationPref = orientation
    }
    
    /**
     * Initialize the debug floater button based on user preference.
     * When tapped, captures a debug report of the current app state.
     */
    private fun initDebugFloater() {
        debugFloaterButton = findViewById(R.id.btnDebugFloater)
        
        debugFloaterButton?.setOnClickListener {
            captureDebugReport()
        }
        
        // Set initial visibility based on preference
        updateDebugFloaterVisibility()
    }
    
    /**
     * Update debug floater visibility based on settings
     */
    private fun updateDebugFloaterVisibility() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val showDebugFloater = prefs.getBoolean("enable_debug_floater", false)
        debugFloaterButton?.visibility = if (showDebugFloater) View.VISIBLE else View.GONE
    }
    
    /**
     * Capture a debug report when the floater button is tapped
     */
    private fun captureDebugReport() {
        Toast.makeText(this, "Capturing debug report...", Toast.LENGTH_SHORT).show()
        
        val debugService = DebugReportService.getInstance(this)
        debugService.captureDebugReportAsync("Captured via floater button in WorldView") { file ->
            if (file != null) {
                Toast.makeText(
                    this,
                    "Debug report saved: ${file.name}",
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    this,
                    "Failed to capture debug report",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        worldViewportHost = findViewById(R.id.worldViewportHost)
        worldOverlayCompose = findViewById(R.id.worldOverlayCompose)
        
        regionNameText = findViewById(R.id.textRegionName)
        avatarNameText = findViewById(R.id.textAvatarName)
        
        // Menu button - opens navigation drawer
        btnMenu = findViewById(R.id.btnMenu)
        btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        
        btnChat = findViewById(R.id.btnChat)
        btnMinimap = findViewById(R.id.btnMinimap)
        btnInventory = findViewById(R.id.btnInventory)
        btnXR = findViewById(R.id.btnXR)
        movementButtonsGroup = findViewById(R.id.movementButtons)
        actionButtonsGroup = findViewById(R.id.actionButtons)
        
        // Setup quick action buttons
        btnChat.setOnClickListener {
            startActivity(Intent(this, ChatActivity::class.java))
        }
        
        btnMinimap.setOnClickListener {
            startActivity(Intent(this, MinimapActivity::class.java))
        }
        
        btnInventory.setOnClickListener {
            startActivity(Intent(this, InventoryActivity::class.java))
        }
        
        btnXR.setOnClickListener {
            if (app.isXREntryAvailable()) {
                startActivity(Intent(this, XRWorldActivity::class.java))
            } else {
                Toast.makeText(this, "XR mode is unavailable in this build", Toast.LENGTH_SHORT).show()
            }
        }

        // Show/hide XR button based on runtime and build availability
        btnXR.visibility = if (app.isXREntryAvailable()) View.VISIBLE else View.GONE
        navigationView.menu.findItem(R.id.nav_xr_mode)?.isVisible = app.isXREntryAvailable()

        worldOverlayCompose.setContent {
            val state by worldUiState.collectAsStateWithLifecycle()
            WorldOverlay(
                state = state,
                movementJoystickFactory = {
                    joystickMove.apply {
                        (parent as? android.view.ViewGroup)?.removeView(this)
                    }
                },
                cameraJoystickFactory = {
                    joystickCamera.apply {
                        (parent as? android.view.ViewGroup)?.removeView(this)
                    }
                },
                onMenu = { drawerLayout.openDrawer(GravityCompat.START) },
                onChat = { startActivity(Intent(this, ChatActivity::class.java)) },
                onMinimap = { startActivity(Intent(this, MinimapActivity::class.java)) },
                onInventory = { startActivity(Intent(this, InventoryActivity::class.java)) },
                onXr = {
                    if (app.isXREntryAvailable()) {
                        startActivity(Intent(this, XRWorldActivity::class.java))
                    } else {
                        Toast.makeText(this, "XR mode is unavailable in this build", Toast.LENGTH_SHORT).show()
                    }
                },
                onGestures = { showGesturesPopup() },
                onFriends = { startActivity(Intent(this, FriendsActivity::class.java)) },
                onNearby = { startActivity(Intent(this, NearbyPeopleActivity::class.java)) },
                onCameraMode = {
                    val controller = app.renderManager.cameraController
                    controller.toggleMode()
                    updateCameraModeButton(controller.mode)
                },
                onFly = { btnFly.performClick() },
                onRun = { btnRun.performClick() },
                onJump = { btnJump.performClick() },
                onSit = { btnSit.performClick() }
            )
        }
        
        // Initialize HUD overlay for Second Life HUD attachments
        initHudOverlay()
        
        // Initialize joysticks
        initJoysticks()
        
        // Initialize movement control buttons
        initMovementControls()
        
        // Initialize action buttons
        initActionButtons()

        // Attach camera orbit/pinch detectors to the empty world area.
        attachCameraGestureDetectors()

        applyInterfacePreferences()
    }
    
    /**
     * Initialize the HUD overlay for displaying Second Life HUD attachments.
     */
    private fun initHudOverlay() {
        hudOverlay = worldViewportHost.hudOverlay
        
        // Connect to HUD manager if available
        if (app.isHudManagerInitialized()) {
            hudOverlay.hudManager = app.hudManager
            
            hudOverlay.listener = object : com.linkpoint.hud.HUDOverlayView.HUDInteractionListener {
                override fun onHUDTouched(hudLocalId: Int, touchPosition: com.linkpoint.protocol.types.LLVector3) {
                    app.hudManager.touchHUD(hudLocalId, touchPosition)
                }
                
                override fun onHUDLongPressed(hudLocalId: Int) {
                    // Show HUD options menu
                    showHUDOptionsMenu(hudLocalId)
                }
            }
            
            // Observe HUD visibility changes
            lifecycleScope.launch {
                app.hudManager.hudsVisible.collectLatest { visible ->
                    hudsVisibleFromManager = visible
                    val prefs = PreferenceManager.getDefaultSharedPreferences(this@WorldViewActivity)
                    val showHud = prefs.getBoolean("show_hud", true)
                    updateHudOverlayVisibility(showHud)
                }
            }
        }
    }

    private fun updateHudOverlayVisibility(showHud: Boolean) {
        val visible = showHud && hudsVisibleFromManager
        worldViewportHost.setHudAttachmentsVisible(visible)
        worldUiState.update { it.copy(overlaysVisibility = it.overlaysVisibility.copy(hudAttachments = visible)) }
    }

    private fun applyInterfacePreferences() {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val showHud = prefs.getBoolean("show_hud", true)
        val showJoysticks = prefs.getBoolean("show_joysticks", true)
        val showActionButtons = prefs.getBoolean("show_action_buttons", true)
        val showMovementButtons = prefs.getBoolean("show_movement_buttons", true)

        updateHudOverlayVisibility(showHud)
        joystickMove.visibility = if (showJoysticks) View.VISIBLE else View.GONE
        joystickCamera.visibility = if (showJoysticks) View.VISIBLE else View.GONE
        actionButtonsGroup.visibility = if (showActionButtons) View.VISIBLE else View.GONE
        movementButtonsGroup.visibility = if (showMovementButtons) View.VISIBLE else View.GONE
        worldUiState.update {
            it.copy(
                overlaysVisibility = it.overlaysVisibility.copy(
                    topStatusHud = showHud,
                    rightActionStack = showActionButtons,
                    bottomChatPreview = showHud,
                    movementJoystick = showJoysticks,
                    cameraJoystick = showJoysticks
                )
            )
        }

        applyCameraPreferences()
    }

    /**
     * Push the user's Camera prefs into RenderManager + CameraController.
     * Called on first show and on every onResume so changes made in Settings
     * take effect when the user comes back.
     */
    private fun applyCameraPreferences() {
        if (!app.isRenderManagerInitialized()) return
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val fov = prefs.getInt("camera_fov", 60).toFloat()
        val followDistance = prefs.getInt("camera_follow_distance", 5).toFloat()
        val drawDistance = prefs.getInt("draw_distance", 128).toFloat()
        // The seekbar is integer 1..20; multiply by 0.05 to get a usable
        // deg/pixel range (~0.05 .. ~1.0).
        val orbitSens = prefs.getInt("camera_orbit_sensitivity", 5) * 0.05f
        val invertY = prefs.getBoolean("camera_invert_y", false)

        app.renderManager.setFieldOfView(fov)
        app.renderManager.setFarClip(drawDistance)
        val controller = app.renderManager.cameraController
        controller.followDistance = followDistance.coerceIn(
            controller.minFollowDistance,
            controller.maxFollowDistance
        )
        controller.orbitSensitivity = orbitSens
        controller.invertY = invertY
    }

    private fun enterLayoutEditorMode() {
        if (isLayoutEditorMode) return
        isLayoutEditorMode = true
        Toast.makeText(
            this,
            "Layout editor enabled. Drag controls to reposition them.",
            Toast.LENGTH_LONG
        ).show()
    }
    
    /**
     * Show options menu for a HUD.
     */
    private fun showHUDOptionsMenu(hudLocalId: Int) {
        if (!app.isHudManagerInitialized()) return
        
        val hud = app.hudManager.getHUD(hudLocalId) ?: return
        
        val options = arrayOf(
            getString(R.string.hide_huds),
            getString(R.string.close)
        )
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(hud.name)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> app.hudManager.setHUDsVisible(false)
                }
            }
            .show()
    }
    
    /**
     * Initialize joystick controls for movement and camera.
     */
    private fun initJoysticks() {
        joystickMove = findViewById(R.id.joystickMove)
        joystickCamera = findViewById(R.id.joystickCamera)
        
        // Movement joystick listener
        joystickMove.listener = object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                // Forward the input to the movement controller
                if (app.isAvatarManagerInitialized()) {
                    movementController.setJoystickInput(x, y)
                }
            }
            
            override fun onJoystickReleased() {
                // Stop movement when joystick is released
                if (app.isAvatarManagerInitialized()) {
                    movementController.setJoystickInput(0f, 0f)
                }
            }
        }
        
        // Camera/rotation joystick listener
        joystickCamera.listener = object : JoystickView.JoystickListener {
            override fun onJoystickMoved(x: Float, y: Float) {
                // Forward the rotation input
                if (app.isAvatarManagerInitialized()) {
                    movementController.setRotationInput(x)
                    // Y could be used for camera pitch if needed
                }
            }
            
            override fun onJoystickReleased() {
                if (app.isAvatarManagerInitialized()) {
                    movementController.setRotationInput(0f)
                }
            }
        }
    }
    
    /**
     * Initialize movement control buttons (fly, run, jump, sit).
     */
    private fun initMovementControls() {
        btnFly = findViewById(R.id.btnFly)
        btnRun = findViewById(R.id.btnRun)
        btnJump = findViewById(R.id.btnJump)
        btnSit = findViewById(R.id.btnSit)
        
        btnFly.setOnClickListener {
            if (app.isAvatarManagerInitialized()) {
                movementController.toggleFly()
                val isFlying = movementController.isFlying.value
                Toast.makeText(
                    this,
                    if (isFlying) R.string.flying_enabled else R.string.flying_disabled,
                    Toast.LENGTH_SHORT
                ).show()
                updateFlyButtonState(isFlying)
            }
        }
        
        btnRun.setOnClickListener {
            if (app.isAvatarManagerInitialized()) {
                movementController.toggleRun()
                val isRunning = movementController.isRunning.value
                Toast.makeText(
                    this,
                    if (isRunning) R.string.running_enabled else R.string.running_disabled,
                    Toast.LENGTH_SHORT
                ).show()
                updateRunButtonState(isRunning)
            }
        }
        
        btnJump.setOnClickListener {
            if (app.isAvatarManagerInitialized()) {
                movementController.jump()
            }
        }
        
        btnSit.setOnClickListener {
            if (app.isAvatarManagerInitialized()) {
                if (movementController.isSitting.value) {
                    movementController.standUp()
                } else {
                    movementController.sitOnGround()
                }
            }
        }
        
        // Observe movement state changes
        lifecycleScope.launch {
            if (app.isAvatarManagerInitialized()) {
                movementController.isFlying.collectLatest { isFlying ->
                    updateFlyButtonState(isFlying)
                }
            }
        }
        
        lifecycleScope.launch {
            if (app.isAvatarManagerInitialized()) {
                movementController.isRunning.collectLatest { isRunning ->
                    updateRunButtonState(isRunning)
                }
            }
        }
        
        lifecycleScope.launch {
            if (app.isAvatarManagerInitialized()) {
                movementController.isSitting.collectLatest { isSitting ->
                    updateSitButtonState(isSitting)
                }
            }
        }
    }
    
    /**
     * Initialize action buttons (gestures, friends, nearby).
     */
    private fun initActionButtons() {
        btnGestures = findViewById(R.id.btnGestures)
        btnFriends = findViewById(R.id.btnFriends)
        btnNearby = findViewById(R.id.btnNearby)
        btnCameraMode = findViewById(R.id.btnCameraMode)

        btnGestures.setOnClickListener {
            showGesturesPopup()
        }

        btnFriends.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }

        btnNearby.setOnClickListener {
            startActivity(Intent(this, NearbyPeopleActivity::class.java))
        }

        btnCameraMode.setOnClickListener {
            val controller = app.renderManager.cameraController
            controller.toggleMode()
            updateCameraModeButton(controller.mode)
            val msg = when (controller.mode) {
                CameraController.Mode.FOLLOW -> R.string.camera_mode_follow
                CameraController.Mode.MOUSELOOK -> R.string.camera_mode_mouselook
            }
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        }
        updateCameraModeButton(app.renderManager.cameraController.mode)
    }

    private fun updateCameraModeButton(mode: CameraController.Mode) {
        btnCameraMode.alpha = when (mode) {
            CameraController.Mode.FOLLOW -> 0.6f
            CameraController.Mode.MOUSELOOK -> 1.0f
        }
        btnCameraMode.isSelected = (mode == CameraController.Mode.MOUSELOOK)
        worldUiState.update {
            it.copy(
                interactionMode = when (mode) {
                    CameraController.Mode.FOLLOW -> WorldUiState.InteractionMode.FOLLOW
                    CameraController.Mode.MOUSELOOK -> WorldUiState.InteractionMode.MOUSELOOK
                }
            )
        }
    }

    /**
     * Hook the world viewport host up to a GestureDetector + ScaleGestureDetector
     * so single-finger drag orbits the camera and pinch zooms it. Joysticks
     * already consume their own touches; the world viewport host only sees
     * events that fall outside the joystick / action-button rail areas, so
     * the two input systems can't conflict.
     */
    private fun attachCameraGestureDetectors() {
        val controller = app.renderManager.cameraController

        cameraGestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onScroll(
                e1: MotionEvent?,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                // GestureDetector's distance is "previous - current" so a
                // rightward drag is negative dx; pass through directly to the
                // controller which handles inversion.
                controller.applyOrbit(-distanceX, -distanceY)
                // Throttle background compute while the user is dragging
                // — drops particles + lowers terrain LOD until release.
                lumiyaSurfaceView?.getRenderer()?.beginInteractiveThrottle()
                return true
            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                handleWorldTap(e.x, e.y)
                return true
            }

            override fun onFling(
                e1: MotionEvent?, e2: MotionEvent,
                velocityX: Float, velocityY: Float
            ): Boolean {
                // Fling lasts ~200ms after release; keep responsive
                // throttling on for that window then release.
                lumiyaSurfaceView?.getRenderer()?.beginInteractiveThrottle()
                lumiyaSurfaceView?.postDelayed(
                    { lumiyaSurfaceView?.getRenderer()?.endInteractiveThrottle() },
                    250L
                )
                return false
            }
        })

        cameraScaleDetector = ScaleGestureDetector(this, object :
            ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                controller.applyZoom(detector.scaleFactor)
                return true
            }
        })

        // Single onTouchListener feeds both detectors; the viewport host
        // is otherwise non-interactive so swallowing every event is fine.
        worldViewportHost.setOnTouchListener { _, ev ->
            cameraScaleDetector?.onTouchEvent(ev)
            cameraGestureDetector?.onTouchEvent(ev)
            if (ev.actionMasked == MotionEvent.ACTION_UP ||
                ev.actionMasked == MotionEvent.ACTION_CANCEL
            ) {
                lumiyaSurfaceView?.getRenderer()?.endInteractiveThrottle()
            }
            true
        }
    }

    /**
     * Hand a world-space tap to the GL renderer's picking system.
     * Posted onto the GL thread because picking reads the live view +
     * projection matrices that drive the current frame.
     */
    private fun handleWorldTap(screenX: Float, screenY: Float) {
        val glView = lumiyaSurfaceView ?: return
        glView.runOnGlThread {
            val pickedId = glView.getRenderer().pickPrim(screenX, screenY)
            if (pickedId != null) {
                runOnUiThread { onPrimPicked(pickedId) }
            }
        }
    }

    /**
     * Called on the UI thread when the user taps a prim. Default
     * behaviour: surface the SL object's UUID via the existing
     * ObjectManager so downstream components (script dialogs, sit-on,
     * pay) can react. Logs the local id for now; full LL-viewer-style
     * "object popup" lives in `ObjectActionsDialog`.
     */
    private fun onPrimPicked(localId: Long) {
        android.util.Log.i(TAG, "Picked object localId=$localId")
        if (app.isObjectManagerInitialized()) {
            app.objectManager.getObject(localId.toInt())?.let { obj ->
                Toast.makeText(
                    this,
                    "Selected ${obj.name.ifBlank { "object" }}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
    
    /**
     * Update fly button visual state.
     */
    private fun updateFlyButtonState(isFlying: Boolean) {
        btnFly.alpha = if (isFlying) 1.0f else 0.6f
        btnFly.isSelected = isFlying
    }
    
    /**
     * Update run button visual state.
     */
    private fun updateRunButtonState(isRunning: Boolean) {
        btnRun.alpha = if (isRunning) 1.0f else 0.6f
        btnRun.isSelected = isRunning
    }
    
    /**
     * Update sit button visual state.
     */
    private fun updateSitButtonState(isSitting: Boolean) {
        btnSit.alpha = if (isSitting) 1.0f else 0.6f
        btnSit.isSelected = isSitting
    }
    
    /**
     * Show gestures popup menu.
     */
    private fun showGesturesPopup() {
        if (!app.isGestureManagerInitialized()) {
            Toast.makeText(this, R.string.gestures_not_available, Toast.LENGTH_SHORT).show()
            return
        }
        
        val gestures = app.gestureManager.getActiveGestures()
        if (gestures.isEmpty()) {
            Toast.makeText(this, R.string.no_active_gestures, Toast.LENGTH_SHORT).show()
            return
        }
        
        // Show a popup menu with active gestures
        val gestureNames = gestures.map { it.name }.toTypedArray()
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(R.string.gestures)
            .setItems(gestureNames) { dialog, which ->
                val gesture = gestures[which]
                lifecycleScope.launch {
                    app.gestureManager.playGesture(gesture.id)
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }
    
    private fun initRenderer() {
        val targetBackend = preferredRendererBackend()
        if (rendererHandoffManager.currentState() == RendererHandoffManager.State.IDLE) {
            rendererHandoffManager.activateInitialBackend(targetBackend, "activity_create")
        } else {
            rendererHandoffManager.switchBackend(targetBackend, "renderer_preference_changed")
        }
    }

    private fun initRendererHandoffManager() {
        rendererHandoffManager = RendererHandoffManager(
            onPauseActiveBackendDrawing = { backend ->
                when (backend) {
                    RendererHandoffManager.RendererBackend.FILAMENT -> {
                        if (app.isRenderManagerInitialized()) {
                            app.renderManager.pauseDrawing("backend_switch")
                        }
                        isRendering = false
                        isSurfaceReady = false
                    }
                    RendererHandoffManager.RendererBackend.LUMIYA -> {
                        lumiyaSurfaceView?.onPause()
                    }
                }
            },
            onFlushPendingRenderUpdates = { backend ->
                if (backend == RendererHandoffManager.RendererBackend.FILAMENT && app.isRenderManagerInitialized()) {
                    app.renderManager.dispatcher.runBlocking {
                        app.renderManager.flushPendingRenderUpdates()
                    }
                }
            },
            onDisposeBackendOwnedGpuResources = { backend ->
                when (backend) {
                    RendererHandoffManager.RendererBackend.FILAMENT -> {
                        if (app.isRenderManagerInitialized()) {
                            app.renderManager.dispatcher.runBlocking {
                                app.renderManager.shutdown()
                            }
                        }
                    }
                    RendererHandoffManager.RendererBackend.LUMIYA -> {
                        lumiyaSurfaceView?.shutdown()
                        lumiyaSurfaceView = null
                    }
                }
                worldViewportHost.clearViewport()
            },
            onAttachTargetBackendAndReplaySnapshot = { backend, snapshot ->
                useSecondaryRenderer = backend == RendererHandoffManager.RendererBackend.LUMIYA
                when (backend) {
                    RendererHandoffManager.RendererBackend.FILAMENT -> attachFilamentRenderer(snapshot)
                    RendererHandoffManager.RendererBackend.LUMIYA -> attachSecondaryRenderer(snapshot)
                }
            },
            snapshotProvider = { captureSceneSnapshot() }
        )
    }

    private fun preferredRendererBackend(): RendererHandoffManager.RendererBackend {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        // Primary renderer is OpenGL ES 3 (Lumiya pipeline). Honour the new
        // renderer_backend key first, then fall back to the legacy boolean
        // preference for users upgrading from the Filament-default era.
        val backendPref = prefs.getString("renderer_backend", "opengl") ?: "opengl"
        if (backendPref.equals("filament", ignoreCase = true)) {
            return RendererHandoffManager.RendererBackend.FILAMENT
        }
        if (backendPref.equals("opengl", ignoreCase = true) ||
            backendPref.equals("gles", ignoreCase = true) ||
            backendPref.equals("lumiya", ignoreCase = true)
        ) {
            return RendererHandoffManager.RendererBackend.LUMIYA
        }
        // Legacy fallback: the old pref defaulted false=Filament. We invert
        // semantics so an unset legacy install lands on OpenGL.
        return if (prefs.getBoolean("enable_secondary_renderer", true)) {
            RendererHandoffManager.RendererBackend.LUMIYA
        } else {
            RendererHandoffManager.RendererBackend.FILAMENT
        }
    }

    private fun captureSceneSnapshot(): RendererHandoffManager.SceneStateSnapshot {
        val controller = app.renderManager.cameraController
        return RendererHandoffManager.SceneStateSnapshot(
            cameraMode = controller.mode,
            cameraYawDeg = controller.yawDeg,
            cameraPitchDeg = controller.pitchDeg,
            cameraFollowDistance = controller.followDistance
        )
    }

    private fun attachFilamentRenderer(snapshot: RendererHandoffManager.SceneStateSnapshot) {
        val controller = app.renderManager.cameraController
        controller.setMode(snapshot.cameraMode)
        controller.yawDeg = snapshot.cameraYawDeg
        controller.pitchDeg = snapshot.cameraPitchDeg
        controller.followDistance = snapshot.cameraFollowDistance

        val renderView: View = if (useSecondaryRenderer) {
            LumiyaGLSurfaceView(this).also { glView ->
                glView.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                lumiyaSurfaceView = glView
                renderBackend = OpenGLES3Backend(glView)
                android.util.Log.i(TAG, "✓ Secondary Lumiya renderer enabled")
            }
        } else {
            SurfaceView(this).also { filamentSurface ->
                filamentSurface.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
                surfaceView = filamentSurface
                lumiyaSurfaceView = null
                renderBackend = FilamentBackend(app.renderManager, filamentSurface)
                android.util.Log.i(TAG, "Initializing RenderManager...")
            }
        }

        worldViewportHost.attachViewport(renderView)

        renderView.holderOrNull()?.addCallback(object : android.view.SurfaceHolder.Callback {
            override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                if (rendererHandoffManager.currentBackend() != RendererHandoffManager.RendererBackend.FILAMENT ||
                    rendererHandoffManager.currentState() != RendererHandoffManager.State.ACTIVE) {
                    android.util.Log.w(TAG, "Ignoring surfaceCreated while backend state is ${rendererHandoffManager.currentState()}")
                    return
                }
                android.util.Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                android.util.Log.i(TAG, "║ ⭐ Surface created - Surface is now ready")
                android.util.Log.i(TAG, "║ Surface valid: ${holder.surface.isValid}")
                android.util.Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")

                isSurfaceReady = true
                renderBackend?.attachSurface(holder)

                synchronized(this@WorldViewActivity) {
                    if (!isRendering) {
                        android.util.Log.i(TAG, "✓ Starting render loop now that surface is ready")
                        isRendering = true
                        startRenderLoop()
                    }
                }
            }

            override fun surfaceChanged(holder: android.view.SurfaceHolder, format: Int, width: Int, height: Int) {
                if (rendererHandoffManager.currentBackend() != RendererHandoffManager.RendererBackend.FILAMENT ||
                    rendererHandoffManager.currentState() != RendererHandoffManager.State.ACTIVE) {
                    android.util.Log.w(TAG, "Ignoring surfaceChanged while backend state is ${rendererHandoffManager.currentState()}")
                    return
                }
                android.util.Log.d(TAG, "Surface changed: ${width}x${height}")
                if (isSurfaceReady) {
                    renderBackend?.onSurfaceChanged(width, height)
                }
            }

            override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                if (rendererHandoffManager.currentBackend() != RendererHandoffManager.RendererBackend.FILAMENT) {
                    return
                }
                android.util.Log.w(TAG, "⚠ Surface destroyed - stopping render loop")
                synchronized(this@WorldViewActivity) {
                    isSurfaceReady = false
                    isRendering = false
                }
                renderBackend?.onSurfaceDestroyed()
            }
        })

        if (!useSecondaryRenderer) {
            wireFilamentDataPipelines()
            android.util.Log.i(TAG, "✓ RenderManager initialized, waiting for surface to be ready...")
        }
    }

    private fun View.holderOrNull(): android.view.SurfaceHolder? {
        return when (this) {
            is SurfaceView -> this.holder
            is LumiyaGLSurfaceView -> this.holder
            else -> null
        }
    }

    private fun wireFilamentDataPipelines() {
        // Connect protocol-side TerrainManager to the now-instantiated
        // TerrainRenderer so incoming LayerData packets actually mesh into
        // visible terrain. No-op if either side isn't ready yet.
        if (app.isTerrainManagerInitialized()) {
            app.renderManager.getTerrainRenderer()?.let { tr ->
                app.terrainManager.setTerrainRenderer(tr)
                android.util.Log.i(TAG, "✓ TerrainRenderer wired into TerrainManager")
            }
        }

        // Bakes-on-Mesh: install a resolver on PrimRenderer so mesh
        // attachments referencing IMG_USE_BAKED_* sentinels get rewritten to
        // the local agent's actual baked texture UUIDs.
        app.renderManager.getPrimRenderer()?.setBomResolver { slot ->
            app.avatarManager.getMyAvatar()?.baker?.getBakedTextures()?.get(slot)
        }

        // Per-frame avatar pose tick. For each tracked avatar with a
        // skeleton, advance the AvatarAnimator and write the resulting
        // bone matrices into SceneManager so the body segments visibly
        // animate. Cheap: ~1ms even for 30 nearby avatars.
        var lastPoseTickMs = System.currentTimeMillis()
        app.renderManager.avatarPoseProvider = {
            try {
                val sm = app.renderManager.getSceneManager()
                if (sm != null && app.isAvatarManagerInitialized()) {
                    val now = System.currentTimeMillis()
                    val dt = ((now - lastPoseTickMs).coerceAtLeast(1L)) / 1000f
                    lastPoseTickMs = now
                    for (avatar in app.avatarManager.getAllAvatars()) {
                        avatar.animator.update(dt)
                        avatar.skeleton.updateBoneMatrices()
                        sm.applyAvatarPose(avatar.agentId, avatar.skeleton)
                        sm.applyAvatarSkinning(avatar.agentId, avatar.skeleton)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.v(TAG, "avatar pose tick error: ${e.message}")
            }
        }
    }

    private fun attachSecondaryRenderer(snapshot: RendererHandoffManager.SceneStateSnapshot) {
        val glView = LumiyaGLSurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        lumiyaSurfaceView = glView
        // Bind the GL engine and hand the consumer a GL-thread executor
        // (queueEvent on the GLSurfaceView) plus a TextureManager-backed
        // fetcher so prim faces actually pull their textures.
        app.bindGlesRenderEngine(
            glView.getEngineProvider(),
            glThreadExecutor = { runnable -> glView.runOnGlThread { runnable.run() } }
        )
        wireGlesDataPipelines(glView)
        worldViewportHost.attachViewport(glView)
        isSurfaceReady = true
        isRendering = false
        android.util.Log.i(TAG, "Replayed snapshot into Lumiya backend (camera mode=${snapshot.cameraMode})")
        android.util.Log.i(TAG, "✓ OpenGL ES 3 (Lumiya) primary renderer attached")
    }

    /**
     * Install protocol-side callbacks that target the Lumiya GL backend.
     * Mirrors [wireFilamentDataPipelines] but for the GL pipeline:
     *
     *  - Per-frame avatar pose tick: advances each tracked avatar's
     *    animator and pushes the resulting joint matrices into the
     *    Lumiya avatar store via [com.linkpoint.render.lumiya.core.LumiyaRenderer.updateAvatarJoints].
     *  - Bakes-on-Mesh texture resolver: future plumbing for routing
     *    IMG_USE_BAKED_* sentinel UUIDs to the agent's baked textures.
     *
     * Texture binding for prims is handled by the command consumer.
     */
    private fun wireGlesDataPipelines(glView: LumiyaGLSurfaceView) {
        val renderer = glView.getRenderer()

        // Drive avatar animation on each GL frame. The hook fires on the
        // GL thread inside LumiyaRenderer.renderFrame (before any draws),
        // so we can upload joint matrices directly without queueing.
        renderer.onBeforeFrame = { deltaTime ->
            try {
                if (app.isAvatarManagerInitialized()) {
                    for (avatar in app.avatarManager.getAllAvatars()) {
                        avatar.animator.update(deltaTime)
                        avatar.skeleton.updateBoneMatrices()
                        val matrices = avatar.skeleton.getSkinningMatrices()
                        val count = avatar.skeleton.boneArray.size
                        renderer.updateAvatarJoints(avatar.agentId, matrices, count)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.v(TAG, "GL avatar pose tick error: ${e.message}")
            }
        }
    }
    
    private fun startRenderLoop() {
        app.renderManager.dispatcher.post(object : Runnable {
            override fun run() {
                if (isRendering) {
                    renderBackend?.renderFrame(System.nanoTime())
                    app.renderManager.dispatcher.postDelayed(this, 16) // ~60fps
                }
            }
        })
    }
    
    private fun setupNavigation() {
        navigationView.setNavigationItemSelectedListener(this)
        
        // Setup header
        val headerView = navigationView.getHeaderView(0)
        val headerName = headerView.findViewById<TextView>(R.id.navHeaderName)
        val headerRegion = headerView.findViewById<TextView>(R.id.navHeaderRegion)
        
        headerName.text = app.sessionManager.getAvatarName()
        worldUiState.update { it.copy(avatarName = app.sessionManager.getAvatarName()) }
        
        lifecycleScope.launch {
            app.sessionManager.currentRegion.collectLatest { region ->
                headerRegion.text = region?.name ?: "Not connected"
                regionNameText.text = region?.name ?: ""
                worldUiState.update { current ->
                    current.copy(regionName = region?.name ?: "Not connected")
                }
            }
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            app.sessionManager.connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        avatarNameText.text = app.sessionManager.getAvatarName()
                        worldUiState.update { it.copy(avatarName = app.sessionManager.getAvatarName()) }
                        
                        // Cache landmarks from inventory after first successful login
                        if (app.startLocationManager.isFirstLoginComplete()) {
                            fetchAndCacheLandmarks()
                        }
                    }
                    ConnectionState.DISCONNECTED -> {
                        finish() // Return to login
                    }
                    else -> {}
                }
            }
        }
        
        lifecycleScope.launch {
            app.sessionManager.currentRegion.collectLatest { region ->
                regionNameText.text = region?.name ?: "Unknown Region"
                worldUiState.update { it.copy(regionName = region?.name ?: "Unknown Region") }
            }
        }

        startTelemetrySampler()
    }

    /**
     * Periodically samples the renderer frame counter and the UDP byte
     * counters to populate the FPS / kbps fields on the top status pill.
     *
     * Both fields default to `null` on `WorldUiState`, which the overlay
     * renders as "--". Without this sampler nothing else writes them and
     * the pill stays as "FPS --  ·  -- kbps" indefinitely. Sampling at
     * 1 Hz is a deliberate trade-off: it's frequent enough to feel live
     * and slow enough that the integer division to kbps doesn't quantise
     * to zero on a quiet circuit.
     *
     * Tied to lifecycleScope so the sampler stops automatically when the
     * activity is destroyed; pause/resume don't gate it because the
     * counters keep advancing across pauses and a stale display is more
     * confusing than a brief lull when returning to the world view.
     */
    private fun startTelemetrySampler() {
        lifecycleScope.launch {
            var lastFrameCount = -1L
            var lastBytes = -1L
            var lastSampleMs = System.currentTimeMillis()
            while (isActive) {
                delay(1000L)
                val nowMs = System.currentTimeMillis()
                val intervalMs = (nowMs - lastSampleMs).coerceAtLeast(1L)
                lastSampleMs = nowMs

                val subsystem = RenderDiagnostics.activeRenderSubsystem()
                val frameCount = subsystem?.let { RenderDiagnostics.frameCount(it) } ?: 0L
                val fps: Int? = if (lastFrameCount < 0L || subsystem == null) {
                    null
                } else {
                    val delta = (frameCount - lastFrameCount).coerceAtLeast(0L)
                    ((delta * 1000L) / intervalMs).toInt()
                }
                lastFrameCount = frameCount

                val totalBytes = if (app.isUdpConnectionInitialized()) {
                    app.udpConnection.totalBytesReceived() + app.udpConnection.totalBytesSent()
                } else {
                    0L
                }
                val kbps: Int? = if (lastBytes < 0L) {
                    null
                } else {
                    val delta = (totalBytes - lastBytes).coerceAtLeast(0L)
                    // bytes/interval -> bits/sec -> kbps
                    ((delta * 8L * 1000L) / intervalMs / 1000L).toInt()
                }
                lastBytes = totalBytes

                worldUiState.update { it.copy(fps = fps, bandwidthKbps = kbps) }
            }
        }
    }

    /**
     * Fetch landmarks from inventory and cache them for start location selection.
     */
    private fun fetchAndCacheLandmarks() {
        if (!app.isInventoryManagerInitialized()) {
            android.util.Log.d(TAG, "Inventory manager not initialized, skipping landmark caching")
            return
        }
        
        lifecycleScope.launch {
            try {
                val landmarks = app.inventoryManager.fetchLandmarks()
                if (landmarks.isNotEmpty()) {
                    // Convert to InventoryLandmarkInfo and cache
                    val landmarkInfos = landmarks.mapNotNull { item ->
                        // Parse landmark asset to get region info
                        // For now, use the item name and description as region info
                        com.linkpoint.core.InventoryLandmarkInfo(
                            itemId = item.itemId.toString(),
                            assetId = item.assetId.toString(),
                            name = item.name,
                            description = item.description,
                            regionName = extractRegionFromDescription(item.description, item.name),
                            x = 128,
                            y = 128,
                            z = 25
                        )
                    }
                    
                    app.startLocationManager.cacheLandmarksFromInventory(landmarkInfos)
                    android.util.Log.i(TAG, "Cached ${landmarkInfos.size} landmarks from inventory")
                }
            } catch (e: Exception) {
                android.util.Log.w(TAG, "Failed to cache landmarks: ${e.message}")
            }
        }
    }
    
    /**
     * Extract region name from landmark description or name.
     */
    private fun extractRegionFromDescription(description: String, name: String): String {
        // Landmarks often have format: "Region Name (128, 128, 25)"
        val regionPattern = Regex("^([^(]+)")
        val match = regionPattern.find(description)
        if (match != null) {
            return match.groupValues[1].trim()
        }
        // Fall back to using the landmark name
        return name.substringBefore(" (").trim()
    }
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_chat -> startActivity(Intent(this, ChatActivity::class.java))
            R.id.nav_inventory -> startActivity(Intent(this, InventoryActivity::class.java))
            R.id.nav_minimap -> startActivity(Intent(this, MinimapActivity::class.java))
            R.id.nav_avatar -> startActivity(Intent(this, MyAvatarActivity::class.java))
            R.id.nav_friends -> startActivity(Intent(this, FriendsActivity::class.java))
            R.id.nav_groups -> startActivity(Intent(this, com.linkpoint.ui.groups.GroupsActivity::class.java))
            R.id.nav_nearby -> startActivity(Intent(this, NearbyPeopleActivity::class.java))
            R.id.nav_radar -> startActivity(Intent(this, com.linkpoint.ui.radar.RadarActivity::class.java))
            R.id.nav_search -> startActivity(Intent(this, com.linkpoint.ui.search.SearchActivity::class.java))
            R.id.nav_world_map -> startActivity(Intent(this, com.linkpoint.ui.map.MapActivity::class.java))
            R.id.nav_teleport_home -> teleportHome()
            R.id.nav_settings -> startActivity(Intent(this, SettingsActivity::class.java))
            R.id.nav_xr_mode -> {
                if (app.isXREntryAvailable()) {
                    startActivity(Intent(this, XRWorldActivity::class.java))
                } else {
                    Toast.makeText(this, "XR mode is unavailable in this build", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.nav_logout -> {
                app.protocol.disconnect()
            }
        }
        
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
    
    /**
     * Teleport home (to user's home location).
     */
    private fun teleportHome() {
        lifecycleScope.launch {
            try {
                if (app.isTeleportManagerInitialized()) {
                    val result = app.teleportManager.teleportHome()
                    when (result) {
                        is com.linkpoint.teleport.TeleportResult.Pending -> {
                            Toast.makeText(this@WorldViewActivity, R.string.teleporting_home, Toast.LENGTH_SHORT).show()
                        }
                        is com.linkpoint.teleport.TeleportResult.Failure -> {
                            Toast.makeText(this@WorldViewActivity, "Failed: ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> {}
                    }
                } else {
                    // Fallback to session manager
                    app.sessionManager.teleportHome()
                    Toast.makeText(this@WorldViewActivity, R.string.teleporting_home, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@WorldViewActivity, "Failed to teleport: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_world_view, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    /**
     * Setup back press handler using modern OnBackPressedCallback
     */
    private fun setupBackPressHandler() {
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    // Confirm exit
                    androidx.appcompat.app.AlertDialog.Builder(this@WorldViewActivity)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Logout") { _, _ ->
                            app.protocol.disconnect()
                        }
                        .setNegativeButton("Cancel", null)
                        .show()
                }
            }
        })
    }
    
    override fun onPause() {
        // Lumiya parity: flip the drawing gate FIRST so any in-flight frame on
        // the render thread short-circuits before we stop the loop or the OS
        // tears down our SurfaceView. Without this, a frame that beat us to
        // `Renderer.beginFrame` could touch a SwapChain that is about to be
        // destroyed by `UiHelper.onDetachedFromSurface`, which threw a native
        // crash and tripped the auto-restart loop the user observed when
        // opening any panel.
        renderBackend?.onPause()
        super.onPause()
        NetworkLogger.log(
            NetworkLogger.Level.INFO,
            NetworkLogger.Category.LIFECYCLE,
            "🌐 WorldViewActivity onPause (renderer=${if (useSecondaryRenderer) "lumiya" else "filament"})"
        )
        // Stop the loop while paused; backend-specific pause has already been delegated.
        isRendering = false
    }

    override fun onResume() {
        super.onResume()
        NetworkLogger.log(
            NetworkLogger.Level.INFO,
            NetworkLogger.Category.LIFECYCLE,
            "🌐 WorldViewActivity onResume (renderer=${if (useSecondaryRenderer) "lumiya" else "filament"})"
        )
        applyScreenOrientation() // Reapply orientation in case user changed setting
        updateDebugFloaterVisibility() // Update debug floater visibility based on settings
        applyInterfacePreferences()

        val preferredBackend = preferredRendererBackend()
        if (preferredBackend != rendererHandoffManager.currentBackend()) {
            rendererHandoffManager.switchBackend(preferredBackend, "on_resume_preferences")
        }

        if (rendererHandoffManager.currentBackend() == RendererHandoffManager.RendererBackend.LUMIYA) {
            lumiyaSurfaceView?.onResume()
            return
        }

        // Re-open the drawing gate. Posted to the render thread so it
        // happens AFTER any pending `recreateSwapChain` from the surface
        // callback drained, mirroring Lumiya's `queueEvent { enableDrawing() }`
        // ordering. If the surface isn't back yet, the gate flips here but
        // `ensureSwapChain` will still no-op until `surfaceCreated` fires —
        // both paths are safe.
        if (app.isRenderManagerInitialized()) {
            app.renderManager.dispatcher.post(
                Runnable { app.renderManager.resumeDrawing("activity_resumed") }
            )
        }

        // Only restart rendering if surface is ready (synchronized to avoid race)
        synchronized(this) {
            if (isSurfaceReady && !isRendering) {
                android.util.Log.i(TAG, "onResume: Restarting render loop")
                isRendering = true
                startRenderLoop()
            } else if (!isSurfaceReady) {
                android.util.Log.w(TAG, "onResume: Surface not ready yet, will start when ready")
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        if (useSecondaryRenderer) {
            // Skip the renderer teardown when the activity is being recreated
            // for a configuration change (rotation, theme, locale, etc.).
            // The new instance immediately spins up a fresh GLSurfaceView,
            // and full shutdown forces a slow EGL/context/program rebuild —
            // this is the "takes too long to restart" stall users hit when
            // rotating or coming back from a panel that triggered a config
            // change. preserveEGLContextOnPause already keeps state alive
            // across pause/resume; this guard extends the same intent to
            // configuration-driven recreation.
            if (!isChangingConfigurations) {
                app.bindGlesRenderEngine(null)
                lumiyaSurfaceView?.shutdown()
            }
            lumiyaSurfaceView = null
        } else {
            isRendering = false
        }
    }
}
