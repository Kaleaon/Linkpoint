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
    
    public Int getTextureId() {
        return textureId
    }
    
    public Int getWidth() {
        return width
    }
    
    public Int getHeight() {
        return height
    }
    
    public Int getFormat() {
        return format
    }
    
    /**
     * Estimate memory usage based on texture dimensions and format
     */
    public Int getEstimatedSize() {
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
    
    public Boolean isReleased() {
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