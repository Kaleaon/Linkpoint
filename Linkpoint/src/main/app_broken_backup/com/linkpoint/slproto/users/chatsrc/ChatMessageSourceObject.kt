package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageSourceObject : ChatMessageSource {
    String name
    @NonNull
    UUID uuid

    ChatMessageSourceObject(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.name = chatMessage.getSenderName()
    }

    ChatMessageSourceObject(@NonNull UUID uuid2, String str) {
        this.uuid = uuid2
        this.name = str
    }

    @NonNull
    fun getDefaultChatter(UUID uuid2): ChatterID {
        return ChatterID.getLocalChatterID(uuid2)
    }

    @Nullable
    fun getSourceName(@NonNull UserManager userManager): String {
        return this.name
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Any
    }

    @Nullable
    fun getSourceUUID(): UUID {
        return this.uuid
    }

    fun serializeTo(@NonNull ChatMessage chatMessage)  {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
