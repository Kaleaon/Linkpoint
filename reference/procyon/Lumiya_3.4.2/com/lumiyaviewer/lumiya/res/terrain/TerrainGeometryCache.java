// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.render.terrain.TerrainPatchGeometry;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
import com.lumiyaviewer.lumiya.res.ResourceMemoryCache;

public class TerrainGeometryCache extends ResourceMemoryCache<TerrainPatchHeightMap, TerrainPatchGeometry>
{
    @Override
    protected ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> CreateNewRequest(final TerrainPatchHeightMap terrainPatchHeightMap, final ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
        return new TerrainGeometryRequest(terrainPatchHeightMap, resourceManager);
    }
    
    private static class TerrainGeometryRequest extends ResourceRequest<TerrainPatchHeightMap, TerrainPatchGeometry> implements Runnable
    {
        public TerrainGeometryRequest(final TerrainPatchHeightMap terrainPatchHeightMap, final ResourceManager<TerrainPatchHeightMap, TerrainPatchGeometry> resourceManager) {
            super(terrainPatchHeightMap, resourceManager);
        }
        
        @Override
        public void cancelRequest() {
            PrimComputeExecutor.getInstance().remove(this);
            super.cancelRequest();
        }
        
        @Override
        public void execute() {
            PrimComputeExecutor.getInstance().execute(this);
        }
        
        @Override
        public void run() {
            try {
                ((ResourceRequest<ResourceParams, TerrainPatchGeometry>)this).completeRequest(new TerrainPatchGeometry(((ResourceRequest<TerrainPatchHeightMap, ResourceType>)this).getParams()));
            }
            catch (final Exception ex) {
                Debug.Warning(ex);
                ((ResourceRequest<ResourceParams, TerrainPatchGeometry>)this).completeRequest(null);
            }
        }
    }
}
