package com.lumiyaviewer.lumiya.res

/**
 * Represents a cached texture with memory tracking capabilities
 */
class CachedTexture(
    private val textureId: Int,
    private val width: Int,
    private val height: Int,
    private val format: Int
) {
    private var released: Boolean = false
    
    fun getTextureId(): Int {
        return textureId
    }
    
    fun getWidth(): Int {
        return width
    }
    
    fun getHeight(): Int {
        return height
    }
    
    fun getFormat(): Int {
        return format
    }
    
    /**
     * Estimate memory usage based on texture dimensions and format
     */
    fun getEstimatedSize(): Int {
        val bytesPerPixel: Int = when (format) {
            0x1907 -> 3 // GL_RGB
            0x1908 -> 4 // GL_RGBA
            0x190A -> 1 // GL_LUMINANCE
            else -> 4 // Default to RGBA
        }
        return width * height * bytesPerPixel
    }
    
    fun isReleased(): Boolean {
        return released
    }
    
    fun release() {
        released = true
        // In a real implementation, this would free GPU resources
    }
    
    override fun toString(): String {
        return "CachedTexture{" +
                "id=" + textureId +
                ", size=" + width + "x" + height +
                ", format=0x" + Integer.toHexString(format) +
                ", estimated=" + getEstimatedSize() + " bytes" +
                "}"
    }
}
