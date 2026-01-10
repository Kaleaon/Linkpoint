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
import androidx.annotation.NonNull

class SLChatFriendshipOfferedEvent : SLChatYesNoEvent {
    UUID sessionID

    SLChatFriendshipOfferedEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.sessionID = chatMessage.getSessionID()
    }

    SLChatFriendshipOfferedEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipOffered
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.friendship_request_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.friendship_request_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.friendship_request_question)
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.friendship_request_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.friendship_request_accepted)
    }

    fun onYesAction(Context context, UserManager userManager)  {
        super.onYesAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID)
        }
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.sessionID)
    }
}
