package com.linkpoint.render.picking

import com.linkpoint.render.RenderContext

interface IntersectPickable {
    ObjectIntersectInfo PickObject(RenderContext renderContext, Float f, Float f2, Float f3)
}
