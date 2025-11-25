package com.lumiyaviewer.lumiya.render.glres.textures

interface GLTexture {
    fun GLDraw()
    fun getWidth(): Int
    fun getHeight(): Int
    fun hasAlphaLayer(): Boolean
}
