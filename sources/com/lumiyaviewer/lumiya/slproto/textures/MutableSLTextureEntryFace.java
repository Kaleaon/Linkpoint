// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

    public MutableSLTextureEntryFace(int i)
    {
        rgba = -1;
        repeatU = 1.0F;
        repeatV = 1.0F;
        offsetU = 1.0F;
        offsetV = 1.0F;
        rotation = 0.0F;
        glow = 0.0F;
        materialb = 0;
        mediab = 0;
        hasAttribute = i;
    }

    public void setGlow(float f)
    {
        glow = f;
        hasAttribute = hasAttribute | 0x200;
    }

    public void setMaterial(byte byte0)
    {
        materialb = byte0;
        hasAttribute = hasAttribute | 0x80;
    }

    public void setMedia(byte byte0)
    {
        mediab = byte0;
        hasAttribute = hasAttribute | 0x100;
    }

    public void setOffsetU(float f)
    {
        offsetU = f;
        hasAttribute = hasAttribute | 0x10;
    }

    public void setOffsetV(float f)
    {
        offsetV = f;
        hasAttribute = hasAttribute | 0x20;
    }

    public void setRGBA(int i)
    {
        rgba = i;
        hasAttribute = hasAttribute | 2;
    }

    public void setRepeatU(float f)
    {
        repeatU = f;
        hasAttribute = hasAttribute | 4;
    }

    public void setRepeatV(float f)
    {
        repeatV = f;
        hasAttribute = hasAttribute | 8;
    }

    public void setRotation(float f)
    {
        rotation = f;
        hasAttribute = hasAttribute | 0x40;
    }

    public void setTextureID(UUID uuid)
    {
        textureID = uuid;
        hasAttribute = hasAttribute | 1;
    }
}
