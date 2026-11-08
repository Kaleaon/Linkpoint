package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.dao.SearchGridResult;
import com.lumiyaviewer.lumiya.dao.SearchGridResultDao;
import com.lumiyaviewer.lumiya.react.DisposeHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.slproto.modules.search.SearchGridQuery;
import de.greenrobot.dao.query.LazyList;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
/* loaded from: classes.dex */
public class SearchManager {
    private final Executor dbExecutor;
    private final SearchGridResultDao searchGridResultDao;
    private final SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults = new SubscriptionPool<>();

    public SearchManager(@Nonnull UserManager userManager, @Nonnull DaoSession daoSession) {
        this.dbExecutor = userManager.getDatabaseExecutor();
        this.searchGridResultDao = daoSession.getSearchGridResultDao();
        this.searchResults.setDisposeHandler(new DisposeHandler() { // from class: com.lumiyaviewer.lumiya.slproto.users.manager.-$Lambda$bhNr-B7VMDi5fNhRKl1Wi5s6H9k
            private final /* synthetic */ void $m$0(Object obj) {
                SearchManager.m361x60619dda((LazyList) obj);
            }

            @Override // com.lumiyaviewer.lumiya.react.DisposeHandler
            public final void onDispose(Object obj) {
                $m$0(obj);
            }
        }, this.dbExecutor);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_manager_SearchManager_1019  reason: not valid java name */
    public static /* synthetic */ void m361x60619dda(LazyList lazyList) {
        if (lazyList.isClosed()) {
            return;
        }
        lazyList.close();
    }

    public SearchGridResultDao getSearchGridResultDao() {
        return this.searchGridResultDao;
    }

    public SubscriptionPool<SearchGridQuery, LazyList<SearchGridResult>> searchResults() {
        return this.searchResults;
    }
}
