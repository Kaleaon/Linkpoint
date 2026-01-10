package com.linkpoint.slproto.textures

import java.util.UUID

class MutableSLTextureEntryFace {
    val BUMP_MASK: Byte = 31
    val FULLBRIGHT_MASK: Byte = 32
    val MEDIA_MASK: Byte = 1
    val SHINY_MASK: Byte = -64
    val TEX_MAP_MASK: Byte = 6
    var glow: Float = 0.0f
    Int hasAttribute
    Byte materialb = 0
    Byte mediab = 0
    var offsetU: Float = 1.0f
    var offsetV: Float = 1.0f
    var repeatU: Float = 1.0f
    var repeatV: Float = 1.0f
    var rgba: Int = -1
    var rotation: Float = 0.0f
    UUID textureID

    MutableSLTextureEntryFace(Int i) {
        this.hasAttribute = i
    }

    fun setGlow(Float f)  {
        this.glow = f
        this.hasAttribute |= 512
    }

    fun setMaterial(Byte b)  {
        this.materialb = b
        this.hasAttribute |= 128
    }

    fun setMedia(Byte b)  {
        this.mediab = b
        this.hasAttribute |= 256
    }

    fun setOffsetU(Float f)  {
        this.offsetU = f
        this.hasAttribute |= 16
    }

    fun setOffsetV(Float f)  {
        this.offsetV = f
        this.hasAttribute |= 32
    }

    fun setRGBA(Int i)  {
        this.rgba = i
        this.hasAttribute |= 2
    }

    fun setRepeatU(Float f)  {
        this.repeatU = f
        this.hasAttribute |= 4
    }

    fun setRepeatV(Float f)  {
        this.repeatV = f
        this.hasAttribute |= 8
    }

    fun setRotation(Float f)  {
        this.rotation = f
        this.hasAttribute |= 64
    }

    fun setTextureID(UUID uuid)  {
        this.textureID = uuid
        this.hasAttribute |= 1
    }
}
