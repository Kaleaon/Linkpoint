package com.linkpoint.slproto.chat;

import android.content.Context;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.SLAgentCircuit;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent;
import com.linkpoint.slproto.messages.ImprovedInstantMessage;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatFriendshipOfferedEvent extends SLChatYesNoEvent {
    public final UUID sessionID;

    public SLChatFriendshipOfferedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.sessionID = chatMessage.getSessionID();
    }

    public SLChatFriendshipOfferedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null);
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipOffered;
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.friendship_request_no);
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.friendship_request_declined);
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.friendship_request_question);
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.friendship_request_yes);
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.friendship_request_accepted);
    }

    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        UUID sourceUUID = this.source.getSourceUUID();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID);
        }
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.sessionID);
    }
}
