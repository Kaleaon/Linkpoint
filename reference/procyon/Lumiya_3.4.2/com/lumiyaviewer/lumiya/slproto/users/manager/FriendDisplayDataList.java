// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import de.greenrobot.dao.AbstractDao;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.dao.Friend;
import java.util.ArrayList;
import de.greenrobot.dao.query.WhereCondition;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.List;
import java.util.Comparator;
import javax.annotation.Nonnull;

class FriendDisplayDataList extends ChatterDisplayDataList
{
    private final boolean onlineFriends;
    
    public FriendDisplayDataList(@Nonnull final UserManager userManager, final OnListUpdated onListUpdated, final boolean onlineFriends) {
        super(userManager, onListUpdated, null);
        this.onlineFriends = onlineFriends;
    }
    
    @Override
    protected List<ChatterID> getChatters() {
        List<Friend> list;
        if (this.onlineFriends) {
            list = ((AbstractDao<Friend, K>)this.userManager.getDaoSession().getFriendDao()).queryBuilder().where(FriendDao.Properties.IsOnline.eq(true), new WhereCondition[0]).list();
        }
        else {
            list = this.userManager.getDaoSession().getFriendDao().loadAll();
        }
        final ArrayList list2 = new ArrayList(list.size());
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            list2.add((Object)ChatterID.getUserChatterID(this.userManager.getUserID(), ((Friend)iterator.next()).getUuid()));
        }
        return (List<ChatterID>)list2;
    }
}
