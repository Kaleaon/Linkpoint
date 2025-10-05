package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.ChatterDao
import com.linkpoint.slproto.users.ChatterID

internal class ActiveChattersDisplayDataList(
    userManager: UserManager,
    onListUpdated: OnListUpdated
) : ChatterDisplayDataList(userManager, onListUpdated, null) {

    override fun getChatters(): List<ChatterID> {
        val chatterList = userManager.daoSession.chatterDao.queryBuilder()
            .where(ChatterDao.Properties.Active.notEq(false))
            .list()
        
        return chatterList.map { chatter ->
            ChatterID.fromDatabaseObject(userManager.userID, chatter)
        }
    }
}