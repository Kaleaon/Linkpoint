package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatFriendshipOfferedEvent : SLChatYesNoEvent() {
    val UUID sessionID

    public SLChatFriendshipOfferedEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.sessionID = chatMessage.getSessionID()
    }

    public SLChatFriendshipOfferedEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipOffered
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.friendship_request_no)
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.friendship_request_declined)
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.friendship_request_question)
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.friendship_request_yes)
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.friendship_request_accepted)
    }

    fun onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID)
        }
    }

    fun serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.sessionID)
    }
}
