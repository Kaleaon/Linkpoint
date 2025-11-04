package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatFriendshipOfferedEvent extends SLChatYesNoEvent {
    public final UUID sessionID;

    public SLChatFriendshipOfferedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.sessionID = chatMessage.getSessionID();
    }

    public SLChatFriendshipOfferedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipOffered;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoButton(Context context) {
        return context.getString(R.string.friendship_request_no);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoMessage(Context context) {
        return context.getString(R.string.friendship_request_declined);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getQuestion(Context context) {
        return context.getString(R.string.friendship_request_question);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesButton(Context context) {
        return context.getString(R.string.friendship_request_yes);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesMessage(Context context) {
        return context.getString(R.string.friendship_request_accepted);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        UUID sourceUUID = this.source.getSourceUUID();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID == null || activeAgentCircuit == null) {
            return;
        }
        activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.sessionID);
    }
}
