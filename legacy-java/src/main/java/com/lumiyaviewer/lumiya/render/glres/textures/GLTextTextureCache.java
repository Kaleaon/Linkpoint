package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.text.DrawableTextBitmap;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextParams;

/* loaded from: classes.dex */
public class GLTextTextureCache extends GLResourceCache<DrawableTextParams, DrawableTextBitmap, GLLoadedTextTexture> {
    private final DrawableTextCache drawableTextCache;

    public GLTextTextureCache(GLLoadQueue gLLoadQueue, DrawableTextCache drawableTextCache) {
        super(gLLoadQueue);
        this.drawableTextCache = drawableTextCache;
    }

    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    protected void CancelRawResource(ResourceConsumer resourceConsumer) {
        this.drawableTextCache.CancelRequest(resourceConsumer);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public int GetResourceSize(DrawableTextBitmap drawableTextBitmap) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public GLLoadedTextTexture LoadResource(DrawableTextParams drawableTextParams, DrawableTextBitmap drawableTextBitmap, RenderContext renderContext) {
        return new GLLoadedTextTexture(renderContext, drawableTextBitmap.getBitmap(), drawableTextBitmap.getBaselineOffset());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.render.glres.GLResourceCache
    public void RequestRawResource(DrawableTextParams drawableTextParams, ResourceConsumer resourceConsumer) {
        this.drawableTextCache.RequestResource(drawableTextParams, resourceConsumer);
    }
}
