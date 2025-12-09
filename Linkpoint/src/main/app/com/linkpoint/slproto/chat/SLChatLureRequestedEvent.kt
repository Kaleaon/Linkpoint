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
import androidx.annotation.NonNull

class SLChatLureRequestedEvent : SLChatEvent {
    private String message

    SLChatLureRequestedEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.message = chatMessage.getMessageText()
    }

    SLChatLureRequestedEvent(String str, @NonNull UUID uuid) {
        super((ChatMessageSourceUnknown as ChatMessageSource).getInstance(), uuid)
        this.message = str
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequested
    }

    /* access modifiers changed from: protected */
    fun getText(Context context, @NonNull UserManager userManager): String {
        if (Strings.isNullOrEmpty(this.message)) {
            return context.getString(R.string.chat_teleport_requested_no_message)
        }
        return context.getString(R.string.chat_teleport_requested_message, Array<Any>{this.message})
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return false
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.message)
    }
}
