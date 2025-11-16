package com.linkpoint.ui.modern

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.linkpoint.LinkpointApp
import com.linkpoint.modern.graphics.ModernGLSurfaceView
import com.linkpoint.modern.graphics.ModernGraphicsSupport

/**
 * Lightweight modern renderer showcase backed by OpenGL ES 3.0.
 *
 * The activity validates availability at runtime, shows a friendly message if the device does
 * not meet requirements, and otherwise drives a simple rotating triangle to verify the modern
 * pipeline works end-to-end.
 */
class ModernWorldActivity : AppCompatActivity() {

    private var glSurfaceView: ModernGLSurfaceView? = null

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

        container.addView(
            overlay,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.CENTER_HORIZONTAL
            )
        )

        setContentView(container)
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView?.onResume()
    }

    override fun onPause() {
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
}
