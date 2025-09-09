package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.text.DateFormat;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SLChatSessionMarkEvent extends SLChatEvent {
    @Nullable
    private final String description;
    @Nonnull
    private final SessionMarkType sessionMarkType;

    public enum SessionMarkType {
        NewSession,
        Teleport
    }

    public SLChatSessionMarkEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.sessionMarkType = SessionMarkType.values()[chatMessage.getChatChannel().intValue()];
        this.description = chatMessage.getMessageText();
    }

    public SLChatSessionMarkEvent(ChatMessageSource chatMessageSource, @Nonnull UUID uuid, @Nonnull SessionMarkType sessionMarkType2, @Nullable String str) {
        super(chatMessageSource, uuid);
        this.sessionMarkType = sessionMarkType2;
        this.description = str;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.SessionMark;
    }

    /* access modifiers changed from: protected */
    public String getText(Context context, @Nonnull UserManager userManager) {
        if (this.sessionMarkType == SessionMarkType.Teleport) {
            return context.getString(R.string.teleport_complete_format, new Object[]{this.description});
        }
        return context.getString(R.string.new_session_mark_format, new Object[]{DateFormat.getDateTimeInstance(3, 3).format(getTimestamp())});
    }

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_SESSION_MARK;
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return true;
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.description);
        chatMessage.setChatChannel(Integer.valueOf(this.sessionMarkType.ordinal()));
    }
}
