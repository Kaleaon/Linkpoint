// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            CardboardActivity

private static final class pageViewId extends Enum
{

    private static final pageDetails $VALUES[];
    public static final pageDetails pageDefault;
    public static final pageDetails pageDetails;
    public static final pageDetails pageObject;
    public static final pageDetails pageScriptDialog;
    public static final pageDetails pageSpeech;
    public static final pageDetails pageTouchAim;
    public static final pageDetails pageYesNo;
    final int pageViewId;

    public static pageViewId valueOf(String s)
    {
        return (pageViewId)Enum.valueOf(com/lumiyaviewer/lumiya/ui/render/CardboardActivity$ControlsPage, s);
    }

    public static pageViewId[] values()
    {
        return $VALUES;
    }

    static 
    {
        pageDefault = new <init>("pageDefault", 0, 0x7f1000f5);
        pageSpeech = new <init>("pageSpeech", 1, 0x7f10010d);
        pageTouchAim = new <init>("pageTouchAim", 2, 0x7f100101);
        pageObject = new <init>("pageObject", 3, 0x7f100102);
        pageScriptDialog = new <init>("pageScriptDialog", 4, 0x7f100112);
        pageYesNo = new <init>("pageYesNo", 5, 0x7f100108);
        pageDetails = new <init>("pageDetails", 6, 0x7f100113);
        $VALUES = (new .VALUES[] {
            pageDefault, pageSpeech, pageTouchAim, pageObject, pageScriptDialog, pageYesNo, pageDetails
        });
    }

    private (String s, int i, int j)
    {
        super(s, i);
        pageViewId = j;
    }
}
