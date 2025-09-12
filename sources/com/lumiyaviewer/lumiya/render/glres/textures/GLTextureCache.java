// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.render.glres.GLSizedResource;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;

// Referenced classes of package com.lumiyaviewer.lumiya.render.glres.textures:
//            GLLoadedTexture

public class GLTextureCache extends GLResourceCache
{

    public GLTextureCache(GLLoadQueue glloadqueue)
    {
        super(glloadqueue);
    }

    protected void CancelRawResource(ResourceConsumer resourceconsumer)
    {
        TextureCache.getInstance().CancelRequest(resourceconsumer);
    }

    protected int GetResourceSize(OpenJPEG openjpeg)
    {
        return openjpeg.getLoadedSize();
    }

    protected volatile int GetResourceSize(Object obj)
    {
        return GetResourceSize((OpenJPEG)obj);
    }

    protected volatile GLSizedResource LoadResource(Object obj, Object obj1, RenderContext rendercontext)
    {
        return LoadResource((DrawableTextureParams)obj, (OpenJPEG)obj1, rendercontext);
    }

    protected GLLoadedTexture LoadResource(DrawableTextureParams drawabletextureparams, OpenJPEG openjpeg, RenderContext rendercontext)
    {
        return new GLLoadedTexture(rendercontext, openjpeg);
    }

    protected void RequestRawResource(DrawableTextureParams drawabletextureparams, ResourceConsumer resourceconsumer)
    {
        TextureCache.getInstance().RequestResource(drawabletextureparams, resourceconsumer);
    }

    protected volatile void RequestRawResource(Object obj, ResourceConsumer resourceconsumer)
    {
        RequestRawResource((DrawableTextureParams)obj, resourceconsumer);
    }
}
