package com.lumiyaviewer.lumiya.render.spatial
import java.util.*

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import java.lang.ref.WeakReference
import javax.annotation.Nonnull

class DrawListAvatarEntry : DrawListObjectEntry {
    private var drawableAvatar: WeakReference<DrawableAvatar> = null
    private var drawableAvatarStub: WeakReference<DrawableAvatarStub> = null
    @Nonnull
    private SLObjectAvatarInfo objectAvatarInfo

    constructor(sLObjectAvatarInfo: SLObjectAvatarInfo) {
        super(sLObjectAvatarInfo)
        this.objectAvatarInfo = sLObjectAvatarInfo
    }

    fun addToDrawList(drawList: DrawList): Unit {
        Any obj = null
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

    @Nonnull
    fun getObjectAvatarInfo(): SLObjectAvatarInfo {
        return this.objectAvatarInfo
    }
}
