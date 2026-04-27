// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.StateAwareFragment;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;
import uk.co.senab.photoview.PhotoViewAttacher;

public class TextureViewFragment extends StateAwareFragment
{
    private class LoadAssetImageTask extends AsyncTask
        implements ResourceConsumer
    {

        private volatile OpenJPEG texture;
        private final Object textureReady;
        final TextureViewFragment this$0;

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj instanceof OpenJPEG)
            {
                texture = (OpenJPEG)obj;
            }
            obj = textureReady;
            obj;
            JVM INSTR monitorenter ;
            textureReady.notify();
            obj;
            JVM INSTR monitorexit ;
            return;
            Exception exception;
            exception;
            throw exception;
        }

        protected transient Bitmap doInBackground(UUID auuid[])
        {
            Debug.Printf("loading asset ID %s", new Object[] {
                auuid[0].toString()
            });
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(auuid[0], TextureClass.Asset), this);
            Object obj = textureReady;
            obj;
            JVM INSTR monitorenter ;
            if (texture != null) goto _L2; else goto _L1
_L1:
            Debug.Printf("asset ID %s is not available, waiting", new Object[] {
                auuid[0].toString()
            });
            textureReady.wait();
            Debug.Printf("done waiting for asset ID %s", new Object[] {
                auuid[0].toString()
            });
_L4:
            obj;
            JVM INSTR monitorexit ;
            InterruptedException interruptedexception;
            if (texture != null)
            {
                return texture.getAsBitmap();
            } else
            {
                return null;
            }
            interruptedexception;
            Debug.Printf("interrupted while waiting for asset ID %s", new Object[] {
                auuid[0].toString()
            });
            obj;
            JVM INSTR monitorexit ;
            return null;
_L2:
            Debug.Printf("asset ID %s is already available", new Object[] {
                auuid[0].toString()
            });
            if (true) goto _L4; else goto _L3
_L3:
            auuid;
            throw auuid;
        }

        protected volatile Object doInBackground(Object aobj[])
        {
            return doInBackground((UUID[])aobj);
        }

        protected void onPostExecute(Bitmap bitmap)
        {
            if (isFragmentStarted() && TextureViewFragment._2D_get2(TextureViewFragment.this) != null && TextureViewFragment._2D_get0(TextureViewFragment.this) != null)
            {
                if (bitmap != null)
                {
                    TextureViewFragment._2D_get0(TextureViewFragment.this).showContent(null);
                    TextureViewFragment._2D_get2(TextureViewFragment.this).setImageBitmap(bitmap);
                    TextureViewFragment._2D_get1(TextureViewFragment.this).update();
                } else
                {
                    TextureViewFragment._2D_get0(TextureViewFragment.this).showMessage(getString(0x7f09011c));
                    TextureViewFragment._2D_get2(TextureViewFragment.this).setImageBitmap(null);
                    TextureViewFragment._2D_get1(TextureViewFragment.this).update();
                }
            }
            TextureViewFragment._2D_set0(TextureViewFragment.this, null);
        }

        protected volatile void onPostExecute(Object obj)
        {
            onPostExecute((Bitmap)obj);
        }

        protected void onPreExecute()
        {
            if (isFragmentStarted() && TextureViewFragment._2D_get0(TextureViewFragment.this) != null)
            {
                TextureViewFragment._2D_get0(TextureViewFragment.this).showLoading();
            }
        }

        private LoadAssetImageTask()
        {
            this$0 = TextureViewFragment.this;
            super();
            textureReady = new Object();
        }

        LoadAssetImageTask(LoadAssetImageTask loadassetimagetask)
        {
            this();
        }
    }


    private static final String ASSET_UUID_KEY = "assetUUID";
    private LoadAssetImageTask loadAssetImageTask;
    private LoadingLayout loadingLayout;
    private PhotoViewAttacher photoViewAttacher;
    private ImageView textureImageView;

    static LoadingLayout _2D_get0(TextureViewFragment textureviewfragment)
    {
        return textureviewfragment.loadingLayout;
    }

    static PhotoViewAttacher _2D_get1(TextureViewFragment textureviewfragment)
    {
        return textureviewfragment.photoViewAttacher;
    }

    static ImageView _2D_get2(TextureViewFragment textureviewfragment)
    {
        return textureviewfragment.textureImageView;
    }

    static LoadAssetImageTask _2D_set0(TextureViewFragment textureviewfragment, LoadAssetImageTask loadassetimagetask)
    {
        textureviewfragment.loadAssetImageTask = loadassetimagetask;
        return loadassetimagetask;
    }

    public TextureViewFragment()
    {
        loadAssetImageTask = null;
    }

    public static Bundle makeArguments(UUID uuid, UUID uuid1)
    {
        Bundle bundle = new Bundle();
        bundle.putString("activeAgentUUID", uuid.toString());
        bundle.putString("assetUUID", uuid1.toString());
        return bundle;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f0400aa, viewgroup, false);
        loadingLayout = (LoadingLayout)layoutinflater.findViewById(0x7f1000bd);
        textureImageView = (ImageView)layoutinflater.findViewById(0x7f100293);
        photoViewAttacher = new PhotoViewAttacher(textureImageView);
        return layoutinflater;
    }

    public void onStart()
    {
        super.onStart();
        UUID uuid = UUIDPool.getUUID(getArguments().getString("assetUUID"));
        if (uuid != null)
        {
            if (loadAssetImageTask != null)
            {
                loadAssetImageTask.cancel(true);
                loadAssetImageTask = null;
            }
            loadAssetImageTask = new LoadAssetImageTask(null);
            loadAssetImageTask.execute(new UUID[] {
                uuid
            });
        }
    }

    public void onStop()
    {
        if (loadAssetImageTask != null)
        {
            loadAssetImageTask.cancel(true);
            loadAssetImageTask = null;
        }
        super.onStop();
    }
}
