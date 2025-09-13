/*
 * Decompiled with CFR 0.152.
 */
package com.vivox.service;

import com.vivox.service.VxClientProxyJNI;

public final class vx_text_mode {
    private static int swigNext = 0;
    private static vx_text_mode[] swigValues;
    public static final vx_text_mode text_mode_disabled;
    public static final vx_text_mode text_mode_enabled;
    private final String swigName;
    private final int swigValue;

    static {
        text_mode_disabled = new vx_text_mode("text_mode_disabled", VxClientProxyJNI.text_mode_disabled_get());
        text_mode_enabled = new vx_text_mode("text_mode_enabled");
        swigValues = new vx_text_mode[]{text_mode_disabled, text_mode_enabled};
    }

    private vx_text_mode(String string2) {
        this.swigName = string2;
        int n = swigNext;
        swigNext = n + 1;
        this.swigValue = n;
    }

    private vx_text_mode(String string2, int n) {
        this.swigName = string2;
        this.swigValue = n;
        swigNext = n + 1;
    }

    private vx_text_mode(String string2, vx_text_mode vx_text_mode2) {
        this.swigName = string2;
        this.swigValue = vx_text_mode2.swigValue;
        swigNext = this.swigValue + 1;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static vx_text_mode swigToEnum(int n) {
        if (n < swigValues.length && n >= 0 && vx_text_mode.swigValues[n].swigValue == n) {
            return swigValues[n];
        }
        int n2 = 0;
        while (n2 < swigValues.length) {
            if (vx_text_mode.swigValues[n2].swigValue == n) {
                return swigValues[n2];
            }
            ++n2;
        }
        throw new IllegalArgumentException("No enum " + vx_text_mode.class + " with value " + n);
    }

    public final int swigValue() {
        return this.swigValue;
    }

    public String toString() {
        return this.swigName;
    }
}

