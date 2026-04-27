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

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.teleport_lure_request_no)
    }

     public fun getNoMessage(context: Context): String {
        return context.getString(R.string.teleport_lure_request_declined)
    }

     public fun getQuestion(context: Context): String {
        return context.getString(R.string.teleport_lure_request_question)
    }

     public fun getText(context: Context, userManager: UserManager): String {
        val string: String = context.getString(R.string.teleport_lure_request_message)
        return !Strings.isNullOrEmpty(this.text) ? string + ": " + this.text : string + "."
    }

     public fun getYesButton(context: Context): String {
        return context.getString(R.string.teleport_lure_request_yes)
    }

     public fun getYesMessage(context: Context): String {
        return context.getString(R.string.teleport_lure_request_accepted)
    }

    /* access modifiers changed from: protected */
     public fun isActionMessage(userManager: UserManager): Boolean {
        return true
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val sourceUUID: UUID = this.source.getSourceUUID()
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            val regionName: String = activeAgentCircuit.getRegionName()
            if (Strings.isNullOrEmpty(regionName)) {
                regionName = context.getString(R.string.unknown_region_name)
            }
            activeAgentCircuit.OfferTeleport(sourceUUID, context.getString(R.string.join_me_in_region, Array<Any>{regionName}))
        }
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
