/*
 * Rendering Settings - All rendering-related settings classes
 */

package lindenlab.llsd.viewer.secondlife.rendering;

import java.util.HashMap;
import java.util.Map;

// Performance Settings
class PerformanceSettings {
    private int targetFPS = 60;
    private boolean vSync = true;
    private boolean adaptiveQualityEnabled = false;
    private int maxCPUUsage = 80; // percentage
    private long maxMemoryUsage = 2L * 1024 * 1024 * 1024; // 2GB
    
    public void setTargetFPS(int fps) { this.targetFPS = fps; }
    public int getTargetFPS() { return targetFPS; }
    
    public void setVSync(boolean vsync) { this.vSync = vsync; }
    public boolean isVSync() { return vSync; }
    
    public void setAdaptiveQualityEnabled(boolean enabled) { this.adaptiveQualityEnabled = enabled; }
    public boolean isAdaptiveQualityEnabled() { return adaptiveQualityEnabled; }
    
    public void setMaxCPUUsage(int usage) { this.maxCPUUsage = usage; }
    public int getMaxCPUUsage() { return maxCPUUsage; }
    
    public void setMaxMemoryUsage(long memory) { this.maxMemoryUsage = memory; }
    public long getMaxMemoryUsage() { return maxMemoryUsage; }
    
    public void applySetting(String setting, Object value) {
        switch (setting.toLowerCase()) {
            case "targetfps":
                if (value instanceof Number) setTargetFPS(((Number) value).intValue());
                break;
            case "vsync":
                if (value instanceof Boolean) setVSync((Boolean) value);
                break;
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("targetFPS", targetFPS);
        map.put("vSync", vSync);
        map.put("adaptiveQualityEnabled", adaptiveQualityEnabled);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("targetFPS")) setTargetFPS(((Number) map.get("targetFPS")).intValue());
        if (map.containsKey("vSync")) setVSync((Boolean) map.get("vSync"));
        if (map.containsKey("adaptiveQualityEnabled")) setAdaptiveQualityEnabled((Boolean) map.get("adaptiveQualityEnabled"));
    }
}

// Effects Settings
class EffectsSettings {
    private boolean effectsEnabled = true;
    private float effectsQuality = 0.6f;
    private boolean bloom = true;
    private boolean motionBlur = false;
    private boolean depthOfField = false;
    private boolean screenSpaceReflections = true;
    
    public void setEffectsEnabled(boolean enabled) { this.effectsEnabled = enabled; }
    public boolean isEffectsEnabled() { return effectsEnabled; }
    
    public void setEffectsQuality(float quality) { this.effectsQuality = quality; }
    public float getEffectsQuality() { return effectsQuality; }
    
    public void setBloom(boolean bloom) { this.bloom = bloom; }
    public boolean isBloom() { return bloom; }
    
    public void setMotionBlur(boolean blur) { this.motionBlur = blur; }
    public boolean isMotionBlur() { return motionBlur; }
    
    public void setDepthOfField(boolean dof) { this.depthOfField = dof; }
    public boolean isDepthOfField() { return depthOfField; }
    
    public void setScreenSpaceReflections(boolean ssr) { this.screenSpaceReflections = ssr; }
    public boolean isScreenSpaceReflections() { return screenSpaceReflections; }
    
    public void applySetting(String setting, Object value) {
        switch (setting.toLowerCase()) {
            case "enabled":
                if (value instanceof Boolean) setEffectsEnabled((Boolean) value);
                break;
            case "quality":
                if (value instanceof Number) setEffectsQuality(((Number) value).floatValue());
                break;
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("effectsEnabled", effectsEnabled);
        map.put("effectsQuality", effectsQuality);
        map.put("bloom", bloom);
        map.put("motionBlur", motionBlur);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("effectsEnabled")) setEffectsEnabled((Boolean) map.get("effectsEnabled"));
        if (map.containsKey("effectsQuality")) setEffectsQuality(((Number) map.get("effectsQuality")).floatValue());
        if (map.containsKey("bloom")) setBloom((Boolean) map.get("bloom"));
        if (map.containsKey("motionBlur")) setMotionBlur((Boolean) map.get("motionBlur"));
    }
}

// Texture Settings
class TextureSettings {
    private AdvancedRenderingSystem.TextureQuality textureQuality = AdvancedRenderingSystem.TextureQuality.MEDIUM;
    private boolean anisotropicFiltering = true;
    private int anisotropyLevel = 16;
    private boolean mipmapping = true;
    private boolean textureCompression = true;
    
    public void setTextureQuality(AdvancedRenderingSystem.TextureQuality quality) { this.textureQuality = quality; }
    public AdvancedRenderingSystem.TextureQuality getTextureQuality() { return textureQuality; }
    
    public void setAnisotropicFiltering(boolean filtering) { this.anisotropicFiltering = filtering; }
    public boolean isAnisotropicFiltering() { return anisotropicFiltering; }
    
    public void setAnisotropyLevel(int level) { this.anisotropyLevel = level; }
    public int getAnisotropyLevel() { return anisotropyLevel; }
    
    public void applySetting(String setting, Object value) {
        switch (setting.toLowerCase()) {
            case "quality":
                if (value instanceof String) {
                    try {
                        setTextureQuality(AdvancedRenderingSystem.TextureQuality.valueOf((String) value));
                    } catch (IllegalArgumentException e) {
                        // Invalid quality level
                    }
                }
                break;
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("textureQuality", textureQuality.name());
        map.put("anisotropicFiltering", anisotropicFiltering);
        map.put("anisotropyLevel", anisotropyLevel);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("textureQuality")) {
            try {
                setTextureQuality(AdvancedRenderingSystem.TextureQuality.valueOf((String) map.get("textureQuality")));
            } catch (IllegalArgumentException e) {
                // Invalid quality level
            }
        }
        if (map.containsKey("anisotropicFiltering")) setAnisotropicFiltering((Boolean) map.get("anisotropicFiltering"));
        if (map.containsKey("anisotropyLevel")) setAnisotropyLevel(((Number) map.get("anisotropyLevel")).intValue());
    }
}

// Shadow Settings
class ShadowSettings {
    private boolean shadowsEnabled = true;
    private AdvancedRenderingSystem.ShadowQuality shadowQuality = AdvancedRenderingSystem.ShadowQuality.MEDIUM;
    private int shadowDistance = 128;
    private float shadowBias = 0.005f;
    
    public void setShadowsEnabled(boolean enabled) { this.shadowsEnabled = enabled; }
    public boolean isShadowsEnabled() { return shadowsEnabled; }
    
    public void setShadowQuality(AdvancedRenderingSystem.ShadowQuality quality) { this.shadowQuality = quality; }
    public AdvancedRenderingSystem.ShadowQuality getShadowQuality() { return shadowQuality; }
    
    public void setShadowDistance(int distance) { this.shadowDistance = distance; }
    public int getShadowDistance() { return shadowDistance; }
    
    public void applySetting(String setting, Object value) {
        switch (setting.toLowerCase()) {
            case "enabled":
                if (value instanceof Boolean) setShadowsEnabled((Boolean) value);
                break;
        }
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("shadowsEnabled", shadowsEnabled);
        map.put("shadowQuality", shadowQuality.name());
        map.put("shadowDistance", shadowDistance);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("shadowsEnabled")) setShadowsEnabled((Boolean) map.get("shadowsEnabled"));
        if (map.containsKey("shadowQuality")) {
            try {
                setShadowQuality(AdvancedRenderingSystem.ShadowQuality.valueOf((String) map.get("shadowQuality")));
            } catch (IllegalArgumentException e) {
                // Invalid quality level
            }
        }
        if (map.containsKey("shadowDistance")) setShadowDistance(((Number) map.get("shadowDistance")).intValue());
    }
}

// Mesh Settings
class MeshSettings {
    private float lodBias = 0.0f;
    private int maxLodLevel = 4;
    private boolean meshStreaming = true;
    private int meshBandwidth = 500; // KB/s
    
    public void setLodBias(float bias) { this.lodBias = bias; }
    public float getLodBias() { return lodBias; }
    
    public void setMaxLodLevel(int level) { this.maxLodLevel = level; }
    public int getMaxLodLevel() { return maxLodLevel; }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("lodBias", lodBias);
        map.put("maxLodLevel", maxLodLevel);
        map.put("meshStreaming", meshStreaming);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("lodBias")) setLodBias(((Number) map.get("lodBias")).floatValue());
        if (map.containsKey("maxLodLevel")) setMaxLodLevel(((Number) map.get("maxLodLevel")).intValue());
        if (map.containsKey("meshStreaming")) meshStreaming = (Boolean) map.get("meshStreaming");
    }
}

// Avatar Settings
class AvatarSettings {
    private int maxVisibleAvatars = 30;
    private int avatarLodBias = 0;
    private boolean avatarImpostors = true;
    private int impostorDistance = 64;
    
    public void setMaxVisibleAvatars(int max) { this.maxVisibleAvatars = max; }
    public int getMaxVisibleAvatars() { return maxVisibleAvatars; }
    
    public void setAvatarLodBias(int bias) { this.avatarLodBias = bias; }
    public int getAvatarLodBias() { return avatarLodBias; }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("maxVisibleAvatars", maxVisibleAvatars);
        map.put("avatarLodBias", avatarLodBias);
        map.put("avatarImpostors", avatarImpostors);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("maxVisibleAvatars")) setMaxVisibleAvatars(((Number) map.get("maxVisibleAvatars")).intValue());
        if (map.containsKey("avatarLodBias")) setAvatarLodBias(((Number) map.get("avatarLodBias")).intValue());
        if (map.containsKey("avatarImpostors")) avatarImpostors = (Boolean) map.get("avatarImpostors");
    }
}

// Particle Settings
class ParticleSettings {
    private int maxParticles = 2000;
    private float particleQuality = 0.6f;
    private boolean particlePhysics = true;
    
    public void setMaxParticles(int max) { this.maxParticles = max; }
    public int getMaxParticles() { return maxParticles; }
    
    public void setParticleQuality(float quality) { this.particleQuality = quality; }
    public float getParticleQuality() { return particleQuality; }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("maxParticles", maxParticles);
        map.put("particleQuality", particleQuality);
        map.put("particlePhysics", particlePhysics);
        return map;
    }
    
    public void fromMap(Map<String, Object> map) {
        if (map.containsKey("maxParticles")) setMaxParticles(((Number) map.get("maxParticles")).intValue());
        if (map.containsKey("particleQuality")) setParticleQuality(((Number) map.get("particleQuality")).floatValue());
        if (map.containsKey("particlePhysics")) particlePhysics = (Boolean) map.get("particlePhysics");
    }
}

// Simplified settings classes for completeness
class TerrainSettings {
    public Map<String, Object> toMap() { return new HashMap<>(); }
    public void fromMap(Map<String, Object> map) { }
}

class WaterSettings {
    public Map<String, Object> toMap() { return new HashMap<>(); }
    public void fromMap(Map<String, Object> map) { }
}

class SkySettings {
    public Map<String, Object> toMap() { return new HashMap<>(); }
    public void fromMap(Map<String, Object> map) { }
}

class LightingSettings {
    public Map<String, Object> toMap() { return new HashMap<>(); }
    public void fromMap(Map<String, Object> map) { }
}

class UISettings {
    public Map<String, Object> toMap() { return new HashMap<>(); }
    public void fromMap(Map<String, Object> map) { }
}

// Performance monitoring
class PerformanceMonitor {
    public void startMonitoring() {
        // Start performance monitoring
    }
    
    public void stopMonitoring() {
        // Stop performance monitoring
    }
    
    public void shutdown() {
        // Cleanup monitoring resources
    }
}

class PerformanceMetrics {
    private int currentFPS = 60;
    
    public int getCurrentFPS() { return currentFPS; }
    public void setCurrentFPS(int fps) { this.currentFPS = fps; }
}