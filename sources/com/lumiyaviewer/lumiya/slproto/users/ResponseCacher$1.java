// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedResponse;
import com.lumiyaviewer.lumiya.dao.CachedResponseDao;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users:
//            ResponseCacher

class val.keyPrefix extends RequestProcessor
{

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
                val$keyPrefix, obj.toString(), obj1
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
            val$keyPrefix, obj.toString()
        });
        if (obj1 != null)
        {
            ResponseCacher._2D_get0(ResponseCacher.this).insertOrReplace(new CachedResponse(ResponseCacher._2D_wrap0(ResponseCacher.this, obj), storeCached(obj1), false));
        }
        return obj1;
    }

    (Executor executor, String s)
    {
        this$0 = final_responsecacher;
        val$keyPrefix = s;
        super(RequestSource.this, executor);
    }
}
