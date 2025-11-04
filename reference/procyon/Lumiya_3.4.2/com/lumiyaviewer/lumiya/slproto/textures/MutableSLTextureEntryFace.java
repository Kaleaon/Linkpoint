// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.textures;

import java.util.UUID;

public class MutableSLTextureEntryFace
{
    public static final byte BUMP_MASK = 31;
    public static final byte FULLBRIGHT_MASK = 32;
    public static final byte MEDIA_MASK = 1;
    public static final byte SHINY_MASK = -64;
    public static final byte TEX_MAP_MASK = 6;
    float glow;
    int hasAttribute;
    byte materialb;
    byte mediab;
    float offsetU;
    float offsetV;
    float repeatU;
    float repeatV;
    int rgba;
    float rotation;
    UUID textureID;
    
    public MutableSLTextureEntryFace(final int hasAttribute) {
        this.rgba = -1;
        this.repeatU = 1.0f;
        this.repeatV = 1.0f;
        this.offsetU = 1.0f;
        this.offsetV = 1.0f;
        this.rotation = 0.0f;
        this.glow = 0.0f;
        this.materialb = 0;
        this.mediab = 0;
        this.hasAttribute = hasAttribute;
    }
    
    public void setGlow(final float glow) {
        this.glow = glow;
        this.hasAttribute |= 0x200;
    }
    
    public void setMaterial(final byte b) {
        this.materialb = b;
        this.hasAttribute |= 0x80;
    }
    
    public void setMedia(final byte b) {
        this.mediab = b;
        this.hasAttribute |= 0x100;
    }
    
    public void setOffsetU(final float offsetU) {
        this.offsetU = offsetU;
        this.hasAttribute |= 0x10;
    }
    
    public void setOffsetV(final float offsetV) {
        this.offsetV = offsetV;
        this.hasAttribute |= 0x20;
    }
    
    public void setRGBA(final int rgba) {
        this.rgba = rgba;
        this.hasAttribute |= 0x2;
    }
    
    public void setRepeatU(final float repeatU) {
        this.repeatU = repeatU;
        this.hasAttribute |= 0x4;
    }
    
    public void setRepeatV(final float repeatV) {
        this.repeatV = repeatV;
        this.hasAttribute |= 0x8;
    }
    
    public void setRotation(final float rotation) {
        this.rotation = rotation;
        this.hasAttribute |= 0x40;
    }
    
    public void setTextureID(final UUID textureID) {
        this.textureID = textureID;
        this.hasAttribute |= 0x1;
    }
}
