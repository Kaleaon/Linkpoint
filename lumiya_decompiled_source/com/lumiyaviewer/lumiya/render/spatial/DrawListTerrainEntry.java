package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import java.lang.ref.WeakReference;
import javax.annotation.Nonnull;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class DrawListTerrainEntry extends DrawListEntry {
    private volatile WeakReference<DrawableTerrainPatch> drawablePatch;
    @Nonnull
    private TerrainPatchInfo patchInfo;
    private final int patchX;
    private final int patchY;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DrawListTerrainEntry(@Nonnull TerrainPatchInfo terrainPatchInfo, int i, int i2) {
        this.patchX = i;
        this.patchY = i2;
        updatePatchInfo(terrainPatchInfo);
    }

    @Override // com.lumiyaviewer.lumiya.render.spatial.DrawListEntry
    public void addToDrawList(@Nonnull DrawList drawList) {
        WeakReference<DrawableTerrainPatch> weakReference = this.drawablePatch;
        DrawableTerrainPatch drawableTerrainPatch = weakReference != null ? weakReference.get() : null;
        if (drawableTerrainPatch == null) {
            drawableTerrainPatch = new DrawableTerrainPatch(drawList.drawableStore.terrainGeometryCache, drawList.drawableStore.glTerrainTextureCache, this.patchInfo, this.patchX, this.patchY);
            this.drawablePatch = new WeakReference<>(drawableTerrainPatch);
        }
        drawList.terrain.add(drawableTerrainPatch);
    }

    public void updatePatchInfo(@Nonnull TerrainPatchInfo terrainPatchInfo) {
        this.patchInfo = terrainPatchInfo;
        float minHeight = terrainPatchInfo.getMinHeight();
        float maxHeight = terrainPatchInfo.getMaxHeight();
        this.boundingBox[0] = this.patchX * 16;
        this.boundingBox[1] = this.patchY * 16;
        this.boundingBox[2] = minHeight;
        this.boundingBox[3] = (this.patchX + 1) * 16;
        this.boundingBox[4] = (this.patchY + 1) * 16;
        this.boundingBox[5] = maxHeight;
        this.drawablePatch = null;
    }
}
