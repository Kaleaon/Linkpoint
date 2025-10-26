package com.linkpoint.slproto.prims

import com.linkpoint.slproto.textures.SLTextureEntry

class PrimDrawParams {
    private val SLTextureEntry textures
    private val PrimVolumeParams volumeParams

    public PrimDrawParams(PrimVolumeParams primVolumeParams, SLTextureEntry sLTextureEntry) {
        this.volumeParams = primVolumeParams
        this.textures = sLTextureEntry
    }

     public fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (obj == null || !(obj instanceof PrimDrawParams)) {
            return false
        }
        val primDrawParams: PrimDrawParams = (PrimDrawParams) obj
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

    val SLTextureEntry getTextures() {
        return this.textures
    }

    val PrimVolumeParams getVolumeParams() {
        return this.volumeParams
    }

     public fun hashCode(): Int {
        val i: Int = 0
        if (this.volumeParams != null) {
            i = this.volumeParams.hashCode() + 0
        }
        return this.textures != null ? i + this.textures.hashCode() : i
    }
}
