/*
 * Second Life Texture Processing - Java implementation of texture stream handling
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.assets;

import lindenlab.llsd.LLSDException;

import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;

/**
 * A utility class for processing and managing Second Life texture assets.
 * <p>
 * This class provides a suite of static methods for handling texture data within
 * the Second Life ecosystem. Its responsibilities include:
 * <ul>
 *   <li>Detecting various texture formats (J2C, TGA, PNG, etc.).</li>
 *   <li>Parsing texture headers to extract metadata like dimensions and alpha channels.</li>
 *   <li>Validating textures against Second Life's constraints (e.g., power-of-two dimensions).</li>
 *   <li>Converting textures between different formats using Java's ImageIO.</li>
 *   <li>Caching processed texture information to improve performance.</li>
 * </ul>
 * As a utility class, it is final and cannot be instantiated.
 */
public final class SLTextureProcessor {
    
    private static final int MAX_TEXTURE_SIZE = 1024;
    private static final int MIN_TEXTURE_SIZE = 64;
    private static final String[] SUPPORTED_FORMATS = {"j2c", "tga", "jpeg", "jpg", "png", "bmp"};
    
    // Cache for processed textures
    private static final Map<UUID, TextureCache> textureCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_MS = 30 * 60 * 1000; // 30 minutes
    
    private SLTextureProcessor() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of the texture formats supported by the processor.
     */
    public enum TextureFormat {
        J2C("j2c", "image/x-j2c"),
        TGA("tga", "image/tga"),
        JPEG("jpg", "image/jpeg"),
        PNG("png", "image/png"),
        BMP("bmp", "image/bmp"),
        UNKNOWN("", "application/octet-stream");
        
        private final String extension;
        private final String mimeType;
        
        TextureFormat(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }
        
        public String getExtension() { return extension; }
        public String getMimeType() { return mimeType; }
        
        public static TextureFormat fromExtension(String ext) {
            if (ext == null) return UNKNOWN;
            String normalized = ext.toLowerCase();
            for (TextureFormat format : values()) {
                if (format.extension.equals(normalized)) {
                    return format;
                }
            }
            return UNKNOWN;
        }
    }
    
    /**
     * A container for metadata extracted from a texture asset.
     * <p>
     * This class holds information such as the texture's format, dimensions,
     * mipmap levels, and whether it has an alpha channel. It also provides a
     * method to check if the texture conforms to Second Life's technical constraints.
     */
    public static class TextureInfo {
        private final UUID textureId;
        private final TextureFormat format;
        private final int width;
        private final int height;
        private final int levels;
        private final long size;
        private final boolean hasAlpha;
        
        public TextureInfo(UUID textureId, TextureFormat format, int width, int height, 
                          int levels, long size, boolean hasAlpha) {
            this.textureId = textureId;
            this.format = format;
            this.width = width;
            this.height = height;
            this.levels = levels;
            this.size = size;
            this.hasAlpha = hasAlpha;
        }
        
        // Getters
        public UUID getTextureId() { return textureId; }
        public TextureFormat getFormat() { return format; }
        public int getWidth() { return width; }
        public int getHeight() { return height; }
        public int getLevels() { return levels; }
        public long getSize() { return size; }
        public boolean hasAlpha() { return hasAlpha; }
        
        public boolean isValid() {
            return textureId != null && format != TextureFormat.UNKNOWN &&
                   width >= MIN_TEXTURE_SIZE && width <= MAX_TEXTURE_SIZE &&
                   height >= MIN_TEXTURE_SIZE && height <= MAX_TEXTURE_SIZE &&
                   isPowerOfTwo(width) && isPowerOfTwo(height);
        }
        
        private boolean isPowerOfTwo(int value) {
            return value > 0 && (value & (value - 1)) == 0;
        }
    }
    
    /**
     * Texture cache entry.
     */
    private static class TextureCache {
        private final byte[] data;
        private final TextureInfo info;
        private final long timestamp;
        
        public TextureCache(byte[] data, TextureInfo info) {
            this.data = data.clone();
            this.info = info;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
        
        public byte[] getData() { return data.clone(); }
        public TextureInfo getInfo() { return info; }
    }
    
    /**
     * Processes raw texture data to extract metadata and validate it.
     * <p>
     * This is the main entry point for texture processing. It checks a cache for
     * previously processed data. If not found, it delegates to a format-specific
     * processing method to parse the image data and extract information. The
     * results are then cached.
     *
     * @param textureId The UUID of the texture asset.
     * @param data      The raw binary data of the texture.
     * @param format    The expected format of the texture data. If {@link TextureFormat#UNKNOWN},
     *                  the format will be auto-detected.
     * @return A {@link TextureInfo} object containing the extracted metadata.
     * @throws LLSDException if the texture data is invalid or processing fails.
     */
    public static TextureInfo processTexture(UUID textureId, byte[] data, TextureFormat format)
            throws LLSDException {
        if (textureId == null || data == null || data.length == 0) {
            throw new LLSDException("Invalid texture data");
        }
        
        // Check cache first
        TextureCache cached = textureCache.get(textureId);
        if (cached != null && !cached.isExpired()) {
            return cached.getInfo();
        }
        
        TextureInfo info;
        try {
            switch (format) {
                case J2C:
                    info = processJ2CTexture(textureId, data);
                    break;
                case TGA:
                    info = processTGATexture(textureId, data);
                    break;
                case JPEG:
                    info = processJPEGTexture(textureId, data);
                    break;
                case PNG:
                    info = processPNGTexture(textureId, data);
                    break;
                case BMP:
                    info = processBMPTexture(textureId, data);
                    break;
                default:
                    info = detectAndProcessTexture(textureId, data);
                    break;
            }
            
            // Cache the processed texture
            textureCache.put(textureId, new TextureCache(data, info));
            
            // Clean expired entries
            cleanExpiredCache();
            
            return info;
        } catch (Exception e) {
            throw new LLSDException("Texture processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process JPEG2000 texture data.
     */
    private static TextureInfo processJ2CTexture(UUID textureId, byte[] data) throws IOException {
        // Basic J2C header validation
        if (data.length < 12) {
            throw new IOException("Invalid J2C data - too short");
        }
        
        // Check for J2C signature
        if (data[0] != (byte)0xFF || data[1] != (byte)0x4F) {
            throw new IOException("Invalid J2C signature");
        }
        
        // Extract basic dimensions (simplified - full J2C parsing would be more complex)
        int width = extractJ2CWidth(data);
        int height = extractJ2CHeight(data);
        int levels = calculateMipLevels(width, height);
        
        return new TextureInfo(textureId, TextureFormat.J2C, width, height, 
                              levels, data.length, true);
    }
    
    /**
     * Process TGA texture data.
     */
    private static TextureInfo processTGATexture(UUID textureId, byte[] data) throws IOException {
        if (data.length < 18) {
            throw new IOException("Invalid TGA data - too short");
        }
        
        // Extract TGA header information
        int width = (data[12] & 0xFF) | ((data[13] & 0xFF) << 8);
        int height = (data[14] & 0xFF) | ((data[15] & 0xFF) << 8);
        int bpp = data[16] & 0xFF;
        boolean hasAlpha = bpp == 32;
        
        if (width <= 0 || height <= 0) {
            throw new IOException("Invalid TGA dimensions");
        }
        
        int levels = calculateMipLevels(width, height);
        return new TextureInfo(textureId, TextureFormat.TGA, width, height, 
                              levels, data.length, hasAlpha);
    }
    
    /**
     * Process standard image formats using Java ImageIO.
     */
    private static TextureInfo processJPEGTexture(UUID textureId, byte[] data) throws IOException {
        return processStandardImage(textureId, data, TextureFormat.JPEG);
    }
    
    private static TextureInfo processPNGTexture(UUID textureId, byte[] data) throws IOException {
        return processStandardImage(textureId, data, TextureFormat.PNG);
    }
    
    private static TextureInfo processBMPTexture(UUID textureId, byte[] data) throws IOException {
        return processStandardImage(textureId, data, TextureFormat.BMP);
    }
    
    private static TextureInfo processStandardImage(UUID textureId, byte[] data, TextureFormat format) 
            throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new IOException("Failed to decode " + format.name() + " image");
            }
            
            int width = image.getWidth();
            int height = image.getHeight();
            boolean hasAlpha = image.getColorModel().hasAlpha();
            int levels = calculateMipLevels(width, height);
            
            return new TextureInfo(textureId, format, width, height, 
                                  levels, data.length, hasAlpha);
        }
    }
    
    /**
     * Auto-detect texture format and process.
     */
    private static TextureInfo detectAndProcessTexture(UUID textureId, byte[] data) throws IOException {
        TextureFormat format = detectTextureFormat(data);
        if (format == TextureFormat.UNKNOWN) {
            throw new IOException("Unsupported texture format");
        }
        
        switch (format) {
            case J2C:
                return processJ2CTexture(textureId, data);
            case TGA:
                return processTGATexture(textureId, data);
            default:
                return processStandardImage(textureId, data, format);
        }
    }
    
    /**
     * Detects the texture format of a byte array by inspecting its header (magic numbers).
     *
     * @param data The image data to be analyzed.
     * @return The detected {@link TextureFormat} (J2C, TGA, PNG, etc.), or
     *         {@link TextureFormat#UNKNOWN} if the format cannot be determined.
     */
    public static TextureFormat detectTextureFormat(byte[] data) {
        if (data == null || data.length < 4) {
            return TextureFormat.UNKNOWN;
        }
        
        // Check JPEG2000
        if (data[0] == (byte)0xFF && data[1] == (byte)0x4F) {
            return TextureFormat.J2C;
        }
        
        // Check JPEG
        if (data[0] == (byte)0xFF && data[1] == (byte)0xD8) {
            return TextureFormat.JPEG;
        }
        
        // Check PNG
        if (data[0] == (byte)0x89 && data[1] == 'P' && data[2] == 'N' && data[3] == 'G') {
            return TextureFormat.PNG;
        }
        
        // Check BMP
        if (data[0] == 'B' && data[1] == 'M') {
            return TextureFormat.BMP;
        }
        
        // Check TGA (more complex detection needed)
        if (data.length >= 18) {
            int imageType = data[2] & 0xFF;
            if (imageType == 2 || imageType == 10) { // Uncompressed or RLE RGB
                return TextureFormat.TGA;
            }
        }
        
        return TextureFormat.UNKNOWN;
    }
    
    /**
     * Converts texture data from a source format to a target format.
     * <p>
     * This method uses Java's ImageIO library to perform the conversion. It is
     * not capable of converting to or from proprietary formats like J2C or TGA
     * without additional libraries.
     *
     * @param sourceData   The source texture data to convert.
     * @param sourceFormat The format of the source data.
     * @param targetFormat The desired target format.
     * @return A new byte array containing the texture data in the target format.
     * @throws IOException if the conversion fails or a format is unsupported by ImageIO.
     */
    public static byte[] convertTexture(byte[] sourceData, TextureFormat sourceFormat,
                                       TextureFormat targetFormat) throws IOException {
        if (sourceFormat == targetFormat) {
            return sourceData.clone();
        }
        
        // Load source image
        BufferedImage image;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(sourceData)) {
            image = ImageIO.read(bis);
            if (image == null) {
                throw new IOException("Failed to load source image");
            }
        }
        
        // Convert to target format
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            String formatName = getImageIOFormat(targetFormat);
            if (formatName == null) {
                throw new IOException("Unsupported target format: " + targetFormat);
            }
            
            ImageIO.write(image, formatName, bos);
            return bos.toByteArray();
        }
    }
    
    private static String getImageIOFormat(TextureFormat format) {
        switch (format) {
            case JPEG:
                return "jpg";
            case PNG:
                return "png";
            case BMP:
                return "bmp";
            default:
                return null;
        }
    }
    
    /**
     * Creates a standard LLSD map structure for a texture stream.
     * <p>
     * This method packages the texture's metadata and its raw data into a single
     * LLSD map, which can then be serialized for transmission or storage.
     *
     * @param textureId The UUID of the texture asset.
     * @param info      The {@link TextureInfo} object containing the texture's metadata.
     * @param data      The raw binary data of the texture.
     * @return A {@link Map} representing the texture stream.
     */
    public static Map<String, Object> createTextureStreamData(UUID textureId,
                                                              TextureInfo info,
                                                              byte[] data) {
        Map<String, Object> streamData = new HashMap<>();
        streamData.put("AssetID", textureId);
        streamData.put("AssetType", SLAssetType.TEXTURE_STREAM);
        streamData.put("Format", info.getFormat().name());
        streamData.put("Width", info.getWidth());
        streamData.put("Height", info.getHeight());
        streamData.put("Levels", info.getLevels());
        streamData.put("HasAlpha", info.hasAlpha());
        streamData.put("Size", info.getSize());
        streamData.put("Data", data);
        streamData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return streamData;
    }
    
    /**
     * Validates if given dimensions are valid for a Second Life texture.
     * <p>
     * SL textures must have dimensions that are powers of two and fall within
     * a specific range (e.g., 64x64 to 1024x1024).
     *
     * @param width  The width of the texture.
     * @param height The height of the texture.
     * @return {@code true} if the dimensions are valid, {@code false} otherwise.
     */
    public static boolean isValidSLTextureDimensions(int width, int height) {
        return width >= MIN_TEXTURE_SIZE && width <= MAX_TEXTURE_SIZE &&
               height >= MIN_TEXTURE_SIZE && height <= MAX_TEXTURE_SIZE &&
               isPowerOfTwo(width) && isPowerOfTwo(height);
    }
    
    /**
     * Calculate mip levels for a texture.
     */
    private static int calculateMipLevels(int width, int height) {
        int maxDimension = Math.max(width, height);
        return (int) (Math.log(maxDimension) / Math.log(2)) + 1;
    }
    
    /**
     * Extract width from J2C data (simplified).
     */
    private static int extractJ2CWidth(byte[] data) {
        // Simplified J2C width extraction - in a real implementation,
        // you'd need to parse the full J2C codestream
        return 512; // Default assumption
    }
    
    /**
     * Extract height from J2C data (simplified).
     */
    private static int extractJ2CHeight(byte[] data) {
        // Simplified J2C height extraction - in a real implementation,
        // you'd need to parse the full J2C codestream
        return 512; // Default assumption
    }
    
    private static boolean isPowerOfTwo(int value) {
        return value > 0 && (value & (value - 1)) == 0;
    }
    
    /**
     * Clean expired cache entries.
     */
    private static void cleanExpiredCache() {
        textureCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Clears all entries from the internal texture cache.
     */
    public static void clearCache() {
        textureCache.clear();
    }
    
    /**
     * Gets statistics about the current state of the texture cache.
     *
     * @return A map containing statistics such as the number of cached items
     *         and the total size of cached data.
     */
    public static Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("CacheSize", textureCache.size());
        
        long totalSize = textureCache.values().stream()
                .mapToLong(cache -> cache.getData().length)
                .sum();
        stats.put("TotalCacheSize", totalSize);
        
        return stats;
    }
}