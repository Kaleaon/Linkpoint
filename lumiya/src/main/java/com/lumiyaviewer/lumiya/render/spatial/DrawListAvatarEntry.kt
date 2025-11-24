package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import java.lang.ref.WeakReference

class DrawListAvatarEntry(
    val objectAvatarInfo: SLObjectAvatarInfo
) : DrawListObjectEntry(objectAvatarInfo) {

    private var drawableAvatarRef: WeakReference<DrawableAvatar>? = null
    private var drawableAvatarStubRef: WeakReference<DrawableAvatarStub>? = null

    override fun addToDrawList(drawList: DrawList) {
        if (drawList.avatars.size < drawList.avatarCountLimit || objectAvatarInfo.isMyAvatar()) {
            var avatar = drawableAvatarRef?.get()
            
            if (avatar == null) {
                val cached = drawList.drawableStore.drawableAvatarCache.getUnchecked(objectAvatarInfo)
                if (cached is DrawableAvatar) {
                    avatar = cached
                    drawableAvatarRef = WeakReference(avatar)
                }
            }
            
            if (avatar != null) {
                drawList.avatars.add(avatar)
                if (objectAvatarInfo.isMyAvatar()) {
                    drawList.myAvatar = avatar
                }
            }
        } else {
            var stub = drawableAvatarStubRef?.get()
            
            if (stub == null) {
                val cached = drawList.drawableStore.drawableAvatarStubCache.getUnchecked(objectAvatarInfo)
                if (cached is DrawableAvatarStub) {
                    stub = cached
                    drawableAvatarStubRef = WeakReference(stub)
                }
            }
            
            if (stub != null) {
                drawList.avatarStubs.add(stub)
            }
        }
    }

    fun getObjectAvatarInfo(): SLObjectAvatarInfo {
        return objectAvatarInfo
    }
}
