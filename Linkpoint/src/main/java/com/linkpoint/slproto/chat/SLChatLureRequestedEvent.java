package com.linkpoint.slproto.chat;

import android.content.Context;
import com.google.common.base.Strings;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.linkpoint.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatLureRequestedEvent extends SLChatEvent {
    private final String message;

    public SLChatLureRequestedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.message = chatMessage.getMessageText();
    }

    public SLChatLureRequestedEvent(String str, @Nonnull UUID uuid) {
        super((ChatMessageSource) ChatMessageSourceUnknown.getInstance(), uuid);
        this.message = str;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequested;
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, @Nonnull UserManager userManager) {
        if (Strings.isNullOrEmpty(this.message)) {
            return context.getString(R.string.chat_teleport_requested_no_message);
        }
        return context.getString(R.string.chat_teleport_requested_message, new Object[]{this.message});
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL;
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return false;
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.message);
    }
}
