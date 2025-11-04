// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.utils.InternPool;

public class PrimParamsPool
{
    public static InternPool<PrimDrawParams> drawParamsPool;
    public static InternPool<PrimPathParams> pathParamsPool;
    public static InternPool<PrimProfileParams> profileParamsPool;
    public static InternPool<PrimVolumeParams> volumeParamsPool;
    
    static {
        PrimParamsPool.pathParamsPool = new InternPool<PrimPathParams>();
        PrimParamsPool.profileParamsPool = new InternPool<PrimProfileParams>();
        PrimParamsPool.volumeParamsPool = new InternPool<PrimVolumeParams>();
        PrimParamsPool.drawParamsPool = new InternPool<PrimDrawParams>();
    }
    
    public static PrimDrawParams get(final PrimDrawParams primDrawParams) {
        return PrimParamsPool.drawParamsPool.intern(primDrawParams);
    }
    
    public static PrimPathParams get(final PrimPathParams primPathParams) {
        return PrimParamsPool.pathParamsPool.intern(primPathParams);
    }
    
    public static PrimProfileParams get(final PrimProfileParams primProfileParams) {
        return PrimParamsPool.profileParamsPool.intern(primProfileParams);
    }
    
    public static PrimVolumeParams get(final PrimVolumeParams primVolumeParams) {
        return PrimParamsPool.volumeParamsPool.intern(primVolumeParams);
    }
}
