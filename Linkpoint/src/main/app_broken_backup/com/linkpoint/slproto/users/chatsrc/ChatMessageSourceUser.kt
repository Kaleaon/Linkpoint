package com.linkpoint.slproto.users.chatsrc

import com.linkpoint.GlobalOptions
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class ChatMessageSourceUser : ChatMessageSource {
    @Nullable
    private String displayName
    @Nullable
    private String legacyName
    @NonNull
    UUID uuid

    ChatMessageSourceUser(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID()
        this.displayName = chatMessage.getSenderName()
        this.legacyName = chatMessage.getSenderLegacyName()
    }

    ChatMessageSourceUser(@NonNull UUID uuid2) {
        this.uuid = uuid2
        this.displayName = null
        this.legacyName = null
    }

    @NonNull
    fun getDefaultChatter(UUID uuid2): ChatterID {
        return ChatterID.getUserChatterID(uuid2, this.uuid)
    }

    @Nullable
    fun getSourceName(@NonNull UserManager userManager): String {
        return GlobalOptions.getInstance().isLegacyUserNames() ? this.legacyName : this.displayName
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.User
    }

    @Nullable
    fun getSourceUUID(): UUID {
        return this.uuid
    }

    fun serializeTo(@NonNull ChatMessage chatMessage)  {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.displayName)
        chatMessage.setSenderLegacyName(this.legacyName)
    }

    fun setDisplayName(@Nullable String str)  {
        this.displayName = str
    }

    fun setLegacyName(@Nullable String str)  {
        this.legacyName = str
    }
}
