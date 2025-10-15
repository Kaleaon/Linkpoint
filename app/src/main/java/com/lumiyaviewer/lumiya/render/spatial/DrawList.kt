package com.lumiyaviewer.lumiya.render.spatial

import com.lumiyaviewer.lumiya.render.DrawableObject
import com.lumiyaviewer.lumiya.render.DrawableStore
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch
import java.util.ArrayList
import javax.annotation.Nonnull
import javax.annotation.Nullable

object DrawList {
    int avatarCountLimit
    @Nonnull
    ArrayList<DrawableAvatarStub> avatarStubs
    @Nonnull
    ArrayList<DrawableAvatar> avatars
    @Nonnull
    DrawableStore drawableStore
    @Nullable
    DrawableAvatar myAvatar
    @Nonnull
    ArrayList<DrawableObject> objects
    int[] renderPasses
    @Nonnull
    ArrayList<DrawableTerrainPatch> terrain

    private DrawList(@Nonnull DrawableStore drawableStore, int i) {
        this.drawableStore = drawableStore
        this.myAvatar = null
        this.objects = ArrayList()
        this.avatars = ArrayList()
        this.avatarStubs = ArrayList()
        this.terrain = ArrayList()
        this.avatarCountLimit = i
    }

    private DrawList(@Nonnull DrawableStore drawableStore, int i, int i2, int i3, int i4, int i5) {
        this.drawableStore = drawableStore
        this.myAvatar = null
        this.objects = ArrayList(i)
        this.avatars = ArrayList(i2)
        this.avatarStubs = ArrayList(i3)
        this.terrain = ArrayList(i4)
        this.avatarCountLimit = i5
    }

    fun create(drawableStore: DrawableStore, drawList: DrawList, i: Int): DrawList {
        if (drawList == null) {
            return DrawList(drawableStore, i)
        }
        return DrawList(drawableStore, (drawList.objects.size() * 4) / 3, (drawList.avatars.size() * 4) / 3, (drawList.avatarStubs.size() * 4) / 3, (drawList.terrain.size() * 4) / 3, i)
    }

    void initRenderPasses() {
        this.renderPasses = new int[this.objects.size()]
    }
}
