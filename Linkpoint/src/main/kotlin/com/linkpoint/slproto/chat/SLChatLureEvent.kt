package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.TeleportProgressDialog
import java.util.UUID
import javax.annotation.Nonnull

val class SLChatLureEvent : SLChatYesNoEvent() {
    private val UUID lureID

    public SLChatLureEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.lureID = chatMessage.getSessionID()
    }

    public SLChatLureEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.lureID = improvedInstantMessage.MessageBlock_Field.ID
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Lure
    }

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.teleport_lure_no)
    }

     public fun getNoMessage(context: Context): String {
        return context.getString(R.string.teleport_lure_declined)
    }

     public fun getQuestion(context: Context): String {
        return context.getString(R.string.teleport_lure_question)
    }

     public fun getYesButton(context: Context): String {
        return context.getString(R.string.teleport_lure_yes)
    }

     public fun getYesMessage(context: Context): String {
        return context.getString(R.string.teleport_lure_accepted)
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        super.onYesAction(context, userManager)
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show()
            activeAgentCircuit.TeleportToLure(this.lureID)
        }
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.lureID)
    }
}
