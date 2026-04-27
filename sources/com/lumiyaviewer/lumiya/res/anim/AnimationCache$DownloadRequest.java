// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.anim;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

// Referenced classes of package com.lumiyaviewer.lumiya.res.anim:
//            AnimationCache

private class this._cls0 extends ResourceRequest
    implements com.lumiyaviewer.lumiya.react.st, com.lumiyaviewer.lumiya.react.st
{

    private Subscription assetSubscription;
    final AnimationCache this$0;

    public void cancelRequest()
    {
        Subscription subscription = assetSubscription;
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        super.cancelRequest();
    }

    public void completeRequest(AnimationData animationdata)
    {
        Subscription subscription = assetSubscription;
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
        super.completeRequest(animationdata);
    }

    public volatile void completeRequest(Object obj)
    {
        completeRequest((AnimationData)obj);
    }

    public void execute()
    {
        AssetResponseCacher assetresponsecacher = (AssetResponseCacher)AnimationCache._2D_get0(AnimationCache.this).get();
        if (assetresponsecacher != null)
        {
            assetSubscription = assetresponsecacher.getPool().subscribe(AssetKey.createAssetKey(null, null, (UUID)getParams(), 20), LoaderExecutor.getInstance(), this, this);
            return;
        } else
        {
            completeRequest(((AnimationData) (null)));
            return;
        }
    }

    public void onData(AssetData assetdata)
    {
        if (assetdata == null || assetdata.getData() == null || assetdata.getStatus() != 1) goto _L2; else goto _L1
_L1:
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(assetdata.getData());
        assetdata = new AnimationData((UUID)getParams(), bytearrayinputstream);
        bytearrayinputstream.close();
_L3:
        completeRequest(assetdata);
        return;
        IOException ioexception;
        ioexception;
        assetdata = null;
_L4:
        Debug.Warning(ioexception);
        if (true) goto _L3; else goto _L2
_L2:
        completeRequest(((AnimationData) (null)));
        return;
        ioexception;
          goto _L4
    }

    public volatile void onData(Object obj)
    {
        onData((AssetData)obj);
    }

    public void onError(Throwable throwable)
    {
        completeRequest(((AnimationData) (null)));
    }

    seCacher(UUID uuid, ResourceManager resourcemanager)
    {
        this$0 = AnimationCache.this;
        super(uuid, resourcemanager);
    }
}
