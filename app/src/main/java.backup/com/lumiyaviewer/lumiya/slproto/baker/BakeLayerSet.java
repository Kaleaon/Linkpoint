package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;

public class BakeLayerSet {
    public BakedTextureIndex bakedTextureIndex;
    public boolean clear_alpha;
    public int height;
    public BakeLayer[] layers;
    public BakeLayer[] maskLayers;
    public int width;

    public BakeLayerSet(BakedTextureIndex bakedTextureIndex2, int i, int i2, boolean z, BakeLayer[] bakeLayerArr, BakeLayer[] bakeLayerArr2) {
        this.bakedTextureIndex = bakedTextureIndex2;
        this.width = i;
        this.height = i2;
        this.clear_alpha = z;
        this.layers = bakeLayerArr;
        this.maskLayers = bakeLayerArr2;
    }
}
