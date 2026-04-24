package com.linkpoint.render.lumiya.core

enum class LumiyaRenderPass {
    WORLD_OPAQUE,
    WORLD_AVATAR,
    WORLD_SKY,
    WORLD_TRANSPARENT,
    WORLD_WATER,
    WORLD_PARTICLES,
    HUD
}

object LumiyaFramePlanner {

    fun createPlan(
        worldPassEnabled: Boolean,
        hasHudElements: Boolean
    ): List<LumiyaRenderPass> {
        val passes = mutableListOf<LumiyaRenderPass>()
        if (worldPassEnabled) {
            passes += LumiyaRenderPass.WORLD_OPAQUE
            passes += LumiyaRenderPass.WORLD_AVATAR
            passes += LumiyaRenderPass.WORLD_SKY
            passes += LumiyaRenderPass.WORLD_TRANSPARENT
            passes += LumiyaRenderPass.WORLD_WATER
            passes += LumiyaRenderPass.WORLD_PARTICLES
        }
        if (hasHudElements) {
            passes += LumiyaRenderPass.HUD
        }
        return passes
    }
}
