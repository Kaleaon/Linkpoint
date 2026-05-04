package com.linkpoint.ui.slurl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.linkpoint.LinkpointApp
import com.linkpoint.R
import com.linkpoint.network.TeleportResult
import com.linkpoint.ui.login.ComposeLoginActivity
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

        val slurl = SLURLParser.parse(uri)

        if (slurl != null) {
            showTeleportDialog(slurl)
        } else {
            showErrorDialog("Invalid SLURL format")
        }
    }

    private fun showTeleportDialog(slurl: SLURLData) {
        val titleText = findViewById<TextView>(R.id.titleText)
        val locationText = findViewById<TextView>(R.id.locationText)
        val teleportButton = findViewById<Button>(R.id.teleportButton)
        val cancelButton = findViewById<Button>(R.id.cancelButton)

        titleText.text = "Teleport to Location"
        locationText.text = "${slurl.regionName}\n(${slurl.x.toInt()}, ${slurl.y.toInt()}, ${slurl.z.toInt()})"

        teleportButton.setOnClickListener {
            if (isExternalLaunch()) {
                showSensitiveActionConfirmation(slurl)
            } else {
                performTeleport(slurl)
            }
        }

        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun isExternalLaunch(): Boolean = intent?.action == Intent.ACTION_VIEW

    private fun showSensitiveActionConfirmation(slurl: SLURLData) {
        AlertDialog.Builder(this)
            .setTitle("Confirm external SLURL action")
            .setMessage("This link can change your login context and teleport location. Continue?")
            .setNegativeButton("Cancel") { _, _ -> }
            .setPositiveButton("Continue") { _, _ -> performTeleport(slurl) }
            .show()
    }

    private fun performTeleport(slurl: SLURLData) {
        if (!app.isConnected()) {
            val loginIntent = Intent(this, ComposeLoginActivity::class.java).apply {
                putExtra("slurl_region", slurl.regionName)
                putExtra("slurl_x", slurl.x)
                putExtra("slurl_y", slurl.y)
                putExtra("slurl_z", slurl.z)
            }
            startActivity(loginIntent)
            finish()
            return
        }

        lifecycleScope.launch {
            val result = app.protocol.teleport(slurl.regionName, slurl.x, slurl.y, slurl.z)
            when (result) {
                is TeleportResult.Success -> finish()
                is TeleportResult.Failure -> showErrorDialog(result.message)
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
        cancelButton.setOnClickListener { finish() }
    }
}

internal object SLURLParser {
    private const val MAX_URI_LENGTH = 2048
    private const val MAX_COMPONENT_LENGTH = 128
    private const val DEFAULT_X = 128f
    private const val DEFAULT_Y = 128f
    private const val DEFAULT_Z = 0f
    private val ALLOWED_HTTP_HOSTS = setOf("maps.secondlife.com")

    fun parse(uri: Uri): SLURLData? {
        if (uri.toString().length > MAX_URI_LENGTH) return null
        return try {
            when (uri.scheme?.lowercase()) {
                "secondlife" -> parseSecondLifeScheme(uri)
                "http", "https" -> parseHttpScheme(uri)
                else -> null
            }
        } catch (_: Exception) {
            null
        }
    }

    private fun parseSecondLifeScheme(uri: Uri): SLURLData? {
        if (!uri.userInfo.isNullOrBlank() || uri.port != -1) return null
        if (!uri.query.isNullOrBlank() || !uri.fragment.isNullOrBlank()) return null
        val regionName = sanitizeComponent(uri.host) ?: return null
        val segments = uri.pathSegments
        if (segments.size > 3) return null
        return buildSlurl(regionName, segments)
    }

    private fun parseHttpScheme(uri: Uri): SLURLData? {
        val host = uri.host?.lowercase() ?: return null
        if (host !in ALLOWED_HTTP_HOSTS) return null
        if (!uri.userInfo.isNullOrBlank() || !uri.fragment.isNullOrBlank()) return null
        val segments = uri.pathSegments
        if (segments.size < 2 || segments.size > 5) return null
        val root = sanitizeComponent(segments[0]) ?: return null
        if (root.lowercase() != "secondlife") return null
        val regionName = sanitizeComponent(segments[1]) ?: return null
        return buildSlurl(regionName, segments.drop(2))
    }

    private fun buildSlurl(regionName: String, coordinates: List<String>): SLURLData? {
        val x = parseCoordinate(coordinates.getOrNull(0), DEFAULT_X) ?: return null
        val y = parseCoordinate(coordinates.getOrNull(1), DEFAULT_Y) ?: return null
        val z = parseCoordinate(coordinates.getOrNull(2), DEFAULT_Z) ?: return null
        return SLURLData(regionName, x, y, z)
    }

    private fun parseCoordinate(raw: String?, fallback: Float): Float? {
        if (raw == null) return fallback
        val sanitized = sanitizeComponent(raw) ?: return null
        return sanitized.toFloatOrNull()
    }

    private fun sanitizeComponent(value: String?): String? {
        val trimmed = value?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        if (trimmed.length > MAX_COMPONENT_LENGTH) return null
        val cleaned = trimmed.filterNot { it.isISOControl() }
        if (cleaned.isBlank() || cleaned.length > MAX_COMPONENT_LENGTH) return null
        return cleaned
    }
}

data class SLURLData(
    val regionName: String,
    val x: Float,
    val y: Float,
    val z: Float
)
