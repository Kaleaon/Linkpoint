package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import java.util.UUID

class SLChatLureRequestEvent : SLChatYesNoEvent {
    
    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid)

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage) : 
        super(source, uuid, message, "Requesting teleport...")

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.LureRequest
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.lure_req_cancel)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.lure_req_cancelled)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.lure_req_question)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.lure_req_send)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.lure_req_sent)
    }
}
