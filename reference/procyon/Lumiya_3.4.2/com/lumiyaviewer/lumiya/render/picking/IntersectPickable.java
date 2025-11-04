// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.render.RenderContext;

public interface IntersectPickable
{
    ObjectIntersectInfo PickObject(final RenderContext p0, final float p1, final float p2, final float p3);
}
