package com.lumiyaviewer.lumiya.ui.modern

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.modern.ModernLinkpointClient
import com.lumiyaviewer.lumiya.modern.auth.ModernAuthManager
import com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics
import com.lumiyaviewer.lumiya.modern.graphics.ModernRenderPipeline
import com.lumiyaviewer.lumiya.modern.utils.ModernPerformanceMonitor
import kotlinx.coroutines.launch

/**
 * Modern Dashboard Activity showcasing Linkpoint's modernized features.
 * 
 * This activity demonstrates:
 * - Material Design 3 UI
 * - Kotlin Coroutines for async operations
 * - Modern architecture patterns (MVVM)
 * - Performance monitoring
 * - Connection diagnostics
 * 
 * Features:
 * - Second Life grid connection status
 * - Performance metrics display
 * - Modern component status
 * - Quick access to key features
 */
class ModernDashboardActivity : AppCompatActivity() {
    
    private lateinit var authManager: ModernAuthManager
    private lateinit var performanceMonitor: ModernPerformanceMonitor
    private lateinit var connectionDiagnostics: ConnectionDiagnostics
    private lateinit var renderPipeline: ModernRenderPipeline
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize modern components
        initializeModernComponents()
        
        // Set up UI (will be replaced with Jetpack Compose in future)
        setupUI()
        
        // Start performance monitoring
        startMonitoring()
        
        // Check connection status
        checkConnectionStatus()
    }
    
    private fun initializeModernComponents() {
        try {
            authManager = ModernAuthManager(this)
            performanceMonitor = ModernPerformanceMonitor()
            connectionDiagnostics = ConnectionDiagnostics(this)
            renderPipeline = ModernRenderPipeline(this)
            
            Toast.makeText(this, "Modern components initialized successfully", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error initializing components: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
    
    private fun setupUI() {
        // For now, we'll use a simple layout
        // In Phase 3, this will be replaced with Jetpack Compose
        
        // Set title
        title = "Linkpoint Dashboard"
        supportActionBar?.subtitle = "Modern Second Life Viewer"
    }
    
    private fun startMonitoring() {
        lifecycleScope.launch {
            try {
                performanceMonitor.startMonitoring()
                
                // Display initial metrics
                val metrics = performanceMonitor.getCurrentMetrics()
                val message = buildString {
                    append("Performance Metrics:\n")
                    append("FPS: ${metrics["fps"] ?: "N/A"}\n")
                    append("Memory: ${metrics["memory_mb"] ?: "N/A"} MB\n")
                    append("CPU: ${metrics["cpu_usage"] ?: "N/A"}%")
                }
                
                runOnUiThread {
                    Toast.makeText(this@ModernDashboardActivity, message, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ModernDashboardActivity,
                        "Performance monitoring error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun checkConnectionStatus() {
        lifecycleScope.launch {
            try {
                val diagnostics = connectionDiagnostics.runDiagnostics()
                
                val status = buildString {
                    append("Connection Diagnostics:\n")
                    append("Network Available: ${diagnostics["network_available"]}\n")
                    append("Internet Access: ${diagnostics["internet_access"]}\n")
                    append("Grid Reachable: ${diagnostics["grid_reachable"]}\n")
                    append("Latency: ${diagnostics["latency_ms"] ?: "N/A"} ms")
                }
                
                runOnUiThread {
                    Toast.makeText(this@ModernDashboardActivity, status, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ModernDashboardActivity,
                        "Connection diagnostic error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.modern_dashboard_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_login -> {
                showLoginDialog()
                true
            }
            R.id.action_connect -> {
                connectToGrid()
                true
            }
            R.id.action_diagnostics -> {
                showDiagnostics()
                true
            }
            R.id.action_settings -> {
                showSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    
    private fun showLoginDialog() {
        Toast.makeText(this, "Login dialog (to be implemented)", Toast.LENGTH_SHORT).show()
        // Will open modern login UI in Phase 3
    }
    
    private fun connectToGrid() {
        lifecycleScope.launch {
            try {
                Toast.makeText(this@ModernDashboardActivity, "Connecting to Second Life grid...", Toast.LENGTH_SHORT).show()
                
                // This will use the modern authentication system
                // For now, just show status
                
                runOnUiThread {
                    Toast.makeText(
                        this@ModernDashboardActivity,
                        "Grid connection functionality will be enabled in Phase 2",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(
                        this@ModernDashboardActivity,
                        "Connection error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    
    private fun showDiagnostics() {
        lifecycleScope.launch {
            checkConnectionStatus()
        }
    }
    
    private fun showSettings() {
        Toast.makeText(this, "Settings (to be implemented with Compose)", Toast.LENGTH_SHORT).show()
        // Will open modern settings UI in Phase 3
    }
    
    override fun onResume() {
        super.onResume()
        performanceMonitor.startMonitoring()
    }
    
    override fun onPause() {
        super.onPause()
        performanceMonitor.stopMonitoring()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        
        // Clean up resources
        try {
            renderPipeline.cleanup()
            performanceMonitor.cleanup()
        } catch (e: Exception) {
            // Log cleanup errors but don't crash
            e.printStackTrace()
        }
    }
}

/**
 * Temporary menu resource IDs (will be defined in menu XML)
 */
object R {
    object menu {
        const val modern_dashboard_menu = 0
    }
    
    object id {
        const val action_login = 1
        const val action_connect = 2
        const val action_diagnostics = 3
        const val action_settings = 4
    }
}
