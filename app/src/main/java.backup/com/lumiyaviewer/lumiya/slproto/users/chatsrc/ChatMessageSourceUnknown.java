package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatMessageSourceUnknown extends ChatMessageSource {
    private static final ChatMessageSourceUnknown Instance = new ChatMessageSourceUnknown();

    private ChatMessageSourceUnknown() {
    }

    public static ChatMessageSourceUnknown getInstance() {
        return Instance;
    }

    @Nonnull
    public ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getLocalChatterID(uuid);
    }

    @Nullable
    public String getSourceName(@Nonnull UserManager userManager) {
        return null;
    }

    @Nonnull
    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Unknown;
    }

    @Nullable
    public UUID getSourceUUID() {
        return null;
    }
}
