package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.utils.InternPool;

public class PrimParamsPool {
    public static InternPool<PrimDrawParams> drawParamsPool = new InternPool<>();
    public static InternPool<PrimPathParams> pathParamsPool = new InternPool<>();
    public static InternPool<PrimProfileParams> profileParamsPool = new InternPool<>();
    public static InternPool<PrimVolumeParams> volumeParamsPool = new InternPool<>();

    public static PrimDrawParams get(PrimDrawParams primDrawParams) {
        return drawParamsPool.intern(primDrawParams);
    }

    public static PrimPathParams get(PrimPathParams primPathParams) {
        return pathParamsPool.intern(primPathParams);
    }

    public static PrimProfileParams get(PrimProfileParams primProfileParams) {
        return profileParamsPool.intern(primProfileParams);
    }

    public static PrimVolumeParams get(PrimVolumeParams primVolumeParams) {
        return volumeParamsPool.intern(primVolumeParams);
    }
}
