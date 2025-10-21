package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceSystem : ChatMessageSource() {
    ChatMessageSourceSystem() {
    }

    public ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getLocalChatterID(uuid)
    }

    public String getSourceName(UserManager userManager) {
        return null
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.System
    }

    public UUID getSourceUUID() {
        return null
    }
}
