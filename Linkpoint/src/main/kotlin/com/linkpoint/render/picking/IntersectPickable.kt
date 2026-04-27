package com.linkpoint.render.picking

import com.linkpoint.render.RenderContext

interface IntersectPickable {
    fun PickObject(renderContext: RenderContext, f: Float, f2: Float, f3: Float): ObjectIntersectInfo
}
