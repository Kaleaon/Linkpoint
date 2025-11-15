package com.lumiyaviewer.lumiya.slproto.users.chatsrc

import com.lumiyaviewer.lumiya.GlobalOptions
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.users.ChatterID
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
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
    ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getUserChatterID(uuid2, this.uuid)
    }

    @Nullable
    String getSourceName(@NonNull UserManager userManager) {
        return GlobalOptions.getInstance().isLegacyUserNames() ? this.legacyName : this.displayName
    }

    @NonNull
    ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.User
    }

    @Nullable
    UUID getSourceUUID() {
        return this.uuid
    }

    Unit serializeTo(@NonNull ChatMessage chatMessage) {
        super.serializeTo(chatMessage)
        chatMessage.setSenderUUID(this.uuid)
        chatMessage.setSenderName(this.displayName)
        chatMessage.setSenderLegacyName(this.legacyName)
    }

    Unit setDisplayName(@Nullable String str) {
        this.displayName = str
    }

    Unit setLegacyName(@Nullable String str) {
        this.legacyName = str
    }
}
