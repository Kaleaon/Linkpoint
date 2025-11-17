package com.linkpoint.render.picking

import com.linkpoint.render.RenderContext

interface IntersectPickable {
    fun PickObject(
        context: RenderContext,
        x: Float,
        y: Float,
        z: Float,
    ): ObjectIntersectInfo
}
