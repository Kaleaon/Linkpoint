package com.linkpoint.ui.world

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.core.ConnectionState
import com.linkpoint.ui.chat.ChatActivity
import com.linkpoint.ui.inventory.InventoryActivity
import com.linkpoint.ui.minimap.MinimapActivity
import com.linkpoint.ui.avatar.MyAvatarActivity
import com.linkpoint.ui.settings.SettingsActivity
import com.linkpoint.ui.xr.XRWorldActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * Main world view activity - shows the 3D world
 * Based on Lumiya's WorldViewActivity
 */
class WorldViewActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    
    companion object {
        private const val TAG = "WorldViewActivity"
    }
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var surfaceView: SurfaceView
    private lateinit var renderContainer: FrameLayout
    
    // HUD elements
    private lateinit var regionNameText: TextView
    private lateinit var avatarNameText: TextView
    private lateinit var btnChat: ImageButton
    private lateinit var btnMinimap: ImageButton
    private lateinit var btnInventory: ImageButton
    private lateinit var btnXR: ImageButton
    
    private val app by lazy { LinkpointApp.getInstance() }
    private var isRendering = false
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_world_view)
        
        initViews()
        initRenderer()
        setupNavigation()
        observeState()
    }
    
    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        renderContainer = findViewById(R.id.renderContainer)
        
        regionNameText = findViewById(R.id.textRegionName)
        avatarNameText = findViewById(R.id.textAvatarName)
        
        btnChat = findViewById(R.id.btnChat)
        btnMinimap = findViewById(R.id.btnMinimap)
        btnInventory = findViewById(R.id.btnInventory)
        btnXR = findViewById(R.id.btnXR)
        
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
    }
    
    private fun initRenderer() {
        surfaceView = SurfaceView(this)
        renderContainer.addView(surfaceView)
        
        app.renderManager.initialize(surfaceView)
        isRendering = true
        
        // Start render loop
        startRenderLoop()
    }
    
    private fun startRenderLoop() {
        surfaceView.post(object : Runnable {
            override fun run() {
                if (isRendering) {
                    app.renderManager.renderFrame()
                    surfaceView.postDelayed(this, 16) // ~60fps
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
    
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_chat -> startActivity(Intent(this, ChatActivity::class.java))
            R.id.nav_inventory -> startActivity(Intent(this, InventoryActivity::class.java))
            R.id.nav_minimap -> startActivity(Intent(this, MinimapActivity::class.java))
            R.id.nav_avatar -> startActivity(Intent(this, MyAvatarActivity::class.java))
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
    
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // Confirm exit
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Logout") { _, _ ->
                    app.protocol.disconnect()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }
    
    override fun onPause() {
        super.onPause()
        isRendering = false
    }
    
    override fun onResume() {
        super.onResume()
        isRendering = true
        startRenderLoop()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        isRendering = false
    }
}
