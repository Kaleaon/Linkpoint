// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render;

import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import javax.annotation.Nonnull;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.CacheBuilder;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache;
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.res.geometry.PrimCache;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache;
import com.lumiyaviewer.lumiya.res.geometry.GeometryCache;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;
import com.google.common.cache.LoadingCache;

public class DrawableStore
{
    public final LoadingCache<SLObjectAvatarInfo, DrawableAvatar> drawableAvatarCache;
    public final LoadingCache<SLObjectAvatarInfo, DrawableAvatarStub> drawableAvatarStubCache;
    public final GeometryCache geometryCache;
    public final GLTerrainTextureCache glTerrainTextureCache;
    public final GLTextureCache glTextureCache;
    public final boolean hasGL20;
    public final MeshCache meshCache;
    public final PrimCache primCache;
    public final SpatialObjectIndex spatialObjectIndex;
    public final TerrainGeometryCache terrainGeometryCache;
    public final GLTextTextureCache textTextureCache;
    
    public DrawableStore(final GLLoadQueue glLoadQueue, final boolean hasGL20, final int n, final boolean b, final int n2, final Object o) {
        this.terrainGeometryCache = new TerrainGeometryCache();
        this.drawableAvatarCache = CacheBuilder.newBuilder().weakKeys().weakValues().build((CacheLoader<? super SLObjectAvatarInfo, DrawableAvatar>)new CacheLoader<SLObjectAvatarInfo, DrawableAvatar>() {
            @Override
            public DrawableAvatar load(@Nonnull final SLObjectAvatarInfo slObjectAvatarInfo) {
                return slObjectAvatarInfo.getAvatarVisualState().createDrawableAvatar(DrawableStore.this);
            }
        });
        this.drawableAvatarStubCache = CacheBuilder.newBuilder().weakKeys().weakValues().build((CacheLoader<? super SLObjectAvatarInfo, DrawableAvatarStub>)new CacheLoader<SLObjectAvatarInfo, DrawableAvatarStub>() {
            @Override
            public DrawableAvatarStub load(@Nonnull final SLObjectAvatarInfo slObjectAvatarInfo) {
                return slObjectAvatarInfo.getAvatarVisualState().createDrawableAvatarStub(DrawableStore.this);
            }
        });
        this.hasGL20 = hasGL20;
        this.glTextureCache = new GLTextureCache(glLoadQueue);
        this.textTextureCache = new GLTextTextureCache(glLoadQueue, new DrawableTextCache(n2));
        this.meshCache = new MeshCache();
        this.geometryCache = new GeometryCache(this.meshCache);
        this.primCache = new PrimCache(this.glTextureCache, this.geometryCache);
        this.spatialObjectIndex = new SpatialObjectIndex(this, n);
        GLTerrainTextureCache glTerrainTextureCache;
        if (b) {
            glTerrainTextureCache = new GLTerrainTextureCache(glLoadQueue, new TerrainTextureCache());
        }
        else {
            glTerrainTextureCache = null;
        }
        this.glTerrainTextureCache = glTerrainTextureCache;
        SpatialIndex.getInstance().EnableObjectIndex(this.spatialObjectIndex, o);
    }
    
    public void setMeshCapURL(final String capURL) {
        this.meshCache.setCapURL(capURL);
    }
}
