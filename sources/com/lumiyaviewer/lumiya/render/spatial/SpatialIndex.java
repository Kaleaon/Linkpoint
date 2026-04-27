// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.render.spatial;

import com.lumiyaviewer.lumiya.render.avatar.DrawableAvatar;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.terrain.TerrainData;
import java.lang.ref.WeakReference;

// Referenced classes of package com.lumiyaviewer.lumiya.render.spatial:
//            SpatialObjectIndex

public class SpatialIndex
{
    private static class InstanceHolder
    {

        private static final SpatialIndex instance = new SpatialIndex(null);

        static SpatialIndex _2D_get0()
        {
            return instance;
        }


        private InstanceHolder()
        {
        }
    }


    private volatile WeakReference indexHolder;
    private volatile SpatialObjectIndex objectIndex;

    private SpatialIndex()
    {
        indexHolder = null;
        objectIndex = null;
    }

    SpatialIndex(SpatialIndex spatialindex)
    {
        this();
    }

    public static SpatialIndex getInstance()
    {
        return InstanceHolder._2D_get0();
    }

    public void DisableObjectIndex(Object obj)
    {
        Object obj1 = null;
        this;
        JVM INSTR monitorenter ;
        SpatialObjectIndex spatialobjectindex;
        spatialobjectindex = objectIndex;
        if (indexHolder != null)
        {
            obj1 = indexHolder.get();
        }
          goto _L1
_L2:
        spatialobjectindex.disableIndex();
_L3:
        indexHolder = null;
        objectIndex = null;
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
_L1:
        if (spatialobjectindex == null || obj1 != obj && obj1 != null) goto _L3; else goto _L2
    }

    public SpatialObjectIndex EnableObjectIndex(SpatialObjectIndex spatialobjectindex, Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        objectIndex = spatialobjectindex;
        indexHolder = new WeakReference(obj);
        spatialobjectindex = objectIndex;
        this;
        JVM INSTR monitorexit ;
        return spatialobjectindex;
        spatialobjectindex;
        throw spatialobjectindex;
    }

    public DrawableAvatar getDrawableAvatar(SLObjectInfo slobjectinfo)
    {
        SpatialObjectIndex spatialobjectindex = objectIndex;
        if (spatialobjectindex != null)
        {
            return spatialobjectindex.getDrawableAvatar(slobjectinfo);
        } else
        {
            return null;
        }
    }

    public SpatialObjectIndex getObjectIndex()
    {
        this;
        JVM INSTR monitorenter ;
        SpatialObjectIndex spatialobjectindex = objectIndex;
        this;
        JVM INSTR monitorexit ;
        return spatialobjectindex;
        Exception exception;
        exception;
        throw exception;
    }

    public void setAvatarCountLimit(int i)
    {
        SpatialObjectIndex spatialobjectindex = objectIndex;
        if (spatialobjectindex != null)
        {
            spatialobjectindex.setAvatarCountLimit(i);
        }
    }

    public void updateTerrainPatch(int i, int j, TerrainData terraindata)
    {
        SpatialObjectIndex spatialobjectindex = objectIndex;
        if (spatialobjectindex != null)
        {
            spatialobjectindex.updateTerrainPatch(i, j, terraindata);
        }
    }
}
