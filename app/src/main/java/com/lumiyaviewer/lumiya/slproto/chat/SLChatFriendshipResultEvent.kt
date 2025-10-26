package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID
import androidx.annotation.NonNull

class SLChatFriendshipResultEvent : SLChatEvent {
    private Boolean accepted

    SLChatFriendshipResultEvent(ChatMessage chatMessage, @NonNull UUID uuid) {
        super(chatMessage, uuid)
        this.accepted = chatMessage.getAccepted().booleanValue()
    }

    SLChatFriendshipResultEvent(@NonNull ChatMessageSource chatMessageSource, @NonNull UUID uuid, ImprovedInstantMessage improvedInstantMessage) {
        super(improvedInstantMessage, uuid, chatMessageSource)
        this.accepted = improvedInstantMessage.MessageBlock_Field.Dialog == 39
    }

    /* access modifiers changed from: protected */
    @NonNull
    SLChatEvent.ChatMessageType getMessageType() {
        return SLChatEvent.ChatMessageType.FriendshipResult
    }

    /* access modifiers changed from: protected */
    String getText(Context context, @NonNull UserManager userManager) {
        return context.getString(this.accepted ? R.string.friendship_accepted : R.string.friendship_declined)
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    Boolean isActionMessage(@NonNull UserManager userManager) {
        return true
    }

    Unit serializeToDatabaseObject(@NonNull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setAccepted(Boolean.valueOf(this.accepted))
    }
}
