// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            ChatYesNoEventViewHolder, ChatEventViewHolder

public abstract class SLChatYesNoEvent extends SLChatTextEvent
{
    public static final class EventState extends Enum
    {

        private static final EventState $VALUES[];
        public static final EventState EventAccepted;
        public static final EventState EventCancelled;
        public static final EventState EventNew;
        public static final EventState VALUES[] = values();

        public static EventState valueOf(String s)
        {
            return (EventState)Enum.valueOf(com/lumiyaviewer/lumiya/slproto/chat/generic/SLChatYesNoEvent$EventState, s);
        }

        public static EventState[] values()
        {
            return $VALUES;
        }

        static 
        {
            EventNew = new EventState("EventNew", 0);
            EventAccepted = new EventState("EventAccepted", 1);
            EventCancelled = new EventState("EventCancelled", 2);
            $VALUES = (new EventState[] {
                EventNew, EventAccepted, EventCancelled
            });
        }

        private EventState(String s, int i)
        {
            super(s, i);
        }
    }


    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues[];
    private EventState eventState;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues;
        }
        int ai[] = new int[EventState.values().length];
        try
        {
            ai[EventState.EventAccepted.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[EventState.EventCancelled.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[EventState.EventNew.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues = ai;
        return ai;
    }

    public SLChatYesNoEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        eventState = EventState.EventNew;
        eventState = EventState.VALUES[chatmessage.getEventState().intValue()];
    }

    public SLChatYesNoEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage, String s)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, s);
        eventState = EventState.EventNew;
    }

    public SLChatYesNoEvent(ChatMessageSource chatmessagesource, UUID uuid, String s)
    {
        super(chatmessagesource, uuid, s);
        eventState = EventState.EventNew;
    }

    public void bindViewHolder(ChatEventViewHolder chateventviewholder, UserManager usermanager, ChatEventTimestampUpdater chateventtimestampupdater)
    {
        super.bindViewHolder(chateventviewholder, usermanager, chateventtimestampupdater);
        if (!(chateventviewholder instanceof ChatYesNoEventViewHolder)) goto _L2; else goto _L1
_L1:
        Button button;
        chateventviewholder = (ChatYesNoEventViewHolder)chateventviewholder;
        chateventviewholder.setEvent(this);
        usermanager = ((ChatYesNoEventViewHolder) (chateventviewholder)).questionMsg;
        chateventtimestampupdater = ((ChatYesNoEventViewHolder) (chateventviewholder)).yesButton;
        button = ((ChatYesNoEventViewHolder) (chateventviewholder)).noButton;
        android.support.v7.widget.CardView cardview = ((ChatYesNoEventViewHolder) (chateventviewholder)).cardView;
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_chat_2D_generic_2D_SLChatYesNoEvent$EventStateSwitchesValues()[eventState.ordinal()];
        JVM INSTR tableswitch 1 3: default 84
    //                   1 144
    //                   2 204
    //                   3 85;
           goto _L2 _L3 _L4 _L5
_L2:
        return;
_L5:
        usermanager.setText(getQuestion(usermanager.getContext()));
        usermanager.setVisibility(0);
        chateventtimestampupdater.setVisibility(0);
        button.setVisibility(0);
        chateventtimestampupdater.setText(getYesButton(chateventtimestampupdater.getContext()));
        button.setText(getNoButton(button.getContext()));
        chateventviewholder.makeCardViewEnabled();
        return;
_L3:
        usermanager.setText(getYesMessage(usermanager.getContext()));
        chateventtimestampupdater.setVisibility(8);
        button.setVisibility(8);
        if (getYesMessage(usermanager.getContext()).equals(""))
        {
            usermanager.setVisibility(8);
        } else
        {
            usermanager.setVisibility(0);
        }
        chateventviewholder.makeCardViewDisabled();
        return;
_L4:
        usermanager.setText(getNoMessage(usermanager.getContext()));
        chateventtimestampupdater.setVisibility(8);
        button.setVisibility(8);
        if (getNoMessage(usermanager.getContext()).equals(""))
        {
            usermanager.setVisibility(8);
        } else
        {
            usermanager.setVisibility(0);
        }
        chateventviewholder.makeCardViewDisabled();
        return;
    }

    public EventState getEventState()
    {
        return eventState;
    }

    protected abstract String getNoButton(Context context);

    protected abstract String getNoMessage(Context context);

    protected abstract String getQuestion(Context context);

    public SLChatEvent.ChatMessageViewType getViewType()
    {
        return SLChatEvent.ChatMessageViewType.VIEW_TYPE_YESNO;
    }

    protected abstract String getYesButton(Context context);

    protected abstract String getYesMessage(Context context);

    protected void onNoAction(Context context, UserManager usermanager)
    {
        eventState = EventState.EventCancelled;
        notifyEventUpdated(usermanager);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        eventState = EventState.EventAccepted;
        notifyEventUpdated(usermanager);
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setEventState(Integer.valueOf(eventState.ordinal()));
    }
}
