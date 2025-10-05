package com.linkpoint.render.spatial

import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.render.avatar.DrawableAvatarStub
import com.linkpoint.render.terrain.DrawableTerrainPatch
import java.util.ArrayList
import javax.annotation.Nonnull
import javax.annotation.Nullable

class DrawList {
    val Int avatarCountLimit
    val ArrayList<DrawableAvatarStub> avatarStubs
    val ArrayList<DrawableAvatar> avatars
    final DrawableStore drawableStore
    public DrawableAvatar myAvatar
    val ArrayList<DrawableObject> objects
    public Int[] renderPasses
    val ArrayList<DrawableTerrainPatch> terrain

    private DrawList(DrawableStore drawableStore, Int i) {
        this.drawableStore = drawableStore
        this.myAvatar = null
        this.objects = ArrayList()
        this.avatars = ArrayList()
        this.avatarStubs = ArrayList()
        this.terrain = ArrayList()
        this.avatarCountLimit = i
    }

    private DrawList(DrawableStore drawableStore, Int i, Int i2, Int i3, Int i4, Int i5) {
        this.drawableStore = drawableStore
        this.myAvatar = null
        this.objects = ArrayList(i)
        this.avatars = ArrayList(i2)
        this.avatarStubs = ArrayList(i3)
        this.terrain = ArrayList(i4)
        this.avatarCountLimit = i5
    }

    @JvmStatic
    DrawList create(DrawableStore drawableStore, DrawList drawList, Int i) {
        if (drawList == null) {
            return DrawList(drawableStore, i)
        }
        return DrawList(drawableStore, (drawList.objects.size() * 4) / 3, (drawList.avatars.size() * 4) / 3, (drawList.avatarStubs.size() * 4) / 3, (drawList.terrain.size() * 4) / 3, i)
    }

    Unit initRenderPasses() {
        this.renderPasses = Int[this.objects.size()]
    }
}
