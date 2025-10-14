package com.lumiyaviewer.lumiya.render.glres.textures

import android.graphics.Bitmap
import com.lumiyaviewer.lumiya.render.RenderContext

class GLLoadedTextTexture(
    renderContext: RenderContext,
    bitmap: Bitmap,
    val baselineOffset: Float,
) : GLLoadedTexture(renderContext, bitmap)