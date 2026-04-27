// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import android.content.Context;
import com.google.common.base.Strings;
import com.google.common.primitives.Booleans;
import com.lumiyaviewer.lumiya.slproto.chat.generic.SLChatEvent;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.chat.ChatterDisplayInfo;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatterItemViewBuilder;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadMessageInfo, UserManager

public class ChatterDisplayData
    implements ChatterDisplayInfo, Comparable
{

    public final ChatterID chatterID;
    public final String displayName;
    final float distanceToUser;
    final boolean isOnline;
    private final SLChatEvent lastMessage;
    private final int unreadCount;
    private final boolean voiceActive;

    ChatterDisplayData(ChatterID chatterid, String s, boolean flag, int i, SLChatEvent slchatevent, float f, boolean flag1)
    {
        chatterID = chatterid;
        displayName = s;
        isOnline = flag;
        unreadCount = i;
        lastMessage = slchatevent;
        distanceToUser = f;
        voiceActive = flag1;
    }

    public void buildView(Context context, ChatterItemViewBuilder chatteritemviewbuilder, UserManager usermanager)
    {
        chatteritemviewbuilder.setLabel(displayName);
        chatteritemviewbuilder.setThumbnailChatterID(chatterID, displayName);
        chatteritemviewbuilder.setOnlineStatusIcon(isOnline, isOnline);
        chatteritemviewbuilder.setUnreadCount(unreadCount);
        chatteritemviewbuilder.setVoiceActive(voiceActive);
        if (lastMessage != null)
        {
            chatteritemviewbuilder.setLastMessage(lastMessage.getPlainTextMessage(context, usermanager, true).toString());
        } else
        {
            chatteritemviewbuilder.setLastMessage(null);
        }
        chatteritemviewbuilder.setDistance(distanceToUser);
    }

    public int compareTo(ChatterDisplayData chatterdisplaydata)
    {
        int i = Booleans.compare(Strings.isNullOrEmpty(displayName), Strings.isNullOrEmpty(chatterdisplaydata.displayName));
        if (i != 0)
        {
            return i;
        }
        String s;
        String s1;
        if (displayName != null)
        {
            s = displayName;
        } else
        {
            s = "";
        }
        if (chatterdisplaydata.displayName != null)
        {
            s1 = chatterdisplaydata.displayName;
        } else
        {
            s1 = "";
        }
        i = s.compareTo(s1);
        if (i != 0)
        {
            return i;
        } else
        {
            return chatterID.compareTo(chatterdisplaydata.chatterID);
        }
    }

    public volatile int compareTo(Object obj)
    {
        return compareTo((ChatterDisplayData)obj);
    }

    public ChatterID getChatterID(UserManager usermanager)
    {
        return chatterID;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    ChatterDisplayData withDisplayName(String s)
    {
        return new ChatterDisplayData(chatterID, s, isOnline, unreadCount, lastMessage, distanceToUser, voiceActive);
    }

    ChatterDisplayData withDistanceToUser(float f)
    {
        return new ChatterDisplayData(chatterID, displayName, isOnline, unreadCount, lastMessage, f, voiceActive);
    }

    ChatterDisplayData withOnlineStatus(boolean flag)
    {
        return new ChatterDisplayData(chatterID, displayName, flag, unreadCount, lastMessage, distanceToUser, voiceActive);
    }

    ChatterDisplayData withUnreadInfo(UnreadMessageInfo unreadmessageinfo)
    {
        return new ChatterDisplayData(chatterID, displayName, isOnline, unreadmessageinfo.unreadCount(), unreadmessageinfo.lastMessage(), distanceToUser, voiceActive);
    }

    ChatterDisplayData withVoiceActive(boolean flag)
    {
        return new ChatterDisplayData(chatterID, displayName, isOnline, unreadCount, lastMessage, distanceToUser, flag);
    }
}
