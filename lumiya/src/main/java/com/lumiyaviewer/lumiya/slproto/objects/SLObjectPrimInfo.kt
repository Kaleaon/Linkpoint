package com.lumiyaviewer.lumiya.slproto.objects

import com.lumiyaviewer.lumiya.render.spatial.DrawListObjectEntry
import com.lumiyaviewer.lumiya.render.spatial.DrawListPrimEntry

open class SLObjectPrimInfo : SLObjectInfo() {
    override fun createDrawListEntry(): DrawListObjectEntry {
        return DrawListPrimEntry(this)
    }

    override fun isAvatar(): Boolean {
        return false
    }
}