// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.textures;

import com.lumiyaviewer.lumiya.utils.InternPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.textures:
//            AutoValue_SLTextureEntryFace, MutableSLTextureEntryFace

public abstract class SLTextureEntryFace
{

    public static final int AttributeAll = -1;
    static final int AttributeGlow = 512;
    static final int AttributeMaterial = 128;
    static final int AttributeMedia = 256;
    static final int AttributeOffsetU = 16;
    static final int AttributeOffsetV = 32;
    static final int AttributeRGBA = 2;
    static final int AttributeRepeatU = 4;
    static final int AttributeRepeatV = 8;
    static final int AttributeRotation = 64;
    static final int AttributeTextureID = 1;
    private static final InternPool pool = new InternPool();

    public SLTextureEntryFace()
    {
    }

    public static SLTextureEntryFace create(MutableSLTextureEntryFace mutablesltextureentryface)
    {
        if (mutablesltextureentryface == null)
        {
            return null;
        } else
        {
            return (SLTextureEntryFace)pool.intern(new AutoValue_SLTextureEntryFace(mutablesltextureentryface.textureID, mutablesltextureentryface.rgba, mutablesltextureentryface.repeatU, mutablesltextureentryface.repeatV, mutablesltextureentryface.offsetU, mutablesltextureentryface.offsetV, mutablesltextureentryface.rotation, mutablesltextureentryface.glow, mutablesltextureentryface.materialb, mutablesltextureentryface.mediab, mutablesltextureentryface.hasAttribute));
        }
    }

    public final float getGlow(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x200) != 0)
        {
            return glow();
        } else
        {
            return sltextureentryface.glow();
        }
    }

    public final byte getMaterial(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x80) != 0)
        {
            return materialb();
        } else
        {
            return sltextureentryface.materialb();
        }
    }

    public final byte getMedia(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x100) != 0)
        {
            return mediab();
        } else
        {
            return sltextureentryface.mediab();
        }
    }

    public final float getOffsetU(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x10) != 0)
        {
            return offsetU();
        } else
        {
            return sltextureentryface.offsetU();
        }
    }

    public final float getOffsetV(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x20) != 0)
        {
            return offsetV();
        } else
        {
            return sltextureentryface.offsetV();
        }
    }

    public final int getRGBA(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 2) != 0)
        {
            return rgba();
        } else
        {
            return sltextureentryface.rgba();
        }
    }

    public final float getRepeatU(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 4) != 0)
        {
            return repeatU();
        } else
        {
            return sltextureentryface.repeatU();
        }
    }

    public final float getRepeatV(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 8) != 0)
        {
            return repeatV();
        } else
        {
            return sltextureentryface.repeatV();
        }
    }

    public final float getRotation(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 0x40) != 0)
        {
            return rotation();
        } else
        {
            return sltextureentryface.rotation();
        }
    }

    public final UUID getTextureID(SLTextureEntryFace sltextureentryface)
    {
        if ((hasAttribute() & 1) != 0)
        {
            return textureID();
        } else
        {
            return sltextureentryface.textureID();
        }
    }

    public abstract float glow();

    public abstract int hasAttribute();

    public abstract byte materialb();

    public abstract byte mediab();

    public abstract float offsetU();

    public abstract float offsetV();

    public abstract float repeatU();

    public abstract float repeatV();

    public abstract int rgba();

    public abstract float rotation();

    public abstract UUID textureID();

}
