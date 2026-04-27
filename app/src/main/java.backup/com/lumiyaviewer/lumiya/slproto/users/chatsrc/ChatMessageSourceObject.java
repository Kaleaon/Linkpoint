package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ChatMessageSourceObject extends ChatMessageSource {
    public final String name;
    @Nonnull
    public final UUID uuid;

    ChatMessageSourceObject(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.name = chatMessage.getSenderName();
    }

    public ChatMessageSourceObject(@Nonnull UUID uuid2, String str) {
        this.uuid = uuid2;
        this.name = str;
    }

    @Nonnull
    public ChatterID getDefaultChatter(UUID uuid2) {
        return ChatterID.getLocalChatterID(uuid2);
    }

    @Nullable
    public String getSourceName(@Nonnull UserManager userManager) {
        return this.name;
    }

    @Nonnull
    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Object;
    }

    @Nullable
    public UUID getSourceUUID() {
        return this.uuid;
    }

    public void serializeTo(@Nonnull ChatMessage chatMessage) {
        super.serializeTo(chatMessage);
        chatMessage.setSenderUUID(this.uuid);
        chatMessage.setSenderName(this.name);
    }
}
