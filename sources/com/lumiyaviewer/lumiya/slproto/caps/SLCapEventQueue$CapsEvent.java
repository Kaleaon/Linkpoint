// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.caps;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.caps:
//            SLCapEventQueue

public static class eventBody
{

    public LLSDNode eventBody;
    public ype eventType;

    public ype(String s, LLSDNode llsdnode)
    {
        try
        {
            eventType = ype.valueOf(s);
        }
        // Misplaced declaration of an exception variable
        catch (String s)
        {
            eventType = ype.UnknownCapsEvent;
        }
        eventBody = llsdnode;
    }
}
