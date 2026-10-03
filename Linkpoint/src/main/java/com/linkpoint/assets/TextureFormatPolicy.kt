package com.linkpoint.assets

/**
 * Shared texture format policy for both render backends (Filament + Lumiya GL).
 *
 * This keeps format selection, mip level count, and color-space conventions
 * aligned so decode/transcode outputs can be consumed consistently.
 */
object TextureFormatPolicy {

    enum class Backend {
        FILAMENT,
        LUMIYA_GL
    }

    enum class TextureSemantic {
        /** Color textures sampled in perceptual space. */
        ALBEDO,
        /** Non-color textures (normal/metal/roughness/etc) sampled in linear space. */
        DATA
    }

    enum class TargetFormat {
        ETC2_RGBA,
        RGBA8,
        FALLBACK
    }

    data class DeviceCapabilities(
        val supportsEtc2Rgba: Boolean,
        val supportsRgba8: Boolean = true,
        val supportsBasisTranscoding: Boolean = false,
        val supportsEtcpak: Boolean = false,
    )

    data class Decision(
        val targetFormat: TargetFormat,
        val semantic: TextureSemantic,
        val srgb: Boolean,
        val mipLevels: Int,
        val useBasisPath: Boolean,
        val useEtcpakPath: Boolean,
    )

    fun decide(
        backend: Backend,
        semantic: TextureSemantic,
        width: Int,
        height: Int,
        capabilities: DeviceCapabilities
    ): Decision {
        val format = when {
            capabilities.supportsEtc2Rgba -> TargetFormat.ETC2_RGBA
            capabilities.supportsRgba8 -> TargetFormat.RGBA8
            else -> TargetFormat.FALLBACK
        }
        return Decision(
            targetFormat = format,
            semantic = semantic,
            srgb = semantic == TextureSemantic.ALBEDO,
            mipLevels = mipLevelsFor(width, height),
            useBasisPath = capabilities.supportsBasisTranscoding,
            useEtcpakPath = capabilities.supportsEtcpak && format == TargetFormat.ETC2_RGBA,
        )
    }

    fun mipLevelsFor(width: Int, height: Int): Int {
        var d = maxOf(width, height).coerceAtLeast(1)
        var levels = 1
        while (d > 1) {
            d = d shr 1
            levels++
        }
        return levels
    }
}

