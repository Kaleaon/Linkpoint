package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FriendManager {
    /* access modifiers changed from: private */
    @Nonnull
    public final ChatterList chatterList;
    /* access modifiers changed from: private */
    @Nonnull
    public final FriendDao friendDao;
    private final OnListUpdated onFriendListUpdated = new OnListUpdated() {
        public void onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.Friends);
        }
    };
    private final OnListUpdated onFriendsOnlineListUpdated = new OnListUpdated() {
        public void onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.FriendsOnline);
        }
    };
    private final SubscriptionPool<UUID, Boolean> onlineStatus = new SubscriptionPool<>();
    @Nonnull
    private final UserManager userManager;

    public FriendManager(@Nonnull UserManager userManager2, @Nonnull DaoSession daoSession, @Nonnull ChatterList chatterList2) {
        this.userManager = userManager2;
        this.friendDao = daoSession.getFriendDao();
        this.chatterList = chatterList2;
        new RequestFinalProcessor<UUID, Boolean>(this.onlineStatus, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            public Boolean processRequest(@Nonnull UUID uuid) {
                Friend friend = (Friend) FriendManager.this.friendDao.load(uuid);
                if (friend != null) {
                    return Boolean.valueOf(friend.getIsOnline());
                }
                return false;
            }
        };
    }

    public void addFriend(UUID uuid) {
        if (((Friend) this.friendDao.load(uuid)) == null) {
            this.friendDao.insert(new Friend(uuid, 1, 1, false));
        }
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public Friend getFriend(@Nullable UUID uuid) {
        if (uuid != null) {
            return (Friend) this.friendDao.load(uuid);
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

    public void removeFriend(UUID uuid) {
        this.friendDao.deleteByKey(uuid);
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public void setUsersOnline(List<UUID> list, boolean z) {
        for (UUID uuid : list) {
            Friend friend = (Friend) this.friendDao.load(uuid);
            if (friend != null) {
                friend.setIsOnline(z);
                this.friendDao.update(friend);
            }
            this.onlineStatus.requestUpdate(uuid);
        }
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }

    public void updateFriendList(ImmutableList<SLAuthReply.Friend> immutableList) {
        HashSet hashSet = new HashSet();
        for (SLAuthReply.Friend friend : immutableList) {
            UUID uuid = friend.uuid;
            Friend friend2 = (Friend) this.friendDao.load(uuid);
            if (friend2 == null) {
                this.friendDao.insertOrReplace(new Friend(uuid, friend.rightsGiven, friend.rightsHas, false));
            } else if (friend2.getRightsGiven() != friend.rightsGiven || friend2.getRightsHas() != friend.rightsHas || friend2.getIsOnline()) {
                friend2.setRightsGiven(friend.rightsGiven);
                friend2.setRightsHas(friend.rightsHas);
                friend2.setIsOnline(false);
                this.friendDao.update(friend2);
            }
            hashSet.add(uuid);
        }
        List<Friend> loadAll = this.friendDao.loadAll();
        Debug.Printf("FriendList: update[1], got %d friends", Integer.valueOf(loadAll.size()));
        for (Friend friend3 : loadAll) {
            if (!hashSet.contains(friend3.getUuid())) {
                this.friendDao.delete(friend3);
            }
        }
        this.chatterList.updateList(ChatterListType.Friends);
        this.chatterList.updateList(ChatterListType.FriendsOnline);
    }
}
