package com.lumiyaviewer.lumiya.render.tex;

/* loaded from: classes.dex */
public enum TextureClass {
    Prim("textures"),
    Sculpt("sculpt"),
    Baked("baked"),
    Asset("asset"),
    Terrain("terrain");

    private final String storePath;

    TextureClass(String str) {
        this.storePath = str;
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static TextureClass[] valuesCustom() {
        return values();
    }

    public final String getStorePath() {
        return this.storePath;
    }
}
