package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedResponse;
import com.lumiyaviewer.lumiya.dao.CachedResponseDao;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.Refreshable;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import java.util.concurrent.Executor;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

abstract class ResponseCacher<KeyType, MessageType> implements Refreshable<KeyType> {
    private final Executor cacheExecutor;
    /* access modifiers changed from: private */
    public final CachedResponseDao cachedresponseDao;
    private final String keyPrefix;
    private final SubscriptionPool<KeyType, MessageType> pool = new SubscriptionPool<>();
    private final RateLimitRequestHandler<KeyType, MessageType> requestHandler;

    ResponseCacher(DaoSession daoSession, Executor executor, final String str) {
        this.cacheExecutor = executor;
        this.cachedresponseDao = daoSession.getCachedResponseDao();
        this.keyPrefix = str;
        this.pool.setCacheInvalidateHandler(new $Lambda$Tb8OaRVtYXRJ6N6ca7skHX1PNws(this), executor);
        this.requestHandler = new RateLimitRequestHandler<>(new RequestProcessor<KeyType, MessageType, MessageType>(this.pool, executor) {
            /* access modifiers changed from: protected */
            public boolean isRequestComplete(@Nonnull KeyType keytype, MessageType messagetype) {
                CachedResponse cachedResponse = (CachedResponse) ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype));
                if (cachedResponse != null) {
                    return !cachedResponse.getMustRevalidate();
                }
                return false;
            }

            /* access modifiers changed from: protected */
            @Nullable
            public MessageType processRequest(@Nonnull KeyType keytype) {
                CachedResponse cachedResponse = (CachedResponse) ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keytype));
                if (cachedResponse == null || cachedResponse.getData() == null) {
                    return null;
                }
                MessageType loadCached = ResponseCacher.this.loadCached(cachedResponse.getData());
                Debug.Printf("%s: returning cached response for key %s (%s)", str, keytype.toString(), loadCached);
                return loadCached;
            }

            /* access modifiers changed from: protected */
            public MessageType processResult(@Nonnull KeyType keytype, MessageType messagetype) {
                Debug.Printf("%s: saving cached data for key %s", str, keytype.toString());
                if (messagetype != null) {
                    ResponseCacher.this.cachedresponseDao.insertOrReplace(new CachedResponse(ResponseCacher.this.getKeyString(keytype), ResponseCacher.this.storeCached(messagetype), false));
                }
                return messagetype;
            }
        });
    }

    /* access modifiers changed from: private */
    public String getKeyString(@Nonnull KeyType keytype) {
        return this.keyPrefix + ":" + keytype.toString();
    }

    public Subscribable<KeyType, MessageType> getPool() {
        return this.pool;
    }

    public RequestSource<KeyType, MessageType> getRequestSource() {
        return this.requestHandler;
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058  reason: not valid java name */
    public /* synthetic */ void m267lambda$com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058(Object obj) {
        CachedResponse cachedResponse = (CachedResponse) this.cachedresponseDao.load(getKeyString(obj));
        if (cachedResponse != null) {
            cachedResponse.setMustRevalidate(true);
            this.cachedresponseDao.update(cachedResponse);
        }
    }

    /* access modifiers changed from: protected */
    public abstract MessageType loadCached(byte[] bArr);

    public void requestUpdate(KeyType keytype) {
        this.pool.requestUpdate(keytype);
    }

    /* access modifiers changed from: protected */
    public abstract byte[] storeCached(@Nonnull MessageType messagetype);
}
