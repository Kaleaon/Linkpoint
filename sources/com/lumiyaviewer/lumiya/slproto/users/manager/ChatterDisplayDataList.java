// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            SortedChatterList, ChatterSubscription, ChatterUserSubscription, ChatterGroupSubscription, 
//            UserManager, OnListUpdated

abstract class ChatterDisplayDataList
{

    private final Map chatterSubscriptions = new HashMap();
    private final SortedChatterList chatters;
    private final AtomicBoolean needsRefresh = new AtomicBoolean(false);
    private final Runnable refreshRunnable = new _2D_.Lambda.n0kmAon3UDOV6Jcsw0ejXq6u0xA(this);
    protected final UserManager userManager;

    ChatterDisplayDataList(UserManager usermanager, OnListUpdated onlistupdated, Comparator comparator)
    {
        userManager = usermanager;
        chatters = new SortedChatterList(onlistupdated, comparator);
    }

    private void refreshList()
    {
        Iterator iterator2;
        for (Iterator iterator = chatterSubscriptions.values().iterator(); iterator.hasNext();)
        {
            ((ChatterSubscription)iterator.next()).isValid = false;
        }

        iterator2 = getChatters().iterator();
_L9:
        if (!iterator2.hasNext()) goto _L2; else goto _L1
_L1:
        Object obj;
        Object obj1;
        ChatterID chatterid;
        chatterid = (ChatterID)iterator2.next();
        obj = (ChatterSubscription)chatterSubscriptions.get(chatterid);
        obj1 = obj;
        if (obj != null) goto _L4; else goto _L3
_L3:
        if (!(chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)) goto _L6; else goto _L5
_L5:
        obj = new ChatterUserSubscription(chatters, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid, userManager);
_L7:
        obj1 = obj;
        if (obj != null)
        {
            chatterSubscriptions.put(chatterid, obj);
            obj1 = obj;
        }
_L4:
        if (obj1 != null)
        {
            obj1.isValid = true;
        }
        continue; /* Loop/switch isn't completed */
_L6:
        if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
        {
            obj = new ChatterGroupSubscription(chatters, (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid, userManager);
        }
        if (true) goto _L7; else goto _L2
_L2:
        Iterator iterator1 = chatterSubscriptions.entrySet().iterator();
        do
        {
            if (!iterator1.hasNext())
            {
                break;
            }
            ChatterSubscription chattersubscription = (ChatterSubscription)((java.util.Map.Entry)iterator1.next()).getValue();
            if (!chattersubscription.isValid)
            {
                iterator1.remove();
                chattersubscription.dispose();
            }
        } while (true);
        Debug.Printf("FriendList: refreshList: %d subscriptions", new Object[] {
            Integer.valueOf(chatterSubscriptions.size())
        });
        return;
        if (true) goto _L9; else goto _L8
_L8:
    }

    public void dispose()
    {
        for (Iterator iterator = chatterSubscriptions.values().iterator(); iterator.hasNext(); ((ChatterSubscription)iterator.next()).unsubscribe()) { }
    }

    public ImmutableList getChatterList()
    {
        return chatters.getChatterList();
    }

    protected abstract List getChatters();

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterDisplayDataList_2957()
    {
        needsRefresh.set(false);
        refreshList();
    }

    void requestRefresh(Executor executor)
    {
label0:
        {
            Debug.Printf("FriendList: requestRefresh: needsRefresh = %s", new Object[] {
                Boolean.toString(needsRefresh.get())
            });
            if (!needsRefresh.getAndSet(true))
            {
                if (executor == null)
                {
                    break label0;
                }
                executor.execute(refreshRunnable);
            }
            return;
        }
        refreshRunnable.run();
    }
}
