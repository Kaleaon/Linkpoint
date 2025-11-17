package com.linkpoint.ui.objpopup
import java.util.*

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.linkpoint.slproto.chat.generic.ChatEventViewHolder
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.manager.SubscribableList
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatEventTimestampUpdater
import com.linkpoint.ui.common.RecyclerSubscribableListAdapter

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
