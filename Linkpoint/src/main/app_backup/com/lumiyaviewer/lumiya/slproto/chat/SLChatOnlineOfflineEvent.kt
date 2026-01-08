package com.lumiyaviewer.lumiya.slproto.chat

import android.content.Context
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

class SLChatOnlineOfflineEvent : SLChatEvent {
    private val isOnline: Boolean

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        this.isOnline = chatMessage.type == ChatMessageType.WentOnline.ordinal
    }

    constructor(source: ChatMessageSource, uuid: UUID, isOnline: Boolean) : 
        super(source, uuid, null) {
        this.isOnline = isOnline
    }

    override fun getMessageType(): ChatMessageType {
        return if (isOnline) ChatMessageType.WentOnline else ChatMessageType.WentOffline
    }

    override fun getMessage(): CharSequence {
        // This usually requires context to stringify properly, but we don't have it here.
        // We'll return a placeholder or use a static lookup if possible.
        // Since we can't easily access strings, we return a generic message.
        // Ideally this should be handled by the ViewHolder using context.
        return if (isOnline) "is online" else "is offline"
    }

    // Helper for ViewHolders/UI to get the localized string
    fun getStatusText(context: Context, name: String): String {
        val resId = if (isOnline) R.string.chat_notification_online else R.string.chat_notification_offline
        return context.getString(resId, name)
    }

    override fun getSenderDisplayName(): CharSequence? {
        return null // usually system or the user themselves
    }

    override fun getChatterID(): com.lumiyaviewer.lumiya.slproto.users.ChatterID {
        return com.lumiyaviewer.lumiya.slproto.users.ChatterID(agentUUID, "")
    }

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_STATUS
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        // Type is already set by getMessageType() -> ordinal in base logic usually
    }
}
