package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.slproto.objects.SLObjectInfo
import java.lang.ref.WeakReference
import androidx.annotation.NonNull

class DrawListPrimEntry : DrawListObjectEntry {
    private volatile WeakReference<DrawableObject> drawableObject = null

    constructor(sLObjectInfo: SLObjectInfo) {
        super(sLObjectInfo)
    }

    fun addToDrawList(drawList: DrawList): Unit {
        WeakReference weakReference = this.drawableObject
        Any obj = weakReference != null ? (DrawableObject) weakReference.get() : null
        if (obj == null) {
            obj = DrawableObject(drawList.drawableStore, this.objectInfo, null)
            this.drawableObject = WeakReference(obj)
        }
        drawList.objects.add(obj)
    }

    @NonNull
    fun getDrawableAttachment(drawableStore: DrawableStore, drawableAvatar: DrawableAvatar): DrawableObject {
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

    fun getDrawableObject(): DrawableObject {
        WeakReference weakReference = this.drawableObject
        return weakReference != null ? (DrawableObject) weakReference.get() : null
    }
}
