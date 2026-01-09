package com.linkpoint.slproto.chat

import android.content.Context
import com.linkpoint.R
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
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
    fun getText(Context context, @NonNull UserManager userManager): String {
        return context.getString(this.accepted ? R.string.friendship_accepted : R.string.friendship_declined)
    }

    SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_NORMAL
    }

    /* access modifiers changed from: protected */
    fun isActionMessage(@NonNull UserManager userManager): Boolean {
        return true
    }

    fun serializeToDatabaseObject(@NonNull ChatMessage chatMessage)  {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setAccepted(Boolean.valueOf(this.accepted))
    }
}
