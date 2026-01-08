package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatTextBoxDialog : SLChatDialogEvent {
    private var ownerName: String = ""
    private var objectName: String = ""

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.ownerName = chatMessage.ownerName ?: ""
        this.objectName = chatMessage.itemName ?: ""
    }

    // Stub constructor for manual creation if needed
    constructor(uuid: UUID) : super(ChatMessage(), uuid)

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.TextBoxDialog
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_DIALOG
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    override fun onDialogIgnored(userManager: UserManager) {
        super.onDialogIgnored(userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    fun onDialogReply(userManager: UserManager, text: String) {
        val sourceUUID = source.sourceUUID
        val activeAgentCircuit = userManager.activeAgentCircuit
        if (activeAgentCircuit != null) {
            activeAgentCircuit.SendChatFromViewer(text, chatChannel)
        }
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.ownerName = ownerName
        chatMessage.itemName = objectName
    }

    override fun showDialog(context: Context, userManager: UserManager) {
        // Stub
    }
    
    override fun getMessage(): CharSequence {
        return "Text Box Dialog"
    }
    
    override fun getSenderDisplayName(): CharSequence? {
        return objectName.ifEmpty { "Object" }
    }
    
    override fun getChatterID(): com.lumiyaviewer.lumiya.slproto.users.ChatterID {
        return com.lumiyaviewer.lumiya.slproto.users.ChatterID(agentUUID, "")
    }
}
