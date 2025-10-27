package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.avatar.SLScriptPermissions
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

class SLChatPermissionRequestEvent : SLChatYesNoEvent {
    private UUID ItemID
    private String ObjectOwner
    private Int Questions

    SLChatPermissionRequestEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
        this.ItemID = chatMessage.getItemID()
        this.ObjectOwner = chatMessage.getItemName()
        this.Questions = chatMessage.getQuestionMask().intValue()
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    SLChatPermissionRequestEvent(ScriptQuestion scriptQuestion, @Nonnull UUID uuid) {
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
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.PermissionRequest
    }

    String getNoButton(Context context) {
        return context.getString(R.string.permission_request_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.permission_request_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.permission_request_question)
    }

    Int getQuestions() {
        return this.Questions
    }

    String getText(Context context, @Nonnull UserManager userManager) {
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

    String getYesButton(Context context) {
        return context.getString(R.string.permission_request_yes)
    }

    String getYesMessage(Context context) {
        return context.getString(R.string.permission_request_accepted)
    }

    Boolean isObjectPopup() {
        return true
    }

    /* access modifiers changed from: protected */
    Unit onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager)
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().avatarControl.ScriptAnswerYes(this.ItemID, this.source.getSourceUUID(), this.Questions)
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this)
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setItemID(this.ItemID)
        chatMessage.setItemName(this.ObjectOwner)
        chatMessage.setQuestionMask(Integer.valueOf(this.Questions))
    }
}
