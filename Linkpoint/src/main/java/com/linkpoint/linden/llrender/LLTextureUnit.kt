package com.linkpoint.linden.llrender

enum class LLTexEnvMode {
    REPLACE,
    MODULATE,
    DECAL,
    BLEND,
    ADD,
    COMBINE,
}

enum class LLTexTarget {
    TEXTURE_2D,
    TEXTURE_3D,
    TEXTURE_CUBE_MAP,
    TEXTURE_2D_MULTISAMPLE,
}

class LLTextureUnit(val index: Int) {

    var boundTexture: LLTexture? = null
        private set
    var boundTarget: LLTexTarget = LLTexTarget.TEXTURE_2D
        private set
    var envMode: LLTexEnvMode = LLTexEnvMode.MODULATE
        private set
    var enabled: Boolean = false
        private set

    fun bind(texture: LLTexture?, target: LLTexTarget = LLTexTarget.TEXTURE_2D) {
        boundTexture = texture
        boundTarget = target
        enabled = texture != null
        texture?.bind(index)
    }

    fun unbind(target: LLTexTarget = LLTexTarget.TEXTURE_2D) {
        boundTexture?.unbind(index)
        boundTexture = null
        enabled = false
    }

    fun setTextureEnvMode(mode: LLTexEnvMode) {
        envMode = mode
    }

    fun activate() {
        enabled = true
    }

    fun deactivate() {
        enabled = false
    }

    fun isBound(): Boolean = boundTexture != null

    override fun toString(): String =
        "LLTextureUnit[$index](enabled=$enabled, tex=$boundTexture, mode=$envMode)"
}

object LLTextureUnitMgr {

    private val units: Array<LLTextureUnit> = Array(RenderConstants.NUM_TEXTURE_LAYERS.toInt()) { i ->
        LLTextureUnit(i)
    }
    var activeUnit: Int = 0
        private set

    fun getUnit(index: Int): LLTextureUnit {
        require(index in units.indices) { "Texture unit index out of range: $index" }
        return units[index]
    }

    fun setActiveUnit(index: Int) {
        require(index in units.indices) { "Texture unit index out of range: $index" }
        activeUnit = index
    }

    fun unbindAll() {
        units.forEach { it.unbind() }
    }

    fun getNumUnits(): Int = units.size
}
