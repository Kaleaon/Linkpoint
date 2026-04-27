/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_media_type {
    public static final vx_media_type media_type_audio = new vx_media_type("media_type_audio");
    public static final vx_media_type media_type_audiovideo = new vx_media_type("media_type_audiovideo");
    public static final vx_media_type media_type_none = new vx_media_type("media_type_none", VxClientProxyJNI.media_type_none_get());
    public static final vx_media_type media_type_text = new vx_media_type("media_type_text");
    public static final vx_media_type media_type_video = new vx_media_type("media_type_video");
    private static int swigNext = 0;
    private static vx_media_type[] swigValues = new vx_media_type[]{media_type_none, media_type_text, media_type_audio, media_type_video, media_type_audiovideo};
    private final String swigName;
    private final int swigValue;

    private vx_media_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_media_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_media_type(String string2, vx_media_type vx_media_type2) {
        this.swigName = string2;
        this.swigValue = vx_media_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_media_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_media_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_media_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_media_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

