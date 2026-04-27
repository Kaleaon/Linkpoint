package com.lumiyaviewer.lumiya.res.terrain;
import java.util.*;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;

public class TerrainGeometryCache extends ResourceMemoryCache<TerrainPatchHeightMap, TerrainPatchGeometry> {

    private static class TerrainGeometryRequest extends ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> implements Runnable {
        public TerrainGeometryRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
            super(terrainPatchHeightMap, resourceManager);
        }

        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }

        public void run() {
            try {
                completeRequest(new TerrainPatchGeometry((TerrainPatchHeightMap) getParams()));
            } catch (Exception e) {
                Debug.Warning(e);
                completeRequest(null);
            }
        }
    }

    /* access modifiers changed from: protected */
    public ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> CreateNewRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
        return new TerrainGeometryRequest(terrainPatchHeightMap, resourceManager);
    }
}
