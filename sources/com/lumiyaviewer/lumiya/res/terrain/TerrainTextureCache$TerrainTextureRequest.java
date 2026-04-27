// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.terrain;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.openjpeg.OpenJPEG;
import com.lumiyaviewer.lumiya.render.tex.DrawableTextureParams;
import com.lumiyaviewer.lumiya.render.tex.TextureClass;
import com.lumiyaviewer.lumiya.res.ResourceConsumer;
import com.lumiyaviewer.lumiya.res.ResourceManager;
import com.lumiyaviewer.lumiya.res.ResourceRequest;
import com.lumiyaviewer.lumiya.res.textures.TextureCache;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchHeightMap;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainTextures;
import com.lumiyaviewer.lumiya.utils.HasPriority;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

// Referenced classes of package com.lumiyaviewer.lumiya.res.terrain:
//            TerrainTextureCache

private static class bakingFuture extends ResourceRequest
    implements Runnable, HasPriority
{
    private class TerrainRawTextureRequest
        implements ResourceConsumer
    {

        private final int layer;
        final TerrainTextureCache.TerrainTextureRequest this$1;

        public void OnResourceReady(Object obj, boolean flag)
        {
            if (obj instanceof OpenJPEG)
            {
                onLayerReady(layer, (OpenJPEG)obj);
            } else
            if (obj == null)
            {
                onLayerReady(layer, null);
                return;
            }
        }

        public TerrainRawTextureRequest(UUID uuid, int i)
        {
            this$1 = TerrainTextureCache.TerrainTextureRequest.this;
            super();
            layer = i;
            TextureCache.getInstance().RequestResource(DrawableTextureParams.create(uuid, TextureClass.Terrain), this);
        }
    }


    private volatile Future bakingFuture;
    private int layerNeededMask;
    private int layerReadyMask;
    private final TerrainRawTextureRequest rawRequests[] = new TerrainRawTextureRequest[4];
    private final OpenJPEG rawTextures[] = new OpenJPEG[4];

    public void cancelRequest()
    {
        int i = 0;
        this;
        JVM INSTR monitorenter ;
_L2:
        if (i >= 4)
        {
            break; /* Loop/switch isn't completed */
        }
        rawRequests[i] = null;
        i++;
        if (true) goto _L2; else goto _L1
_L1:
        Future future = bakingFuture;
        if (future == null)
        {
            break MISSING_BLOCK_LABEL_40;
        }
        future.cancel(false);
        this;
        JVM INSTR monitorexit ;
        super.cancelRequest();
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public void execute()
    {
        int i;
        i = 0;
        layerNeededMask = ((TerrainPatchInfo)getParams()).getLayerMask();
        layerReadyMask = 0;
        if (layerNeededMask == 0)
        {
            break MISSING_BLOCK_LABEL_99;
        }
        this;
        JVM INSTR monitorenter ;
_L3:
        if (i >= 4) goto _L2; else goto _L1
_L1:
        if (rawRequests[i] == null && (layerNeededMask & 1 << i) != 0)
        {
            rawRequests[i] = new TerrainRawTextureRequest(((TerrainPatchInfo)getParams()).getTextures().getTextureUUID(i), i);
        }
        i++;
          goto _L3
_L2:
        return;
        Exception exception;
        exception;
        throw exception;
        bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
        return;
    }

    public int getPriority()
    {
        return 0;
    }

    protected void onLayerReady(int i, OpenJPEG openjpeg)
    {
        boolean flag = false;
        this;
        JVM INSTR monitorenter ;
        rawTextures[i] = openjpeg;
        layerReadyMask = layerReadyMask | 1 << i;
        if (openjpeg == null) goto _L2; else goto _L1
_L1:
        openjpeg = openjpeg.toString();
_L9:
        Debug.Printf("Terrain: onLayerReady (%d), rawTexture %s, layerNeededMask %d, layerReadyMask %d", new Object[] {
            Integer.valueOf(i), openjpeg, Integer.valueOf(layerNeededMask), Integer.valueOf(layerReadyMask)
        });
        if ((layerNeededMask & layerReadyMask) != layerNeededMask) goto _L4; else goto _L3
_L3:
        i = 0;
_L11:
        if (i >= 4) goto _L6; else goto _L5
_L5:
        if ((layerNeededMask & 1 << i) == 0 || rawTextures[i] != null)
        {
            break MISSING_BLOCK_LABEL_180;
        }
        Debug.Printf("Terrain: texture for layer %d is not ready", new Object[] {
            Integer.valueOf(i)
        });
        i = ((flag) ? 1 : 0);
_L10:
        if (i == 0) goto _L8; else goto _L7
_L7:
        bakingFuture = TextureCache.getInstance().getDecompressorExecutor().submit(this);
_L4:
        this;
        JVM INSTR monitorexit ;
        return;
_L2:
        openjpeg = "null";
          goto _L9
_L8:
        completeRequest(null);
          goto _L4
        openjpeg;
        throw openjpeg;
_L6:
        i = 1;
          goto _L10
        i++;
          goto _L11
    }

    public void run()
    {
        try
        {
            Object obj = (TerrainPatchInfo)getParams();
            obj = OpenJPEG.bakeTerrain(256, 256, rawTextures, ((TerrainPatchInfo) (obj)).getTextureHeightMap(), ((TerrainPatchInfo) (obj)).getHeightMap().getMapWidth(), ((TerrainPatchInfo) (obj)).getHeightMap().getMapHeight());
            Debug.Printf("Terrain: Baked texture producer: produced baked texture", new Object[0]);
            completeRequest(obj);
            return;
        }
        catch (Exception exception)
        {
            Debug.Warning(exception);
        }
        completeRequest(null);
    }

    public TerrainRawTextureRequest.layer(TerrainPatchInfo terrainpatchinfo, ResourceManager resourcemanager)
    {
        super(terrainpatchinfo, resourcemanager);
        bakingFuture = null;
    }
}
