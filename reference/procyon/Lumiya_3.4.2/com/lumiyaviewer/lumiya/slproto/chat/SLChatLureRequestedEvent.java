// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public final class SLChatLureRequestedEvent extends SLChatEvent
{
    private final String message;
    
    public SLChatLureRequestedEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.message = chatMessage.getMessageText();
    }
    
    public SLChatLureRequestedEvent(final String message, @Nonnull final UUID uuid) {
        super(ChatMessageSourceUnknown.getInstance(), uuid);
        this.message = message;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.LureRequested;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        if (Strings.isNullOrEmpty(this.message)) {
            return context.getString(2131296437);
        }
        return context.getString(2131296436, new Object[] { this.message });
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_NORMAL;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return false;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.message);
    }
}
