package com.lumiyaviewer.lumiya.render.glres.textures

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.opengl.GLES11
import android.view.Surface
import com.lumiyaviewer.lumiya.Debug

@TargetApi(15)
class GLExternalTexture(val width: Int, val height: Int) {
    private val handle: Int
    private val onFrameAvailableListener = SurfaceTexture.OnFrameAvailableListener { 
        // No-op or handle frame available
    }
    private val surface: Surface
    private val surfaceTexture: SurfaceTexture

    init {
        val iArr = IntArray(1)
        GLES11.glGenTextures(1, iArr, 0)
        this.handle = iArr[0]
        bind()
        GLES11.glTexImage2D(36197, 0, 6408, width, height, 0, 6408, 5121, null)
        GLES11.glTexParameteri(36197, 10241, 9729)
        GLES11.glTexParameteri(36197, 10240, 9729)
        this.surfaceTexture = SurfaceTexture(this.handle)
        this.surfaceTexture.setDefaultBufferSize(width, height)
        this.surfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener)
        this.surface = Surface(this.surfaceTexture)
    }

    @TargetApi(15)
    fun bind() {
        GLES11.glBindTexture(36197, this.handle)
    }

    fun getCanvas(): Canvas? {
        return this.surface.lockCanvas(null)
    }

    fun postCanvas(canvas: Canvas) {
        this.surface.unlockCanvasAndPost(canvas)
    }

    @TargetApi(15)
    fun release() {
        this.surface.release()
        this.surfaceTexture.release()
        val arr = IntArray(1)
        arr[0] = this.handle
        GLES11.glDeleteTextures(1, arr, 0)
    }

    @TargetApi(11)
    fun update(fArr: FloatArray) {
        this.surfaceTexture.updateTexImage()
        this.surfaceTexture.getTransformMatrix(fArr)
    }
}
