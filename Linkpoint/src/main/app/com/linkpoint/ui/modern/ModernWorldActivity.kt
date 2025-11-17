package com.linkpoint.ui.modern

import android.os.Bundle
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.linkpoint.LinkpointApp
import com.linkpoint.GridConnectionService
import com.linkpoint.modern.graphics.ModernGLSurfaceView
import com.linkpoint.modern.graphics.ModernGraphicsSupport
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Lightweight modern renderer showcase backed by OpenGL ES 3.0.
 *
 * The activity validates availability at runtime, shows a friendly message if the device does
 * not meet requirements, and otherwise drives a simple rotating triangle to verify the modern
 * pipeline works end-to-end.
 */
class ModernWorldActivity : AppCompatActivity() {

    private var glSurfaceView: ModernGLSurfaceView? = null
    private var connectionBinder: GridConnectionService.ConnectionBinder? = null
    private var connectionMonitorJob: Job? = null
    private lateinit var connectionStatusView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val status = LinkpointApp.getModernGraphicsStatus()
            ?: ModernGraphicsSupport.evaluate(this).also {
                LinkpointApp.setModernGraphicsStatus(it)
            }

        if (!status.supported) {
            showUnsupportedMessage(status)
            return
        }

        val container = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        val surfaceView = ModernGLSurfaceView(this)
        glSurfaceView = surfaceView
        container.addView(surfaceView)

        val overlay = TextView(this).apply {
            text = buildString {
                append("OpenGL ES ")
                append(status.reportedVersion.ifEmpty { "3.0+" })
                append(" • ")
                append(status.deviceSummary)
            }
            alpha = 0.75f
            setPadding(32, 24, 32, 24)
        }

        connectionStatusView = TextView(this).apply {
            text = "Connection status: Initialising…"
            alpha = 0.85f
            setPadding(32, 24, 32, 24)
        }

        container.addView(
            overlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            )
        )
        container.addView(
            connectionStatusView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            )
        )

        setContentView(container)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
        bindConnectionService()
    }

    override fun onPause() {
        unbindConnectionService()
        glSurfaceView?.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        glSurfaceView?.release()
        glSurfaceView = null
        super.onDestroy()
    }

    private fun showUnsupportedMessage(status: ModernGraphicsSupport.Status) {
        val message = TextView(this).apply {
            textAlignment = View.TEXT_ALIGNMENT_CENTER
            text = buildString {
                appendLine("Modern graphics unavailable")
                appendLine()
                appendLine(status.reason ?: "OpenGL ES 3.0 support not detected.")
                appendLine()
                append("Reported version: ")
                append(status.reportedVersion.ifEmpty { "unknown" })
            }
            setPadding(48, 96, 48, 96)
        }
        setContentView(message)
    }

    private fun bindConnectionService() {
        if (connectionBinder != null) return
        val intent = Intent(this, GridConnectionService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindConnectionService() {
        connectionMonitorJob?.cancel()
        connectionMonitorJob = null
        if (connectionBinder != null) {
            unbindService(serviceConnection)
            connectionBinder = null
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as? GridConnectionService.ConnectionBinder ?: return
            connectionBinder = binder
            connectionMonitorJob?.cancel()
            connectionMonitorJob = lifecycleScope.launch {
                repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                    binder.state().collect { state ->
                        updateConnectionStatus(state)
                    }
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            connectionMonitorJob?.cancel()
            connectionMonitorJob = null
            connectionBinder = null
            connectionStatusView.text = "Connection status: Service unavailable"
        }
    }

    private fun updateConnectionStatus(state: GridConnectionService.ConnectionState) {
        connectionStatusView.text = when (state) {
            GridConnectionService.ConnectionState.Idle -> "Connection status: Idle"
            is GridConnectionService.ConnectionState.Connecting -> buildString {
                append("Connecting to ")
                append(state.grid)
                append(" as ")
                append(state.avatarName)
                append("…")
            }
            is GridConnectionService.ConnectionState.Connected -> buildString {
                append("Connected to ")
                append(state.grid)
                append(" as ")
                append(state.avatarName)
            }
            is GridConnectionService.ConnectionState.Error -> buildString {
                append("Connection error: ")
                append(state.message)
            }
        }
    }
}
