package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatOnlineOfflineEvent extends SLChatEvent {
    private final boolean wentOnline;

    public SLChatOnlineOfflineEvent(ChatMessage chatMessage, @Nonnull UUID uuid, boolean z) {
        super(chatMessage, uuid);
        this.wentOnline = z;
    }

    public SLChatOnlineOfflineEvent(ChatMessageSource chatMessageSource, @Nonnull UUID uuid, boolean z) {
        super(chatMessageSource, uuid);
        this.wentOnline = z;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return this.wentOnline ? SLChatEvent.ChatMessageType.WentOnline : SLChatEvent.ChatMessageType.WentOffline;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(this.wentOnline ? R.string.went_online : R.string.went_offline);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected boolean isActionMessage(@Nonnull UserManager userManager) {
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
