package com.lumiyaviewer.lumiya.render.terrain;

import android.opengl.Matrix;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.RenderContext;
import com.lumiyaviewer.lumiya.render.glres.textures.GLLoadedTexture;
import com.lumiyaviewer.lumiya.render.glres.textures.GLTerrainTextureCache;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.terrain.TerrainGeometryCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;

public class DrawableTerrainPatch implements ResourceConsumer {
    private volatile TerrainPatchGeometry geometry;
    private final float[] objWorldMatrix = new float[16];
    private volatile GLLoadedTexture texture;

    public DrawableTerrainPatch(TerrainGeometryCache terrainGeometryCache, GLTerrainTextureCache gLTerrainTextureCache, TerrainPatchInfo terrainPatchInfo, int i, int i2) {
        Matrix.setIdentityM(this.objWorldMatrix, 0);
        Matrix.translateM(this.objWorldMatrix, 0, (float) (i * 16), (float) (i2 * 16), 0.0f);
        terrainGeometryCache.RequestResource(terrainPatchInfo.getHeightMap(), this);
        if (gLTerrainTextureCache != null) {
            gLTerrainTextureCache.RequestResource(terrainPatchInfo, this);
        }
    }

    public static void GLPrepare(RenderContext renderContext) {
        TerrainPatchGeometry.GLPrepare(renderContext);
    }

    public void GLDraw(RenderContext renderContext) {
        TerrainPatchGeometry terrainPatchGeometry = this.geometry;
        if (terrainPatchGeometry != null) {
            terrainPatchGeometry.GLDraw(renderContext, this.objWorldMatrix, this.texture);
        }
    }

    public void OnResourceReady(Object obj, boolean z) {
        Object[] objArr = new Object[1];
        objArr[0] = obj != null ? obj.toString() : "null";
        Debug.Printf("DrawableTerrainPatch: got resource = %s", objArr);
        if (obj instanceof TerrainPatchGeometry) {
            this.geometry = (TerrainPatchGeometry) obj;
        } else if (obj instanceof GLLoadedTexture) {
            this.texture = (GLLoadedTexture) obj;
        }
    }
}
