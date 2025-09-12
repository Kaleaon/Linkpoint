// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ImprovedInstantMessage;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.UUID;

public final class SLChatGroupInvitationEvent extends SLChatYesNoEvent
{

    private final UUID groupID;
    private final int joinFee;
    private final UUID sessionID;

    public SLChatGroupInvitationEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        joinFee = chatmessage.getTransactionAmount().intValue();
        sessionID = chatmessage.getSessionID();
        groupID = chatmessage.getItemID();
    }

    public SLChatGroupInvitationEvent(ChatMessageSource chatmessagesource, UUID uuid, ImprovedInstantMessage improvedinstantmessage)
    {
        super(chatmessagesource, uuid, improvedinstantmessage, null);
        groupID = improvedinstantmessage.AgentData_Field.AgentID;
        sessionID = improvedinstantmessage.MessageBlock_Field.ID;
        if (improvedinstantmessage.MessageBlock_Field.BinaryBucket.length < 4)
        {
            joinFee = 0;
            return;
        } else
        {
            chatmessagesource = ByteBuffer.wrap(improvedinstantmessage.MessageBlock_Field.BinaryBucket);
            chatmessagesource.order(ByteOrder.BIG_ENDIAN);
            joinFee = chatmessagesource.getInt();
            return;
        }
    }

    private void DoAcceptGroupInvite(UUID uuid, UUID uuid1, boolean flag)
    {
        Object obj = UserManager.getUserManager(agentUUID);
        if (obj != null)
        {
            obj = ((UserManager) (obj)).getActiveAgentCircuit();
            if (obj != null)
            {
                ((SLAgentCircuit) (obj)).getModules().groupManager.AcceptGroupInvite(uuid, uuid1, flag);
            }
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_chat_SLChatGroupInvitationEvent_3762(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.GroupInvitation;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090186);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f090183);
    }

    public String getQuestion(Context context)
    {
        if (joinFee == 0)
        {
            return context.getString(0x7f090189);
        } else
        {
            return context.getString(0x7f09018c, new Object[] {
                Integer.valueOf(joinFee)
            });
        }
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f09018d);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f090181);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_chat_SLChatGroupInvitationEvent_3561(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        DoAcceptGroupInvite(groupID, sessionID, true);
    }

    protected void onNoAction(Context context, UserManager usermanager)
    {
        super.onNoAction(context, usermanager);
        DoAcceptGroupInvite(groupID, sessionID, false);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        if (joinFee == 0)
        {
            DoAcceptGroupInvite(groupID, sessionID, true);
            return;
        } else
        {
            usermanager = new android.app.AlertDialog.Builder(context);
            usermanager.setMessage(context.getString(0x7f090182, new Object[] {
                Integer.valueOf(joinFee)
            })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.hXLxI3fDexZfuKx5RzOoCtsGy3I._cls1(this)).setNegativeButton("No", new _2D_.Lambda.hXLxI3fDexZfuKx5RzOoCtsGy3I());
            usermanager.create().show();
            return;
        }
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setTransactionAmount(Integer.valueOf(joinFee));
        chatmessage.setSessionID(sessionID);
        chatmessage.setItemID(groupID);
    }
}
