// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.avapicker;

import com.google.common.base.Predicate;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayData;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.avapicker:
//            AvatarPickerFragment

private static class <init>
    implements Predicate
{

    public boolean apply(ChatterDisplayData chatterdisplaydata)
    {
        return chatterdisplaydata != null && (chatterdisplaydata.chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.init>) && chatterdisplaydata.chatterID.isValidUUID();
    }

    public volatile boolean apply(Object obj)
    {
        return apply((ChatterDisplayData)obj);
    }

    private ()
    {
    }

    ( )
    {
        this();
    }
}
