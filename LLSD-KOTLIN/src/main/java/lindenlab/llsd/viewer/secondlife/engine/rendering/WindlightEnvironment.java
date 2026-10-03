/*
 * Windlight Environment - Java implementation of Windlight atmospheric rendering system
 *
 * Based on Second Life Windlight and Firestorm environmental rendering
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine.rendering;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import lindenlab.llsd.viewer.secondlife.engine.Quaternion;
import java.util.*;
import java.util.UUID;

/**
 * Windlight environmental rendering system.
 * 
 * <p>This class implements the Windlight atmospheric rendering system used in
 * Second Life and Firestorm viewers, providing realistic sky, water, and
 * environmental lighting effects.</p>
 * 
 * @since 1.0
 */
public class WindlightEnvironment {
    
    // Sky settings
    private SkySettings skySettings;
    private WaterSettings waterSettings;
    private Vector3 sunDirection;
    private Vector3 moonDirection;
    private double dayTime; // 0.0 to 1.0 (0 = midnight, 0.5 = noon)
    
    // Environmental settings
    private double ambient;
    private double fogDensity;
    private Vector3 fogColor;
    private double fogStart;
    private double fogEnd;
    
    // Advanced settings
    private boolean useGammaCurve;
    private double exposure;
    private Vector3 starBrightness;
    private double cloudCoverage;
    private double cloudDensity;
    private Vector3 cloudColor;
    private double cloudScale;
    
    /**
     * Sky rendering settings.
     */
    public static class SkySettings {
        private Vector3 horizonColor;
        private Vector3 zenithColor;
        private Vector3 sunColor;
        private Vector3 ambientColor;
        private Vector3 cloudColor;
        
        private double hazeDensity;
        private double hazeHorizon;
        private double densityMultiplier;
        private double distanceMultiplier;
        
        private double sunGlowFocus;
        private double sunGlowSize;
        private double sceneGamma;
        
        // Rayleigh and Mie scattering
        private Vector3 rayleighScattering;
        private Vector3 mieScattering;
        private double mieAsymmetry;
        
        public SkySettings() {
            // Default Second Life sky settings
            this.horizonColor = new Vector3(0.25, 0.25, 0.32);
            this.zenithColor = new Vector3(0.24, 0.26, 0.30);
            this.sunColor = new Vector3(0.74, 0.65, 0.39);
            this.ambientColor = new Vector3(0.22, 0.23, 0.24);
            this.cloudColor = new Vector3(0.41, 0.41, 0.41);
            
            this.hazeDensity = 0.7;
            this.hazeHorizon = 0.19;
            this.densityMultiplier = 0.18;
            this.distanceMultiplier = 0.8;
            
            this.sunGlowFocus = 0.05;
            this.sunGlowSize = 1.75;
            this.sceneGamma = 1.0;
            
            this.rayleighScattering = new Vector3(0.13, 0.31, 0.75);
            this.mieScattering = new Vector3(0.04, 0.04, 0.04);
            this.mieAsymmetry = 0.8;
        }
        
        // Getters and setters
        public Vector3 getHorizonColor() { return horizonColor; }
        public void setHorizonColor(Vector3 horizonColor) { this.horizonColor = clampColor(horizonColor); }
        
        public Vector3 getZenithColor() { return zenithColor; }
        public void setZenithColor(Vector3 zenithColor) { this.zenithColor = clampColor(zenithColor); }
        
        public Vector3 getSunColor() { return sunColor; }
        public void setSunColor(Vector3 sunColor) { this.sunColor = clampColor(sunColor); }
        
        public Vector3 getAmbientColor() { return ambientColor; }
        public void setAmbientColor(Vector3 ambientColor) { this.ambientColor = clampColor(ambientColor); }
        
        public Vector3 getCloudColor() { return cloudColor; }
        public void setCloudColor(Vector3 cloudColor) { this.cloudColor = clampColor(cloudColor); }
        
        public double getHazeDensity() { return hazeDensity; }
        public void setHazeDensity(double hazeDensity) { this.hazeDensity = Math.max(0.0, hazeDensity); }
        
        public double getHazeHorizon() { return hazeHorizon; }
        public void setHazeHorizon(double hazeHorizon) { this.hazeHorizon = Math.max(0.0, Math.min(2.0, hazeHorizon)); }
        
        public double getDensityMultiplier() { return densityMultiplier; }
        public void setDensityMultiplier(double densityMultiplier) { this.densityMultiplier = Math.max(0.0, densityMultiplier); }
        
        public double getDistanceMultiplier() { return distanceMultiplier; }
        public void setDistanceMultiplier(double distanceMultiplier) { this.distanceMultiplier = Math.max(0.0, distanceMultiplier); }
        
        public double getSunGlowFocus() { return sunGlowFocus; }
        public void setSunGlowFocus(double sunGlowFocus) { this.sunGlowFocus = Math.max(0.0, sunGlowFocus); }
        
        public double getSunGlowSize() { return sunGlowSize; }
        public void setSunGlowSize(double sunGlowSize) { this.sunGlowSize = Math.max(0.0, sunGlowSize); }
        
        public double getSceneGamma() { return sceneGamma; }
        public void setSceneGamma(double sceneGamma) { this.sceneGamma = Math.max(0.1, Math.min(3.0, sceneGamma)); }
        
        public Vector3 getRayleighScattering() { return rayleighScattering; }
        public void setRayleighScattering(Vector3 rayleighScattering) { this.rayleighScattering = clampScattering(rayleighScattering); }
        
        public Vector3 getMieScattering() { return mieScattering; }
        public void setMieScattering(Vector3 mieScattering) { this.mieScattering = clampScattering(mieScattering); }
        
        public double getMieAsymmetry() { return mieAsymmetry; }
        public void setMieAsymmetry(double mieAsymmetry) { this.mieAsymmetry = Math.max(-1.0, Math.min(1.0, mieAsymmetry)); }
        
        public Map<String, Object> toLLSD() {
            Map<String, Object> data = new HashMap<>();
            data.put("HorizonColor", Arrays.asList(horizonColor.x, horizonColor.y, horizonColor.z));
            data.put("ZenithColor", Arrays.asList(zenithColor.x, zenithColor.y, zenithColor.z));
            data.put("SunColor", Arrays.asList(sunColor.x, sunColor.y, sunColor.z));
            data.put("AmbientColor", Arrays.asList(ambientColor.x, ambientColor.y, ambientColor.z));
            data.put("CloudColor", Arrays.asList(cloudColor.x, cloudColor.y, cloudColor.z));
            data.put("HazeDensity", hazeDensity);
            data.put("HazeHorizon", hazeHorizon);
            data.put("DensityMultiplier", densityMultiplier);
            data.put("DistanceMultiplier", distanceMultiplier);
            data.put("SunGlowFocus", sunGlowFocus);
            data.put("SunGlowSize", sunGlowSize);
            data.put("SceneGamma", sceneGamma);
            data.put("RayleighScattering", Arrays.asList(rayleighScattering.x, rayleighScattering.y, rayleighScattering.z));
            data.put("MieScattering", Arrays.asList(mieScattering.x, mieScattering.y, mieScattering.z));
            data.put("MieAsymmetry", mieAsymmetry);
            return data;
        }
    }
    
    /**
     * Water rendering settings.
     */
    public static class WaterSettings {
        private Vector3 waterColor;
        private Vector3 waterFogColor;
        private double waterFogDensity;
        private double underwaterFogModifier;
        
        private Vector3 normalScale;
        private UUID normalMapTexture;
        private double fresnelScale;
        private double fresnelOffset;
        private double scaleAbove;
        private double scaleBelow;
        
        private double blurMultiplier;
        private Vector3 littleWaveDirection;
        private Vector3 bigWaveDirection;
        
        public WaterSettings() {
            // Default Second Life water settings
            this.waterColor = new Vector3(0.04, 0.15, 0.20);
            this.waterFogColor = new Vector3(0.04, 0.15, 0.20);
            this.waterFogDensity = 10.0;
            this.underwaterFogModifier = 0.25;
            
            this.normalScale = new Vector3(1.0, 1.0, 1.0);
            this.fresnelScale = 0.40;
            this.fresnelOffset = 0.50;
            this.scaleAbove = 0.025;
            this.scaleBelow = 0.2;
            
            this.blurMultiplier = 0.04;
            this.littleWaveDirection = new Vector3(1.0, 1.0, 0.0);
            this.bigWaveDirection = new Vector3(1.0, 0.09, 0.0);
        }
        
        // Getters and setters
        public Vector3 getWaterColor() { return waterColor; }
        public void setWaterColor(Vector3 waterColor) { this.waterColor = clampColor(waterColor); }
        
        public Vector3 getWaterFogColor() { return waterFogColor; }
        public void setWaterFogColor(Vector3 waterFogColor) { this.waterFogColor = clampColor(waterFogColor); }
        
        public double getWaterFogDensity() { return waterFogDensity; }
        public void setWaterFogDensity(double waterFogDensity) { this.waterFogDensity = Math.max(0.0, waterFogDensity); }
        
        public double getUnderwaterFogModifier() { return underwaterFogModifier; }
        public void setUnderwaterFogModifier(double underwaterFogModifier) { this.underwaterFogModifier = Math.max(0.0, underwaterFogModifier); }
        
        public Vector3 getNormalScale() { return normalScale; }
        public void setNormalScale(Vector3 normalScale) { this.normalScale = normalScale; }
        
        public UUID getNormalMapTexture() { return normalMapTexture; }
        public void setNormalMapTexture(UUID normalMapTexture) { this.normalMapTexture = normalMapTexture; }
        
        public double getFresnelScale() { return fresnelScale; }
        public void setFresnelScale(double fresnelScale) { this.fresnelScale = Math.max(0.0, Math.min(1.0, fresnelScale)); }
        
        public double getFresnelOffset() { return fresnelOffset; }
        public void setFresnelOffset(double fresnelOffset) { this.fresnelOffset = Math.max(0.0, Math.min(1.0, fresnelOffset)); }
        
        public double getScaleAbove() { return scaleAbove; }
        public void setScaleAbove(double scaleAbove) { this.scaleAbove = Math.max(0.0, scaleAbove); }
        
        public double getScaleBelow() { return scaleBelow; }
        public void setScaleBelow(double scaleBelow) { this.scaleBelow = Math.max(0.0, scaleBelow); }
        
        public double getBlurMultiplier() { return blurMultiplier; }
        public void setBlurMultiplier(double blurMultiplier) { this.blurMultiplier = Math.max(0.0, blurMultiplier); }
        
        public Vector3 getLittleWaveDirection() { return littleWaveDirection; }
        public void setLittleWaveDirection(Vector3 littleWaveDirection) { this.littleWaveDirection = littleWaveDirection; }
        
        public Vector3 getBigWaveDirection() { return bigWaveDirection; }
        public void setBigWaveDirection(Vector3 bigWaveDirection) { this.bigWaveDirection = bigWaveDirection; }
        
        public Map<String, Object> toLLSD() {
            Map<String, Object> data = new HashMap<>();
            data.put("WaterColor", Arrays.asList(waterColor.x, waterColor.y, waterColor.z));
            data.put("WaterFogColor", Arrays.asList(waterFogColor.x, waterFogColor.y, waterFogColor.z));
            data.put("WaterFogDensity", waterFogDensity);
            data.put("UnderwaterFogModifier", underwaterFogModifier);
            data.put("NormalScale", Arrays.asList(normalScale.x, normalScale.y, normalScale.z));
            if (normalMapTexture != null) data.put("NormalMapTexture", normalMapTexture);
            data.put("FresnelScale", fresnelScale);
            data.put("FresnelOffset", fresnelOffset);
            data.put("ScaleAbove", scaleAbove);
            data.put("ScaleBelow", scaleBelow);
            data.put("BlurMultiplier", blurMultiplier);
            data.put("LittleWaveDirection", Arrays.asList(littleWaveDirection.x, littleWaveDirection.y));
            data.put("BigWaveDirection", Arrays.asList(bigWaveDirection.x, bigWaveDirection.y));
            return data;
        }
    }
    
    /**
     * Default constructor with standard Windlight settings.
     */
    public WindlightEnvironment() {
        this.skySettings = new SkySettings();
        this.waterSettings = new WaterSettings();
        this.sunDirection = new Vector3(0.0, 0.707, 0.707); // 45 degree sun
        this.moonDirection = new Vector3(0.0, -0.707, 0.707); // Opposite to sun
        this.dayTime = 0.5; // Noon
        
        this.ambient = 0.25;
        this.fogDensity = 0.5;
        this.fogColor = new Vector3(0.5, 0.6, 0.7);
        this.fogStart = 20.0;
        this.fogEnd = 500.0;
        
        this.useGammaCurve = true;
        this.exposure = 1.0;
        this.starBrightness = new Vector3(2.0, 2.0, 2.0);
        this.cloudCoverage = 0.4;
        this.cloudDensity = 1.0;
        this.cloudColor = new Vector3(1.0, 1.0, 1.0);
        this.cloudScale = 0.42;
    }
    
    // Getters
    public SkySettings getSkySettings() { return skySettings; }
    public WaterSettings getWaterSettings() { return waterSettings; }
    public Vector3 getSunDirection() { return sunDirection; }
    public Vector3 getMoonDirection() { return moonDirection; }
    public double getDayTime() { return dayTime; }
    public double getAmbient() { return ambient; }
    public double getFogDensity() { return fogDensity; }
    public Vector3 getFogColor() { return fogColor; }
    public double getFogStart() { return fogStart; }
    public double getFogEnd() { return fogEnd; }
    public boolean isUseGammaCurve() { return useGammaCurve; }
    public double getExposure() { return exposure; }
    public Vector3 getStarBrightness() { return starBrightness; }
    public double getCloudCoverage() { return cloudCoverage; }
    public double getCloudDensity() { return cloudDensity; }
    public Vector3 getCloudColor() { return cloudColor; }
    public double getCloudScale() { return cloudScale; }
    
    // Setters
    public void setSkySettings(SkySettings skySettings) {
        this.skySettings = skySettings != null ? skySettings : new SkySettings();
    }
    
    public void setWaterSettings(WaterSettings waterSettings) {
        this.waterSettings = waterSettings != null ? waterSettings : new WaterSettings();
    }
    
    public void setSunDirection(Vector3 sunDirection) {
        this.sunDirection = sunDirection != null ? sunDirection.normalize() : new Vector3(0.0, 0.707, 0.707);
    }
    
    public void setMoonDirection(Vector3 moonDirection) {
        this.moonDirection = moonDirection != null ? moonDirection.normalize() : new Vector3(0.0, -0.707, 0.707);
    }
    
    public void setDayTime(double dayTime) {
        this.dayTime = Math.max(0.0, Math.min(1.0, dayTime));
        updateSunMoonPositions();
    }
    
    public void setAmbient(double ambient) {
        this.ambient = Math.max(0.0, Math.min(1.0, ambient));
    }
    
    public void setFogDensity(double fogDensity) {
        this.fogDensity = Math.max(0.0, fogDensity);
    }
    
    public void setFogColor(Vector3 fogColor) {
        this.fogColor = clampColor(fogColor);
    }
    
    public void setFogStart(double fogStart) {
        this.fogStart = Math.max(0.0, fogStart);
    }
    
    public void setFogEnd(double fogEnd) {
        this.fogEnd = Math.max(fogStart, fogEnd);
    }
    
    public void setUseGammaCurve(boolean useGammaCurve) {
        this.useGammaCurve = useGammaCurve;
    }
    
    public void setExposure(double exposure) {
        this.exposure = Math.max(0.1, Math.min(10.0, exposure));
    }
    
    public void setStarBrightness(Vector3 starBrightness) {
        this.starBrightness = starBrightness != null ? starBrightness : Vector3.ZERO;
    }
    
    public void setCloudCoverage(double cloudCoverage) {
        this.cloudCoverage = Math.max(0.0, Math.min(1.0, cloudCoverage));
    }
    
    public void setCloudDensity(double cloudDensity) {
        this.cloudDensity = Math.max(0.0, cloudDensity);
    }
    
    public void setCloudColor(Vector3 cloudColor) {
        this.cloudColor = clampColor(cloudColor);
    }
    
    public void setCloudScale(double cloudScale) {
        this.cloudScale = Math.max(0.0, cloudScale);
    }
    
    /**
     * Update sun and moon positions based on day time.
     */
    public void updateSunMoonPositions() {
        // Convert day time to radians (0.0 = midnight, 0.5 = noon)
        double angle = (dayTime - 0.25) * 2.0 * Math.PI; // Offset so noon is at top
        
        // Sun position (sinusoidal path across sky)
        double sunElevation = Math.sin(angle);
        double sunAzimuth = Math.cos(angle);
        this.sunDirection = new Vector3(sunAzimuth * 0.707, sunElevation, Math.abs(sunAzimuth) * 0.707).normalize();
        
        // Moon is opposite to sun
        this.moonDirection = sunDirection.multiply(-1.0);
    }
    
    /**
     * Get current light direction (sun during day, moon during night).
     */
    public Vector3 getCurrentLightDirection() {
        double sunInfluence = Math.max(0.0, sunDirection.y); // Y is up
        if (sunInfluence > 0.1) {
            return sunDirection;
        } else {
            return moonDirection;
        }
    }
    
    /**
     * Get current light color based on time of day.
     */
    public Vector3 getCurrentLightColor() {
        double sunInfluence = Math.max(0.0, sunDirection.y);
        
        if (sunInfluence > 0.1) {
            // Daytime - use sun color
            return skySettings.getSunColor().multiply(sunInfluence);
        } else {
            // Nighttime - use moon color (cooler, dimmer)
            double moonInfluence = Math.max(0.0, moonDirection.y) * 0.1; // Much dimmer than sun
            return new Vector3(0.6, 0.7, 0.9).multiply(moonInfluence); // Cool blue moonlight
        }
    }
    
    /**
     * Get current ambient light based on atmospheric scattering.
     */
    public Vector3 getCurrentAmbientColor() {
        Vector3 ambientBase = skySettings.getAmbientColor();
        double sunInfluence = Math.max(0.0, sunDirection.y);
        
        // Blend between ambient and horizon color based on sun position
        Vector3 horizonInfluence = skySettings.getHorizonColor().multiply(sunInfluence * 0.5);
        return ambientBase.add(horizonInfluence).multiply(ambient);
    }
    
    /**
     * Calculate atmospheric perspective color for a given distance.
     */
    public Vector3 getAtmosphericColor(double distance) {
        double fogFactor = 1.0 - Math.exp(-distance * fogDensity / 1000.0);
        fogFactor = Math.max(0.0, Math.min(1.0, fogFactor));
        
        // Blend fog color with horizon color based on sun position
        Vector3 atmosphericColor = fogColor;
        double sunInfluence = Math.max(0.0, sunDirection.y);
        if (sunInfluence > 0.0) {
            atmosphericColor = fogColor.lerp(skySettings.getHorizonColor(), sunInfluence * 0.3);
        }
        
        return atmosphericColor.multiply(fogFactor);
    }
    
    /**
     * Get effective visibility distance based on atmospheric conditions.
     */
    public double getVisibilityDistance() {
        // Base visibility affected by fog density and haze
        double baseVisibility = fogEnd;
        double hazeFactor = skySettings.getHazeDensity() * skySettings.getDensityMultiplier();
        return baseVisibility / (1.0 + hazeFactor);
    }
    
    /**
     * Create LLSD representation of the environment.
     */
    public Map<String, Object> toLLSD() {
        Map<String, Object> envData = new HashMap<>();
        
        envData.put("SkySettings", skySettings.toLLSD());
        envData.put("WaterSettings", waterSettings.toLLSD());
        
        envData.put("SunDirection", Arrays.asList(sunDirection.x, sunDirection.y, sunDirection.z));
        envData.put("MoonDirection", Arrays.asList(moonDirection.x, moonDirection.y, moonDirection.z));
        envData.put("DayTime", dayTime);
        
        envData.put("Ambient", ambient);
        envData.put("FogDensity", fogDensity);
        envData.put("FogColor", Arrays.asList(fogColor.x, fogColor.y, fogColor.z));
        envData.put("FogStart", fogStart);
        envData.put("FogEnd", fogEnd);
        
        envData.put("UseGammaCurve", useGammaCurve);
        envData.put("Exposure", exposure);
        envData.put("StarBrightness", Arrays.asList(starBrightness.x, starBrightness.y, starBrightness.z));
        
        envData.put("CloudCoverage", cloudCoverage);
        envData.put("CloudDensity", cloudDensity);
        envData.put("CloudColor", Arrays.asList(cloudColor.x, cloudColor.y, cloudColor.z));
        envData.put("CloudScale", cloudScale);
        
        return envData;
    }
    
    /**
     * Create environment from LLSD data.
     */
    @SuppressWarnings("unchecked")
    public static WindlightEnvironment fromLLSD(Map<String, Object> data) {
        WindlightEnvironment env = new WindlightEnvironment();
        
        if (data.containsKey("DayTime")) {
            env.setDayTime(((Number) data.get("DayTime")).doubleValue());
        }
        
        if (data.containsKey("SunDirection")) {
            List<Double> sunDir = (List<Double>) data.get("SunDirection");
            env.setSunDirection(new Vector3(sunDir.get(0), sunDir.get(1), sunDir.get(2)));
        }
        
        // Additional property parsing would continue here...
        
        return env;
    }
    
    private static Vector3 clampColor(Vector3 color) {
        if (color == null) return Vector3.ZERO;
        return new Vector3(
            Math.max(0.0, Math.min(5.0, color.x)), // Allow HDR values up to 5.0
            Math.max(0.0, Math.min(5.0, color.y)),
            Math.max(0.0, Math.min(5.0, color.z))
        );
    }
    
    private static Vector3 clampScattering(Vector3 scattering) {
        if (scattering == null) return Vector3.ZERO;
        return new Vector3(
            Math.max(0.0, Math.min(2.0, scattering.x)),
            Math.max(0.0, Math.min(2.0, scattering.y)),
            Math.max(0.0, Math.min(2.0, scattering.z))
        );
    }
    
    @Override
    public String toString() {
        return String.format("WindlightEnvironment[dayTime=%.2f, sunDir=%s, fogDensity=%.2f]",
                           dayTime, sunDirection.toShortString(), fogDensity);
    }
}