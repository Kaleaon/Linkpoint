package com.linkpoint.render.glres.textures

import android.graphics.Bitmap
import com.linkpoint.render.RenderContext

class GLLoadedTextTexture(
    renderContext: RenderContext,
    bitmap: Bitmap,
    val baselineOffset: Float,
) : GLLoadedTexture(renderContext, bitmap)