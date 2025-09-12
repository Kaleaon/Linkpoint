// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils.wlist;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.utils.wlist:
//            ChunkedListLoader

public static interface I
{

    public abstract Executor getListEventsExecutor();

    public abstract void onListItemAddedAtEnd();

    public abstract void onListItemChanged(int i);

    public abstract void onListItemsAdded(int i, int j);

    public abstract void onListItemsRemoved(int i, int j);

    public abstract void onListReloaded();
}
