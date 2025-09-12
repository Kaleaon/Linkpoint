// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.textures;

import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.textures:
//            SLTextureEntryFace

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

    AutoValue_SLTextureEntryFace(UUID uuid, int i, float f, float f1, float f2, float f3, float f4, 
            float f5, byte byte0, byte byte1, int j)
    {
        textureID = uuid;
        rgba = i;
        repeatU = f;
        repeatV = f1;
        offsetU = f2;
        offsetV = f3;
        rotation = f4;
        glow = f5;
        materialb = byte0;
        mediab = byte1;
        hasAttribute = j;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof SLTextureEntryFace)
        {
            obj = (SLTextureEntryFace)obj;
            if ((textureID != null ? textureID.equals(((SLTextureEntryFace) (obj)).textureID()) : ((SLTextureEntryFace) (obj)).textureID() == null) && (rgba == ((SLTextureEntryFace) (obj)).rgba() && Float.floatToIntBits(repeatU) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).repeatU()) && Float.floatToIntBits(repeatV) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).repeatV()) && Float.floatToIntBits(offsetU) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).offsetU()) && Float.floatToIntBits(offsetV) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).offsetV()) && Float.floatToIntBits(rotation) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).rotation()) && Float.floatToIntBits(glow) == Float.floatToIntBits(((SLTextureEntryFace) (obj)).glow()) && materialb == ((SLTextureEntryFace) (obj)).materialb() && mediab == ((SLTextureEntryFace) (obj)).mediab()))
            {
                return hasAttribute == ((SLTextureEntryFace) (obj)).hasAttribute();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public float glow()
    {
        return glow;
    }

    public int hasAttribute()
    {
        return hasAttribute;
    }

    public int hashCode()
    {
        int i;
        if (textureID == null)
        {
            i = 0;
        } else
        {
            i = textureID.hashCode();
        }
        return ((((((((((i ^ 0xf4243) * 0xf4243 ^ rgba) * 0xf4243 ^ Float.floatToIntBits(repeatU)) * 0xf4243 ^ Float.floatToIntBits(repeatV)) * 0xf4243 ^ Float.floatToIntBits(offsetU)) * 0xf4243 ^ Float.floatToIntBits(offsetV)) * 0xf4243 ^ Float.floatToIntBits(rotation)) * 0xf4243 ^ Float.floatToIntBits(glow)) * 0xf4243 ^ materialb) * 0xf4243 ^ mediab) * 0xf4243 ^ hasAttribute;
    }

    public byte materialb()
    {
        return materialb;
    }

    public byte mediab()
    {
        return mediab;
    }

    public float offsetU()
    {
        return offsetU;
    }

    public float offsetV()
    {
        return offsetV;
    }

    public float repeatU()
    {
        return repeatU;
    }

    public float repeatV()
    {
        return repeatV;
    }

    public int rgba()
    {
        return rgba;
    }

    public float rotation()
    {
        return rotation;
    }

    public UUID textureID()
    {
        return textureID;
    }

    public String toString()
    {
        return (new StringBuilder()).append("SLTextureEntryFace{textureID=").append(textureID).append(", ").append("rgba=").append(rgba).append(", ").append("repeatU=").append(repeatU).append(", ").append("repeatV=").append(repeatV).append(", ").append("offsetU=").append(offsetU).append(", ").append("offsetV=").append(offsetV).append(", ").append("rotation=").append(rotation).append(", ").append("glow=").append(glow).append(", ").append("materialb=").append(materialb).append(", ").append("mediab=").append(mediab).append(", ").append("hasAttribute=").append(hasAttribute).append("}").toString();
    }
}
