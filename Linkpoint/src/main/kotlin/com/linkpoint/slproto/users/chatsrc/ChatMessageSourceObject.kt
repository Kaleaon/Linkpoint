package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceObject : ChatMessageSource() {
    val String name
    val UUID uuid

    ChatMessageSourceObject(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.name = chatMessage.getSenderName()
    }

    public ChatMessageSourceObject(UUID uuid2, String str) {
        this.uuid = uuid2
        this.name = str
    }

    public ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getLocalChatterID(uuid2)
    }

    public String getSourceName(UserManager userManager) {
        return this.name
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Object
    }

    public UUID getSourceUUID() {
        return this.uuid
    }

    public Unit serializeTo(ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
