// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatYesNoEvent;
import com.lumiyaviewer.lumiya.slproto.users.chatsrc.ChatMessageSourceUnknown;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectPopupsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

public final class SLVoiceUpgradeEvent extends SLChatYesNoEvent
{

    private final boolean isInstall;
    private final String upgradeURL;

    public SLVoiceUpgradeEvent(ChatMessage chatmessage, UUID uuid)
    {
        boolean flag = false;
        super(chatmessage, uuid);
        upgradeURL = chatmessage.getItemName();
        if (chatmessage.getAssetType().intValue() != 0)
        {
            flag = true;
        }
        isInstall = flag;
    }

    public SLVoiceUpgradeEvent(UUID uuid, String s, boolean flag, String s1)
    {
        super(ChatMessageSourceUnknown.getInstance(), uuid, s);
        upgradeURL = s1;
        isInstall = flag;
    }

    protected com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType getMessageType()
    {
        return com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent.ChatMessageType.VoiceUpgrade;
    }

    public String getNoButton(Context context)
    {
        return context.getString(0x7f090386);
    }

    public String getNoMessage(Context context)
    {
        if (isInstall)
        {
            return context.getString(0x7f09037b);
        } else
        {
            return context.getString(0x7f090385);
        }
    }

    public String getQuestion(Context context)
    {
        if (isInstall)
        {
            return context.getString(0x7f09015d);
        } else
        {
            return context.getString(0x7f090365);
        }
    }

    public String getText(Context context, UserManager usermanager)
    {
        return text;
    }

    public String getYesButton(Context context)
    {
        if (isInstall)
        {
            return context.getString(0x7f09037c);
        } else
        {
            return context.getString(0x7f090387);
        }
    }

    public String getYesMessage(Context context)
    {
        return "";
    }

    public boolean isObjectPopup()
    {
        return false;
    }

    protected void onNoAction(Context context, UserManager usermanager)
    {
        super.onNoAction(context, usermanager);
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
    }

    public void onYesAction(Context context, UserManager usermanager)
    {
        super.onYesAction(context, usermanager);
        usermanager.getObjectPopupsManager().cancelObjectPopup(this);
        usermanager = new Intent("android.intent.action.VIEW");
        usermanager.setData(Uri.parse(upgradeURL));
        context.startActivity(usermanager);
    }

    public void serializeToDatabaseObject(ChatMessage chatmessage)
    {
        super.serializeToDatabaseObject(chatmessage);
        chatmessage.setItemName(upgradeURL);
        int i;
        if (isInstall)
        {
            i = 1;
        } else
        {
            i = 0;
        }
        chatmessage.setAssetType(Integer.valueOf(i));
    }
}
