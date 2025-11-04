// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.textures;

import javax.annotation.Nullable;
import java.util.UUID;

final class AutoValue_SLTextureEntryFace extends SLTextureEntryFace
{
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
    
    AutoValue_SLTextureEntryFace(@Nullable final UUID textureID, final int rgba, final float repeatU, final float repeatV, final float offsetU, final float offsetV, final float rotation, final float glow, final byte b, final byte b2, final int hasAttribute) {
        this.textureID = textureID;
        this.rgba = rgba;
        this.repeatU = repeatU;
        this.repeatV = repeatV;
        this.offsetU = offsetU;
        this.offsetV = offsetV;
        this.rotation = rotation;
        this.glow = glow;
        this.materialb = b;
        this.mediab = b2;
        this.hasAttribute = hasAttribute;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean b = true;
        if (o == this) {
            return true;
        }
        if (o instanceof SLTextureEntryFace) {
            final SLTextureEntryFace slTextureEntryFace = (SLTextureEntryFace)o;
            if (this.textureID == null) {
                if (slTextureEntryFace.textureID() != null) {
                    return false;
                }
            }
            else if (!this.textureID.equals(slTextureEntryFace.textureID())) {
                return false;
            }
            if (this.rgba != slTextureEntryFace.rgba() || Float.floatToIntBits(this.repeatU) != Float.floatToIntBits(slTextureEntryFace.repeatU()) || Float.floatToIntBits(this.repeatV) != Float.floatToIntBits(slTextureEntryFace.repeatV()) || Float.floatToIntBits(this.offsetU) != Float.floatToIntBits(slTextureEntryFace.offsetU()) || Float.floatToIntBits(this.offsetV) != Float.floatToIntBits(slTextureEntryFace.offsetV()) || Float.floatToIntBits(this.rotation) != Float.floatToIntBits(slTextureEntryFace.rotation()) || Float.floatToIntBits(this.glow) != Float.floatToIntBits(slTextureEntryFace.glow()) || this.materialb != slTextureEntryFace.materialb() || this.mediab != slTextureEntryFace.mediab()) {
                return false;
            }
            if (this.hasAttribute != slTextureEntryFace.hasAttribute()) {
                b = false;
            }
            return b;
            b = false;
            return b;
        }
        return false;
    }
    
    @Override
    public float glow() {
        return this.glow;
    }
    
    @Override
    public int hasAttribute() {
        return this.hasAttribute;
    }
    
    @Override
    public int hashCode() {
        int hashCode;
        if (this.textureID == null) {
            hashCode = 0;
        }
        else {
            hashCode = this.textureID.hashCode();
        }
        return ((((((((((hashCode ^ 0xF4243) * 1000003 ^ this.rgba) * 1000003 ^ Float.floatToIntBits(this.repeatU)) * 1000003 ^ Float.floatToIntBits(this.repeatV)) * 1000003 ^ Float.floatToIntBits(this.offsetU)) * 1000003 ^ Float.floatToIntBits(this.offsetV)) * 1000003 ^ Float.floatToIntBits(this.rotation)) * 1000003 ^ Float.floatToIntBits(this.glow)) * 1000003 ^ this.materialb) * 1000003 ^ this.mediab) * 1000003 ^ this.hasAttribute;
    }
    
    @Override
    public byte materialb() {
        return this.materialb;
    }
    
    @Override
    public byte mediab() {
        return this.mediab;
    }
    
    @Override
    public float offsetU() {
        return this.offsetU;
    }
    
    @Override
    public float offsetV() {
        return this.offsetV;
    }
    
    @Override
    public float repeatU() {
        return this.repeatU;
    }
    
    @Override
    public float repeatV() {
        return this.repeatV;
    }
    
    @Override
    public int rgba() {
        return this.rgba;
    }
    
    @Override
    public float rotation() {
        return this.rotation;
    }
    
    @Nullable
    @Override
    public UUID textureID() {
        return this.textureID;
    }
    
    @Override
    public String toString() {
        return "SLTextureEntryFace{textureID=" + this.textureID + ", " + "rgba=" + this.rgba + ", " + "repeatU=" + this.repeatU + ", " + "repeatV=" + this.repeatV + ", " + "offsetU=" + this.offsetU + ", " + "offsetV=" + this.offsetV + ", " + "rotation=" + this.rotation + ", " + "glow=" + this.glow + ", " + "materialb=" + this.materialb + ", " + "mediab=" + this.mediab + ", " + "hasAttribute=" + this.hasAttribute + "}";
    }
}
