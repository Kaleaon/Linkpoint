/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

public final class vx_attempt_stun {
    public static final vx_attempt_stun attempt_stun_off = new vx_attempt_stun("attempt_stun_off");
    public static final vx_attempt_stun attempt_stun_on = new vx_attempt_stun("attempt_stun_on");
    public static final vx_attempt_stun attempt_stun_unspecified = new vx_attempt_stun("attempt_stun_unspecified");
    private static int swigNext = 0;
    private static vx_attempt_stun[] swigValues = new vx_attempt_stun[]{attempt_stun_unspecified, attempt_stun_on, attempt_stun_off};
    private final String swigName;
    private final int swigValue;

    private vx_attempt_stun(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_attempt_stun(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_attempt_stun(String string2, vx_attempt_stun vx_attempt_stun2) {
        this.swigName = string2;
        this.swigValue = vx_attempt_stun2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_attempt_stun swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_attempt_stun.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_attempt_stun.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_attempt_stun.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

