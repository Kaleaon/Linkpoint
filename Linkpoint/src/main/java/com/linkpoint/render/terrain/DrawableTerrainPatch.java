package com.linkpoint.render.terrain;
import java.util.*;

import android.opengl.Matrix;
import com.linkpoint.Debug;
import com.linkpoint.render.RenderContext;
import com.linkpoint.render.glres.textures.GLLoadedTexture;
import com.linkpoint.render.glres.textures.GLTerrainTextureCache;
import com.linkpoint.res.ResourceConsumer;
import com.linkpoint.res.terrain.TerrainGeometryCache;
import com.linkpoint.slproto.terrain.TerrainPatchInfo;

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
