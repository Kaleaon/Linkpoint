/*
 * Second Life Sound Processing - Java implementation of audio stream handling
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.assets;

import lindenlab.llsd.LLSDException;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A utility class for processing and managing Second Life sound assets.
 * <p>
 * This class provides a suite of static methods for handling audio data within
 * the Second Life ecosystem. Its responsibilities include:
 * <ul>
 *   <li>Detecting audio formats (WAV, OGG, MP3).</li>
 *   <li>Parsing audio headers to extract metadata like sample rate, channels, and duration.</li>
 *   <li>Validating audio data against Second Life's constraints (e.g., max duration, max size).</li>
 *   <li>Caching processed audio information to improve performance.</li>
 *   <li>Creating standard LLSD structures for sound streams.</li>
 * </ul>
 * As a utility class, it is final and cannot be instantiated.
 */
public final class SLSoundProcessor {
    
    private static final int MAX_SOUND_SIZE = 10 * 1024 * 1024; // 10MB
    private static final int MAX_SOUND_DURATION = 10; // 10 seconds
    private static final String[] SUPPORTED_FORMATS = {"wav", "ogg", "mp3"};
    
    // Cache for processed sounds
    private static final Map<UUID, SoundCache> soundCache = new ConcurrentHashMap<>();
    private static final long CACHE_EXPIRY_MS = 15 * 60 * 1000; // 15 minutes
    
    private SLSoundProcessor() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of the audio formats supported by the sound processor.
     */
    public enum AudioFormat {
        WAV("wav", "audio/wav"),
        OGG("ogg", "audio/ogg"),
        MP3("mp3", "audio/mpeg"),
        UNKNOWN("", "application/octet-stream");
        
        private final String extension;
        private final String mimeType;
        
        AudioFormat(String extension, String mimeType) {
            this.extension = extension;
            this.mimeType = mimeType;
        }
        
        public String getExtension() { return extension; }
        public String getMimeType() { return mimeType; }
        
        public static AudioFormat fromExtension(String ext) {
            if (ext == null) return UNKNOWN;
            String normalized = ext.toLowerCase();
            for (AudioFormat format : values()) {
                if (format.extension.equals(normalized)) {
                    return format;
                }
            }
            return UNKNOWN;
        }
    }
    
    /**
     * A container for metadata extracted from an audio asset.
     * <p>
     * This class holds information such as the sound's format, sample rate,
     * duration, and size. It also provides a method to check if the audio
     * conforms to Second Life's technical constraints.
     */
    public static class AudioInfo {
        private final UUID soundId;
        private final AudioFormat format;
        private final int sampleRate;
        private final int channels;
        private final int bitsPerSample;
        private final double duration;
        private final long size;
        private final boolean isCompressed;
        
        public AudioInfo(UUID soundId, AudioFormat format, int sampleRate, int channels,
                        int bitsPerSample, double duration, long size, boolean isCompressed) {
            this.soundId = soundId;
            this.format = format;
            this.sampleRate = sampleRate;
            this.channels = channels;
            this.bitsPerSample = bitsPerSample;
            this.duration = duration;
            this.size = size;
            this.isCompressed = isCompressed;
        }
        
        // Getters
        public UUID getSoundId() { return soundId; }
        public AudioFormat getFormat() { return format; }
        public int getSampleRate() { return sampleRate; }
        public int getChannels() { return channels; }
        public int getBitsPerSample() { return bitsPerSample; }
        public double getDuration() { return duration; }
        public long getSize() { return size; }
        public boolean isCompressed() { return isCompressed; }
        
        public boolean isValid() {
            return soundId != null && format != AudioFormat.UNKNOWN &&
                   sampleRate > 0 && channels > 0 && bitsPerSample > 0 &&
                   duration > 0 && duration <= MAX_SOUND_DURATION &&
                   size <= MAX_SOUND_SIZE;
        }
    }
    
    /**
     * Sound cache entry.
     */
    private static class SoundCache {
        private final byte[] data;
        private final AudioInfo info;
        private final long timestamp;
        
        public SoundCache(byte[] data, AudioInfo info) {
            this.data = data.clone();
            this.info = info;
            this.timestamp = System.currentTimeMillis();
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() - timestamp > CACHE_EXPIRY_MS;
        }
        
        public byte[] getData() { return data.clone(); }
        public AudioInfo getInfo() { return info; }
    }
    
    /**
     * Processes raw audio data to extract metadata and validate it.
     * <p>
     * This method serves as the main entry point for sound processing. It first
     * checks a cache for previously processed data. If not found, it delegates
     * to a format-specific processing method (e.g., for WAV or OGG) to parse
     * the audio header and extract information. The results are then cached.
     *
     * @param soundId The UUID of the sound asset.
     * @param data    The raw binary data of the sound.
     * @param format  The expected format of the audio data. If {@link AudioFormat#UNKNOWN},
     *                the format will be auto-detected.
     * @return An {@link AudioInfo} object containing the extracted metadata.
     * @throws LLSDException if the audio data is invalid or processing fails.
     */
    public static AudioInfo processSound(UUID soundId, byte[] data, AudioFormat format)
            throws LLSDException {
        if (soundId == null || data == null || data.length == 0) {
            throw new LLSDException("Invalid sound data");
        }
        
        if (data.length > MAX_SOUND_SIZE) {
            throw new LLSDException("Sound data too large: " + data.length + " bytes");
        }
        
        // Check cache first
        SoundCache cached = soundCache.get(soundId);
        if (cached != null && !cached.isExpired()) {
            return cached.getInfo();
        }
        
        AudioInfo info;
        try {
            switch (format) {
                case WAV:
                    info = processWAVSound(soundId, data);
                    break;
                case OGG:
                    info = processOGGSound(soundId, data);
                    break;
                case MP3:
                    info = processMP3Sound(soundId, data);
                    break;
                default:
                    info = detectAndProcessSound(soundId, data);
                    break;
            }
            
            // Cache the processed sound
            soundCache.put(soundId, new SoundCache(data, info));
            
            // Clean expired entries
            cleanExpiredCache();
            
            return info;
        } catch (Exception e) {
            throw new LLSDException("Sound processing failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Process WAV audio data.
     */
    private static AudioInfo processWAVSound(UUID soundId, byte[] data) throws IOException {
        if (data.length < 44) {
            throw new IOException("Invalid WAV data - too short");
        }
        
        // Check WAV header
        if (data[0] != 'R' || data[1] != 'I' || data[2] != 'F' || data[3] != 'F') {
            throw new IOException("Invalid WAV RIFF header");
        }
        
        if (data[8] != 'W' || data[9] != 'A' || data[10] != 'V' || data[11] != 'E') {
            throw new IOException("Invalid WAV format");
        }
        
        // Parse WAV header
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        
        // Skip RIFF header (12 bytes)
        buffer.position(12);
        
        // Find fmt chunk
        while (buffer.remaining() >= 8) {
            int chunkId = buffer.getInt();
            int chunkSize = buffer.getInt();
            
            if (chunkId == 0x20746d66) { // "fmt "
                if (chunkSize < 16) {
                    throw new IOException("Invalid fmt chunk size");
                }
                
                short audioFormat = buffer.getShort();
                short channels = buffer.getShort();
                int sampleRate = buffer.getInt();
                int byteRate = buffer.getInt();
                short blockAlign = buffer.getShort();
                short bitsPerSample = buffer.getShort();
                
                if (audioFormat != 1) { // Only PCM supported
                    throw new IOException("Unsupported WAV audio format: " + audioFormat);
                }
                
                // Calculate duration
                // Find data chunk size
                long dataSize = findWAVDataSize(data, buffer.position());
                double duration = (double) dataSize / byteRate;
                
                return new AudioInfo(soundId, AudioFormat.WAV, sampleRate, channels,
                                   bitsPerSample, duration, data.length, false);
            } else {
                // Skip chunk
                buffer.position(buffer.position() + chunkSize);
            }
        }
        
        throw new IOException("WAV fmt chunk not found");
    }
    
    /**
     * Process OGG audio data.
     */
    private static AudioInfo processOGGSound(UUID soundId, byte[] data) throws IOException {
        if (data.length < 4) {
            throw new IOException("Invalid OGG data - too short");
        }
        
        // Check OGG signature
        if (data[0] != 'O' || data[1] != 'g' || data[2] != 'g' || data[3] != 'S') {
            throw new IOException("Invalid OGG signature");
        }
        
        // Basic OGG processing (simplified - full OGG parsing would be more complex)
        // For now, assume standard values
        int sampleRate = 44100;
        int channels = 2;
        int bitsPerSample = 16;
        double duration = estimateOGGDuration(data.length);
        
        return new AudioInfo(soundId, AudioFormat.OGG, sampleRate, channels,
                           bitsPerSample, duration, data.length, true);
    }
    
    /**
     * Process MP3 audio data.
     */
    private static AudioInfo processMP3Sound(UUID soundId, byte[] data) throws IOException {
        if (data.length < 3) {
            throw new IOException("Invalid MP3 data - too short");
        }
        
        // Check MP3 signature
        if ((data[0] & 0xFF) != 0xFF || (data[1] & 0xE0) != 0xE0) {
            throw new IOException("Invalid MP3 frame header");
        }
        
        // Basic MP3 processing (simplified)
        int sampleRate = 44100;
        int channels = 2;
        int bitsPerSample = 16;
        double duration = estimateMP3Duration(data.length);
        
        return new AudioInfo(soundId, AudioFormat.MP3, sampleRate, channels,
                           bitsPerSample, duration, data.length, true);
    }
    
    /**
     * Auto-detect audio format and process.
     */
    private static AudioInfo detectAndProcessSound(UUID soundId, byte[] data) throws IOException {
        AudioFormat format = detectAudioFormat(data);
        if (format == AudioFormat.UNKNOWN) {
            throw new IOException("Unsupported audio format");
        }
        
        switch (format) {
            case WAV:
                return processWAVSound(soundId, data);
            case OGG:
                return processOGGSound(soundId, data);
            case MP3:
                return processMP3Sound(soundId, data);
            default:
                throw new IOException("Format detection failed");
        }
    }
    
    /**
     * Detects the audio format of a byte array by inspecting its header (magic numbers).
     *
     * @param data The audio data to be analyzed.
     * @return The detected {@link AudioFormat} (WAV, OGG, MP3), or
     *         {@link AudioFormat#UNKNOWN} if the format cannot be determined.
     */
    public static AudioFormat detectAudioFormat(byte[] data) {
        if (data == null || data.length < 4) {
            return AudioFormat.UNKNOWN;
        }
        
        // Check WAV
        if (data[0] == 'R' && data[1] == 'I' && data[2] == 'F' && data[3] == 'F') {
            return AudioFormat.WAV;
        }
        
        // Check OGG
        if (data[0] == 'O' && data[1] == 'g' && data[2] == 'g' && data[3] == 'S') {
            return AudioFormat.OGG;
        }
        
        // Check MP3
        if ((data[0] & 0xFF) == 0xFF && (data[1] & 0xE0) == 0xE0) {
            return AudioFormat.MP3;
        }
        
        return AudioFormat.UNKNOWN;
    }
    
    /**
     * Creates a standard LLSD map structure for a sound stream.
     * <p>
     * This method packages the sound's metadata and its raw data into a single
     * LLSD map, which can then be serialized for transmission or storage.
     *
     * @param soundId The UUID of the sound asset.
     * @param info    The {@link AudioInfo} object containing the sound's metadata.
     * @param data    The raw binary data of the sound.
     * @return A {@link Map} representing the sound stream.
     */
    public static Map<String, Object> createSoundStreamData(UUID soundId,
                                                            AudioInfo info,
                                                            byte[] data) {
        Map<String, Object> streamData = new HashMap<>();
        streamData.put("AssetID", soundId);
        streamData.put("AssetType", SLAssetType.AUDIO_STREAM);
        streamData.put("Format", info.getFormat().name());
        streamData.put("SampleRate", info.getSampleRate());
        streamData.put("Channels", info.getChannels());
        streamData.put("BitsPerSample", info.getBitsPerSample());
        streamData.put("Duration", info.getDuration());
        streamData.put("Size", info.getSize());
        streamData.put("Compressed", info.isCompressed());
        streamData.put("Data", data);
        streamData.put("Timestamp", System.currentTimeMillis() / 1000.0);
        
        return streamData;
    }
    
    /**
     * Validates an {@link AudioInfo} object against Second Life's specific
     * constraints, such as maximum duration and file size.
     *
     * @param info The {@link AudioInfo} object to validate.
     * @return {@code true} if the audio meets all Second Life constraints,
     *         {@code false} otherwise.
     */
    public static boolean isValidSLAudio(AudioInfo info) {
        return info != null && info.isValid() &&
               info.getDuration() <= MAX_SOUND_DURATION &&
               info.getSize() <= MAX_SOUND_SIZE;
    }
    
    /**
     * Converts audio data from a given format to WAV format.
     * <p>
     * <b>Note:</b> This is a placeholder for a full implementation. A real
     * implementation would require a dedicated audio processing library.
     *
     * @param sourceData   The source audio data to convert.
     * @param sourceFormat The format of the source data.
     * @return The converted WAV data.
     * @throws IOException if the conversion is not supported or fails.
     */
    public static byte[] convertToWAV(byte[] sourceData, AudioFormat sourceFormat) throws IOException {
        if (sourceFormat == AudioFormat.WAV) {
            return sourceData.clone();
        }
        
        // For now, just return original data
        // In a full implementation, you'd use audio processing libraries
        // like JavaSound API or external libraries like FFMPEG
        throw new IOException("Audio conversion not yet implemented for " + sourceFormat);
    }
    
    /**
     * Find WAV data chunk size.
     */
    private static long findWAVDataSize(byte[] data, int startPos) {
        ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
        buffer.position(startPos);
        
        while (buffer.remaining() >= 8) {
            int chunkId = buffer.getInt();
            int chunkSize = buffer.getInt();
            
            if (chunkId == 0x61746164) { // "data"
                return chunkSize;
            } else {
                buffer.position(buffer.position() + chunkSize);
            }
        }
        
        return 0;
    }
    
    /**
     * Estimate OGG duration (simplified).
     */
    private static double estimateOGGDuration(long fileSize) {
        // Very rough estimation - actual implementation would parse OGG structure
        double estimatedBitrate = 128000; // 128 kbps
        return (fileSize * 8.0) / estimatedBitrate;
    }
    
    /**
     * Estimate MP3 duration (simplified).
     */
    private static double estimateMP3Duration(long fileSize) {
        // Very rough estimation - actual implementation would parse MP3 frames
        double estimatedBitrate = 128000; // 128 kbps
        return (fileSize * 8.0) / estimatedBitrate;
    }
    
    /**
     * Clean expired cache entries.
     */
    private static void cleanExpiredCache() {
        soundCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }
    
    /**
     * Clears all entries from the internal sound cache.
     */
    public static void clearCache() {
        soundCache.clear();
    }
    
    /**
     * Gets statistics about the current state of the sound cache.
     *
     * @return A map containing statistics such as the number of cached items
     *         and the total size of cached data.
     */
    public static Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("CacheSize", soundCache.size());
        
        long totalSize = soundCache.values().stream()
                .mapToLong(cache -> cache.getData().length)
                .sum();
        stats.put("TotalCacheSize", totalSize);
        
        return stats;
    }
}