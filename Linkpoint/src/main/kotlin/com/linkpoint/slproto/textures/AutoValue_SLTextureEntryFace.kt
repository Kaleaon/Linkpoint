package com.linkpoint.slproto.textures

import java.util.UUID
import javax.annotation.Nullable

final class AutoValue_SLTextureEntryFace : SLTextureEntryFace() {
    private val Float glow
    private val Int hasAttribute
    private val Byte materialb
    private val Byte mediab
    private val Float offsetU
    private val Float offsetV
    private val Float repeatU
    private val Float repeatV
    private val Int rgba
    private val Float rotation
    private val UUID textureID

    AutoValue_SLTextureEntryFace(UUID uuid, Int i, Float f, Float f2, Float f3, Float f4, Float f5, Float f6, Byte b, Byte b2, Int i2) {
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

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof SLTextureEntryFace)) {
            return false
        }
        val sLTextureEntryFace: SLTextureEntryFace = (SLTextureEntryFace) obj
        if (this.textureID != null ? this.textureID.equals(sLTextureEntryFace.textureID()) : sLTextureEntryFace.textureID() == null) {
            if (this.rgba == sLTextureEntryFace.rgba() && Float.floatToIntBits(this.repeatU) == Float.floatToIntBits(sLTextureEntryFace.repeatU()) && Float.floatToIntBits(this.repeatV) == Float.floatToIntBits(sLTextureEntryFace.repeatV()) && Float.floatToIntBits(this.offsetU) == Float.floatToIntBits(sLTextureEntryFace.offsetU()) && Float.floatToIntBits(this.offsetV) == Float.floatToIntBits(sLTextureEntryFace.offsetV()) && Float.floatToIntBits(this.rotation) == Float.floatToIntBits(sLTextureEntryFace.rotation()) && Float.floatToIntBits(this.glow) == Float.floatToIntBits(sLTextureEntryFace.glow()) && this.materialb == sLTextureEntryFace.materialb() && this.mediab == sLTextureEntryFace.mediab()) {
                return this.hasAttribute == sLTextureEntryFace.hasAttribute()
            }
        }
        return false
    }

     public fun glow(): Float {
        return this.glow
    }

     public fun hasAttribute(): Int {
        return this.hasAttribute
    }

     public fun hashCode(): Int {
        return (((((((((((((((((((((this.textureID == null ? 0 : this.textureID.hashCode()) ^ 1000003) * 1000003) ^ this.rgba) * 1000003) ^ Float.floatToIntBits(this.repeatU)) * 1000003) ^ Float.floatToIntBits(this.repeatV)) * 1000003) ^ Float.floatToIntBits(this.offsetU)) * 1000003) ^ Float.floatToIntBits(this.offsetV)) * 1000003) ^ Float.floatToIntBits(this.rotation)) * 1000003) ^ Float.floatToIntBits(this.glow)) * 1000003) ^ this.materialb) * 1000003) ^ this.mediab) * 1000003) ^ this.hasAttribute
    }

     public fun materialb(): Byte {
        return this.materialb
    }

     public fun mediab(): Byte {
        return this.mediab
    }

     public fun offsetU(): Float {
        return this.offsetU
    }

     public fun offsetV(): Float {
        return this.offsetV
    }

     public fun repeatU(): Float {
        return this.repeatU
    }

     public fun repeatV(): Float {
        return this.repeatV
    }

     public fun rgba(): Int {
        return this.rgba
    }

     public fun rotation(): Float {
        return this.rotation
    }

     public fun textureID(): UUID {
        return this.textureID
    }

     public fun toString(): String {
        return "SLTextureEntryFace{textureID=" + this.textureID + ", " + "rgba=" + this.rgba + ", " + "repeatU=" + this.repeatU + ", " + "repeatV=" + this.repeatV + ", " + "offsetU=" + this.offsetU + ", " + "offsetV=" + this.offsetV + ", " + "rotation=" + this.rotation + ", " + "glow=" + this.glow + ", " + "materialb=" + this.materialb + ", " + "mediab=" + this.mediab + ", " + "hasAttribute=" + this.hasAttribute + "}"
    }
}
