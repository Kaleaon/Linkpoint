// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLChatFriendshipOfferedEvent extends SLChatYesNoEvent
{
    public final UUID sessionID;
    
    public SLChatFriendshipOfferedEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.sessionID = chatMessage.getSessionID();
    }
    
    public SLChatFriendshipOfferedEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.FriendshipOffered;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296554);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296553);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131296555);
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296556);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131296552);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final UUID sourceUUID = this.source.getSourceUUID();
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID);
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.sessionID);
    }
}
