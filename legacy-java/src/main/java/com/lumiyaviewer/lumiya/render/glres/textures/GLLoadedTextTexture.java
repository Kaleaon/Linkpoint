package com.lumiyaviewer.lumiya.render.glres.textures;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.render.RenderContext;

/* loaded from: classes.dex */
public class GLLoadedTextTexture extends GLLoadedTexture {
    public final float baselineOffset;

    public GLLoadedTextTexture(RenderContext renderContext, Bitmap bitmap, float f) {
        super(renderContext, bitmap);
        this.baselineOffset = f;
    }
}
