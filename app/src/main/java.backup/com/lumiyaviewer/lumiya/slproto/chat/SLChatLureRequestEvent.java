package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;
import javax.annotation.Nonnull;

public final class SLChatLureRequestEvent extends SLChatYesNoEvent {
    public SLChatLureRequestEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
    }

    public SLChatLureRequestEvent(ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null);
    }

    /* access modifiers changed from: protected */
    @Nonnull
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequest;
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.teleport_lure_request_no);
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.teleport_lure_request_declined);
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.teleport_lure_request_question);
    }

    public String getText(Context context, @Nonnull UserManager userManager) {
        String string = context.getString(R.string.teleport_lure_request_message);
        return !Strings.isNullOrEmpty(this.text) ? string + ": " + this.text : string + ".";
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.teleport_lure_request_yes);
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.teleport_lure_request_accepted);
    }

    /* access modifiers changed from: protected */
    public boolean isActionMessage(@Nonnull UserManager userManager) {
        return true;
    }

    public void onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager);
        UUID sourceUUID = this.source.getSourceUUID();
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit();
        if (sourceUUID != null && activeAgentCircuit != null) {
            String regionName = activeAgentCircuit.getRegionName();
            if (Strings.isNullOrEmpty(regionName)) {
                regionName = context.getString(R.string.unknown_region_name);
            }
            activeAgentCircuit.OfferTeleport(sourceUUID, context.getString(R.string.join_me_in_region, new Object[]{regionName}));
        }
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
    }
}
