package com.lumiyaviewer.lumiya.render.tex;

public enum TextureClass {
    Prim("textures"),
    Sculpt("sculpt"),
    Baked("baked"),
    Asset("asset"),
    Terrain("terrain");
    
    private final String storePath;

    private TextureClass(String str) {
        this.storePath = str;
    }

    public final String getStorePath() {
        return this.storePath;
    }
}
