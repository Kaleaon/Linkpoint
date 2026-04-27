package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

public class ObjectIntersectInfo {
    public final IntersectInfo intersectInfo;
    public final SLObjectInfo objInfo;
    public final float pickDepth;

    public ObjectIntersectInfo(IntersectInfo intersectInfo, SLObjectInfo sLObjectInfo, float f) {
        this.intersectInfo = intersectInfo;
        this.objInfo = sLObjectInfo;
        this.pickDepth = f;
    }
}
