// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.mutelist;

import com.google.common.collect.Ordering;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.mutelist:
//            MuteListData, MuteListEntry, MuteType

static final class  extends Ordering
{

    public int compare(MuteListEntry mutelistentry, MuteListEntry mutelistentry1)
    {
        int i = mutelistentry.type.getViewOrder() - mutelistentry1.type.getViewOrder();
        if (i != 0)
        {
            return i;
        } else
        {
            return mutelistentry.name.compareToIgnoreCase(mutelistentry1.name);
        }
    }

    public volatile int compare(Object obj, Object obj1)
    {
        return compare((MuteListEntry)obj, (MuteListEntry)obj1);
    }

    ()
    {
    }
}
