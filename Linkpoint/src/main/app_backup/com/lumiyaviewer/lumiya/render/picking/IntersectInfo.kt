package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.slproto.types.LLVector4

class IntersectInfo {
    var intersectPoint: LLVector4? = null
    var s: Float = 0f
    var t: Float = 0f

    constructor()
    
    constructor(intersectPoint: LLVector4, s: Float, t: Float) {
        this.intersectPoint = intersectPoint
        this.s = s
        this.t = t
    }

    override fun toString(): String {
        return "IntersectInfo{intersectPoint=$intersectPoint, s=$s, t=$t}"
    }
}
