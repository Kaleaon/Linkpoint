/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_log_level {
    public static final vx_log_level log_debug = new vx_log_level("log_debug");
    public static final vx_log_level log_error = new vx_log_level("log_error", VxClientProxyJNI.log_error_get());
    public static final vx_log_level log_info = new vx_log_level("log_info");
    public static final vx_log_level log_trace = new vx_log_level("log_trace");
    public static final vx_log_level log_warning = new vx_log_level("log_warning");
    private static int swigNext = 0;
    private static vx_log_level[] swigValues = new vx_log_level[]{log_error, log_warning, log_info, log_debug, log_trace};
    private final String swigName;
    private final int swigValue;

    private vx_log_level(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_log_level(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_log_level(String string2, vx_log_level vx_log_level2) {
        this.swigName = string2;
        this.swigValue = vx_log_level2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_log_level swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_log_level.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_log_level.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_log_level.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

