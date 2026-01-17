package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class FriendDisplayDataList extends ChatterDisplayDataList {
    private final boolean onlineFriends;

    public FriendDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated, boolean z) {
        super(userManager, onListUpdated, null);
        this.onlineFriends = z;
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayDataList
    protected List<ChatterID> getChatters() {
        List<Friend> list = this.onlineFriends ? this.userManager.getDaoSession().getFriendDao().queryBuilder().where(FriendDao.Properties.IsOnline.eq(true), new WhereCondition[0]).list() : this.userManager.getDaoSession().getFriendDao().loadAll();
        ArrayList arrayList = new ArrayList(list.size());
        for (Friend friend : list) {
            arrayList.add(ChatterID.getUserChatterID(this.userManager.getUserID(), friend.getUuid()));
        }
        return arrayList;
    }
}
