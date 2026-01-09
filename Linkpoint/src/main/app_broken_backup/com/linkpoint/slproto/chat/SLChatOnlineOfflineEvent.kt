package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatOnlineOfflineEvent : SLChatEvent {
    private Boolean wentOnline

    SLChatOnlineOfflineEvent(ChatMessage chatMessage, @NonNull UUID uuid, Boolean z) {
        super(chatMessage, uuid)
        this.wentOnline = z
    }

    SLChatOnlineOfflineEvent(ChatMessageSource chatMessageSource, @NonNull UUID uuid, Boolean z) {
        super(chatMessageSource, uuid)
        this.wentOnline = z
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return this.wentOnline ? SLChatEvent.ChatMessageType.WentOnline : SLChatEvent.ChatMessageType.WentOffline
    }

    /* access modifiers changed from: protected */
    fun getText(Context context, @NonNull UserManager userManager): String {
        return context.getString(this.wentOnline ? R.string.went_online : R.string.went_offline)
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
    }
}
