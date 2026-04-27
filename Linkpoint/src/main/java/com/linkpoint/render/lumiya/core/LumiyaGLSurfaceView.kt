package com.linkpoint.render.lumiya.core

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import com.linkpoint.render.RenderDiagnostics
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * GLSurfaceView configured for the Lumiya GL ES 3.2 render engine.
 *
 * This is used instead of a bare SurfaceView when the Lumiya engine is
 * the active renderer.  It handles EGL context creation with the correct
 * version and invokes [LumiyaRenderer] on the GL thread.
 */
class LumiyaGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    companion object {
        private const val TAG = "LumiyaGLSurfaceView"
    }

    private val lumiyaRenderer = LumiyaRenderer()
    private var surfaceReady = false
    private var schedulerDriven = false

    init {
        // Request GL ES 3.2 context
        setEGLContextClientVersion(3)
        // 8-bit RGBA + 24-bit depth + 8-bit stencil
        setEGLConfigChooser(8, 8, 8, 8, 24, 8)
        preserveEGLContextOnPause = true

        setRenderer(object : Renderer {
            override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
                Log.i(TAG, "onSurfaceCreated")
                RenderDiagnostics.glSurfaceCreated()
                // Surface object isn't available here; init deferred to onSurfaceChanged
            }

            override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
                Log.i(TAG, "onSurfaceChanged ${width}x${height}")
                RenderDiagnostics.glSurfaceChanged(width, height)
                if (!lumiyaRenderer.isInitialized) {
                    val surface = holder.surface
                    lumiyaRenderer.initialize(context, surface, width, height)
                } else {
                    lumiyaRenderer.onSurfaceChanged(width, height)
                }
                surfaceReady = true
            }

            override fun onDrawFrame(gl: GL10?) {
                if (!surfaceReady) return
                lumiyaRenderer.renderFrame()
                RenderDiagnostics.glFrame()
            }
        })

        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun getRenderer(): LumiyaRenderer = lumiyaRenderer

    fun getEngineProvider(): RenderEngineProvider = lumiyaRenderer

    /**
     * Switch between legacy continuous GLSurfaceView pacing and scheduler-
     * driven pacing where each tick maps to [requestRender].
     */
    fun setSchedulerDrivenRendering(enabled: Boolean) {
        schedulerDriven = enabled
        renderMode = if (enabled) RENDERMODE_WHEN_DIRTY else RENDERMODE_CONTINUOUSLY
    }

    fun dispatchScheduledFrame(@Suppress("UNUSED_PARAMETER") frameTimeNanos: Long) {
        if (!surfaceReady) return
        if (schedulerDriven) {
            requestRender()
        }
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
    }

    fun shutdown() {
        RenderDiagnostics.glShutdown("LumiyaGLSurfaceView.shutdown()")
        queueEvent {
            lumiyaRenderer.shutdown()
        }
    }
}
