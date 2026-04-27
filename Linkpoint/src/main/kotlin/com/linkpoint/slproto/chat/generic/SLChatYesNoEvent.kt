package com.linkpoint.slproto.chat.generic

import android.content.Context
import android.support.v7.widget.CardView
import android.widget.Button
import android.widget.TextView
import com.linkpoint.dao.ChatMessage
import com.linkpoint.slproto.chat.SLChatTextEvent
import com.linkpoint.slproto.chat.generic.SLChatEvent
import com.linkpoint.slproto.messages.ImprovedInstantMessage
import com.linkpoint.slproto.users.chatsrc.ChatMessageSource
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.chat.ChatEventTimestampUpdater
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

abstract class SLChatYesNoEvent : SLChatTextEvent() {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues  reason: not valid java name */
    private const val /* synthetic */ IntArray f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues = null
    private EventState eventState = EventState.EventNew

    enum class EventState {
        EventNew,
        EventAccepted,
        EventCancelled
        
        const val Array<EventState> VALUES = null

        static {
            VALUES = values()
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues  reason: not valid java name */
    @JvmStatic
private /* synthetic */ IntArray m157getcomlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues() {
        if (f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues != null) {
            return f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues
        }
        val iArr: IntArray = Int[EventState.values().length]
        try {
            iArr[EventState.EventAccepted.ordinal()] = 1
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[EventState.EventCancelled.ordinal()] = 2
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[EventState.EventNew.ordinal()] = 3
        } catch (NoSuchFieldError e3) {
        }
        f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues = iArr
        return iArr
    }

    public SLChatYesNoEvent(ChatMessage chatMessage, UUID uuid) {
        super(chatMessage, uuid)
        this.eventState = EventState.VALUES[chatMessage.getEventState().intValue()]
    }

    public SLChatYesNoEvent(ChatMessageSource chatMessageSource, UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str) {
        super(chatMessageSource, uuid, improvedInstantMessage, str)
    }

    public SLChatYesNoEvent(ChatMessageSource chatMessageSource, UUID uuid, String str) {
        super(chatMessageSource, uuid, str)
    }

    fun bindViewHolder(chatEventViewHolder: ChatEventViewHolder, userManager: UserManager, chatEventTimestampUpdater: ChatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater)
        if (chatEventViewHolder instanceof ChatYesNoEventViewHolder) {
            val chatYesNoEventViewHolder: ChatYesNoEventViewHolder = (ChatYesNoEventViewHolder) chatEventViewHolder
            chatYesNoEventViewHolder.setEvent(this)
            val textView: TextView = chatYesNoEventViewHolder.questionMsg
            val button: Button = chatYesNoEventViewHolder.yesButton
            val button2: Button = chatYesNoEventViewHolder.noButton
            val cardView: CardView = chatYesNoEventViewHolder.cardView
            switch (m157getcomlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues()[this.eventState.ordinal()]) {
                case 1:
                    textView.setText(getYesMessage(textView.getContext()))
                    button.setVisibility(8)
                    button2.setVisibility(8)
                    if (getYesMessage(textView.getContext()).equals("")) {
                        textView.setVisibility(8)
                    } else {
                        textView.setVisibility(0)
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled()
                    return
                case 2:
                    textView.setText(getNoMessage(textView.getContext()))
                    button.setVisibility(8)
                    button2.setVisibility(8)
                    if (getNoMessage(textView.getContext()).equals("")) {
                        textView.setVisibility(8)
                    } else {
                        textView.setVisibility(0)
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled()
                    return
                case 3:
                    textView.setText(getQuestion(textView.getContext()))
                    textView.setVisibility(0)
                    button.setVisibility(0)
                    button2.setVisibility(0)
                    button.setText(getYesButton(button.getContext()))
                    button2.setText(getNoButton(button2.getContext()))
                    chatYesNoEventViewHolder.makeCardViewEnabled()
                    return
                default:
                    return
            }
        }
    }

     public fun getEventState(): EventState {
        return this.eventState
    }

    /* access modifiers changed from: protected */
    public abstract String getNoButton(Context context)

    /* access modifiers changed from: protected */
    public abstract String getNoMessage(Context context)

    /* access modifiers changed from: protected */
    public abstract String getQuestion(Context context)

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_YESNO
    }

    /* access modifiers changed from: protected */
    public abstract String getYesButton(Context context)

    /* access modifiers changed from: protected */
    public abstract String getYesMessage(Context context)

    /* access modifiers changed from: protected */
    fun onNoAction(context: Context, userManager: UserManager) {
        this.eventState = EventState.EventCancelled
        notifyEventUpdated(userManager)
    }

    fun onYesAction(context: Context, userManager: UserManager) {
        this.eventState = EventState.EventAccepted
        notifyEventUpdated(userManager)
    }

    fun serializeToDatabaseObject(chatMessage: ChatMessage) {
        super.serializeToDatabaseObject(chatMessage)
        chatMessage.setEventState(Integer.valueOf(this.eventState.ordinal()))
    }
}
