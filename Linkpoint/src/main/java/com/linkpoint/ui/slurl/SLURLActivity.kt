package com.linkpoint.ui.slurl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.network.TeleportResult
import com.linkpoint.ui.navigation.LegacyFeatureBridge
import com.linkpoint.ui.navigation.RouteArgs
import kotlinx.coroutines.launch

/**
 * SLURL Handler Activity
 * Handles secondlife:// and https://maps.secondlife.com URLs
 */
class SLURLActivity : AppCompatActivity() {
    
    companion object {
        private const val TAG = "SLURLActivity"
    }
    
    private val app by lazy { LinkpointApp.getInstance() }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_slurl)
        
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val uri = intent.data ?: run {
            finish()
            return
        }
        
        val slurl = parseSLURL(uri)
        
        if (slurl != null) {
            showTeleportDialog(slurl)
        } else {
            showErrorDialog("Invalid SLURL format")
        }
    }
    
    private fun parseSLURL(uri: Uri): SLURLData? {
        return try {
            when (uri.scheme) {
                "secondlife" -> parseSecondLifeScheme(uri)
                "http", "https" -> parseHttpScheme(uri)
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    private fun parseSecondLifeScheme(uri: Uri): SLURLData? {
        // secondlife://RegionName/x/y/z
        val pathSegments = uri.pathSegments
        if (pathSegments.isEmpty()) return null
        
        val regionName = uri.host ?: return null
        val x = pathSegments.getOrNull(0)?.toFloatOrNull() ?: 128f
        val y = pathSegments.getOrNull(1)?.toFloatOrNull() ?: 128f
        val z = pathSegments.getOrNull(2)?.toFloatOrNull() ?: 0f
        
        return SLURLData(regionName, x, y, z)
    }
    
    private fun parseHttpScheme(uri: Uri): SLURLData? {
        // https://maps.secondlife.com/secondlife/RegionName/x/y/z
        val pathSegments = uri.pathSegments
        if (pathSegments.size < 2) return null
        if (pathSegments[0] != "secondlife") return null
        
        val regionName = pathSegments[1]
        val x = pathSegments.getOrNull(2)?.toFloatOrNull() ?: 128f
        val y = pathSegments.getOrNull(3)?.toFloatOrNull() ?: 128f
        val z = pathSegments.getOrNull(4)?.toFloatOrNull() ?: 0f
        
        return SLURLData(regionName, x, y, z)
    }
    
    private fun showTeleportDialog(slurl: SLURLData) {
        val titleText = findViewById<TextView>(R.id.titleText)
        val locationText = findViewById<TextView>(R.id.locationText)
        val teleportButton = findViewById<Button>(R.id.teleportButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        
        titleText.text = "Teleport to Location"
        locationText.text = "${slurl.regionName}\n(${slurl.x.toInt()}, ${slurl.y.toInt()}, ${slurl.z.toInt()})"
        
        teleportButton.setOnClickListener {
            performTeleport(slurl)
        }
        
        cancelButton.setOnClickListener {
            finish()
        }
    }
    
    private fun performTeleport(slurl: SLURLData) {
        if (!app.isConnected()) {
            // Not logged in - go to login first
            startActivity(
                LegacyFeatureBridge.loginIntent(
                    context = this,
                    args = RouteArgs.Login(slurl.regionName, slurl.x, slurl.y, slurl.z)
                )
            )
            finish()
            return
        }
        
        // Already logged in - teleport directly
        lifecycleScope.launch {
            val result = app.protocol.teleport(
                slurl.regionName,
                slurl.x,
                slurl.y,
                slurl.z
            )
            
            when (result) {
                is TeleportResult.Success -> {
                    finish()
                }
                is TeleportResult.Failure -> {
                    showErrorDialog(result.message)
                }
            }
        }
    }
    
    private fun showErrorDialog(message: String) {
        val titleText = findViewById<TextView>(R.id.titleText)
        val locationText = findViewById<TextView>(R.id.locationText)
        val teleportButton = findViewById<Button>(R.id.teleportButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)
        
        titleText.text = "Error"
        locationText.text = message
        teleportButton.visibility = android.view.View.GONE
        cancelButton.text = "Close"
        cancelButton.setOnClickListener {
            finish()
        }
    }
}

data class SLURLData(
    val regionName: String,
    val x: Float,
    val y: Float,
    val z: Float
)
