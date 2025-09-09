package com.lumiyaviewer.lumiya.render.picking;

import com.lumiyaviewer.lumiya.render.RenderContext;

public interface IntersectPickable {
    ObjectIntersectInfo PickObject(RenderContext renderContext, float f, float f2, float f3);
}
