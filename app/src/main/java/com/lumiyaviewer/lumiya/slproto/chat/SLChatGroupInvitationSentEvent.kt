package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class SLChatGroupInvitationSentEvent : SLChatEvent {
    SLChatGroupInvitationSentEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLChatGroupInvitationSentEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid) {
        super(chatMessageSource, uuid)
    }

    /* access modifiers changed from: protected */
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.GroupInvitationSent
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @Nonnull UserManager userManager) {
        String sourceName = this.source.getSourceName(userManager)
        Object[] objArr = Object[1]
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
    Boolean isActionMessage(@Nonnull UserManager userManager) {
        return false
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
