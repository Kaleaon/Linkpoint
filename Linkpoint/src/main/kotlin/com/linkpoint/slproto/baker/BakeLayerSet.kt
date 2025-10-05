package com.linkpoint.slproto.baker

import com.linkpoint.slproto.avatar.BakedTextureIndex

class BakeLayerSet {
    public BakedTextureIndex bakedTextureIndex
    public Boolean clear_alpha
    public Int height
    public BakeLayer[] layers
    public BakeLayer[] maskLayers
    public Int width

    public BakeLayerSet(BakedTextureIndex bakedTextureIndex2, Int i, Int i2, Boolean z, BakeLayer[] bakeLayerArr, BakeLayer[] bakeLayerArr2) {
        this.bakedTextureIndex = bakedTextureIndex2
        this.width = i
        this.height = i2
        this.clear_alpha = z
        this.layers = bakeLayerArr
        this.maskLayers = bakeLayerArr2
    }
}
