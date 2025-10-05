package com.linkpoint.slproto.objects

import com.google.common.collect.ImmutableList
import java.util.UUID

class SLAvatarObjectDisplayInfo(
    name: String?,
    objectInfo: SLObjectInfo,
    distance: Float,
    val children: ImmutableList<SLObjectDisplayInfo>,
    private val implicitlyAdded: Boolean
) : SLObjectDisplayInfo(objectInfo.localID, name, distance, objectInfo.hierLevel), 
    SLObjectDisplayInfo.HasChildrenObjects {
    
    val uuid: UUID = objectInfo.id

    override fun getChildren(): ImmutableList<SLObjectDisplayInfo> {
        return children
    }

    fun isImplicitlyAdded(): Boolean {
        return implicitlyAdded
    }
}