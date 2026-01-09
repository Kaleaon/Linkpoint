package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatSystemMessageEvent : SLChatEvent {
    @NonNull
    private String text

    SLChatSystemMessageEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.text = chatMessage.getMessageText()
    }

    SLChatSystemMessageEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, @NonNull String str) {
        super(chatMessageSource, uuid)
        this.text = str
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SystemMessage
    }

    /* access modifiers changed from: protected */
    fun getText(Context context, @NonNull UserManager userManager): String {
        return this.text
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_PLAIN
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.text)
    }
}
