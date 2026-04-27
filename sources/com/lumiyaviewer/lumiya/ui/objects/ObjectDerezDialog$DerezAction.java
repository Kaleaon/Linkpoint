// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import com.lumiyaviewer.lumiya.slproto.types.EDeRezDestination;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectDerezDialog

public static final class deRezDestination extends Enum
{

    private static final Delete $VALUES[];
    public static final Delete Delete;
    public static final Delete Take;
    public static final Delete TakeCopy;
    public final EDeRezDestination deRezDestination;
    public final int derezQuestionId;

    public static deRezDestination valueOf(String s)
    {
        return (deRezDestination)Enum.valueOf(com/lumiyaviewer/lumiya/ui/objects/ObjectDerezDialog$DerezAction, s);
    }

    public static deRezDestination[] values()
    {
        return $VALUES;
    }

    static 
    {
        Take = new <init>("Take", 0, 0x7f0900e3, EDeRezDestination.DRD_TAKE_INTO_AGENT_INVENTORY);
        TakeCopy = new <init>("TakeCopy", 1, 0x7f0900e4, EDeRezDestination.DRD_ACQUIRE_TO_AGENT_INVENTORY);
        Delete = new <init>("Delete", 2, 0x7f0900e2, EDeRezDestination.DRD_TRASH);
        $VALUES = (new .VALUES[] {
            Take, TakeCopy, Delete
        });
    }

    private (String s, int i, int j, EDeRezDestination ederezdestination)
    {
        super(s, i);
        derezQuestionId = j;
        deRezDestination = ederezdestination;
    }
}
