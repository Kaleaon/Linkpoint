package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import java.util.UUID

class SLChatLureRequestedEvent : SLChatYesNoEvent {
    
    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid)

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : 
        super(source, uuid, message, "Teleport requested")

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.LureRequested
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.lure_req_decline)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.lure_req_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.lure_req_received)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.lure_req_accept)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.lure_req_accepted)
    }
}
