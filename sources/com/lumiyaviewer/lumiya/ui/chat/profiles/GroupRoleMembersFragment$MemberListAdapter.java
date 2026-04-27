// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import de.greenrobot.dao.query.LazyList;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupRoleMembersFragment

private class layoutInflater extends android.support.v7.widget.berListAdapter
{

    private boolean canDeleteMembers;
    private boolean canDeleteMyself;
    private LazyList data;
    private final LayoutInflater layoutInflater;
    final GroupRoleMembersFragment this$0;

    public int getItemCount()
    {
        if (data != null)
        {
            return data.size();
        } else
        {
            return 0;
        }
    }

    public volatile void onBindViewHolder(android.support.v7.widget.berListAdapter berlistadapter, int i)
    {
        onBindViewHolder((onBindViewHolder)berlistadapter, i);
    }

    public void onBindViewHolder(onBindViewHolder onbindviewholder, int i)
    {
        if (data != null && i >= 0 && i < data.size())
        {
            onbindviewholder.indToData((GroupRoleMember)data.get(i), canDeleteMembers, canDeleteMyself);
        }
    }

    public volatile android.support.v7.widget.berListAdapter onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        return onCreateViewHolder(viewgroup, i);
    }

    public onCreateViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
    {
        viewgroup = layoutInflater.inflate(0x7f04004d, viewgroup, false);
        return new init>(GroupRoleMembersFragment.this, viewgroup, GroupRoleMembersFragment._2D_get0(GroupRoleMembersFragment.this).getUserID());
    }

    public volatile void onViewRecycled(android.support.v7.widget.berListAdapter berlistadapter)
    {
        onViewRecycled((onViewRecycled)berlistadapter);
    }

    public void onViewRecycled(onViewRecycled onviewrecycled)
    {
        onviewrecycled.ecycle();
    }

    public void setData(LazyList lazylist, boolean flag, boolean flag1)
    {
        data = lazylist;
        canDeleteMembers = flag;
        canDeleteMyself = flag1;
        notifyDataSetChanged();
    }

    Y(Context context)
    {
        this$0 = GroupRoleMembersFragment.this;
        super();
        data = null;
        canDeleteMembers = false;
        canDeleteMyself = false;
        layoutInflater = LayoutInflater.from(context);
    }
}
