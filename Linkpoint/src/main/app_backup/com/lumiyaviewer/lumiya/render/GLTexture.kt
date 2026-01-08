package com.lumiyaviewer.lumiya.render

interface GLTexture {
    fun getWidth(): Int
    fun getHeight(): Int
    fun getNumComponents(): Int
    fun hasAlphaLayer(): Boolean
    fun getLoadedSize(): Int
    fun setAsTexture(): Int
}
