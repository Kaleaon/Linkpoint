// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.avatar.SLScriptPermissions;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptQuestion;
import com.lumiyaviewer.lumiya.slproto.modules.SLAvatarControl;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSource;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLChatPermissionRequestEvent extends SLChatYesNoEvent
{

    private final UUID ItemID;
    private final String ObjectOwner;
    private final int Questions;

    public SLChatPermissionRequestEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        ItemID = chatmessage.getItemID();
        ObjectOwner = chatmessage.getItemName();
        Questions = chatmessage.getQuestionMask().intValue();
    }

    public SLChatPermissionRequestEvent(ScriptQuestion scriptquestion, UUID uuid)
    {
        int j = 0;
        super(new ChatMessageSourceObject(scriptquestion.Data_Field.TaskID, SLMessage.stringFromVariableOEM(scriptquestion.Data_Field.ObjectName)), uuid, null);
        ObjectOwner = SLMessage.stringFromVariableOEM(scriptquestion.Data_Field.ObjectOwner);
        ItemID = scriptquestion.Data_Field.ItemID;
        uuid = SLScriptPermissions.values();
        int l = uuid.length;
        for (int i = 0; i < l;)
        {
            SLScriptPermissions slscriptpermissions = uuid[i];
            int k = j;
            if ((scriptquestion.Data_Field.Questions & slscriptpermissions.getPermMask()) != 0)
            {
                k = j | slscriptpermissions.getPermMask();
            }
            i++;
            j = k;
        }

        Questions = j;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.PermissionRequest;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f09026f);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f09026d);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f090270);
    }

    public int getQuestions()
    {
        return Questions;
    }

    public String getText(Context context, UserManager usermanager)
    {
        usermanager = "";
        SLScriptPermissions aslscriptpermissions[] = SLScriptPermissions.values();
        int j = aslscriptpermissions.length;
        for (int i = 0; i < j;)
        {
            SLScriptPermissions slscriptpermissions = aslscriptpermissions[i];
            Object obj = usermanager;
            if ((Questions & slscriptpermissions.getPermMask()) != 0)
            {
                obj = usermanager;
                if (!usermanager.equals(""))
                {
                    obj = (new StringBuilder()).append(usermanager).append(", ").toString();
                }
                obj = (new StringBuilder()).append(((String) (obj))).append(slscriptpermissions.getMessage()).toString();
            }
            i++;
            usermanager = ((UserManager) (obj));
        }

        return context.getString(0x7f09026e, new Object[] {
            ObjectOwner, usermanager
        });
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f090271);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f09026c);
    }

    public boolean isObjectPopup()
    {
        return true;
    }

    protected void onNoAction(Context context, UserManager usermanager)
    {
        super.onNoAction(context, usermanager);
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        context = usermanager.getActiveAgentCircuit();
        if (context != null)
        {
            context.getModules().avatarControl.ScriptAnswerYes(ItemID, source.getSourceUUID(), Questions);
        }
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setItemID(ItemID);
        chatmessage.setItemName(ObjectOwner);
        chatmessage.setQuestionMask(Integer.valueOf(Questions));
    }
}
