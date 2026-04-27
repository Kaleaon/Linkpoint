// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.text;


// Referenced classes of package com.lumiyaviewer.lumiya.res.text:
//            AutoValue_DrawableTextParams

public abstract class DrawableTextParams
{

    public DrawableTextParams()
    {
    }

    public static DrawableTextParams create(String s, int i)
    {
        return new AutoValue_DrawableTextParams(s, i);
    }

    public abstract int backgroundColor();

    public abstract String text();
}
