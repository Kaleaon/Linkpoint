package com.linkpoint.render.picking

import com.linkpoint.slproto.objects.SLObjectInfo

class ObjectIntersectInfo {
    val IntersectInfo intersectInfo
    val SLObjectInfo objInfo
    val Float pickDepth

    public ObjectIntersectInfo(IntersectInfo intersectInfo, SLObjectInfo sLObjectInfo, Float f) {
        this.intersectInfo = intersectInfo
        this.objInfo = sLObjectInfo
        this.pickDepth = f
    }
}
