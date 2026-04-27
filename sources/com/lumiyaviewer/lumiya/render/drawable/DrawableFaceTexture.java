// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.drawable;

import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLCleanable;
import com.lumiyaviewer.lumiya.render.glres.GLResourceManager;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableFaceTexture
    implements ResourceConsumer, GLCleanable
{

    private final DrawableTextureParams drawableTextureParams;
    private GLTextureCache glTextureCache;
    private volatile boolean hasAlphaLayer;
    private volatile GLLoadedTexture loadedTexture;
    private boolean textureRequested;

    public DrawableFaceTexture(DrawableTextureParams drawabletextureparams)
    {
        glTextureCache = null;
        textureRequested = false;
        loadedTexture = null;
        hasAlphaLayer = false;
        drawableTextureParams = drawabletextureparams;
    }

    public void GLCleanup()
    {
        if (glTextureCache != null)
        {
            glTextureCache.CancelRequest(this);
        }
        textureRequested = false;
        loadedTexture = null;
        hasAlphaLayer = false;
    }

    public final boolean GLDraw(RenderContext rendercontext)
    {
        GLLoadedTexture glloadedtexture = loadedTexture;
        if (glloadedtexture != null)
        {
            glloadedtexture.GLDraw();
            return true;
        }
        if (!textureRequested)
        {
            textureRequested = true;
            glTextureCache = rendercontext.drawableStore.glTextureCache;
            rendercontext.glResourceManager.addCleanable(this);
            glTextureCache.RequestResource(drawableTextureParams, this);
        }
        return false;
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        if (obj instanceof GLLoadedTexture)
        {
            obj = (GLLoadedTexture)obj;
            loadedTexture = ((GLLoadedTexture) (obj));
            hasAlphaLayer = ((GLLoadedTexture) (obj)).hasAlphaLayer();
        } else
        if (obj == null)
        {
            loadedTexture = null;
            hasAlphaLayer = false;
            return;
        }
    }

    boolean hasAlphaLayer()
    {
        return hasAlphaLayer;
    }
}
