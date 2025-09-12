// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLLoadedTextTexture

public class GLTextTextureCache extends GLResourceCache
{

    private final DrawableTextCache drawableTextCache;

    public GLTextTextureCache(GLLoadQueue glloadqueue, DrawableTextCache drawabletextcache)
    {
        super(glloadqueue);
        drawableTextCache = drawabletextcache;
    }

    protected void CancelRawResource(ResourceConsumer resourceconsumer)
    {
        drawableTextCache.CancelRequest(resourceconsumer);
    }

    protected int GetResourceSize(DrawableTextBitmap drawabletextbitmap)
    {
        return 0;
    }

    protected volatile int GetResourceSize(Object obj)
    {
        return GetResourceSize((DrawableTextBitmap)obj);
    }

    protected volatile GLSizedResource LoadResource(Object obj, Object obj1, RenderContext rendercontext)
    {
        return LoadResource((DrawableTextParams)obj, (DrawableTextBitmap)obj1, rendercontext);
    }

    protected GLLoadedTextTexture LoadResource(DrawableTextParams drawabletextparams, DrawableTextBitmap drawabletextbitmap, RenderContext rendercontext)
    {
        return new GLLoadedTextTexture(rendercontext, drawabletextbitmap.getBitmap(), drawabletextbitmap.getBaselineOffset());
    }

    protected void RequestRawResource(DrawableTextParams drawabletextparams, ResourceConsumer resourceconsumer)
    {
        drawableTextCache.RequestResource(drawabletextparams, resourceconsumer);
    }

    protected volatile void RequestRawResource(Object obj, ResourceConsumer resourceconsumer)
    {
        RequestRawResource((DrawableTextParams)obj, resourceconsumer);
    }
}
