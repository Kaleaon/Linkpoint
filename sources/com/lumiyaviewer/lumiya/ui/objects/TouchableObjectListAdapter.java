// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

class TouchableObjectListAdapter extends BaseAdapter
{

    private final Context context;
    private ImmutableList objects;

    TouchableObjectListAdapter(Context context1)
    {
        objects = ImmutableList.of();
        context = context1;
    }

    public int getCount()
    {
        return objects.size();
    }

    public SLObjectInfo getItem(int i)
    {
        if (i >= 0 && i < objects.size())
        {
            return (SLObjectInfo)objects.get(i);
        } else
        {
            return null;
        }
    }

    public volatile Object getItem(int i)
    {
        return getItem(i);
    }

    public long getItemId(int i)
    {
        SLObjectInfo slobjectinfo = getItem(i);
        if (slobjectinfo != null)
        {
            return (long)slobjectinfo.localID;
        } else
        {
            return -1L;
        }
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        Object obj = null;
        SLObjectInfo slobjectinfo = getItem(i);
        if (slobjectinfo == null)
        {
            return null;
        }
        if (view != null && view.getId() != 0x7f100299)
        {
            view = obj;
        }
        if (view == null)
        {
            view = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(0x7f0400ae, viewgroup, false);
        }
        ((TextView)view.findViewById(0x7f10029a)).setText(slobjectinfo.getName());
        viewgroup = view.findViewById(0x7f10029b);
        if (slobjectinfo.isTouchable())
        {
            i = 0;
        } else
        {
            i = 4;
        }
        viewgroup.setVisibility(i);
        return view;
    }

    public boolean hasStableIds()
    {
        return true;
    }

    public boolean isEmpty()
    {
        return objects.isEmpty();
    }

    public void setData(ImmutableList immutablelist)
    {
        if (immutablelist == null)
        {
            immutablelist = ImmutableList.of();
        }
        objects = immutablelist;
        notifyDataSetChanged();
    }
}
