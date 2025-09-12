// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.wlist:
//            ChunkedListLoader

protected static class fromId
{

    public final List entries;
    final long fromId;
    final boolean hasMore;

    public (List list, boolean flag, long l)
    {
        entries = list;
        hasMore = flag;
        fromId = l;
    }
}
