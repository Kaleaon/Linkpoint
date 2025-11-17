package com.linkpoint.render.picking

import com.linkpoint.slproto.objects.SLObjectInfo

data class ObjectIntersectInfo(
    val intersectInfo: IntersectInfo,
    val objInfo: SLObjectInfo,
    val pickDepth: Float,
)
