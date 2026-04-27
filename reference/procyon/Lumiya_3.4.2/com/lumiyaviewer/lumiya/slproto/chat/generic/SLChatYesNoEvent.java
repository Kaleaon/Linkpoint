// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.widget.Button;
import android.widget.TextView;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import java.util.UUID;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;

public abstract class SLChatYesNoEvent extends SLChatTextEvent
{
    @Nonnull
    private EventState eventState;
    
    private static /* synthetic */ int[] -getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues() {
        if (SLChatYesNoEvent.-com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues != null) {
            return SLChatYesNoEvent.-com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues;
        }
        int[] -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues = new int[EventState.values().length];
        while (true) {
            try {
                -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues[EventState.EventAccepted.ordinal()] = 1;
                try {
                    -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues[EventState.EventCancelled.ordinal()] = 2;
                    try {
                        -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues[EventState.EventNew.ordinal()] = 3;
                        return -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues = -com-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues;
                    }
                    catch (final NoSuchFieldError noSuchFieldError) {}
                }
                catch (final NoSuchFieldError noSuchFieldError2) {}
            }
            catch (final NoSuchFieldError noSuchFieldError3) {
                continue;
            }
            break;
        }
    }
    
    public SLChatYesNoEvent(final ChatMessage chatMessage, @Nonnull final UUID uuid) {
        super(chatMessage, uuid);
        this.eventState = EventState.EventNew;
        this.eventState = EventState.VALUES[chatMessage.getEventState()];
    }
    
    public SLChatYesNoEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final ImprovedInstantMessage improvedInstantMessage, final String s) {
        super(chatMessageSource, uuid, improvedInstantMessage, s);
        this.eventState = EventState.EventNew;
    }
    
    public SLChatYesNoEvent(@Nonnull final ChatMessageSource chatMessageSource, @Nonnull final UUID uuid, final String s) {
        super(chatMessageSource, uuid, s);
        this.eventState = EventState.EventNew;
    }
    
    @Override
    public void bindViewHolder(final ChatEventViewHolder chatEventViewHolder, final UserManager userManager, @Nullable final ChatEventTimestampUpdater chatEventTimestampUpdater) {
        super.bindViewHolder(chatEventViewHolder, userManager, chatEventTimestampUpdater);
        if (chatEventViewHolder instanceof ChatYesNoEventViewHolder) {
            final ChatYesNoEventViewHolder chatYesNoEventViewHolder = (ChatYesNoEventViewHolder)chatEventViewHolder;
            chatYesNoEventViewHolder.setEvent(this);
            final TextView questionMsg = chatYesNoEventViewHolder.questionMsg;
            final Button yesButton = chatYesNoEventViewHolder.yesButton;
            final Button noButton = chatYesNoEventViewHolder.noButton;
            final CardView cardView = chatYesNoEventViewHolder.cardView;
            switch (-getcom-lumiyaviewer-lumiya-slproto-chat-generic-SLChatYesNoEvent$EventStateSwitchesValues()[this.eventState.ordinal()]) {
                case 3: {
                    questionMsg.setText((CharSequence)this.getQuestion(questionMsg.getContext()));
                    questionMsg.setVisibility(0);
                    yesButton.setVisibility(0);
                    noButton.setVisibility(0);
                    yesButton.setText((CharSequence)this.getYesButton(yesButton.getContext()));
                    noButton.setText((CharSequence)this.getNoButton(noButton.getContext()));
                    chatYesNoEventViewHolder.makeCardViewEnabled();
                    break;
                }
                case 1: {
                    questionMsg.setText((CharSequence)this.getYesMessage(questionMsg.getContext()));
                    yesButton.setVisibility(8);
                    noButton.setVisibility(8);
                    if (this.getYesMessage(questionMsg.getContext()).equals("")) {
                        questionMsg.setVisibility(8);
                    }
                    else {
                        questionMsg.setVisibility(0);
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled();
                    break;
                }
                case 2: {
                    questionMsg.setText((CharSequence)this.getNoMessage(questionMsg.getContext()));
                    yesButton.setVisibility(8);
                    noButton.setVisibility(8);
                    if (this.getNoMessage(questionMsg.getContext()).equals("")) {
                        questionMsg.setVisibility(8);
                    }
                    else {
                        questionMsg.setVisibility(0);
                    }
                    chatYesNoEventViewHolder.makeCardViewDisabled();
                    break;
                }
            }
        }
    }
    
    @Nonnull
    public EventState getEventState() {
        return this.eventState;
    }
    
    protected abstract String getNoButton(final Context p0);
    
    protected abstract String getNoMessage(final Context p0);
    
    protected abstract String getQuestion(final Context p0);
    
    @Override
    public ChatMessageViewType getViewType() {
        return ChatMessageViewType.VIEW_TYPE_YESNO;
    }
    
    protected abstract String getYesButton(final Context p0);
    
    protected abstract String getYesMessage(final Context p0);
    
    protected void onNoAction(final Context context, final UserManager userManager) {
        this.eventState = EventState.EventCancelled;
        this.notifyEventUpdated(userManager);
    }
    
    public void onYesAction(final Context context, final UserManager userManager) {
        this.eventState = EventState.EventAccepted;
        this.notifyEventUpdated(userManager);
    }
    
    @Override
    public void serializeToDatabaseObject(@Nonnull final ChatMessage chatMessage) {
        super.serializeToDatabaseObject(chatMessage);
        chatMessage.setEventState(this.eventState.ordinal());
    }
    
    public enum EventState
    {
        EventAccepted("EventAccepted", 1), 
        EventCancelled("EventCancelled", 2), 
        EventNew("EventNew", 0);
        
        public static final EventState[] VALUES;
        
        static {
            VALUES = values();
        }
        
        private EventState(final String name, final int ordinal) {
        }
    }
}
