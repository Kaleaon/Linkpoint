package com.linkpoint.slproto.textures

import java.util.UUID

class MutableSLTextureEntryFace {
    val BUMP_MASK: Byte = 31
    val FULLBRIGHT_MASK: Byte = 32
    val MEDIA_MASK: Byte = 1
    val SHINY_MASK: Byte = -64
    val TEX_MAP_MASK: Byte = 6
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

    MutableSLTextureEntryFace(Int i) {
        this.hasAttribute = i
    }

    fun setGlow(Float f): Unit {
        this.glow = f
        this.hasAttribute |= 512
    }

    fun setMaterial(Byte b): Unit {
        this.materialb = b
        this.hasAttribute |= 128
    }

    fun setMedia(Byte b): Unit {
        this.mediab = b
        this.hasAttribute |= 256
    }

    fun setOffsetU(Float f): Unit {
        this.offsetU = f
        this.hasAttribute |= 16
    }

    fun setOffsetV(Float f): Unit {
        this.offsetV = f
        this.hasAttribute |= 32
    }

    fun setRGBA(Int i): Unit {
        this.rgba = i
        this.hasAttribute |= 2
    }

    fun setRepeatU(Float f): Unit {
        this.repeatU = f
        this.hasAttribute |= 4
    }

    fun setRepeatV(Float f): Unit {
        this.repeatV = f
        this.hasAttribute |= 8
    }

    fun setRotation(Float f): Unit {
        this.rotation = f
        this.hasAttribute |= 64
    }

    fun setTextureID(UUID uuid): Unit {
        this.textureID = uuid
        this.hasAttribute |= 1
    }
}
