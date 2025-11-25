package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry

class PrimDrawParams(
    val volumeParams: PrimVolumeParams?,
    val textures: SLTextureEntry?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is PrimDrawParams) return false
        
        if (this.volumeParams == null && other.volumeParams != null) return false
        if (this.volumeParams != null && other.volumeParams == null) return false
        if (this.volumeParams != null && this.volumeParams != other.volumeParams) return false
        
        if (this.textures == null && other.textures != null) return false
        if (this.textures != null && other.textures == null) return false
        if (this.textures != null && this.textures != other.textures) return false
        
        return true
    }

    fun getTextures(): SLTextureEntry? {
        return this.textures
    }

    fun getVolumeParams(): PrimVolumeParams? {
        return this.volumeParams
    }

    override fun hashCode(): Int {
        var i = 0
        if (this.volumeParams != null) {
            i = this.volumeParams.hashCode()
        }
        return if (this.textures != null) i + this.textures.hashCode() else i
    }
}
