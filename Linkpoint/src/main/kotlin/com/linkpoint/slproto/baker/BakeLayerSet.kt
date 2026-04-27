package com.linkpoint.slproto.baker

import com.linkpoint.slproto.avatar.BakedTextureIndex

class BakeLayerSet {
    public BakedTextureIndex bakedTextureIndex
    public Boolean clear_alpha
    public Int height
    public Array<BakeLayer> layers
    public Array<BakeLayer> maskLayers
    public Int width

    public BakeLayerSet(BakedTextureIndex bakedTextureIndex2, Int i, Int i2, Boolean z, Array<BakeLayer> bakeLayerArr, Array<BakeLayer> bakeLayerArr2) {
        this.bakedTextureIndex = bakedTextureIndex2
        this.width = i
        this.height = i2
        this.clear_alpha = z
        this.layers = bakeLayerArr
        this.maskLayers = bakeLayerArr2
    }
}
