package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.text.DateFormat
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class SLChatSessionMarkEvent : SLChatEvent {
    @Nullable
    private String description
    @NonNull
    private SessionMarkType sessionMarkType

    enum SessionMarkType {
        NewSession,
        Teleport
    }

    SLChatSessionMarkEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.sessionMarkType = SessionMarkType.values()[chatMessage.getChatChannel().intValue()]
        this.description = chatMessage.getMessageText()
    }

    SLChatSessionMarkEvent(ChatMessageSource chatMessageSource, @NonNull UUID uuid, @NonNull SessionMarkType sessionMarkType2, @Nullable String str) {
        super(chatMessageSource, uuid)
        this.sessionMarkType = sessionMarkType2
        this.description = str
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SessionMark
    }

    /* access modifiers changed from: protected */
    fun getText(Context context, @NonNull UserManager userManager): String {
        if (this.sessionMarkType == SessionMarkType.Teleport) {
            return context.getString(R.string.teleport_complete_format, Array<Any>{this.description})
        }
        return context.getString(R.string.new_session_mark_format, Array<Any>{DateFormat.getDateTimeInstance(3, 3).format(getTimestamp())})
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_SESSION_MARK
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.description)
        chatMessage.setChatChannel(Integer.valueOf(this.sessionMarkType.ordinal()))
    }
}
