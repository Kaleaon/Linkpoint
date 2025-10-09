package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.render.RenderContext

interface IntersectPickable {
    fun PickObject(renderContext: RenderContext, x: Float, y: Float, z: Float): ObjectIntersectInfo?
}
