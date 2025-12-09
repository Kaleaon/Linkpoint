package com.linkpoint.slproto.prims

import com.linkpoint.slproto.textures.SLTextureEntry

class PrimDrawParams {
    private SLTextureEntry textures
    private PrimVolumeParams volumeParams

    PrimDrawParams(PrimVolumeParams primVolumeParams, SLTextureEntry sLTextureEntry) {
        this.volumeParams = primVolumeParams
        this.textures = sLTextureEntry
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (obj == null || !(obj instanceof PrimDrawParams)) {
            return false
        }
        PrimDrawParams primDrawParams = (PrimDrawParams) obj
        if ((this.volumeParams == null) != (primDrawParams.volumeParams == null)) {
            return false
        }
        if (this.volumeParams != null && !this.volumeParams.equals(primDrawParams.volumeParams)) {
            return false
        }
        if ((this.textures == null) != (primDrawParams.textures == null)) {
            return false
        }
        return this.textures == null || this.textures.equals(primDrawParams.textures)
    }

    fun getTextures(): SLTextureEntry {
        return this.textures
    }

    fun getVolumeParams(): PrimVolumeParams {
        return this.volumeParams
    }

    fun hashCode(): Int {
        Int i = 0
        if (this.volumeParams != null) {
            i = this.volumeParams.hashCode() + 0
        }
        return this.textures != null ? i + this.textures.hashCode() : i
    }
}
