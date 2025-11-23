package com.linkpoint.slproto.objects

import com.linkpoint.slproto.types.LLVector3
import java.util.UUID

interface SLObjectDisplayInfo {
    fun getLocalID(): Int
    fun getUUID(): UUID
    fun getPosition(): LLVector3
    fun getRotation(): LLVector3 // Or Quaternion
    fun getScale(): LLVector3
}
