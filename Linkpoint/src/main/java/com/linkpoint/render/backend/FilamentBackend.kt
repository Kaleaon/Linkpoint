package com.linkpoint.render.backend

import android.view.SurfaceHolder
import android.view.SurfaceView
import com.linkpoint.render.RenderManager

class FilamentBackend(
    private val renderManager: RenderManager,
    private val surfaceView: SurfaceView
) : RenderBackend {

    init {
        renderManager.initializeOnRenderThread(surfaceView)
    }

    override fun attachSurface(holder: SurfaceHolder) {
        renderManager.dispatcher.post(Runnable { renderManager.recreateSwapChain() })
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        renderManager.dispatcher.post(Runnable { renderManager.recreateSwapChain(width, height) })
    }

    override fun onSurfaceDestroyed() {
        renderManager.onSurfaceDestroyed()
        renderManager.pauseDrawing("surface_destroyed")
    }

    override fun onResume() {
        renderManager.dispatcher.post(Runnable {
            renderManager.onAppResumed()
            renderManager.resumeDrawing("activity_resumed")
            renderManager.recreateSwapChain()
        })
    }

    override fun onPause() {
        renderManager.onAppPaused()
        renderManager.pauseDrawing("activity_paused")
    }

    override fun renderFrame(frameTimeNanos: Long) {
        renderManager.renderFrame()
    }

    override fun shutdown() {
        renderManager.pauseDrawing("activity_destroyed")
    }
}
