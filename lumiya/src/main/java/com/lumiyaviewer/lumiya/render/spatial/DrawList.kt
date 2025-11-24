package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch
import java.util.ArrayList

class DrawList private constructor(
    val drawableStore: DrawableStore,
    objectsCapacity: Int,
    avatarsCapacity: Int,
    avatarStubsCapacity: Int,
    terrainCapacity: Int,
    val avatarCountLimit: Int
) {
    val avatarStubs: ArrayList<DrawableAvatarStub> = ArrayList(avatarStubsCapacity)
    val avatars: ArrayList<DrawableAvatar> = ArrayList(avatarsCapacity)
    var myAvatar: DrawableAvatar? = null
    val objects: ArrayList<DrawableObject> = ArrayList(objectsCapacity)
    lateinit var renderPasses: IntArray
    val terrain: ArrayList<DrawableTerrainPatch> = ArrayList(terrainCapacity)

    private constructor(drawableStore: DrawableStore, limit: Int) : this(
        drawableStore,
        10, 10, 10, 10, limit
    )

    companion object {
        fun create(drawableStore: DrawableStore, previousDrawList: DrawList?, limit: Int): DrawList {
            if (previousDrawList == null) {
                return DrawList(drawableStore, limit)
            }
            return DrawList(
                drawableStore,
                (previousDrawList.objects.size * 4) / 3,
                (previousDrawList.avatars.size * 4) / 3,
                (previousDrawList.avatarStubs.size * 4) / 3,
                (previousDrawList.terrain.size * 4) / 3,
                limit
            )
        }
    }

    fun initRenderPasses() {
        this.renderPasses = IntArray(this.objects.size)
    }
}
