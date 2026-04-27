// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.users;

import de.greenrobot.dao.AbstractDao;
import com.lumiyaviewer.lumiya.react.Subscribable;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedResponse;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.react.RequestSource;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.dao.DaoSession;
import com.lumiyaviewer.lumiya.react.RateLimitRequestHandler;
import com.lumiyaviewer.lumiya.react.SubscriptionPool;
import com.lumiyaviewer.lumiya.dao.CachedResponseDao;
import java.util.concurrent.Executor;
import com.lumiyaviewer.lumiya.react.Refreshable;

abstract class ResponseCacher<KeyType, MessageType> implements Refreshable<KeyType>
{
    private final Executor cacheExecutor;
    private final CachedResponseDao cachedresponseDao;
    private final String keyPrefix;
    private final SubscriptionPool<KeyType, MessageType> pool;
    private final RateLimitRequestHandler<KeyType, MessageType> requestHandler;
    
    ResponseCacher(final DaoSession daoSession, final Executor cacheExecutor, final String keyPrefix) {
        this.pool = new SubscriptionPool<KeyType, MessageType>();
        this.cacheExecutor = cacheExecutor;
        this.cachedresponseDao = daoSession.getCachedResponseDao();
        this.keyPrefix = keyPrefix;
        this.pool.setCacheInvalidateHandler(new _$Lambda$Tb8OaRVtYXRJ6N6ca7skHX1PNws(this), cacheExecutor);
        this.requestHandler = new RateLimitRequestHandler<KeyType, MessageType>(new RequestProcessor<KeyType, MessageType, MessageType>(this.pool, cacheExecutor) {
            @Override
            protected boolean isRequestComplete(@Nonnull final KeyType keyType, final MessageType messageType) {
                final CachedResponse cachedResponse = ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keyType));
                return cachedResponse != null && (cachedResponse.getMustRevalidate() ^ true);
            }
            
            @Nullable
            @Override
            protected MessageType processRequest(@Nonnull final KeyType keyType) {
                final CachedResponse cachedResponse = ResponseCacher.this.cachedresponseDao.load(ResponseCacher.this.getKeyString(keyType));
                if (cachedResponse != null && cachedResponse.getData() != null) {
                    final MessageType loadCached = ResponseCacher.this.loadCached(cachedResponse.getData());
                    Debug.Printf("%s: returning cached response for key %s (%s)", keyPrefix, keyType.toString(), loadCached);
                    return loadCached;
                }
                return null;
            }
            
            @Override
            protected MessageType processResult(@Nonnull final KeyType keyType, final MessageType messageType) {
                Debug.Printf("%s: saving cached data for key %s", keyPrefix, keyType.toString());
                if (messageType != null) {
                    ((AbstractDao<CachedResponse, K>)ResponseCacher.this.cachedresponseDao).insertOrReplace(new CachedResponse(ResponseCacher.this.getKeyString(keyType), ResponseCacher.this.storeCached(messageType), false));
                }
                return messageType;
            }
        });
    }
    
    private String getKeyString(@Nonnull final KeyType keyType) {
        return this.keyPrefix + ":" + keyType.toString();
    }
    
    public Subscribable<KeyType, MessageType> getPool() {
        return this.pool;
    }
    
    public RequestSource<KeyType, MessageType> getRequestSource() {
        return this.requestHandler;
    }
    
    protected abstract MessageType loadCached(final byte[] p0);
    
    @Override
    public void requestUpdate(final KeyType keyType) {
        this.pool.requestUpdate(keyType);
    }
    
    protected abstract byte[] storeCached(@Nonnull final MessageType p0);
}
