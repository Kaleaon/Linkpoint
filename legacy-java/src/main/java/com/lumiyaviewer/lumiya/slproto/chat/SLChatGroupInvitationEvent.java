package com.lumiyaviewer.lumiya.slproto.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatGroupInvitationEvent extends SLChatYesNoEvent {
    private final UUID groupID;
    private final int joinFee;
    private final UUID sessionID;

    public SLChatGroupInvitationEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.joinFee = chatMessage.getTransactionAmount().intValue();
        this.sessionID = chatMessage.getSessionID();
        this.groupID = chatMessage.getItemID();
    }

    public SLChatGroupInvitationEvent(ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.groupID = improvedInstantMessage.AgentData_Field.AgentID;
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID;
        if (improvedInstantMessage.MessageBlock_Field.BinaryBucket.length < 4) {
            this.joinFee = 0;
            return;
        }
        ByteBuffer wrap = ByteBuffer.wrap(improvedInstantMessage.MessageBlock_Field.BinaryBucket);
        wrap.order(ByteOrder.BIG_ENDIAN);
        this.joinFee = wrap.getInt();
    }

    private void DoAcceptGroupInvite(UUID uuid, UUID uuid2, boolean z) {
        SLAgentCircuit activeAgentCircuit;
        UserManager userManager = UserManager.getUserManager(this.agentUUID);
        if (userManager == null || (activeAgentCircuit = userManager.getActiveAgentCircuit()) == null) {
            return;
        }
        activeAgentCircuit.getModules().groupManager.AcceptGroupInvite(uuid, uuid2, z);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.GroupInvitation;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoButton(Context context) {
        return context.getString(R.string.join_group_no);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoMessage(Context context) {
        return context.getString(R.string.join_group_declined);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getQuestion(Context context) {
        return this.joinFee == 0 ? context.getString(R.string.join_group_question_free) : context.getString(R.string.join_group_question_not_free, Integer.valueOf(this.joinFee));
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesButton(Context context) {
        return context.getString(R.string.join_group_yes);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesMessage(Context context) {
        return context.getString(R.string.join_group_accepted);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_chat_SLChatGroupInvitationEvent_3561, reason: not valid java name */
    public /* synthetic */ void m153x2b58eb32(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        DoAcceptGroupInvite(this.groupID, this.sessionID, true);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    protected void onNoAction(Context context, UserManager userManager) {
        super.onNoAction(context, userManager);
        DoAcceptGroupInvite(this.groupID, this.sessionID, false);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        if (this.joinFee == 0) {
            DoAcceptGroupInvite(this.groupID, this.sessionID, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(context.getString(R.string.join_group_confirm, Integer.valueOf(this.joinFee))).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((SLChatGroupInvitationEvent) this).m153x2b58eb32(dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.slproto.chat.-$Lambda$hXLxI3fDexZfuKx5RzOoCtsGy3I
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        });
        builder.create().show();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setTransactionAmount(Integer.valueOf(this.joinFee));
        chatMessage.setSessionID(this.sessionID);
        chatMessage.setItemID(this.groupID);
    }
}
