package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatSystemMessageEvent : SLChatEvent() {
    private val String text

    public SLChatSystemMessageEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.text = chatMessage.getMessageText()
    }

    public SLChatSystemMessageEvent(ChatMessageSource chatMessageSource, UUID uuid, String str) {
        super(chatMessageSource, uuid)
        this.text = str
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SystemMessage
    }

    /* access modifiers changed from: protected */
     public fun getText(context: Context, userManager: UserManager): String {
        return this.text
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_PLAIN
    }

    /* access modifiers changed from: protected */
     public fun isActionMessage(userManager: UserManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.text)
    }
}
