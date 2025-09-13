package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public class SLChatTextEvent extends SLChatEvent {
    protected final String text;

    public SLChatTextEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.text = chatMessage.getMessageText();
    }

    public SLChatTextEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str) {
        super(improvedInstantMessage, uuid, chatMessageSource);
        if (str != null) {
            this.text = str;
        } else if (improvedInstantMessage != null) {
            this.text = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        } else {
            this.text = null;
        }
    }

    public SLChatTextEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, LoadURL loadURL) {
        super(chatMessageSource, uuid);
        this.text = SLMessage.stringFromVariableUTF(loadURL.Data_Field.Message) + ": " + SLMessage.stringFromVariableUTF(loadURL.Data_Field.URL);
    }

    public SLChatTextEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, String str) {
        super(chatMessageSource, uuid);
        this.text = str;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Text;
    }

    public String getRawText() {
        return (this.text == null || !this.text.startsWith("/me ")) ? this.text : this.text.substring(4);
    }

    public String getText(Context context, @Nonnull UserManager userManager) {
        return (this.text == null || !this.text.startsWith("/me ")) ? this.text : this.text.substring(4);
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return this.text != null && this.text.startsWith("/me ");
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.text);
    }
}
