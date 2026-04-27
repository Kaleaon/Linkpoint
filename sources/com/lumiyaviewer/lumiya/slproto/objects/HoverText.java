// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.objects;

import com.google.common.base.Objects;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.objects:
//            AutoValue_HoverText

public abstract class HoverText
{

    public HoverText()
    {
    }

    public static HoverText create(String s, int i)
    {
        return new AutoValue_HoverText(s, i);
    }

    public abstract int color();

    public final boolean sameText(HoverText hovertext)
    {
        String s = null;
        String s1 = text();
        if (hovertext != null)
        {
            s = hovertext.text();
        }
        return Objects.equal(s1, s);
    }

    public abstract String text();
}
