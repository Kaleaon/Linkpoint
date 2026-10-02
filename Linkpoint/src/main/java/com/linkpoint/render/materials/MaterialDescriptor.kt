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
        val rotation: Float
    ) {
        companion object { val IDENTITY = UvTransform(1f, 1f, 0f, 0f, 0f) }
    }
}
