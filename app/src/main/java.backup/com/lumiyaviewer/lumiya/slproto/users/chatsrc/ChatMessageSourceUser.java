package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatMessageSourceUser extends ChatMessageSource {
    @Nullable
    private String displayName;
    @Nullable
    private String legacyName;
    @Nonnull
    public final UUID uuid;

    ChatMessageSourceUser(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.displayName = chatMessage.getSenderName();
        this.legacyName = chatMessage.getSenderLegacyName();
    }

    public ChatMessageSourceUser(@Nonnull UUID uuid2) {
        this.uuid = uuid2;
        this.displayName = null;
        this.legacyName = null;
    }

    @Nonnull
    public ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getUserChatterID(uuid2, this.uuid);
    }

    @Nullable
    public String getSourceName(@Nonnull UserManager userManager) {
        return GlobalOptions.getInstance().isLegacyUserNames() ? this.legacyName : this.displayName;
    }

    @Nonnull
    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.User;
    }

    @Nullable
    public UUID getSourceUUID() {
        return this.uuid;
    }

    public void serializeTo(@Nonnull ChatMessage chatMessage) {
        super.serializeTo(chatMessage);
        chatMessage.setSenderUUID(this.uuid);
        chatMessage.setSenderName(this.displayName);
        chatMessage.setSenderLegacyName(this.legacyName);
    }

    public void setDisplayName(@Nullable String str) {
        this.displayName = str;
    }

    public void setLegacyName(@Nullable String str) {
        this.legacyName = str;
    }
}
