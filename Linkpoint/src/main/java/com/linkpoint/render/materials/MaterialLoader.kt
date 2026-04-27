package com.linkpoint.render.materials

import android.content.Context
import android.util.Log
import com.google.android.filament.Engine
import com.google.android.filament.Material
import com.google.android.filament.MaterialInstance
import com.google.android.filament.filamat.MaterialBuilder
import com.google.android.filament.filamat.MaterialPackage
import java.nio.ByteBuffer

/**
 * Loads and manages Filament materials for rendering.
 * Uses filamat to compile materials at runtime from .mat source files.
 *
 * Based on the reference viewer's material handling approach where simple default
 * materials are used for prims until textures are loaded.
 */
class MaterialLoader(
    private val context: Context,
    private val engine: Engine
) {
    companion object {
        private const val TAG = "MaterialLoader"

        // Simple unlit material source for basic prim rendering
        // This provides a visible placeholder until proper textures load
        private const val UNLIT_MATERIAL_SOURCE = """
            material {
                name : UnlitDefault,
                shadingModel : unlit,
                parameters : [
                    { type : float4, name : baseColor }
                ]
            }
            fragment {
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);
                    material.baseColor = materialParams.baseColor;
                }
            }
        """

        // Lit material with per-face PBR + optional base color texture +
        // texture transform (scale / offset / rotation from TextureEntry).
        //
        //   baseColor      tint applied to the texture sample
        //   baseColorMap   diffuse texture (defaults to a 1×1 white via
        //                  hasTexture=0 -> we just skip the sample)
        //   hasTexture     0 = ignore baseColorMap; 1 = sample it
        //   texScale       (sx, sy) UV repeat
        //   texOffset      (ox, oy) UV offset
        //   texRotation    rotation around (0.5, 0.5)
        //   metallic, roughness
        private const val LIT_MATERIAL_SOURCE = """
            material {
                name : LitDefault,
                shadingModel : lit,
                requires : [ uv0 ],
                parameters : [
                    { type : float4,    name : baseColor },
                    { type : sampler2d, name : baseColorMap },
                    { type : float,     name : hasTexture },
                    { type : sampler2d, name : normalMap },
                    { type : float,     name : hasNormalMap },
                    { type : float2,    name : texScale },
                    { type : float2,    name : texOffset },
                    { type : float,     name : texRotation },
                    { type : float,     name : metallic },
                    { type : float,     name : roughness }
                ]
            }
            fragment {
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);
                    float2 uv = getUV0();
                    // Apply rotation around (0.5, 0.5), then scale, then offset.
                    float c = cos(materialParams.texRotation);
                    float s = sin(materialParams.texRotation);
                    float2 centred = uv - float2(0.5, 0.5);
                    float2 rotated = float2(centred.x * c - centred.y * s,
                                            centred.x * s + centred.y * c);
                    uv = rotated + float2(0.5, 0.5);
                    uv = uv * materialParams.texScale + materialParams.texOffset;

                    float4 sampled = float4(1.0, 1.0, 1.0, 1.0);
                    if (materialParams.hasTexture > 0.5) {
                        sampled = texture(materialParams_baseColorMap, uv);
                    }
                    material.baseColor = materialParams.baseColor * sampled;
                    if (materialParams.hasNormalMap > 0.5) {
                        // Tangent-space normal: read [0,1], remap to [-1,1].
                        // Filament's TBN is rebuilt from the packed
                        // TANGENTS quaternion attribute (a real
                        // tangent/bitangent/normal frame).
                        float3 n = texture(materialParams_normalMap, uv).xyz * 2.0 - 1.0;
                        material.normal = n;
                    }
                    material.metallic = materialParams.metallic;
                    material.roughness = materialParams.roughness;
                }
            }
        """

        // Terrain material with 4-channel splatting and elevation-based blending.
        //
        // SL terrain blends between four detail textures based on the vertex's
        // world Z (height) using two interpolated thresholds per region
        // corner ("low" and "high"). Each corner of the 256m region has its
        // own [start, range] pair (4 floats each, packed into two float4
        // params); we pick the blend bounds at each fragment by bilinearly
        // interpolating the corner params from the world-space XY position.
        //
        // The four detail textures are tiled at a fixed UV scale (~16m) and
        // blended into a single base colour. This is the minimum-viable
        // splatting: full LL parity would also include slope-based blending,
        // bump maps, and per-region overrides, but elevation-only is what
        // actually shows up at typical viewer settings.
        private const val TERRAIN_MATERIAL_SOURCE = """
            material {
                name : TerrainSplat,
                shadingModel : lit,
                requires : [ uv0, uv1 ],
                parameters : [
                    { type : sampler2d, name : detail0 },
                    { type : sampler2d, name : detail1 },
                    { type : sampler2d, name : detail2 },
                    { type : sampler2d, name : detail3 },
                    { type : float4,    name : startHeights },
                    { type : float4,    name : heightRanges },
                    { type : float,     name : detailScale }
                ]
            }
            fragment {
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);

                    // UV0 is normalised region position (0..1 in X and Y).
                    float2 uv = getUV0();
                    // UV1.x carries the vertex's world-space Z (height).
                    float h = getUV1().x;

                    // Bilinearly interpolate corner blend params from XY in
                    // the region. corner order: (0,0) (1,0) (0,1) (1,1).
                    float wx = clamp(uv.x, 0.0, 1.0);
                    float wy = clamp(uv.y, 0.0, 1.0);
                    float s00 = materialParams.startHeights.x;
                    float s10 = materialParams.startHeights.y;
                    float s01 = materialParams.startHeights.z;
                    float s11 = materialParams.startHeights.w;
                    float r00 = materialParams.heightRanges.x;
                    float r10 = materialParams.heightRanges.y;
                    float r01 = materialParams.heightRanges.z;
                    float r11 = materialParams.heightRanges.w;
                    float startHeight = mix(mix(s00, s10, wx), mix(s01, s11, wx), wy);
                    float heightRange = mix(mix(r00, r10, wx), mix(r01, r11, wx), wy);
                    float t = clamp((h - startHeight) / max(heightRange, 0.001), 0.0, 1.0);

                    // Three blend zones: detail0 -> detail1 -> detail2 -> detail3
                    // mapped to t in [0, 1/3, 2/3, 1].
                    float scale = max(materialParams.detailScale, 0.001);
                    float2 dUV = uv * (256.0 / scale);
                    float4 c0 = texture(materialParams_detail0, dUV);
                    float4 c1 = texture(materialParams_detail1, dUV);
                    float4 c2 = texture(materialParams_detail2, dUV);
                    float4 c3 = texture(materialParams_detail3, dUV);

                    float4 col;
                    if (t < 0.3333) {
                        col = mix(c0, c1, t * 3.0);
                    } else if (t < 0.6666) {
                        col = mix(c1, c2, (t - 0.3333) * 3.0);
                    } else {
                        col = mix(c2, c3, (t - 0.6666) * 3.0);
                    }

                    // Slope-based blend: cliff faces (vertical normal -> low
                    // normal.z) bias toward detail3 (typically rock in the
                    // SL ground texture set), matching how cliffs look on
                    // the desktop viewer.
                    float3 wn = getWorldGeometricNormalVector();
                    float slope = clamp(1.0 - abs(wn.z), 0.0, 1.0);
                    // Smooth threshold so flat areas keep the elevation blend.
                    slope = smoothstep(0.4, 0.85, slope);
                    col = mix(col, c3, slope);

                    material.baseColor = col;
                    material.metallic = 0.0;
                    material.roughness = 0.95;
                }
            }
        """

        init {
            // Initialize MaterialBuilder (required before any material compilation)
            MaterialBuilder.init()
        }
    }

    private var unlitMaterial: Material? = null
    private var litMaterial: Material? = null
    private var terrainMaterial: Material? = null
    private val customMaterials = mutableMapOf<String, Material>()

    /**
     * Initialize default materials.
     * Must be called after Engine is created.
     */
    fun initialize(): Boolean {
        try {
            Log.i(TAG, "Initializing MaterialLoader...")

            // Compile and create unlit default material
            unlitMaterial = compileMaterial(UNLIT_MATERIAL_SOURCE, "UnlitDefault")
            if (unlitMaterial == null) {
                Log.e(TAG, "Failed to create unlit material")
                return false
            }

            // Compile and create lit default material
            litMaterial = compileMaterial(LIT_MATERIAL_SOURCE, "LitDefault")
            if (litMaterial == null) {
                Log.e(TAG, "Failed to create lit material")
                return false
            }

            // Terrain splatting material; non-fatal if it fails to compile —
            // TerrainRenderer will fall back to the lit material with no
            // detail textures.
            terrainMaterial = compileMaterial(TERRAIN_MATERIAL_SOURCE, "TerrainSplat")
            if (terrainMaterial == null) {
                Log.w(TAG, "Failed to compile terrain material; falling back to lit material")
            }

            Log.i(TAG, "MaterialLoader initialized successfully")
            return true

        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize MaterialLoader", e)
            return false
        }
    }

    /**
     * Compile a material from source string.
     */
    private fun compileMaterial(source: String, name: String): Material? {
        return try {
            val builder = MaterialBuilder()
                .platform(MaterialBuilder.Platform.MOBILE)
                .targetApi(MaterialBuilder.TargetApi.OPENGL)
                .optimization(MaterialBuilder.Optimization.PERFORMANCE)
                .material(source)

            val result = builder.build()
            if (result == null) {
                Log.e(TAG, "MaterialBuilder.build() returned null for $name - shader compilation may have failed")
                Log.e(TAG, "Material source preview: ${source.take(200)}...")
                return null
            }

            if (!result.isValid) {
                Log.e(TAG, "Material $name is not valid")
                return null
            }

            val packageData = result.buffer
            val material = Material.Builder()
                .payload(packageData, packageData.remaining())
                .build(engine)

            Log.d(TAG, "Compiled material: $name")
            material

        } catch (e: Exception) {
            Log.e(TAG, "Failed to compile material $name", e)
            null
        }
    }

    /**
     * Load a material from assets folder.
     */
    fun loadMaterialFromAssets(assetPath: String): Material? {
        // Check cache first
        customMaterials[assetPath]?.let { return it }

        return try {
            val inputStream = context.assets.open(assetPath)
            val source = inputStream.bufferedReader().use { it.readText() }
            inputStream.close()

            val material = compileMaterial(source, assetPath)
            if (material != null) {
                customMaterials[assetPath] = material
            }
            material

        } catch (e: Exception) {
            Log.e(TAG, "Failed to load material from $assetPath", e)
            null
        }
    }

    /**
     * Get the default unlit material.
     */
    fun getUnlitMaterial(): Material? = unlitMaterial

    /**
     * Get the default lit material.
     */
    fun getLitMaterial(): Material? = litMaterial

    /**
     * Get the terrain splatting material. May be null if compilation failed;
     * callers should fall back to the lit material in that case.
     */
    fun getTerrainMaterial(): Material? = terrainMaterial

    /**
     * Create a material instance with a specific color.
     * Uses the unlit material for simple colored objects.
     */
    fun createColoredInstance(r: Float, g: Float, b: Float, a: Float = 1f): MaterialInstance? {
        val material = unlitMaterial ?: return null
        val instance = material.createInstance()
        instance.setParameter("baseColor", r, g, b, a)
        return instance
    }

    /**
     * Create a lit material instance with color and PBR parameters.
     */
    fun createLitInstance(
        r: Float, g: Float, b: Float, a: Float = 1f,
        metallic: Float = 0f,
        roughness: Float = 0.5f
    ): MaterialInstance? {
        val material = litMaterial ?: return null
        val instance = material.createInstance()
        instance.setParameter("baseColor", r, g, b, a)
        instance.setParameter("metallic", metallic)
        instance.setParameter("roughness", roughness)
        // Default: no texture, identity UV transform.
        instance.setParameter("hasTexture", 0f)
        instance.setParameter("hasNormalMap", 0f)
        instance.setParameter("texScale", 1f, 1f)
        instance.setParameter("texOffset", 0f, 0f)
        instance.setParameter("texRotation", 0f)
        return instance
    }

    /**
     * Create a default gray material instance for placeholder objects.
     */
    fun createDefaultInstance(): MaterialInstance? {
        // Light gray color similar to SL default prim color
        return createLitInstance(0.8f, 0.8f, 0.8f, 1f, 0f, 0.5f)
    }

    /**
     * Shutdown and release all materials.
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down MaterialLoader")

        customMaterials.values.forEach { engine.destroyMaterial(it) }
        customMaterials.clear()

        unlitMaterial?.let { engine.destroyMaterial(it) }
        litMaterial?.let { engine.destroyMaterial(it) }

        unlitMaterial = null
        litMaterial = null

        MaterialBuilder.shutdown()
    }
}
