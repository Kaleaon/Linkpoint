package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceUnknown : ChatMessageSource() {
    private const val ChatMessageSourceUnknown Instance = ChatMessageSourceUnknown()

    private ChatMessageSourceUnknown() {
    }

    @JvmStatic
     fun getInstance(): ChatMessageSourceUnknown {
        return Instance
    }

     public fun getDefaultChatter(uuid: UUID): ChatterID {
        return ChatterID.getLocalChatterID(uuid)
    }

     public fun getSourceName(userManager: UserManager): String {
        return null
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Unknown
    }

     public fun getSourceUUID(): UUID {
        return null
    }
}
