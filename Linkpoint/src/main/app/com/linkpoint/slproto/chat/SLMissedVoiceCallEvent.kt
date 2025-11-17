package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.chat.generic.SLChatYesNoEvent
import com.linkpoint.slproto.modules.SLModules
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
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
