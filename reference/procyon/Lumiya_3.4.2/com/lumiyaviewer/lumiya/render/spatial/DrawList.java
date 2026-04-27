// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import com.lumiyaviewer.lumiya.render.DrawableObject;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import java.util.ArrayList;

public class DrawList
{
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
    
    private DrawList(@Nonnull final DrawableStore drawableStore, final int avatarCountLimit) {
        this.drawableStore = drawableStore;
        this.myAvatar = null;
        this.objects = new ArrayList<DrawableObject>();
        this.avatars = new ArrayList<DrawableAvatar>();
        this.avatarStubs = new ArrayList<DrawableAvatarStub>();
        this.terrain = new ArrayList<DrawableTerrainPatch>();
        this.avatarCountLimit = avatarCountLimit;
    }
    
    private DrawList(@Nonnull final DrawableStore drawableStore, final int initialCapacity, final int initialCapacity2, final int initialCapacity3, final int initialCapacity4, final int avatarCountLimit) {
        this.drawableStore = drawableStore;
        this.myAvatar = null;
        this.objects = new ArrayList<DrawableObject>(initialCapacity);
        this.avatars = new ArrayList<DrawableAvatar>(initialCapacity2);
        this.avatarStubs = new ArrayList<DrawableAvatarStub>(initialCapacity3);
        this.terrain = new ArrayList<DrawableTerrainPatch>(initialCapacity4);
        this.avatarCountLimit = avatarCountLimit;
    }
    
    public static DrawList create(@Nonnull final DrawableStore drawableStore, @Nullable final DrawList list, final int n) {
        if (list == null) {
            return new DrawList(drawableStore, n);
        }
        return new DrawList(drawableStore, list.objects.size() * 4 / 3, list.avatars.size() * 4 / 3, list.avatarStubs.size() * 4 / 3, list.terrain.size() * 4 / 3, n);
    }
    
    void initRenderPasses() {
        this.renderPasses = new int[this.objects.size()];
    }
}
