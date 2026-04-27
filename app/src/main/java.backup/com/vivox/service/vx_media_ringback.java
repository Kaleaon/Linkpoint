/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_media_ringback {
    public static final vx_media_ringback media_ringback_busy = new vx_media_ringback("media_ringback_busy", VxClientProxyJNI.media_ringback_busy_get());
    public static final vx_media_ringback media_ringback_none = new vx_media_ringback("media_ringback_none", VxClientProxyJNI.media_ringback_none_get());
    public static final vx_media_ringback media_ringback_ringing = new vx_media_ringback("media_ringback_ringing", VxClientProxyJNI.media_ringback_ringing_get());
    private static int swigNext = 0;
    private static vx_media_ringback[] swigValues = new vx_media_ringback[]{media_ringback_none, media_ringback_ringing, media_ringback_busy};
    private final String swigName;
    private final int swigValue;

    private vx_media_ringback(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_media_ringback(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_media_ringback(String string2, vx_media_ringback vx_media_ringback2) {
        this.swigName = string2;
        this.swigValue = vx_media_ringback2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_media_ringback swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_media_ringback.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_media_ringback.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_media_ringback.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

