/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_dtmf_type {
    public static final vx_dtmf_type dtmf_0 = new vx_dtmf_type("dtmf_0", VxClientProxyJNI.dtmf_0_get());
    public static final vx_dtmf_type dtmf_1 = new vx_dtmf_type("dtmf_1", VxClientProxyJNI.dtmf_1_get());
    public static final vx_dtmf_type dtmf_2 = new vx_dtmf_type("dtmf_2", VxClientProxyJNI.dtmf_2_get());
    public static final vx_dtmf_type dtmf_3 = new vx_dtmf_type("dtmf_3", VxClientProxyJNI.dtmf_3_get());
    public static final vx_dtmf_type dtmf_4 = new vx_dtmf_type("dtmf_4", VxClientProxyJNI.dtmf_4_get());
    public static final vx_dtmf_type dtmf_5 = new vx_dtmf_type("dtmf_5", VxClientProxyJNI.dtmf_5_get());
    public static final vx_dtmf_type dtmf_6 = new vx_dtmf_type("dtmf_6", VxClientProxyJNI.dtmf_6_get());
    public static final vx_dtmf_type dtmf_7 = new vx_dtmf_type("dtmf_7", VxClientProxyJNI.dtmf_7_get());
    public static final vx_dtmf_type dtmf_8 = new vx_dtmf_type("dtmf_8", VxClientProxyJNI.dtmf_8_get());
    public static final vx_dtmf_type dtmf_9 = new vx_dtmf_type("dtmf_9", VxClientProxyJNI.dtmf_9_get());
    public static final vx_dtmf_type dtmf_A = new vx_dtmf_type("dtmf_A", VxClientProxyJNI.dtmf_A_get());
    public static final vx_dtmf_type dtmf_B = new vx_dtmf_type("dtmf_B", VxClientProxyJNI.dtmf_B_get());
    public static final vx_dtmf_type dtmf_C = new vx_dtmf_type("dtmf_C", VxClientProxyJNI.dtmf_C_get());
    public static final vx_dtmf_type dtmf_D = new vx_dtmf_type("dtmf_D", VxClientProxyJNI.dtmf_D_get());
    public static final vx_dtmf_type dtmf_max = new vx_dtmf_type("dtmf_max", VxClientProxyJNI.dtmf_max_get());
    public static final vx_dtmf_type dtmf_pound = new vx_dtmf_type("dtmf_pound", VxClientProxyJNI.dtmf_pound_get());
    public static final vx_dtmf_type dtmf_star = new vx_dtmf_type("dtmf_star", VxClientProxyJNI.dtmf_star_get());
    private static int swigNext = 0;
    private static vx_dtmf_type[] swigValues = new vx_dtmf_type[]{dtmf_0, dtmf_1, dtmf_2, dtmf_3, dtmf_4, dtmf_5, dtmf_6, dtmf_7, dtmf_8, dtmf_9, dtmf_pound, dtmf_star, dtmf_A, dtmf_B, dtmf_C, dtmf_D, dtmf_max};
    private final String swigName;
    private final int swigValue;

    private vx_dtmf_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_dtmf_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_dtmf_type(String string2, vx_dtmf_type vx_dtmf_type2) {
        this.swigName = string2;
        this.swigValue = vx_dtmf_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_dtmf_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_dtmf_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_dtmf_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_dtmf_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

