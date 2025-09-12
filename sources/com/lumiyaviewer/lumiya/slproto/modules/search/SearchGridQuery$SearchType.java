// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.search;


// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.search:
//            SearchGridQuery

public static final class  extends Enum
{

    private static final Places $VALUES[];
    public static final Places Groups;
    public static final Places People;
    public static final Places Places;

    public static  valueOf(String s)
    {
        return ()Enum.valueOf(com/lumiyaviewer/lumiya/slproto/modules/search/SearchGridQuery$SearchType, s);
    }

    public static [] values()
    {
        return $VALUES;
    }

    static 
    {
        People = new <init>("People", 0);
        Groups = new <init>("Groups", 1);
        Places = new <init>("Places", 2);
        $VALUES = (new .VALUES[] {
            People, Groups, Places
        });
    }

    private (String s, int i)
    {
        super(s, i);
    }
}
