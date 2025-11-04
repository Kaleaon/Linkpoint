// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public final class SLChatFriendshipResultEvent extends SLChatEvent
{
    private final boolean accepted;
    
    public SLChatFriendshipResultEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.accepted = chatMessage.getAccepted();
    }
    
    public SLChatFriendshipResultEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(improvedInstantMessage, uuid, chatMessageSource);
        this.accepted = (improvedInstantMessage.MessageBlock_Field.Dialog == 39);
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.FriendshipResult;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        int n;
        if (this.accepted) {
            n = 2131296550;
        }
        else {
            n = 2131296551;
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
        chatMessage.setAccepted(this.accepted);
    }
}
