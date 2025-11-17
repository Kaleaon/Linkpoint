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

    Unit setGlow(Float f) {
        this.glow = f
        this.hasAttribute |= 512
    }

    Unit setMaterial(Byte b) {
        this.materialb = b
        this.hasAttribute |= 128
    }

    Unit setMedia(Byte b) {
        this.mediab = b
        this.hasAttribute |= 256
    }

    Unit setOffsetU(Float f) {
        this.offsetU = f
        this.hasAttribute |= 16
    }

    Unit setOffsetV(Float f) {
        this.offsetV = f
        this.hasAttribute |= 32
    }

    Unit setRGBA(Int i) {
        this.rgba = i
        this.hasAttribute |= 2
    }

    Unit setRepeatU(Float f) {
        this.repeatU = f
        this.hasAttribute |= 4
    }

    Unit setRepeatV(Float f) {
        this.repeatV = f
        this.hasAttribute |= 8
    }

    Unit setRotation(Float f) {
        this.rotation = f
        this.hasAttribute |= 64
    }

    Unit setTextureID(UUID uuid) {
        this.textureID = uuid
        this.hasAttribute |= 1
    }
}
