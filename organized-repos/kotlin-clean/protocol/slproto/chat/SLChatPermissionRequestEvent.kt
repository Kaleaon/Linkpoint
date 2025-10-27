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
import javax.annotation.Nonnull

val class SLChatPermissionRequestEvent : SLChatYesNoEvent() {
    private val UUID ItemID
    private val String ObjectOwner
    private val Int Questions

    public SLChatPermissionRequestEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.ItemID = chatMessage.getItemID()
        this.ObjectOwner = chatMessage.getItemName()
        this.Questions = chatMessage.getQuestionMask().intValue()
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public SLChatPermissionRequestEvent(ScriptQuestion scriptQuestion, UUID uuid) {
        super(ChatMessageSourceObject(scriptQuestion.Data_Field.TaskID, SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectName)), uuid, (String) null)
        Int i = 0
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
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.PermissionRequest
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.permission_request_no)
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.permission_request_declined)
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.permission_request_question)
    }

    public Int getQuestions() {
        return this.Questions
    }

    public String getText(Context context, UserManager userManager) {
        String str = ""
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

    public String getYesButton(Context context) {
        return context.getString(R.string.permission_request_yes)
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.permission_request_accepted)
    }

    public Boolean isObjectPopup() {
        return true
    }

    /* access modifiers changed from: protected */
    fun onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().avatarControl.ScriptAnswerYes(this.ItemID, this.source.getSourceUUID(), this.Questions)
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    fun serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemID(this.ItemID)
        chatMessage.setItemName(this.ObjectOwner)
        chatMessage.setQuestionMask(Integer.valueOf(this.Questions))
    }
}
