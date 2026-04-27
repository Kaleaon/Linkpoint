package com.lumiyaviewer.lumiya.ui.objpopup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter;

public class ObjectPopupsAdapter extends RecyclerSubscribableListAdapter<SLChatEvent> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final UserManager userManager;

    public ObjectPopupsAdapter(Context context2, SubscribableList<SLChatEvent> subscribableList, UserManager userManager2) {
        super(subscribableList);
        this.context = context2;
        this.userManager = userManager2;
        this.layoutInflater = LayoutInflater.from(context2);
    }

    /* access modifiers changed from: protected */
    public void bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, SLChatEvent sLChatEvent) {
        if (viewHolder instanceof ChatEventViewHolder) {
            sLChatEvent.bindViewHolder((ChatEventViewHolder) viewHolder, this.userManager, (ChatEventTimestampUpdater) null);
        }
    }

    /* access modifiers changed from: protected */
    public RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, int i) {
        return SLChatEvent.createViewHolder(this.layoutInflater, i, viewGroup, this);
    }

    /* access modifiers changed from: protected */
    public int getObjectViewType(SLChatEvent sLChatEvent) {
        return sLChatEvent.getViewType().ordinal();
    }
}
