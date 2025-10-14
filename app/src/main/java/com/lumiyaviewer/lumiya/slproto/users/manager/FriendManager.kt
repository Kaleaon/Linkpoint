package com.lumiyaviewer.lumiya.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.dao.Friend
import com.lumiyaviewer.lumiya.dao.FriendDao
import com.lumiyaviewer.lumiya.react.RequestFinalProcessor
import com.lumiyaviewer.lumiya.react.Subscribable
import com.lumiyaviewer.lumiya.react.SubscriptionPool
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import java.util.HashSet
import java.util.List
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class FriendManager {
    /* access modifiers changed from: private */
    @Nonnull
    ChatterList chatterList
    /* access modifiers changed from: private */
    @Nonnull
    FriendDao friendDao
    private OnListUpdated onFriendListUpdated = OnListUpdated() {
        Unit onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.Friends)
        }
    }
    private OnListUpdated onFriendsOnlineListUpdated = OnListUpdated() {
        Unit onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.FriendsOnline)
        }
    }
    private SubscriptionPool<UUID, Boolean> onlineStatus = SubscriptionPool<>()
    @Nonnull
    private UserManager userManager

    FriendManager(@Nonnull UserManager userManager2, @Nonnull DaoSession daoSession, @Nonnull ChatterList chatterList2) {
        this.userManager = userManager2
        this.friendDao = daoSession.getFriendDao()
        this.chatterList = chatterList2
        RequestFinalProcessor<UUID, Boolean>(this.onlineStatus, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            Boolean processRequest(@Nonnull UUID uuid) {
                Friend friend = (Friend) FriendManager.this.friendDao.load(uuid)
                if (friend != null) {
                    return Boolean.valueOf(friend.getIsOnline())
                }
                return false
            }
        }
    }

    Unit addFriend(UUID uuid) {
        if (((Friend) this.friendDao.load(uuid)) == null) {
            this.friendDao.insert(Friend(uuid, 1, 1, false))
        }
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    Friend getFriend(@Nullable UUID uuid) {
        if (uuid != null) {
            return (Friend) this.friendDao.load(uuid)
        }
        return null
    }

    ChatterDisplayDataList getFriendList() {
        return FriendDisplayDataList(this.userManager, this.onFriendListUpdated, false)
    }

    ChatterDisplayDataList getFriendsOnlineList() {
        return FriendDisplayDataList(this.userManager, this.onFriendsOnlineListUpdated, true)
    }

    Subscribable<UUID, Boolean> getOnlineStatus() {
        return this.onlineStatus
    }

    Unit removeFriend(UUID uuid) {
        this.friendDao.deleteByKey(uuid)
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    Unit setUsersOnline(List<UUID> list, Boolean z) {
        for (UUID uuid : list) {
            Friend friend = (Friend) this.friendDao.load(uuid)
            if (friend != null) {
                friend.setIsOnline(z)
                this.friendDao.update(friend)
            }
            this.onlineStatus.requestUpdate(uuid)
        }
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    Unit updateFriendList(ImmutableList<SLAuthReply.Friend> immutableList) {
        HashSet hashSet = HashSet()
        for (SLAuthReply.Friend friend : immutableList) {
            UUID uuid = friend.uuid
            Friend friend2 = (Friend) this.friendDao.load(uuid)
            if (friend2 == null) {
                this.friendDao.insertOrReplace(Friend(uuid, friend.rightsGiven, friend.rightsHas, false))
            } else if (friend2.getRightsGiven() != friend.rightsGiven || friend2.getRightsHas() != friend.rightsHas || friend2.getIsOnline()) {
                friend2.setRightsGiven(friend.rightsGiven)
                friend2.setRightsHas(friend.rightsHas)
                friend2.setIsOnline(false)
                this.friendDao.update(friend2)
            }
            hashSet.add(uuid)
        }
        List<Friend> loadAll = this.friendDao.loadAll()
        Debug.Printf("FriendList: update[1], got %d friends", Int.valueOf(loadAll.size()))
        for (Friend friend3 : loadAll) {
            if (!hashSet.contains(friend3.getUuid())) {
                this.friendDao.delete(friend3)
            }
        }
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }
}
