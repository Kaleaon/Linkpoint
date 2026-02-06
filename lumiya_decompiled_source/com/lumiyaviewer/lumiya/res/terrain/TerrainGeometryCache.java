package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
/* loaded from: classes.dex */
public class TerrainGeometryCache extends ResourceMemoryCache<TerrainPatchHeightMap, TerrainPatchGeometry> {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class TerrainGeometryRequest extends ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> implements Runnable {
        public TerrainGeometryRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
            super(terrainPatchHeightMap, resourceManager);
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }

        @Override // com.lumiyaviewer.lumiya.res.ResourceRequest
        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                completeRequest(new TerrainPatchGeometry(getParams()));
            } catch (Exception e) {
                Debug.Warning(e);
                completeRequest(null);
            }
        }
    }

    protected ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> CreateNewRequest(TerrainPatchHeightMap terrainPatchHeightMap, ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
        return new TerrainGeometryRequest(terrainPatchHeightMap, resourceManager);
    }

    @Override // com.lumiyaviewer.lumiya.res.ResourceManager
    protected /* bridge */ /* synthetic */ ResourceRequest CreateNewRequest(Object obj, ResourceManager resourceManager) {
        return CreateNewRequest((TerrainPatchHeightMap) obj, (ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry>) resourceManager);
    }
}
