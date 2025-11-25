package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.avatar.SLScriptPermissions
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatPermissionRequestEvent : SLChatYesNoEvent {
    private val itemID: UUID?
    private val objectOwner: String
    private val questions: Int

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.itemID = chatMessage.itemID
        this.objectOwner = chatMessage.itemName ?: ""
        this.questions = chatMessage.questionMask ?: 0
    }

    constructor(scriptQuestion: ScriptQuestion, uuid: UUID) : 
        super(
            ChatMessageSourceObject(
                scriptQuestion.Data_Field.TaskID, 
                SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectName)
            ), 
            uuid, 
            null as String?
        ) {
        
        this.objectOwner = SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectOwner)
        this.itemID = scriptQuestion.Data_Field.ItemID
        
        var q = 0
        for (perm in SLScriptPermissions.values()) {
            if ((scriptQuestion.Data_Field.Questions and perm.permMask) != 0) {
                q = q or perm.permMask
            }
        }
        this.questions = q
    }

    override fun getMessageType(): ChatMessageType {
        return ChatMessageType.PermissionRequest
    }

    override fun getNoButton(context: Context): String {
        return context.getString(R.string.permission_request_no)
    }

    override fun getNoMessage(context: Context): String {
        return context.getString(R.string.permission_request_declined)
    }

    override fun getQuestion(context: Context): String {
        return context.getString(R.string.permission_request_question)
    }

    fun getQuestions(): Int {
        return questions
    }

    // Assuming getMessage is used instead of getText
    override fun getMessage(): CharSequence {
        // We can't easily format here without context. 
        // Returning a generic message.
        return "Script requesting permissions"
    }
    
    // Or if context is available via some means in call site:
    fun getFormattedText(context: Context): String {
        var str = ""
        for (perm in SLScriptPermissions.values()) {
            if ((questions and perm.permMask) != 0) {
                if (str.isNotEmpty()) {
                    str += ", "
                }
                str += perm.message
            }
        }
        return context.getString(R.string.permission_request_format, objectOwner, str)
    }

    override fun getYesButton(context: Context): String {
        return context.getString(R.string.permission_request_yes)
    }

    override fun getYesMessage(context: Context): String {
        return context.getString(R.string.permission_request_accepted)
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    override fun onNoAction(context: Context, userManager: UserManager) {
        super.onNoAction(context, userManager)
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val activeAgentCircuit = userManager.activeAgentCircuit
        if (activeAgentCircuit != null && itemID != null) {
            activeAgentCircuit.modules.avatarControl.ScriptAnswerYes(itemID, source.sourceUUID, questions)
        }
        userManager.objectPopupsManager.cancelObjectPopup(this)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.itemID = itemID
        chatMessage.itemName = objectOwner
        chatMessage.questionMask = questions
    }
}
