package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatGroupInvitationSentEvent : SLChatEvent() {
    public SLChatGroupInvitationSentEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
    }

    public SLChatGroupInvitationSentEvent(ChatMessageSource chatMessageSource, UUID uuid) {
        super(chatMessageSource, uuid)
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.GroupInvitationSent
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, UserManager userManager) {
        String sourceName = this.source.getSourceName(userManager)
        Object[] objArr = Object[1]
        if (sourceName == null) {
            sourceName = "(unknown)"
        }
        objArr[0] = sourceName
        return context.getString(R.string.invitation_sent_text, objArr)
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return false
    }

    fun serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
