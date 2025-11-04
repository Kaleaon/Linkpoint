// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;

public class ObjectIntersectInfo
{
    public final IntersectInfo intersectInfo;
    public final SLObjectInfo objInfo;
    public final float pickDepth;
    
    public ObjectIntersectInfo(final IntersectInfo intersectInfo, final SLObjectInfo objInfo, final float pickDepth) {
        this.intersectInfo = intersectInfo;
        this.objInfo = objInfo;
        this.pickDepth = pickDepth;
    }
}
