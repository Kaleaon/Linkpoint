package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.render.RenderContext

interface IntersectPickable {
    fun PickObject(context: RenderContext, x: Float, y: Float, z: Float): ObjectIntersectInfo
}