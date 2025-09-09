package com.lumiyaviewer.lumiya.slproto.textures;

import java.util.UUID;

public class MutableSLTextureEntryFace {
    public static final byte BUMP_MASK = 31;
    public static final byte FULLBRIGHT_MASK = 32;
    public static final byte MEDIA_MASK = 1;
    public static final byte SHINY_MASK = -64;
    public static final byte TEX_MAP_MASK = 6;
    float glow = 0.0f;
    int hasAttribute;
    byte materialb = 0;
    byte mediab = 0;
    float offsetU = 1.0f;
    float offsetV = 1.0f;
    float repeatU = 1.0f;
    float repeatV = 1.0f;
    int rgba = -1;
    float rotation = 0.0f;
    UUID textureID;

    public MutableSLTextureEntryFace(int i) {
        this.hasAttribute = i;
    }

    public void setGlow(float f) {
        this.glow = f;
        this.hasAttribute |= 512;
    }

    public void setMaterial(byte b) {
        this.materialb = b;
        this.hasAttribute |= 128;
    }

    public void setMedia(byte b) {
        this.mediab = b;
        this.hasAttribute |= 256;
    }

    public void setOffsetU(float f) {
        this.offsetU = f;
        this.hasAttribute |= 16;
    }

    public void setOffsetV(float f) {
        this.offsetV = f;
        this.hasAttribute |= 32;
    }

    public void setRGBA(int i) {
        this.rgba = i;
        this.hasAttribute |= 2;
    }

    public void setRepeatU(float f) {
        this.repeatU = f;
        this.hasAttribute |= 4;
    }

    public void setRepeatV(float f) {
        this.repeatV = f;
        this.hasAttribute |= 8;
    }

    public void setRotation(float f) {
        this.rotation = f;
        this.hasAttribute |= 64;
    }

    public void setTextureID(UUID uuid) {
        this.textureID = uuid;
        this.hasAttribute |= 1;
    }
}
