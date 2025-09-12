// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import de.greenrobot.dao.query.LazyList;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UserManager

public class SearchManager
{

    private final Executor dbExecutor;
    private final SearchGridResultDao searchGridResultDao;
    private final SubscriptionPool searchResults = new SubscriptionPool();

    public SearchManager(UserManager usermanager, DaoSession daosession)
    {
        dbExecutor = usermanager.getDatabaseExecutor();
        searchGridResultDao = daosession.getSearchGridResultDao();
        searchResults.setDisposeHandler(new _2D_.Lambda.bhNr_2D_B7VMDi5fNhRKl1Wi5s6H9k(), dbExecutor);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019(LazyList lazylist)
    {
        if (!lazylist.isClosed())
        {
            lazylist.close();
        }
    }

    public SearchGridResultDao getSearchGridResultDao()
    {
        return searchGridResultDao;
    }

    public SubscriptionPool searchResults()
    {
        return searchResults;
    }
}
