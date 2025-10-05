package com.linkpoint.slproto.objects

import com.google.common.collect.ImmutableList

class SLPrimObjectDisplayInfoWithChildren(
    objectInfo: SLObjectInfo,
    distance: Float,
    val children: ImmutableList<SLObjectDisplayInfo>,
    private val implicitlyAdded: Boolean
) : SLPrimObjectDisplayInfo(objectInfo, distance), SLObjectDisplayInfo.HasChildrenObjects {

    override fun getChildren(): ImmutableList<SLObjectDisplayInfo> {
        return children
    }

    fun isImplicitlyAdded(): Boolean {
        return implicitlyAdded
    }
}