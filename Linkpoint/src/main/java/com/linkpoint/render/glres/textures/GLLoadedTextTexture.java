package com.linkpoint.render.glres.textures;

import android.graphics.Bitmap;
import com.linkpoint.render.RenderContext;

public class GLLoadedTextTexture extends GLLoadedTexture {
    public final float baselineOffset;

    public GLLoadedTextTexture(RenderContext renderContext, Bitmap bitmap, float f) {
        super(renderContext, bitmap);
        this.baselineOffset = f;
    }
}
