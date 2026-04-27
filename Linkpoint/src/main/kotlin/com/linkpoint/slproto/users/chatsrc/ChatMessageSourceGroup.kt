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

     public fun getDefaultChatter(uuid2: UUID): ChatterID {
        return ChatterID.getGroupChatterID(uuid2, this.uuid)
    }

     public fun getSourceName(userManager: UserManager): String {
        return this.name
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Group
    }

     public fun getSourceUUID(): UUID {
        return this.uuid
    }

    fun serializeTo(chatMessage: ChatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
