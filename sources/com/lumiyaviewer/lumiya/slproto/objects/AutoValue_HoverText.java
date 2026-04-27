// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            HoverText

final class AutoValue_HoverText extends HoverText
{

    private final int color;
    private final String text;

    AutoValue_HoverText(String s, int i)
    {
        if (s == null)
        {
            throw new NullPointerException("Null text");
        } else
        {
            text = s;
            color = i;
            return;
        }
    }

    public int color()
    {
        return color;
    }

    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj instanceof HoverText)
        {
            obj = (HoverText)obj;
            if (text.equals(((HoverText) (obj)).text()))
            {
                return color == ((HoverText) (obj)).color();
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
        return (text.hashCode() ^ 0xf4243) * 0xf4243 ^ color;
    }

    public String text()
    {
        return text;
    }

    public String toString()
    {
        return (new StringBuilder()).append("HoverText{text=").append(text).append(", ").append("color=").append(color).append("}").toString();
    }
}
