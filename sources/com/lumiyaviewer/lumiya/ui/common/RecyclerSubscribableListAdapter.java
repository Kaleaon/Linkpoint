// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.view.ViewGroup;
import com.google.common.base.Optional;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

public abstract class RecyclerSubscribableListAdapter extends android.support.v7.widget.RecyclerView.Adapter
{
    private class LocalItemList extends AbstractList
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

        public LocalItemList(SubscribableList subscribablelist, Optional optional)
        {
            this$0 = RecyclerSubscribableListAdapter.this;
            super();
            backingList.addAll(subscribablelist.addSubscription(this, optional));
        }
    }


    private final LocalItemList localItemList;

    public RecyclerSubscribableListAdapter(SubscribableList subscribablelist)
    {
        localItemList = new LocalItemList(subscribablelist, Optional.of(UIThreadExecutor.getInstance()));
    }

    protected abstract void bindObjectViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, Object obj);

    protected abstract android.support.v7.widget.RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewgroup, int i);

    public int getItemCount()
    {
        return localItemList.size();
    }

    public int getItemViewType(int i)
    {
        return getObjectViewType(localItemList.get(i));
    }

    public Object getObject(int i)
    {
        if (i >= 0 && i < localItemList.size())
        {
            return localItemList.get(i);
        } else
        {
            return null;
        }
    }

    protected abstract int getObjectViewType(Object obj);

    public void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
    {
        bindObjectViewHolder(viewholder, localItemList.get(i));
    }

    public android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return createObjectViewHolder(viewgroup, i);
    }
}
