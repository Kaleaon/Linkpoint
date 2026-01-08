package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
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
    ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getLocalChatterID(uuid2)
    }

    @Nullable
    String getSourceName(@NonNull UserManager userManager) {
        return this.name
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Any
    }

    @Nullable
    UUID getSourceUUID() {
        return this.uuid
    }

    Unit serializeTo(@NonNull ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
