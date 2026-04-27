package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog;
import java.util.UUID;
import javax.annotation.Nonnull;

/* loaded from: classes.dex */
public final class SLChatLureEvent extends SLChatYesNoEvent {
    private final UUID lureID;

    public SLChatLureEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.lureID = chatMessage.getSessionID();
    }

    public SLChatLureEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, null);
        this.lureID = improvedInstantMessage.MessageBlock_Field.ID;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    @Nonnull
    protected SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Lure;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoButton(Context context) {
        return context.getString(R.string.teleport_lure_no);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getNoMessage(Context context) {
        return context.getString(R.string.teleport_lure_declined);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getQuestion(Context context) {
        return context.getString(R.string.teleport_lure_question);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesButton(Context context) {
        return context.getString(R.string.teleport_lure_yes);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public String getYesMessage(Context context) {
        return context.getString(R.string.teleport_lure_accepted);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (activeAgentCircuit != null) {
            new TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show();
            activeAgentCircuit.TeleportToLure(this.lureID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent, com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent, com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setSessionID(this.lureID);
    }
}
