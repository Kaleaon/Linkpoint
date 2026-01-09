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

    fun getNoButton(Context context): String {
        return context.getString(R.string.missed_voice_call_no)
    }

    fun getNoMessage(Context context): String {
        return context.getString(R.string.missed_voice_call_declined)
    }

    fun getQuestion(Context context): String {
        return context.getString(R.string.missed_voice_call_question)
    }

    fun getYesButton(Context context): String {
        return context.getString(R.string.missed_voice_call_yes)
    }

    fun getYesMessage(Context context): String {
        return ""
    }

    fun onYesAction(Context context, UserManager userManager): Unit {
        SLModules modules
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.voice.userVoiceChatRequest(this.source.getSourceUUID())
        }
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage): Unit {
        super.serializeToDatabaseObject(chatMessage)
    }
}
