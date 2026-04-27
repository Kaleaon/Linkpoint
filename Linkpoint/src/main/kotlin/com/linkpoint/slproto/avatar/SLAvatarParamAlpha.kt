package com.linkpoint.slproto.avatar

import javax.annotation.Nullable

class SLAvatarParamAlpha {
    val Float domain
    val Boolean multiplyBlend
    val Boolean skipIfZero
    val String tgaFile

    SLAvatarParamAlpha(Float f, String str, Boolean z, Boolean z2) {
        this.domain = f
        this.tgaFile = str
        this.skipIfZero = z
        this.multiplyBlend = z2
    }

     public override fun equals(obj: Object): Boolean {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        val sLAvatarParamAlpha: SLAvatarParamAlpha = (SLAvatarParamAlpha) obj
        if (Float.compare(sLAvatarParamAlpha.domain, this.domain) == 0 && this.skipIfZero == sLAvatarParamAlpha.skipIfZero && this.multiplyBlend == sLAvatarParamAlpha.multiplyBlend) {
            return this.tgaFile != null ? this.tgaFile.equals(sLAvatarParamAlpha.tgaFile) : sLAvatarParamAlpha.tgaFile == null
        }
        return false
    }

     public override fun hashCode(): Int {
        val i: Int = 1
        val hashCode: Int = ((this.skipIfZero ? 1 : 0) + (((this.tgaFile != null ? this.tgaFile.hashCode() : 0) + ((this.domain != 0.0f ? Float.floatToIntBits(this.domain) : 0) * 31)) * 31)) * 31
        if (!this.multiplyBlend) {
            i = 0
        }
        return hashCode + i
    }
}
