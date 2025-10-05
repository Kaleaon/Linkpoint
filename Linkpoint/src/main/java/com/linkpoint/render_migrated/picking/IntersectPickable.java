package com.linkpoint.render.picking;

import com.linkpoint.render.RenderContext;

public interface IntersectPickable {
    ObjectIntersectInfo PickObject(RenderContext renderContext, float f, float f2, float f3);
}
