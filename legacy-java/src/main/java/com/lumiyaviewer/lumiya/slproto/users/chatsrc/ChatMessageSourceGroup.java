package com.lumiyaviewer.lumiya.slproto.users.chatsrc;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ChatMessageSourceGroup extends ChatMessageSource {

    @Nonnull
    public final String name;

    @Nonnull
    public final UUID uuid;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ChatMessageSourceGroup(ChatMessage chatMessage) {
        this.uuid = chatMessage.getSenderUUID();
        this.name = chatMessage.getSenderName();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nonnull
    public ChatterID getDefaultChatter(UUID uuid) {
        return ChatterID.getGroupChatterID(uuid, this.uuid);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nullable
    public String getSourceName(@Nonnull UserManager userManager) {
        return this.name;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nonnull
    public ChatMessageSource.ChatMessageSourceType getSourceType() {
        return ChatMessageSource.ChatMessageSourceType.Group;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    @Nullable
    public UUID getSourceUUID() {
        return this.uuid;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
    public void serializeTo(@Nonnull ChatMessage chatMessage) {
        super.serializeTo(chatMessage);
        chatMessage.setSenderUUID(this.uuid);
        chatMessage.setSenderName(this.name);
    }
}
