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

public final class SLChatOnlineOfflineEvent extends SLChatEvent
{
    private final boolean wentOnline;
    
    public SLChatOnlineOfflineEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid, final boolean wentOnline) {
        super(chatMessage, uuid);
        this.wentOnline = wentOnline;
    }
    
    public SLChatOnlineOfflineEvent(final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final boolean wentOnline) {
        super(chatMessageSource, uuid);
        this.wentOnline = wentOnline;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        ChatMessageType chatMessageType;
        if (this.wentOnline) {
            chatMessageType = ChatMessageType.WentOnline;
        }
        else {
            chatMessageType = ChatMessageType.WentOffline;
        }
        return chatMessageType;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        int n;
        if (this.wentOnline) {
            n = 2131297161;
        }
        else {
            n = 2131297160;
        }
        return context.getString(n);
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_NORMAL;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return true;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
