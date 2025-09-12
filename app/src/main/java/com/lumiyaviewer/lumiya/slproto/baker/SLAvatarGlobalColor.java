package com.lumiyaviewer.lumiya.slproto.baker;

public enum SLAvatarGlobalColor {
    skin_color(new int[]{111, 110, 108}),
    hair_color(new int[]{114, 113, 115, 112}),
    eye_color(new int[]{99, 98});
    
    private int[] paramIDs;

    private SLAvatarGlobalColor(int[] iArr) {
        this.paramIDs = iArr;
    }

    public int[] getParamIDs() {
        return this.paramIDs;
    }
}
