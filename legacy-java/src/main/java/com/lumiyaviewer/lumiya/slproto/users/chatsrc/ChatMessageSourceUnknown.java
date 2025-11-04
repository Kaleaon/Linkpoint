package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class ChatMessageSourceUnknown extends ChatMessageSource {
    private static final ChatMessageSourceUnknown Instance = new ChatMessageSourceUnknown();

    private ChatMessageSourceUnknown() {
    }

    public static ChatMessageSourceUnknown getInstance() {
        return Instance;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nonnull
    public ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getLocalChatterID(uuid);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nullable
    public String getSourceName(@Nonnull UserManager userManager) {
        return null;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nonnull
    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Unknown;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nullable
    public UUID getSourceUUID() {
        return null;
    }
}
