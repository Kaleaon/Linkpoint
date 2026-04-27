// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.chat.generic:
//            ChatEventViewHolder

public class ChatScriptDialogViewHolder extends ChatEventViewHolder
    implements android.view.View.OnClickListener
{

    private static final int dialogButtonIds[] = {
        0x7f100135, 0x7f100136, 0x7f100137, 0x7f100132, 0x7f100133, 0x7f100134, 0x7f10012f, 0x7f100130, 0x7f100131, 0x7f10012c, 
        0x7f10012d, 0x7f10012e
    };
    public final CardView cardView;
    public final Button dialogButtonIgnore;
    public final Button dialogButtons[];
    public final View dialogButtonsLayout;
    private SLChatScriptDialog dialogEvent;
    public final TextView dialogResultTextView;

    ChatScriptDialogViewHolder(View view, android.support.v7.widget.RecyclerView.Adapter adapter)
    {
        super(view, adapter);
        dialogResultTextView = (TextView)view.findViewById(0x7f10012a);
        dialogButtonsLayout = view.findViewById(0x7f10012b);
        cardView = (CardView)view.findViewById(0x7f100127);
        dialogButtons = new Button[dialogButtonIds.length];
        for (int i = 0; i < dialogButtonIds.length; i++)
        {
            dialogButtons[i] = (Button)view.findViewById(dialogButtonIds[i]);
            if (dialogButtons[i] != null)
            {
                dialogButtons[i].setOnClickListener(this);
            }
        }

        dialogButtonIgnore = (Button)view.findViewById(0x7f100129);
        if (dialogButtonIgnore != null)
        {
            dialogButtonIgnore.setOnClickListener(this);
        }
    }

    public void onClick(View view)
    {
        view.getId();
        JVM INSTR tableswitch 2131755305 2131755305: default 24
    //                   2131755305 76;
           goto _L1 _L2
_L1:
        int i = 0;
_L9:
        if (i >= dialogButtonIds.length) goto _L4; else goto _L3
_L3:
        if (view.getId() != dialogButtonIds[i]) goto _L6; else goto _L5
_L5:
        if (dialogEvent != null)
        {
            dialogEvent.onDialogButton(UserManager.getUserManager(dialogEvent.getAgentUUID()), i);
            requestAdapterUpdate();
        }
_L4:
        return;
_L2:
        if (dialogEvent == null) goto _L4; else goto _L7
_L7:
        dialogEvent.onDialogIgnored(UserManager.getUserManager(dialogEvent.getAgentUUID()));
        requestAdapterUpdate();
        return;
_L6:
        i++;
        if (true) goto _L9; else goto _L8
_L8:
    }

    public void setDialogEvent(SLChatScriptDialog slchatscriptdialog)
    {
        dialogEvent = slchatscriptdialog;
    }

}
