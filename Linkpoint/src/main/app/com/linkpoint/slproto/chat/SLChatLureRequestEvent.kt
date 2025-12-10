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
import androidx.annotation.NonNull

class SLChatLureRequestEvent : SLChatYesNoEvent {
    SLChatLureRequestEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLChatLureRequestEvent(ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.LureRequest
    }

    fun getNoButton(Context context): String {
        return context.getString(R.string.teleport_lure_request_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.teleport_lure_request_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.teleport_lure_request_question)
    }

    fun getText(Context context, @NonNull UserManager userManager): String {
        String string = context.getString(R.string.teleport_lure_request_message)
        return !Strings.isNullOrEmpty(this.text) ? string + ": " + this.text : string + "."
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.teleport_lure_request_yes)
    }

    fun getYesMessage(Context context): String {
        return context.getString(R.string.teleport_lure_request_accepted)
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    fun onYesAction(Context context, UserManager userManager): Unit {
        super.onYesAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            String regionName = activeAgentCircuit.getRegionName()
            if (Strings.isNullOrEmpty(regionName)) {
                regionName = context.getString(R.string.unknown_region_name)
            }
            activeAgentCircuit.OfferTeleport(sourceUUID, context.getString(R.string.join_me_in_region, Array<Any>{regionName}))
        }
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        super.serializeToDatabaseObject(chatMessage)
    }
}
