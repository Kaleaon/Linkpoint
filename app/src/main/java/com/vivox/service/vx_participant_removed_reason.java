/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_participant_removed_reason {
    public static final vx_participant_removed_reason participant_banned = new vx_participant_removed_reason("participant_banned", VxClientProxyJNI.participant_banned_get());
    public static final vx_participant_removed_reason participant_kicked = new vx_participant_removed_reason("participant_kicked", VxClientProxyJNI.participant_kicked_get());
    public static final vx_participant_removed_reason participant_left = new vx_participant_removed_reason("participant_left", VxClientProxyJNI.participant_left_get());
    public static final vx_participant_removed_reason participant_timeout = new vx_participant_removed_reason("participant_timeout", VxClientProxyJNI.participant_timeout_get());
    private static int swigNext = 0;
    private static vx_participant_removed_reason[] swigValues = new vx_participant_removed_reason[]{participant_left, participant_timeout, participant_kicked, participant_banned};
    private final String swigName;
    private final int swigValue;

    private vx_participant_removed_reason(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_participant_removed_reason(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_participant_removed_reason(String string2, vx_participant_removed_reason vx_participant_removed_reason2) {
        this.swigName = string2;
        this.swigValue = vx_participant_removed_reason2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_participant_removed_reason swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_participant_removed_reason.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_participant_removed_reason.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_participant_removed_reason.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

