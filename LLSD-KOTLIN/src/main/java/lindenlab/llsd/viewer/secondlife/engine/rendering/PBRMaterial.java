/*
 * PBR Material - Java implementation of Physically Based Rendering materials
 *
 * Based on Second Life PBR implementation and modern rendering standards
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer.secondlife.engine.rendering;

import lindenlab.llsd.viewer.secondlife.engine.Vector3;
import java.util.*;
import java.util.UUID;

/**
 * Physically Based Rendering (PBR) material system.
 * 
 * <p>This class implements a modern PBR material system compatible with Second Life
 * and modern rendering standards. It supports metallic-roughness workflow with
 * additional features for virtual world rendering.</p>
 * 
 * @since 1.0
 */
public class PBRMaterial {
    
    // Material properties
    private Vector3 baseColor;
    private double metallic;
    private double roughness;
    private Vector3 emissive;
    private double emissiveStrength;
    private double normalStrength;
    private double occlusionStrength;
    private double alphaCutoff;
    private BlendMode blendMode;
    private CullMode cullMode;
    
    // Texture references
    private UUID baseColorTexture;
    private UUID metallicRoughnessTexture;
    private UUID normalTexture;
    private UUID emissiveTexture;
    private UUID occlusionTexture;
    private UUID heightTexture;
    
    // Texture transforms
    private TextureTransform baseColorTransform;
    private TextureTransform metallicRoughnessTransform;
    private TextureTransform normalTransform;
    private TextureTransform emissiveTransform;
    private TextureTransform occlusionTransform;
    private TextureTransform heightTransform;
    
    // Advanced properties
    private double specularFactor;
    private Vector3 specularColorFactor;
    private UUID specularTexture;
    private UUID specularColorTexture;
    private TextureTransform specularTransform;
    private TextureTransform specularColorTransform;
    
    // Clearcoat extension
    private double clearcoatFactor;
    private double clearcoatRoughnessFactor;
    private Vector3 clearcoatNormalScale;
    private UUID clearcoatTexture;
    private UUID clearcoatRoughnessTexture;
    private UUID clearcoatNormalTexture;
    
    // Second Life specific properties
    private boolean fullbright;
    private double glow;
    private double alphaMode;
    private boolean doubleSided;
    
    /**
     * Blend modes for material rendering.
     */
    public enum BlendMode {
        OPAQUE,
        MASK,
        BLEND
    }
    
    /**
     * Face culling modes.
     */
    public enum CullMode {
        NONE,
        FRONT,
        BACK
    }
    
    /**
     * Default constructor with standard PBR values.
     */
    public PBRMaterial() {
        this.baseColor = new Vector3(1.0, 1.0, 1.0);
        this.metallic = 0.0;
        this.roughness = 1.0;
        this.emissive = Vector3.ZERO;
        this.emissiveStrength = 1.0;
        this.normalStrength = 1.0;
        this.occlusionStrength = 1.0;
        this.alphaCutoff = 0.5;
        this.blendMode = BlendMode.OPAQUE;
        this.cullMode = CullMode.BACK;
        
        this.baseColorTransform = new TextureTransform();
        this.metallicRoughnessTransform = new TextureTransform();
        this.normalTransform = new TextureTransform();
        this.emissiveTransform = new TextureTransform();
        this.occlusionTransform = new TextureTransform();
        this.heightTransform = new TextureTransform();
        
        this.specularFactor = 1.0;
        this.specularColorFactor = new Vector3(1.0, 1.0, 1.0);
        this.specularTransform = new TextureTransform();
        this.specularColorTransform = new TextureTransform();
        
        this.clearcoatFactor = 0.0;
        this.clearcoatRoughnessFactor = 0.0;
        this.clearcoatNormalScale = new Vector3(1.0, 1.0, 1.0);
        
        this.fullbright = false;
        this.glow = 0.0;
        this.alphaMode = 1.0;
        this.doubleSided = false;
    }
    
    // Getters
    public Vector3 getBaseColor() { return baseColor; }
    public double getMetallic() { return metallic; }
    public double getRoughness() { return roughness; }
    public Vector3 getEmissive() { return emissive; }
    public double getEmissiveStrength() { return emissiveStrength; }
    public double getNormalStrength() { return normalStrength; }
    public double getOcclusionStrength() { return occlusionStrength; }
    public double getAlphaCutoff() { return alphaCutoff; }
    public BlendMode getBlendMode() { return blendMode; }
    public CullMode getCullMode() { return cullMode; }
    
    public UUID getBaseColorTexture() { return baseColorTexture; }
    public UUID getMetallicRoughnessTexture() { return metallicRoughnessTexture; }
    public UUID getNormalTexture() { return normalTexture; }
    public UUID getEmissiveTexture() { return emissiveTexture; }
    public UUID getOcclusionTexture() { return occlusionTexture; }
    public UUID getHeightTexture() { return heightTexture; }
    
    public TextureTransform getBaseColorTransform() { return baseColorTransform; }
    public TextureTransform getMetallicRoughnessTransform() { return metallicRoughnessTransform; }
    public TextureTransform getNormalTransform() { return normalTransform; }
    public TextureTransform getEmissiveTransform() { return emissiveTransform; }
    public TextureTransform getOcclusionTransform() { return occlusionTransform; }
    public TextureTransform getHeightTransform() { return heightTransform; }
    
    public double getSpecularFactor() { return specularFactor; }
    public Vector3 getSpecularColorFactor() { return specularColorFactor; }
    public UUID getSpecularTexture() { return specularTexture; }
    public UUID getSpecularColorTexture() { return specularColorTexture; }
    
    public double getClearcoatFactor() { return clearcoatFactor; }
    public double getClearcoatRoughnessFactor() { return clearcoatRoughnessFactor; }
    public Vector3 getClearcoatNormalScale() { return clearcoatNormalScale; }
    public UUID getClearcoatTexture() { return clearcoatTexture; }
    public UUID getClearcoatRoughnessTexture() { return clearcoatRoughnessTexture; }
    public UUID getClearcoatNormalTexture() { return clearcoatNormalTexture; }
    
    public boolean isFullbright() { return fullbright; }
    public double getGlow() { return glow; }
    public double getAlphaMode() { return alphaMode; }
    public boolean isDoubleSided() { return doubleSided; }
    
    // Setters with validation
    public void setBaseColor(Vector3 baseColor) {
        this.baseColor = clampColor(baseColor);
    }
    
    public void setMetallic(double metallic) {
        this.metallic = Math.max(0.0, Math.min(1.0, metallic));
    }
    
    public void setRoughness(double roughness) {
        this.roughness = Math.max(0.0, Math.min(1.0, roughness));
    }
    
    public void setEmissive(Vector3 emissive) {
        this.emissive = clampColor(emissive);
    }
    
    public void setEmissiveStrength(double emissiveStrength) {
        this.emissiveStrength = Math.max(0.0, emissiveStrength);
    }
    
    public void setNormalStrength(double normalStrength) {
        this.normalStrength = normalStrength;
    }
    
    public void setOcclusionStrength(double occlusionStrength) {
        this.occlusionStrength = Math.max(0.0, Math.min(1.0, occlusionStrength));
    }
    
    public void setAlphaCutoff(double alphaCutoff) {
        this.alphaCutoff = Math.max(0.0, Math.min(1.0, alphaCutoff));
    }
    
    public void setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode != null ? blendMode : BlendMode.OPAQUE;
    }
    
    public void setCullMode(CullMode cullMode) {
        this.cullMode = cullMode != null ? cullMode : CullMode.BACK;
    }
    
    // Texture setters
    public void setBaseColorTexture(UUID texture) { this.baseColorTexture = texture; }
    public void setMetallicRoughnessTexture(UUID texture) { this.metallicRoughnessTexture = texture; }
    public void setNormalTexture(UUID texture) { this.normalTexture = texture; }
    public void setEmissiveTexture(UUID texture) { this.emissiveTexture = texture; }
    public void setOcclusionTexture(UUID texture) { this.occlusionTexture = texture; }
    public void setHeightTexture(UUID texture) { this.heightTexture = texture; }
    
    // Transform setters
    public void setBaseColorTransform(TextureTransform transform) {
        this.baseColorTransform = transform != null ? transform : new TextureTransform();
    }
    
    public void setMetallicRoughnessTransform(TextureTransform transform) {
        this.metallicRoughnessTransform = transform != null ? transform : new TextureTransform();
    }
    
    public void setNormalTransform(TextureTransform transform) {
        this.normalTransform = transform != null ? transform : new TextureTransform();
    }
    
    public void setEmissiveTransform(TextureTransform transform) {
        this.emissiveTransform = transform != null ? transform : new TextureTransform();
    }
    
    public void setOcclusionTransform(TextureTransform transform) {
        this.occlusionTransform = transform != null ? transform : new TextureTransform();
    }
    
    public void setHeightTransform(TextureTransform transform) {
        this.heightTransform = transform != null ? transform : new TextureTransform();
    }
    
    // Advanced property setters
    public void setSpecularFactor(double specularFactor) {
        this.specularFactor = Math.max(0.0, specularFactor);
    }
    
    public void setSpecularColorFactor(Vector3 specularColorFactor) {
        this.specularColorFactor = clampColor(specularColorFactor);
    }
    
    public void setSpecularTexture(UUID texture) { this.specularTexture = texture; }
    public void setSpecularColorTexture(UUID texture) { this.specularColorTexture = texture; }
    
    public void setClearcoatFactor(double clearcoatFactor) {
        this.clearcoatFactor = Math.max(0.0, Math.min(1.0, clearcoatFactor));
    }
    
    public void setClearcoatRoughnessFactor(double clearcoatRoughnessFactor) {
        this.clearcoatRoughnessFactor = Math.max(0.0, Math.min(1.0, clearcoatRoughnessFactor));
    }
    
    public void setClearcoatNormalScale(Vector3 scale) {
        this.clearcoatNormalScale = scale != null ? scale : new Vector3(1.0, 1.0, 1.0);
    }
    
    public void setClearcoatTexture(UUID texture) { this.clearcoatTexture = texture; }
    public void setClearcoatRoughnessTexture(UUID texture) { this.clearcoatRoughnessTexture = texture; }
    public void setClearcoatNormalTexture(UUID texture) { this.clearcoatNormalTexture = texture; }
    
    // Second Life specific setters
    public void setFullbright(boolean fullbright) { this.fullbright = fullbright; }
    
    public void setGlow(double glow) {
        this.glow = Math.max(0.0, Math.min(1.0, glow));
    }
    
    public void setAlphaMode(double alphaMode) {
        this.alphaMode = Math.max(0.0, Math.min(1.0, alphaMode));
    }
    
    public void setDoubleSided(boolean doubleSided) { this.doubleSided = doubleSided; }
    
    /**
     * Check if this material has transparency.
     */
    public boolean hasTransparency() {
        return blendMode == BlendMode.BLEND || 
               (blendMode == BlendMode.MASK && alphaCutoff < 1.0);
    }
    
    /**
     * Check if this material emits light.
     */
    public boolean isEmissive() {
        return !emissive.isZero() && emissiveStrength > 0.0;
    }
    
    /**
     * Check if this material is metallic.
     */
    public boolean isMetallic() {
        return metallic > 0.5;
    }
    
    /**
     * Check if this material uses clearcoat.
     */
    public boolean hasClearcoat() {
        return clearcoatFactor > 0.0;
    }
    
    /**
     * Get effective roughness (considering clearcoat).
     */
    public double getEffectiveRoughness() {
        if (hasClearcoat()) {
            return Math.max(roughness, clearcoatRoughnessFactor);
        }
        return roughness;
    }
    
    /**
     * Create LLSD representation of this material.
     */
    public Map<String, Object> toLLSD() {
        Map<String, Object> materialData = new HashMap<>();
        
        // Basic properties
        materialData.put("BaseColor", Arrays.asList(baseColor.x, baseColor.y, baseColor.z));
        materialData.put("Metallic", metallic);
        materialData.put("Roughness", roughness);
        materialData.put("Emissive", Arrays.asList(emissive.x, emissive.y, emissive.z));
        materialData.put("EmissiveStrength", emissiveStrength);
        materialData.put("NormalStrength", normalStrength);
        materialData.put("OcclusionStrength", occlusionStrength);
        materialData.put("AlphaCutoff", alphaCutoff);
        materialData.put("BlendMode", blendMode.name());
        materialData.put("CullMode", cullMode.name());
        
        // Textures
        Map<String, Object> textures = new HashMap<>();
        if (baseColorTexture != null) textures.put("BaseColor", baseColorTexture);
        if (metallicRoughnessTexture != null) textures.put("MetallicRoughness", metallicRoughnessTexture);
        if (normalTexture != null) textures.put("Normal", normalTexture);
        if (emissiveTexture != null) textures.put("Emissive", emissiveTexture);
        if (occlusionTexture != null) textures.put("Occlusion", occlusionTexture);
        if (heightTexture != null) textures.put("Height", heightTexture);
        materialData.put("Textures", textures);
        
        // Texture transforms
        Map<String, Object> transforms = new HashMap<>();
        transforms.put("BaseColor", baseColorTransform.toLLSD());
        transforms.put("MetallicRoughness", metallicRoughnessTransform.toLLSD());
        transforms.put("Normal", normalTransform.toLLSD());
        transforms.put("Emissive", emissiveTransform.toLLSD());
        transforms.put("Occlusion", occlusionTransform.toLLSD());
        transforms.put("Height", heightTransform.toLLSD());
        materialData.put("TextureTransforms", transforms);
        
        // Extensions
        Map<String, Object> extensions = new HashMap<>();
        
        // Specular extension
        if (specularFactor != 1.0 || specularTexture != null) {
            Map<String, Object> specular = new HashMap<>();
            specular.put("SpecularFactor", specularFactor);
            specular.put("SpecularColorFactor", Arrays.asList(
                specularColorFactor.x, specularColorFactor.y, specularColorFactor.z));
            if (specularTexture != null) specular.put("SpecularTexture", specularTexture);
            if (specularColorTexture != null) specular.put("SpecularColorTexture", specularColorTexture);
            extensions.put("KHR_materials_specular", specular);
        }
        
        // Clearcoat extension
        if (hasClearcoat()) {
            Map<String, Object> clearcoat = new HashMap<>();
            clearcoat.put("ClearcoatFactor", clearcoatFactor);
            clearcoat.put("ClearcoatRoughnessFactor", clearcoatRoughnessFactor);
            clearcoat.put("ClearcoatNormalScale", Arrays.asList(
                clearcoatNormalScale.x, clearcoatNormalScale.y, clearcoatNormalScale.z));
            if (clearcoatTexture != null) clearcoat.put("ClearcoatTexture", clearcoatTexture);
            if (clearcoatRoughnessTexture != null) clearcoat.put("ClearcoatRoughnessTexture", clearcoatRoughnessTexture);
            if (clearcoatNormalTexture != null) clearcoat.put("ClearcoatNormalTexture", clearcoatNormalTexture);
            extensions.put("KHR_materials_clearcoat", clearcoat);
        }
        
        materialData.put("Extensions", extensions);
        
        // Second Life specific
        Map<String, Object> secondLife = new HashMap<>();
        secondLife.put("Fullbright", fullbright);
        secondLife.put("Glow", glow);
        secondLife.put("AlphaMode", alphaMode);
        secondLife.put("DoubleSided", doubleSided);
        materialData.put("SecondLife", secondLife);
        
        return materialData;
    }
    
    /**
     * Create material from LLSD data.
     */
    @SuppressWarnings("unchecked")
    public static PBRMaterial fromLLSD(Map<String, Object> data) {
        PBRMaterial material = new PBRMaterial();
        
        // Basic properties
        if (data.containsKey("BaseColor")) {
            List<Double> color = (List<Double>) data.get("BaseColor");
            material.baseColor = new Vector3(color.get(0), color.get(1), color.get(2));
        }
        
        if (data.containsKey("Metallic")) {
            material.metallic = ((Number) data.get("Metallic")).doubleValue();
        }
        
        if (data.containsKey("Roughness")) {
            material.roughness = ((Number) data.get("Roughness")).doubleValue();
        }
        
        if (data.containsKey("Emissive")) {
            List<Double> emissive = (List<Double>) data.get("Emissive");
            material.emissive = new Vector3(emissive.get(0), emissive.get(1), emissive.get(2));
        }
        
        // Additional properties...
        // (Implementation would continue for all properties)
        
        return material;
    }
    
    private Vector3 clampColor(Vector3 color) {
        if (color == null) return Vector3.ZERO;
        return new Vector3(
            Math.max(0.0, Math.min(1.0, color.x)),
            Math.max(0.0, Math.min(1.0, color.y)),
            Math.max(0.0, Math.min(1.0, color.z))
        );
    }
    
    @Override
    public String toString() {
        return String.format("PBRMaterial[baseColor=%s, metallic=%.2f, roughness=%.2f]",
                           baseColor.toShortString(), metallic, roughness);
    }
}