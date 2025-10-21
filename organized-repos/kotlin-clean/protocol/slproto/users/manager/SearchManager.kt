package com.linkpoint.slproto.users.manager

import com.linkpoint.dao.DaoSession
import com.linkpoint.dao.SearchGridResult
import com.linkpoint.dao.SearchGridResultDao
import com.linkpoint.react.SubscriptionPool
import com.linkpoint.slproto.modules.search.SearchGridQuery
import de.greenrobot.dao.query.LazyList
import java.util.concurrent.Executor
import javax.annotation.Nonnull

class SearchManager {
    private val Executor dbExecutor
    private val SearchGridResultDao searchGridResultDao
    private val SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults = SubscriptionPool<>()

    public SearchManager(UserManager userManager, DaoSession daoSession) {
        this.dbExecutor = userManager.getDatabaseExecutor()
        this.searchGridResultDao = daoSession.getSearchGridResultDao()
        this.searchResults.setDisposeHandler($Lambda$bhNrB7VMDi5fNhRKl1Wi5s6H9k(), this.dbExecutor)
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019  reason: not valid java name */
    static /* synthetic */ Unit m350lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019(LazyList lazyList) {
        if (!lazyList.isClosed()) {
            lazyList.close()
        }
    }

    public SearchGridResultDao getSearchGridResultDao() {
        return this.searchGridResultDao
    }

    public SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults() {
        return this.searchResults
    }
}
