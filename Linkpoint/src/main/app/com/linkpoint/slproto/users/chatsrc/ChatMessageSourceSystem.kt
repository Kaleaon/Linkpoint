package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageSourceSystem : ChatMessageSource {
    ChatMessageSourceSystem() {
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
        return ChatMessageSource.ChatMessageSourceType.System
    }

    @Nullable
    UUID getSourceUUID() {
        return null
    }
}
