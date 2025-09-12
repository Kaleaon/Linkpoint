// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.Friend;
import com.lumiyaviewer.lumiya.dao.FriendDao;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayDataList, UserManager, OnListUpdated

class FriendDisplayDataList extends ChatterDisplayDataList
{

    private final boolean onlineFriends;

    public FriendDisplayDataList(UserManager usermanager, OnListUpdated onlistupdated, boolean flag)
    {
        super(usermanager, onlistupdated, null);
        onlineFriends = flag;
    }

    protected List getChatters()
    {
        Object obj;
        ArrayList arraylist;
        if (onlineFriends)
        {
            obj = userManager.getDaoSession().getFriendDao().queryBuilder().where(com.lumiyaviewer.lumiya.dao.FriendDao.Properties.IsOnline.eq(Boolean.valueOf(true)), new WhereCondition[0]).list();
        } else
        {
            obj = userManager.getDaoSession().getFriendDao().loadAll();
        }
        arraylist = new ArrayList(((List) (obj)).size());
        Friend friend;
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); arraylist.add(ChatterID.getUserChatterID(userManager.getUserID(), friend.getUuid())))
        {
            friend = (Friend)((Iterator) (obj)).next();
        }

        return arraylist;
    }
}
