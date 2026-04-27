// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            RecyclerSubscribableListAdapter

private class backingList extends AbstractList
{

    private final List backingList = new ArrayList();
    final RecyclerSubscribableListAdapter this$0;

    public void add(int i, Object obj)
    {
        backingList.add(i, obj);
        notifyItemInserted(i);
    }

    public void clear()
    {
        backingList.clear();
        notifyDataSetChanged();
    }

    public Object get(int i)
    {
        return backingList.get(i);
    }

    public Object remove(int i)
    {
        Object obj = backingList.remove(i);
        notifyItemRemoved(i);
        return obj;
    }

    public Object set(int i, Object obj)
    {
        obj = backingList.set(i, obj);
        notifyItemChanged(i);
        return obj;
    }

    public int size()
    {
        return backingList.size();
    }

    public (SubscribableList subscribablelist, Optional optional)
    {
        this$0 = RecyclerSubscribableListAdapter.this;
        super();
        backingList.addAll(subscribablelist.addSubscription(this, optional));
    }
}
