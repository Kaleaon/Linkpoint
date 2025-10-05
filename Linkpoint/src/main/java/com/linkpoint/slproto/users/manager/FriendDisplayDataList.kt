package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.Friend
import com.linkpoint.dao.FriendDao
import com.linkpoint.slproto.users.ChatterID

internal class FriendDisplayDataList(
    userManager: UserManager,
    onListUpdated: OnListUpdated,
    private val onlineFriends: Boolean
) : ChatterDisplayDataList(userManager, onListUpdated, null) {

    override fun getChatters(): List<ChatterID> {
        val friendList = if (onlineFriends) {
            userManager.daoSession.friendDao.queryBuilder()
                .where(FriendDao.Properties.IsOnline.eq(true))
                .list()
        } else {
            userManager.daoSession.friendDao.loadAll()
        }
        
        return friendList.map { friend ->
            ChatterID.getUserChatterID(userManager.userID, friend.uuid)
        }
    }
}