package com.linkpoint.graphics.filament

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Material
import com.google.android.filament.filamat.MaterialBuilder
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.concurrent.ConcurrentHashMap

/**
 * FilamentMaterialManager - Manages material loading and caching for Filament
 * 
 * Handles:
 * - Loading precompiled materials from assets
 * - Runtime material compilation (fallback)
 * - Material caching
 * - Material lifecycle
 */
class FilamentMaterialManager(
    private val context: Context,
    private val engine: Engine
) {
    companion object {
        private const val TAG = "FilamentMaterialMgr"
        private const val MATERIALS_PATH = "materials"
    }
    
    // Material types supported
    enum class MaterialType {
        UNLIT_COLOR,      // Simple colored surfaces
        TERRAIN,          // Terrain with textures
        PRIM_BASIC,       // Basic prim with texture
        PRIM_PBR,         // Full PBR prim
        AVATAR_SKIN,      // Avatar skin with SSS
        WATER,            // Water with animations
        SKY,              // Sky rendering
        TRANSPARENT       // Transparent surfaces
    }
    
    // Material cache
    private val materialCache = ConcurrentHashMap<MaterialType, Material>()
    
    // Track if MaterialBuilder is initialized
    private var materialBuilderInitialized = false
    
    /**
     * Preload all essential materials
     */
    fun preloadMaterials() {
        Log.i(TAG, "Preloading materials...")
        
        // Load critical materials
        loadMaterial(MaterialType.UNLIT_COLOR)
        loadMaterial(MaterialType.TERRAIN)
        loadMaterial(MaterialType.PRIM_BASIC)
        loadMaterial(MaterialType.PRIM_PBR)
        
        Log.i(TAG, "Preloaded ${materialCache.size} materials")
    }
    
    /**
     * Get a material by type, loading if necessary
     */
    fun getMaterial(type: MaterialType): Material {
        return materialCache.getOrPut(type) {
            loadMaterial(type)
        }
    }
    
    /**
     * Load a material from assets or compile at runtime
     */
    private fun loadMaterial(type: MaterialType): Material {
        // Try to load precompiled material first
        val filamatName = getFilamatName(type)
        
        try {
            val material = loadPrecompiledMaterial(filamatName)
            if (material != null) {
                Log.i(TAG, "Loaded precompiled material: $filamatName")
                return material
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to load precompiled material $filamatName: ${e.message}")
        }
        
        // Fallback to runtime compilation
        Log.i(TAG, "Compiling material at runtime: $type")
        return compileRuntimeMaterial(type)
    }
    
    /**
     * Load precompiled .filamat material from assets
     */
    private fun loadPrecompiledMaterial(filename: String): Material? {
        return try {
            val path = "$MATERIALS_PATH/$filename"
            val materialData = context.assets.open(path).use { input ->
                val buffer = ByteBuffer.allocateDirect(input.available())
                val channel = Channels.newChannel(input)
                channel.read(buffer)
                buffer.flip()
                buffer
            }
            
            Material.Builder()
                .payload(materialData, materialData.remaining())
                .build(engine)
        } catch (e: Exception) {
            Log.w(TAG, "Could not load $filename: ${e.message}")
            null
        }
    }
    
    /**
     * Compile a material at runtime using MaterialBuilder
     */
    private fun compileRuntimeMaterial(type: MaterialType): Material {
        if (!materialBuilderInitialized) {
            MaterialBuilder.init()
            materialBuilderInitialized = true
        }
        
        val materialPackage = when (type) {
            MaterialType.UNLIT_COLOR -> buildUnlitColorMaterial()
            MaterialType.TERRAIN -> buildTerrainMaterial()
            MaterialType.PRIM_BASIC -> buildPrimBasicMaterial()
            MaterialType.PRIM_PBR -> buildPrimPBRMaterial()
            MaterialType.AVATAR_SKIN -> buildAvatarSkinMaterial()
            MaterialType.WATER -> buildWaterMaterial()
            MaterialType.SKY -> buildSkyMaterial()
            MaterialType.TRANSPARENT -> buildTransparentMaterial()
        }
        
        if (!materialPackage.isValid) {
            throw RuntimeException("Failed to compile material: $type")
        }
        
        return Material.Builder()
            .payload(materialPackage.buffer, materialPackage.size)
            .build(engine)
    }
    
    /**
     * Build unlit color material at runtime
     */
    private fun buildUnlitColorMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("UnlitColor")
            .shading(MaterialBuilder.Shading.UNLIT)
            .require(MaterialBuilder.VertexAttribute.COLOR)
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    material.baseColor = getColor()
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build terrain material at runtime
     */
    private fun buildTerrainMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("Terrain")
            .shading(MaterialBuilder.Shading.LIT)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "baseColorMap")
            .parameter(MaterialBuilder.UniformType.FLOAT, "roughness")
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    vec2 uv = getUV0()
                    vec4 baseColor = texture(materialParams_baseColorMap, uv)
                    material.baseColor = baseColor
                    material.roughness = materialParams.roughness
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build basic prim material at runtime
     */
    private fun buildPrimBasicMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("PrimBasic")
            .shading(MaterialBuilder.Shading.LIT)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "baseColorMap")
            .parameter(MaterialBuilder.UniformType.FLOAT3, "baseColor")
            .parameter(MaterialBuilder.UniformType.FLOAT, "roughness")
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    vec2 uv = getUV0()
                    vec4 texColor = texture(materialParams_baseColorMap, uv)
                    material.baseColor = vec4(texColor.rgb * materialParams.baseColor, texColor.a)
                    material.roughness = materialParams.roughness
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build PBR prim material at runtime
     */
    private fun buildPrimPBRMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("PrimPBR")
            .shading(MaterialBuilder.Shading.LIT)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .require(MaterialBuilder.VertexAttribute.TANGENTS)
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "baseColorMap")
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "normalMap")
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "metallicRoughnessMap")
            .parameter(MaterialBuilder.UniformType.FLOAT, "metallicFactor")
            .parameter(MaterialBuilder.UniformType.FLOAT, "roughnessFactor")
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    vec2 uv = getUV0()
                    vec4 baseColor = texture(materialParams_baseColorMap, uv)
                    vec3 normal = texture(materialParams_normalMap, uv).rgb * 2.0 - 1.0
                    vec4 mr = texture(materialParams_metallicRoughnessMap, uv)
                    
                    material.baseColor = baseColor
                    material.normal = normal
                    material.metallic = mr.b * materialParams.metallicFactor
                    material.roughness = mr.g * materialParams.roughnessFactor
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build avatar skin material at runtime
     */
    private fun buildAvatarSkinMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("AvatarSkin")
            .shading(MaterialBuilder.Shading.SUBSURFACE)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "skinTexture")
            .parameter(MaterialBuilder.UniformType.FLOAT, "roughness")
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    vec2 uv = getUV0()
                    vec4 skinColor = texture(materialParams_skinTexture, uv)
                    material.baseColor = skinColor
                    material.roughness = materialParams.roughness
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build water material at runtime
     */
    private fun buildWaterMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("Water")
            .shading(MaterialBuilder.Shading.LIT)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .parameter(MaterialBuilder.UniformType.FLOAT3, "waterColor")
            .parameter(MaterialBuilder.UniformType.FLOAT, "roughness")
            .blending(MaterialBuilder.BlendingMode.TRANSPARENT)
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    material.baseColor = vec4(materialParams.waterColor, 0.7)
                    material.roughness = materialParams.roughness
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build sky material at runtime
     */
    private fun buildSkyMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("Sky")
            .shading(MaterialBuilder.Shading.UNLIT)
            .parameter(MaterialBuilder.UniformType.FLOAT3, "skyColor")
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    material.baseColor = vec4(materialParams.skyColor, 1.0)
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Build transparent material at runtime
     */
    private fun buildTransparentMaterial(): MaterialBuilder.MaterialPackage {
        return MaterialBuilder()
            .platform(MaterialBuilder.Platform.MOBILE)
            .name("Transparent")
            .shading(MaterialBuilder.Shading.LIT)
            .require(MaterialBuilder.VertexAttribute.UV0)
            .parameter(MaterialBuilder.UniformType.SAMPLER_2D, "baseColorMap")
            .parameter(MaterialBuilder.UniformType.FLOAT, "alpha")
            .blending(MaterialBuilder.BlendingMode.TRANSPARENT)
            .material("""
                void material(inout MaterialInputs material) {
                    prepareMaterial(material)
                    vec2 uv = getUV0()
                    vec4 texColor = texture(materialParams_baseColorMap, uv)
                    material.baseColor = vec4(texColor.rgb, texColor.a * materialParams.alpha)
                }
            """.trimIndent())
            .optimization(MaterialBuilder.Optimization.NONE)
            .build(engine.backend)
    }
    
    /**
     * Get the .filamat filename for a material type
     */
    private fun getFilamatName(type: MaterialType): String {
        return when (type) {
            MaterialType.UNLIT_COLOR -> "unlit_color.filamat"
            MaterialType.TERRAIN -> "terrain.filamat"
            MaterialType.PRIM_BASIC -> "prim_basic.filamat"
            MaterialType.PRIM_PBR -> "prim_pbr.filamat"
            MaterialType.AVATAR_SKIN -> "avatar_skin.filamat"
            MaterialType.WATER -> "water.filamat"
            MaterialType.SKY -> "sky.filamat"
            MaterialType.TRANSPARENT -> "transparent.filamat"
        }
    }
    
    /**
     * Cleanup all materials
     */
    fun destroy() {
        Log.i(TAG, "Destroying ${materialCache.size} materials...")
        
        materialCache.values.forEach { material ->
            try {
                engine.destroyMaterial(material)
            } catch (e: Exception) {
                Log.w(TAG, "Error destroying material: ${e.message}")
            }
        }
        
        materialCache.clear()
        
        if (materialBuilderInitialized) {
            try {
                MaterialBuilder.shutdown()
                materialBuilderInitialized = false
            } catch (e: Exception) {
                Log.w(TAG, "Error shutting down MaterialBuilder: ${e.message}")
            }
        }
        
        Log.i(TAG, "Material manager destroyed")
    }
}
