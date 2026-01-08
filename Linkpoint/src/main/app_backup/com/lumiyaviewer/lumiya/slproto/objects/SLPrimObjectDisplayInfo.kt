package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID

class SLPrimObjectDisplayInfo(val info: SLObjectInfo, override val distance: Float) : SLObjectDisplayInfo {
    override val localID: Int get() = info.localID
    override val name: String? get() = info.name
    
    override fun getUUID(): UUID = info.getUUID()
    override fun getPosition(): LLVector3 = info.getPosition()
    override fun getRotation(): LLQuaternion? = info.getRotation()
    override fun getScale(): LLVector3 = info.getScale()
}
