package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.messages.LoadURL
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

open class SLChatTextEvent : SLChatEvent {
    protected var text: String? = null

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.text = chatMessage.messageText
    }

    constructor(
        chatMessageSource: ChatMessageSource,
        uuid: UUID,
        improvedInstantMessage: ImprovedInstantMessage?,
        str: String?
    ) : super(chatMessageSource, uuid) {
        if (str != null) {
            this.text = str
        } else if (improvedInstantMessage != null) {
            this.text = SLMessage.stringFromVariableUTF(improvedInstantMessage.MessageBlock_Field.Message)
        } else {
            this.text = null
        }
    }

    constructor(chatMessageSource: ChatMessageSource, uuid: UUID, loadURL: LoadURL) : super(chatMessageSource, uuid) {
        this.text = SLMessage.stringFromVariableUTF(loadURL.Data_Field.Message) + ": " + SLMessage.stringFromVariableUTF(loadURL.Data_Field.URL)
    }

    constructor(chatMessageSource: ChatMessageSource, uuid: UUID, str: String) : super(chatMessageSource, uuid) {
        this.text = str
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.Text
    }

    open fun getRawText(): String? {
        return if (text == null || text?.startsWith("/me ") == false) text else text?.substring(4)
    }
    
    // Helper for compatibility if needed, though getMessage() is the standard
    open fun getText(context: Context, userManager: UserManager): String? {
         return getRawText()
    }

    override fun getMessage(): CharSequence {
        return getRawText() ?: ""
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    open fun isActionMessage(userManager: UserManager): Boolean {
        return text != null && text!!.startsWith("/me ")
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.messageText = this.text
    }
    
    override fun getSenderDisplayName(): CharSequence? {
        return source.name
    }
    
    override fun getChatterID(): com.lumiyaviewer.lumiya.slproto.users.ChatterID {
        return com.lumiyaviewer.lumiya.slproto.users.ChatterID(agentUUID, "")
    }
}
