package com.linkpoint.slproto.baker

enum class SLAvatarGlobalColor {
    skin_color(IntArray{111, 110, 108}),
    hair_color(IntArray{114, 113, 115, 112}),
    eye_color(IntArray{99, 98})
    
    private IntArray paramIDs

    private SLAvatarGlobalColor(IntArray iArr) {
        this.paramIDs = iArr
    }

    public IntArray getParamIDs() {
        return this.paramIDs
    }
}
