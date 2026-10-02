package com.linkpoint.render.materials

import java.util.concurrent.ConcurrentHashMap
import kotlin.math.max
import kotlin.math.min

/**
 * Transcodes glTF 2.0 PBR material parameters into Blinn-Phong specular and diffuse uniforms
 * for OpenGL ES 2.0 compatibility rendering.
 */
object PbrToPhongTranscoder {

    /** Transcoded Blinn-Phong parameters ready for shader uniform binding. */
    data class PhongMaterialParams(
        val diffuseR: Float,
        val diffuseG: Float,
        val diffuseB: Float,
        val diffuseA: Float,
        val specularR: Float,
        val specularG: Float,
        val specularB: Float,
        val specularExponent: Float,
        val hasBaseColorTexture: Boolean,
        val hasNormalMap: Boolean
    ) {
        val diffuseArray: FloatArray
            get() = floatArrayOf(diffuseR, diffuseG, diffuseB, diffuseA)

        val specularArray: FloatArray
            get() = floatArrayOf(specularR, specularG, specularB)
    }

    private data class TranscodeKey(
        val baseColorR: Int,
        val baseColorG: Int,
        val baseColorB: Int,
        val baseColorA: Int,
        val metallicBits: Int,
        val roughnessBits: Int,
        val hasBaseColorTexture: Boolean,
        val hasNormalMap: Boolean
    )

    private const val MAX_CACHE_SIZE = 1024
    private val cache = ConcurrentHashMap<TranscodeKey, PhongMaterialParams>()

    /**
     * Transcodes PBR factors into Blinn-Phong uniforms, caching computed results.
     */
    fun transcode(
        baseColorR: Float,
        baseColorG: Float,
        baseColorB: Float,
        baseColorA: Float,
        metallic: Float,
        roughness: Float,
        hasBaseColorTexture: Boolean = false,
        hasNormalMap: Boolean = false
    ): PhongMaterialParams {
        val key = TranscodeKey(
            baseColorR = (baseColorR * 1000f).toInt(),
            baseColorG = (baseColorG * 1000f).toInt(),
            baseColorB = (baseColorB * 1000f).toInt(),
            baseColorA = (baseColorA * 1000f).toInt(),
            metallicBits = metallic.toRawBits(),
            roughnessBits = roughness.toRawBits(),
            hasBaseColorTexture = hasBaseColorTexture,
            hasNormalMap = hasNormalMap
        )

        cache[key]?.let { return it }

        if (cache.size > MAX_CACHE_SIZE) {
            cache.clear()
        }

        val computed = computePhongParams(
            baseColorR, baseColorG, baseColorB, baseColorA,
            metallic, roughness,
            hasBaseColorTexture, hasNormalMap
        )

        cache[key] = computed
        return computed
    }

    /**
     * Transcodes a [MaterialDescriptor] into [PhongMaterialParams].
     */
    fun transcode(descriptor: MaterialDescriptor): PhongMaterialParams {
        val baseColor = descriptor.baseColor
        val hasBaseColorTexture = descriptor.baseColorTexture != null
        val hasNormalMap = descriptor.normalTexture != null

        return transcode(
            baseColorR = baseColor.x,
            baseColorG = baseColor.y,
            baseColorB = baseColor.z,
            baseColorA = baseColor.w,
            metallic = descriptor.metallicFactor,
            roughness = descriptor.roughnessFactor,
            hasBaseColorTexture = hasBaseColorTexture,
            hasNormalMap = hasNormalMap
        )
    }

    /** Clears the transcoder cache. */
    fun clearCache() {
        cache.clear()
    }

    /** Current number of cached transcoded materials. */
    fun cacheSize(): Int = cache.size

    private fun computePhongParams(
        baseR: Float,
        baseG: Float,
        baseB: Float,
        baseA: Float,
        metallic: Float,
        roughness: Float,
        hasBaseColorTexture: Boolean,
        hasNormalMap: Boolean
    ): PhongMaterialParams {
        // Requirement 4: Fall back to neutral white diffuse when base color texture or color is missing/invalid
        val validBaseR = if (baseR.isNaN()) 1f else baseR.coerceIn(0f, 1f)
        val validBaseG = if (baseG.isNaN()) 1f else baseG.coerceIn(0f, 1f)
        val validBaseB = if (baseB.isNaN()) 1f else baseB.coerceIn(0f, 1f)
        val validBaseA = if (baseA.isNaN()) 1f else baseA.coerceIn(0f, 1f)

        val m = if (metallic.isNaN()) 0f else metallic.coerceIn(0f, 1f)
        val r = if (roughness.isNaN()) 0.5f else roughness.coerceIn(0f, 1f)

        // Diffuse color: dielectrics retain full base color, metals absorb diffuse light
        val diffuseFactor = 1f - m
        val diffR = validBaseR * diffuseFactor
        val diffG = validBaseG * diffuseFactor
        val diffB = validBaseB * diffuseFactor
        val diffA = validBaseA

        // Specular color: dielectrics have ~4% reflectance (0.04), metals specular reflect base color
        val dielectricsF0 = 0.04f
        val specR = lerp(dielectricsF0, validBaseR, m)
        val specG = lerp(dielectricsF0, validBaseG, m)
        val specB = lerp(dielectricsF0, validBaseB, m)

        // Specular exponent (shininess): map roughness r [0, 1] to Blinn-Phong exponent [1, 256]
        // Formula: alpha = (2 / (max(r, 0.02)^4)) - 2
        val safeR = max(r, 0.02f)
        val r4 = safeR * safeR * safeR * safeR
        val rawExponent = (2f / r4) - 2f
        val specularExponent = max(1f, min(256f, rawExponent))

        return PhongMaterialParams(
            diffuseR = diffR,
            diffuseG = diffG,
            diffuseB = diffB,
            diffuseA = diffA,
            specularR = specR,
            specularG = specG,
            specularB = specB,
            specularExponent = specularExponent,
            hasBaseColorTexture = hasBaseColorTexture,
            hasNormalMap = hasNormalMap
        )
    }

    private fun lerp(start: Float, stop: Float, amount: Float): Float {
        return start + (stop - start) * amount
    }
}
