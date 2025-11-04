// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.render.spatial;

import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import java.lang.ref.WeakReference;

class DrawListTerrainEntry extends DrawListEntry
{
    private volatile WeakReference<DrawableTerrainPatch> drawablePatch;
    @Nonnull
    private TerrainPatchInfo patchInfo;
    private final int patchX;
    private final int patchY;
    
    DrawListTerrainEntry(@Nonnull final TerrainPatchInfo terrainPatchInfo, final int patchX, final int patchY) {
        this.patchX = patchX;
        this.patchY = patchY;
        this.updatePatchInfo(terrainPatchInfo);
    }
    
    @Override
    public void addToDrawList(@Nonnull final DrawList list) {
        DrawableTerrainPatch drawableTerrainPatch = null;
        final WeakReference<DrawableTerrainPatch> drawablePatch = this.drawablePatch;
        if (drawablePatch != null) {
            drawableTerrainPatch = drawablePatch.get();
        }
        DrawableTerrainPatch drawableTerrainPatch2;
        if ((drawableTerrainPatch2 = drawableTerrainPatch) == null) {
            drawableTerrainPatch2 = new DrawableTerrainPatch(list.drawableStore.terrainGeometryCache, list.drawableStore.glTerrainTextureCache, this.patchInfo, this.patchX, this.patchY);
            this.drawablePatch = new WeakReference<DrawableTerrainPatch>(drawableTerrainPatch2);
        }
        list.terrain.add(drawableTerrainPatch2);
    }
    
    public void updatePatchInfo(@Nonnull final TerrainPatchInfo patchInfo) {
        this.patchInfo = patchInfo;
        final float minHeight = patchInfo.getMinHeight();
        final float maxHeight = patchInfo.getMaxHeight();
        this.boundingBox[0] = (float)(this.patchX * 16);
        this.boundingBox[1] = (float)(this.patchY * 16);
        this.boundingBox[2] = minHeight;
        this.boundingBox[3] = (float)((this.patchX + 1) * 16);
        this.boundingBox[4] = (float)((this.patchY + 1) * 16);
        this.boundingBox[5] = maxHeight;
        this.drawablePatch = null;
    }
}
