// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;

public final class SLChatGroupInvitationEvent extends SLChatYesNoEvent
{
    private final UUID groupID;
    private final int joinFee;
    private final UUID sessionID;
    
    public SLChatGroupInvitationEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.joinFee = chatMessage.getTransactionAmount();
        this.sessionID = chatMessage.getSessionID();
        this.groupID = chatMessage.getItemID();
    }
    
    public SLChatGroupInvitationEvent(final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.groupID = improvedInstantMessage.AgentData_Field.AgentID;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length < 4) {
            this.joinFee = 0;
        }
        else {
            final ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
            wrap.order(ByteOrder.BIG_ENDIAN);
            this.joinFee = wrap.getInt();
        }
    }
    
    private void DoAcceptGroupInvite(final UUID uuid, final UUID uuid2, final boolean b) {
        final UserManager userManager = UserManager.getUserManager(this.agentUUID);
        if (userManager != null) {
            final SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
            if (activeAgentCircuit != null) {
                activeAgentCircuit.getModules().groupManager.AcceptGroupInvite(uuid, uuid2, b);
            }
        }
    }
    
    @Nonnull
    @Override
    protected ChatMessageType getMessageType() {
        return ChatMessageType.GroupInvitation;
    }
    
    public String getNoButton(final Context context) {
        return context.getString(2131296646);
    }
    
    public String getNoMessage(final Context context) {
        return context.getString(2131296643);
    }
    
    public String getQuestion(final Context context) {
        if (this.joinFee == 0) {
            return context.getString(2131296649);
        }
        return context.getString(2131296652, new Object[] { this.joinFee });
    }
    
    public String getYesButton(final Context context) {
        return context.getString(2131296653);
    }
    
    public String getYesMessage(final Context context) {
        return context.getString(2131296641);
    }
    
    @Override
    protected void onNoAction(final Context context, final UserManager userManager) {
        super.onNoAction(context, userManager);
        this.DoAcceptGroupInvite(this.groupID, this.sessionID, false);
    }
    
    @Override
    public void onYesAction(final Context context, final UserManager userManager) {
        super.onYesAction(context, userManager);
        if (this.joinFee == 0) {
            this.DoAcceptGroupInvite(this.groupID, this.sessionID, true);
        }
        else {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(context);
            alertDialog$Builder.setMessage((CharSequence)context.getString(2131296642, new Object[] { this.joinFee })).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I$1(this)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I());
            alertDialog$Builder.create().show();
        }
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTransactionAmount(this.joinFee);
        chatMessage.setSessionID(this.sessionID);
        chatMessage.setItemID(this.groupID);
    }
}
