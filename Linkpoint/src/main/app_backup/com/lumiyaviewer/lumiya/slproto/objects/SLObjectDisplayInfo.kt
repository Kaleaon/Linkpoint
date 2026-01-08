package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
import java.util.UUID

interface SLObjectDisplayInfo {
    val localID: Int
    val name: String?
    val distance: Float
    
    fun getUUID(): UUID
    fun getPosition(): LLVector3
    fun getRotation(): LLQuaternion?
    fun getScale(): LLVector3
}
