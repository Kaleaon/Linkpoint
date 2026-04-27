package com.lumiyaviewer.lumiya.res;

/**
 * Represents a cached texture with memory tracking capabilities
 */
public class CachedTexture {
    private final int textureId;
    private final int width;
    private final int height;
    private final int format;
    private boolean released = false;
    
    public CachedTexture(int textureId, int width, int height, int format) {
        this.textureId = textureId;
        this.width = width;
        this.height = height;
        this.format = format;
    }
    
    public int getTextureId() {
        return textureId;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getFormat() {
        return format;
    }
    
    /**
     * Estimate memory usage based on texture dimensions and format
     */
    public int getEstimatedSize() {
        int bytesPerPixel;
        switch (format) {
            case 0x1907: // GL_RGB
                bytesPerPixel = 3;
                break;
            case 0x1908: // GL_RGBA
                bytesPerPixel = 4;
                break;
            case 0x190A: // GL_LUMINANCE
                bytesPerPixel = 1;
                break;
            default:
                bytesPerPixel = 4; // Default to RGBA
        }
        return width * height * bytesPerPixel;
    }
    
    public boolean isReleased() {
        return released;
    }
    
    public void release() {
        released = true;
        // In a real implementation, this would free GPU resources
    }
    
    @Override
    public String toString() {
        return "CachedTexture{" +
                "id=" + textureId +
                ", size=" + width + "x" + height +
                ", format=0x" + Integer.toHexString(format) +
                ", estimated=" + getEstimatedSize() + " bytes" +
                "}";
    }
}