package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.text.DateFormat
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class SLChatSessionMarkEvent : SLChatEvent() {
    private val String description
    private val SessionMarkType sessionMarkType

    enum class SessionMarkType {
        NewSession,
        Teleport
    }

    public SLChatSessionMarkEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.sessionMarkType = SessionMarkType.values()[chatMessage.getChatChannel().intValue()]
        this.description = chatMessage.getMessageText()
    }

    public SLChatSessionMarkEvent(ChatMessageSource chatMessageSource, UUID uuid, SessionMarkType sessionMarkType2, String str) {
        super(chatMessageSource, uuid)
        this.sessionMarkType = sessionMarkType2
        this.description = str
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SessionMark
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, UserManager userManager) {
        if (this.sessionMarkType == SessionMarkType.Teleport) {
            return context.getString(R.string.teleport_complete_format, Object[]{this.description})
        }
        return context.getString(R.string.new_session_mark_format, Object[]{DateFormat.getDateTimeInstance(3, 3).format(getTimestamp())})
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_SESSION_MARK
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return true
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.description)
        chatMessage.setChatChannel(Integer.valueOf(this.sessionMarkType.ordinal()))
    }
}
