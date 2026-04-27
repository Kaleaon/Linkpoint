// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.avatar;


public class SLAvatarParamAlpha
{

    public final float domain;
    public final boolean multiplyBlend;
    public final boolean skipIfZero;
    public final String tgaFile;

    SLAvatarParamAlpha(float f, String s, boolean flag, boolean flag1)
    {
        domain = f;
        tgaFile = s;
        skipIfZero = flag;
        multiplyBlend = flag1;
    }

    public boolean equals(Object obj)
    {
        boolean flag = true;
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        obj = (SLAvatarParamAlpha)obj;
        if (Float.compare(((SLAvatarParamAlpha) (obj)).domain, domain) != 0)
        {
            return false;
        }
        if (skipIfZero != ((SLAvatarParamAlpha) (obj)).skipIfZero)
        {
            return false;
        }
        if (multiplyBlend != ((SLAvatarParamAlpha) (obj)).multiplyBlend)
        {
            return false;
        }
        if (tgaFile != null)
        {
            flag = tgaFile.equals(((SLAvatarParamAlpha) (obj)).tgaFile);
        } else
        if (((SLAvatarParamAlpha) (obj)).tgaFile != null)
        {
            return false;
        }
        return flag;
    }

    public int hashCode()
    {
        int l = 1;
        int i;
        int j;
        int k;
        if (domain != 0.0F)
        {
            i = Float.floatToIntBits(domain);
        } else
        {
            i = 0;
        }
        if (tgaFile != null)
        {
            j = tgaFile.hashCode();
        } else
        {
            j = 0;
        }
        if (skipIfZero)
        {
            k = 1;
        } else
        {
            k = 0;
        }
        if (!multiplyBlend)
        {
            l = 0;
        }
        return (k + (j + i * 31) * 31) * 31 + l;
    }
}
