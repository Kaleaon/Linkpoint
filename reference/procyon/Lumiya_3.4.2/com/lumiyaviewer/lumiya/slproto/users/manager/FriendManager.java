// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.Debug;
import java.util.HashSet;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.google.common.collect.ImmutableList;
import java.util.Iterator;
import java.util.List;
import com.lumiyaviewer.lumiya.react.Subscribable;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.dao.Friend;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import javax.annotation.Nonnull;

public class FriendManager
{
    @Nonnull
    private final ChatterList chatterList;
    @Nonnull
    private final FriendDao friendDao;
    private final OnListUpdated onFriendListUpdated;
    private final OnListUpdated onFriendsOnlineListUpdated;
    private final SubscriptionPool<UUID, Boolean> onlineStatus;
    @Nonnull
    private final UserManager userManager;
    
    public FriendManager(@Nonnull final UserManager userManager, @Nonnull final DaoSession daoSession, @Nonnull final ChatterList chatterList) {
        this.onlineStatus = new SubscriptionPool<UUID, Boolean>();
        this.onFriendListUpdated = new OnListUpdated() {
            @Override
            public void onListUpdated() {
                FriendManager.this.chatterList.notifyListUpdated(ChatterListType.Friends);
            }
        };
        this.onFriendsOnlineListUpdated = new OnListUpdated() {
            @Override
            public void onListUpdated() {
                FriendManager.this.chatterList.notifyListUpdated(ChatterListType.FriendsOnline);
            }
        };
        this.userManager = userManager;
        this.friendDao = daoSession.getFriendDao();
        this.chatterList = chatterList;
        new RequestFinalProcessor<UUID, Boolean>(this.onlineStatus, userManager.getDatabaseExecutor()) {
            @Override
            protected Boolean processRequest(@Nonnull final UUID uuid) {
                final Friend friend = FriendManager.this.friendDao.load(uuid);
                if (friend != null) {
                    return friend.getIsOnline();
                }
                return false;
            }
        };
    }
    
    public void addFriend(final UUID uuid) {
        if (this.friendDao.load(uuid) == null) {
            ((AbstractDao<Friend, K>)this.friendDao).insert(new Friend(uuid, 1, 1, false));
        }
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }
    
    public Friend getFriend(@Nullable final UUID uuid) {
        if (uuid != null) {
            return this.friendDao.load(uuid);
        }
        return null;
    }
    
    public ChatterDisplayDataList getFriendList() {
        return new FriendDisplayDataList(this.userManager, this.onFriendListUpdated, false);
    }
    
    public ChatterDisplayDataList getFriendsOnlineList() {
        return new FriendDisplayDataList(this.userManager, this.onFriendsOnlineListUpdated, true);
    }
    
    public Subscribable<UUID, Boolean> getOnlineStatus() {
        return this.onlineStatus;
    }
    
    public void removeFriend(final UUID uuid) {
        ((AbstractDao<T, UUID>)this.friendDao).deleteByKey(uuid);
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }
    
    public void setUsersOnline(final List<UUID> list, final boolean isOnline) {
        for (final UUID uuid : list) {
            final Friend friend = this.friendDao.load(uuid);
            if (friend != null) {
                friend.setIsOnline(isOnline);
                ((AbstractDao<Friend, K>)this.friendDao).update(friend);
            }
            this.onlineStatus.requestUpdate(uuid);
        }
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }
    
    public void updateFriendList(final ImmutableList<SLAuthReply.Friend> list) {
        final HashSet set = new HashSet();
        for (final SLAuthReply.Friend friend : list) {
            final UUID uuid = friend.uuid;
            final Friend friend2 = this.friendDao.load(uuid);
            if (friend2 == null) {
                ((AbstractDao<Friend, K>)this.friendDao).insertOrReplace(new Friend(uuid, friend.rightsGiven, friend.rightsHas, false));
            }
            else if (friend2.getRightsGiven() != friend.rightsGiven || friend2.getRightsHas() != friend.rightsHas || friend2.getIsOnline()) {
                friend2.setRightsGiven(friend.rightsGiven);
                friend2.setRightsHas(friend.rightsHas);
                friend2.setIsOnline(false);
                ((AbstractDao<Friend, K>)this.friendDao).update(friend2);
            }
            set.add(uuid);
        }
        final List<Friend> loadAll = this.friendDao.loadAll();
        Debug.Printf("FriendList: update[1], got %d friends", loadAll.size());
        for (final Friend friend3 : loadAll) {
            if (!set.contains(friend3.getUuid())) {
                ((AbstractDao<Friend, K>)this.friendDao).delete(friend3);
            }
        }
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }
}
