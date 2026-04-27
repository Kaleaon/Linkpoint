package com.linkpoint.render.glres.textures
import java.util.*

import android.annotation.TargetApi
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.SurfaceTexture
import android.opengl.GLES11
import android.view.Surface
import java.nio.Buffer

@TargetApi(15)
class GLExternalTexture {
    private val Int handle
    private val Int height
    private val SurfaceTexture.OnFrameAvailableListener onFrameAvailableListener = SurfaceTexture.OnFrameAvailableListener() {
        fun onFrameAvailable(surfaceTexture: SurfaceTexture) {
        }
    }
    private val Surface surface
    private val SurfaceTexture surfaceTexture
    private val Int width

    @TargetApi(15)
    public GLExternalTexture(Int i, Int i2) {
        this.width = i
        this.height = i2
        val iArr: IntArray = Int[1]
        GLES11.glGenTextures(1, iArr, 0)
        this.handle = iArr[0]
        bind()
        GLES11.glTexImage2D(36197, 0, 6408, i, i2, 0, 6408, 5121, (Buffer) null)
        GLES11.glTexParameteri(36197, 10241, 9729)
        GLES11.glTexParameteri(36197, 10240, 9729)
        this.surfaceTexture = SurfaceTexture(this.handle)
        this.surfaceTexture.setDefaultBufferSize(i, i2)
        this.surfaceTexture.setOnFrameAvailableListener(this.onFrameAvailableListener)
        this.surface = Surface(this.surfaceTexture)
    }

    @TargetApi(15)
    fun bind() {
        GLES11.glBindTexture(36197, this.handle)
    }

     public fun getCanvas(): Canvas {
        return this.surface.lockCanvas((Rect) null)
    }

     public fun getHeight(): Int {
        return this.height
    }

     public fun getWidth(): Int {
        return this.width
    }

    fun postCanvas(canvas: Canvas) {
        this.surface.unlockCanvasAndPost(canvas)
    }

    @TargetApi(15)
    fun release() {
        this.surface.release()
        this.surfaceTexture.release()
        GLES11.glDeleteTextures(1, IntArray{this.handle}, 0)
    }

    @TargetApi(11)
    fun update(fArr: FloatArray) {
        this.surfaceTexture.updateTexImage()
        this.surfaceTexture.getTransformMatrix(fArr)
    }
}
