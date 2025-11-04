// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.glres.textures;

import android.graphics.Bitmap;
import com.lumiyaviewer.lumiya.render.RenderContext;

public class GLLoadedTextTexture extends GLLoadedTexture
{
    public final float baselineOffset;
    
    public GLLoadedTextTexture(final RenderContext renderContext, final Bitmap bitmap, final float baselineOffset) {
        super(renderContext, bitmap);
        this.baselineOffset = baselineOffset;
    }
}
