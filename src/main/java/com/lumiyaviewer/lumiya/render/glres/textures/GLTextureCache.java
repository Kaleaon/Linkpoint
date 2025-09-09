package com.lumiyaviewer.lumiya.render.glres.textures;

import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.GLResourceCache;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;

public class GLTextureCache extends GLResourceCache<DrawableTextureParams, OpenJPEG, GLLoadedTexture> {
    public GLTextureCache(GLLoadQueue gLLoadQueue) {
        super(gLLoadQueue);
    }

    protected void CancelRawResource(ResourceConsumer resourceConsumer) {
        TextureCache.getInstance().CancelRequest(resourceConsumer);
    }

    protected int GetResourceSize(OpenJPEG openJPEG) {
        return openJPEG.getLoadedSize();
    }

    protected GLLoadedTexture LoadResource(DrawableTextureParams drawableTextureParams, OpenJPEG openJPEG, RenderContext renderContext) {
        return new GLLoadedTexture(renderContext, openJPEG);
    }

    protected void RequestRawResource(DrawableTextureParams drawableTextureParams, ResourceConsumer resourceConsumer) {
        TextureCache.getInstance().RequestResource(drawableTextureParams, resourceConsumer);
    }
}
