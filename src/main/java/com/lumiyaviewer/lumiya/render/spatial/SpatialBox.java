package com.lumiyaviewer.lumiya.render.spatial;

public class SpatialBox {
    protected final float s;
    protected final float x;
    protected final float xs;
    protected final float y;
    protected final float ys;
    protected final float zs;

    public SpatialBox(float f, float f2, float f3, float f4, float f5, float f6) {
        this.x = f;
        this.y = f2;
        this.s = f3;
        this.xs = f4;
        this.ys = f5;
        this.zs = f6;
    }
}
