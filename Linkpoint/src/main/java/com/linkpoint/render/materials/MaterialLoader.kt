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
 * Based on Lumiya's material handling approach where simple default
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

        // Simple lit material for prims with basic lighting
        private const val LIT_MATERIAL_SOURCE = """
            material {
                name : LitDefault,
                shadingModel : lit,
                parameters : [
                    { type : float4, name : baseColor },
                    { type : float, name : metallic },
                    { type : float, name : roughness }
                ]
            }
            fragment {
                void material(inout MaterialInputs material) {
                    prepareMaterial(material);
                    material.baseColor = materialParams.baseColor;
                    material.metallic = materialParams.metallic;
                    material.roughness = materialParams.roughness;
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

            val result = builder.build(engine.jobSystem)
            if (result == null) {
                Log.e(TAG, "MaterialBuilder.build() returned null for $name")
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
