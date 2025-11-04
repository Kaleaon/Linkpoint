// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import java.text.DateFormat;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;

public class SLChatSessionMarkEvent extends SLChatEvent
{
    @Nullable
    private final String description;
    @Nonnull
    private final SessionMarkType sessionMarkType;
    
    public SLChatSessionMarkEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.sessionMarkType = SessionMarkType.values()[chatMessage.getChatChannel()];
        this.description = chatMessage.getMessageText();
    }
    
    public SLChatSessionMarkEvent(final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, @Nonnull final SessionMarkType sessionMarkType, @Nullable final String description) {
        super(chatMessageSource, uuid);
        this.sessionMarkType = sessionMarkType;
        this.description = description;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.SessionMark;
    }
    
    @Override
    protected String getText(final Context context, @Nonnull final UserManager userManager) {
        if (this.sessionMarkType == SessionMarkType.Teleport) {
            return context.getString(2131297083, new Object[] { this.description });
        }
        return context.getString(2131296732, new Object[] { DateFormat.getDateTimeInstance(3, 3).format(this.getTimestamp()) });
    }
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_SESSION_MARK;
    }
    
    @Override
    protected boolean isActionMessage(@Nonnull final UserManager userManager) {
        return true;
    }
    
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setMessageText(this.description);
        chatMessage.setChatChannel(this.sessionMarkType.ordinal());
    }
    
    public enum SessionMarkType
    {
        NewSession("NewSession", 0), 
        Teleport("Teleport", 1);
        
        private SessionMarkType(final String name, final int ordinal) {
        }
    }
}
