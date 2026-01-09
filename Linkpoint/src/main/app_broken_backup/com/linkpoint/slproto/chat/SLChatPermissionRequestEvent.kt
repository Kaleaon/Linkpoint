package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.avatar.SLScriptPermissions
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ScriptQuestion
import com.linkpoint.slproto.users.chatsrc.ChatMessageSourceObject
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatPermissionRequestEvent : SLChatYesNoEvent {
    private UUID ItemID
    private String ObjectOwner
    private Int Questions

    SLChatPermissionRequestEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.ItemID = chatMessage.getItemID()
        this.ObjectOwner = chatMessage.getItemName()
        this.Questions = chatMessage.getQuestionMask().intValue()
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLChatPermissionRequestEvent(ScriptQuestion scriptQuestion, @NonNull UUID uuid) {
        super(ChatMessageSourceObject(scriptQuestion.Data_Field.TaskID, SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectName)), uuid, (String) null)
        var i: Int = 0
        this.ObjectOwner = SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectOwner)
        this.ItemID = scriptQuestion.Data_Field.ItemID
        for (SLScriptPermissions sLScriptPermissions : SLScriptPermissions.values()) {
            if ((scriptQuestion.Data_Field.Questions & sLScriptPermissions.getPermMask()) != 0) {
                i |= sLScriptPermissions.getPermMask()
            }
        }
        this.Questions = i
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.PermissionRequest
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.permission_request_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.permission_request_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.permission_request_question)
    }

    fun getQuestions(): Int {
        return this.Questions
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        var str: String = ""
        for (SLScriptPermissions sLScriptPermissions : SLScriptPermissions.values()) {
            if ((this.Questions & sLScriptPermissions.getPermMask()) != 0) {
                if (!str.equals("")) {
                    str = str + ", "
                }
                str = str + sLScriptPermissions.getMessage()
            }
        }
        return context.getString(R.string.permission_request_format, Array<Any>{this.ObjectOwner, str})
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.permission_request_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.permission_request_accepted)
    }

    fun isObjectPopup(): Boolean {
        return true
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager)  {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun onYesAction(Context context, UserManager userManager)  {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().avatarControl.ScriptAnswerYes(this.ItemID, this.source.getSourceUUID(), this.Questions)
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemID(this.ItemID)
        chatMessage.setItemName(this.ObjectOwner)
        chatMessage.setQuestionMask(Integer.valueOf(this.Questions))
    }
}
