// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.terrain;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;

// Referenced classes of package com.lumiyaviewer.lumiya.render.terrain:
//            TerrainPatchGeometry

public class DrawableTerrainPatch
    implements ResourceConsumer
{

    private volatile TerrainPatchGeometry geometry;
    private final float objWorldMatrix[] = new float[16];
    private volatile GLLoadedTexture texture;

    public DrawableTerrainPatch(TerrainGeometryCache terraingeometrycache, GLTerrainTextureCache glterraintexturecache, TerrainPatchInfo terrainpatchinfo, int i, int j)
    {
        Matrix.setIdentityM(objWorldMatrix, 0);
        Matrix.translateM(objWorldMatrix, 0, i * 16, j * 16, 0.0F);
        terraingeometrycache.RequestResource(terrainpatchinfo.getHeightMap(), this);
        if (glterraintexturecache != null)
        {
            glterraintexturecache.RequestResource(terrainpatchinfo, this);
        }
    }

    public static void GLPrepare(RenderContext rendercontext)
    {
        TerrainPatchGeometry.GLPrepare(rendercontext);
    }

    public void GLDraw(RenderContext rendercontext)
    {
        TerrainPatchGeometry terrainpatchgeometry = geometry;
        if (terrainpatchgeometry != null)
        {
            GLLoadedTexture glloadedtexture = texture;
            terrainpatchgeometry.GLDraw(rendercontext, objWorldMatrix, glloadedtexture);
        }
    }

    public void OnResourceReady(Object obj, boolean flag)
    {
        String s;
        if (obj != null)
        {
            s = obj.toString();
        } else
        {
            s = "null";
        }
        Debug.Printf("DrawableTerrainPatch: got resource = %s", new Object[] {
            s
        });
        if (obj instanceof TerrainPatchGeometry)
        {
            geometry = (TerrainPatchGeometry)obj;
        } else
        if (obj instanceof GLLoadedTexture)
        {
            texture = (GLLoadedTexture)obj;
            return;
        }
    }
}
