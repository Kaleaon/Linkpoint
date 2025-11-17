package com.linkpoint.slproto.textures

import com.linkpoint.utils.InternPool
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

abstract class SLTextureEntryFace {
    Int AttributeAll = -1
    Int AttributeGlow = 512
    Int AttributeMaterial = 128
    Int AttributeMedia = 256
    Int AttributeOffsetU = 16
    Int AttributeOffsetV = 32
    Int AttributeRGBA = 2
    Int AttributeRepeatU = 4
    Int AttributeRepeatV = 8
    Int AttributeRotation = 64
    Int AttributeTextureID = 1
    private InternPool<SLTextureEntryFace> pool = InternPool<>()

    SLTextureEntryFace create(MutableSLTextureEntryFace mutableSLTextureEntryFace) {
        if (mutableSLTextureEntryFace == null) {
            return null
        }
        return pool.intern(AutoValue_SLTextureEntryFace(mutableSLTextureEntryFace.textureID, mutableSLTextureEntryFace.rgba, mutableSLTextureEntryFace.repeatU, mutableSLTextureEntryFace.repeatV, mutableSLTextureEntryFace.offsetU, mutableSLTextureEntryFace.offsetV, mutableSLTextureEntryFace.rotation, mutableSLTextureEntryFace.glow, mutableSLTextureEntryFace.materialb, mutableSLTextureEntryFace.mediab, mutableSLTextureEntryFace.hasAttribute))
    }

    Float getGlow(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 512) != 0 ? glow() : sLTextureEntryFace.glow()
    }

    Byte getMaterial(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 128) != 0 ? materialb() : sLTextureEntryFace.materialb()
    }

    Byte getMedia(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 256) != 0 ? mediab() : sLTextureEntryFace.mediab()
    }

    Float getOffsetU(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 16) != 0 ? offsetU() : sLTextureEntryFace.offsetU()
    }

    Float getOffsetV(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 32) != 0 ? offsetV() : sLTextureEntryFace.offsetV()
    }

    Int getRGBA(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 2) != 0 ? rgba() : sLTextureEntryFace.rgba()
    }

    Float getRepeatU(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 4) != 0 ? repeatU() : sLTextureEntryFace.repeatU()
    }

    Float getRepeatV(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 8) != 0 ? repeatV() : sLTextureEntryFace.repeatV()
    }

    Float getRotation(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 64) != 0 ? rotation() : sLTextureEntryFace.rotation()
    }

    @Nullable
    UUID getTextureID(@NonNull SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 1) != 0 ? textureID() : sLTextureEntryFace.textureID()
    }

    abstract Float glow()

    abstract Int hasAttribute()

    abstract Byte materialb()

    abstract Byte mediab()

    abstract Float offsetU()

    abstract Float offsetV()

    abstract Float repeatU()

    abstract Float repeatV()

    abstract Int rgba()

    abstract Float rotation()

    @Nullable
    abstract UUID textureID()
}
