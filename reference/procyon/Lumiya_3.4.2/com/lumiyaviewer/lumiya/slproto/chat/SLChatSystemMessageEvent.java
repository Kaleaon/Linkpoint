// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public final class SLChatSystemMessageEvent extends SLChatEvent
{
    @Nonnull
    private final String text;
    
    public SLChatSystemMessageEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.text = chatMessage.getMessageText();
    }
    
    public SLChatSystemMessageEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, @Nonnull final String text) {
        super(chatMessageSource, uuid);
        this.text = text;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.SystemMessage;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        return this.text;
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_PLAIN;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return true;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.text);
    }
}
