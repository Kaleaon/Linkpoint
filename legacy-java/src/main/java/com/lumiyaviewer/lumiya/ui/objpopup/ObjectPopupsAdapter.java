package com.lumiyaviewer.lumiya.ui.objpopup;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter;

/* loaded from: classes.dex */
public class ObjectPopupsAdapter extends RecyclerSubscribableListAdapter<SLChatEvent> {
    private final Context context;
    private final LayoutInflater layoutInflater;
    private final UserManager userManager;

    public ObjectPopupsAdapter(Context context, SubscribableList<SLChatEvent> subscribableList, UserManager userManager) {
        super(subscribableList);
        this.context = context;
        this.userManager = userManager;
        this.layoutInflater = LayoutInflater.from(context);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter
    public void bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, SLChatEvent sLChatEvent) {
        if (viewHolder instanceof ChatEventViewHolder) {
            sLChatEvent.bindViewHolder((ChatEventViewHolder) viewHolder, this.userManager, null);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter
    protected RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, int i) {
        return SLChatEvent.createViewHolder(this.layoutInflater, i, viewGroup, this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter
    public int getObjectViewType(SLChatEvent sLChatEvent) {
        return sLChatEvent.getViewType().ordinal();
    }
}
