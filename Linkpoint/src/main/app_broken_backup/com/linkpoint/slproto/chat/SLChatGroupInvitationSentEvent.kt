package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatGroupInvitationSentEvent : SLChatEvent {
    SLChatGroupInvitationSentEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLChatGroupInvitationSentEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid) {
        super(chatMessageSource, uuid)
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.GroupInvitationSent
    }

    /* access modifiers changed from: protected */
    fun getText(Context context, @NonNull UserManager userManager): String {
        var sourceName: String = this.source.getSourceName(userManager)
        Array<Any> objArr = Object[1]
        if (sourceName == null) {
            sourceName = "(unknown)"
        }
        objArr[0] = sourceName
        return context.getString(R.string.invitation_sent_text, objArr)
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return false
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
    }
}
