package com.linkpoint.slproto.chat;

import android.content.Context;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.messages.ImprovedInstantMessage;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatFriendshipResultEvent extends SLChatEvent {
    private final boolean accepted;

    public SLChatFriendshipResultEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.accepted = chatMessage.getAccepted().booleanValue();
    }

    public SLChatFriendshipResultEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(improvedInstantMessage, uuid, chatMessageSource);
        this.accepted = improvedInstantMessage.MessageBlock_Field.Dialog == 39;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipResult;
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(this.accepted ? R.string.friendship_accepted : R.string.friendship_declined);
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
        chatMessage.setAccepted(Boolean.valueOf(this.accepted));
    }
}
