package com.lumiyaviewer.lumiya.slproto.baker;

/* loaded from: classes.dex */
public enum SLAvatarGlobalColor {
    skin_color(new int[]{111, 110, 108}),
    hair_color(new int[]{114, 113, 115, 112}),
    eye_color(new int[]{99, 98});

    private int[] paramIDs;

    SLAvatarGlobalColor(int[] iArr) {
        this.paramIDs = iArr;
    }

    /* renamed from: values, reason: to resolve conflict with enum method */
    public static SLAvatarGlobalColor[] valuesCustom() {
        return values();
    }

    public int[] getParamIDs() {
        return this.paramIDs;
    }
}
