package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;

public class PrimDrawParams {
    private final SLTextureEntry textures;
    private final PrimVolumeParams volumeParams;

    public PrimDrawParams(PrimVolumeParams primVolumeParams, SLTextureEntry sLTextureEntry) {
        this.volumeParams = primVolumeParams;
        this.textures = sLTextureEntry;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || !(obj instanceof PrimDrawParams)) {
            return false;
        }
        PrimDrawParams primDrawParams = (PrimDrawParams) obj;
        if ((this.volumeParams == null) != (primDrawParams.volumeParams == null)) {
            return false;
        }
        if (this.volumeParams != null && !this.volumeParams.equals(primDrawParams.volumeParams)) {
            return false;
        }
        if ((this.textures == null) != (primDrawParams.textures == null)) {
            return false;
        }
        return this.textures == null || this.textures.equals(primDrawParams.textures);
    }

    public final SLTextureEntry getTextures() {
        return this.textures;
    }

    public final PrimVolumeParams getVolumeParams() {
        return this.volumeParams;
    }

    public int hashCode() {
        int i = 0;
        if (this.volumeParams != null) {
            i = this.volumeParams.hashCode() + 0;
        }
        return this.textures != null ? i + this.textures.hashCode() : i;
    }
}
