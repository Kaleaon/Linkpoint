package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent
import com.lumiyaviewer.lumiya.slproto.modules.SLModules
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLMissedVoiceCallEvent : SLChatYesNoEvent {
    SLMissedVoiceCallEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
    }

    SLMissedVoiceCallEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, String str) {
        super(chatMessageSource, uuid, str)
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.MissedVoiceCall
    }

    String getNoButton(Context context) {
        return context.getString(R.string.missed_voice_call_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.missed_voice_call_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.missed_voice_call_question)
    }

    String getYesButton(Context context) {
        return context.getString(R.string.missed_voice_call_yes)
    }

    String getYesMessage(Context context) {
        return ""
    }

    Unit onYesAction(Context context, UserManager userManager) {
        SLModules modules
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.voice.userVoiceChatRequest(this.source.getSourceUUID())
        }
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
