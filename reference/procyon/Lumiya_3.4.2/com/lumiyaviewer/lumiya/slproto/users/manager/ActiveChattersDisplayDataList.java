// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.dao.Chatter;
import java.util.ArrayList;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;
import java.util.Comparator;
import javax.annotation.Nonnull;

class ActiveChattersDisplayDataList extends ChatterDisplayDataList
{
    public ActiveChattersDisplayDataList(@Nonnull final UserManager userManager, final OnListUpdated onListUpdated) {
        super(userManager, onListUpdated, null);
    }
    
    @Override
    protected List<ChatterID> getChatters() {
        final List<Chatter> list = ((AbstractDao<Chatter, K>)this.userManager.getDaoSession().getChatterDao()).queryBuilder().where(ChatterDao.Properties.Active.notEq(false), new WhereCondition[0]).list();
        final ArrayList list2 = new ArrayList(list.size());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add((Object)ChatterID.fromDatabaseObject(this.userManager.getUserID(), (Chatter)iterator.next()));
        }
        return (List<ChatterID>)list2;
    }
}
