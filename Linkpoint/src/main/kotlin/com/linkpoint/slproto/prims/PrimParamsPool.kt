package com.linkpoint.slproto.prims

import com.linkpoint.utils.InternPool

class PrimParamsPool {
    @JvmStatic
    val drawParamsPool: InternPool<PrimDrawParams> = InternPool<>()
    @JvmStatic
    val pathParamsPool: InternPool<PrimPathParams> = InternPool<>()
    @JvmStatic
    val profileParamsPool: InternPool<PrimProfileParams> = InternPool<>()
    @JvmStatic
    val volumeParamsPool: InternPool<PrimVolumeParams> = InternPool<>()

    @JvmStatic
     fun get(primDrawParams: PrimDrawParams): PrimDrawParams {
        return drawParamsPool.intern(primDrawParams)
    }

    @JvmStatic
     fun get(primPathParams: PrimPathParams): PrimPathParams {
        return pathParamsPool.intern(primPathParams)
    }

    @JvmStatic
     fun get(primProfileParams: PrimProfileParams): PrimProfileParams {
        return profileParamsPool.intern(primProfileParams)
    }

    @JvmStatic
     fun get(primVolumeParams: PrimVolumeParams): PrimVolumeParams {
        return volumeParamsPool.intern(primVolumeParams)
    }
}
