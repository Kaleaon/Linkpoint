package com.linkpoint.render.lumiya.core

enum class LumiyaRenderPass {
    WORLD_OPAQUE,
    WORLD_AVATAR,
    WORLD_SKY,
    WORLD_TRANSPARENT,
    /**
     * Glow / emissive double-pass. Re-draws faces with a non-zero
     * SL "glow" value using additive blending so they brighten the
     * already-composited colour. Skipped during interactive throttle
     * — the popping under a fling is more distracting than the
     * absent glow.
     */
    WORLD_EMISSIVE,
    WORLD_WATER,
    WORLD_PARTICLES,
    HUD
}

object LumiyaFramePlanner {

    /**
     * @param interactiveThrottle when true (touch fling, sustained drag),
     *   skip the particle pass to keep the frame budget under control.
     *   The FXAA resolve is still controlled separately at the renderer
     *   level. Mirrors Lumiya's "responsive mode" pause-particles.
     */
    fun createPlan(
        worldPassEnabled: Boolean,
        hasHudElements: Boolean,
        interactiveThrottle: Boolean = false
    ): List<LumiyaRenderPass> {
        val passes = mutableListOf<LumiyaRenderPass>()
        if (worldPassEnabled) {
            passes += LumiyaRenderPass.WORLD_OPAQUE
            passes += LumiyaRenderPass.WORLD_AVATAR
            passes += LumiyaRenderPass.WORLD_SKY
            passes += LumiyaRenderPass.WORLD_TRANSPARENT
            if (!interactiveThrottle) passes += LumiyaRenderPass.WORLD_EMISSIVE
            passes += LumiyaRenderPass.WORLD_WATER
            if (!interactiveThrottle) passes += LumiyaRenderPass.WORLD_PARTICLES
        }
        if (hasHudElements) {
            passes += LumiyaRenderPass.HUD
        }
        return passes
    }
}
