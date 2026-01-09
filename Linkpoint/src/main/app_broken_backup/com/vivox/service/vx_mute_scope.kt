/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_mute_scope {
    vx_mute_scope mute_scope_all = vx_mute_scope("mute_scope_all", VxClientProxyJNI.mute_scope_all_get())
    vx_mute_scope mute_scope_audio = vx_mute_scope("mute_scope_audio", VxClientProxyJNI.mute_scope_audio_get())
    vx_mute_scope mute_scope_text = vx_mute_scope("mute_scope_text", VxClientProxyJNI.mute_scope_text_get())
    private Int swigNext = 0
    private vx_mute_scope[] swigValues = vx_mute_scope[]{mute_scope_all, mute_scope_audio, mute_scope_text}
    private String swigName
    private Int swigValue

    private vx_mute_scope(String string2) {
        this.swigName = string2
        var n: Int = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_mute_scope(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_mute_scope(String string2, vx_mute_scope vx_mute_scope2) {
        this.swigName = string2
        this.swigValue = vx_mute_scope2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_mute_scope swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_mute_scope.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        var n2: Int = 0
        while (n2 < swigValues.length) {
            if (vx_mute_scope.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_mute_scope.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

