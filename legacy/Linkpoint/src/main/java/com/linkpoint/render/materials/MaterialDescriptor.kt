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

            private fun setIdentityM(m: FloatArray, offset: Int) {
                java.util.Arrays.fill(m, offset, offset + 16, 0f)
                m[offset] = 1f
                m[offset + 5] = 1f
                m[offset + 10] = 1f
                m[offset + 15] = 1f
            }

            private fun translateM(m: FloatArray, offset: Int, x: Float, y: Float, z: Float) {
                for (i in 0..3) {
                    m[offset + 12 + i] += m[offset + i] * x + m[offset + 4 + i] * y + m[offset + 8 + i] * z
                }
            }

            private fun scaleM(m: FloatArray, offset: Int, x: Float, y: Float, z: Float) {
                for (i in 0..3) {
                    m[offset + i] *= x
                    m[offset + 4 + i] *= y
                    m[offset + 8 + i] *= z
                }
            }

            private fun rotateZM(m: FloatArray, offset: Int, angleDegrees: Float) {
                if (angleDegrees == 0f) return
                val radians = Math.toRadians(angleDegrees.toDouble())
                val s = Math.sin(radians).toFloat()
                val c = Math.cos(radians).toFloat()
                val a00 = m[offset]
                val a01 = m[offset + 1]
                val a02 = m[offset + 2]
                val a03 = m[offset + 3]
                val a10 = m[offset + 4]
                val a11 = m[offset + 5]
                val a12 = m[offset + 6]
                val a13 = m[offset + 7]
                m[offset] = a00 * c + a10 * s
                m[offset + 1] = a01 * c + a11 * s
                m[offset + 2] = a02 * c + a12 * s
                m[offset + 3] = a03 * c + a13 * s
                m[offset + 4] = a10 * c - a00 * s
                m[offset + 5] = a11 * c - a01 * s
                m[offset + 6] = a12 * c - a02 * s
                m[offset + 7] = a13 * c - a03 * s
            }

            fun computeMatrix(
                scaleS: Float,
                scaleT: Float,
                offsetS: Float,
                offsetT: Float,
                rotation: Float,
                dest: FloatArray = FloatArray(16)
            ): FloatArray {
                setIdentityM(dest, 0)
                translateM(dest, 0, 0.5f + offsetS, 0.5f + offsetT, 0f)
                if (rotation != 0f) {
                    rotateZM(dest, 0, Math.toDegrees(rotation.toDouble()).toFloat())
                }
                scaleM(dest, 0, scaleS, scaleT, 1f)
                translateM(dest, 0, -0.5f, -0.5f, 0f)
                return dest
            }
        }
    }
}
