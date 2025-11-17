package com.linkpoint.slproto.objects

import com.linkpoint.render.spatial.DrawListObjectEntry
import com.linkpoint.render.spatial.DrawListPrimEntry

open class SLObjectPrimInfo : SLObjectInfo() {
    override fun createDrawListEntry(): DrawListObjectEntry {
        return DrawListPrimEntry(this)
    }

    override fun isAvatar(): Boolean {
        return false
    }
}