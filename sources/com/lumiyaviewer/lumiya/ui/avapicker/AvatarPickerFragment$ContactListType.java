// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.avapicker;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.avapicker:
//            AvatarPickerFragment

private static final class drawableId extends Enum
{

    private static final Nearby $VALUES[];
    public static final Nearby Friends;
    public static final Nearby Nearby;
    public static final Nearby Recent;
    public final int drawableId;

    public static drawableId valueOf(String s)
    {
        return (drawableId)Enum.valueOf(com/lumiyaviewer/lumiya/ui/avapicker/AvatarPickerFragment$ContactListType, s);
    }

    public static drawableId[] values()
    {
        return $VALUES;
    }

    static 
    {
        Recent = new <init>("Recent", 0, 0x7f02007d);
        Friends = new <init>("Friends", 1, 0x7f02007e);
        Nearby = new <init>("Nearby", 2, 0x7f020083);
        $VALUES = (new .VALUES[] {
            Recent, Friends, Nearby
        });
    }

    private (String s, int i, int j)
    {
        super(s, i);
        drawableId = j;
    }
}
