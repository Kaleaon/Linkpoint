/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_sessiongroup_audio_injection_control_type {
    vx_sessiongroup_audio_injection_control_type VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX = vx_sessiongroup_audio_injection_control_type("VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX", VxClientProxyJNI.VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX_get())
    vx_sessiongroup_audio_injection_control_type VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN = vx_sessiongroup_audio_injection_control_type("VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN", VxClientProxyJNI.VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN_get())
    vx_sessiongroup_audio_injection_control_type VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART = vx_sessiongroup_audio_injection_control_type("VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART", VxClientProxyJNI.VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART_get())
    vx_sessiongroup_audio_injection_control_type VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START = vx_sessiongroup_audio_injection_control_type("VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START", VxClientProxyJNI.VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START_get())
    vx_sessiongroup_audio_injection_control_type VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP = vx_sessiongroup_audio_injection_control_type("VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP", VxClientProxyJNI.VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP_get())
    private Int swigNext = 0
    private vx_sessiongroup_audio_injection_control_type[] swigValues = vx_sessiongroup_audio_injection_control_type[]{VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_STOP, VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_START, VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_RESTART, VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MIN, VX_SESSIONGROUP_AUDIO_INJECTION_CONTROL_MAX}
    private String swigName
    private Int swigValue

    private vx_sessiongroup_audio_injection_control_type(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_sessiongroup_audio_injection_control_type(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_sessiongroup_audio_injection_control_type(String string2, vx_sessiongroup_audio_injection_control_type vx_sessiongroup_audio_injection_control_type2) {
        this.swigName = string2
        this.swigValue = vx_sessiongroup_audio_injection_control_type2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_sessiongroup_audio_injection_control_type swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_sessiongroup_audio_injection_control_type.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_sessiongroup_audio_injection_control_type.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_sessiongroup_audio_injection_control_type.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

