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

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.friendship_request_no)
    }

     public fun getNoMessage(context: Context): String {
        return context.getString(R.string.friendship_request_declined)
    }

     public fun getQuestion(context: Context): String {
        return context.getString(R.string.friendship_request_question)
    }

     public fun getYesButton(context: Context): String {
        return context.getString(R.string.friendship_request_yes)
    }

     public fun getYesMessage(context: Context): String {
        return context.getString(R.string.friendship_request_accepted)
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val sourceUUID: UUID = this.source.getSourceUUID()
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID)
        }
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.sessionID)
    }
}
