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

    public String getNoButton(Context context) {
        return context.getString(R.string.missed_voice_call_no)
    }

    public String getNoMessage(Context context) {
        return context.getString(R.string.missed_voice_call_declined)
    }

    public String getQuestion(Context context) {
        return context.getString(R.string.missed_voice_call_question)
    }

    public String getYesButton(Context context) {
        return context.getString(R.string.missed_voice_call_yes)
    }

    public String getYesMessage(Context context) {
        return ""
    }

    public Unit onYesAction(Context context, UserManager userManager) {
        SLModules modules
        super.onYesAction(context, userManager)
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (activeAgentCircuit != null && (modules = activeAgentCircuit.getModules()) != null) {
            modules.voice.userVoiceChatRequest(this.source.getSourceUUID())
        }
    }

    public Unit serializeToDatabaseObject(ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
    }
}
