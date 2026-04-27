package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class ActiveChattersDisplayDataList extends ChatterDisplayDataList {
    public ActiveChattersDisplayDataList(@Nonnull UserManager userManager, OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, null);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.users.manager.ChatterDisplayDataList
    protected List<ChatterID> getChatters() {
        List<Chatter> list = this.userManager.getDaoSession().getChatterDao().queryBuilder().where(ChatterDao.Properties.Active.notEq(false), new WhereCondition[0]).list();
        ArrayList arrayList = new ArrayList(list.size());
        Iterator<T> it = list.iterator();
        while (it.hasNext()) {
            arrayList.add(ChatterID.fromDatabaseObject(this.userManager.getUserID(), (Chatter) it.next()));
        }
        return arrayList;
    }
}
