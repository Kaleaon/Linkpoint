package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.text.DateFormat
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLChatSessionMarkEvent : SLChatEvent {
    @Nullable
    private String description
    @Nonnull
    private SessionMarkType sessionMarkType

    enum SessionMarkType {
        NewSession,
        Teleport
    }

    SLChatSessionMarkEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
        this.sessionMarkType = SessionMarkType.values()[chatMessage.getChatChannel().intValue()]
        this.description = chatMessage.getMessageText()
    }

    SLChatSessionMarkEvent(ChatMessageSource chatMessageSource, @Nonnull UUID uuid, @Nonnull SessionMarkType sessionMarkType2, @Nullable String str) {
        super(chatMessageSource, uuid)
        this.sessionMarkType = sessionMarkType2
        this.description = str
    }

    /* access modifiers changed from: protected */
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SessionMark
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @Nonnull UserManager userManager) {
        if (this.sessionMarkType == SessionMarkType.Teleport) {
            return context.getString(R.string.teleport_complete_format, Array<Any>{this.description})
        }
        return context.getString(R.string.new_session_mark_format, Array<Any>{DateFormat.getDateTimeInstance(3, 3).format(getTimestamp())})
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_SESSION_MARK
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@Nonnull UserManager userManager) {
        return true
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.description)
        chatMessage.setChatChannel(Integer.valueOf(this.sessionMarkType.ordinal()))
    }
}
