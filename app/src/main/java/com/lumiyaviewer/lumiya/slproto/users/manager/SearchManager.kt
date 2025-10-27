package com.lumiyaviewer.lumiya.slproto.users.manager

import com.lumiyaviewer.lumiya.dao.DaoSession
import com.lumiyaviewer.lumiya.dao.SearchGridResult
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao
import com.lumiyaviewer.lumiya.react.SubscriptionPool
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery
import de.greenrobot.dao.query.LazyList
import java.util.concurrent.Executor
import androidx.annotation.NonNull

class SearchManager {
    private Executor dbExecutor
    private SearchGridResultDao searchGridResultDao
    private SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults = SubscriptionPool<>()

    SearchManager(@NonNull UserManager userManager, @NonNull DaoSession daoSession) {
        this.dbExecutor = userManager.getDatabaseExecutor()
        this.searchGridResultDao = daoSession.getSearchGridResultDao()
        this.searchResults.setDisposeHandler($Lambda$bhNrB7VMDi5fNhRKl1Wi5s6H9k(), this.dbExecutor)
    }

    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019  reason: not valid java name */
    /* synthetic */ Unit m350lambda$com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019(LazyList lazyList) {
        if (!lazyList.isClosed()) {
            lazyList.close()
        }
    }

    SearchGridResultDao getSearchGridResultDao() {
        return this.searchGridResultDao
    }

    SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults() {
        return this.searchResults
    }
}
