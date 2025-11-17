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
import androidx.annotation.NonNull

class SLChatLureEvent : SLChatYesNoEvent {
    private UUID lureID

    SLChatLureEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.lureID = chatMessage.getSessionID()
    }

    SLChatLureEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.lureID = improvedInstantMessage.MessageBlock_Field.ID
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.Lure
    }

    String getNoButton(Context context) {
        return context.getString(R.string.teleport_lure_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.teleport_lure_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.teleport_lure_question)
    }

    String getYesButton(Context context) {
        return context.getString(R.string.teleport_lure_yes)
    }

    String getYesMessage(Context context) {
        return context.getString(R.string.teleport_lure_accepted)
    }

    Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null) {
            TeleportProgressDialog(context, userManager, R.string.teleporting_progress_message).show()
            activeAgentCircuit.TeleportToLure(this.lureID)
        }
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.lureID)
    }
}
