// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import javax.annotation.Nonnull;

class ChatMessageSourceGroup extends ChatMessageSource
{
    @Nonnull
    public final String name;
    @Nonnull
    public final UUID uuid;
    
    ChatMessageSourceGroup(final ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.name = chatMessage.getSenderName();
    }
    
    @Nonnull
    @Override
    public ChatterID getDefaultChatter(final UUID uuid) {
        return ChatterID.getGroupChatterID(uuid, this.uuid);
    }
    
    @Nullable
    @Override
    public String getSourceName(@Nonnull final UserManager userManager) {
        return this.name;
    }
    
    @Nonnull
    @Override
    public ChatMessageSourceType getSourceType() {
        return ChatMessageSourceType.Group;
    }
    
    @Nullable
    @Override
    public UUID getSourceUUID() {
        return this.uuid;
    }
    
    @Override
    public void serializeTo(@Nonnull final ChatMessage chatMessage) {
        super.serializeTo(chatMessage);
        chatMessage.setSenderUUID(this.uuid);
        chatMessage.setSenderName(this.name);
    }
}
