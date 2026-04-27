// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.inventory;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import java.util.UUID;
import uk.co.senab.photoview.PhotoViewAttacher;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.inventory:
//            TextureViewFragment

private class <init> extends AsyncTask
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

    private Q()
    {
        this$0 = TextureViewFragment.this;
        super();
        textureReady = new Object();
    }

    textureReady(textureReady textureready)
    {
        this();
    }
}
