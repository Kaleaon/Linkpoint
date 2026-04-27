package com.lumiyaviewer.lumiya.slproto.avatar;

import javax.annotation.Nullable;

public class SLAvatarParamAlpha {
    public final float domain;
    public final boolean multiplyBlend;
    public final boolean skipIfZero;
    @Nullable
    public final String tgaFile;

    SLAvatarParamAlpha(float f, @Nullable String str, boolean z, boolean z2) {
        this.domain = f;
        this.tgaFile = str;
        this.skipIfZero = z;
        this.multiplyBlend = z2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SLAvatarParamAlpha sLAvatarParamAlpha = (SLAvatarParamAlpha) obj;
        if (Float.compare(sLAvatarParamAlpha.domain, this.domain) == 0 && this.skipIfZero == sLAvatarParamAlpha.skipIfZero && this.multiplyBlend == sLAvatarParamAlpha.multiplyBlend) {
            return this.tgaFile != null ? this.tgaFile.equals(sLAvatarParamAlpha.tgaFile) : sLAvatarParamAlpha.tgaFile == null;
        }
        return false;
    }

    public int hashCode() {
        int i = 1;
        int hashCode = ((this.skipIfZero ? 1 : 0) + (((this.tgaFile != null ? this.tgaFile.hashCode() : 0) + ((this.domain != 0.0f ? Float.floatToIntBits(this.domain) : 0) * 31)) * 31)) * 31;
        if (!this.multiplyBlend) {
            i = 0;
        }
        return hashCode + i;
    }
}
