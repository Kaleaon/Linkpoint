// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;


// Referenced classes of package com.lumiyaviewer.lumiya:
//            GlobalOptions

public static final class lodName extends Enum
{

    private static final disabled $VALUES[];
    public static final disabled disabled;
    public static final disabled high;
    public static final disabled low;
    public static final disabled lowest;
    public static final disabled medium;
    private String lodName;

    public static lodName valueOf(String s)
    {
        return (lodName)Enum.valueOf(com/lumiyaviewer/lumiya/GlobalOptions$MeshRendering, s);
    }

    public static lodName[] values()
    {
        return $VALUES;
    }

    public String getLODName()
    {
        return lodName;
    }

    static 
    {
        high = new <init>("high", 0, "high_lod");
        medium = new <init>("medium", 1, "medium_lod");
        low = new <init>("low", 2, "low_lod");
        lowest = new <init>("lowest", 3, "lowest_lod");
        disabled = new <init>("disabled", 4, null);
        $VALUES = (new .VALUES[] {
            high, medium, low, lowest, disabled
        });
    }

    private _cls9(String s, int i, String s1)
    {
        super(s, i);
        lodName = s1;
    }
}
