package com.linkpoint.slproto.textures

import java.util.UUID

class MutableSLTextureEntryFace {
    const val Byte BUMP_MASK = 31
    const val Byte FULLBRIGHT_MASK = 32
    const val Byte MEDIA_MASK = 1
    const val Byte SHINY_MASK = -64
    const val Byte TEX_MAP_MASK = 6
    val glow: Float = 0.0f
    Int hasAttribute
    val materialb: Byte = 0
    val mediab: Byte = 0
    val offsetU: Float = 1.0f
    val offsetV: Float = 1.0f
    val repeatU: Float = 1.0f
    val repeatV: Float = 1.0f
    val rgba: Int = -1
    val rotation: Float = 0.0f
    UUID textureID

    public MutableSLTextureEntryFace(Int i) {
        this.hasAttribute = i
    }

    fun setGlow(f: Float) {
        this.glow = f
        this.hasAttribute |= 512
    }

    fun setMaterial(b: Byte) {
        this.materialb = b
        this.hasAttribute |= 128
    }

    fun setMedia(b: Byte) {
        this.mediab = b
        this.hasAttribute |= 256
    }

    fun setOffsetU(f: Float) {
        this.offsetU = f
        this.hasAttribute |= 16
    }

    fun setOffsetV(f: Float) {
        this.offsetV = f
        this.hasAttribute |= 32
    }

    fun setRGBA(i: Int) {
        this.rgba = i
        this.hasAttribute |= 2
    }

    fun setRepeatU(f: Float) {
        this.repeatU = f
        this.hasAttribute |= 4
    }

    fun setRepeatV(f: Float) {
        this.repeatV = f
        this.hasAttribute |= 8
    }

    fun setRotation(f: Float) {
        this.rotation = f
        this.hasAttribute |= 64
    }

    fun setTextureID(uuid: UUID) {
        this.textureID = uuid
        this.hasAttribute |= 1
    }
}
