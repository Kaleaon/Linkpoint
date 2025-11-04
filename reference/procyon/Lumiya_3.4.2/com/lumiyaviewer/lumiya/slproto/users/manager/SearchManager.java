// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.DisposeHandler;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import java.util.concurrent.Executor;

public class SearchManager
{
    private final Executor dbExecutor;
    private final SearchGridResultDao searchGridResultDao;
    private final SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults;
    
    public SearchManager(@Nonnull final UserManager userManager, @Nonnull final DaoSession daoSession) {
        this.searchResults = new SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>>();
        this.dbExecutor = userManager.getDatabaseExecutor();
        this.searchGridResultDao = daoSession.getSearchGridResultDao();
        this.searchResults.setDisposeHandler(new _$Lambda$bhNr_B7VMDi5fNhRKl1Wi5s6H9k(), this.dbExecutor);
    }
    
    public SearchGridResultDao getSearchGridResultDao() {
        return this.searchGridResultDao;
    }
    
    public SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults() {
        return this.searchResults;
    }
}
