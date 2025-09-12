// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lumiyaviewer.lumiya.render.avatar.AvatarVisualState;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatarStub;
import com.lumiyaviewer.lumiya.render.glres.GLLoadQueue;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextTextureCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTextureCache;
import com.lumiyaviewer.lumiya.render.spatial.SpatialIndex;
import com.lumiyaviewer.lumiya.render.spatial.SpatialObjectIndex;
import com.lumiyaviewer.lumiya.res.geometry.GeometryCache;
import com.lumiyaviewer.lumiya.res.geometry.PrimCache;
import com.lumiyaviewer.lumiya.res.mesh.MeshCache;
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache;
import com.lumiyaviewer.lumiya.res.terrain.TerrainTextureCache;
import com.lumiyaviewer.lumiya.res.text.DrawableTextCache;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectAvatarInfo;

public class DrawableStore
{

    public final LoadingCache drawableAvatarCache = CacheBuilder.newBuilder().weakKeys().weakValues().build(new CacheLoader() {

        final DrawableStore this$0;

        public DrawableAvatar load(SLObjectAvatarInfo slobjectavatarinfo)
        {
            return slobjectavatarinfo.getAvatarVisualState().createDrawableAvatar(DrawableStore.this);
        }

        public volatile Object load(Object obj1)
            throws Exception
        {
            return load((SLObjectAvatarInfo)obj1);
        }

            
            {
                this$0 = DrawableStore.this;
                super();
            }
    });
    public final LoadingCache drawableAvatarStubCache = CacheBuilder.newBuilder().weakKeys().weakValues().build(new CacheLoader() {

        final DrawableStore this$0;

        public DrawableAvatarStub load(SLObjectAvatarInfo slobjectavatarinfo)
        {
            return slobjectavatarinfo.getAvatarVisualState().createDrawableAvatarStub(DrawableStore.this);
        }

        public volatile Object load(Object obj1)
            throws Exception
        {
            return load((SLObjectAvatarInfo)obj1);
        }

            
            {
                this$0 = DrawableStore.this;
                super();
            }
    });
    public final GeometryCache geometryCache;
    public final GLTerrainTextureCache glTerrainTextureCache;
    public final GLTextureCache glTextureCache;
    public final boolean hasGL20;
    public final MeshCache meshCache = new MeshCache();
    public final PrimCache primCache;
    public final SpatialObjectIndex spatialObjectIndex;
    public final TerrainGeometryCache terrainGeometryCache = new TerrainGeometryCache();
    public final GLTextTextureCache textTextureCache;

    public DrawableStore(GLLoadQueue glloadqueue, boolean flag, int i, boolean flag1, int j, Object obj)
    {
        hasGL20 = flag;
        glTextureCache = new GLTextureCache(glloadqueue);
        textTextureCache = new GLTextTextureCache(glloadqueue, new DrawableTextCache(j));
        geometryCache = new GeometryCache(meshCache);
        primCache = new PrimCache(glTextureCache, geometryCache);
        spatialObjectIndex = new SpatialObjectIndex(this, i);
        if (flag1)
        {
            glloadqueue = new GLTerrainTextureCache(glloadqueue, new TerrainTextureCache());
        } else
        {
            glloadqueue = null;
        }
        glTerrainTextureCache = glloadqueue;
        SpatialIndex.getInstance().EnableObjectIndex(spatialObjectIndex, obj);
    }

    public void setMeshCapURL(String s)
    {
        meshCache.setCapURL(s);
    }
}
