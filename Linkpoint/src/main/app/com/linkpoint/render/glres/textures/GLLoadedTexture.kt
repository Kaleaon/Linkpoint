package com.linkpoint.render.glres.textures

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES10
import android.opengl.GLES20
import android.opengl.GLUtils
import com.linkpoint.Debug
import com.linkpoint.openjpeg.OpenJPEG
import com.linkpoint.render.RenderContext
import java.io.IOException

/**
 * GL texture loaded from bitmap or OpenJPEG data
 */
class GLLoadedTexture : GLResourceTexture {
    
    private val hasAlphaLayer: Boolean
    val height: Int
    val width: Int

    /**
     * Create texture from bitmap
     */
    constructor(renderContext: RenderContext, bitmap: Bitmap) : super(
        renderContext.glResourceManager,
        bitmap.height * bitmap.rowBytes
    ) {
        // Bind texture
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle)
        } else {
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, handle)
        }
        
        hasAlphaLayer = bitmap.hasAlpha()
        width = bitmap.width
        height = bitmap.height
        
        renderContext.KeepTexture(bitmap)
        GLUtils.texImage2D(GLES10.GL_TEXTURE_2D, 0, bitmap, 0)
        
        // Set texture parameters
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        } else {
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S, GLES10.GL_REPEAT.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T, GLES10.GL_REPEAT.toFloat())
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0)
        }
    }

    /**
     * Create texture from OpenJPEG data
     */
    constructor(renderContext: RenderContext, openJPEG: OpenJPEG) : super(
        renderContext.glResourceManager,
        openJPEG.getLoadedSize()
    ) {
        // Bind texture
        if (renderContext.hasGL20) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, handle)
        } else {
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, handle)
        }
        
        hasAlphaLayer = openJPEG.hasAlphaLayer()
        width = openJPEG.getWidth()
        height = openJPEG.getHeight()
        
        renderContext.KeepTexture(openJPEG)
        
        // Upload texture data
        if (renderContext.hasGL30) {
            openJPEG.SetAsImmutableTexture()
        } else {
            openJPEG.SetAsTexture()
        }
        
        // Set texture parameters
        if (renderContext.hasGL20) {
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        } else {
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MIN_FILTER, GLES10.GL_NEAREST.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_MAG_FILTER, GLES10.GL_NEAREST.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_S, GLES10.GL_REPEAT.toFloat())
            GLES10.glTexParameterf(GLES10.GL_TEXTURE_2D, GLES10.GL_TEXTURE_WRAP_T, GLES10.GL_REPEAT.toFloat())
            GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, 0)
        }
    }

    /**
     * Bind texture for drawing
     */
    fun GLDraw() {
        GLES10.glBindTexture(GLES10.GL_TEXTURE_2D, handle)
    }

    /**
     * Get texture height
     */
    fun getHeight(): Int = height

    /**
     * Get texture width
     */
    fun getWidth(): Int = width

    /**
     * Check if texture has alpha channel
     */
    fun hasAlphaLayer(): Boolean = hasAlphaLayer

    companion object {
        /**
         * Load texture from assets
         */
        fun loadFromAssets(
            renderContext: RenderContext,
            context: Context,
            path: String
        ): GLLoadedTexture? {
            return try {
                context.assets.open(path).use { inputStream ->
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    bitmap?.let { GLLoadedTexture(renderContext, it) }
                }
            } catch (e: IOException) {
                Debug.Warning(e)
                null
            }
        }
    }
}
