package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageSourceUnknown : ChatMessageSource {
    private ChatMessageSourceUnknown Instance = ChatMessageSourceUnknown()

    private ChatMessageSourceUnknown() {
    }

    ChatMessageSourceUnknown getInstance() {
        return Instance
    }

    @NonNull
    ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getLocalChatterID(uuid)
    }

    @Nullable
    String getSourceName(@NonNull UserManager userManager) {
        return null
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Unknown
    }

    @Nullable
    UUID getSourceUUID() {
        return null
    }
}
