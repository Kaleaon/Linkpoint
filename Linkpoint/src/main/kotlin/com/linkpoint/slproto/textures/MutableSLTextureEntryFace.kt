package com.linkpoint.slproto.textures

import java.util.UUID

class MutableSLTextureEntryFace {
    const val Byte BUMP_MASK = 31
    const val Byte FULLBRIGHT_MASK = 32
    const val Byte MEDIA_MASK = 1
    const val Byte SHINY_MASK = -64
    const val Byte TEX_MAP_MASK = 6
    Float glow = 0.0f
    Int hasAttribute
    Byte materialb = 0
    Byte mediab = 0
    Float offsetU = 1.0f
    Float offsetV = 1.0f
    Float repeatU = 1.0f
    Float repeatV = 1.0f
    Int rgba = -1
    Float rotation = 0.0f
    UUID textureID

    public MutableSLTextureEntryFace(Int i) {
        this.hasAttribute = i
    }

    fun setGlow(Float f) {
        this.glow = f
        this.hasAttribute |= 512
    }

    fun setMaterial(Byte b) {
        this.materialb = b
        this.hasAttribute |= 128
    }

    fun setMedia(Byte b) {
        this.mediab = b
        this.hasAttribute |= 256
    }

    fun setOffsetU(Float f) {
        this.offsetU = f
        this.hasAttribute |= 16
    }

    fun setOffsetV(Float f) {
        this.offsetV = f
        this.hasAttribute |= 32
    }

    fun setRGBA(Int i) {
        this.rgba = i
        this.hasAttribute |= 2
    }

    fun setRepeatU(Float f) {
        this.repeatU = f
        this.hasAttribute |= 4
    }

    fun setRepeatV(Float f) {
        this.repeatV = f
        this.hasAttribute |= 8
    }

    fun setRotation(Float f) {
        this.rotation = f
        this.hasAttribute |= 64
    }

    fun setTextureID(UUID uuid) {
        this.textureID = uuid
        this.hasAttribute |= 1
    }
}
