// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.anim;

import android.content.res.AssetManager;
import com.google.common.collect.ImmutableSet;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.LumiyaApp;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.render.avatar.AnimationData;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.LoaderExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetData;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.assets.AssetResponseCacher;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AnimationCache extends ResourceMemoryCache
{
    private static class AssetLoadRequest extends ResourceRequest
        implements Runnable
    {

        private final String assetName;

        public void cancelRequest()
        {
            LoaderExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute()
        {
            LoaderExecutor.getInstance().execute(this);
        }

        public void run()
        {
            Object obj;
            Object obj1;
            obj1 = null;
            obj = LumiyaApp.getAssetManager();
            if (obj == null)
            {
                break MISSING_BLOCK_LABEL_220;
            }
            obj = ((AssetManager) (obj)).open((new StringBuilder()).append("anims/").append(assetName).toString());
            if (obj == null) goto _L2; else goto _L1
_L1:
            AnimationData animationdata = new AnimationData((UUID)getParams(), ((InputStream) (obj)));
            obj1 = animationdata;
            if (animationdata.getPriority() < 6)
            {
                break MISSING_BLOCK_LABEL_101;
            }
            Debug.Printf("Animation: priority %d loaded from asset %s", new Object[] {
                Integer.valueOf(animationdata.getPriority()), assetName
            });
            obj1 = animationdata;
_L8:
            Object obj2;
            obj2 = obj1;
            if (obj == null)
            {
                break MISSING_BLOCK_LABEL_113;
            }
            ((InputStream) (obj)).close();
            obj2 = obj1;
_L3:
            completeRequest(obj2);
            return;
            obj;
            Debug.Warning(((Throwable) (obj)));
            obj2 = obj1;
              goto _L3
            obj2;
            obj = null;
_L7:
            Debug.Warning(((Throwable) (obj2)));
            obj2 = obj;
            if (obj1 == null) goto _L3; else goto _L4
_L4:
            ((InputStream) (obj1)).close();
            obj2 = obj;
              goto _L3
            obj1;
            Debug.Warning(((Throwable) (obj1)));
            obj2 = obj;
              goto _L3
            obj1;
            obj = null;
_L6:
            if (obj != null)
            {
                try
                {
                    ((InputStream) (obj)).close();
                }
                // Misplaced declaration of an exception variable
                catch (Object obj)
                {
                    Debug.Warning(((Throwable) (obj)));
                }
            }
            throw obj1;
            obj1;
            continue; /* Loop/switch isn't completed */
            obj2;
            obj = obj1;
            obj1 = obj2;
            if (true) goto _L6; else goto _L5
_L5:
            obj2;
            animationdata = null;
            obj1 = obj;
            obj = animationdata;
              goto _L7
            obj2;
            obj1 = obj;
            obj = animationdata;
              goto _L7
_L2:
            obj1 = null;
              goto _L8
            obj2 = null;
              goto _L3
        }

        AssetLoadRequest(UUID uuid, ResourceManager resourcemanager, String s)
        {
            super(uuid, resourcemanager);
            assetName = s;
        }
    }

    private class DownloadRequest extends ResourceRequest
        implements com.lumiyaviewer.lumiya.react.Subscription.OnData, com.lumiyaviewer.lumiya.react.Subscription.OnError
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

        DownloadRequest(UUID uuid, ResourceManager resourcemanager)
        {
            this$0 = AnimationCache.this;
            super(uuid, resourcemanager);
        }
    }

    private static class InstanceHolder
    {

        private static final AnimationCache Instance = new AnimationCache(null);

        static AnimationCache _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    private final ImmutableSet assetAnimations;
    private final AtomicReference assetResponseCacher;

    static AtomicReference _2D_get0(AnimationCache animationcache)
    {
        return animationcache.assetResponseCacher;
    }

    private AnimationCache()
    {
        com.google.common.collect.ImmutableSet.Builder builder;
        AssetManager assetmanager;
        assetResponseCacher = new AtomicReference(null);
        builder = ImmutableSet.builder();
        assetmanager = LumiyaApp.getAssetManager();
        if (assetmanager == null)
        {
            break MISSING_BLOCK_LABEL_48;
        }
        String as[] = assetmanager.list("anims");
        if (as != null)
        {
            try
            {
                builder.addAll(Arrays.asList(as));
            }
            catch (IOException ioexception)
            {
                Debug.Warning(ioexception);
            }
        }
        assetAnimations = builder.build();
        return;
    }

    AnimationCache(AnimationCache animationcache)
    {
        this();
    }

    public static AnimationCache getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    protected volatile ResourceRequest CreateNewRequest(Object obj, ResourceManager resourcemanager)
    {
        return CreateNewRequest((UUID)obj, resourcemanager);
    }

    protected ResourceRequest CreateNewRequest(UUID uuid, ResourceManager resourcemanager)
    {
        String s = uuid.toString();
        if (assetAnimations.contains(s))
        {
            return new AssetLoadRequest(uuid, resourcemanager, s);
        } else
        {
            return new DownloadRequest(uuid, resourcemanager);
        }
    }

    public void setAssetResponseCacher(AssetResponseCacher assetresponsecacher)
    {
        assetResponseCacher.set(assetresponsecacher);
    }
}
