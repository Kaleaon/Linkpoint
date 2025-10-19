/*
 * Second Life Data Stream Processing - Java implementation of general asset streaming
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.assets;

import lindenlab.llsd.LLSDException;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.*;

/**
 * A utility class for processing and managing Second Life data streams and assets.
 * <p>
 * This class provides a comprehensive suite of static methods for handling
 * binary asset data within the Second Life ecosystem. Its responsibilities include:
 * <ul>
 *   <li>Compressing and decompressing asset data using various algorithms (Gzip, Deflate).</li>
 *   <li>Calculating checksums for data integrity validation.</li>
 *   <li>Splitting large assets into smaller, manageable chunks for streaming.</li>
 *   <li>Reconstructing assets from a list of stream chunks.</li>
 *   <li>Caching processed asset data to improve performance.</li>
 * </ul>
 * As a utility class, it is final and cannot be instantiated.
 */
public final class SLDataStreamProcessor {
    
    private static final int MAX_ASSET_SIZE = 100 * 1024 * 1024; // 100MB
    private static final int CHUNK_SIZE = 64 * 1024; // 64KB chunks
    private static final int COMPRESSION_THRESHOLD = 1024; // 1KB minimum for compression
    
    // Cache for processed data streams
    private static final Map<UUID, DataCache> dataCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_MS = 60 * 60 * 1000; // 1 hour
    
    private SLDataStreamProcessor() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of the supported compression types for data streams.
     */
    public enum CompressionType {
        NONE(0, "none"),
        GZIP(1, "gzip"),
        DEFLATE(2, "deflate"),
        LZ4(3, "lz4");
        
        private final int value;
        private final String name;
        
        CompressionType(int value, String name) {
            this.value = value;
            this.name = name;
        }
        
        public int getValue() { return value; }
        public String getName() { return name; }
        
        public static CompressionType fromValue(int value) {
            for (CompressionType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return NONE;
        }
    }
    
    /**
     * Represents a single chunk of a data stream.
     * <p>
     * A large asset is typically split into multiple chunks for transfer. Each
     * chunk contains a portion of the data, its index in the sequence, and a
     * checksum for validation.
     */
    public static class StreamChunk {
        private final int chunkIndex;
        private final byte[] data;
        private final String checksum;
        private final boolean isCompressed;
        
        public StreamChunk(int chunkIndex, byte[] data, String checksum, boolean isCompressed) {
            this.chunkIndex = chunkIndex;
            this.data = data.clone();
            this.checksum = checksum;
            this.isCompressed = isCompressed;
        }
        
        public int getChunkIndex() { return chunkIndex; }
        public byte[] getData() { return data.clone(); }
        public String getChecksum() { return checksum; }
        public boolean isCompressed() { return isCompressed; }
        
        public boolean isValid() {
            return chunkIndex >= 0 && data != null && data.length > 0 &&
                   checksum != null && checksum.equals(calculateChecksum(data));
        }
    }
    
    /**
     * A container for metadata about a processed data stream.
     * <p>
     * This class holds information such as the asset's ID and type, its original
     * and compressed sizes, the compression method used, and an overall checksum.
     */
    public static class DataStreamInfo {
        private final UUID assetId;
        private final int assetType;
        private final long totalSize;
        private final long compressedSize;
        private final CompressionType compression;
        private final int chunkCount;
        private final String overallChecksum;
        private final Map<String, Object> metadata;
        
        public DataStreamInfo(UUID assetId, int assetType, long totalSize, long compressedSize,
                             CompressionType compression, int chunkCount, String overallChecksum,
                             Map<String, Object> metadata) {
            this.assetId = assetId;
            this.assetType = assetType;
            this.totalSize = totalSize;
            this.compressedSize = compressedSize;
            this.compression = compression;
            this.chunkCount = chunkCount;
            this.overallChecksum = overallChecksum;
            this.metadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
        }
        
        // Getters
        public UUID getAssetId() { return assetId; }
        public int getAssetType() { return assetType; }
        public long getTotalSize() { return totalSize; }
        public long getCompressedSize() { return compressedSize; }
        public CompressionType getCompression() { return compression; }
        public int getChunkCount() { return chunkCount; }
        public String getOverallChecksum() { return overallChecksum; }
        public Map<String, Object> getMetadata() { return new HashMap<>(metadata); }
        
        public boolean isValid() {
            return assetId != null && totalSize > 0 && totalSize <= MAX_ASSET_SIZE &&
                   chunkCount > 0 && overallChecksum != null;
        }
        
        public double getCompressionRatio() {
            return compression == CompressionType.NONE ? 1.0 : 
                   (double) compressedSize / totalSize;
        }
    }
    
    /**
     * Data cache entry.
     */
    private static class DataCache {
        private final byte[] data;
        private final DataStreamInfo info;
        private final long timestamp;
        
        public DataCache(byte[] data, DataStreamInfo info) {
            this.data = data.clone();
            this.info = info;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
        
        public byte[] getData() { return data.clone(); }
        public DataStreamInfo getInfo() { return info; }
    }
    
    /**
     * Processes raw asset data to prepare it for streaming.
     * <p>
     * This method takes raw asset data, optionally compresses it, calculates
     * checksums, and generates metadata about the stream. The resulting
     * {@link DataStreamInfo} can be used to manage the asset transfer. Processed
     * data is cached to avoid redundant computation.
     *
     * @param assetId           The UUID of the asset being processed.
     * @param assetType         The type code of the asset.
     * @param data              The raw binary data of the asset.
     * @param enableCompression If {@code true}, the data will be compressed if it
     *                          meets the compression criteria.
     * @return A {@link DataStreamInfo} object containing metadata about the processed stream.
     * @throws LLSDException if the input parameters are invalid or if processing fails.
     */
    public static DataStreamInfo processDataStream(UUID assetId, int assetType, byte[] data,
                                                  boolean enableCompression) throws LLSDException {
        if (assetId == null || data == null || data.length == 0) {
            throw new LLSDException("Invalid data stream parameters");
        }
        
        if (data.length > MAX_ASSET_SIZE) {
            throw new LLSDException("Asset too large: " + data.length + " bytes");
        }
        
        // Check cache first
        DataCache cached = dataCache.get(assetId);
        if (cached != null && !cached.isExpired()) {
            return cached.getInfo();
        }
        
        try {
            // Determine compression
            CompressionType compression = CompressionType.NONE;
            byte[] processedData = data;
            
            if (enableCompression && data.length >= COMPRESSION_THRESHOLD) {
                byte[] compressed = compressData(data, CompressionType.GZIP);
                if (compressed.length < data.length * 0.9) { // Only if significant compression
                    processedData = compressed;
                    compression = CompressionType.GZIP;
                }
            }
            
            // Calculate checksums
            String overallChecksum = calculateChecksum(data);
            
            // Calculate chunks
            int chunkCount = (int) Math.ceil((double) processedData.length / CHUNK_SIZE);
            
            // Create metadata
            Map<String, Object> metadata = createAssetMetadata(assetType, data);
            
            // Create stream info
            DataStreamInfo info = new DataStreamInfo(
                assetId, assetType, data.length, processedData.length,
                compression, chunkCount, overallChecksum, metadata
            );
            
            // Cache the processed data
            dataCache.put(assetId, new DataCache(processedData, info));
            
            // Clean expired entries
            cleanExpiredCache();
            
            return info;
            
        } catch (Exception e) {
            throw new LLSDException("Data stream processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Splits a byte array into a list of {@link StreamChunk} objects.
     * <p>
     * Each chunk is created with a fixed maximum size. If compression is enabled,
     * each chunk is compressed individually.
     *
     * @param data        The data to be split into chunks.
     * @param compression The compression type to apply to each chunk.
     * @return A list of {@link StreamChunk} objects.
     * @throws IOException if an error occurs during compression.
     */
    public static List<StreamChunk> createStreamChunks(byte[] data, CompressionType compression)
            throws IOException {
        List<StreamChunk> chunks = new ArrayList<>();
        
        for (int i = 0; i < data.length; i += CHUNK_SIZE) {
            int chunkSize = Math.min(CHUNK_SIZE, data.length - i);
            byte[] chunkData = Arrays.copyOfRange(data, i, i + chunkSize);
            
            // Compress chunk if needed
            boolean isCompressed = false;
            if (compression != CompressionType.NONE) {
                byte[] compressed = compressData(chunkData, compression);
                if (compressed.length < chunkData.length) {
                    chunkData = compressed;
                    isCompressed = true;
                }
            }
            
            String checksum = calculateChecksum(chunkData);
            chunks.add(new StreamChunk(i / CHUNK_SIZE, chunkData, checksum, isCompressed));
        }
        
        return chunks;
    }
    
    /**
     * Reconstructs the original data from a list of {@link StreamChunk} objects.
     * <p>
     * The chunks are first sorted by their index, then validated, decompressed
     * if necessary, and concatenated to form the original data.
     *
     * @param chunks      The list of chunks to reconstruct from.
     * @param compression The compression type that was used for the chunks.
     * @return The reconstructed byte array.
     * @throws IOException if a chunk is invalid or if decompression fails.
     */
    public static byte[] reconstructFromChunks(List<StreamChunk> chunks, CompressionType compression)
            throws IOException {
        // Sort chunks by index
        chunks.sort(Comparator.comparingInt(StreamChunk::getChunkIndex));
        
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        
        for (StreamChunk chunk : chunks) {
            if (!chunk.isValid()) {
                throw new IOException("Invalid chunk: " + chunk.getChunkIndex());
            }
            
            byte[] chunkData = chunk.getData();
            
            // Decompress chunk if needed
            if (chunk.isCompressed() && compression != CompressionType.NONE) {
                chunkData = decompressData(chunkData, compression);
            }
            
            result.write(chunkData);
        }
        
        return result.toByteArray();
    }
    
    /**
     * Compresses a byte array using the specified compression algorithm.
     *
     * @param data        The data to compress.
     * @param compression The compression algorithm to use (e.g., GZIP, DEFLATE).
     * @return The compressed data. Returns a clone of the original if the
     *         compression type is NONE.
     * @throws IOException if a compression error occurs.
     */
    public static byte[] compressData(byte[] data, CompressionType compression) throws IOException {
        switch (compression) {
            case GZIP:
                return compressGzip(data);
            case DEFLATE:
                return compressDeflate(data);
            default:
                return data.clone();
        }
    }
    
    /**
     * Decompresses a byte array using the specified compression algorithm.
     *
     * @param data        The data to decompress.
     * @param compression The algorithm used for compression.
     * @return The decompressed data.
     * @throws IOException if a decompression error occurs.
     */
    public static byte[] decompressData(byte[] data, CompressionType compression) throws IOException {
        switch (compression) {
            case GZIP:
                return decompressGzip(data);
            case DEFLATE:
                return decompressDeflate(data);
            default:
                return data.clone();
        }
    }
    
    /**
     * Compress data using GZIP.
     */
    private static byte[] compressGzip(byte[] data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream gzos = new GZIPOutputStream(bos)) {
            gzos.write(data);
            gzos.finish();
            return bos.toByteArray();
        }
    }
    
    /**
     * Decompress GZIP data.
     */
    private static byte[] decompressGzip(byte[] data) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             GZIPInputStream gzis = new GZIPInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int len;
            while ((len = gzis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }
    }
    
    /**
     * Compress data using Deflate.
     */
    private static byte[] compressDeflate(byte[] data) throws IOException {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             DeflaterOutputStream dos = new DeflaterOutputStream(bos)) {
            dos.write(data);
            dos.finish();
            return bos.toByteArray();
        }
    }
    
    /**
     * Decompress Deflate data.
     */
    private static byte[] decompressDeflate(byte[] data) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data);
             InflaterInputStream iis = new InflaterInputStream(bis);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            
            byte[] buffer = new byte[8192];
            int len;
            while ((len = iis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        }
    }
    
    /**
     * Calculates the SHA-256 checksum of a byte array.
     *
     * @param data The data to be hashed.
     * @return A hex string representation of the SHA-256 checksum.
     * @throws RuntimeException if the SHA-256 algorithm is not available.
     */
    public static String calculateChecksum(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data);
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
    
    /**
     * Validates the integrity of a byte array by comparing its calculated
     * checksum against an expected checksum.
     *
     * @param data             The data to validate.
     * @param expectedChecksum The expected SHA-256 checksum as a hex string.
     * @return {@code true} if the calculated checksum matches the expected one,
     *         {@code false} otherwise.
     */
    public static boolean validateDataIntegrity(byte[] data, String expectedChecksum) {
        String actualChecksum = calculateChecksum(data);
        return actualChecksum.equals(expectedChecksum);
    }
    
    /**
     * Create asset-specific metadata.
     */
    private static Map<String, Object> createAssetMetadata(int assetType, byte[] data) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("OriginalSize", data.length);
        metadata.put("AssetType", assetType);
        metadata.put("ProcessedAt", System.currentTimeMillis() / 1000.0);
        
        // Add type-specific metadata
        if (SLAssetType.isTextureType(assetType)) {
            metadata.put("Category", "Texture");
        } else if (SLAssetType.isSoundType(assetType)) {
            metadata.put("Category", "Audio");
        } else {
            metadata.put("Category", "Data");
        }
        
        return metadata;
    }
    
    /**
     * Creates the final LLSD map structure for a data stream.
     * <p>
     * This method packages the stream's metadata and a summary of its chunks
     * into a single LLSD map, which can then be serialized and transmitted.
     *
     * @param info   The {@link DataStreamInfo} containing the stream's metadata.
     * @param chunks The list of {@link StreamChunk} objects that make up the stream.
     * @return A {@link Map} representing the complete data stream structure.
     */
    public static Map<String, Object> createDataStreamLLSD(DataStreamInfo info, List<StreamChunk> chunks) {
        Map<String, Object> streamData = new HashMap<>();
        streamData.put("AssetID", info.getAssetId());
        streamData.put("AssetType", info.getAssetType());
        streamData.put("TotalSize", info.getTotalSize());
        streamData.put("CompressedSize", info.getCompressedSize());
        streamData.put("Compression", info.getCompression().getName());
        streamData.put("ChunkCount", info.getChunkCount());
        streamData.put("Checksum", info.getOverallChecksum());
        streamData.put("Metadata", info.getMetadata());
        
        // Add chunk information
        List<Map<String, Object>> chunkList = new ArrayList<>();
        for (StreamChunk chunk : chunks) {
            Map<String, Object> chunkData = new HashMap<>();
            chunkData.put("Index", chunk.getChunkIndex());
            chunkData.put("Size", chunk.getData().length);
            chunkData.put("Checksum", chunk.getChecksum());
            chunkData.put("Compressed", chunk.isCompressed());
            chunkList.add(chunkData);
        }
        streamData.put("Chunks", chunkList);
        
        streamData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return streamData;
    }
    
    /**
     * Clean expired cache entries.
     */
    private static void cleanExpiredCache() {
        dataCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Clears all entries from the internal data cache.
     */
    public static void clearCache() {
        dataCache.clear();
    }
    
    /**
     * Gets statistics about the current state of the data cache.
     *
     * @return A map containing statistics such as the number of cached items
     *         and the total size of cached data.
     */
    public static Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("CacheSize", dataCache.size());
        
        long totalSize = dataCache.values().stream()
                .mapToLong(cache -> cache.getData().length)
                .sum();
        stats.put("TotalCacheSize", totalSize);
        
        long totalOriginalSize = dataCache.values().stream()
                .mapToLong(cache -> cache.getInfo().getTotalSize())
                .sum();
        stats.put("TotalOriginalSize", totalOriginalSize);
        
        double compressionRatio = totalSize > 0 ? (double) totalSize / totalOriginalSize : 1.0;
        stats.put("OverallCompressionRatio", compressionRatio);
        
        return stats;
    }
}