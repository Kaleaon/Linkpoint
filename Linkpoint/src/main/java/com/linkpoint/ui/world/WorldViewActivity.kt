package com.linkpoint.ui.world

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.preference.PreferenceManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.ConnectionState
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.friends.FriendsActivity
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.minimap.MinimapActivity
import com.linkpoint.ui.avatar.MyAvatarActivity
import com.linkpoint.ui.people.NearbyPeopleActivity
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.ui.xr.XRWorldActivity
import com.linkpoint.utils.DebugReportService
import kotlinx.coroutines.flow.collectLatest
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
    private lateinit var renderContainer: FrameLayout
    
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
    
    // Movement controller (lazy init after login)
    private val movementController by lazy {
        app.avatarManager.movementController
    }
    
    // Debug floater button
    private var debugFloaterButton: FloatingActionButton? = null
    
    private val app by lazy { LinkpointApp.getInstance() }
    @Volatile private var isRendering = false
    @Volatile private var isSurfaceReady = false
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
        renderContainer = findViewById(R.id.renderContainer)
        
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
            if (app.isXRAvailable()) {
                startActivity(Intent(this, XRWorldActivity::class.java))
            }
        }
        
        // Show/hide XR button based on availability
        btnXR.visibility = if (app.isXRAvailable()) View.VISIBLE else View.GONE
        
        // Initialize HUD overlay for Second Life HUD attachments
        initHudOverlay()
        
        // Initialize joysticks
        initJoysticks()
        
        // Initialize movement control buttons
        initMovementControls()
        
        // Initialize action buttons
        initActionButtons()

        applyInterfacePreferences()
    }
    
    /**
     * Initialize the HUD overlay for displaying Second Life HUD attachments.
     */
    private fun initHudOverlay() {
        hudOverlay = findViewById(R.id.hudOverlay)
        
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
        hudOverlay.visibility = if (showHud && hudsVisibleFromManager) View.VISIBLE else View.GONE
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
        
        btnGestures.setOnClickListener {
            showGesturesPopup()
        }
        
        btnFriends.setOnClickListener {
            startActivity(Intent(this, FriendsActivity::class.java))
        }
        
        btnNearby.setOnClickListener {
            startActivity(Intent(this, NearbyPeopleActivity::class.java))
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
        surfaceView = SurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        renderContainer.removeAllViews()
        renderContainer.addView(surfaceView)
        
        // Add a callback to ensure SwapChain is created when surface is available
        surfaceView.holder.addCallback(object : android.view.SurfaceHolder.Callback {
            override fun surfaceCreated(holder: android.view.SurfaceHolder) {
                android.util.Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                android.util.Log.i(TAG, "║ ⭐ Surface created - Surface is now ready")
                android.util.Log.i(TAG, "║ Surface valid: ${holder.surface.isValid}")
                android.util.Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                
                // Mark surface as ready (volatile ensures visibility across threads)
                isSurfaceReady = true
                
                // Eagerly create SwapChain now that surface is available
                // This is more efficient than waiting for ensureSwapChain() to detect it on first render frame
                app.renderManager.dispatcher.post(
                    Runnable { app.renderManager.recreateSwapChain() }
                )
                
                // Start render loop only if not already rendering (synchronized to avoid race)
                synchronized(this@WorldViewActivity) {
                    if (!isRendering) {
                        android.util.Log.i(TAG, "✓ Starting render loop now that surface is ready")
                        isRendering = true
                        startRenderLoop()
                    }
                }
            }
            
            override fun surfaceChanged(holder: android.view.SurfaceHolder, format: Int, width: Int, height: Int) {
                android.util.Log.d(TAG, "Surface changed: ${width}x${height}")
                // Recreate SwapChain to handle new dimensions or format changes
                // This ensures viewport is updated immediately rather than waiting for next render frame
                if (isSurfaceReady) {
                    app.renderManager.dispatcher.post(
                        Runnable { app.renderManager.recreateSwapChain() }
                    )
                }
            }
            
            override fun surfaceDestroyed(holder: android.view.SurfaceHolder) {
                android.util.Log.w(TAG, "⚠ Surface destroyed - stopping render loop")
                // Mark surface as not ready first to prevent new render operations
                // Then stop rendering (volatile + synchronized ensures atomicity)
                synchronized(this@WorldViewActivity) {
                    isSurfaceReady = false
                    isRendering = false
                }
            }
        })
        
        // Initialize RenderManager with the SurfaceView
        android.util.Log.i(TAG, "Initializing RenderManager...")
        app.renderManager.initializeOnRenderThread(surfaceView)
        
        // Don't start render loop here - wait for surfaceCreated callback
        android.util.Log.i(TAG, "✓ RenderManager initialized, waiting for surface to be ready...")
    }
    
    private fun startRenderLoop() {
        app.renderManager.dispatcher.post(object : Runnable {
            override fun run() {
                if (isRendering) {
                    app.renderManager.renderFrame()
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
        
        lifecycleScope.launch {
            app.sessionManager.currentRegion.collectLatest { region ->
                headerRegion.text = region?.name ?: "Not connected"
                regionNameText.text = region?.name ?: ""
            }
        }
    }
    
    private fun observeState() {
        lifecycleScope.launch {
            app.sessionManager.connectionState.collectLatest { state ->
                when (state) {
                    ConnectionState.CONNECTED -> {
                        avatarNameText.text = app.sessionManager.getAvatarName()
                        
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
                if (app.isXRAvailable()) {
                    startActivity(Intent(this, XRWorldActivity::class.java))
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
        super.onPause()
        isRendering = false
    }
    
    override fun onResume() {
        super.onResume()
        applyScreenOrientation() // Reapply orientation in case user changed setting
        updateDebugFloaterVisibility() // Update debug floater visibility based on settings
        applyInterfacePreferences()
        
        // Only restart rendering if surface is ready (synchronized to avoid race)
        synchronized(this) {
            if (isSurfaceReady && !isRendering) {
                android.util.Log.i(TAG, "onResume: Restarting render loop")
                // Ensure SwapChain is recreated - it may have been destroyed when activity was paused
                // The UiHelper may have called onDetachedFromSurface() while we were paused
                app.renderManager.dispatcher.post(
                    Runnable { app.renderManager.recreateSwapChain() }
                )
                isRendering = true
                startRenderLoop()
            } else if (!isSurfaceReady) {
                android.util.Log.w(TAG, "onResume: Surface not ready yet, will start when ready")
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRendering = false
    }
}
