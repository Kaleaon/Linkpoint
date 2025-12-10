package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.messages.LoadURL
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatTextEvent : SLChatEvent {
    protected String text

    SLChatTextEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.text = chatMessage.getMessageText()
    }

    SLChatTextEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str) {
        super(improvedInstantMessage, uuid, chatMessageSource)
        if (str != null) {
            this.text = str
        } else if (improvedInstantMessage != null) {
            this.text = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        } else {
            this.text = null
        }
    }

    SLChatTextEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, LoadURL loadURL) {
        super(chatMessageSource, uuid)
        this.text = SLMessage.stringFromVariableUTF(loadURL.Data_Field.Message) + ": " + SLMessage.stringFromVariableUTF(loadURL.Data_Field.URL)
    }

    SLChatTextEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, String str) {
        super(chatMessageSource, uuid)
        this.text = str
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Text
    }

    fun getRawText(): String {
        return (this.text == null || !this.text.startsWith("/me ")) ? this.text : this.text.substring(4)
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        return (this.text == null || !this.text.startsWith("/me ")) ? this.text : this.text.substring(4)
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return this.text != null && this.text.startsWith("/me ")
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setMessageText(this.text)
    }
}
