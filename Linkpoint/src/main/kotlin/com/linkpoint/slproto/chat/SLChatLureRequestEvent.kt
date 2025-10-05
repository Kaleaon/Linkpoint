package com.linkpoint.slproto.chat

import android.content.Context
import com.google.common.base.Strings
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatLureRequestEvent : SLChatYesNoEvent() {
    public SLChatLureRequestEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
    }

    public SLChatLureRequestEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequest
    }

    public String getNoButton(Context context) {
        return context.getString(R.string.teleport_lure_request_no)
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.teleport_lure_request_declined)
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.teleport_lure_request_question)
    }

    public String getText(Context context, UserManager userManager) {
        String string = context.getString(R.string.teleport_lure_request_message)
        return !Strings.isNullOrEmpty(this.text) ? string + ": " + this.text : string + "."
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.teleport_lure_request_yes)
    }

    public String getYesMessage(Context context) {
        return context.getString(R.string.teleport_lure_request_accepted)
    }

    /* access modifiers changed from: protected */
    public Boolean isActionMessage(UserManager userManager) {
        return true
    }

    public Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            String regionName = activeAgentCircuit.getRegionName()
            if (Strings.isNullOrEmpty(regionName)) {
                regionName = context.getString(R.string.unknown_region_name)
            }
            activeAgentCircuit.OfferTeleport(sourceUUID, context.getString(R.string.join_me_in_region, Object[]{regionName}))
        }
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
