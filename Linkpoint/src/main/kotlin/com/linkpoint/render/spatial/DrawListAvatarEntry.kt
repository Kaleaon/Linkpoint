package com.linkpoint.render.spatial
import java.util.*

import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.render.avatar.DrawableAvatarStub
import com.linkpoint.slproto.objects.SLObjectAvatarInfo
import java.lang.ref.WeakReference
import javax.annotation.Nonnull

class DrawListAvatarEntry : DrawListObjectEntry() {
    private WeakReference<DrawableAvatar> drawableAvatar = null
    private WeakReference<DrawableAvatarStub> drawableAvatarStub = null
    private val SLObjectAvatarInfo objectAvatarInfo

    public DrawListAvatarEntry(SLObjectAvatarInfo sLObjectAvatarInfo) {
        super(sLObjectAvatarInfo)
        this.objectAvatarInfo = sLObjectAvatarInfo
    }

    fun addToDrawList(DrawList drawList) {
        Object obj = null
        WeakReference weakReference
        if (drawList.avatars.size() < drawList.avatarCountLimit || this.objectAvatarInfo.isMyAvatar()) {
            DrawableAvatar drawableAvatar
            weakReference = this.drawableAvatar
            if (weakReference != null) {
                drawableAvatar = (DrawableAvatar) weakReference.get()
            }
            if (drawableAvatar == null) {
                drawableAvatar = (DrawableAvatar) drawList.drawableStore.drawableAvatarCache.getUnchecked(this.objectAvatarInfo)
                this.drawableAvatar = WeakReference(drawableAvatar)
            }
            drawList.avatars.add(drawableAvatar)
            if (this.objectAvatarInfo.isMyAvatar()) {
                drawList.myAvatar = drawableAvatar
                return
            }
            return
        }
        weakReference = this.drawableAvatarStub
        if (weakReference != null) {
            obj = (DrawableAvatarStub) weakReference.get()
        }
        if (obj == null) {
            obj = (DrawableAvatarStub) drawList.drawableStore.drawableAvatarStubCache.getUnchecked(this.objectAvatarInfo)
            this.drawableAvatarStub = WeakReference(obj)
        }
        drawList.avatarStubs.add(obj)
    }

    public SLObjectAvatarInfo getObjectAvatarInfo() {
        return this.objectAvatarInfo
    }
}
