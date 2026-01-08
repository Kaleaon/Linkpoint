package com.lumiyaviewer.lumiya.render

import com.lumiyaviewer.lumiya.render.avatar.AvatarSkeleton

open class DrawableObject {
    open fun isRiggedMesh(): Boolean {
        return false
    }

    open fun DrawRigged30(renderContext: RenderContext, pass: Int): Int {
        return 0
    }

    open fun DrawRigged(renderContext: RenderContext, avatarSkeleton: AvatarSkeleton, pass: Int) {
        // Stub
    }

    open fun Draw(renderContext: RenderContext, pass: Int) {
        // Stub
    }
}
