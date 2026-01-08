/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service

import com.vivox.service.VxClientProxyJNI

class vx_aux_audio_properties_state {
    vx_aux_audio_properties_state aux_audio_properties_none = vx_aux_audio_properties_state("aux_audio_properties_none", VxClientProxyJNI.aux_audio_properties_none_get())
    private Int swigNext = 0
    private vx_aux_audio_properties_state[] swigValues = vx_aux_audio_properties_state[]{aux_audio_properties_none}
    private String swigName
    private Int swigValue

    private vx_aux_audio_properties_state(String string2) {
        this.swigName = string2
        Int n = swigNext
        swigNext = n + 1
        this.swigValue = n
    }

    private vx_aux_audio_properties_state(String string2, Int n) {
        this.swigName = string2
        this.swigValue = n
        swigNext = n + 1
    }

    private vx_aux_audio_properties_state(String string2, vx_aux_audio_properties_state vx_aux_audio_properties_state2) {
        this.swigName = string2
        this.swigValue = vx_aux_audio_properties_state2.swigValue
        swigNext = this.swigValue + 1
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    vx_aux_audio_properties_state swigToEnum(Int n) {
        if (n < swigValues.length && n >= 0 && vx_aux_audio_properties_state.swigValues[n].swigValue == n) {
            return swigValues[n]
        }
        Int n2 = 0
        while (n2 < swigValues.length) {
            if (vx_aux_audio_properties_state.swigValues[n2].swigValue == n) {
                return swigValues[n2]
            }
            ++n2
        }
        throw IllegalArgumentException("No enum " + vx_aux_audio_properties_state.class + " with value " + n)
    }

    Int swigValue() {
        return this.swigValue
    }

    String toString() {
        return this.swigName
    }
}

