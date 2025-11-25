package com.lumiyaviewer.lumiya.slproto.objects

import java.util.UUID

class SLObjectAvatarInfo : SLObjectInfo() {
    override fun isAvatar(): Boolean = true
    
    fun ApplyAvatarAnimation(anim: Any) {}
    fun ApplyAvatarAppearance(app: Any) {}
}
