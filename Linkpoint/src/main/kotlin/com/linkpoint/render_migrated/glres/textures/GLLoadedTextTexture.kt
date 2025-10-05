package com.linkpoint.render.glres.textures

import android.graphics.Bitmap
import com.linkpoint.render.RenderContext

class GLLoadedTextTexture : GLLoadedTexture() {
    val Float baselineOffset

    public GLLoadedTextTexture(RenderContext renderContext, Bitmap bitmap, Float f) {
        super(renderContext, bitmap)
        this.baselineOffset = f
    }
}
