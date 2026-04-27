// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.avatar;

import javax.annotation.Nullable;

public class SLAvatarParamAlpha
{
    public final float domain;
    public final boolean multiplyBlend;
    public final boolean skipIfZero;
    @Nullable
    public final String tgaFile;
    
    SLAvatarParamAlpha(final float domain, @Nullable final String tgaFile, final boolean skipIfZero, final boolean multiplyBlend) {
        this.domain = domain;
        this.tgaFile = tgaFile;
        this.skipIfZero = skipIfZero;
        this.multiplyBlend = multiplyBlend;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean equals = true;
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        final SLAvatarParamAlpha slAvatarParamAlpha = (SLAvatarParamAlpha)o;
        if (Float.compare(slAvatarParamAlpha.domain, this.domain) != 0) {
            return false;
        }
        if (this.skipIfZero != slAvatarParamAlpha.skipIfZero) {
            return false;
        }
        if (this.multiplyBlend != slAvatarParamAlpha.multiplyBlend) {
            return false;
        }
        if (this.tgaFile != null) {
            equals = this.tgaFile.equals(slAvatarParamAlpha.tgaFile);
        }
        else if (slAvatarParamAlpha.tgaFile != null) {
            equals = false;
        }
        return equals;
    }
    
    @Override
    public int hashCode() {
        int n = 1;
        int floatToIntBits;
        if (this.domain != 0.0f) {
            floatToIntBits = Float.floatToIntBits(this.domain);
        }
        else {
            floatToIntBits = 0;
        }
        int hashCode;
        if (this.tgaFile != null) {
            hashCode = this.tgaFile.hashCode();
        }
        else {
            hashCode = 0;
        }
        int n2;
        if (this.skipIfZero) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (!this.multiplyBlend) {
            n = 0;
        }
        return (n2 + (hashCode + floatToIntBits * 31) * 31) * 31 + n;
    }
}
