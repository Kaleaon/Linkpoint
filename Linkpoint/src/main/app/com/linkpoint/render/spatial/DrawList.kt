package com.linkpoint.render.spatial

import com.linkpoint.render.DrawableObject
import com.linkpoint.render.DrawableStore
import com.linkpoint.render.avatar.DrawableAvatar
import com.linkpoint.render.avatar.DrawableAvatarStub
import com.linkpoint.render.terrain.DrawableTerrainPatch
import java.util.ArrayList
import androidx.annotation.NonNull
import androidx.annotation.Nullable

object DrawList {
    int avatarCountLimit
    @NonNull
    ArrayList<DrawableAvatarStub> avatarStubs
    @NonNull
    ArrayList<DrawableAvatar> avatars
    @NonNull
    DrawableStore drawableStore
    @Nullable
    DrawableAvatar myAvatar
    @NonNull
    ArrayList<DrawableObject> objects
    int[] renderPasses
    @NonNull
    ArrayList<DrawableTerrainPatch> terrain

    private DrawList(@NonNull DrawableStore drawableStore, int i) {
        this.drawableStore = drawableStore
        this.myAvatar = null
        this.objects = ArrayList()
        this.avatars = ArrayList()
        this.avatarStubs = ArrayList()
        this.terrain = ArrayList()
        this.avatarCountLimit = i
    }

    private DrawList(@NonNull DrawableStore drawableStore, int i, int i2, int i3, int i4, int i5) {
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
