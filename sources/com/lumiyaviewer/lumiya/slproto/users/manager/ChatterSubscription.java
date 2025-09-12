// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayData, UserManager, ChatterList, ActiveChattersManager, 
//            SortedChatterList, UnreadMessageInfo

class ChatterSubscription
{

    private final SortedChatterList chatterList;
    ChatterDisplayData displayData;
    boolean isValid;
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onUnreadCount = new _2D_.Lambda.x6PlkRNg0xExeA_EUn8oEJWcOq8._cls1(this);
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onVoiceStatusChanged = new _2D_.Lambda.x6PlkRNg0xExeA_EUn8oEJWcOq8(this);
    private final Subscription unreadCountSubscription;
    private final Subscription voiceChatInfoSubscription;

    ChatterSubscription(SortedChatterList sortedchatterlist, ChatterID chatterid, UserManager usermanager)
    {
        chatterList = sortedchatterlist;
        displayData = new ChatterDisplayData(chatterid, null, false, 0, null, (0.0F / 0.0F), false);
        unreadCountSubscription = usermanager.getChatterList().getActiveChattersManager().getUnreadCounts().subscribe(chatterid, onUnreadCount);
        voiceChatInfoSubscription = usermanager.getVoiceChatInfo().subscribe(chatterid, onVoiceStatusChanged);
        isValid = true;
        sortedchatterlist.addChatter(displayData);
    }

    private void onUnreadCountChanged(UnreadMessageInfo unreadmessageinfo)
    {
        if (unreadmessageinfo != null)
        {
            setChatterDisplayData(displayData.withUnreadInfo(unreadmessageinfo));
        }
    }

    private void onVoiceChatInfoChanged(VoiceChatInfo voicechatinfo)
    {
        boolean flag1 = false;
        ChatterDisplayData chatterdisplaydata = displayData;
        boolean flag = flag1;
        if (voicechatinfo != null)
        {
            flag = flag1;
            if (voicechatinfo.state != com.lumiyaviewer.lumiya.voice.common.model.VoiceChatInfo.VoiceChatState.None)
            {
                flag = true;
            }
        }
        setChatterDisplayData(chatterdisplaydata.withVoiceActive(flag));
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscription_2D_mthref_2D_0(VoiceChatInfo voicechatinfo)
    {
        onVoiceChatInfoChanged(voicechatinfo);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterSubscription_2D_mthref_2D_1(UnreadMessageInfo unreadmessageinfo)
    {
        onUnreadCountChanged(unreadmessageinfo);
    }

    public void dispose()
    {
        unsubscribe();
        chatterList.removeChatter(displayData);
    }

    void setChatterDisplayData(ChatterDisplayData chatterdisplaydata)
    {
        ChatterDisplayData chatterdisplaydata1 = displayData;
        displayData = chatterdisplaydata;
        chatterList.replaceChatter(chatterdisplaydata1, displayData);
    }

    public void unsubscribe()
    {
        unreadCountSubscription.unsubscribe();
        voiceChatInfoSubscription.unsubscribe();
    }
}
