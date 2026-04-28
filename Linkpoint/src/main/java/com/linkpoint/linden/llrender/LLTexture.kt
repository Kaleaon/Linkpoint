package com.linkpoint.linden.llrender

enum class LLTextureState {
    UNLOADED,
    LOADING,
    LOADED,
    FAILED,
    DISCARDED,
}

abstract class LLTexture {

    var textureId: Int = 0
        protected set
    var width: Int = 0
        protected set
    var height: Int = 0
        protected set
    var components: Int = 4
        protected set

    var state: LLTextureState = LLTextureState.UNLOADED
        protected set

    @set:JvmName("setDiscardLevelProp")
    var discardLevel: Int = 0
        protected set

    val isValid: Boolean get() = textureId != 0 && state == LLTextureState.LOADED

    abstract fun bind(unit: Int = 0)
    abstract fun unbind(unit: Int = 0)
    abstract fun setDiscardLevel(level: Int)

    fun getByteSize(): Long = width.toLong() * height.toLong() * components.toLong()

    override fun toString(): String =
        "LLTexture(id=$textureId, ${width}x${height}x${components}, state=$state)"
}
