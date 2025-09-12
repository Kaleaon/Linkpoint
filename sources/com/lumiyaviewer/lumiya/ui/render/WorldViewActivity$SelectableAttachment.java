// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.render;


// Referenced classes of package com.lumiyaviewer.lumiya.ui.render:
//            WorldViewActivity

private static class attachmentName
{

    private String attachmentName;
    private int localID;

    public int getLocalID()
    {
        return localID;
    }

    public String toString()
    {
        return attachmentName;
    }

    public (int i, String s)
    {
        localID = i;
        attachmentName = s;
    }
}
