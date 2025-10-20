package com.linkpoint.texture

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLUtils
import com.linkpoint.Debug
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger

/**
 * GLTexture - OpenGL ES texture wrapper
 * 
 * Handles:
 * - Texture creation and deletion
 * - Binding and unbinding
 * - Mipmap generation
 * - Texture parameters
 * - Memory tracking
 * 
 * Based on Firestorm's LLImageGL
 */
class GLTexture private constructor(
    val textureId: Int,
    val width: Int,
    val height: Int,
    val format: Int = GLES20.GL_RGBA
) {
    
    companion object {
        private val textureCounter = AtomicInteger(0)
        private val activeTextures = mutableSetOf<Int>()
        
        /**
         * Create texture from RGBA data
         */
        fun create(rgba: ByteArray, width: Int, height: Int): GLTexture {
            val textureIds = IntArray(1)
            GLES20.glGenTextures(1, textureIds, 0)
            
            val textureId = textureIds[0]
            if (textureId == 0) {
                throw RuntimeException("Failed to generate texture")
            }
            
            // Bind texture
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            
            // Set texture parameters
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR_MIPMAP_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT)
            
            // Upload texture data
            val buffer = ByteBuffer.allocateDirect(rgba.size)
            buffer.order(ByteOrder.nativeOrder())
            buffer.put(rgba)
            buffer.position(0)
            
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                buffer
            )
            
            // Generate mipmaps
            GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
            
            // Unbind
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            
            // Check for errors
            val error = GLES20.glGetError()
            if (error != GLES20.GL_NO_ERROR) {
                Debug.Log("GL Error creating texture: $error")
            }
            
            activeTextures.add(textureId)
            textureCounter.incrementAndGet()
            
            return GLTexture(textureId, width, height)
        }
        
        /**
         * Create empty texture (for render targets)
         */
        fun createEmpty(width: Int, height: Int, format: Int = GLES20.GL_RGBA): GLTexture {
            val textureIds = IntArray(1)
            GLES20.glGenTextures(1, textureIds, 0)
            
            val textureId = textureIds[0]
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
            
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE)
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE)
            
            GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                format,
                width,
                height,
                0,
                format,
                GLES20.GL_UNSIGNED_BYTE,
                null
            )
            
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
            
            activeTextures.add(textureId)
            textureCounter.incrementAndGet()
            
            return GLTexture(textureId, width, height, format)
        }
        
        /**
         * Get number of active textures
         */
        fun getActiveTextureCount(): Int = activeTextures.size
        
        /**
         * Get total textures created
         */
        fun getTotalTexturesCreated(): Int = textureCounter.get()
    }
    
    private var deleted = false
    
    /**
     * Bind this texture
     */
    fun bind(unit: Int = 0) {
        if (deleted) {
            Debug.Log("Attempting to bind deleted texture $textureId")
            return
        }
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + unit)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)
    }
    
    /**
     * Unbind texture
     */
    fun unbind() {
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
    }
    
    /**
     * Update texture data
     */
    fun update(rgba: ByteArray, x: Int = 0, y: Int = 0, width: Int = this.width, height: Int = this.height) {
        if (deleted) return
        
        bind()
        
        val buffer = ByteBuffer.allocateDirect(rgba.size)
        buffer.order(ByteOrder.nativeOrder())
        buffer.put(rgba)
        buffer.position(0)
        
        GLES20.glTexSubImage2D(
            GLES20.GL_TEXTURE_2D,
            0,
            x, y,
            width, height,
            GLES20.GL_RGBA,
            GLES20.GL_UNSIGNED_BYTE,
            buffer
        )
        
        GLES20.glGenerateMipmap(GLES20.GL_TEXTURE_2D)
        
        unbind()
    }
    
    /**
     * Delete texture
     */
    fun delete() {
        if (deleted) return
        
        val textureIds = intArrayOf(textureId)
        GLES20.glDeleteTextures(1, textureIds, 0)
        
        activeTextures.remove(textureId)
        deleted = true
    }
    
    /**
     * Set texture parameters
     */
    fun setParameters(
        minFilter: Int = GLES20.GL_LINEAR_MIPMAP_LINEAR,
        magFilter: Int = GLES20.GL_LINEAR,
        wrapS: Int = GLES20.GL_REPEAT,
        wrapT: Int = GLES20.GL_REPEAT
    ) {
        bind()
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, minFilter)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, magFilter)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, wrapS)
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, wrapT)
        unbind()
    }
    
    /**
     * Get memory size estimate (bytes)
     */
    fun getMemorySize(): Int {
        // RGBA8 = 4 bytes per pixel, with mipmaps ~1.33x
        return (width * height * 4 * 1.33).toInt()
    }
    
    override fun toString(): String {
        return "GLTexture(id=$textureId, ${width}x$height, deleted=$deleted)"
    }
}
