// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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

abstract class ResponseCacher
    implements Refreshable
{

    private final Executor cacheExecutor;
    private final CachedResponseDao cachedresponseDao;
    private final String keyPrefix;
    private final SubscriptionPool pool = new SubscriptionPool();
    private final RateLimitRequestHandler requestHandler;

    static CachedResponseDao _2D_get0(ResponseCacher responsecacher)
    {
        return responsecacher.cachedresponseDao;
    }

    static String _2D_wrap0(ResponseCacher responsecacher, Object obj)
    {
        return responsecacher.getKeyString(obj);
    }

    ResponseCacher(DaoSession daosession, Executor executor, String s)
    {
        cacheExecutor = executor;
        cachedresponseDao = daosession.getCachedResponseDao();
        keyPrefix = s;
        pool.setCacheInvalidateHandler(new _2D_.Lambda.Tb8OaRVtYXRJ6N6ca7skHX1PNws(this), executor);
        requestHandler = new RateLimitRequestHandler(new RequestProcessor(executor, s) {

            final ResponseCacher this$0;
            final String val$keyPrefix;

            protected boolean isRequestComplete(Object obj, Object obj1)
            {
                obj = (CachedResponse)ResponseCacher._2D_get0(ResponseCacher.this).load(ResponseCacher._2D_wrap0(ResponseCacher.this, obj));
                if (obj != null)
                {
                    return ((CachedResponse) (obj)).getMustRevalidate() ^ true;
                } else
                {
                    return false;
                }
            }

            protected Object processRequest(Object obj)
            {
                Object obj1 = (CachedResponse)ResponseCacher._2D_get0(ResponseCacher.this).load(ResponseCacher._2D_wrap0(ResponseCacher.this, obj));
                if (obj1 != null && ((CachedResponse) (obj1)).getData() != null)
                {
                    obj1 = loadCached(((CachedResponse) (obj1)).getData());
                    Debug.Printf("%s: returning cached response for key %s (%s)", new Object[] {
                        keyPrefix, obj.toString(), obj1
                    });
                    return obj1;
                } else
                {
                    return null;
                }
            }

            protected Object processResult(Object obj, Object obj1)
            {
                Debug.Printf("%s: saving cached data for key %s", new Object[] {
                    keyPrefix, obj.toString()
                });
                if (obj1 != null)
                {
                    ResponseCacher._2D_get0(ResponseCacher.this).insertOrReplace(new CachedResponse(ResponseCacher._2D_wrap0(ResponseCacher.this, obj), storeCached(obj1), false));
                }
                return obj1;
            }

            
            {
                this$0 = ResponseCacher.this;
                keyPrefix = s;
                super(final_requestsource, executor);
            }
        });
    }

    private String getKeyString(Object obj)
    {
        return (new StringBuilder()).append(keyPrefix).append(":").append(obj.toString()).toString();
    }

    public Subscribable getPool()
    {
        return pool;
    }

    public RequestSource getRequestSource()
    {
        return requestHandler;
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_ResponseCacher_1058(Object obj)
    {
        obj = (CachedResponse)cachedresponseDao.load(getKeyString(obj));
        if (obj != null)
        {
            ((CachedResponse) (obj)).setMustRevalidate(true);
            cachedresponseDao.update(obj);
        }
    }

    protected abstract Object loadCached(byte abyte0[]);

    public void requestUpdate(Object obj)
    {
        pool.requestUpdate(obj);
    }

    protected abstract byte[] storeCached(Object obj);
}
