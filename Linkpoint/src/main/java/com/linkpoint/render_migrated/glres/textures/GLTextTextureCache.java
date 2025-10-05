package com.linkpoint.render.glres.textures;

import com.linkpoint.render.RenderContext;
import com.linkpoint.render.glres.GLLoadQueue;
import com.linkpoint.render.glres.GLResourceCache;
import com.linkpoint.res.ResourceConsumer;
import com.linkpoint.res.text.DrawableTextBitmap;
import com.linkpoint.res.text.DrawableTextCache;
import com.linkpoint.res.text.DrawableTextParams;

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
