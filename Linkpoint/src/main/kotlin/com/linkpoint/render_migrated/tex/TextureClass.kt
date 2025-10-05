package com.linkpoint.render.tex

enum class TextureClass {
    Prim("textures"),
    Sculpt("sculpt"),
    Baked("baked"),
    Asset("asset"),
    Terrain("terrain")
    
    private val String storePath

    private TextureClass(String str) {
        this.storePath = str
    }

    val String getStorePath() {
        return this.storePath
    }
}
