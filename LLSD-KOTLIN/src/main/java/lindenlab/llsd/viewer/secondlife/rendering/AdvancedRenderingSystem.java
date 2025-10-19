/*
 * Advanced Rendering System - Fine-grained rendering controls for Second Life viewer
 *
 * Based on Second Life viewer implementation
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.rendering;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Advanced rendering system with fine-grained controls replacing basic/plus/ultra presets.
 * 
 * Features:
 * - Detailed graphics settings control
 * - Battery conservation mode (blank background)
 * - Performance optimization
 * - Real-time quality adjustment
 * - Memory and CPU monitoring
 */
public class AdvancedRenderingSystem {
    private static final Logger LOGGER = Logger.getLogger(AdvancedRenderingSystem.class.getName());
    
    // Rendering state
    private final AtomicBoolean renderingEnabled = new AtomicBoolean(true);
    private final AtomicBoolean batteryConservationMode = new AtomicBoolean(false);
    private final AtomicInteger frameRate = new AtomicInteger(60);
    
    // Settings categories
    private final QualitySettings qualitySettings;
    private final PerformanceSettings performanceSettings;
    private final EffectsSettings effectsSettings;
    private final TerrainSettings terrainSettings;
    private final WaterSettings waterSettings;
    private final SkySettings skySettings;
    private final LightingSettings lightingSettings;
    private final ShadowSettings shadowSettings;
    private final TextureSettings textureSettings;
    private final MeshSettings meshSettings;
    private final AvatarSettings avatarSettings;
    private final ParticleSettings particleSettings;
    private final UISettings uiSettings;
    
    // Performance monitoring
    private final PerformanceMonitor performanceMonitor;
    private final Map<String, Object> renderStatistics;
    
    public AdvancedRenderingSystem() {
        this.qualitySettings = new QualitySettings();
        this.performanceSettings = new PerformanceSettings();
        this.effectsSettings = new EffectsSettings();
        this.terrainSettings = new TerrainSettings();
        this.waterSettings = new WaterSettings();
        this.skySettings = new SkySettings();
        this.lightingSettings = new LightingSettings();
        this.shadowSettings = new ShadowSettings();
        this.textureSettings = new TextureSettings();
        this.meshSettings = new MeshSettings();
        this.avatarSettings = new AvatarSettings();
        this.particleSettings = new ParticleSettings();
        this.uiSettings = new UISettings();
        
        this.performanceMonitor = new PerformanceMonitor();
        this.renderStatistics = new ConcurrentHashMap<>();
        
        // Apply default settings
        applyBalancedPreset();
        
        LOGGER.info("Advanced rendering system initialized");
    }
    
    // Main rendering control
    
    public void setRenderingEnabled(boolean enabled) {
        boolean wasEnabled = renderingEnabled.getAndSet(enabled);
        if (wasEnabled != enabled) {
            LOGGER.info("Rendering " + (enabled ? "enabled" : "disabled"));
            if (!enabled) {
                clearFrameBuffer();
            }
        }
    }
    
    public boolean isRenderingEnabled() {
        return renderingEnabled.get();
    }
    
    public void setBatteryConservationMode(boolean enabled) {
        boolean wasEnabled = batteryConservationMode.getAndSet(enabled);
        if (wasEnabled != enabled) {
            LOGGER.info("Battery conservation mode " + (enabled ? "enabled" : "disabled"));
            if (enabled) {
                // Apply power-saving settings
                applyPowerSavingSettings();
                setRenderingEnabled(false);
            } else {
                // Restore previous settings
                restorePreviousSettings();
                setRenderingEnabled(true);
            }
        }
    }
    
    public boolean isBatteryConservationMode() {
        return batteryConservationMode.get();
    }
    
    private void clearFrameBuffer() {
        // Clear to black background for battery conservation
        // In real implementation, this would clear the OpenGL framebuffer
        LOGGER.fine("Clearing frame buffer to blank background");
    }
    
    // Quality presets (replacing basic/plus/ultra)
    
    public void applyUltraLowPreset() {
        LOGGER.info("Applying Ultra Low quality preset");
        qualitySettings.setOverallQuality(0.1f);
        performanceSettings.setTargetFPS(30);
        effectsSettings.setEffectsEnabled(false);
        textureSettings.setTextureQuality(TextureQuality.VERY_LOW);
        shadowSettings.setShadowsEnabled(false);
        meshSettings.setLodBias(-2.0f);
        avatarSettings.setMaxVisibleAvatars(5);
        particleSettings.setMaxParticles(100);
    }
    
    public void applyLowPreset() {
        LOGGER.info("Applying Low quality preset");
        qualitySettings.setOverallQuality(0.3f);
        performanceSettings.setTargetFPS(45);
        effectsSettings.setEffectsEnabled(true);
        effectsSettings.setEffectsQuality(0.3f);
        textureSettings.setTextureQuality(TextureQuality.LOW);
        shadowSettings.setShadowsEnabled(false);
        meshSettings.setLodBias(-1.0f);
        avatarSettings.setMaxVisibleAvatars(15);
        particleSettings.setMaxParticles(500);
    }
    
    public void applyBalancedPreset() {
        LOGGER.info("Applying Balanced quality preset");
        qualitySettings.setOverallQuality(0.6f);
        performanceSettings.setTargetFPS(60);
        effectsSettings.setEffectsEnabled(true);
        effectsSettings.setEffectsQuality(0.6f);
        textureSettings.setTextureQuality(TextureQuality.MEDIUM);
        shadowSettings.setShadowsEnabled(true);
        shadowSettings.setShadowQuality(ShadowQuality.MEDIUM);
        meshSettings.setLodBias(0.0f);
        avatarSettings.setMaxVisibleAvatars(30);
        particleSettings.setMaxParticles(2000);
    }
    
    public void applyHighPreset() {
        LOGGER.info("Applying High quality preset");
        qualitySettings.setOverallQuality(0.8f);
        performanceSettings.setTargetFPS(60);
        effectsSettings.setEffectsEnabled(true);
        effectsSettings.setEffectsQuality(0.8f);
        textureSettings.setTextureQuality(TextureQuality.HIGH);
        shadowSettings.setShadowsEnabled(true);
        shadowSettings.setShadowQuality(ShadowQuality.HIGH);
        meshSettings.setLodBias(1.0f);
        avatarSettings.setMaxVisibleAvatars(50);
        particleSettings.setMaxParticles(5000);
    }
    
    public void applyUltraPreset() {
        LOGGER.info("Applying Ultra quality preset");
        qualitySettings.setOverallQuality(1.0f);
        performanceSettings.setTargetFPS(60);
        effectsSettings.setEffectsEnabled(true);
        effectsSettings.setEffectsQuality(1.0f);
        textureSettings.setTextureQuality(TextureQuality.ULTRA);
        shadowSettings.setShadowsEnabled(true);
        shadowSettings.setShadowQuality(ShadowQuality.ULTRA);
        meshSettings.setLodBias(2.0f);
        avatarSettings.setMaxVisibleAvatars(100);
        particleSettings.setMaxParticles(10000);
    }
    
    // Performance optimization
    
    private void applyPowerSavingSettings() {
        // Store current settings for restoration
        storeCurrentSettings();
        
        // Apply extreme power saving
        performanceSettings.setTargetFPS(15);
        performanceSettings.setVSync(false);
        effectsSettings.setEffectsEnabled(false);
        shadowSettings.setShadowsEnabled(false);
        particleSettings.setMaxParticles(0);
        textureSettings.setTextureQuality(TextureQuality.VERY_LOW);
        meshSettings.setLodBias(-3.0f);
        avatarSettings.setMaxVisibleAvatars(1);
        
        LOGGER.info("Applied power saving settings");
    }
    
    private Map<String, Object> storedSettings = new HashMap<>();
    
    private void storeCurrentSettings() {
        storedSettings.clear();
        storedSettings.put("targetFPS", performanceSettings.getTargetFPS());
        storedSettings.put("vsync", performanceSettings.isVSync());
        storedSettings.put("effectsEnabled", effectsSettings.isEffectsEnabled());
        storedSettings.put("shadowsEnabled", shadowSettings.isShadowsEnabled());
        storedSettings.put("maxParticles", particleSettings.getMaxParticles());
        storedSettings.put("textureQuality", textureSettings.getTextureQuality());
        storedSettings.put("lodBias", meshSettings.getLodBias());
        storedSettings.put("maxAvatars", avatarSettings.getMaxVisibleAvatars());
    }
    
    private void restorePreviousSettings() {
        if (!storedSettings.isEmpty()) {
            performanceSettings.setTargetFPS((Integer) storedSettings.get("targetFPS"));
            performanceSettings.setVSync((Boolean) storedSettings.get("vsync"));
            effectsSettings.setEffectsEnabled((Boolean) storedSettings.get("effectsEnabled"));
            shadowSettings.setShadowsEnabled((Boolean) storedSettings.get("shadowsEnabled"));
            particleSettings.setMaxParticles((Integer) storedSettings.get("maxParticles"));
            textureSettings.setTextureQuality((TextureQuality) storedSettings.get("textureQuality"));
            meshSettings.setLodBias((Float) storedSettings.get("lodBias"));
            avatarSettings.setMaxVisibleAvatars((Integer) storedSettings.get("maxAvatars"));
            
            LOGGER.info("Restored previous settings");
        }
    }
    
    // Enums for quality levels
    
    public enum TextureQuality {
        VERY_LOW(64, 0.25f),
        LOW(128, 0.5f),
        MEDIUM(256, 0.75f),
        HIGH(512, 1.0f),
        ULTRA(1024, 1.25f);
        
        private final int maxSize;
        private final float detailBias;
        
        TextureQuality(int maxSize, float detailBias) {
            this.maxSize = maxSize;
            this.detailBias = detailBias;
        }
        
        public int getMaxSize() { return maxSize; }
        public float getDetailBias() { return detailBias; }
    }
    
    public enum ShadowQuality {
        DISABLED(0, 0),
        LOW(512, 2),
        MEDIUM(1024, 4),
        HIGH(2048, 6),
        ULTRA(4096, 8);
        
        private final int shadowMapSize;
        private final int cascadeCount;
        
        ShadowQuality(int shadowMapSize, int cascadeCount) {
            this.shadowMapSize = shadowMapSize;
            this.cascadeCount = cascadeCount;
        }
        
        public int getShadowMapSize() { return shadowMapSize; }
        public int getCascadeCount() { return cascadeCount; }
    }
    
    // Getter methods for settings (simplified implementations)
    
    public QualitySettings getQualitySettings() { return qualitySettings; }
    public PerformanceSettings getPerformanceSettings() { return performanceSettings; }
    public EffectsSettings getEffectsSettings() { return effectsSettings; }
    public TerrainSettings getTerrainSettings() { return terrainSettings; }
    public WaterSettings getWaterSettings() { return waterSettings; }
    public SkySettings getSkySettings() { return skySettings; }
    public LightingSettings getLightingSettings() { return lightingSettings; }
    public ShadowSettings getShadowSettings() { return shadowSettings; }
    public TextureSettings getTextureSettings() { return textureSettings; }
    public MeshSettings getMeshSettings() { return meshSettings; }
    public AvatarSettings getAvatarSettings() { return avatarSettings; }
    public ParticleSettings getParticleSettings() { return particleSettings; }
    public UISettings getUISettings() { return uiSettings; }
    
    // Statistics and monitoring
    
    public Map<String, Object> getRenderStatistics() {
        Map<String, Object> stats = new HashMap<>(renderStatistics);
        stats.put("renderingEnabled", renderingEnabled.get());
        stats.put("batteryConservationMode", batteryConservationMode.get());
        stats.put("currentFPS", frameRate.get());
        return stats;
    }
    
    // Shutdown
    
    public void shutdown() {
        LOGGER.info("Advanced rendering system shutdown");
    }
}