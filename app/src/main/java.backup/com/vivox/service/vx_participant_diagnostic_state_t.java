/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_participant_diagnostic_state_t {
    public static final vx_participant_diagnostic_state_t participant_diagnostic_state_speaking_while_mic_muted = new vx_participant_diagnostic_state_t("participant_diagnostic_state_speaking_while_mic_muted", VxClientProxyJNI.participant_diagnostic_state_speaking_while_mic_muted_get());
    public static final vx_participant_diagnostic_state_t participant_diagnostic_state_speaking_while_mic_volume_zero = new vx_participant_diagnostic_state_t("participant_diagnostic_state_speaking_while_mic_volume_zero", VxClientProxyJNI.participant_diagnostic_state_speaking_while_mic_volume_zero_get());
    private static int swigNext = 0;
    private static vx_participant_diagnostic_state_t[] swigValues = new vx_participant_diagnostic_state_t[]{participant_diagnostic_state_speaking_while_mic_muted, participant_diagnostic_state_speaking_while_mic_volume_zero};
    private final String swigName;
    private final int swigValue;

    private vx_participant_diagnostic_state_t(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_participant_diagnostic_state_t(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_participant_diagnostic_state_t(String string2, vx_participant_diagnostic_state_t vx_participant_diagnostic_state_t2) {
        this.swigName = string2;
        this.swigValue = vx_participant_diagnostic_state_t2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_participant_diagnostic_state_t swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_participant_diagnostic_state_t.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_participant_diagnostic_state_t.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_participant_diagnostic_state_t.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

