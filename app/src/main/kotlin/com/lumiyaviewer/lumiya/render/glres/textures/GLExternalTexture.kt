package com.lumiyaviewer.lumiya.render.glres.textures

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.opengl.GLES11
import android.view.Surface
import java.nio.Buffer

@TargetApi(15)
class GLExternalTexture(
    val width: Int,
    val height: Int
) {
    private val handle: Int
    
    private val onFrameAvailableListener = SurfaceTexture.OnFrameAvailableListener { 
        // Empty implementation
    }
    
    private val surfaceTexture: SurfaceTexture
    private val surface: Surface

    init {
        val handleArray = IntArray(1)
        GLES11.glGenTextures(1, handleArray, 0)
        handle = handleArray[0]
        
        bind()
        GLES11.glTexImage2D(36197, 0, 6408, width, height, 0, 6408, 5121, null as Buffer?)
        GLES11.glTexParameteri(36197, 10241, 9729)
        GLES11.glTexParameteri(36197, 10240, 9729)
        
        surfaceTexture = SurfaceTexture(handle)
        surfaceTexture.setDefaultBufferSize(width, height)
        surfaceTexture.setOnFrameAvailableListener(onFrameAvailableListener)
        surface = Surface(surfaceTexture)
    }

    @TargetApi(15)
    fun bind() {
        GLES11.glBindTexture(36197, handle)
    }

    fun getCanvas(): Canvas? {
        return surface.lockCanvas(null as Rect?)
    }

    fun postCanvas(canvas: Canvas) {
        surface.unlockCanvasAndPost(canvas)
    }

    @TargetApi(15)
    fun release() {
        surface.release()
        surfaceTexture.release()
        GLES11.glDeleteTextures(1, intArrayOf(handle), 0)
    }

    @TargetApi(11)
    fun update(transformMatrix: FloatArray) {
        surfaceTexture.updateTexImage()
        surfaceTexture.getTransformMatrix(transformMatrix)
    }
}
