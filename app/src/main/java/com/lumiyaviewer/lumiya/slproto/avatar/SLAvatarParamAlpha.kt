package com.lumiyaviewer.lumiya.slproto.avatar

import javax.annotation.Nullable

class SLAvatarParamAlpha {
    float domain
    Boolean multiplyBlend
    Boolean skipIfZero
    @Nullable
    String tgaFile

    SLAvatarParamAlpha(float f, @Nullable String str, Boolean z, Boolean z2) {
        this.domain = f
        this.tgaFile = str
        this.skipIfZero = z
        this.multiplyBlend = z2
    }

    Boolean equals(Object obj) {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        SLAvatarParamAlpha sLAvatarParamAlpha = (SLAvatarParamAlpha) obj
        if (Float.compare(sLAvatarParamAlpha.domain, this.domain) == 0 && this.skipIfZero == sLAvatarParamAlpha.skipIfZero && this.multiplyBlend == sLAvatarParamAlpha.multiplyBlend) {
            return this.tgaFile != null ? this.tgaFile.equals(sLAvatarParamAlpha.tgaFile) : sLAvatarParamAlpha.tgaFile == null
        }
        return false
    }

    Int hashCode() {
        Int i = 1
        Int hashCode = ((this.skipIfZero ? 1 : 0) + (((this.tgaFile != null ? this.tgaFile.hashCode() : 0) + ((this.domain != 0.0f ? Float.floatToIntBits(this.domain) : 0) * 31)) * 31)) * 31
        if (!this.multiplyBlend) {
            i = 0
        }
        return hashCode + i
    }
}
