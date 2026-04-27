// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextBoxDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            ChatEventViewHolder

public class ChatTextBoxViewHolder extends ChatEventViewHolder
    implements android.view.View.OnClickListener, android.view.View.OnKeyListener, android.view.View.OnFocusChangeListener
{

    private final Button dialogButtonIgnore;
    public final View dialogButtonsLayout;
    public final TextView dialogResultTextView;
    private final EditText textBox;
    private SLChatTextBoxDialog textBoxEvent;
    private final Button textBoxSend;

    ChatTextBoxViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
    {
        super(view, adapter);
        textBoxEvent = null;
        dialogResultTextView = (TextView)view.findViewById(0x7f10012a);
        dialogButtonsLayout = view.findViewById(0x7f10012b);
        textBoxSend = (Button)view.findViewById(0x7f100139);
        dialogButtonIgnore = (Button)view.findViewById(0x7f100129);
        textBox = (EditText)view.findViewById(0x7f100138);
        if (textBoxSend != null)
        {
            textBoxSend.setOnClickListener(this);
        }
        if (dialogButtonIgnore != null)
        {
            dialogButtonIgnore.setOnClickListener(this);
        }
        if (textBox != null)
        {
            textBox.setOnKeyListener(this);
            textBox.setOnFocusChangeListener(this);
        }
    }

    public void onClick(View view)
    {
        view.getId();
        JVM INSTR lookupswitch 2: default 32
    //                   2131755305: 33
    //                   2131755321: 62;
           goto _L1 _L2 _L3
_L1:
        return;
_L2:
        if (textBoxEvent != null)
        {
            textBoxEvent.onDialogIgnored(UserManager.getUserManager(textBoxEvent.getAgentUUID()));
            requestAdapterUpdate();
            return;
        }
        continue; /* Loop/switch isn't completed */
_L3:
        if (textBox.getVisibility() == 0)
        {
            if (textBoxEvent != null)
            {
                textBoxEvent.onEnteredText(UserManager.getUserManager(textBoxEvent.getAgentUUID()), textBox.getText().toString());
                requestAdapterUpdate();
                return;
            }
        } else
        {
            textBox.setVisibility(0);
            textBox.requestFocus();
            textBoxSend.setText(0x7f090354);
            return;
        }
        if (true) goto _L1; else goto _L4
_L4:
    }

    public void onFocusChange(View view, boolean flag)
    {
        if (view == textBox && textBox != null && !flag)
        {
            textBox.setVisibility(4);
            textBoxSend.setText(0x7f090353);
        }
    }

    public boolean onKey(View view, int i, KeyEvent keyevent)
    {
        if (keyevent.getAction() == 0 && i == 66 && view.getId() == 0x7f100138)
        {
            if (textBoxEvent != null)
            {
                textBoxEvent.onEnteredText(UserManager.getUserManager(textBoxEvent.getAgentUUID()), textBox.getText().toString());
                requestAdapterUpdate();
            }
            return true;
        } else
        {
            return false;
        }
    }

    public void setTextBoxEvent(SLChatTextBoxDialog slchattextboxdialog)
    {
        if (textBoxEvent != slchattextboxdialog)
        {
            textBoxEvent = slchattextboxdialog;
            if (textBox != null)
            {
                textBox.clearFocus();
                textBox.setVisibility(4);
                textBoxSend.setText(0x7f090353);
                textBox.setText(null);
            }
        }
    }
}
