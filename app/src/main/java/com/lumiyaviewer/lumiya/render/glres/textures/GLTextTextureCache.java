package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;

public class GLTextTextureCache extends GLResourceCache<DrawableTextParams, DrawableTextBitmap, GLLoadedTextTexture> {
    private final DrawableTextCache drawableTextCache;

    public GLTextTextureCache(GLLoadQueue gLLoadQueue, DrawableTextCache drawableTextCache2) {
        super(gLLoadQueue);
        this.drawableTextCache = drawableTextCache2;
    }

    /* access modifiers changed from: protected */
    public void CancelRawResource(ResourceConsumer resourceConsumer) {
        this.drawableTextCache.CancelRequest(resourceConsumer);
    }

    /* access modifiers changed from: protected */
    public int GetResourceSize(DrawableTextBitmap drawableTextBitmap) {
        return 0;
    }

    /* access modifiers changed from: protected */
    public GLLoadedTextTexture LoadResource(DrawableTextParams drawableTextParams, DrawableTextBitmap drawableTextBitmap, RenderContext renderContext) {
        return new GLLoadedTextTexture(renderContext, drawableTextBitmap.getBitmap(), drawableTextBitmap.getBaselineOffset());
    }

    /* access modifiers changed from: protected */
    public void RequestRawResource(DrawableTextParams drawableTextParams, ResourceConsumer resourceConsumer) {
        this.drawableTextCache.RequestResource(drawableTextParams, resourceConsumer);
    }
}
