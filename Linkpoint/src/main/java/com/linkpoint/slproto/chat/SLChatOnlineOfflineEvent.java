package com.linkpoint.slproto.chat;

import android.content.Context;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

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

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return this.wentOnline ? SLChatEvent.ChatMessageType.WentOnline : SLChatEvent.ChatMessageType.WentOffline;
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(this.wentOnline ? R.string.went_online : R.string.went_offline);
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return true;
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
