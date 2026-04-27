package com.linkpoint.slproto.users.manager;

import com.linkpoint.dao.Chatter;
import com.linkpoint.dao.ChatterDao;
import com.linkpoint.slproto.users.ChatterID;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nonnull;

class ActiveChattersDisplayDataList extends ChatterDisplayDataList {
    public ActiveChattersDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, (Comparator<? super ChatterDisplayData>) null);
    }

    /* access modifiers changed from: protected */
    public List<ChatterID> getChatters() {
        List<Chatter> list = this.userManager.getDaoSession().getChatterDao().queryBuilder().where(ChatterDao.Properties.Active.notEq(false), new WhereCondition[0]).list();
        ArrayList arrayList = new ArrayList(list.size());
        for (Chatter fromDatabaseObject : list) {
            arrayList.add(ChatterID.fromDatabaseObject(this.userManager.getUserID(), fromDatabaseObject));
        }
        return arrayList;
    }
}
