package com.lumiyaviewer.lumiya.render

import com.lumiyaviewer.lumiya.render.cache.DrawableAvatarCache
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache

class DrawableStore {
    var glTextureCache: GLTextureCache = GLTextureCache()
    var textTextureCache: GLTextTextureCache = GLTextTextureCache()
    lateinit var glTerrainTextureCache: GLTerrainTextureCache
    lateinit var terrainGeometryCache: TerrainGeometryCache
    val drawableAvatarCache = DrawableAvatarCache()
}
