// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objpopup;

import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import android.support.v7.widget.RecyclerView;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.LayoutInflater;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter;

public class ObjectPopupsAdapter extends RecyclerSubscribableListAdapter<SLChatEvent>
{
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final UserManager userManager;
    
    public ObjectPopupsAdapter(final Context context, final SubscribableList<SLChatEvent> list, final UserManager userManager) {
        super(list);
        this.context = context;
        this.userManager = userManager;
        this.layoutInflater = LayoutInflater.from(context);
    }
    
    @Override
    protected void bindObjectViewHolder(final ViewHolder viewHolder, final SLChatEvent slChatEvent) {
        if (viewHolder instanceof ChatEventViewHolder) {
            slChatEvent.bindViewHolder((ChatEventViewHolder)viewHolder, this.userManager, null);
        }
    }
    
    @Override
    protected ViewHolder createObjectViewHolder(final ViewGroup viewGroup, final int n) {
        return SLChatEvent.createViewHolder(this.layoutInflater, n, viewGroup, this);
    }
    
    @Override
    protected int getObjectViewType(final SLChatEvent slChatEvent) {
        return slChatEvent.getViewType().ordinal();
    }
}
