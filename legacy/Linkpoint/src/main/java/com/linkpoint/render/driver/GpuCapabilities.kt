package com.linkpoint.render.driver

/**
 * Post-context GPU capability snapshot. Built once by
 * [LumiyaRenderContext.queryGPUCapabilities] after a GL context
 * exists, then read freely by feature gates around the renderer.
 *
 * Splitting this from the pre-context [GraphicsDriverProbe.DriverProfile]
 * matters because some features can only be detected after the
 * driver loads (extension list, max sample count). The two pieces
 * are joined in [resolveFeatureFlags] which produces a single
 * [FeatureFlags] decision used by every subsystem.
 *
 * @property glVersion 30 / 31 / 32 — the highest GLES version the
 *           driver actually negotiates. May be lower than
 *           [GraphicsDriverProbe.DriverProfile.glesMajorVersion]
 *           when an emulator or a buggy driver downgrades.
 * @property vendor [GraphicsDriverProbe.VendorFamily] inferred from
 *           GL_VENDOR / GL_RENDERER (refined past the pre-context
 *           Build.HARDWARE guess once we have GL strings).
 * @property extensions Set of GL extension strings the driver
 *           reports. Pre-tokenised into a HashSet so feature checks
 *           are O(1).
 * @property maxTextureSize / [maxRenderbufferSize] / [maxSamples]
 *           Driver-side hard limits. Producers should clamp their
 *           own targets to these or risk a context-loss-on-bind.
 */
data class GpuCapabilities(
    val glVersion: Int,
    val vendor: GraphicsDriverProbe.VendorFamily,
    val rendererString: String,
    val vendorString: String,
    val versionString: String,
    val extensions: Set<String>,
    val maxTextureSize: Int,
    val maxRenderbufferSize: Int,
    val maxSamples: Int,
    val maxUniformBlockSize: Int,
    val maxAnisotropy: Float
) {
    fun hasExtension(name: String): Boolean = name in extensions

    /** Concise log line — vendor, version, key extension flags. */
    fun summary(): String = buildString {
        append("GL ES $glVersion ($vendorString / $rendererString)")
        append(" maxTex=$maxTextureSize")
        if (maxSamples > 1) append(" msaa=$maxSamples")
        if (maxAnisotropy > 1f) append(" aniso=$maxAnisotropy")
    }

    companion object {
        /** Empty caps used during early init / unit tests. */
        val EMPTY = GpuCapabilities(
            glVersion = 0,
            vendor = GraphicsDriverProbe.VendorFamily.UNKNOWN,
            rendererString = "",
            vendorString = "",
            versionString = "",
            extensions = emptySet(),
            maxTextureSize = 0,
            maxRenderbufferSize = 0,
            maxSamples = 0,
            maxUniformBlockSize = 0,
            maxAnisotropy = 1f
        )
    }
}

/**
 * Per-feature on/off decision, derived from the pre-context driver
 * probe + the post-context GPU caps. One source of truth for every
 * feature gate so the renderer can't ship a feature that contradicts
 * what the driver advertises.
 *
 * Design note: each flag corresponds to exactly one optional code
 * path. Producers that consult these flags should treat `false` as
 * "skip this code entirely", not "fall back to a slower variant" —
 * fallbacks live inside the producer and the flag only decides
 * whether to even attempt.
 */
data class FeatureFlags(
    /**
     * Persistent-mapped UBO via GL_EXT_buffer_storage. Saves per-frame
     * glBufferSubData on Adreno/Mali drivers that implement it. Off
     * if the extension isn't there, off on PowerVR which historically
     * mishandles persistent maps.
     */
    val persistentMappedUBO: Boolean,
    /**
     * Hardware occlusion queries via
     * GL_ANY_SAMPLES_PASSED_CONSERVATIVE. Requires GL ES 3.0+; gated
     * off on devices that don't reach it. Even when supported,
     * [com.linkpoint.render.lumiya.spatial.OcclusionQuerySet] is off
     * by default (see its docstring) — this flag just unblocks the
     * runtime toggle.
     */
    val occlusionQueries: Boolean,
    /**
     * FXAA off-screen pass. Needs FBOs (GLES 3.0+) and at least 4MB of
     * extra color/depth RAM. We also disable on devices with very
     * small max texture sizes since the resolve sampler would
     * otherwise sample outside the framebuffer.
     */
    val fxaa: Boolean,
    /**
     * Planar water reflection / refraction (mWaterRef / mWaterDis).
     * Requires FBOs + a fragment shader version of the water
     * sampler. Off by default and only enabled when explicitly
     * requested by the user — but this flag is the floor that even
     * the user request can't override.
     */
    val waterPlanarReflections: Boolean,
    /** Anisotropic filtering via GL_EXT_texture_filter_anisotropic. */
    val anisotropicFiltering: Boolean,
    /** Compute shaders (GLES 3.1+). Used by future GPU skinning paths. */
    val computeShaders: Boolean,
    /**
     * Vulkan present and at hardware level ≥ 1. Read by future
     * Vulkan-backed renderer; the GLES path ignores this. Producers
     * that want to *prefer* Vulkan should also check that the device
     * meets a deqp date threshold.
     */
    val vulkanReady: Boolean,
    /**
     * Tile-based renderer hint. True for Adreno / Mali / PowerVR;
     * false for desktop-style architectures. Used by passes that
     * benefit from invalidate-after-resolve (FXAA) or want to avoid
     * mid-pass framebuffer reads.
     */
    val tileBasedRenderer: Boolean
) {
    companion object {
        /** Worst-case flags — every optional feature off. */
        val ALL_OFF = FeatureFlags(
            persistentMappedUBO = false,
            occlusionQueries = false,
            fxaa = false,
            waterPlanarReflections = false,
            anisotropicFiltering = false,
            computeShaders = false,
            vulkanReady = false,
            tileBasedRenderer = false
        )
    }
}

/**
 * Combine a pre-context driver profile with a post-context GPU caps
 * snapshot to decide which optional features the renderer should
 * enable. Pure function; trivially testable.
 *
 * Conservative defaults: when in doubt, off. The renderer should
 * *never* die because a feature gate said "on" on a driver that
 * couldn't deliver — better to ship a featureless frame than no
 * frame at all.
 */
fun resolveFeatureFlags(
    profile: GraphicsDriverProbe.DriverProfile,
    caps: GpuCapabilities
): FeatureFlags {
    val gles = caps.glVersion
    val tbr = caps.vendor in setOf(
        GraphicsDriverProbe.VendorFamily.ADRENO,
        GraphicsDriverProbe.VendorFamily.MALI,
        GraphicsDriverProbe.VendorFamily.POWERVR,
        GraphicsDriverProbe.VendorFamily.XCLIPSE
    )

    // PowerVR mishandled GL_EXT_buffer_storage on shipped drivers
    // through ~2022 (lost writes after a context-loss restore).
    // Disable until we have a known-good allow-list.
    val persistent = caps.hasExtension("GL_EXT_buffer_storage") &&
        caps.vendor != GraphicsDriverProbe.VendorFamily.POWERVR

    val occlusion = gles >= 30

    val fxaa = gles >= 30 && caps.maxTextureSize >= 1024

    // Reflections need a second world pass + extra FBOs. Require
    // GLES 3.1 since the shader fetches gl_FragCoord-derived UV
    // perturbation that misbehaves on some 3.0 drivers, and require
    // a max texture size large enough for a half-res 1080p target.
    val reflections = gles >= 31 &&
        caps.maxTextureSize >= 2048 &&
        caps.maxRenderbufferSize >= 2048

    val aniso = caps.hasExtension("GL_EXT_texture_filter_anisotropic") &&
        caps.maxAnisotropy > 1f

    val compute = gles >= 31

    // Vulkan-readiness is purely about whether a future Vulkan
    // backend would have a chance. Ignore by GLES backend.
    val vk = profile.hasVulkan && profile.vulkanHardwareLevel >= 1

    return FeatureFlags(
        persistentMappedUBO = persistent,
        occlusionQueries = occlusion,
        fxaa = fxaa,
        waterPlanarReflections = reflections,
        anisotropicFiltering = aniso,
        computeShaders = compute,
        vulkanReady = vk,
        tileBasedRenderer = tbr
    )
}
