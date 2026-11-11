package com.linkpoint.render.shadow

import com.linkpoint.render.driver.GpuCapabilities
import com.linkpoint.render.driver.GraphicsDriverProbe

/**
 * Classifies target hardware into appropriate performance tiers on app launch
 * based on system hardware attributes, driver profile, GL vendor/renderer strings, and GPU caps.
 */
object GpuTierClassifier {

    /**
     * Classify hardware into [GpuTier] using pre-context driver profile and post-context GPU capabilities.
     */
    fun classifyTier(
        profile: GraphicsDriverProbe.DriverProfile,
        caps: GpuCapabilities
    ): GpuTier {
        val renderer = caps.rendererString.lowercase()
        val vendor = caps.vendorString.lowercase()
        val hardware = profile.hardwareName.lowercase()
        val manufacturer = profile.manufacturer.lowercase()
        val hay = "$renderer $vendor $hardware $manufacturer"

        // Tier 3 explicitly checked first for known budget/legacy mobile GPUs
        if (isBudgetGpu(hay, caps, profile)) {
            return GpuTier.TIER_3
        }

        // Tier 1 explicitly checked for high-end flagship GPUs
        if (isHighEndGpu(hay, caps, profile)) {
            return GpuTier.TIER_1
        }

        // Tier 2 for mid-tier GPUs
        if (isMidRangeGpu(hay, caps, profile)) {
            return GpuTier.TIER_2
        }

        // Fallback heuristics based on GLES version & max texture size
        return when {
            caps.glVersion >= 32 && caps.maxTextureSize >= 8192 && profile.vulkanHardwareLevel >= 1 -> GpuTier.TIER_1
            caps.glVersion >= 31 && caps.maxTextureSize >= 4096 -> GpuTier.TIER_2
            else -> GpuTier.TIER_3
        }
    }

    private fun isBudgetGpu(
        hay: String,
        caps: GpuCapabilities,
        profile: GraphicsDriverProbe.DriverProfile
    ): Boolean {
        // Explicit budget GPU patterns
        val budgetPatterns = listOf(
            "adreno 3", "adreno 4", "adreno 5", "adreno 610", "adreno 612", "adreno 613", "adreno 615",
            "mali-400", "mali-450", "mali-t", "mali-g31", "mali-g51", "powervr ge", "powervr rogue ge"
        )

        for (pattern in budgetPatterns) {
            if (hay.contains(pattern)) return true
        }

        // Extremely low texture limit or low GL ES version
        if (caps.maxTextureSize > 0 && caps.maxTextureSize < 4096) return true
        if (profile.glesMajorVersion in 1..2 || caps.glVersion in 1..29) return true

        return false
    }

    private fun isHighEndGpu(
        hay: String,
        caps: GpuCapabilities,
        profile: GraphicsDriverProbe.DriverProfile
    ): Boolean {
        val highEndPatterns = listOf(
            "adreno 7", "adreno 8", "adreno (tm) 7", "adreno (tm) 8",
            "mali-g7", "mali-g9", "immortalis", "xclipse", "apple",
            "nvidia", "rtx", "geforce", "radeon"
        )

        for (pattern in highEndPatterns) {
            if (hay.contains(pattern)) return true
        }

        if (caps.glVersion >= 32 && profile.vulkanHardwareLevel >= 1 && caps.maxTextureSize >= 8192) {
            return true
        }

        return false
    }

    private fun isMidRangeGpu(
        hay: String,
        caps: GpuCapabilities,
        profile: GraphicsDriverProbe.DriverProfile
    ): Boolean {
        val midRangePatterns = listOf(
            "adreno 6", "mali-g5", "mali-g6", "powervr gm", "tegra"
        )

        for (pattern in midRangePatterns) {
            if (hay.contains(pattern)) return true
        }

        if (caps.glVersion >= 31 && caps.maxTextureSize >= 4096) {
            return true
        }

        return false
    }
}
