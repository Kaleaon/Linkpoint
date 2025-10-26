package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatOnlineOfflineEvent : SLChatEvent() {
    private val Boolean wentOnline

    public SLChatOnlineOfflineEvent(ChatMessage chatMessage, UUID uuid, Boolean z) {
        super(chatMessage, uuid)
        this.wentOnline = z
    }

    public SLChatOnlineOfflineEvent(ChatMessageSource chatMessageSource, UUID uuid, Boolean z) {
        super(chatMessageSource, uuid)
        this.wentOnline = z
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return this.wentOnline ? SLChatEvent.ChatMessageType.WentOnline : SLChatEvent.ChatMessageType.WentOffline
    }

    /* access modifiers changed from: protected */
     public fun getText(context: Context, userManager: UserManager): String {
        return context.getString(this.wentOnline ? R.string.went_online : R.string.went_offline)
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
     public fun isActionMessage(userManager: UserManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
