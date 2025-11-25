package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo

class SpatialIndex {
    companion object {
        private val instance = SpatialIndex()
        fun getInstance(): SpatialIndex = instance
    }
    
    fun getDrawableAvatar(obj: SLObjectInfo?): com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar? {
        return null // Stub
    }
    
    // Removed definition of SpatialObjectIndex to avoid redeclaration
}
