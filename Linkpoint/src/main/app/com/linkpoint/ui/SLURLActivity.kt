package com.linkpoint.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * Handles SLURL (Second Life URL) intents
 * Parses secondlife:// URLs and http://maps.secondlife.com URLs
 */
class SLURLActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        handleIntent(intent)
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }
    
    private fun handleIntent(intent: Intent) {
        val uri = intent.data
        if (uri == null) {
            Toast.makeText(this, "Invalid SLURL", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        
        val slurl = parseSLURL(uri)
        if (slurl != null) {
            // Show teleport confirmation dialog
            showTeleportDialog(slurl)
        } else {
            Toast.makeText(this, "Could not parse SLURL", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    
    private fun parseSLURL(uri: Uri): SLURL? {
        return when (uri.scheme) {
            "secondlife" -> parseSecondLifeScheme(uri)
            "http", "https" -> parseHttpScheme(uri)
            else -> null
        }
    }
    
    private fun parseSecondLifeScheme(uri: Uri): SLURL? {
        // Format: secondlife://RegionName/x/y/z
        val path = uri.path ?: return null
        val parts = path.trim('/').split('/')
        
        if (parts.isEmpty()) return null
        
        val regionName = parts.getOrNull(0) ?: return null
        val x = parts.getOrNull(1)?.toFloatOrNull() ?: 128f
        val y = parts.getOrNull(2)?.toFloatOrNull() ?: 128f
        val z = parts.getOrNull(3)?.toFloatOrNull() ?: 25f
        
        return SLURL(regionName, x, y, z)
    }
    
    private fun parseHttpScheme(uri: Uri): SLURL? {
        // Format: http://maps.secondlife.com/secondlife/RegionName/x/y/z
        val path = uri.path ?: return null
        
        // Remove /secondlife/ prefix
        val cleanPath = path.removePrefix("/secondlife/").trim('/')
        val parts = cleanPath.split('/')
        
        if (parts.isEmpty()) return null
        
        // Handle URL-encoded region names
        val regionName = Uri.decode(parts.getOrNull(0) ?: return null)
        val x = parts.getOrNull(1)?.toFloatOrNull() ?: 128f
        val y = parts.getOrNull(2)?.toFloatOrNull() ?: 128f
        val z = parts.getOrNull(3)?.toFloatOrNull() ?: 25f
        
        return SLURL(regionName, x, y, z)
    }
    
    private fun showTeleportDialog(slurl: SLURL) {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Teleport")
            .setMessage("Teleport to ${slurl.regionName}\nPosition: (${slurl.x.toInt()}, ${slurl.y.toInt()}, ${slurl.z.toInt()})?")
            .setPositiveButton("Teleport") { _, _ ->
                // TODO: Implement teleport functionality
                Toast.makeText(this, "Teleporting to ${slurl.regionName}...", Toast.LENGTH_LONG).show()
                
                // Launch main activity with teleport intent
                val mainIntent = Intent(this, MainActivity::class.java).apply {
                    putExtra("teleport_region", slurl.regionName)
                    putExtra("teleport_x", slurl.x)
                    putExtra("teleport_y", slurl.y)
                    putExtra("teleport_z", slurl.z)
                }
                startActivity(mainIntent)
                finish()
            }
            .setNegativeButton("Cancel") { _, _ ->
                finish()
            }
            .setOnCancelListener {
                finish()
            }
            .show()
    }
    
    data class SLURL(
        val regionName: String,
        val x: Float,
        val y: Float,
        val z: Float
    ) {
        override fun toString(): String {
            return "secondlife://$regionName/${x.toInt()}/${y.toInt()}/${z.toInt()}"
        }
    }
}
