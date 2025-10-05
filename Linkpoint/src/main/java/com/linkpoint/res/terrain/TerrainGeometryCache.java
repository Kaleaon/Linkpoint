package com.linkpoint.res.terrain;
import java.util.*;

import com.linkpoint.Debug;
import com.linkpoint.render.terrain.TerrainPatchGeometry;
import com.linkpoint.res.ResourceManager;
import com.linkpoint.res.ResourceMemoryCache;
import com.linkpoint.res.ResourceRequest;
import com.linkpoint.res.executors.PrimComputeExecutor;
import com.linkpoint.slproto.terrain.TerrainPatchHeightMap;

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
