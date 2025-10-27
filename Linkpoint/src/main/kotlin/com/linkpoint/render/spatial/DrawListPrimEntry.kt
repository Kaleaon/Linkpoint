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

    fun addToDrawList(drawList: DrawList) {
        val weakReference: WeakReference = this.drawableObject
        val obj: Object = weakReference != null ? (DrawableObject) weakReference.get() : null
        if (obj == null) {
            obj = DrawableObject(drawList.drawableStore, this.objectInfo, null)
            this.drawableObject = WeakReference(obj)
        }
        drawList.objects.add(obj)
    }

     public fun getDrawableAttachment(drawableStore: DrawableStore, drawableAvatar: DrawableAvatar): DrawableObject {
        val drawableObject: DrawableObject = null
        val weakReference: WeakReference = this.drawableObject
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

     public fun getDrawableObject(): DrawableObject {
        val weakReference: WeakReference = this.drawableObject
        return weakReference != null ? (DrawableObject) weakReference.get() : null
    }
}
