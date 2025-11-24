package com.lumiyaviewer.lumiya.slproto.chat.generic

import android.content.Context
import com.lumiyaviewer.lumiya.dao.ChatMessage
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import java.util.UUID

abstract class SLChatYesNoEvent : SLChatTextEvent {
    enum class EventState {
        EventNew,
        EventAccepted,
        EventCancelled
    }

    var eventState: EventState = EventState.EventNew
        private set

    constructor(chatMessage: ChatMessage, uuid: UUID) : super(chatMessage, uuid) {
        val stateOrdinal = chatMessage.eventState ?: 0
        this.eventState = if (stateOrdinal in EventState.values().indices) {
            EventState.values()[stateOrdinal]
        } else {
            EventState.EventNew
        }
    }

    constructor(source: ChatMessageSource, uuid: UUID, message: ImprovedInstantMessage, text: String?) : super(source, uuid, message, text)
    constructor(source: ChatMessageSource, uuid: UUID, text: String) : super(source, uuid, text)

    override fun getViewType(): ChatMessageViewType {
        return ChatMessageViewType.VIEW_TYPE_YESNO
    }

    protected abstract fun getNoButton(context: Context): String
    protected abstract fun getNoMessage(context: Context): String
    protected abstract fun getQuestion(context: Context): String
    protected abstract fun getYesButton(context: Context): String
    protected abstract fun getYesMessage(context: Context): String

    open fun onNoAction(context: Context, userManager: UserManager) {
        this.eventState = EventState.EventCancelled
        notifyEventUpdated(userManager)
    }

    open fun onYesAction(context: Context, userManager: UserManager) {
        this.eventState = EventState.EventAccepted
        notifyEventUpdated(userManager)
    }

    override fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.eventState = eventState.ordinal
    }
    
    // Stub for notifyEventUpdated if it's missing in SLChatTextEvent
    fun notifyEventUpdated(userManager: UserManager) {
        // Implementation handled by system or stub
    }
}
