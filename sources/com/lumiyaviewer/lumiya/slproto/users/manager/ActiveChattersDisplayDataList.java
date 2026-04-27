// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.Chatter;
import com.lumiyaviewer.lumiya.dao.ChatterDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.query.QueryBuilder;
import de.greenrobot.dao.query.WhereCondition;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayDataList, UserManager, OnListUpdated

class ActiveChattersDisplayDataList extends ChatterDisplayDataList
{

    public ActiveChattersDisplayDataList(UserManager usermanager, OnListUpdated onlistupdated)
    {
        super(usermanager, onlistupdated, null);
    }

    protected List getChatters()
    {
        Object obj = userManager.getDaoSession().getChatterDao().queryBuilder().where(com.lumiyaviewer.lumiya.dao.ChatterDao.Properties.Active.notEq(Boolean.valueOf(false)), new WhereCondition[0]).list();
        ArrayList arraylist = new ArrayList(((List) (obj)).size());
        Chatter chatter;
        for (obj = ((Iterable) (obj)).iterator(); ((Iterator) (obj)).hasNext(); arraylist.add(ChatterID.fromDatabaseObject(userManager.getUserID(), chatter)))
        {
            chatter = (Chatter)((Iterator) (obj)).next();
        }

        return arraylist;
    }
}
