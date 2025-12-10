package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.Friend
import com.linkpoint.dao.FriendDao
import com.linkpoint.slproto.users.ChatterID
import de.greenrobot.dao.query.WhereCondition
import java.util.ArrayList
import java.util.Comparator
import java.util.List
import androidx.annotation.NonNull

class FriendDisplayDataList : ChatterDisplayDataList {
    private Boolean onlineFriends

    FriendDisplayDataList(@NonNull UserManager userManager, OnListUpdated onListUpdated, Boolean z) {
        super(userManager, onListUpdated, (Comparator<? super ChatterDisplayData>) null)
        this.onlineFriends = z
    }

    /* access modifiers changed from: protected */
    fun getChatters(): List<ChatterID> {
        List<Friend> list = this.onlineFriends ? this.userManager.getDaoSession().getFriendDao().queryBuilder().where(FriendDao.Properties.IsOnline.eq(true), WhereCondition[0]).list() : this.userManager.getDaoSession().getFriendDao().loadAll()
        ArrayList arrayList = ArrayList(list.size())
        for (Friend uuid : list) {
            arrayList.add(ChatterID.getUserChatterID(this.userManager.getUserID(), uuid.getUuid()))
        }
        return arrayList
    }
}
