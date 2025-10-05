package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.GlobalOptions
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class ChatMessageSourceUser : ChatMessageSource() {
    private String displayName
    private String legacyName
    val UUID uuid

    ChatMessageSourceUser(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.displayName = chatMessage.getSenderName()
        this.legacyName = chatMessage.getSenderLegacyName()
    }

    public ChatMessageSourceUser(UUID uuid2) {
        this.uuid = uuid2
        this.displayName = null
        this.legacyName = null
    }

    public ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getUserChatterID(uuid2, this.uuid)
    }

    public String getSourceName(UserManager userManager) {
        return GlobalOptions.getInstance().isLegacyUserNames() ? this.legacyName : this.displayName
    }

    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.User
    }

    public UUID getSourceUUID() {
        return this.uuid
    }

    fun serializeTo(ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.displayName)
        chatMessage.setSenderLegacyName(this.legacyName)
    }

    fun setDisplayName(String str) {
        this.displayName = str
    }

    fun setLegacyName(String str) {
        this.legacyName = str
    }
}
