// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            ImageAssetView

private class <init> extends AsyncTask
    implements ResourceConsumer
{

    private volatile OpenJPEG texture;
    private final Object textureReady;
    final ImageAssetView this$0;

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
        ImageAssetView._2D_set0(ImageAssetView.this, bitmap);
        if (ImageAssetView._2D_get0(ImageAssetView.this))
        {
            requestLayout();
        }
        invalidate();
        ImageAssetView._2D_set1(ImageAssetView.this, null);
    }

    protected volatile void onPostExecute(Object obj)
    {
        onPostExecute((Bitmap)obj);
    }

    private I()
    {
        this$0 = ImageAssetView.this;
        super();
        textureReady = new Object();
    }

    textureReady(textureReady textureready)
    {
        this();
    }
}
