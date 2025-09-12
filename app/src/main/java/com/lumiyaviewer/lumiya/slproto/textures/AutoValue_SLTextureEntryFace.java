package com.lumiyaviewer.lumiya.slproto.textures;

import java.util.UUID;
import javax.annotation.Nullable;

final class AutoValue_SLTextureEntryFace extends SLTextureEntryFace {
    private final float glow;
    private final int hasAttribute;
    private final byte materialb;
    private final byte mediab;
    private final float offsetU;
    private final float offsetV;
    private final float repeatU;
    private final float repeatV;
    private final int rgba;
    private final float rotation;
    private final UUID textureID;

    AutoValue_SLTextureEntryFace(@Nullable UUID uuid, int i, float f, float f2, float f3, float f4, float f5, float f6, byte b, byte b2, int i2) {
        this.textureID = uuid;
        this.rgba = i;
        this.repeatU = f;
        this.repeatV = f2;
        this.offsetU = f3;
        this.offsetV = f4;
        this.rotation = f5;
        this.glow = f6;
        this.materialb = b;
        this.mediab = b2;
        this.hasAttribute = i2;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SLTextureEntryFace)) {
            return false;
        }
        SLTextureEntryFace sLTextureEntryFace = (SLTextureEntryFace) obj;
        if (this.textureID != null ? this.textureID.equals(sLTextureEntryFace.textureID()) : sLTextureEntryFace.textureID() == null) {
            if (this.rgba == sLTextureEntryFace.rgba() && Float.floatToIntBits(this.repeatU) == Float.floatToIntBits(sLTextureEntryFace.repeatU()) && Float.floatToIntBits(this.repeatV) == Float.floatToIntBits(sLTextureEntryFace.repeatV()) && Float.floatToIntBits(this.offsetU) == Float.floatToIntBits(sLTextureEntryFace.offsetU()) && Float.floatToIntBits(this.offsetV) == Float.floatToIntBits(sLTextureEntryFace.offsetV()) && Float.floatToIntBits(this.rotation) == Float.floatToIntBits(sLTextureEntryFace.rotation()) && Float.floatToIntBits(this.glow) == Float.floatToIntBits(sLTextureEntryFace.glow()) && this.materialb == sLTextureEntryFace.materialb() && this.mediab == sLTextureEntryFace.mediab()) {
                return this.hasAttribute == sLTextureEntryFace.hasAttribute();
            }
        }
        return false;
    }

    public float glow() {
        return this.glow;
    }

    public int hasAttribute() {
        return this.hasAttribute;
    }

    public int hashCode() {
        return (((((((((((((((((((((this.textureID == null ? 0 : this.textureID.hashCode()) ^ 1000003) * 1000003) ^ this.rgba) * 1000003) ^ Float.floatToIntBits(this.repeatU)) * 1000003) ^ Float.floatToIntBits(this.repeatV)) * 1000003) ^ Float.floatToIntBits(this.offsetU)) * 1000003) ^ Float.floatToIntBits(this.offsetV)) * 1000003) ^ Float.floatToIntBits(this.rotation)) * 1000003) ^ Float.floatToIntBits(this.glow)) * 1000003) ^ this.materialb) * 1000003) ^ this.mediab) * 1000003) ^ this.hasAttribute;
    }

    public byte materialb() {
        return this.materialb;
    }

    public byte mediab() {
        return this.mediab;
    }

    public float offsetU() {
        return this.offsetU;
    }

    public float offsetV() {
        return this.offsetV;
    }

    public float repeatU() {
        return this.repeatU;
    }

    public float repeatV() {
        return this.repeatV;
    }

    public int rgba() {
        return this.rgba;
    }

    public float rotation() {
        return this.rotation;
    }

    @Nullable
    public UUID textureID() {
        return this.textureID;
    }

    public String toString() {
        return "SLTextureEntryFace{textureID=" + this.textureID + ", " + "rgba=" + this.rgba + ", " + "repeatU=" + this.repeatU + ", " + "repeatV=" + this.repeatV + ", " + "offsetU=" + this.offsetU + ", " + "offsetV=" + this.offsetV + ", " + "rotation=" + this.rotation + ", " + "glow=" + this.glow + ", " + "materialb=" + this.materialb + ", " + "mediab=" + this.mediab + ", " + "hasAttribute=" + this.hasAttribute + "}";
    }
}
