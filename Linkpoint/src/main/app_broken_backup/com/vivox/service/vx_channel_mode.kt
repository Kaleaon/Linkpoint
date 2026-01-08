/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_channel_mode {
    vx_channel_mode channel_mode_auditorium = vx_channel_mode("channel_mode_auditorium", VxClientProxyJNI.channel_mode_auditorium_get())
    vx_channel_mode channel_mode_lecture = vx_channel_mode("channel_mode_lecture", VxClientProxyJNI.channel_mode_lecture_get())
    vx_channel_mode channel_mode_none = vx_channel_mode("channel_mode_none", VxClientProxyJNI.channel_mode_none_get())
    vx_channel_mode channel_mode_normal = vx_channel_mode("channel_mode_normal", VxClientProxyJNI.channel_mode_normal_get())
    vx_channel_mode channel_mode_open = vx_channel_mode("channel_mode_open", VxClientProxyJNI.channel_mode_open_get())
    vx_channel_mode channel_mode_presentation = vx_channel_mode("channel_mode_presentation", VxClientProxyJNI.channel_mode_presentation_get())
    private Int swigNext = 0
    private vx_channel_mode[] swigValues = vx_channel_mode[]{channel_mode_none, channel_mode_normal, channel_mode_presentation, channel_mode_lecture, channel_mode_open, channel_mode_auditorium}
    private String swigName
    private Int swigValue

    private vx_channel_mode(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_channel_mode(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_channel_mode(String string2, vx_channel_mode vx_channel_mode2) {
        this.swigName = string2
        this.swigValue = vx_channel_mode2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_channel_mode swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_channel_mode.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_channel_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_channel_mode.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

