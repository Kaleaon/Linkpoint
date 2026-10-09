package com.linkpoint.render.materials

import com.linkpoint.protocol.textures.TextureEntryParser
import java.util.UUID

/** Backend-neutral material payload used by both Filament and GLES paths. */
data class MaterialDescriptor(
    val baseColor: Float4 = Float4(1f, 1f, 1f, 1f),
    val baseColorTexture: TextureRef? = null,
    val alphaMode: AlphaMode = AlphaMode.BLEND,
    val alphaCutoff: Float = 0.5f,
    val doubleSided: Boolean = false,
    val normalTexture: TextureRef? = null,
    val normalScale: Float = 1.0f,
    val metallicRoughnessTexture: TextureRef? = null,
    val metallicFactor: Float = 0f,
    val roughnessFactor: Float = 0.5f,
    val emissiveTexture: TextureRef? = null,
    val emissiveFactor: Float3 = Float3.ZERO,
    val occlusionTexture: TextureRef? = null,
    val occlusionFactor: Float = 1.0f,
    val uvTransform: UvTransform = UvTransform.IDENTITY
) {
    enum class AlphaMode { OPAQUE, MASK, BLEND }

    data class TextureRef(
        /** UUID on the object face (can be BoM sentinel). */
        val declaredId: UUID,
        /** Final UUID after BoM resolution (or declaredId when not BoM). */
        val resolvedId: UUID,
        /** True iff resolvedId is expected to be fetchable from the asset CDN. */
        val isDownloadable: Boolean = TextureEntryParser.shouldDownload(resolvedId)
    )

    data class Float3(val x: Float, val y: Float, val z: Float) {
        companion object { val ZERO = Float3(0f, 0f, 0f) }
    }

    data class Float4(val x: Float, val y: Float, val z: Float, val w: Float)

    data class UvTransform(
        val scaleS: Float,
        val scaleT: Float,
        val offsetS: Float,
        val offsetT: Float,
        val rotation: Float,
        val precomputedMatrix: FloatArray? = null
    ) {
        val isIdentity: Boolean = scaleS == 1f && scaleT == 1f && offsetS == 0f && offsetT == 0f && rotation == 0f

        val matrix: FloatArray by lazy(LazyThreadSafetyMode.NONE) {
            if (precomputedMatrix != null) {
                precomputedMatrix
            } else if (isIdentity) {
                IDENTITY_MATRIX
            } else {
                computeMatrix(scaleS, scaleT, offsetS, offsetT, rotation)
            }
        }

        companion object {
            val IDENTITY_MATRIX: FloatArray = floatArrayOf(
                1f, 0f, 0f, 0f,
                0f, 1f, 0f, 0f,
                0f, 0f, 1f, 0f,
                0f, 0f, 0f, 1f
            )
            val IDENTITY = UvTransform(1f, 1f, 0f, 0f, 0f, precomputedMatrix = IDENTITY_MATRIX)

            fun computeMatrix(
                scaleS: Float,
                scaleT: Float,
                offsetS: Float,
                offsetT: Float,
                rotation: Float,
                dest: FloatArray = FloatArray(16)
            ): FloatArray {
                val cos = if (rotation == 0f) 1f else kotlin.math.cos(rotation.toDouble()).toFloat()
                val sin = if (rotation == 0f) 0f else kotlin.math.sin(rotation.toDouble()).toFloat()

                dest[0] = cos * scaleS
                dest[1] = sin * scaleS
                dest[2] = 0f
                dest[3] = 0f

                dest[4] = -sin * scaleT
                dest[5] = cos * scaleT
                dest[6] = 0f
                dest[7] = 0f

                dest[8] = 0f
                dest[9] = 0f
                dest[10] = 1f
                dest[11] = 0f

                dest[12] = 0.5f + offsetS - 0.5f * (cos * scaleS - sin * scaleT)
                dest[13] = 0.5f + offsetT - 0.5f * (sin * scaleS + cos * scaleT)
                dest[14] = 0f
                dest[15] = 1f

                return dest
            }
        }
    }
}
