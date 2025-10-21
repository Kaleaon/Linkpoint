package com.linkpoint.slproto.baker

enum class SLAvatarGlobalColor {
    skin_color(Int[]{111, 110, 108}),
    hair_color(Int[]{114, 113, 115, 112}),
    eye_color(Int[]{99, 98})
    
    private Int[] paramIDs

    private SLAvatarGlobalColor(Int[] iArr) {
        this.paramIDs = iArr
    }

    public Int[] getParamIDs() {
        return this.paramIDs
    }
}
