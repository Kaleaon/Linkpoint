// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.ChatMessage;
import com.lumiyaviewer.lumiya.dao.ChatMessageDao;
import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ActiveChattersManager, UserManager, UnreadMessageInfo

class val.userManager extends RequestFinalProcessor
{

    final ActiveChattersManager this$0;
    final UserManager val$userManager;

    protected UnreadMessageInfo processRequest(ChatterID chatterid)
        throws Throwable
    {
        Chatter chatter = getChatter(chatterid);
        if (chatter == null) goto _L2; else goto _L1
_L1:
        if (chatter.getLastMessageID() == null) goto _L4; else goto _L3
_L3:
        chatterid = (ChatMessage)ActiveChattersManager._2D_get0(ActiveChattersManager.this).load(chatter.getLastMessageID());
        if (chatterid == null) goto _L4; else goto _L5
_L5:
        chatterid = SLChatEvent.loadFromDatabaseObject(chatterid, val$userManager.getUserID());
_L7:
        return UnreadMessageInfo.create(chatter.getUnreadCount(), chatterid);
_L2:
        return UnreadMessageInfo.create(0, null);
_L4:
        chatterid = null;
        if (true) goto _L7; else goto _L6
_L6:
    }

    protected volatile Object processRequest(Object obj)
        throws Throwable
    {
        return processRequest((ChatterID)obj);
    }

    (Executor executor, UserManager usermanager)
    {
        this$0 = final_activechattersmanager;
        val$userManager = usermanager;
        super(RequestSource.this, executor);
    }
}
