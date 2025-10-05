package com.linkpoint.render.tex

enum class TextureClass(val storePath: String) {
    Prim("textures"),
    Sculpt("sculpt"),
    Baked("baked"),
    Asset("asset"),
    Terrain("terrain")
}