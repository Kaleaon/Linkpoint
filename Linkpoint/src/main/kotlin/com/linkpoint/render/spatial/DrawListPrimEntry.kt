package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.slproto.objects.SLObjectInfo
import java.lang.ref.WeakReference
import javax.annotation.Nonnull

class DrawListPrimEntry : DrawListObjectEntry() {
    private volatile WeakReference<DrawableObject> drawableObject = null

    public DrawListPrimEntry(SLObjectInfo sLObjectInfo) {
        super(sLObjectInfo)
    }

    fun addToDrawList(DrawList drawList) {
        WeakReference weakReference = this.drawableObject
        Object obj = weakReference != null ? (DrawableObject) weakReference.get() : null
        if (obj == null) {
            obj = DrawableObject(drawList.drawableStore, this.objectInfo, null)
            this.drawableObject = WeakReference(obj)
        }
        drawList.objects.add(obj)
    }

    public DrawableObject getDrawableAttachment(DrawableStore drawableStore, DrawableAvatar drawableAvatar) {
        DrawableObject drawableObject = null
        WeakReference weakReference = this.drawableObject
        if (weakReference != null) {
            drawableObject = (DrawableObject) weakReference.get()
        }
        if (drawableObject != null) {
            return drawableObject
        }
        drawableObject = DrawableObject(drawableStore, this.objectInfo, drawableAvatar)
        this.drawableObject = WeakReference(drawableObject)
        return drawableObject
    }

    public DrawableObject getDrawableObject() {
        WeakReference weakReference = this.drawableObject
        return weakReference != null ? (DrawableObject) weakReference.get() : null
    }
}
