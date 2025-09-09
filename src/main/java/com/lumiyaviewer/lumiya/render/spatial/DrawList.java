package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableObject;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DrawList {
    public final int avatarCountLimit;
    @Nonnull
    public final ArrayList<DrawableAvatarStub> avatarStubs;
    @Nonnull
    public final ArrayList<DrawableAvatar> avatars;
    @Nonnull
    final DrawableStore drawableStore;
    @Nullable
    public DrawableAvatar myAvatar;
    @Nonnull
    public final ArrayList<DrawableObject> objects;
    public int[] renderPasses;
    @Nonnull
    public final ArrayList<DrawableTerrainPatch> terrain;

    private DrawList(@Nonnull DrawableStore drawableStore, int i) {
        this.drawableStore = drawableStore;
        this.myAvatar = null;
        this.objects = new ArrayList();
        this.avatars = new ArrayList();
        this.avatarStubs = new ArrayList();
        this.terrain = new ArrayList();
        this.avatarCountLimit = i;
    }

    private DrawList(@Nonnull DrawableStore drawableStore, int i, int i2, int i3, int i4, int i5) {
        this.drawableStore = drawableStore;
        this.myAvatar = null;
        this.objects = new ArrayList(i);
        this.avatars = new ArrayList(i2);
        this.avatarStubs = new ArrayList(i3);
        this.terrain = new ArrayList(i4);
        this.avatarCountLimit = i5;
    }

    public static DrawList create(@Nonnull DrawableStore drawableStore, @Nullable DrawList drawList, int i) {
        if (drawList == null) {
            return new DrawList(drawableStore, i);
        }
        return new DrawList(drawableStore, (drawList.objects.size() * 4) / 3, (drawList.avatars.size() * 4) / 3, (drawList.avatarStubs.size() * 4) / 3, (drawList.terrain.size() * 4) / 3, i);
    }

    void initRenderPasses() {
        this.renderPasses = new int[this.objects.size()];
    }
}
