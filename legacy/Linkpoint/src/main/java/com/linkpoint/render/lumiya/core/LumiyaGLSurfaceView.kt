package com.linkpoint.render.lumiya.core

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.util.Log
import com.linkpoint.render.RenderDiagnostics
import com.linkpoint.render.RenderStateManager
import com.linkpoint.ui.overlay.OverlayManager
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

/**
 * GLSurfaceView configured for the Lumiya GL ES 3.2 render engine.
 *
 * This is used instead of a bare SurfaceView when the Lumiya engine is
 * the active renderer.  It handles EGL context creation with the correct
 * version and invokes [LumiyaRenderer] on the GL thread.
 *
 * Integrates with [OverlayManager.OverlaySurfaceTarget] to pause 3D rendering (0 FPS)
 * during full-screen 2D overlays while preserving the EGL context and GPU resources.
 */
class LumiyaGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs), OverlayManager.OverlaySurfaceTarget {

    companion object {
        private const val TAG = "LumiyaGLSurfaceView"
    }

    val renderStateManager = RenderStateManager()
    private val lumiyaRenderer = LumiyaRenderer()
    private var surfaceReady = false
    private var lastFrameTimeMs = 0L

    @Volatile
    private var lastFrameTimeNs: Long = 0L

    @Volatile
    private var currentCalculatedFps: Float = 0.0f

    @Volatile
    private var isPausedState: Boolean = false

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
                if (!surfaceReady || isPausedState) return

                val nowMs = System.currentTimeMillis()
                if (lastFrameTimeMs > 0L) {
                    val elapsedMs = nowMs - lastFrameTimeMs
                    val sleepMs = renderStateManager.calculateFrameDelayMs(elapsedMs, nowMs)
                    if (sleepMs > 0L) {
                        try {
                            Thread.sleep(sleepMs)
                        } catch (_: InterruptedException) {
                        }
                    }
                }
                lastFrameTimeMs = System.currentTimeMillis()

                val now = System.nanoTime()
                if (lastFrameTimeNs > 0) {
                    val deltaSec = (now - lastFrameTimeNs) / 1_000_000_000.0f
                    if (deltaSec > 0) {
                        val instantFps = 1.0f / deltaSec
                        currentCalculatedFps = if (currentCalculatedFps == 0.0f) instantFps else currentCalculatedFps * 0.9f + instantFps * 0.1f
                    }
                }
                lastFrameTimeNs = now

                lumiyaRenderer.renderFrame()
                RenderDiagnostics.glFrame()
            }
        })

        renderMode = RENDERMODE_CONTINUOUSLY
        renderStateManager.attachGlSurfaceView(this)
        OverlayManager.getInstance().bindSurfaceTarget(this)
    }

    fun getRenderer(): LumiyaRenderer = lumiyaRenderer

    fun getEngineProvider(): RenderEngineProvider = lumiyaRenderer

    /**
     * Run [block] on the GL thread, after the next frame's synchronization
     * point. Safe to call from any thread. Use this when you need to touch
     * GL resources (texture uploads, joint UBO writes, drawable mutations)
     * from outside the Renderer callbacks.
     *
     * Mirrors Lumiya's `WorldViewRenderer.queueEvent { ... }` idiom.
     */
    fun runOnGlThread(block: () -> Unit) {
        queueEvent(block)
    }

    override fun pause3dRendering() {
        Log.i(TAG, "pause3dRendering requested via OverlayManager")
        onPause()
    }

    override fun resume3dRendering() {
        Log.i(TAG, "resume3dRendering requested via OverlayManager")
        onResume()
    }

    override fun setDirtyFlagRenderMode(enabled: Boolean) {
        val newMode = if (enabled) RENDERMODE_WHEN_DIRTY else RENDERMODE_CONTINUOUSLY
        renderMode = newMode
        Log.i(TAG, "Render mode set to: ${if (enabled) "WHEN_DIRTY" else "CONTINUOUSLY"}")
    }

    override fun requestRenderFrame() {
        if (renderMode == RENDERMODE_WHEN_DIRTY && !isPausedState) {
            requestRender()
        }
    }

    override fun isEglContextPreserved(): Boolean {
        return preserveEGLContextOnPause
    }

    override fun getCurrent3dFps(): Float {
        return if (isPausedState) 0.0f else currentCalculatedFps
    }

    override fun onPause() {
        isPausedState = true
        currentCalculatedFps = 0.0f
        lastFrameTimeNs = 0L
        super.onPause()
        Log.d(TAG, "onPause (GL thread paused, EGL context preserved=${preserveEGLContextOnPause})")
    }

    override fun onResume() {
        isPausedState = false
        lastFrameTimeNs = 0L
        super.onResume()
        Log.d(TAG, "onResume (GL thread resumed)")
    }

    fun shutdown() {
        OverlayManager.getInstance().bindSurfaceTarget(null)
        RenderDiagnostics.glShutdown("LumiyaGLSurfaceView.shutdown()")
        queueEvent {
            lumiyaRenderer.shutdown()
        }
    }
}
