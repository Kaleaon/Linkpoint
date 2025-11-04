// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.terrain;

import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache;
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;

public class DrawableTerrainPatch implements ResourceConsumer
{
    private volatile TerrainPatchGeometry geometry;
    private final float[] objWorldMatrix;
    private volatile GLLoadedTexture texture;
    
    public DrawableTerrainPatch(final TerrainGeometryCache terrainGeometryCache, final GLTerrainTextureCache glTerrainTextureCache, final TerrainPatchInfo terrainPatchInfo, final int n, final int n2) {
        Matrix.setIdentityM(this.objWorldMatrix = new float[16], 0);
        Matrix.translateM(this.objWorldMatrix, 0, (float)(n * 16), (float)(n2 * 16), 0.0f);
        ((ResourceMemoryCache<TerrainPatchHeightMap, ResourceType>)terrainGeometryCache).RequestResource(terrainPatchInfo.getHeightMap(), this);
        if (glTerrainTextureCache != null) {
            ((ResourceMemoryCache<ResourceParams, ResourceType>)glTerrainTextureCache).RequestResource((ResourceParams)terrainPatchInfo, this);
        }
    }
    
    public static void GLPrepare(final RenderContext renderContext) {
        TerrainPatchGeometry.GLPrepare(renderContext);
    }
    
    public void GLDraw(final RenderContext renderContext) {
        final TerrainPatchGeometry geometry = this.geometry;
        if (geometry != null) {
            geometry.GLDraw(renderContext, this.objWorldMatrix, this.texture);
        }
    }
    
    @Override
    public void OnResourceReady(final Object o, final boolean b) {
        String string;
        if (o != null) {
            string = o.toString();
        }
        else {
            string = "null";
        }
        Debug.Printf("DrawableTerrainPatch: got resource = %s", string);
        if (o instanceof TerrainPatchGeometry) {
            this.geometry = (TerrainPatchGeometry)o;
        }
        else if (o instanceof GLLoadedTexture) {
            this.texture = (GLLoadedTexture)o;
        }
    }
}
