package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch

class DrawList private constructor(
    val drawableStore: DrawableStore,
    val avatarCountLimit: Int,
    objectsCapacity: Int = 0,
    avatarsCapacity: Int = 0,
    avatarStubsCapacity: Int = 0,
    terrainCapacity: Int = 0
) {
    
    var myAvatar: DrawableAvatar? = null
    val objects = ArrayList<DrawableObject>(objectsCapacity)
    val avatars = ArrayList<DrawableAvatar>(avatarsCapacity)
    val avatarStubs = ArrayList<DrawableAvatarStub>(avatarStubsCapacity)
    val terrain = ArrayList<DrawableTerrainPatch>(terrainCapacity)
    var renderPasses: IntArray? = null

    companion object {
        @JvmStatic
        fun create(
            drawableStore: DrawableStore,
            previousDrawList: DrawList?,
            avatarCountLimit: Int
        ): DrawList {
            return if (previousDrawList == null) {
                DrawList(drawableStore, avatarCountLimit)
            } else {
                // Create with capacities based on previous draw list (with 33% growth)
                DrawList(
                    drawableStore,
                    avatarCountLimit,
                    objectsCapacity = (previousDrawList.objects.size * 4) / 3,
                    avatarsCapacity = (previousDrawList.avatars.size * 4) / 3,
                    avatarStubsCapacity = (previousDrawList.avatarStubs.size * 4) / 3,
                    terrainCapacity = (previousDrawList.terrain.size * 4) / 3
                )
            }
        }
    }

    fun initRenderPasses() {
        renderPasses = IntArray(objects.size)
    }
}
