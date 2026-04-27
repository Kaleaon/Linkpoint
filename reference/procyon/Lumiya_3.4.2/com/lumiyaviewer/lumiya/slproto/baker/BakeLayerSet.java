// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.baker;

import com.lumiyaviewer.lumiya.slproto.avatar.BakedTextureIndex;

public class BakeLayerSet
{
    public BakedTextureIndex bakedTextureIndex;
    public boolean clear_alpha;
    public int height;
    public BakeLayer[] layers;
    public BakeLayer[] maskLayers;
    public int width;
    
    public BakeLayerSet(final BakedTextureIndex bakedTextureIndex, final int width, final int height, final boolean clear_alpha, final BakeLayer[] layers, final BakeLayer[] maskLayers) {
        this.bakedTextureIndex = bakedTextureIndex;
        this.width = width;
        this.height = height;
        this.clear_alpha = clear_alpha;
        this.layers = layers;
        this.maskLayers = maskLayers;
    }
}
