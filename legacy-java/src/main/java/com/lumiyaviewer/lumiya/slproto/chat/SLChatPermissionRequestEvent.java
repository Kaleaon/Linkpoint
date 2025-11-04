package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.avatar.SLScriptPermissions;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatPermissionRequestEvent extends SLChatYesNoEvent {
    private final UUID ItemID;
    private final String ObjectOwner;
    private final int Questions;

    public SLChatPermissionRequestEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.ItemID = chatMessage.getItemID();
        this.ObjectOwner = chatMessage.getItemName();
        this.Questions = chatMessage.getQuestionMask().intValue();
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public SLChatPermissionRequestEvent(ScriptQuestion scriptQuestion, @Nonnull UUID uuid) {
        super(new ChatMessageSourceObject(scriptQuestion.Data_Field.TaskID, SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectName)), uuid, null);
        int i = 0;
        this.ObjectOwner = SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectOwner);
        this.ItemID = scriptQuestion.Data_Field.ItemID;
        for (SLScriptPermissions sLScriptPermissions : SLScriptPermissions.valuesCustom()) {
            if ((scriptQuestion.Data_Field.Questions & sLScriptPermissions.getPermMask()) != 0) {
                i |= sLScriptPermissions.getPermMask();
            }
        }
        this.Questions = i;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.PermissionRequest;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoButton(Context context) {
        return context.getString(R.string.permission_request_no);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoMessage(Context context) {
        return context.getString(R.string.permission_request_declined);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getQuestion(Context context) {
        return context.getString(R.string.permission_request_question);
    }

    public int getQuestions() {
        return this.Questions;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public String getText(Context context, @Nonnull UserManager userManager) {
        String str = "";
        for (SLScriptPermissions sLScriptPermissions : SLScriptPermissions.valuesCustom()) {
            if ((this.Questions & sLScriptPermissions.getPermMask()) != 0) {
                if (!str.equals("")) {
                    str = str + ", ";
                }
                str = str + sLScriptPermissions.getMessage();
            }
        }
        return context.getString(R.string.permission_request_format, this.ObjectOwner, str);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesButton(Context context) {
        return context.getString(R.string.permission_request_yes);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesMessage(Context context) {
        return context.getString(R.string.permission_request_accepted);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public boolean isObjectPopup() {
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    protected void onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().avatarControl.ScriptAnswerYes(this.ItemID, this.source.getSourceUUID(), this.Questions);
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setItemID(this.ItemID);
        chatMessage.setItemName(this.ObjectOwner);
        chatMessage.setQuestionMask(Integer.valueOf(this.Questions));
    }
}
