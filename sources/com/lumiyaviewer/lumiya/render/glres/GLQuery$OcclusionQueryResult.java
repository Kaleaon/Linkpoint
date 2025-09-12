// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres;


// Referenced classes of package com.lumiyaviewer.lumiya.render.glres:
//            GLQuery

public static final class  extends Enum
{

    private static final Invisible $VALUES[];
    public static final Invisible Invisible;
    public static final Invisible NotReady;
    public static final Invisible Visible;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/render/glres/GLQuery$OcclusionQueryResult, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        NotReady = new <init>("NotReady", 0);
        Visible = new <init>("Visible", 1);
        Invisible = new <init>("Invisible", 2);
        $VALUES = (new .VALUES[] {
            NotReady, Visible, Invisible
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
