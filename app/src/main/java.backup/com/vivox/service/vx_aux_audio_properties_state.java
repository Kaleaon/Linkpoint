/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_aux_audio_properties_state {
    public static final vx_aux_audio_properties_state aux_audio_properties_none = new vx_aux_audio_properties_state("aux_audio_properties_none", VxClientProxyJNI.aux_audio_properties_none_get());
    private static int swigNext = 0;
    private static vx_aux_audio_properties_state[] swigValues = new vx_aux_audio_properties_state[]{aux_audio_properties_none};
    private final String swigName;
    private final int swigValue;

    private vx_aux_audio_properties_state(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_aux_audio_properties_state(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_aux_audio_properties_state(String string2, vx_aux_audio_properties_state vx_aux_audio_properties_state2) {
        this.swigName = string2;
        this.swigValue = vx_aux_audio_properties_state2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_aux_audio_properties_state swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_aux_audio_properties_state.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_aux_audio_properties_state.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_aux_audio_properties_state.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

