// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ChatterListAdapter

class ChatterListSimpleAdapter extends ChatterListAdapter
{

    private ImmutableList data;

    ChatterListSimpleAdapter(Context context, UserManager usermanager)
    {
        super(context, usermanager);
        data = null;
    }

    public boolean areAllItemsEnabled()
    {
        return true;
    }

    public int getCount()
    {
        if (data != null)
        {
            return data.size();
        } else
        {
            return 0;
        }
    }

    public Object getItem(int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            return data.get(i);
        } else
        {
            return null;
        }
    }

    public long getItemId(int i)
    {
        return 0L;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    public boolean isEmpty()
    {
        if (data != null)
        {
            return data.isEmpty();
        } else
        {
            return true;
        }
    }

    public boolean isEnabled(int i)
    {
        return true;
    }

    protected void setData(ImmutableList immutablelist)
    {
        data = immutablelist;
        notifyDataSetChanged();
    }
}
