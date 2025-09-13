/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_log_type {
    public static final vx_log_type log_to_callback = new vx_log_type("log_to_callback");
    public static final vx_log_type log_to_file = new vx_log_type("log_to_file");
    public static final vx_log_type log_to_file_and_callback = new vx_log_type("log_to_file_and_callback");
    public static final vx_log_type log_to_none = new vx_log_type("log_to_none", VxClientProxyJNI.log_to_none_get());
    private static int swigNext = 0;
    private static vx_log_type[] swigValues = new vx_log_type[]{log_to_none, log_to_file, log_to_callback, log_to_file_and_callback};
    private final String swigName;
    private final int swigValue;

    private vx_log_type(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_log_type(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_log_type(String string2, vx_log_type vx_log_type2) {
        this.swigName = string2;
        this.swigValue = vx_log_type2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_log_type swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_log_type.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_log_type.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_log_type.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

