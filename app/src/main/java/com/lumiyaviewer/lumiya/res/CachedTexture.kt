package com.lumiyaviewer.lumiya.res

/**
 * Represents a cached texture with memory tracking capabilities
 */
class CachedTexture {
    private Int textureId
    private Int width
    private Int height
    private Int format
    private var released: Boolean = false
    
    constructor(textureId: Int, width: Int, height: Int, format: Int) {
        this.textureId = textureId
        this.width = width
        this.height = height
        this.format = format
    }
    
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
    
    fun isReleased(): Boolean {
        return released
    }
    
    fun release(): Unit {
        released = true
        // In a real implementation, this would free GPU resources
    }
    
    override fun toString(): String {
        return "CachedTexture{" +
                "id=" + textureId +
                ", size=" + width + "x" + height +
                ", format=0x" + Int.toHexString(format) +
                ", estimated=" + getEstimatedSize() + " bytes" +
                "}";
    }
}
