// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import java.util.UUID;
import javax.annotation.Nullable;

public class ChatMessageSourceUser extends ChatMessageSource
{
    @Nullable
    private String displayName;
    @Nullable
    private String legacyName;
    @Nonnull
    public final UUID uuid;
    
    ChatMessageSourceUser(final ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.displayName = chatMessage.getSenderName();
        this.legacyName = chatMessage.getSenderLegacyName();
    }
    
    public ChatMessageSourceUser(@Nonnull final UUID uuid) {
        this.uuid = uuid;
        this.displayName = null;
        this.legacyName = null;
    }
    
    @Nonnull
    @Override
    public ChatterID getDefaultChatter(final UUID uuid) {
        return ChatterID.getUserChatterID(uuid, this.uuid);
    }
    
    @Nullable
    @Override
    public String getSourceName(@Nonnull final UserManager userManager) {
        if (GlobalOptions.getInstance().isLegacyUserNames()) {
            return this.legacyName;
        }
        return this.displayName;
    }
    
    @Nonnull
    @Override
    public ChatMessageSourceType getSourceType() {
        return ChatMessageSourceType.User;
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
        chatMessage.setSenderName(this.displayName);
        chatMessage.setSenderLegacyName(this.legacyName);
    }
    
    public void setDisplayName(@Nullable final String displayName) {
        this.displayName = displayName;
    }
    
    public void setLegacyName(@Nullable final String legacyName) {
        this.legacyName = legacyName;
    }
}
