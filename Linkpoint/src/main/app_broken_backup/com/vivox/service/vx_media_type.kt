/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_media_type {
    vx_media_type media_type_audio = vx_media_type("media_type_audio")
    vx_media_type media_type_audiovideo = vx_media_type("media_type_audiovideo")
    vx_media_type media_type_none = vx_media_type("media_type_none", VxClientProxyJNI.media_type_none_get())
    vx_media_type media_type_text = vx_media_type("media_type_text")
    vx_media_type media_type_video = vx_media_type("media_type_video")
    private Int swigNext = 0
    private vx_media_type[] swigValues = vx_media_type[]{media_type_none, media_type_text, media_type_audio, media_type_video, media_type_audiovideo}
    private String swigName
    private Int swigValue

    private vx_media_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_media_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_media_type(String string2, vx_media_type vx_media_type2) {
        this.swigName = string2
        this.swigValue = vx_media_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_media_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_media_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_media_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_media_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

