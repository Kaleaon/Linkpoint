package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
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

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipResult;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    protected String getText(Context context, @Nonnull UserManager userManager) {
        return context.getString(this.accepted ? R.string.friendship_accepted : R.string.friendship_declined);
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
        chatMessage.setAccepted(Boolean.valueOf(this.accepted));
    }
}
