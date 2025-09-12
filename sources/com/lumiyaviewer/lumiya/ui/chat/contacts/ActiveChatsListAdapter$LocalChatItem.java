// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadMessageInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ActiveChatsListAdapter, ChatterItemViewBuilder

private class <init>
    implements ChatterDisplayInfo
{

    private final ChatterID chatterID;
    final ActiveChatsListAdapter this$0;
    private UnreadMessageInfo unreadMessageInfo;
    private VoiceChatInfo voiceChatInfo;

    public void buildView(Context context, ChatterItemViewBuilder chatteritemviewbuilder, UserManager usermanager)
    {
        boolean flag1 = false;
        Object obj = new StringBuilder(context.getString(0x7f090191));
        int i;
        if (ActiveChatsListAdapter._2D_get1(ActiveChatsListAdapter.this) != null)
        {
            ((StringBuilder) (obj)).append(": ");
            i = ActiveChatsListAdapter._2D_get1(ActiveChatsListAdapter.this).inChatRangeUsers();
            boolean flag;
            if (i != 0)
            {
                ((StringBuilder) (obj)).append(context.getString(0x7f090310, new Object[] {
                    Integer.valueOf(i)
                }));
            } else
            {
                ((StringBuilder) (obj)).append(context.getString(0x7f0901eb));
            }
        }
        if (unreadMessageInfo != null)
        {
            i = unreadMessageInfo.unreadCount();
        } else
        {
            i = 0;
        }
        chatteritemviewbuilder.setUnreadCount(i);
        chatteritemviewbuilder.setLabel(((StringBuilder) (obj)).toString());
        chatteritemviewbuilder.setThumbnailDefaultIcon(0x7f01000b);
        chatteritemviewbuilder.setThumbnailChatterID(chatterID, null);
        if (unreadMessageInfo != null)
        {
            obj = unreadMessageInfo.lastMessage();
        } else
        {
            obj = null;
        }
        if (obj != null)
        {
            context = ((SLChatEvent) (obj)).getPlainTextMessage(context, usermanager, false).toString();
        } else
        {
            context = null;
        }
        chatteritemviewbuilder.setLastMessage(context);
        flag = flag1;
        if (voiceChatInfo != null)
        {
            flag = flag1;
            if (voiceChatInfo.state != com.lumiyaviewer.lumiya.voice.common.model.iceChatInfo)
            {
                flag = true;
            }
        }
        chatteritemviewbuilder.setVoiceActive(flag);
    }

    public ChatterID getChatterID(UserManager usermanager)
    {
        return chatterID;
    }

    public String getDisplayName()
    {
        return ActiveChatsListAdapter._2D_get0(ActiveChatsListAdapter.this).getString(0x7f090191);
    }

    public void setUnreadInfo(UnreadMessageInfo unreadmessageinfo)
    {
        unreadMessageInfo = unreadmessageinfo;
    }

    public void setVoiceChatInfo(VoiceChatInfo voicechatinfo)
    {
        voiceChatInfo = voicechatinfo;
    }

    private (ChatterID chatterid)
    {
        this$0 = ActiveChatsListAdapter.this;
        super();
        chatterID = chatterid;
    }

    chatterID(ChatterID chatterid, chatterID chatterid1)
    {
        this(chatterid);
    }
}
