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
import javax.annotation.Nonnull
import javax.annotation.Nullable

class FriendManager {
    /* access modifiers changed from: private */
    val ChatterList chatterList
    /* access modifiers changed from: private */
    val FriendDao friendDao
    private val OnListUpdated onFriendListUpdated = OnListUpdated() {
        fun onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.Friends)
        }
    }
    private val OnListUpdated onFriendsOnlineListUpdated = OnListUpdated() {
        fun onListUpdated() {
            FriendManager.this.chatterList.notifyListUpdated(ChatterListType.FriendsOnline)
        }
    }
    private val SubscriptionPool<UUID, Boolean> onlineStatus = SubscriptionPool<>()
    private val UserManager userManager

    public FriendManager(UserManager userManager2, DaoSession daoSession, ChatterList chatterList2) {
        this.userManager = userManager2
        this.friendDao = daoSession.getFriendDao()
        this.chatterList = chatterList2
        RequestFinalProcessor<UUID, Boolean>(this.onlineStatus, userManager2.getDatabaseExecutor()) {
            /* access modifiers changed from: protected */
             public fun processRequest(uuid: UUID): Boolean {
                val friend: Friend = (Friend) FriendManager.this.friendDao.load(uuid)
                if (friend != null) {
                    return Boolean.valueOf(friend.getIsOnline())
                }
                return false
            }
        }
    }

    fun addFriend(uuid: UUID) {
        if (((Friend) this.friendDao.load(uuid)) == null) {
            this.friendDao.insert(Friend(uuid, 1, 1, false))
        }
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

     public fun getFriend(uuid: UUID): Friend {
        if (uuid != null) {
            return (Friend) this.friendDao.load(uuid)
        }
        return null
    }

     public fun getFriendList(): ChatterDisplayDataList {
        return FriendDisplayDataList(this.userManager, this.onFriendListUpdated, false)
    }

     public fun getFriendsOnlineList(): ChatterDisplayDataList {
        return FriendDisplayDataList(this.userManager, this.onFriendsOnlineListUpdated, true)
    }

    public Subscribable<UUID, Boolean> getOnlineStatus() {
        return this.onlineStatus
    }

    fun removeFriend(uuid: UUID) {
        this.friendDao.deleteByKey(uuid)
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    fun setUsersOnline(list: List<UUID>, z: Boolean) {
        for (UUID uuid : list) {
            val friend: Friend = (Friend) this.friendDao.load(uuid)
            if (friend != null) {
                friend.setIsOnline(z)
                this.friendDao.update(friend)
            }
            this.onlineStatus.requestUpdate(uuid)
        }
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }

    fun updateFriendList(immutableList: ImmutableList<SLAuthReply.Friend>) {
        val hashSet: HashSet = HashSet()
        for (SLAuthReply.Friend friend : immutableList) {
            val uuid: UUID = friend.uuid
            val friend2: Friend = (Friend) this.friendDao.load(uuid)
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
        val loadAll: List<Friend> = this.friendDao.loadAll()
        Debug.Printf("FriendList: update[1], got %d friends", Integer.valueOf(loadAll.size()))
        for (Friend friend3 : loadAll) {
            if (!hashSet.contains(friend3.getUuid())) {
                this.friendDao.delete(friend3)
            }
        }
        this.chatterList.updateList(ChatterListType.Friends)
        this.chatterList.updateList(ChatterListType.FriendsOnline)
    }
}
