package com.linkpoint.slproto.chat;

import android.content.Context;
import com.linkpoint.R;
import com.linkpoint.dao.ChatMessage;
import com.linkpoint.slproto.SLAgentCircuit;
import com.linkpoint.slproto.chat.generic.SLChatEvent;
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent;
import com.linkpoint.slproto.messages.ImprovedInstantMessage;
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource;
import com.linkpoint.slproto.users.manager.UserManager;
import com.linkpoint.ui.common.TeleportProgressDialog;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatLureEvent extends SLChatYesNoEvent {
    private final UUID lureID;

    public SLChatLureEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.lureID = chatMessage.getSessionID();
    }

    public SLChatLureEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null);
        this.lureID = improvedInstantMessage.MessageBlock_Field.ID;
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Lure;
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.teleport_lure_no);
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.teleport_lure_declined);
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.teleport_lure_question);
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.teleport_lure_yes);
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.teleport_lure_accepted);
    }

    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            new TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show();
            activeAgentCircuit.TeleportToLure(this.lureID);
        }
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.lureID);
    }
}
