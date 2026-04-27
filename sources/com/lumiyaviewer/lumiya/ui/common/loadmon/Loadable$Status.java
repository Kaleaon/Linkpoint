// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common.loadmon;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.common.loadmon:
//            Loadable

public static final class  extends Enum
{

    private static final Error $VALUES[];
    public static final Error Error;
    public static final Error Idle;
    public static final Error Loaded;
    public static final Error Loading;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/ui/common/loadmon/Loadable$Status, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        Idle = new <init>("Idle", 0);
        Loading = new <init>("Loading", 1);
        Loaded = new <init>("Loaded", 2);
        Error = new <init>("Error", 3);
        $VALUES = (new .VALUES[] {
            Idle, Loading, Loaded, Error
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
