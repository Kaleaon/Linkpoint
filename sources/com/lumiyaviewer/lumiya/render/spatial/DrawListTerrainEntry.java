// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.terrain.DrawableTerrainPatch;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListEntry, DrawList

class DrawListTerrainEntry extends DrawListEntry
{

    private volatile WeakReference drawablePatch;
    private TerrainPatchInfo patchInfo;
    private final int patchX;
    private final int patchY;

    DrawListTerrainEntry(TerrainPatchInfo terrainpatchinfo, int i, int j)
    {
        patchX = i;
        patchY = j;
        updatePatchInfo(terrainpatchinfo);
    }

    public void addToDrawList(DrawList drawlist)
    {
        DrawableTerrainPatch drawableterrainpatch = null;
        Object obj = drawablePatch;
        if (obj != null)
        {
            drawableterrainpatch = (DrawableTerrainPatch)((WeakReference) (obj)).get();
        }
        obj = drawableterrainpatch;
        if (drawableterrainpatch == null)
        {
            obj = new DrawableTerrainPatch(drawlist.drawableStore.terrainGeometryCache, drawlist.drawableStore.glTerrainTextureCache, patchInfo, patchX, patchY);
            drawablePatch = new WeakReference(obj);
        }
        drawlist.terrain.add(obj);
    }

    public void updatePatchInfo(TerrainPatchInfo terrainpatchinfo)
    {
        patchInfo = terrainpatchinfo;
        float f = terrainpatchinfo.getMinHeight();
        float f1 = terrainpatchinfo.getMaxHeight();
        boundingBox[0] = patchX * 16;
        boundingBox[1] = patchY * 16;
        boundingBox[2] = f;
        boundingBox[3] = (patchX + 1) * 16;
        boundingBox[4] = (patchY + 1) * 16;
        boundingBox[5] = f1;
        drawablePatch = null;
    }
}
