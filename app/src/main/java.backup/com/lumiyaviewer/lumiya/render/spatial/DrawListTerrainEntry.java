package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;

class DrawListTerrainEntry extends DrawListEntry {
    private volatile WeakReference<DrawableTerrainPatch> drawablePatch;
    @Nonnull
    private TerrainPatchInfo patchInfo;
    private final int patchX;
    private final int patchY;

    DrawListTerrainEntry(@Nonnull TerrainPatchInfo terrainPatchInfo, int i, int i2) {
        this.patchX = i;
        this.patchY = i2;
        updatePatchInfo(terrainPatchInfo);
    }

    public void addToDrawList(@Nonnull DrawList drawList) {
        Object obj = null;
        WeakReference weakReference = this.drawablePatch;
        if (weakReference != null) {
            obj = (DrawableTerrainPatch) weakReference.get();
        }
        if (obj == null) {
            obj = new DrawableTerrainPatch(drawList.drawableStore.terrainGeometryCache, drawList.drawableStore.glTerrainTextureCache, this.patchInfo, this.patchX, this.patchY);
            this.drawablePatch = new WeakReference(obj);
        }
        drawList.terrain.add(obj);
    }

    public void updatePatchInfo(@Nonnull TerrainPatchInfo terrainPatchInfo) {
        this.patchInfo = terrainPatchInfo;
        float minHeight = terrainPatchInfo.getMinHeight();
        float maxHeight = terrainPatchInfo.getMaxHeight();
        this.boundingBox[0] = (float) (this.patchX * 16);
        this.boundingBox[1] = (float) (this.patchY * 16);
        this.boundingBox[2] = minHeight;
        this.boundingBox[3] = (float) ((this.patchX + 1) * 16);
        this.boundingBox[4] = (float) ((this.patchY + 1) * 16);
        this.boundingBox[5] = maxHeight;
        this.drawablePatch = null;
    }
}
