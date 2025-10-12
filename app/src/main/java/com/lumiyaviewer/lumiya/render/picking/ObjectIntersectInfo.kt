package com.lumiyaviewer.lumiya.render.picking

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo

data class ObjectIntersectInfo(
    val intersectInfo: IntersectInfo,
    val objInfo: SLObjectInfo,
    val pickDepth: Float
)