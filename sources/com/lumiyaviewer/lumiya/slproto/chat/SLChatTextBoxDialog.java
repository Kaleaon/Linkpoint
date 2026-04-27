// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import android.view.View;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatEventViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.ChatTextBoxViewHolder;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatDialogEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatEventTimestampUpdater;
import com.lumiyaviewer.lumiya.ui.common.TextFieldDialogBuilder;
import java.util.UUID;

public final class SLChatTextBoxDialog extends SLChatDialogEvent
{

    private String enteredValue;
    private final int textBoxButtonIndex;

    public SLChatTextBoxDialog(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        enteredValue = null;
        textBoxButtonIndex = chatmessage.getTextBoxButtonIndex().intValue();
        enteredValue = chatmessage.getDialogSelectedOption();
    }

    public SLChatTextBoxDialog(ScriptDialog scriptdialog, UUID uuid, int i)
    {
        super(scriptdialog, uuid);
        enteredValue = null;
        textBoxButtonIndex = i;
    }

    public void bindViewHolder(ChatEventViewHolder chateventviewholder, UserManager usermanager, ChatEventTimestampUpdater chateventtimestampupdater)
    {
        super.bindViewHolder(chateventviewholder, usermanager, chateventtimestampupdater);
        if (chateventviewholder instanceof ChatTextBoxViewHolder)
        {
            chateventviewholder = (ChatTextBoxViewHolder)chateventviewholder;
            if (enteredValue != null || ignored)
            {
                if (ignored)
                {
                    ((ChatTextBoxViewHolder) (chateventviewholder)).dialogResultTextView.setText(0x7f0900fe);
                } else
                {
                    ((ChatTextBoxViewHolder) (chateventviewholder)).dialogResultTextView.setText(((ChatTextBoxViewHolder) (chateventviewholder)).dialogResultTextView.getContext().getString(0x7f090351, new Object[] {
                        enteredValue
                    }));
                }
                ((ChatTextBoxViewHolder) (chateventviewholder)).dialogResultTextView.setVisibility(0);
                ((ChatTextBoxViewHolder) (chateventviewholder)).dialogButtonsLayout.setVisibility(8);
            } else
            {
                ((ChatTextBoxViewHolder) (chateventviewholder)).dialogResultTextView.setVisibility(8);
                ((ChatTextBoxViewHolder) (chateventviewholder)).dialogButtonsLayout.setVisibility(0);
            }
            chateventviewholder.setTextBoxEvent(this);
        }
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.TextBoxDialog;
    }

    public com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType getViewType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageViewType.VIEW_TYPE_TEXTBOX;
    }

    public boolean isObjectPopup()
    {
        return true;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4223(UserManager usermanager, String s)
    {
        onEnteredText(usermanager, s);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_chat_SLChatTextBoxDialog_4314(UserManager usermanager)
    {
        onDialogIgnored(usermanager);
    }

    public void onDialogIgnored(UserManager usermanager)
    {
        super.onDialogIgnored(usermanager);
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void onEnteredText(UserManager usermanager, String s)
    {
        enteredValue = s;
        UUID uuid = source.getSourceUUID();
        SLAgentCircuit slagentcircuit = usermanager.getActiveAgentCircuit();
        if (uuid != null && slagentcircuit != null)
        {
            slagentcircuit.SendScriptDialogReply(uuid, chatChannel, textBoxButtonIndex, s);
        }
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setTextBoxButtonIndex(Integer.valueOf(textBoxButtonIndex));
        chatmessage.setDialogSelectedOption(enteredValue);
    }

    public void showDialog(Context context, UserManager usermanager)
    {
        (new TextFieldDialogBuilder(context)).setTitle(text).setOnTextEnteredListener(new _2D_.Lambda.Iyj6QpN_2D_ZLoXueXenKuJvDVzcmI._cls1(this, usermanager)).setOnTextCancelledListener(new _2D_.Lambda.Iyj6QpN_2D_ZLoXueXenKuJvDVzcmI(this, usermanager)).show();
    }
}
