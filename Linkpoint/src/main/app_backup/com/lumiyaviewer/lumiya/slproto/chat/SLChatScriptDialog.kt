package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.util.UUID

class SLChatScriptDialog : SLChatDialogEvent {
    private var buttons: Array<String> = emptyArray()
    private var selectedOption: String? = null

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.selectedOption = chatMessage.dialogSelectedOption
        val buttonBytes = chatMessage.dialogButtons
        if (buttonBytes != null) {
            try {
                val ois = ObjectInputStream(ByteArrayInputStream(buttonBytes))
                @Suppress("UNCHECKED_CAST")
                this.buttons = ois.readObject() as Array<String>
            } catch (e: Exception) {
                // ignore
            }
        }
    }

    constructor(scriptDialog: ScriptDialog, uuid: UUID, buttons: Array<String>) : 
        super(scriptDialog, uuid) {
        this.buttons = buttons
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.ScriptDialog
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_DIALOG
    }

    fun getButtons(): Array<String> {
        return buttons
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    fun onDialogButton(userManager: UserManager, buttonIndex: Int) {
        if (buttonIndex in buttons.indices) {
            this.selectedOption = buttons[buttonIndex]
            val sourceUUID = source.sourceUUID
            val activeAgentCircuit = userManager.activeAgentCircuit
            if (activeAgentCircuit != null) {
                activeAgentCircuit.SendScriptDialogReply(sourceUUID, chatChannel, buttonIndex, selectedOption)
            }
            userManager.objectPopupsManager.cancelObjectPopup(this)
        }
    }

    override fun onDialogIgnored(userManager: UserManager) {
        super.onDialogIgnored(userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        try {
            val bos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(bos)
            oos.writeObject(buttons)
            chatMessage.dialogButtons = bos.toByteArray()
        } catch (e: Exception) {
            // ignore
        }
        chatMessage.dialogSelectedOption = selectedOption
    }

    override fun showDialog(context: Context, userManager: UserManager) {
        // Stub
    }
    
    override fun getMessage(): CharSequence {
        return "Script Dialog: " + (selectedOption ?: "Pending...")
    }
    
    override fun getSenderDisplayName(): CharSequence? {
        return "Script"
    }
    
    override fun getChatterID(): com.lumiyaviewer.lumiya.slproto.users.ChatterID {
        return com.lumiyaviewer.lumiya.slproto.users.ChatterID(agentUUID, "")
    }
}
