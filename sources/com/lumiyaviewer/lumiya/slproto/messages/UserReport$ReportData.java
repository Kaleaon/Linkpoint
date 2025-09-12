// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            UserReport

public static class 
{

    public UUID AbuseRegionID;
    public byte AbuseRegionName[];
    public UUID AbuserID;
    public int Category;
    public int CheckFlags;
    public byte Details[];
    public UUID ObjectID;
    public LLVector3 Position;
    public int ReportType;
    public UUID ScreenshotID;
    public byte Summary[];
    public byte VersionString[];

    public ()
    {
    }
}
