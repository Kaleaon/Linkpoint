package com.linkpoint.slproto.textures

import com.linkpoint.utils.InternPool
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLTextureEntryFace {
const val A: IntttributeAll = -1
const val A: IntttributeGlow = 512
const val A: IntttributeMaterial = 128
const val A: IntttributeMedia = 256
const val A: IntttributeOffsetU = 16
const val A: IntttributeOffsetV = 32
const val A: IntttributeRGBA = 2
const val A: IntttributeRepeatU = 4
const val A: IntttributeRepeatV = 8
const val A: IntttributeRotation = 64
const val A: IntttributeTextureID = 1
    private const val InternPool<SLTextureEntryFace> pool = InternPool<>()

    @JvmStatic
     fun create(mutableSLTextureEntryFace: MutableSLTextureEntryFace): SLTextureEntryFace {
        if (mutableSLTextureEntryFace == null) {
            return null
        }
        return pool.intern(AutoValue_SLTextureEntryFace(mutableSLTextureEntryFace.textureID, mutableSLTextureEntryFace.rgba, mutableSLTextureEntryFace.repeatU, mutableSLTextureEntryFace.repeatV, mutableSLTextureEntryFace.offsetU, mutableSLTextureEntryFace.offsetV, mutableSLTextureEntryFace.rotation, mutableSLTextureEntryFace.glow, mutableSLTextureEntryFace.materialb, mutableSLTextureEntryFace.mediab, mutableSLTextureEntryFace.hasAttribute))
    }

    val Float getGlow(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 512) != 0 ? glow() : sLTextureEntryFace.glow()
    }

    val Byte getMaterial(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 128) != 0 ? materialb() : sLTextureEntryFace.materialb()
    }

    val Byte getMedia(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 256) != 0 ? mediab() : sLTextureEntryFace.mediab()
    }

    val Float getOffsetU(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 16) != 0 ? offsetU() : sLTextureEntryFace.offsetU()
    }

    val Float getOffsetV(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 32) != 0 ? offsetV() : sLTextureEntryFace.offsetV()
    }

    val Int getRGBA(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 2) != 0 ? rgba() : sLTextureEntryFace.rgba()
    }

    val Float getRepeatU(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 4) != 0 ? repeatU() : sLTextureEntryFace.repeatU()
    }

    val Float getRepeatV(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 8) != 0 ? repeatV() : sLTextureEntryFace.repeatV()
    }

    val Float getRotation(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 64) != 0 ? rotation() : sLTextureEntryFace.rotation()
    }

    val UUID getTextureID(SLTextureEntryFace sLTextureEntryFace) {
        return (hasAttribute() & 1) != 0 ? textureID() : sLTextureEntryFace.textureID()
    }

    public abstract Float glow()

    public abstract Int hasAttribute()

    public abstract Byte materialb()

    public abstract Byte mediab()

    public abstract Float offsetU()

    public abstract Float offsetV()

    public abstract Float repeatU()

    public abstract Float repeatV()

    public abstract Int rgba()

    public abstract Float rotation()

    public abstract UUID textureID()
}
