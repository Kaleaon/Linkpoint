// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import java.util.UUID;

public class ChatMessageSourceObject extends ChatMessageSource
{
    public final String name;
    @Nonnull
    public final UUID uuid;
    
    ChatMessageSourceObject(final ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.name = chatMessage.getSenderName();
    }
    
    public ChatMessageSourceObject(@Nonnull final UUID uuid, final String name) {
        this.uuid = uuid;
        this.name = name;
    }
    
    @Nonnull
    @Override
    public ChatterID getDefaultChatter(final UUID uuid) {
        return ChatterID.getLocalChatterID(uuid);
    }
    
    @Nullable
    @Override
    public String getSourceName(@Nonnull final UserManager userManager) {
        return this.name;
    }
    
    @Nonnull
    @Override
    public ChatMessageSourceType getSourceType() {
        return ChatMessageSourceType.Object;
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
