/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_media_completion_type {
    vx_media_completion_type aux_buffer_audio_capture = vx_media_completion_type("aux_buffer_audio_capture", VxClientProxyJNI.aux_buffer_audio_capture_get())
    vx_media_completion_type aux_buffer_audio_render = vx_media_completion_type("aux_buffer_audio_render", VxClientProxyJNI.aux_buffer_audio_render_get())
    vx_media_completion_type media_completion_type_none = vx_media_completion_type("media_completion_type_none", VxClientProxyJNI.media_completion_type_none_get())
    vx_media_completion_type sessiongroup_audio_injection = vx_media_completion_type("sessiongroup_audio_injection", VxClientProxyJNI.sessiongroup_audio_injection_get())
    private Int swigNext = 0
    private vx_media_completion_type[] swigValues = vx_media_completion_type[]{media_completion_type_none, aux_buffer_audio_capture, aux_buffer_audio_render, sessiongroup_audio_injection}
    private String swigName
    private Int swigValue

    private vx_media_completion_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_media_completion_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_media_completion_type(String string2, vx_media_completion_type vx_media_completion_type2) {
        this.swigName = string2
        this.swigValue = vx_media_completion_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_media_completion_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_media_completion_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_media_completion_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_media_completion_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

