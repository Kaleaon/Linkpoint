package com.linkpoint.slproto.prims

import com.linkpoint.utils.InternPool

class PrimParamsPool {
    @JvmStatic
    InternPool<PrimDrawParams> drawParamsPool = InternPool<>()
    @JvmStatic
    InternPool<PrimPathParams> pathParamsPool = InternPool<>()
    @JvmStatic
    InternPool<PrimProfileParams> profileParamsPool = InternPool<>()
    @JvmStatic
    InternPool<PrimVolumeParams> volumeParamsPool = InternPool<>()

    @JvmStatic
    PrimDrawParams get(PrimDrawParams primDrawParams) {
        return drawParamsPool.intern(primDrawParams)
    }

    @JvmStatic
    PrimPathParams get(PrimPathParams primPathParams) {
        return pathParamsPool.intern(primPathParams)
    }

    @JvmStatic
    PrimProfileParams get(PrimProfileParams primProfileParams) {
        return profileParamsPool.intern(primProfileParams)
    }

    @JvmStatic
    PrimVolumeParams get(PrimVolumeParams primVolumeParams) {
        return volumeParamsPool.intern(primVolumeParams)
    }
}
