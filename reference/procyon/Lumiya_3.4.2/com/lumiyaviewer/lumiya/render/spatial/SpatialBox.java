// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

public class SpatialBox
{
    protected final float s;
    protected final float x;
    protected final float xs;
    protected final float y;
    protected final float ys;
    protected final float zs;
    
    public SpatialBox(final float x, final float y, final float s, final float xs, final float ys, final float zs) {
        this.x = x;
        this.y = y;
        this.s = s;
        this.xs = xs;
        this.ys = ys;
        this.zs = zs;
    }
}
