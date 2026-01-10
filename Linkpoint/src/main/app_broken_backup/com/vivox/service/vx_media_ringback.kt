/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_media_ringback {
    vx_media_ringback media_ringback_busy = vx_media_ringback("media_ringback_busy", VxClientProxyJNI.media_ringback_busy_get())
    vx_media_ringback media_ringback_none = vx_media_ringback("media_ringback_none", VxClientProxyJNI.media_ringback_none_get())
    vx_media_ringback media_ringback_ringing = vx_media_ringback("media_ringback_ringing", VxClientProxyJNI.media_ringback_ringing_get())
    private Int swigNext = 0
    private vx_media_ringback[] swigValues = vx_media_ringback[]{media_ringback_none, media_ringback_ringing, media_ringback_busy}
    private String swigName
    private Int swigValue

    private vx_media_ringback(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_media_ringback(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_media_ringback(String string2, vx_media_ringback vx_media_ringback2) {
        this.swigName = string2
        this.swigValue = vx_media_ringback2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_media_ringback swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_media_ringback.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_media_ringback.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_media_ringback.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

