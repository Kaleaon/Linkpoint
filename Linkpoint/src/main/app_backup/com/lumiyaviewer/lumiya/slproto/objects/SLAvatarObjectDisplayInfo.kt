package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID

class SLAvatarObjectDisplayInfo(
    override val name: String?,
    val info: SLObjectInfo,
    override val distance: Float,
    val children: List<SLObjectDisplayInfo>,
    val implicitlyAdded: Boolean
) : SLObjectDisplayInfo {
    override val localID: Int get() = info.localID
    override fun getUUID(): UUID = info.getUUID()
    override fun getPosition(): LLVector3 = info.getPosition()
    override fun getRotation(): LLQuaternion? = info.getRotation()
    override fun getScale(): LLVector3 = info.getScale()
}
