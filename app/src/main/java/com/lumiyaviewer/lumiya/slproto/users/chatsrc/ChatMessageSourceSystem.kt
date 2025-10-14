package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceSystem : ChatMessageSource {
    ChatMessageSourceSystem() {
    }

    @Nonnull
    ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getLocalChatterID(uuid)
    }

    @Nullable
    String getSourceName(@Nonnull UserManager userManager) {
        return null
    }

    @Nonnull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.System
    }

    @Nullable
    UUID getSourceUUID() {
        return null
    }
}
