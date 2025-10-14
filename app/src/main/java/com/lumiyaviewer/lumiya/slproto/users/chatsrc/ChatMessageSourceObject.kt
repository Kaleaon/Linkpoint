package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceObject : ChatMessageSource {
    String name
    @Nonnull
    UUID uuid

    ChatMessageSourceObject(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.name = chatMessage.getSenderName()
    }

    ChatMessageSourceObject(@Nonnull UUID uuid2, String str) {
        this.uuid = uuid2
        this.name = str
    }

    @Nonnull
    ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getLocalChatterID(uuid2)
    }

    @Nullable
    String getSourceName(@Nonnull UserManager userManager) {
        return this.name
    }

    @Nonnull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Any
    }

    @Nullable
    UUID getSourceUUID() {
        return this.uuid
    }

    Unit serializeTo(@Nonnull ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
