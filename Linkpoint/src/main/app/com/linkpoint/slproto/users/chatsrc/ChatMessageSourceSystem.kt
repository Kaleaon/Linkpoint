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
    fun getDefaultChatter(UUID uuid): ChatterID {
        return ChatterID.getLocalChatterID(uuid)
    }

    @Nullable
    fun getSourceName(@NonNull UserManager userManager): String {
        return null
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.System
    }

    @Nullable
    fun getSourceUUID(): UUID {
        return null
    }
}
