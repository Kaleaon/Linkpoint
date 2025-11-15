package com.lumiyaviewer.lumiya.ui.objpopup
import java.util.*

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.manager.SubscribableList
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater
import com.lumiyaviewer.lumiya.ui.common.RecyclerSubscribableListAdapter

class ObjectPopupsAdapter : RecyclerSubscribableListAdapter<SLChatEvent> {
    private Context context
    private LayoutInflater layoutInflater
    private UserManager userManager

    ObjectPopupsAdapter(Context context2, SubscribableList<SLChatEvent> subscribableList, UserManager userManager2) {
        super(subscribableList)
        this.context = context2
        this.userManager = userManager2
        this.layoutInflater = LayoutInflater.from(context2)
    }

    /* access modifiers changed from: protected */
    Unit bindObjectViewHolder(RecyclerView.ViewHolder viewHolder, SLChatEvent sLChatEvent) {
        if (viewHolder instanceof ChatEventViewHolder) {
            sLChatEvent.bindViewHolder((ChatEventViewHolder) viewHolder, this.userManager, (ChatEventTimestampUpdater) null)
        }
    }

    /* access modifiers changed from: protected */
    RecyclerView.ViewHolder createObjectViewHolder(ViewGroup viewGroup, Int i) {
        return SLChatEvent.createViewHolder(this.layoutInflater, i, viewGroup, this)
    }

    /* access modifiers changed from: protected */
    Int getObjectViewType(SLChatEvent sLChatEvent) {
        return sLChatEvent.getViewType().ordinal()
    }
}
