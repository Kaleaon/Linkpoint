// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.google.common.cache.LoadingCache;
import com.lumiyaviewer.lumiya.render.DrawableStore;
import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.res.executors.PrimComputeExecutor;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            DrawListTerrainEntry, SpatialTree, DrawList, DrawListObjectEntry, 
//            FrustrumInfo, FrustrumPlanes, DrawListEntry

public class SpatialObjectIndex
{
    private class DrawListUpdateTask
        implements Runnable, com.lumiyaviewer.lumiya.res.collections.WeakQueue.LowPriority
    {

        final SpatialObjectIndex this$0;

        public void run()
        {
            if (SpatialObjectIndex._2D_get6(SpatialObjectIndex.this) && SpatialObjectIndex._2D_get5(SpatialObjectIndex.this) ^ true)
            {
                boolean flag;
                if (!SpatialObjectIndex._2D_get2(SpatialObjectIndex.this).getAndSet(false))
                {
                    flag = SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isTreeWalkNeeded();
                } else
                {
                    flag = true;
                }
                if (flag)
                {
                    FrustrumPlanes frustrumplanes = SpatialObjectIndex._2D_get4(SpatialObjectIndex.this);
                    FrustrumInfo frustruminfo = SpatialObjectIndex._2D_get3(SpatialObjectIndex.this);
                    if (frustrumplanes != null && frustruminfo != null)
                    {
                        SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).walkTree(frustrumplanes, frustruminfo.viewDistance);
                    }
                }
                if (SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isDrawListChanged())
                {
                    SpatialObjectIndex._2D_set0(SpatialObjectIndex.this, SpatialObjectIndex._2D_wrap2(SpatialObjectIndex.this, SpatialObjectIndex._2D_get0(SpatialObjectIndex.this)));
                }
            }
        }

        private DrawListUpdateTask()
        {
            this$0 = SpatialObjectIndex.this;
            super();
        }

        DrawListUpdateTask(DrawListUpdateTask drawlistupdatetask)
        {
            this();
        }
    }

    private class ObjectsUpdateTask
        implements Runnable
    {

        final SpatialObjectIndex this$0;

        public void run()
        {
            if (!SpatialObjectIndex._2D_get6(SpatialObjectIndex.this) || !(SpatialObjectIndex._2D_get5(SpatialObjectIndex.this) ^ true))
            {
                break MISSING_BLOCK_LABEL_286;
            }
            Object obj1 = SpatialObjectIndex._2D_get7(SpatialObjectIndex.this);
            obj1;
            JVM INSTR monitorenter ;
            Object obj;
            DrawListObjectEntry adrawlistobjectentry[];
            adrawlistobjectentry = (DrawListObjectEntry[])SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).toArray(new DrawListObjectEntry[SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).size()]);
            obj = (DrawListObjectEntry[])SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).toArray(new DrawListObjectEntry[SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).size()]);
            SpatialObjectIndex._2D_get9(SpatialObjectIndex.this).clear();
            SpatialObjectIndex._2D_get8(SpatialObjectIndex.this).clear();
            obj1;
            JVM INSTR monitorexit ;
            boolean flag;
            int k = adrawlistobjectentry.length;
            int j = 0;
            flag = false;
            while (j < k) 
            {
                obj1 = adrawlistobjectentry[j];
                if (!((DrawListObjectEntry) (obj1)).getObjectInfo().isDead)
                {
                    flag |= SpatialObjectIndex._2D_wrap1(SpatialObjectIndex.this, ((DrawListObjectEntry) (obj1)));
                } else
                {
                    flag |= SpatialObjectIndex._2D_wrap0(SpatialObjectIndex.this, ((DrawListObjectEntry) (obj1)));
                }
                j++;
            }
            break MISSING_BLOCK_LABEL_195;
            obj;
            throw obj;
            int l = obj.length;
            boolean flag2 = false;
            boolean flag1 = flag;
            for (int i = ((flag2) ? 1 : 0); i < l; i++)
            {
                DrawListObjectEntry drawlistobjectentry = obj[i];
                flag1 |= SpatialObjectIndex._2D_wrap0(SpatialObjectIndex.this, drawlistobjectentry);
            }

            if (flag1 || SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isDrawListChanged() || SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).isTreeWalkNeeded())
            {
                SpatialObjectIndex._2D_get1(SpatialObjectIndex.this).set(true);
            }
        }

        private ObjectsUpdateTask()
        {
            this$0 = SpatialObjectIndex.this;
            super();
        }

        ObjectsUpdateTask(ObjectsUpdateTask objectsupdatetask)
        {
            this();
        }
    }


    private static final int NUM_DEPTH_BINS = 16;
    private static final float REGION_SIZE_XY = 256F;
    private static final float REGION_SIZE_Z = 4096F;
    private volatile int avatarCountLimit;
    private final AtomicBoolean drawListUpdateRequested = new AtomicBoolean(false);
    private final DrawListUpdateTask drawListUpdateTask = new DrawListUpdateTask(null);
    private final DrawableStore drawableStore;
    private final AtomicBoolean frustrumChanged = new AtomicBoolean(false);
    private volatile FrustrumInfo frustrumInfo;
    private volatile FrustrumPlanes frustrumPlanes;
    private volatile boolean indexDisabled;
    private volatile boolean initialUpdateCompleted;
    private final Object lock = new Object();
    private final Object objectUpdateRemoveLock = new Object();
    private volatile DrawList objectsInFrustrum;
    private final Set objectsToRemove = Collections.newSetFromMap(new IdentityHashMap());
    private final Set objectsToUpdate = Collections.newSetFromMap(new IdentityHashMap());
    private final ObjectsUpdateTask objectsUpdateTask = new ObjectsUpdateTask(null);
    private final SpatialTree spatialTree = new SpatialTree(16, 256F, 256F, 4096F, this);
    private final DrawListTerrainEntry terrain[][] = (DrawListTerrainEntry[][])Array.newInstance(com/lumiyaviewer/lumiya/render/spatial/DrawListTerrainEntry, new int[] {
        16, 16
    });
    private final Map terrainDirty = new HashMap();
    private final Object terrainLock = new Object();
    private final Runnable terrainUpdate = new Runnable() {

        final SpatialObjectIndex this$0;

        public void run()
        {
            int j = 0;
_L5:
            if (!SpatialObjectIndex._2D_get6(SpatialObjectIndex.this) || !(SpatialObjectIndex._2D_get5(SpatialObjectIndex.this) ^ true)) goto _L2; else goto _L1
_L1:
            Object obj1 = SpatialObjectIndex._2D_get13(SpatialObjectIndex.this);
            obj1;
            JVM INSTR monitorenter ;
            Object obj;
            int k;
            Iterator iterator = SpatialObjectIndex._2D_get12(SpatialObjectIndex.this).entrySet().iterator();
            if (!iterator.hasNext())
            {
                break MISSING_BLOCK_LABEL_327;
            }
            obj = (java.util.Map.Entry)iterator.next();
            k = ((Integer)((java.util.Map.Entry) (obj)).getKey()).intValue();
            obj = (TerrainData)((java.util.Map.Entry) (obj)).getValue();
            iterator.remove();
            boolean flag = true;
_L12:
            obj1;
            JVM INSTR monitorexit ;
            if (flag) goto _L3; else goto _L2
_L2:
            if (j != 0)
            {
                SpatialObjectIndex._2D_get1(SpatialObjectIndex.this).set(true);
            }
            return;
            obj;
            throw obj;
_L3:
            if (k < 0 || obj == null) goto _L5; else goto _L4
_L4:
            com.lumiyaviewer.lumiya.slproto.terrain.TerrainPatchInfo terrainpatchinfo;
            j = k % 16;
            k /= 16;
            terrainpatchinfo = ((TerrainData) (obj)).getPatchInfo(j, k);
            if (terrainpatchinfo == null) goto _L7; else goto _L6
_L6:
            obj1 = SpatialObjectIndex._2D_get13(SpatialObjectIndex.this);
            obj1;
            JVM INSTR monitorenter ;
            obj = SpatialObjectIndex._2D_get11(SpatialObjectIndex.this)[j][k];
            if (obj != null) goto _L9; else goto _L8
_L8:
            DrawListTerrainEntry adrawlistterrainentry[];
            adrawlistterrainentry = SpatialObjectIndex._2D_get11(SpatialObjectIndex.this)[j];
            obj = new DrawListTerrainEntry(terrainpatchinfo, j, k);
            adrawlistterrainentry[k] = ((DrawListTerrainEntry) (obj));
_L10:
            obj1;
            JVM INSTR monitorexit ;
            SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).updateObject(((DrawListEntry) (obj)));
_L11:
            j = 1;
              goto _L5
_L9:
            ((DrawListTerrainEntry) (obj)).updatePatchInfo(terrainpatchinfo);
              goto _L10
            obj;
            throw obj;
_L7:
            obj = SpatialObjectIndex._2D_get13(SpatialObjectIndex.this);
            obj;
            JVM INSTR monitorenter ;
            obj1 = SpatialObjectIndex._2D_get11(SpatialObjectIndex.this)[j][k];
            SpatialObjectIndex._2D_get11(SpatialObjectIndex.this)[j][k] = null;
            obj;
            JVM INSTR monitorexit ;
            if (obj1 != null)
            {
                SpatialObjectIndex._2D_get10(SpatialObjectIndex.this).removeObject(((DrawListEntry) (obj1)));
            }
              goto _L11
            Exception exception;
            exception;
            throw exception;
            obj = null;
            k = -1;
            flag = false;
              goto _L12
        }

            
            {
                this$0 = SpatialObjectIndex.this;
                super();
            }
    };

    static int _2D_get0(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.avatarCountLimit;
    }

    static AtomicBoolean _2D_get1(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.drawListUpdateRequested;
    }

    static SpatialTree _2D_get10(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.spatialTree;
    }

    static DrawListTerrainEntry[][] _2D_get11(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.terrain;
    }

    static Map _2D_get12(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.terrainDirty;
    }

    static Object _2D_get13(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.terrainLock;
    }

    static AtomicBoolean _2D_get2(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.frustrumChanged;
    }

    static FrustrumInfo _2D_get3(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.frustrumInfo;
    }

    static FrustrumPlanes _2D_get4(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.frustrumPlanes;
    }

    static boolean _2D_get5(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.indexDisabled;
    }

    static boolean _2D_get6(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.initialUpdateCompleted;
    }

    static Object _2D_get7(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.objectUpdateRemoveLock;
    }

    static Set _2D_get8(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.objectsToRemove;
    }

    static Set _2D_get9(SpatialObjectIndex spatialobjectindex)
    {
        return spatialobjectindex.objectsToUpdate;
    }

    static DrawList _2D_set0(SpatialObjectIndex spatialobjectindex, DrawList drawlist)
    {
        spatialobjectindex.objectsInFrustrum = drawlist;
        return drawlist;
    }

    static boolean _2D_wrap0(SpatialObjectIndex spatialobjectindex, DrawListObjectEntry drawlistobjectentry)
    {
        return spatialobjectindex.handleRemoveObject(drawlistobjectentry);
    }

    static boolean _2D_wrap1(SpatialObjectIndex spatialobjectindex, DrawListObjectEntry drawlistobjectentry)
    {
        return spatialobjectindex.handleUpdateObject(drawlistobjectentry);
    }

    static DrawList _2D_wrap2(SpatialObjectIndex spatialobjectindex, int i)
    {
        return spatialobjectindex.getObjectsInCells(i);
    }

    public SpatialObjectIndex(DrawableStore drawablestore, int i)
    {
        initialUpdateCompleted = false;
        indexDisabled = false;
        frustrumInfo = null;
        frustrumPlanes = null;
        avatarCountLimit = 5;
        drawableStore = drawablestore;
        avatarCountLimit = i;
        objectsInFrustrum = DrawList.create(drawablestore, null, i);
    }

    private DrawList getObjectsInCells(int i)
    {
        DrawList drawlist = DrawList.create(drawableStore, objectsInFrustrum, i);
        spatialTree.addDrawables(drawlist);
        drawlist.initRenderPasses();
        return drawlist;
    }

    private boolean handleRemoveObject(DrawListObjectEntry drawlistobjectentry)
    {
        spatialTree.removeObject(drawlistobjectentry);
        drawlistobjectentry.getObjectInfo().clearDrawListEntry();
        return false;
    }

    private boolean handleUpdateObject(DrawListObjectEntry drawlistobjectentry)
    {
        drawlistobjectentry.updateBoundingBox();
        spatialTree.updateObject(drawlistobjectentry);
        return false;
    }

    private void removeObject(DrawListObjectEntry drawlistobjectentry)
    {
        Object obj = objectUpdateRemoveLock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag;
        boolean flag1;
        flag = objectsToUpdate.remove(drawlistobjectentry);
        flag1 = objectsToRemove.add(drawlistobjectentry);
        obj;
        JVM INSTR monitorexit ;
        if (flag | flag1 && initialUpdateCompleted && indexDisabled ^ true)
        {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask);
        }
        return;
        drawlistobjectentry;
        throw drawlistobjectentry;
    }

    public void completeInitialUpdate()
    {
        initialUpdateCompleted = true;
        if (!indexDisabled)
        {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask);
            PrimComputeExecutor.getInstance().execute(terrainUpdate);
            drawListUpdateRequested.set(true);
        }
    }

    void disableIndex()
    {
        indexDisabled = true;
    }

    DrawableAvatar getDrawableAvatar(SLObjectInfo slobjectinfo)
    {
        return (DrawableAvatar)drawableStore.drawableAvatarCache.getIfPresent(slobjectinfo);
    }

    public DrawList getObjectsInFrustrum()
    {
        return objectsInFrustrum;
    }

    void requestEntryRemoval(DrawListEntry drawlistentry)
    {
        if (drawlistentry instanceof DrawListObjectEntry)
        {
            removeObject((DrawListObjectEntry)drawlistentry);
        }
    }

    public void setAvatarCountLimit(int i)
    {
        avatarCountLimit = i;
    }

    public void setViewport(FrustrumInfo frustruminfo, FrustrumPlanes frustrumplanes)
    {
        boolean flag = true;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (frustrumInfo != null) goto _L2; else goto _L1
_L1:
        frustrumInfo = frustruminfo;
_L4:
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_64;
        }
        frustrumPlanes = frustrumplanes;
        frustrumChanged.set(true);
        if (initialUpdateCompleted && indexDisabled ^ true)
        {
            drawListUpdateRequested.set(true);
        }
        obj;
        JVM INSTR monitorexit ;
        return;
_L2:
        if (!frustrumInfo.equals(frustruminfo))
        {
            frustrumInfo = frustruminfo;
            continue; /* Loop/switch isn't completed */
        }
        break MISSING_BLOCK_LABEL_91;
        frustruminfo;
        throw frustruminfo;
        flag = false;
        if (true) goto _L4; else goto _L3
_L3:
    }

    public boolean updateDrawListIfNeeded()
    {
        if (drawListUpdateRequested.getAndSet(false))
        {
            PrimComputeExecutor.getInstance().execute(drawListUpdateTask);
            return true;
        } else
        {
            return false;
        }
    }

    public void updateObject(DrawListObjectEntry drawlistobjectentry)
    {
        Object obj = objectUpdateRemoveLock;
        obj;
        JVM INSTR monitorenter ;
        if (drawlistobjectentry.getObjectInfo().isDead) goto _L2; else goto _L1
_L1:
        boolean flag = objectsToUpdate.add(drawlistobjectentry);
_L4:
        obj;
        JVM INSTR monitorexit ;
        if (flag && initialUpdateCompleted && indexDisabled ^ true)
        {
            PrimComputeExecutor.getInstance().execute(objectsUpdateTask);
        }
        return;
_L2:
        flag = objectsToRemove.add(drawlistobjectentry);
        if (true) goto _L4; else goto _L3
_L3:
        drawlistobjectentry;
        throw drawlistobjectentry;
    }

    void updateTerrainPatch(int i, int j, TerrainData terraindata)
    {
        Object obj = terrainLock;
        obj;
        JVM INSTR monitorenter ;
        terrainDirty.put(Integer.valueOf(j * 16 + i), terraindata);
        obj;
        JVM INSTR monitorexit ;
        if (initialUpdateCompleted && indexDisabled ^ true)
        {
            PrimComputeExecutor.getInstance().execute(terrainUpdate);
        }
        return;
        terraindata;
        throw terraindata;
    }
}
