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
import javax.annotation.Nonnull

val class SLMissedVoiceCallEvent : SLChatYesNoEvent() {
    public SLMissedVoiceCallEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
    }

    public SLMissedVoiceCallEvent(ChatMessageSource chatMessageSource, UUID uuid, String str) {
        super(chatMessageSource, uuid, str)
    }

    /* access modifiers changed from: protected */
    public SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.MissedVoiceCall
    }

     public fun getNoButton(context: Context): String {
        return context.getString(R.string.missed_voice_call_no)
    }

     public fun getNoMessage(context: Context): String {
        return context.getString(R.string.missed_voice_call_declined)
    }

     public fun getQuestion(context: Context): String {
        return context.getString(R.string.missed_voice_call_question)
    }

     public fun getYesButton(context: Context): String {
        return context.getString(R.string.missed_voice_call_yes)
    }

     public fun getYesMessage(context: Context): String {
        return ""
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        SLModules modules
        super.onYesAction(context, userManager)
        val activeAgentCircuit: SLAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.voice.userVoiceChatRequest(this.source.getSourceUUID())
        }
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
