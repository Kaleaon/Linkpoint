package com.linkpoint.render.backend

import android.view.SurfaceHolder

/**
 * Backend-agnostic render lifecycle contract used by WorldViewActivity.
 */
interface RenderBackend {
    fun attachSurface(holder: SurfaceHolder)
    fun onSurfaceChanged(width: Int, height: Int)
    fun onSurfaceDestroyed()
    fun onResume()
    fun onPause()
    fun renderFrame(frameTimeNanos: Long)
    fun shutdown()
}
