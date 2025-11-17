package com.linkpoint.slproto.textures

import java.util.UUID
import androidx.annotation.Nullable

class AutoValue_SLTextureEntryFace : SLTextureEntryFace {
    private Float glow
    private Int hasAttribute
    private Byte materialb
    private Byte mediab
    private Float offsetU
    private Float offsetV
    private Float repeatU
    private Float repeatV
    private Int rgba
    private Float rotation
    private UUID textureID

    AutoValue_SLTextureEntryFace(@Nullable UUID uuid, Int i, Float f, Float f2, Float f3, Float f4, Float f5, Float f6, Byte b, Byte b2, Int i2) {
        this.textureID = uuid
        this.rgba = i
        this.repeatU = f
        this.repeatV = f2
        this.offsetU = f3
        this.offsetV = f4
        this.rotation = f5
        this.glow = f6
        this.materialb = b
        this.mediab = b2
        this.hasAttribute = i2
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLTextureEntryFace)) {
            return false
        }
        SLTextureEntryFace sLTextureEntryFace = (SLTextureEntryFace) obj
        if (this.textureID != null ? this.textureID.equals(sLTextureEntryFace.textureID()) : sLTextureEntryFace.textureID() == null) {
            if (this.rgba == sLTextureEntryFace.rgba() && Float.floatToIntBits(this.repeatU) == Float.floatToIntBits(sLTextureEntryFace.repeatU()) && Float.floatToIntBits(this.repeatV) == Float.floatToIntBits(sLTextureEntryFace.repeatV()) && Float.floatToIntBits(this.offsetU) == Float.floatToIntBits(sLTextureEntryFace.offsetU()) && Float.floatToIntBits(this.offsetV) == Float.floatToIntBits(sLTextureEntryFace.offsetV()) && Float.floatToIntBits(this.rotation) == Float.floatToIntBits(sLTextureEntryFace.rotation()) && Float.floatToIntBits(this.glow) == Float.floatToIntBits(sLTextureEntryFace.glow()) && this.materialb == sLTextureEntryFace.materialb() && this.mediab == sLTextureEntryFace.mediab()) {
                return this.hasAttribute == sLTextureEntryFace.hasAttribute()
            }
        }
        return false
    }

    Float glow() {
        return this.glow
    }

    Int hasAttribute() {
        return this.hasAttribute
    }

    Int hashCode() {
        return (((((((((((((((((((((this.textureID == null ? 0 : this.textureID.hashCode()) ^ 1000003) * 1000003) ^ this.rgba) * 1000003) ^ Float.floatToIntBits(this.repeatU)) * 1000003) ^ Float.floatToIntBits(this.repeatV)) * 1000003) ^ Float.floatToIntBits(this.offsetU)) * 1000003) ^ Float.floatToIntBits(this.offsetV)) * 1000003) ^ Float.floatToIntBits(this.rotation)) * 1000003) ^ Float.floatToIntBits(this.glow)) * 1000003) ^ this.materialb) * 1000003) ^ this.mediab) * 1000003) ^ this.hasAttribute
    }

    Byte materialb() {
        return this.materialb
    }

    Byte mediab() {
        return this.mediab
    }

    Float offsetU() {
        return this.offsetU
    }

    Float offsetV() {
        return this.offsetV
    }

    Float repeatU() {
        return this.repeatU
    }

    Float repeatV() {
        return this.repeatV
    }

    Int rgba() {
        return this.rgba
    }

    Float rotation() {
        return this.rotation
    }

    @Nullable
    UUID textureID() {
        return this.textureID
    }

    String toString() {
        return "SLTextureEntryFace{textureID=" + this.textureID + ", " + "rgba=" + this.rgba + ", " + "repeatU=" + this.repeatU + ", " + "repeatV=" + this.repeatV + ", " + "offsetU=" + this.offsetU + ", " + "offsetV=" + this.offsetV + ", " + "rotation=" + this.rotation + ", " + "glow=" + this.glow + ", " + "materialb=" + this.materialb + ", " + "mediab=" + this.mediab + ", " + "hasAttribute=" + this.hasAttribute + "}"
    }
}
