package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo
import java.lang.ref.WeakReference

class DrawListAvatarEntry(private val objectAvatarInfo: SLObjectAvatarInfo) : DrawListObjectEntry(objectAvatarInfo) {
    
    private var drawableAvatar: WeakReference<DrawableAvatar>? = null
    private var drawableAvatarStub: WeakReference<DrawableAvatarStub>? = null

    override fun addToDrawList(drawList: DrawList) {
        if (drawList.avatars.size < drawList.avatarCountLimit || objectAvatarInfo.isMyAvatar()) {
            // Add full avatar
            var avatar = drawableAvatar?.get()
            if (avatar == null) {
                avatar = drawList.drawableStore.drawableAvatarCache.getUnchecked(objectAvatarInfo)
                drawableAvatar = WeakReference(avatar)
            }
            drawList.avatars.add(avatar)
            
            if (objectAvatarInfo.isMyAvatar()) {
                drawList.myAvatar = avatar
            }
        } else {
            // Add avatar stub (simplified representation)
            var stub = drawableAvatarStub?.get()
            if (stub == null) {
                stub = drawList.drawableStore.drawableAvatarStubCache.getUnchecked(objectAvatarInfo)
                drawableAvatarStub = WeakReference(stub)
            }
            drawList.avatarStubs.add(stub)
        }
    }

    fun getObjectAvatarInfo(): SLObjectAvatarInfo = objectAvatarInfo
}
