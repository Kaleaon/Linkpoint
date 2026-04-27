// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.avatar.SLScriptPermissions;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLChatPermissionRequestEvent extends SLChatYesNoEvent
{
    private final UUID ItemID;
    private final String ObjectOwner;
    private final int Questions;
    
    public SLChatPermissionRequestEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.ItemID = chatMessage.getItemID();
        this.ObjectOwner = chatMessage.getItemName();
        this.Questions = chatMessage.getQuestionMask();
    }
    
    public SLChatPermissionRequestEvent(final ScriptQuestion scriptQuestion, @Nonnull final UUID uuid) {
        int questions = 0;
        super(new ChatMessageSourceObject(scriptQuestion.Data_Field.TaskID, SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectName)), uuid, (String)null);
        this.ObjectOwner = SLMessage.stringFromVariableOEM(scriptQuestion.Data_Field.ObjectOwner);
        this.ItemID = scriptQuestion.Data_Field.ItemID;
        final SLScriptPermissions[] values = SLScriptPermissions.values();
        int n;
        for (int length = values.length, i = 0; i < length; ++i, questions = n) {
            final SLScriptPermissions slScriptPermissions = values[i];
            n = questions;
            if ((scriptQuestion.Data_Field.Questions & slScriptPermissions.getPermMask()) != 0x0) {
                n = (questions | slScriptPermissions.getPermMask());
            }
        }
        this.Questions = questions;
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.PermissionRequest;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296879);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296877);
    }
    
    public String getQuestion(final Context context) {
        return context.getString(2131296880);
    }
    
    public int getQuestions() {
        return this.Questions;
    }
    
    @Override
    public String getText(final Context context, @Nonnull final UserManager userManager) {
        String str = "";
        final SLScriptPermissions[] values = SLScriptPermissions.values();
        String string;
        for (int length = values.length, i = 0; i < length; ++i, str = string) {
            final SLScriptPermissions slScriptPermissions = values[i];
            string = str;
            if ((this.Questions & slScriptPermissions.getPermMask()) != 0x0) {
                String string2 = str;
                if (!str.equals("")) {
                    string2 = str + ", ";
                }
                string = string2 + slScriptPermissions.getMessage();
            }
        }
        return context.getString(2131296878, new Object[] { this.ObjectOwner, str });
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296881);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131296876);
    }
    
    @Override
    public boolean isObjectPopup() {
        return true;
    }
    
    @Override
    protected void onNoAction(final Context context, final UserManager userManager) {
        super.onNoAction(context, userManager);
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            activeAgentCircuit.getModules().avatarControl.ScriptAnswerYes(this.ItemID, this.source.getSourceUUID(), this.Questions);
        }
        userManager.getObjectPopupsManager().cancelObjectPopup(this);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setItemID(this.ItemID);
        chatMessage.setItemName(this.ObjectOwner);
        chatMessage.setQuestionMask(this.Questions);
    }
}
