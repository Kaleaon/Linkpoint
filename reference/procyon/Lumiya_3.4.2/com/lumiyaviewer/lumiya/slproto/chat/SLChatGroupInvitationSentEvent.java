// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public final class SLChatGroupInvitationSentEvent extends SLChatEvent
{
    public SLChatGroupInvitationSentEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
    }
    
    public SLChatGroupInvitationSentEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid) {
        super(chatMessageSource, uuid);
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.GroupInvitationSent;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        String sourceName = this.source.getSourceName(userManager);
        if (sourceName == null) {
            sourceName = "(unknown)";
        }
        return context.getString(2131296638, new Object[] { sourceName });
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
    }
}
