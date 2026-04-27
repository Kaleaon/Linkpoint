package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SLChatYesNoEvent extends SLChatTextEvent {

    /* renamed from: -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues  reason: not valid java name */
    private static final /* synthetic */ int[] f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues = null;
    @Nonnull
    private EventState eventState = EventState.EventNew;

    public enum EventState {
        EventNew,
        EventAccepted,
        EventCancelled;
        
        public static final EventState[] VALUES = null;

        static {
            VALUES = values();
        }
    }

    /* renamed from: -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues  reason: not valid java name */
    private static /* synthetic */ int[] m157getcomlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues() {
        if (f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues != null) {
            return f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues;
        }
        int[] iArr = new int[EventState.values().length];
        try {
            iArr[EventState.EventAccepted.ordinal()] = 1;
        } catch (NoSuchFieldError e) {
        }
        try {
            iArr[EventState.EventCancelled.ordinal()] = 2;
        } catch (NoSuchFieldError e2) {
        }
        try {
            iArr[EventState.EventNew.ordinal()] = 3;
        } catch (NoSuchFieldError e3) {
        }
        f74comlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues = iArr;
        return iArr;
    }

    public SLChatYesNoEvent(ChatMessage chatMessage, @Nonnull UUID uuid) {
        super(chatMessage, uuid);
        this.eventState = EventState.VALUES[chatMessage.getEventState().intValue()];
    }

    public SLChatYesNoEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, ImprovedInstantMessage improvedInstantMessage, String str) {
        super(chatMessageSource, uuid, improvedInstantMessage, str);
    }

    public SLChatYesNoEvent(@Nonnull ChatMessageSource chatMessageSource, @Nonnull UUID uuid, String str) {
        super(chatMessageSource, uuid, str);
    }

    public void bindViewHolder(ChatEventViewHolder chatEventViewHolder, UserManager userManager, @Nullable ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatYesNoEventViewHolder) {
            ChatYesNoEventViewHolder chatYesNoEventViewHolder = (ChatYesNoEventViewHolder) chatEventViewHolder;
            chatYesNoEventViewHolder.setEvent(this);
            TextView textView = chatYesNoEventViewHolder.questionMsg;
            Button button = chatYesNoEventViewHolder.yesButton;
            Button button2 = chatYesNoEventViewHolder.noButton;
            CardView cardView = chatYesNoEventViewHolder.cardView;
            switch (m157getcomlumiyaviewerlumiyaslprotochatgenericSLChatYesNoEvent$EventStateSwitchesValues()[this.eventState.ordinal()]) {
                case 1:
                    textView.setText(getYesMessage(textView.getContext()));
                    button.setVisibility(8);
                    button2.setVisibility(8);
                    if (getYesMessage(textView.getContext()).equals("")) {
                        textView.setVisibility(8);
                    } else {
                        textView.setVisibility(0);
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled();
                    return;
                case 2:
                    textView.setText(getNoMessage(textView.getContext()));
                    button.setVisibility(8);
                    button2.setVisibility(8);
                    if (getNoMessage(textView.getContext()).equals("")) {
                        textView.setVisibility(8);
                    } else {
                        textView.setVisibility(0);
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled();
                    return;
                case 3:
                    textView.setText(getQuestion(textView.getContext()));
                    textView.setVisibility(0);
                    button.setVisibility(0);
                    button2.setVisibility(0);
                    button.setText(getYesButton(button.getContext()));
                    button2.setText(getNoButton(button2.getContext()));
                    chatYesNoEventViewHolder.makeCardViewEnabled();
                    return;
                default:
                    return;
            }
        }
    }

    @Nonnull
    public EventState getEventState() {
        return this.eventState;
    }

    /* access modifiers changed from: protected */
    public abstract String getNoButton(Context context);

    /* access modifiers changed from: protected */
    public abstract String getNoMessage(Context context);

    /* access modifiers changed from: protected */
    public abstract String getQuestion(Context context);

    public SLChatEvent.ChatMessageViewType getViewType() {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_YESNO;
    }

    /* access modifiers changed from: protected */
    public abstract String getYesButton(Context context);

    /* access modifiers changed from: protected */
    public abstract String getYesMessage(Context context);

    /* access modifiers changed from: protected */
    public void onNoAction(Context context, UserManager userManager) {
        this.eventState = EventState.EventCancelled;
        notifyEventUpdated(userManager);
    }

    public void onYesAction(Context context, UserManager userManager) {
        this.eventState = EventState.EventAccepted;
        notifyEventUpdated(userManager);
    }

    public void serializeToDatabaseObject(@Nonnull ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setEventState(Integer.valueOf(this.eventState.ordinal()));
    }
}
