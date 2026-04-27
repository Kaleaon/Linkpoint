// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.tex;

public enum TextureClass
{
    Asset("Asset", 3, "asset"), 
    Baked("Baked", 2, "baked"), 
    Prim("Prim", 0, "textures"), 
    Sculpt("Sculpt", 1, "sculpt"), 
    Terrain("Terrain", 4, "terrain");
    
    private final String storePath;
    
    private TextureClass(final String name, final int ordinal, final String storePath) {
        this.storePath = storePath;
    }
    
    public final String getStorePath() {
        return this.storePath;
    }
}
