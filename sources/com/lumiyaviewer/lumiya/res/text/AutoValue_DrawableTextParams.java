// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.text;


// Referenced classes of package com.lumiyaviewer.lumiya.res.text:
//            DrawableTextParams

final class AutoValue_DrawableTextParams extends DrawableTextParams
{

    private final int backgroundColor;
    private final String text;

    AutoValue_DrawableTextParams(String s, int i)
    {
        if (s == null)
        {
            throw new NullPointerException("Null text");
        } else
        {
            text = s;
            backgroundColor = i;
            return;
        }
    }

    public int backgroundColor()
    {
        return backgroundColor;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof DrawableTextParams)
        {
            obj = (DrawableTextParams)obj;
            if (text.equals(((DrawableTextParams) (obj)).text()))
            {
                return backgroundColor == ((DrawableTextParams) (obj)).backgroundColor();
            } else
            {
                return false;
            }
        } else
        {
            return false;
        }
    }

    public int hashCode()
    {
        return (text.hashCode() ^ 0xf4243) * 0xf4243 ^ backgroundColor;
    }

    public String text()
    {
        return text;
    }

    public String toString()
    {
        return (new StringBuilder()).append("DrawableTextParams{text=").append(text).append(", ").append("backgroundColor=").append(backgroundColor).append("}").toString();
    }
}
