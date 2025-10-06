package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceGroup : ChatMessageSource() {
    val String name
    val UUID uuid

    ChatMessageSourceGroup(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.name = chatMessage.getSenderName()
    }

    public ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getGroupChatterID(uuid2, this.uuid)
    }

    public String getSourceName(UserManager userManager) {
        return this.name
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Group
    }

    public UUID getSourceUUID() {
        return this.uuid
    }

    fun serializeTo(ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
