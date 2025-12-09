package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageSourceGroup : ChatMessageSource {
    @NonNull
    String name
    @NonNull
    UUID uuid

    ChatMessageSourceGroup(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.name = chatMessage.getSenderName()
    }

    @NonNull
    fun getDefaultChatter(UUID uuid2): ChatterID {
        return ChatterID.getGroupChatterID(uuid2, this.uuid)
    }

    @Nullable
    fun getSourceName(@NonNull UserManager userManager): String {
        return this.name
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Group
    }

    @Nullable
    fun getSourceUUID(): UUID {
        return this.uuid
    }

    fun serializeTo(@NonNull ChatMessage chatMessage): Unit {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.name)
    }
}
