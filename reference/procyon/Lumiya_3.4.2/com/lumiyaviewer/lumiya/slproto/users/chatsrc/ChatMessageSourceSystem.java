// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.UUID;

class ChatMessageSourceSystem extends ChatMessageSource
{
    @Nonnull
    @Override
    public ChatterID getDefaultChatter(final UUID uuid) {
        return ChatterID.getLocalChatterID(uuid);
    }
    
    @Nullable
    @Override
    public String getSourceName(@Nonnull final UserManager userManager) {
        return null;
    }
    
    @Nonnull
    @Override
    public ChatMessageSourceType getSourceType() {
        return ChatMessageSourceType.System;
    }
    
    @Nullable
    @Override
    public UUID getSourceUUID() {
        return null;
    }
}
