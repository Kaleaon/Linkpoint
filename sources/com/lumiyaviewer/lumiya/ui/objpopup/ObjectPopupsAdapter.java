// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter;

public class ObjectPopupsAdapter extends RecyclerSubscribableListAdapter
{

    private final Context context;
    private final LayoutInflater layoutInflater;
    private final UserManager userManager;

    public ObjectPopupsAdapter(Context context1, SubscribableList subscribablelist, UserManager usermanager)
    {
        super(subscribablelist);
        context = context1;
        userManager = usermanager;
        layoutInflater = LayoutInflater.from(context1);
    }

    protected void bindObjectViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, SLChatEvent slchatevent)
    {
        if (viewholder instanceof ChatEventViewHolder)
        {
            slchatevent.bindViewHolder((ChatEventViewHolder)viewholder, userManager, null);
        }
    }

    protected volatile void bindObjectViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, Object obj)
    {
        bindObjectViewHolder(viewholder, (SLChatEvent)obj);
    }

    protected android.support.v7.widget.RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewgroup, int i)
    {
        return SLChatEvent.createViewHolder(layoutInflater, i, viewgroup, this);
    }

    protected int getObjectViewType(SLChatEvent slchatevent)
    {
        return slchatevent.getViewType().ordinal();
    }

    protected volatile int getObjectViewType(Object obj)
    {
        return getObjectViewType((SLChatEvent)obj);
    }
}
