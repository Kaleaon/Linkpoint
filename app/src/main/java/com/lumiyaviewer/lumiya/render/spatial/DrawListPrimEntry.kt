package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo
import java.lang.ref.WeakReference

class DrawListPrimEntry(objectInfo: SLObjectInfo) : DrawListObjectEntry(objectInfo) {
    
    @Volatile
    private var drawableObject: WeakReference<DrawableObject>? = null

    override fun addToDrawList(drawList: DrawList) {
        var drawable = drawableObject?.get()
        if (drawable == null) {
            drawable = DrawableObject(drawList.drawableStore, objectInfo, null)
            drawableObject = WeakReference(drawable)
        }
        drawList.objects.add(drawable)
    }

    fun getDrawableAttachment(drawableStore: DrawableStore, drawableAvatar: DrawableAvatar): DrawableObject {
        var drawable = drawableObject?.get()
        if (drawable != null) {
            return drawable
        }
        
        drawable = DrawableObject(drawableStore, objectInfo, drawableAvatar)
        drawableObject = WeakReference(drawable)
        return drawable
    }

    fun getDrawableObject(): DrawableObject? {
        return drawableObject?.get()
    }
}
