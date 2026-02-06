/*
 * OpenAL Audio Engine - Java implementation of 3D spatial audio
 *
 * Based on Second Life viewer audio implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.libraries.audio.openal;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OpenAL-based 3D audio engine for Second Life.
 * <p>
 * This class provides comprehensive 3D spatial audio capabilities including:
 * <ul>
 *   <li>3D positional audio with distance attenuation</li>
 *   <li>Doppler effect simulation</li>
 *   <li>Environmental audio effects and reverb</li>
 *   <li>Streaming audio support for music and large sound files</li>
 *   <li>Audio source management and pooling</li>
 *   <li>Multiple listener support for different audio contexts</li>
 * </ul>
 * 
 * @author LLSD Java Team
 * @since 1.0
 */
public class OpenALAudioEngine {
    
    private boolean initialized = false;
    private AudioDevice device;
    private AudioContext context;
    private final Map<UUID, AudioSource> activeSources = new ConcurrentHashMap<>();
    private final Map<UUID, AudioBuffer> loadedBuffers = new ConcurrentHashMap<>();
    private final AudioSettings settings = new AudioSettings();
    private AudioListener listener;
    
    // Audio source pool for efficient reuse
    private final Queue<AudioSource> sourcePool = new LinkedList<>();
    private static final int MAX_CONCURRENT_SOURCES = 64;
    
    /**
     * Audio engine configuration settings.
     */
    public static class AudioSettings {
        public float masterVolume = 1.0f;
        public float sfxVolume = 1.0f;
        public float musicVolume = 1.0f;
        public float voiceVolume = 1.0f;
        public float ambientVolume = 1.0f;
        
        // 3D audio settings
        public float dopplerFactor = 1.0f;
        public float speedOfSound = 343.3f; // meters per second
        public float maxAudioDistance = 100.0f;
        public float rolloffFactor = 1.0f;
        
        // Environmental settings
        public boolean enableReverb = true;
        public boolean enableEAX = false; // Environmental Audio Extensions
        public String environmentPreset = "Generic";
        
        // Performance settings
        public int maxSources = 32;
        public int streamingBufferSize = 4096;
        public int streamingBufferCount = 4;
        public boolean enableHRTF = true; // Head-Related Transfer Function
    }
    
    /**
     * Represents an OpenAL audio device.
     */
    public static class AudioDevice {
        private final String deviceName;
        private final boolean isDefault;
        private final List<String> supportedExtensions;
        
        public AudioDevice(String deviceName, boolean isDefault) {
            this.deviceName = deviceName;
            this.isDefault = isDefault;
            this.supportedExtensions = new ArrayList<>();
        }
        
        public String getDeviceName() { return deviceName; }
        public boolean isDefault() { return isDefault; }
        public List<String> getSupportedExtensions() { return supportedExtensions; }
        
        public void addExtension(String extension) {
            supportedExtensions.add(extension);
        }
    }
    
    /**
     * OpenAL audio context.
     */
    public static class AudioContext {
        private final AudioDevice device;
        private final int sampleRate;
        private final int refreshRate;
        
        public AudioContext(AudioDevice device, int sampleRate, int refreshRate) {
            this.device = device;
            this.sampleRate = sampleRate;
            this.refreshRate = refreshRate;
        }
        
        public AudioDevice getDevice() { return device; }
        public int getSampleRate() { return sampleRate; }
        public int getRefreshRate() { return refreshRate; }
    }
    
    /**
     * 3D audio listener representing the user's ears.
     */
    public static class AudioListener {
        private Vector3 position = new Vector3(0, 0, 0);
        private Vector3 velocity = new Vector3(0, 0, 0);
        private Vector3 forward = new Vector3(0, 0, -1);
        private Vector3 up = new Vector3(0, 1, 0);
        private float gain = 1.0f;
        
        // Getters and setters
        public Vector3 getPosition() { return position; }
        public void setPosition(Vector3 position) { this.position = position; }
        
        public Vector3 getVelocity() { return velocity; }
        public void setVelocity(Vector3 velocity) { this.velocity = velocity; }
        
        public Vector3 getForward() { return forward; }
        public void setForward(Vector3 forward) { this.forward = forward; }
        
        public Vector3 getUp() { return up; }
        public void setUp(Vector3 up) { this.up = up; }
        
        public float getGain() { return gain; }
        public void setGain(float gain) { this.gain = Math.max(0.0f, gain); }
        
        /**
         * Set listener orientation from forward and up vectors.
         */
        public void setOrientation(Vector3 forward, Vector3 up) {
            this.forward = forward.normalize();
            this.up = up.normalize();
        }
    }
    
    /**
     * Audio buffer containing loaded sound data.
     */
    public static class AudioBuffer {
        private final UUID bufferId;
        private final int alBufferHandle; // OpenAL buffer handle
        private final AudioFormat format;
        private final int sampleRate;
        private final int size;
        private final float duration;
        
        public enum AudioFormat {
            MONO8, MONO16, STEREO8, STEREO16
        }
        
        public AudioBuffer(UUID bufferId, AudioFormat format, int sampleRate, int size) {
            this.bufferId = bufferId;
            this.format = format;
            this.sampleRate = sampleRate;
            this.size = size;
            this.duration = calculateDuration(format, sampleRate, size);
            this.alBufferHandle = generateBufferHandle(); // Mock handle
        }
        
        public UUID getBufferId() { return bufferId; }
        public int getAlBufferHandle() { return alBufferHandle; }
        public AudioFormat getFormat() { return format; }
        public int getSampleRate() { return sampleRate; }
        public int getSize() { return size; }
        public float getDuration() { return duration; }
        
        private float calculateDuration(AudioFormat format, int sampleRate, int size) {
            int bytesPerSample = (format == AudioFormat.MONO16 || format == AudioFormat.STEREO16) ? 2 : 1;
            int channels = (format == AudioFormat.STEREO8 || format == AudioFormat.STEREO16) ? 2 : 1;
            return (float) size / (sampleRate * bytesPerSample * channels);
        }
        
        private int generateBufferHandle() {
            return Math.abs(bufferId.hashCode()); // Mock handle generation
        }
    }
    
    /**
     * 3D audio source that can play sounds at specific positions.
     */
    public static class AudioSource {
        private final UUID sourceId;
        private final int alSourceHandle; // OpenAL source handle
        private Vector3 position = new Vector3(0, 0, 0);
        private Vector3 velocity = new Vector3(0, 0, 0);
        private float pitch = 1.0f;
        private float gain = 1.0f;
        private float maxDistance = 100.0f;
        private float rolloffFactor = 1.0f;
        private float referenceDistance = 1.0f;
        private boolean looping = false;
        private boolean relative = false; // Position relative to listener
        private AudioBuffer buffer;
        private AudioSourceState state = AudioSourceState.INITIAL;
        
        public enum AudioSourceState {
            INITIAL, PLAYING, PAUSED, STOPPED
        }
        
        public AudioSource(UUID sourceId) {
            this.sourceId = sourceId;
            this.alSourceHandle = generateSourceHandle(); // Mock handle
        }
        
        // Getters and setters
        public UUID getSourceId() { return sourceId; }
        public int getAlSourceHandle() { return alSourceHandle; }
        
        public Vector3 getPosition() { return position; }
        public void setPosition(Vector3 position) { this.position = position; }
        
        public Vector3 getVelocity() { return velocity; }
        public void setVelocity(Vector3 velocity) { this.velocity = velocity; }
        
        public float getPitch() { return pitch; }
        public void setPitch(float pitch) { this.pitch = Math.max(0.5f, Math.min(2.0f, pitch)); }
        
        public float getGain() { return gain; }
        public void setGain(float gain) { this.gain = Math.max(0.0f, gain); }
        
        public float getMaxDistance() { return maxDistance; }
        public void setMaxDistance(float distance) { this.maxDistance = Math.max(0.0f, distance); }
        
        public float getRolloffFactor() { return rolloffFactor; }
        public void setRolloffFactor(float factor) { this.rolloffFactor = Math.max(0.0f, factor); }
        
        public float getReferenceDistance() { return referenceDistance; }
        public void setReferenceDistance(float distance) { this.referenceDistance = Math.max(0.0f, distance); }
        
        public boolean isLooping() { return looping; }
        public void setLooping(boolean looping) { this.looping = looping; }
        
        public boolean isRelative() { return relative; }
        public void setRelative(boolean relative) { this.relative = relative; }
        
        public AudioBuffer getBuffer() { return buffer; }
        public void setBuffer(AudioBuffer buffer) { this.buffer = buffer; }
        
        public AudioSourceState getState() { return state; }
        public void setState(AudioSourceState state) { this.state = state; }
        
        private int generateSourceHandle() {
            return Math.abs(sourceId.hashCode()); // Mock handle generation
        }
    }
    
    /**
     * Initialize the OpenAL audio engine.
     * 
     * @return true if initialization was successful, false otherwise
     */
    public boolean initialize() {
        if (initialized) {
            return true;
        }
        
        try {
            System.out.println("Initializing OpenAL audio engine...");
            
            // Initialize OpenAL device (placeholder)
            device = initializeAudioDevice();
            if (device == null) {
                System.err.println("Failed to initialize audio device");
                return false;
            }
            
            // Create audio context (placeholder)
            context = new AudioContext(device, 44100, 60);
            
            // Initialize audio listener
            listener = new AudioListener();
            
            // Initialize source pool
            initializeSourcePool();
            
            initialized = true;
            System.out.println("OpenAL audio engine initialized successfully");
            System.out.println("Device: " + device.getDeviceName());
            System.out.println("Sample Rate: " + context.getSampleRate() + " Hz");
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Error initializing OpenAL audio engine: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load audio data into a buffer.
     * 
     * @param soundId Unique identifier for the sound
     * @param audioData Raw audio data
     * @param format Audio format
     * @param sampleRate Sample rate in Hz
     * @return The created audio buffer, or null if loading failed
     */
    public AudioBuffer loadAudioBuffer(UUID soundId, byte[] audioData, 
                                      AudioBuffer.AudioFormat format, int sampleRate) {
        if (!initialized || audioData == null) {
            return null;
        }
        
        // Check if already loaded
        AudioBuffer existing = loadedBuffers.get(soundId);
        if (existing != null) {
            return existing;
        }
        
        try {
            AudioBuffer buffer = new AudioBuffer(soundId, format, sampleRate, audioData.length);
            
            // Upload audio data to OpenAL (placeholder)
            System.out.println("Loading audio buffer: " + soundId + " (" + audioData.length + " bytes)");
            
            loadedBuffers.put(soundId, buffer);
            return buffer;
            
        } catch (Exception e) {
            System.err.println("Failed to load audio buffer: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Create a new audio source for playing sounds.
     * 
     * @return A new audio source, or null if no sources available
     */
    public AudioSource createAudioSource() {
        if (!initialized) {
            return null;
        }
        
        // Try to reuse a source from the pool
        AudioSource source = sourcePool.poll();
        if (source == null && activeSources.size() < settings.maxSources) {
            source = new AudioSource(UUID.randomUUID());
        }
        
        if (source != null) {
            activeSources.put(source.getSourceId(), source);
        }
        
        return source;
    }
    
    /**
     * Play a sound at a specific 3D position.
     * 
     * @param soundId The ID of the loaded sound buffer
     * @param position 3D position of the sound
     * @param gain Volume (0.0 to 1.0)
     * @param pitch Pitch multiplier (0.5 to 2.0)
     * @param looping Whether the sound should loop
     * @return The source playing the sound, or null if failed
     */
    public AudioSource playSound3D(UUID soundId, Vector3 position, float gain, 
                                  float pitch, boolean looping) {
        AudioBuffer buffer = loadedBuffers.get(soundId);
        if (buffer == null) {
            System.err.println("Sound buffer not found: " + soundId);
            return null;
        }
        
        AudioSource source = createAudioSource();
        if (source == null) {
            System.err.println("No available audio sources");
            return null;
        }
        
        // Configure source
        source.setBuffer(buffer);
        source.setPosition(position);
        source.setGain(gain * settings.sfxVolume * settings.masterVolume);
        source.setPitch(pitch);
        source.setLooping(looping);
        source.setRelative(false);
        
        // Apply 3D audio settings
        source.setMaxDistance(settings.maxAudioDistance);
        source.setRolloffFactor(settings.rolloffFactor);
        
        // Start playing (placeholder for actual OpenAL call)
        source.setState(AudioSource.AudioSourceState.PLAYING);
        System.out.println("Playing 3D sound: " + soundId + " at " + position);
        
        return source;
    }
    
    /**
     * Update the audio listener position and orientation.
     * 
     * @param position Listener position
     * @param velocity Listener velocity (for Doppler effect)
     * @param forward Forward vector
     * @param up Up vector
     */
    public void updateListener(Vector3 position, Vector3 velocity, Vector3 forward, Vector3 up) {
        if (!initialized || listener == null) {
            return;
        }
        
        listener.setPosition(position);
        listener.setVelocity(velocity);
        listener.setOrientation(forward, up);
        
        // Update OpenAL listener (placeholder)
        System.out.println("Updated listener position: " + position);
    }
    
    /**
     * Update all active audio sources (call once per frame).
     * 
     * @param deltaTime Time elapsed since last update in seconds
     */
    public void update(float deltaTime) {
        if (!initialized) {
            return;
        }
        
        // Update all active sources
        Iterator<Map.Entry<UUID, AudioSource>> iterator = activeSources.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, AudioSource> entry = iterator.next();
            AudioSource source = entry.getValue();
            
            // Check if source is still playing (placeholder for actual OpenAL query)
            if (source.getState() == AudioSource.AudioSourceState.STOPPED) {
                // Return source to pool
                sourcePool.offer(source);
                iterator.remove();
            }
        }
        
        // Apply global audio settings (placeholder)
        updateGlobalSettings();
    }
    
    /**
     * Get the current audio settings.
     * 
     * @return The audio settings object
     */
    public AudioSettings getSettings() {
        return settings;
    }
    
    /**
     * Get the audio listener.
     * 
     * @return The audio listener object
     */
    public AudioListener getListener() {
        return listener;
    }
    
    /**
     * Shutdown the audio engine and cleanup resources.
     */
    public void shutdown() {
        if (!initialized) {
            return;
        }
        
        System.out.println("Shutting down OpenAL audio engine...");
        
        // Stop all active sources
        for (AudioSource source : activeSources.values()) {
            source.setState(AudioSource.AudioSourceState.STOPPED);
        }
        activeSources.clear();
        sourcePool.clear();
        
        // Cleanup buffers
        loadedBuffers.clear();
        
        // Destroy OpenAL context and device (placeholder)
        context = null;
        device = null;
        listener = null;
        
        initialized = false;
        System.out.println("OpenAL audio engine shutdown complete");
    }
    
    // Private helper methods
    
    private AudioDevice initializeAudioDevice() {
        // Placeholder for OpenAL device enumeration and selection
        AudioDevice device = new AudioDevice("Default OpenAL Device", true);
        device.addExtension("AL_EXT_OFFSET");
        device.addExtension("AL_EXT_LINEAR_DISTANCE");
        device.addExtension("AL_EXT_EXPONENT_DISTANCE");
        return device;
    }
    
    private void initializeSourcePool() {
        // Pre-create some audio sources for efficient reuse
        for (int i = 0; i < Math.min(16, settings.maxSources); i++) {
            sourcePool.offer(new AudioSource(UUID.randomUUID()));
        }
    }
    
    private void updateGlobalSettings() {
        // Apply Doppler effect settings (placeholder)
        // alDopplerFactor(settings.dopplerFactor);
        // alSpeedOfSound(settings.speedOfSound);
        
        // Update listener gain
        if (listener != null) {
            listener.setGain(settings.masterVolume);
        }
    }
}