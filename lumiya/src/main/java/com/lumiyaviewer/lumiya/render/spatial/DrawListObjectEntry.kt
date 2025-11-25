package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo

open class DrawListObjectEntry : DrawListEntry() {
    var drawObject: DrawableObject? = null

    override fun addToDrawList(drawList: DrawList) {
        // Stub
    }

    override fun requestEntryRemoval() {
        // Stub
    }
    
    open fun getObjectInfo(): SLObjectInfo {
        throw NotImplementedError("Must override getObjectInfo")
    }
    
    fun updateBoundingBox() {
        // Stub
    }
}

class DrawListPrimEntry(val info: SLObjectInfo) : DrawListObjectEntry() {
    fun getDrawableObject(): DrawableObject? {
        return drawObject
    }
    
    fun getDrawableAttachment(store: DrawableStore?, avatar: DrawableAvatar?): DrawableObject? {
        return drawObject
    }
    
    override fun getObjectInfo(): SLObjectInfo = info
}

class DrawListAvatarEntry(val info: SLObjectInfo) : DrawListObjectEntry() {
    override fun getObjectInfo(): SLObjectInfo = info
}
