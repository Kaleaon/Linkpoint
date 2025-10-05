package com.linkpoint.slproto.chat

import android.content.Context
import com.google.common.base.Strings
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatLureRequestedEvent : SLChatEvent() {
    private val String message

    public SLChatLureRequestedEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.message = chatMessage.getMessageText()
    }

    public SLChatLureRequestedEvent(String str, UUID uuid) {
        super((ChatMessageSource) ChatMessageSourceUnknown.getInstance(), uuid)
        this.message = str
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequested
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, UserManager userManager) {
        if (Strings.isNullOrEmpty(this.message)) {
            return context.getString(R.string.chat_teleport_requested_no_message)
        }
        return context.getString(R.string.chat_teleport_requested_message, Object[]{this.message})
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return false
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.message)
    }
}
