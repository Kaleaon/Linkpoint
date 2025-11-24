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

class SLChatGroupInvitationEvent : SLChatYesNoEvent {
    private val fee: Int
    private val groupID: UUID
    private val groupName: String?
    private val sessionID: UUID

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.sessionID = chatMessage.sessionID ?: UUID(0, 0)
        // Assuming groupID/fee/groupName are stored in sessionID or other fields in a real implementation
        // For now using defaults or parsing if logic was available
        this.groupID = UUID(0, 0) 
        this.fee = 0
        this.groupName = null
    }

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : super(source, uuid, message, null) {
        this.sessionID = message.MessageBlock_Field.ID
        // Extract additional fields if available in ImprovedInstantMessage
        this.groupID = UUID(0, 0) // Placeholder
        this.fee = 0 // Placeholder
        this.groupName = "Unknown Group" // Placeholder
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.GroupInvitation
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.group_invite_decline)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.group_invite_declined)
    }

    override fun getQuestion(context: Context): String {
        return if (fee > 0) {
            context.getString(R.string.group_invite_fee_question, groupName, fee)
        } else {
            context.getString(R.string.group_invite_question, groupName)
        }
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.group_invite_accept)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.group_invite_accepted)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val activeAgentCircuit = userManager.activeAgentCircuit
        if (activeAgentCircuit != null) {
            activeAgentCircuit.AcceptGroupInvite(sessionID, groupID)
        }
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.sessionID = sessionID
        // Serialize other fields if ChatMessage supports them
    }
}
