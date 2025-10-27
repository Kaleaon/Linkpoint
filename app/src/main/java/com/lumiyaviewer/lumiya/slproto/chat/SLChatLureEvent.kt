package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.TeleportProgressDialog
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
