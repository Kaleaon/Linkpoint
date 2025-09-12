// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserGroupsProfileTab

private static class <init> extends BaseAdapter
{

    private ImmutableList avatarGroupList;
    private final LayoutInflater inflater;

    public int getCount()
    {
        if (avatarGroupList != null)
        {
            return avatarGroupList.size();
        } else
        {
            return 0;
        }
    }

    public com.lumiyaviewer.lumiya.slproto.modules.groups.GroupList getItem(int i)
    {
        if (avatarGroupList != null && i >= 0 && i < avatarGroupList.size())
        {
            return (com.lumiyaviewer.lumiya.slproto.modules.groups.GroupList)avatarGroupList.get(i);
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
            view1 = inflater.inflate(0x1090003, viewgroup, false);
        }
        view = getItem(i);
        if (view != null)
        {
            ((TextView)view1.findViewById(0x1020014)).setText(((com.lumiyaviewer.lumiya.slproto.modules.groups.m) (view)).oupName);
        }
        return view1;
    }

    public boolean hasStableIds()
    {
        return false;
    }

    void setData(AvatarGroupList avatargrouplist)
    {
        com.google.common.collect.t t = new com.google.common.collect.t();
        t.t(avatargrouplist.Groups.values());
        avatarGroupList = t.avatarGroupList();
        notifyDataSetChanged();
    }

    private try(Context context)
    {
        avatarGroupList = null;
        inflater = LayoutInflater.from(context);
    }

    inflater(Context context, inflater inflater1)
    {
        this(context);
    }
}
