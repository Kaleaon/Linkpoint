// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import com.lumiyaviewer.lumiya.slproto.textures.SLTextureEntry;

public class PrimDrawParams
{
    private final SLTextureEntry textures;
    private final PrimVolumeParams volumeParams;
    
    public PrimDrawParams(final PrimVolumeParams volumeParams, final SLTextureEntry textures) {
        this.volumeParams = volumeParams;
        this.textures = textures;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof PrimDrawParams)) {
            return false;
        }
        final PrimDrawParams primDrawParams = (PrimDrawParams)o;
        int n;
        if (this.volumeParams == null) {
            n = 1;
        }
        else {
            n = 0;
        }
        int n2;
        if (primDrawParams.volumeParams == null) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n != n2) {
            return false;
        }
        if (this.volumeParams != null && !this.volumeParams.equals(primDrawParams.volumeParams)) {
            return false;
        }
        boolean b;
        if (this.textures == null) {
            b = true;
        }
        else {
            b = false;
        }
        boolean b2;
        if (primDrawParams.textures == null) {
            b2 = true;
        }
        else {
            b2 = false;
        }
        return b == b2 && (this.textures == null || this.textures.equals(primDrawParams.textures));
    }
    
    public final SLTextureEntry getTextures() {
        return this.textures;
    }
    
    public final PrimVolumeParams getVolumeParams() {
        return this.volumeParams;
    }
    
    @Override
    public int hashCode() {
        int n = 0;
        if (this.volumeParams != null) {
            n = this.volumeParams.hashCode() + 0;
        }
        int n2 = n;
        if (this.textures != null) {
            n2 = n + this.textures.hashCode();
        }
        return n2;
    }
}
