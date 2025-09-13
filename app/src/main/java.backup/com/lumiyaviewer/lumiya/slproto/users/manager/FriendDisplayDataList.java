package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

class FriendDisplayDataList extends ChatterDisplayDataList {
    private final boolean onlineFriends;

    public FriendDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated, boolean z) {
        super(userManager, onListUpdated, (Comparator<? super ChatterDisplayData>) null);
        this.onlineFriends = z;
    }

    /* access modifiers changed from: protected */
    public List<ChatterID> getChatters() {
        List<Friend> list = this.onlineFriends ? this.userManager.getDaoSession().getFriendDao().queryBuilder().where(FriendDao.Properties.IsOnline.eq(true), new WhereCondition[0]).list() : this.userManager.getDaoSession().getFriendDao().loadAll();
        ArrayList arrayList = new ArrayList(list.size());
        for (Friend uuid : list) {
            arrayList.add(ChatterID.getUserChatterID(this.userManager.getUserID(), uuid.getUuid()));
        }
        return arrayList;
    }
}
