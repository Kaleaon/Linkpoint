package com.linkpoint.slproto.users.manager

import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.dao.DaoSession
import com.linkpoint.dao.Friend
import com.linkpoint.dao.FriendDao
import com.linkpoint.react.RequestFinalProcessor
import com.linkpoint.react.Subscribable
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.auth.SLAuthReply
import java.util.HashSet
import java.util.List
import java.util.UUID
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class FriendManager {
    /* access modifiers changed from: private */
    @NonNull
    ChatterList chatterList
    /* access modifiers changed from: private */
    @NonNull
    FriendDao friendDao
    private OnListUpdated onFriendListUpdated = OnListUpdated() {
        fun onListUpdated()  {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.Friends)
        }
    }
    private OnListUpdated onFriendsOnlineListUpdated = OnListUpdated() {
        fun onListUpdated()  {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.FriendsOnline)
        }
    }
    private SubscriptionPool<UUID, Boolean> onlineStatus = SubscriptionPool<>()
    @NonNull
    private UserManager userManager

    FriendManager(@NonNull UserManager userManager2, @NonNull DaoSession daoSession, @NonNull ChatterList chatterList2) {
        this.userManager = userManager2
        this.friendDao = daoSession.getFriendDao()
        this.chatterList = chatterList2
        RequestFinalProcessor<UUID, Boolean>(this.onlineStatus, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
            fun processRequest(@NonNull UUID uuid): Boolean {
                Friend friend = (FriendManager as Friend).this.friendDao.load(uuid)
                if (friend != null) {
                    return Boolean.valueOf(friend.getIsOnline())
                }
                return false
            }
        }
    }

    fun addFriend(UUID uuid)  {
        if (((this as Friend).friendDao.load(uuid)) == null) {
            this.friendDao.insert(Friend(uuid, 1, 1, false))
        }
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    fun getFriend(@Nullable UUID uuid): Friend {
        if (uuid != null) {
            return (this as Friend).friendDao.load(uuid)
        }
        return null
    }

    fun getFriendList(): ChatterDisplayDataList {
        return FriendDisplayDataList(this.userManager, this.onFriendListUpdated, false)
    }

    fun getFriendsOnlineList(): ChatterDisplayDataList {
        return FriendDisplayDataList(this.userManager, this.onFriendsOnlineListUpdated, true)
    }

    fun getOnlineStatus(): Subscribable<UUID, Boolean> {
        return this.onlineStatus
    }

    fun removeFriend(UUID uuid)  {
        this.friendDao.deleteByKey(uuid)
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    fun setUsersOnline(List<UUID> list, Boolean z)  {
        for (UUID uuid : list) {
            Friend friend = (this as Friend).friendDao.load(uuid)
            if (friend != null) {
                friend.setIsOnline(z)
                this.friendDao.update(friend)
            }
            this.onlineStatus.requestUpdate(uuid)
        }
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    fun updateFriendList(ImmutableList<SLAuthReply.Friend> immutableList)  {
        HashSet hashSet = HashSet()
        for (SLAuthReply.Friend friend : immutableList) {
            UUID uuid = friend.uuid
            Friend friend2 = (this as Friend).friendDao.load(uuid)
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
