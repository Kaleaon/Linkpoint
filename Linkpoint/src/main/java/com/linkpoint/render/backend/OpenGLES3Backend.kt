package com.linkpoint.render.backend

import android.opengl.GLSurfaceView
import android.view.SurfaceHolder
import com.linkpoint.render.lumiya.core.LumiyaGLSurfaceView

class OpenGLES3Backend(
    private val glSurfaceView: LumiyaGLSurfaceView
) : RenderBackend {

    init {
        glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_WHEN_DIRTY
    }

    override fun attachSurface(holder: SurfaceHolder) {
        // GLSurfaceView owns EGL/surface wiring internally.
    }

    override fun onSurfaceChanged(width: Int, height: Int) {
        // Forwarded through GLSurfaceView.Renderer.onSurfaceChanged.
    }

    override fun onSurfaceDestroyed() {
        glSurfaceView.queueEvent {
            glSurfaceView.getRenderer().onSurfaceDestroyed()
        }
    }

    override fun onResume() {
        glSurfaceView.onResume()
    }

    override fun onPause() {
        glSurfaceView.onPause()
    }

    override fun renderFrame(frameTimeNanos: Long) {
        glSurfaceView.requestRender()
    }

    override fun shutdown() {
        glSurfaceView.shutdown()
    }
}
