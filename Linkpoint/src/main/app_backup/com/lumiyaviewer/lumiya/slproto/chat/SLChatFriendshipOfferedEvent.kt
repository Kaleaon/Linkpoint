package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatFriendshipOfferedEvent : SLChatYesNoEvent {
    private val sessionID: UUID

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.sessionID = chatMessage.sessionID ?: UUID(0, 0) // Assuming sessionID can be null in ChatMessage
    }

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : super(source, uuid, message, null) {
        this.sessionID = message.MessageBlock_Field.ID
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.FriendshipOffered
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.friendship_request_no)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.friendship_request_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.friendship_request_question)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.friendship_request_yes)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.friendship_request_accepted)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val sourceUUID = source.getSourceUUID()
        val activeAgentCircuit = userManager.activeAgentCircuit
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, sessionID)
        }
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.sessionID = sessionID
    }
}
