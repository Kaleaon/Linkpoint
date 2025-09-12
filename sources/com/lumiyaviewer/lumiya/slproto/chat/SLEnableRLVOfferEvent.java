// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import android.content.SharedPreferences;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.messages.ChatFromSimulator;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceObject;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLEnableRLVOfferEvent extends SLChatYesNoEvent
{

    public SLEnableRLVOfferEvent(ChatMessage chatmessage, UUID uuid)
    {
        super(chatmessage, uuid);
    }

    public SLEnableRLVOfferEvent(ChatFromSimulator chatfromsimulator, UUID uuid)
    {
        super(new ChatMessageSourceObject(chatfromsimulator.ChatData_Field.SourceID, SLMessage.stringFromVariableOEM(chatfromsimulator.ChatData_Field.FromName)), uuid, SLMessage.stringFromVariableUTF(chatfromsimulator.ChatData_Field.Message));
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.EnableRLVOffer;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090110);
    }

    public String getNoMessage(Context context)
    {
        return context.getString(0x7f09010f);
    }

    public String getQuestion(Context context)
    {
        return context.getString(0x7f090111);
    }

    public String getText(Context context, UserManager usermanager)
    {
        return context.getString(0x7f0902aa);
    }

    public String getYesButton(Context context)
    {
        return context.getString(0x7f090112);
    }

    public String getYesMessage(Context context)
    {
        return context.getString(0x7f09010e);
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
        context = LumiyaApp.getDefaultSharedPreferences().edit();
        context.putBoolean("rlv_enabled", true);
        context.commit();
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
    }
}
