// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager.assets;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.CachedAsset;
import com.lumiyaviewer.lumiya.dao.CachedAssetDao;
import com.lumiyaviewer.lumiya.react.RequestProcessor;
import com.lumiyaviewer.lumiya.react.RequestSource;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager.assets:
//            AssetResponseCacher, AssetKey, AssetData

class this._cls0 extends RequestProcessor
{

    final AssetResponseCacher this$0;

    protected boolean isRequestComplete(AssetKey assetkey, AssetData assetdata)
    {
        assetkey = (CachedAsset)AssetResponseCacher._2D_get0(AssetResponseCacher.this).load(assetkey.toString());
        if (assetkey != null)
        {
            return assetkey.getMustRevalidate() ^ true;
        } else
        {
            return false;
        }
    }

    protected volatile boolean isRequestComplete(Object obj, Object obj1)
    {
        return isRequestComplete((AssetKey)obj, (AssetData)obj1);
    }

    protected AssetData processRequest(AssetKey assetkey)
    {
        Object obj = (CachedAsset)AssetResponseCacher._2D_get0(AssetResponseCacher.this).load(assetkey.toString());
        if (obj != null)
        {
            obj = new AssetData(((CachedAsset) (obj)).getStatus(), ((CachedAsset) (obj)).getData());
            Debug.Printf("AssetCache: returning cached response for key %s", new Object[] {
                assetkey
            });
            return ((AssetData) (obj));
        } else
        {
            Debug.Printf("AssetCache: no cached data for key %s", new Object[] {
                assetkey
            });
            return null;
        }
    }

    protected volatile Object processRequest(Object obj)
    {
        return processRequest((AssetKey)obj);
    }

    protected AssetData processResult(AssetKey assetkey, AssetData assetdata)
    {
        Debug.Printf("AssetCache: saving cached data for key %s", new Object[] {
            assetkey.toString()
        });
        if (assetdata != null)
        {
            AssetResponseCacher._2D_get0(AssetResponseCacher.this).insertOrReplace(new CachedAsset(assetkey.toString(), assetdata.getStatus(), assetdata.getData(), false));
        }
        return assetdata;
    }

    protected volatile Object processResult(Object obj, Object obj1)
    {
        return processResult((AssetKey)obj, (AssetData)obj1);
    }

    (RequestSource requestsource, Executor executor)
    {
        this$0 = AssetResponseCacher.this;
        super(requestsource, executor);
    }
}
