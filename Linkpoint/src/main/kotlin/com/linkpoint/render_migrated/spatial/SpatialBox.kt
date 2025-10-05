package com.linkpoint.render.spatial

class SpatialBox {
    protected val Float s
    protected val Float x
    protected val Float xs
    protected val Float y
    protected val Float ys
    protected val Float zs

    public SpatialBox(Float f, Float f2, Float f3, Float f4, Float f5, Float f6) {
        this.x = f
        this.y = f2
        this.s = f3
        this.xs = f4
        this.ys = f5
        this.zs = f6
    }
}
