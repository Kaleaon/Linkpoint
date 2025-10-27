package com.linkpoint.res

/**
 * Represents a cached texture with memory tracking capabilities
 */
class CachedTexture {
    private val Int textureId
    private val Int width
    private val Int height
    private val Int format
    private Boolean released = false
    
    public CachedTexture(Int textureId, Int width, Int height, Int format) {
        this.textureId = textureId
        this.width = width
        this.height = height
        this.format = format
    }
    
     public fun getTextureId(): Int {
        return textureId
    }
    
     public fun getWidth(): Int {
        return width
    }
    
     public fun getHeight(): Int {
        return height
    }
    
     public fun getFormat(): Int {
        return format
    }
    
    /**
     * Estimate memory usage based on texture dimensions and format
     */
     public fun getEstimatedSize(): Int {
        Int bytesPerPixel
        switch (format) {
            case 0x1907: // GL_RGB
                bytesPerPixel = 3
                break
            case 0x1908: // GL_RGBA
                bytesPerPixel = 4
                break
            case 0x190A: // GL_LUMINANCE
                bytesPerPixel = 1
                break
            default:
                bytesPerPixel = 4; // Default to RGBA
        }
        return width * height * bytesPerPixel
    }
    
     public fun isReleased(): Boolean {
        return released
    }
    
    fun release() {
        released = true
        // In a real implementation, this would free GPU resources
    }
    
    override String toString() {
        return "CachedTexture{" +
                "id=" + textureId +
                ", size=" + width + "x" + height +
                ", format=0x" + Integer.toHexString(format) +
                ", estimated=" + getEstimatedSize() + " bytes" +
                "}"
    }
}