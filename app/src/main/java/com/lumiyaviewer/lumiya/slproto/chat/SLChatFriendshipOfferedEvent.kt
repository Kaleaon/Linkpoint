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
import java.util.UUID
import javax.annotation.Nonnull

class SLChatFriendshipOfferedEvent : SLChatYesNoEvent {
    UUID sessionID

    SLChatFriendshipOfferedEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid)
        this.sessionID = chatMessage.getSessionID()
    }

    SLChatFriendshipOfferedEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(chatMessageSource, uuid, improvedInstantMessage, (String) null)
        this.sessionID = improvedInstantMessage.MessageBlock_Field.ID
    }

    /* access modifiers changed from: protected */
    @Nonnull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipOffered
    }

    String getNoButton(Context context) {
        return context.getString(R.string.friendship_request_no)
    }

    String getNoMessage(Context context) {
        return context.getString(R.string.friendship_request_declined)
    }

    String getQuestion(Context context) {
        return context.getString(R.string.friendship_request_question)
    }

    String getYesButton(Context context) {
        return context.getString(R.string.friendship_request_yes)
    }

    String getYesMessage(Context context) {
        return context.getString(R.string.friendship_request_accepted)
    }

    Unit onYesAction(Context context, UserManager userManager) {
        super.onYesAction(context, userManager)
        UUID sourceUUID = this.source.getSourceUUID()
        SLAgentCircuit activeAgentCircuit = userManager.getActiveAgentCircuit()
        if (sourceUUID != null && activeAgentCircuit != null) {
            activeAgentCircuit.AcceptFriendship(sourceUUID, this.sessionID)
        }
    }

    Unit serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setSessionID(this.sessionID)
    }
}
