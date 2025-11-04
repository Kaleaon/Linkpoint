// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class SLChatTextEvent extends SLChatEvent
{
    protected final String text;
    
    public SLChatTextEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.text = chatMessage.getMessageText();
    }
    
    public SLChatTextEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage, final String text) {
        super(improvedInstantMessage, uuid, chatMessageSource);
        if (text != null) {
            this.text = text;
        }
        else if (improvedInstantMessage != null) {
            this.text = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message);
        }
        else {
            this.text = null;
        }
    }
    
    public SLChatTextEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final LoadURL loadURL) {
        super(chatMessageSource, uuid);
        this.text = SLMessage.stringFromVariableUTF(loadURL.Data_Field.Message) + ": " + SLMessage.stringFromVariableUTF(loadURL.Data_Field.URL);
    }
    
    public SLChatTextEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final String text) {
        super(chatMessageSource, uuid);
        this.text = text;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.Text;
    }
    
    public String getRawText() {
        if (this.text != null && this.text.startsWith("/me ")) {
            return this.text.substring(4);
        }
        return this.text;
    }
    
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        if (this.text != null && this.text.startsWith("/me ")) {
            return this.text.substring(4);
        }
        return this.text;
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_NORMAL;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return this.text != null && this.text.startsWith("/me ");
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.text);
    }
}
