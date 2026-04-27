// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import com.lumiyaviewer.lumiya.slproto.inventory.SLTaskInventory;

public class TaskInventoryListAdapter extends BaseAdapter
{

    private final Context context;
    private SLTaskInventory taskInventory;

    public TaskInventoryListAdapter(Context context1)
    {
        context = context1;
        taskInventory = null;
    }

    public int getCount()
    {
        if (taskInventory != null)
        {
            return taskInventory.entries.size();
        } else
        {
            return 0;
        }
    }

    public SLInventoryEntry getItem(int i)
    {
        if (taskInventory != null)
        {
            return (SLInventoryEntry)taskInventory.entries.get(i);
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
        return (long)i;
    }

    public View getView(int i, View view, ViewGroup viewgroup)
    {
        View view1 = view;
        if (view == null)
        {
            view1 = ((LayoutInflater)context.getSystemService("layout_inflater")).inflate(0x7f040053, viewgroup, false);
        }
        view = getItem(i);
        ((TextView)view1.findViewById(0x7f1001bf)).setText(((SLInventoryEntry) (view)).name);
        i = view.getDrawableResource();
        if (i >= 0)
        {
            ((ImageView)view1.findViewById(0x7f1001bd)).setImageResource(i);
            i = view.getSubtypeDrawableResource();
            if (i >= 0)
            {
                ((ImageView)view1.findViewById(0x7f1001be)).setImageResource(i);
            } else
            {
                ((ImageView)view1.findViewById(0x7f1001be)).setImageBitmap(null);
            }
        } else
        {
            ((ImageView)view1.findViewById(0x7f1001bd)).setImageBitmap(null);
            ((ImageView)view1.findViewById(0x7f1001be)).setImageBitmap(null);
        }
        view1.findViewById(0x7f1001c0).setVisibility(8);
        return view1;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public void setData(SLTaskInventory sltaskinventory)
    {
        taskInventory = sltaskinventory;
        notifyDataSetChanged();
    }
}
