// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager, ChatterListType, ChatterList, FriendDisplayDataList, 
//            OnListUpdated, ChatterDisplayDataList

public class FriendManager
{

    private final ChatterList chatterList;
    private final FriendDao friendDao;
    private final OnListUpdated onFriendListUpdated = new OnListUpdated() {

        final FriendManager this$0;

        public void onListUpdated()
        {
            FriendManager._2D_get0(FriendManager.this).notifyListUpdated(ChatterListType.Friends);
        }

            
            {
                this$0 = FriendManager.this;
                super();
            }
    };
    private final OnListUpdated onFriendsOnlineListUpdated = new OnListUpdated() {

        final FriendManager this$0;

        public void onListUpdated()
        {
            FriendManager._2D_get0(FriendManager.this).notifyListUpdated(ChatterListType.FriendsOnline);
        }

            
            {
                this$0 = FriendManager.this;
                super();
            }
    };
    private final SubscriptionPool onlineStatus = new SubscriptionPool();
    private final UserManager userManager;

    static ChatterList _2D_get0(FriendManager friendmanager)
    {
        return friendmanager.chatterList;
    }

    static FriendDao _2D_get1(FriendManager friendmanager)
    {
        return friendmanager.friendDao;
    }

    public FriendManager(UserManager usermanager, DaoSession daosession, ChatterList chatterlist)
    {
        userManager = usermanager;
        friendDao = daosession.getFriendDao();
        chatterList = chatterlist;
        new RequestFinalProcessor(onlineStatus, usermanager.getDatabaseExecutor()) {

            final FriendManager this$0;

            protected Boolean processRequest(UUID uuid)
            {
                uuid = (Friend)FriendManager._2D_get1(FriendManager.this).load(uuid);
                if (uuid != null)
                {
                    return Boolean.valueOf(uuid.getIsOnline());
                } else
                {
                    return Boolean.valueOf(false);
                }
            }

            protected volatile Object processRequest(Object obj)
                throws Throwable
            {
                return processRequest((UUID)obj);
            }

            
            {
                this$0 = FriendManager.this;
                super(requestsource, executor);
            }
        };
    }

    public void addFriend(UUID uuid)
    {
        if ((Friend)friendDao.load(uuid) == null)
        {
            uuid = new Friend(uuid, 1, 1, false);
            friendDao.insert(uuid);
        }
        chatterList.updateList(ChatterListType.Friends);
        chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public Friend getFriend(UUID uuid)
    {
        if (uuid != null)
        {
            return (Friend)friendDao.load(uuid);
        } else
        {
            return null;
        }
    }

    public ChatterDisplayDataList getFriendList()
    {
        return new FriendDisplayDataList(userManager, onFriendListUpdated, false);
    }

    public ChatterDisplayDataList getFriendsOnlineList()
    {
        return new FriendDisplayDataList(userManager, onFriendsOnlineListUpdated, true);
    }

    public Subscribable getOnlineStatus()
    {
        return onlineStatus;
    }

    public void removeFriend(UUID uuid)
    {
        friendDao.deleteByKey(uuid);
        chatterList.updateList(ChatterListType.Friends);
        chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public void setUsersOnline(List list, boolean flag)
    {
        UUID uuid;
        for (list = list.iterator(); list.hasNext(); onlineStatus.requestUpdate(uuid))
        {
            uuid = (UUID)list.next();
            Friend friend = (Friend)friendDao.load(uuid);
            if (friend != null)
            {
                friend.setIsOnline(flag);
                friendDao.update(friend);
            }
        }

        chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public void updateFriendList(ImmutableList immutablelist)
    {
        HashSet hashset;
        hashset = new HashSet();
        immutablelist = immutablelist.iterator();
_L4:
        UUID uuid;
        Object obj;
        Friend friend1;
        if (!immutablelist.hasNext())
        {
            break; /* Loop/switch isn't completed */
        }
        obj = (com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend)immutablelist.next();
        uuid = ((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).uuid;
        friend1 = (Friend)friendDao.load(uuid);
        if (friend1 != null) goto _L2; else goto _L1
_L1:
        obj = new Friend(uuid, ((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsGiven, ((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsHas, false);
        friendDao.insertOrReplace(obj);
_L5:
        hashset.add(uuid);
        if (true) goto _L4; else goto _L3
_L6:
        friend1.setRightsGiven(((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsGiven);
        friend1.setRightsHas(((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsHas);
        friend1.setIsOnline(false);
        friendDao.update(friend1);
          goto _L5
_L2:
        if (friend1.getRightsGiven() == ((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsGiven && friend1.getRightsHas() == ((com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply.Friend) (obj)).rightsHas && !friend1.getIsOnline()) goto _L5; else goto _L6
_L3:
        immutablelist = friendDao.loadAll();
        Debug.Printf("FriendList: update[1], got %d friends", new Object[] {
            Integer.valueOf(immutablelist.size())
        });
        immutablelist = immutablelist.iterator();
        do
        {
            if (!immutablelist.hasNext())
            {
                break;
            }
            Friend friend = (Friend)immutablelist.next();
            if (!hashset.contains(friend.getUuid()))
            {
                friendDao.delete(friend);
            }
        } while (true);
        chatterList.updateList(ChatterListType.Friends);
        chatterList.updateList(ChatterListType.FriendsOnline);
        return;
    }
}
