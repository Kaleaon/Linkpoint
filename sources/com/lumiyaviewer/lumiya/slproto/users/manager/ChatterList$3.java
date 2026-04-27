// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.Map;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterList, ChatterListType, ChatterDisplayDataList, UserManager, 
//            ActiveChattersManager, FriendManager, GroupManager, NearbyChattersDisplayDataList

class val.userManager extends RequestFinalProcessor
{

    private static final int _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues[];
    final int $SWITCH_TABLE$com$lumiyaviewer$lumiya$slproto$users$manager$ChatterListType[];
    final ChatterList this$0;
    final UserManager val$userManager;

    private static int[] _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues()
    {
        if (_2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues != null)
        {
            return _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues;
        }
        int ai[] = new int[ChatterListType.values().length];
        try
        {
            ai[ChatterListType.Active.ordinal()] = 1;
        }
        catch (NoSuchFieldError nosuchfielderror4) { }
        try
        {
            ai[ChatterListType.Friends.ordinal()] = 2;
        }
        catch (NoSuchFieldError nosuchfielderror3) { }
        try
        {
            ai[ChatterListType.FriendsOnline.ordinal()] = 3;
        }
        catch (NoSuchFieldError nosuchfielderror2) { }
        try
        {
            ai[ChatterListType.Groups.ordinal()] = 4;
        }
        catch (NoSuchFieldError nosuchfielderror1) { }
        try
        {
            ai[ChatterListType.Nearby.ordinal()] = 5;
        }
        catch (NoSuchFieldError nosuchfielderror) { }
        _2D_com_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues = ai;
        return ai;
    }

    protected void cancelRequest(ChatterListType chatterlisttype)
    {
        chatterlisttype = (ChatterDisplayDataList)ChatterList._2D_get1(ChatterList.this).remove(chatterlisttype);
        if (chatterlisttype != null)
        {
            chatterlisttype.dispose();
        }
    }

    protected volatile void cancelRequest(Object obj)
    {
        cancelRequest((ChatterListType)obj);
    }

    protected ImmutableList processRequest(ChatterListType chatterlisttype)
    {
        Object obj;
        ChatterDisplayDataList chatterdisplaydatalist;
        obj = (ChatterDisplayDataList)ChatterList._2D_get1(ChatterList.this).get(chatterlisttype);
        chatterdisplaydatalist = ((ChatterDisplayDataList) (obj));
        if (obj != null) goto _L2; else goto _L1
_L1:
        _2D_getcom_2D_lumiyaviewer_2D_lumiya_2D_slproto_2D_users_2D_manager_2D_ChatterListTypeSwitchesValues()[chatterlisttype.ordinal()];
        JVM INSTR tableswitch 1 5: default 64
    //                   1 97
    //                   2 111
    //                   3 125
    //                   4 139
    //                   5 153;
           goto _L3 _L4 _L5 _L6 _L7 _L8
_L3:
        ((ChatterDisplayDataList) (obj)).requestRefresh(val$userManager.getDatabaseExecutor());
        ChatterList._2D_get1(ChatterList.this).put(chatterlisttype, obj);
        chatterdisplaydatalist = ((ChatterDisplayDataList) (obj));
_L2:
        return chatterdisplaydatalist.getChatterList();
_L4:
        obj = ChatterList._2D_get0(ChatterList.this).getActiveChattersList();
        continue; /* Loop/switch isn't completed */
_L5:
        obj = ChatterList._2D_get2(ChatterList.this).getFriendList();
        continue; /* Loop/switch isn't completed */
_L6:
        obj = ChatterList._2D_get2(ChatterList.this).getFriendsOnlineList();
        continue; /* Loop/switch isn't completed */
_L7:
        obj = ChatterList._2D_get3(ChatterList.this).getGroupList();
        continue; /* Loop/switch isn't completed */
_L8:
        obj = new NearbyChattersDisplayDataList(val$userManager, ChatterList._2D_get4(ChatterList.this));
        if (true) goto _L3; else goto _L9
_L9:
    }

    protected volatile Object processRequest(Object obj)
        throws Throwable
    {
        return processRequest((ChatterListType)obj);
    }

    e(Executor executor, UserManager usermanager)
    {
        this$0 = final_chatterlist;
        val$userManager = usermanager;
        super(RequestSource.this, executor);
    }
}
