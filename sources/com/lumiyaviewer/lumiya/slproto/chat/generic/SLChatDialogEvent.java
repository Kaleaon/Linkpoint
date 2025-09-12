// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat.generic;

import android.content.Context;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.SLChatTextEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ScriptDialog;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public abstract class SLChatDialogEvent extends SLChatTextEvent
{

    protected final int chatChannel;
    protected boolean ignored;

    public SLChatDialogEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
        ignored = false;
        chatChannel = chatmessage.getChatChannel().intValue();
        ignored = chatmessage.getDialogIgnored().booleanValue();
    }

    public SLChatDialogEvent(ScriptDialog scriptdialog, UUID uuid)
    {
        super(new ChatMessageSourceObject(scriptdialog.Data_Field.ObjectID, SLMessage.stringFromVariableOEM(scriptdialog.Data_Field.ObjectName)), uuid, sanitizeDialogText(SLMessage.stringFromVariableUTF(scriptdialog.Data_Field.Message)));
        ignored = false;
        chatChannel = scriptdialog.Data_Field.ChatChannel;
    }

    private static String sanitizeDialogText(String s)
    {
        for (; s.contains("\n\n"); s = s.replace("\n\n", "\n")) { }
        return s.trim();
    }

    protected void onDialogIgnored(UserManager usermanager)
    {
        ignored = true;
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setChatChannel(Integer.valueOf(chatChannel));
        chatmessage.setDialogIgnored(Boolean.valueOf(ignored));
    }

    public abstract void showDialog(Context context, UserManager usermanager);
}
