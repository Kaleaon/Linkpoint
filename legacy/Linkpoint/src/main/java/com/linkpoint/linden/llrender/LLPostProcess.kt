package com.linkpoint.linden.llrender

data class LLPostProcSettings(
    val bloomEnabled: Boolean = false,
    val bloomStrength: Float = 0.5f,
    val bloomWidth: Float = 2.0f,
    val nightVisionEnabled: Boolean = false,
    val nightVisionBrightness: Float = 1.5f,
    val dofEnabled: Boolean = false,
    val dofFocalLength: Float = 10.0f,
    val dofAperture: Float = 2.8f,
    val colorCorrectionEnabled: Boolean = false,
    val colorCorrectionBrightness: Float = 1.0f,
    val colorCorrectionContrast: Float = 1.0f,
    val colorCorrectionSaturation: Float = 1.0f,
)

class LLPostProcess {

    var settings: LLPostProcSettings = LLPostProcSettings()
        private set

    private val effectChain: MutableList<String> = mutableListOf()
    private val renderTargets: MutableMap<String, LLRenderTarget> = mutableMapOf()

    var isInitialized: Boolean = false
        private set

    fun initialize(width: Int, height: Int): Boolean {
        renderTargets.clear()
        val rt0 = LLRenderTarget()
        val rt1 = LLRenderTarget()
        if (!rt0.allocate(width, height, LLRenderTargetFormat.RGBA)) return false
        if (!rt1.allocate(width, height, LLRenderTargetFormat.RGBA)) return false
        renderTargets["ping"] = rt0
        renderTargets["pong"] = rt1
        isInitialized = true
        return true
    }

    fun configure(settings: LLPostProcSettings) {
        this.settings = settings
        effectChain.clear()
        if (settings.bloomEnabled) effectChain.add("bloom")
        if (settings.nightVisionEnabled) effectChain.add("nightvision")
        if (settings.dofEnabled) effectChain.add("dof")
        if (settings.colorCorrectionEnabled) effectChain.add("colorcorrect")
    }

    fun apply(source: LLRenderTarget, target: LLRenderTarget): Boolean {
        if (!isInitialized) return false
        if (!source.isAllocated || !target.isAllocated) return false
        var src = source
        var dst = renderTargets["ping"]!!
        for (effect in effectChain) {
            dst.bindTarget()
            applyEffect(effect, src, dst)
            dst.bindScreen()
            val tmp = src
            src = dst
            dst = if (dst === renderTargets["ping"]!!) renderTargets["pong"]!! else renderTargets["ping"]!!
            if (tmp === source) continue
        }
        target.bindTarget()
        src.copyContents(src)
        target.bindScreen()
        return true
    }

    private fun applyEffect(effect: String, src: LLRenderTarget, dst: LLRenderTarget) {
        // no-op in JVM context; real GL would dispatch the appropriate shader pass
    }

    fun getRenderTarget(name: String): LLRenderTarget? = renderTargets[name]

    fun getEffectChain(): List<String> = effectChain.toList()

    fun release() {
        renderTargets.values.forEach { it.release() }
        renderTargets.clear()
        effectChain.clear()
        isInitialized = false
    }
}
